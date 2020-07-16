package com._4point.aem.docservices.rest_services.server.output;

import static com._4point.aem.docservices.rest_services.server.FormParameters.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com._4point.aem.docservices.rest_services.server.AcceptHeaders;
import com._4point.aem.docservices.rest_services.server.ContentType;
import com._4point.aem.docservices.rest_services.server.DataParameter;
import com._4point.aem.docservices.rest_services.server.Exceptions.BadRequestException;
import com._4point.aem.docservices.rest_services.server.Exceptions.InternalServerErrorException;
import com._4point.aem.docservices.rest_services.server.Exceptions.NotAcceptableException;
import com._4point.aem.docservices.rest_services.server.ServletUtils;
import com._4point.aem.docservices.rest_services.server.TemplateParameter;
import com._4point.aem.docservices.rest_services.server.DataParameter.ParameterType;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.api.output.OutputService;
import com._4point.aem.fluentforms.api.output.OutputService.GeneratePdfOutputArgumentBuilder;
import com._4point.aem.fluentforms.api.output.OutputService.OutputServiceException;
import com._4point.aem.fluentforms.impl.UsageContext;
import com._4point.aem.fluentforms.impl.output.AdobeOutputServiceAdapter;
import com._4point.aem.fluentforms.impl.output.OutputServiceImpl;
import com._4point.aem.fluentforms.impl.output.TraditionalOutputService;
import com.adobe.fd.output.api.AcrobatVersion;

@SuppressWarnings("serial")
@Component(service=Servlet.class, property={Constants.SERVICE_DESCRIPTION + "=FormsService.RenderPdfForm Service",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST})
@SlingServletPaths("/services/OutputService/GeneratePdfOutput")
public class GeneratePdfOutput extends SlingAllMethodsServlet {

	private static final Logger log = LoggerFactory.getLogger(GeneratePdfOutput.class);
	private final DocumentFactory docFactory = DocumentFactory.getDefault();
	private final Supplier<TraditionalOutputService> outputServiceFactory = this::getAdobeOutputService;

	@Reference
	private com.adobe.fd.output.api.OutputService adobeOutputService;

	@Override
	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		try {
			this.processInput(request, response);
		} catch (BadRequestException br) {
			log.warn("Bad Request from the user", br);
			response.sendError(SlingHttpServletResponse.SC_BAD_REQUEST, br.getMessage());
		} catch (InternalServerErrorException ise) {
			log.error("Internal server error", ise);
			response.sendError(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR, ise.getMessage());
		} catch (NotAcceptableException nae) {
			log.error("NotAcceptable error", nae);
			response.sendError(SlingHttpServletResponse.SC_NOT_ACCEPTABLE, nae.getMessage());
		} catch (Exception e) {  			// Some exception we haven't anticipated.
			log.error(e.getMessage() != null ? e.getMessage() : e.getClass().getName() , e);	// Make sure this gets into our log.
			throw e;
		}
	}

	private void processInput(SlingHttpServletRequest request, SlingHttpServletResponse response) throws BadRequestException, InternalServerErrorException, NotAcceptableException {
		OutputService outputService = new OutputServiceImpl(outputServiceFactory.get(), UsageContext.SERVER_SIDE);

		GeneratePdfOutputParameters reqParameters = GeneratePdfOutputParameters.readFormParameters(request, false);	// TODO: Make the validation of XML a config parameter.
		TemplateParameter template = reqParameters.getTemplate();
		Document data = reqParameters.getData() != null ? docFactory.create(reqParameters.getData()) : null;
		PathOrUrl contentRoot = reqParameters.getContentRoot();
		AcrobatVersion acrobatVersion = reqParameters.getAcrobatVersion();
		Path debugDir = reqParameters.getDebugDir();
		Boolean embedFonts = reqParameters.getEmbedFonts();
		Boolean linearizedPdf = reqParameters.getLinearizedPdf();
		Locale locale = reqParameters.getLocale();
		Boolean retainPdfFormState = reqParameters.getRetainPdfFormState();
		Boolean retainUnsignedSignatureFields = reqParameters.getRetainUnsignedSignatureFields();
		Boolean taggedPDF = reqParameters.getTaggedPDF();
		byte[] xci = reqParameters.getXci();
		
		try {
			// In the following call to the formsService, we only set the parameters if they are not null.
			GeneratePdfOutputArgumentBuilder argBuilder = outputService.generatePDFOutput()
												.transform(b->contentRoot == null ? b : b.setContentRoot(contentRoot))
												.transform(b->acrobatVersion == null ? b : b.setAcrobatVersion(acrobatVersion))
												.transform(b->debugDir == null ? b : b.setDebugDir(debugDir))
												.transform(b->embedFonts == null ? b : b.setEmbedFonts(embedFonts))
												.transform(b->linearizedPdf == null ? b : b.setLinearizedPDF(linearizedPdf))
												.transform(b->locale == null ? b : b.setLocale(locale))
												.transform(b->retainPdfFormState == null ? b : b.setRetainPDFFormState(retainPdfFormState))
												.transform(b->retainUnsignedSignatureFields == null ? b : b.setRetainUnsignedSignatureFields(retainUnsignedSignatureFields))
												.transform(b->taggedPDF == null ? b : b.setTaggedPDF(taggedPDF.booleanValue()))
												.transform(b->xci == null ? b : b.setXci(docFactory.create(xci)));

			try (Document result = executeOn(template, data, argBuilder)) {
				String contentType = result.getContentType();
				ServletUtils.validateAcceptHeader(request.getHeader(AcceptHeaders.ACCEPT_HEADER_STR), contentType);
				response.setContentType(contentType);
				response.setContentLength((int)result.length());
				ServletUtils.transfer(result.getInputStream(), response.getOutputStream());
			}
		} catch (FileNotFoundException fnfex) {
			throw new BadRequestException("Bad request parameter while rendering PDF (" + fnfex.getMessage() + ").", fnfex);
		} catch (OutputServiceException | IOException ex1) {
			throw new InternalServerErrorException("Internal Error while rendering PDF. (" + ex1.getMessage() + ").", ex1);
		} catch (IllegalArgumentException ex2) {
			throw new BadRequestException("Bad arguments while rendering PDF", ex2);
		}
	}

	private Document executeOn(TemplateParameter template, Document data, GeneratePdfOutputArgumentBuilder argBuilder) throws OutputServiceException, FileNotFoundException {
		switch(template.getType()) {
			case ByteArray:
			{
				byte[] templateParam = template.getArray();
				return argBuilder.executeOn(docFactory.create(templateParam), data);
			}
			case PathOrUrl:
			{
				PathOrUrl templateParam = template.getPathOrUrl();
				return argBuilder.executeOn(templateParam, data);
			}
			default:
				// this should never be executed.
				throw new IllegalStateException("Found unexpected template parameter type (" + template.getType().toString() + ").");
		}
	}

	private TraditionalOutputService getAdobeOutputService() {
		return new AdobeOutputServiceAdapter(adobeOutputService);
	}

	private static class GeneratePdfOutputParameters {
		private static final String TEMPLATE_PARAM = "template";
		private static final String DATA_PARAM = "data";
		private static final String ACROBAT_VERSION_PARAM = "outputOptions.acrobatVersion";
		private static final String CONTENT_ROOT_PARAM = "outputOptions.contentRoot";
		private static final String DEBUG_DIR_PARAM = "outputOptions.debugDir";
		private static final String EMBED_FONTS_PARAM = "outputOptions.embedFonts";
		private static final String LINEARIZED_PDF_PARAM = "outputOptions.linearizedPdf";
		private static final String LOCALE_PARAM = "outputOptions.locale";
		private static final String RETAIN_PDF_FORM_STATE_PARAM = "outputOptions.retainPdfFormState";
		private static final String RETAIN_UNSIGNED_SIGNATURE_FIELDS_PARAM = "outputOptions.retainUnsignedSignatureFields";
		private static final String TAGGED_PDF_PARAM = "outputOptions.taggedPdf";
		private static final String XCI_PARAM = "outputOptions.xci";
		
		private AcrobatVersion acrobatVersion = null;
		private PathOrUrl contentRoot = null;
		private Path debugDir = null;
		private Boolean embedFonts = null;
		private Boolean linearizedPdf = null;
		private Locale locale = null;
		private Boolean retainPdfFormState = null;
		private Boolean retainUnsignedSignatureFields = null;
		private Boolean taggedPDF = null;
		private byte[] xci = null;
		private final TemplateParameter template;
		private final byte[] data;
		
		public GeneratePdfOutputParameters(TemplateParameter template, byte[] data) {
			super();
			this.template = template;
			this.data = data;
		}

		public GeneratePdfOutputParameters(TemplateParameter template) {
			super();
			this.template = template;
			this.data = null;
		}

		public AcrobatVersion getAcrobatVersion() {
			return acrobatVersion;
		}

		private GeneratePdfOutputParameters setAcrobatVersion(String acrobatVersionStr) {
			this.acrobatVersion = AcrobatVersion.valueOf(acrobatVersionStr);
			return this;
		}

		public PathOrUrl getContentRoot() {
			return contentRoot;
		}

		private GeneratePdfOutputParameters setContentRoot(String contentRootStr) {
			this.contentRoot = PathOrUrl.from(contentRootStr);
			return this;
		}

		public Path getDebugDir() {
			return debugDir;
		}

		private GeneratePdfOutputParameters setDebugDir(String debugDirStr) {
			this.debugDir = Paths.get(debugDirStr);
			return this;
		}

		public Boolean getEmbedFonts() {
			return embedFonts;
		}

		public GeneratePdfOutputParameters setEmbedFonts(String embedFonts) {
			this.embedFonts = Boolean.valueOf(embedFonts);
			return this;
		}

		public Boolean getLinearizedPdf() {
			return linearizedPdf;
		}

		public GeneratePdfOutputParameters setLinearizedPdf(String linearizedPdf) {
			this.linearizedPdf = Boolean.valueOf(linearizedPdf);
			return this;
		}

		public Locale getLocale() {
			return locale;
		}

		private GeneratePdfOutputParameters setLocale(String localeStr) {
			this.locale = Locale.forLanguageTag(localeStr);
			return this;
		}

		public Boolean getRetainPdfFormState() {
			return retainPdfFormState;
		}

		public GeneratePdfOutputParameters setRetainPdfFormState(String retainPdfFormState) {
			this.retainPdfFormState = Boolean.valueOf(retainPdfFormState);
			return this;
		}

		public Boolean getRetainUnsignedSignatureFields() {
			return retainUnsignedSignatureFields;
		}

		public GeneratePdfOutputParameters setRetainUnsignedSignatureFields(String retainUnsignedSignatureFields) {
			this.retainUnsignedSignatureFields = Boolean.valueOf(retainUnsignedSignatureFields);
			return this;
		}

		public Boolean getTaggedPDF() {
			return taggedPDF;
		}

		private GeneratePdfOutputParameters setTaggedPDF(String taggedPDFStr) {
			this.taggedPDF = Boolean.valueOf(taggedPDFStr);
			return this;
		}

		public byte[] getXci() {
			return xci;
		}

		private GeneratePdfOutputParameters setXci(byte[] xci) {
			this.xci = xci;
			return this;
		}

//		public Optional<PathOrUrl> getTemplateAsPathOrUrl() {
//			if (template.getType() != TemplateParameter.ParameterType.PathOrUrl) {
//				return Optional.empty();
//			}
//			return Optional.of(template.getPathOrUrl());
//		}
//
//		public Optional<byte[]> getTemplateAsByteArray() {
//			if (template.getType() != TemplateParameter.ParameterType.ByteArray) {
//				return Optional.empty();
//			}
//			return Optional.of(template.getArray());
//		}

		public TemplateParameter getTemplate() {
			return template;
		}
		
		public byte[] getData() {
			return data;
		}

		/**
		 * Read in the request parameters and translate them into a RenderPdfFormParameters object.
		 * 
		 * @param request
		 * @param validateXml
		 * @return
		 * @throws BadRequestException
		 */
		public static GeneratePdfOutputParameters readFormParameters(SlingHttpServletRequest request, boolean validateXml) throws BadRequestException {
			try {
				
				TemplateParameter template = TemplateParameter.readParameter(getMandatoryParameter(request, TEMPLATE_PARAM));

				// Data parameter is optional. If Data is not supplied, then an empty form is produced.
				Optional<DataParameter> data = getOptionalParameter(request, DATA_PARAM)
															.map(p -> DataParameter.readParameter(p, validateXml));

				if (data.isPresent() && data.get().getType() == ParameterType.PathOrUrl) {
					throw new BadRequestException("GeneratePdfOutput does not support passing data by reference, only by value.");
				}
				byte[] inputData = data.map(DataParameter::getArray).orElse(null);

				GeneratePdfOutputParameters result = inputData == null ? new GeneratePdfOutputParameters(template) : new GeneratePdfOutputParameters(template, inputData);
				
				getOptionalParameter(request, ACROBAT_VERSION_PARAM).ifPresent(rp->result.setAcrobatVersion(rp.getString()));
				getOptionalParameter(request, CONTENT_ROOT_PARAM).ifPresent(rp->result.setContentRoot(rp.getString()));
				getOptionalParameter(request, DEBUG_DIR_PARAM).ifPresent(rp->result.setDebugDir(rp.getString()));
				getOptionalParameter(request, EMBED_FONTS_PARAM).ifPresent(rp->result.setEmbedFonts(rp.getString()));
				getOptionalParameter(request, LINEARIZED_PDF_PARAM).ifPresent(rp->result.setLinearizedPdf(rp.getString()));
				getOptionalParameter(request, LOCALE_PARAM).ifPresent(rp->result.setLocale(rp.getString()));
				getOptionalParameter(request, RETAIN_PDF_FORM_STATE_PARAM).ifPresent(rp->result.setRetainPdfFormState(rp.getString()));
				getOptionalParameter(request, RETAIN_UNSIGNED_SIGNATURE_FIELDS_PARAM).ifPresent(rp->result.setRetainUnsignedSignatureFields(rp.getString()));
				getOptionalParameter(request, TAGGED_PDF_PARAM).ifPresent(rp->result.setTaggedPDF(rp.getString()));
				getOptionalParameter(request, XCI_PARAM).ifPresent(rp->result.setXci(rp.get()));
				
				return result;
			} catch (IllegalArgumentException e) {
				throw new BadRequestException("There was a problem with one of the incoming parameters.", e);
			}
		}	
	}
}
