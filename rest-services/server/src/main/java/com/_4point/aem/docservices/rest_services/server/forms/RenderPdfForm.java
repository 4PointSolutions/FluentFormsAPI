package com._4point.aem.docservices.rest_services.server.forms;

import static com._4point.aem.docservices.rest_services.server.FormParameters.getMandatoryParameter;
import static com._4point.aem.docservices.rest_services.server.FormParameters.getOptionalParameter;
import static com._4point.aem.docservices.rest_services.server.FormParameters.getOptionalParameters;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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

import com._4point.aem.docservices.rest_services.server.DataParameter;
import com._4point.aem.docservices.rest_services.server.DataParameter.ParameterType;
import com._4point.aem.docservices.rest_services.server.Exceptions.BadRequestException;
import com._4point.aem.docservices.rest_services.server.Exceptions.InternalServerErrorException;
import com._4point.aem.docservices.rest_services.server.Exceptions.NotAcceptableException;
import com._4point.aem.docservices.rest_services.server.ServletUtils;
import com._4point.aem.docservices.rest_services.server.TemplateParameter;
import com._4point.aem.fluentforms.api.AbsoluteOrRelativeUrl;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.api.forms.FormsService;
import com._4point.aem.fluentforms.api.forms.FormsService.FormsServiceException;
import com._4point.aem.fluentforms.api.forms.FormsService.RenderPDFFormArgumentBuilder;
import com._4point.aem.fluentforms.impl.AdobeDocumentFactoryImpl;
import com._4point.aem.fluentforms.impl.UsageContext;
import com._4point.aem.fluentforms.impl.forms.AdobeFormsServiceAdapter;
import com._4point.aem.fluentforms.impl.forms.FormsServiceImpl;
import com._4point.aem.fluentforms.impl.forms.TraditionalFormsService;
import com.adobe.fd.forms.api.AcrobatVersion;
import com.adobe.fd.forms.api.CacheStrategy;
import com.adobe.fd.forms.api.RenderAtClient;

@SuppressWarnings("serial")
@Component(service=Servlet.class, property={Constants.SERVICE_DESCRIPTION + "=FormsService.RenderPdfForm Service",
											"sling.servlet.methods=" + HttpConstants.METHOD_POST})
@SlingServletPaths(ServletUtils.SERVICES_PREFIX + "/FormsService/RenderPdfForm")

public class RenderPdfForm extends SlingAllMethodsServlet {

	private static final Logger log = LoggerFactory.getLogger(RenderPdfForm.class);
	private final DocumentFactory docFactory = AdobeDocumentFactoryImpl.getFactory();	// We know we're running on AEM, so we'll use the AdobeDocumentFactoryImpl.
	private final Supplier<TraditionalFormsService> formServiceFactory = this::getAdobeFormsService;

	@Reference
	private com.adobe.fd.forms.api.FormsService adobeFormsService;

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
		FormsService formsService = new FormsServiceImpl(formServiceFactory.get(), UsageContext.SERVER_SIDE);

		RenderPdfFormParameters reqParameters = RenderPdfFormParameters.readFormParameters(request, false);	// TODO: Make the validation of XML a config parameter.
		TemplateParameter template = reqParameters.getTemplate();
		PathOrUrl contentRoot = reqParameters.getContentRoot();
		AcrobatVersion acrobatVersion = reqParameters.getAcrobatVersion();
		CacheStrategy cacheStrategy = reqParameters.getCacheStrategy();
		Path debugDir = reqParameters.getDebugDir();
		Boolean embedFonts = reqParameters.getEmbedFonts();
		Locale locale = reqParameters.getLocale();
		RenderAtClient renderAtClient= reqParameters.getRenderAtClient();
		List<AbsoluteOrRelativeUrl> submitUrls = reqParameters.getSubmitUrls();
		Boolean taggedPDF = reqParameters.getTaggedPDF();
		byte[] xci = reqParameters.getXci();
		
		
		try(Document data = reqParameters.getData() != null ? docFactory.create(reqParameters.getData()) : null) {
			// In the following call to the formsService, we only set the parameters if they are not null.
			RenderPDFFormArgumentBuilder argBuilder = formsService.renderPDFForm()
												.transform(b->contentRoot == null ? b : b.setContentRoot(contentRoot))
												.transform(b->acrobatVersion == null ? b : b.setAcrobatVersion(acrobatVersion))
												.transform(b->cacheStrategy == null ? b : b.setCacheStrategy(cacheStrategy))
												.transform(b->debugDir == null ? b : b.setDebugDir(debugDir))
												.transform(b->embedFonts == null ? b : b.setEmbedFonts(embedFonts.booleanValue()))
												.transform(b->locale == null ? b : b.setLocale(locale))
												.transform(b->renderAtClient == null ? b : b.setRenderAtClient(renderAtClient))
												.transform(b->submitUrls == null || submitUrls.isEmpty() ? b : b.setSubmitUrls(submitUrls))
												.transform(b->taggedPDF == null ? b : b.setTaggedPDF(taggedPDF.booleanValue()))
												.transform(b->xci == null ? b : b.setXci(docFactory.create(xci)));
			try (Document result = executeOn(template, data, argBuilder)) {
				ServletUtils.transferDocumentToResponse(request, response, result, true);
			}
		} catch (FileNotFoundException fnfex) {
			throw new BadRequestException("Bad request parameter while rendering PDF (" + fnfex.getMessage() + ").", fnfex);
		} catch (FormsServiceException | IOException ex1) {
			throw new InternalServerErrorException("Internal Error while rendering PDF. (" + ex1.getMessage() + ").", ex1);
		} catch (IllegalArgumentException ex2) {
			throw new BadRequestException("Bad arguments while rendering PDF", ex2);
		}
	}

	private Document executeOn(TemplateParameter template, Document data, RenderPDFFormArgumentBuilder argBuilder) throws FormsServiceException, FileNotFoundException {
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

	private TraditionalFormsService getAdobeFormsService() {
		return new AdobeFormsServiceAdapter(adobeFormsService);
	}

	private static class RenderPdfFormParameters {
		private static final String TEMPLATE_PARAM = "template";
		private static final String DATA_PARAM = "data";
		private static final String ACROBAT_VERSION_PARAM = "renderOptions.acrobatVersion";
		private static final String CACHE_STRATEGY_PARAM = "renderOptions.cacheStrategy";
		private static final String CONTENT_ROOT_PARAM = "renderOptions.contentRoot";
		private static final String DEBUG_DIR_PARAM = "renderOptions.debugDir";
		private static final String EMBED_FONTS_PARAM = "renderOptions.embedFonts";
		private static final String LOCALE_PARAM = "renderOptions.locale";
		private static final String RENDER_AT_CLIENT_PARAM = "renderOptions.renderAtClient";
		private static final String SUBMIT_URL_PARAM = "renderOptions.submitUrl";
		private static final String TAGGED_PDF_PARAM = "renderOptions.taggedPdf";
		private static final String XCI_PARAM = "renderOptions.xci";
		
		private AcrobatVersion acrobatVersion = null;
		private CacheStrategy cacheStrategy = null;
		private PathOrUrl contentRoot = null;
		private Path debugDir = null;
		private Boolean embedFonts = null;
		private Locale locale = null;
		private RenderAtClient renderAtClient = null;
		private List<AbsoluteOrRelativeUrl> submitUrls = null;
		private Boolean taggedPDF = null;
		private byte[] xci = null;
		private final TemplateParameter template;
		private final byte[] data;
		
		public RenderPdfFormParameters(TemplateParameter template, byte[] data) {
			super();
			this.template = template;
			this.data = data;
		}

		public RenderPdfFormParameters(TemplateParameter template) {
			super();
			this.template = template;
			this.data = null;
		}

		public AcrobatVersion getAcrobatVersion() {
			return acrobatVersion;
		}

		private RenderPdfFormParameters setAcrobatVersion(String acrobatVersionStr) {
			this.acrobatVersion = AcrobatVersion.valueOf(acrobatVersionStr);
			return this;
		}

		public CacheStrategy getCacheStrategy() {
			return cacheStrategy;
		}

		private RenderPdfFormParameters setCacheStrategy(String cacheStrategyStr) {
			this.cacheStrategy = CacheStrategy.valueOf(cacheStrategyStr);
			return this;
		}

		public PathOrUrl getContentRoot() {
			return contentRoot;
		}

		private RenderPdfFormParameters setContentRoot(String contentRootStr) {
			this.contentRoot = PathOrUrl.from(contentRootStr);
			return this;
		}

		public Path getDebugDir() {
			return debugDir;
		}

		private RenderPdfFormParameters setDebugDir(String debugDirStr) {
			this.debugDir = Paths.get(debugDirStr);
			return this;
		}

		public Boolean getEmbedFonts() {
			return embedFonts;
		}

		private RenderPdfFormParameters setEmbedFonts(String embedFontsStr) {
			this.embedFonts = Boolean.valueOf(embedFontsStr);
			return this;
		}

		public Locale getLocale() {
			return locale;
		}

		private RenderPdfFormParameters setLocale(String localeStr) {
			this.locale = Locale.forLanguageTag(localeStr);
			return this;
		}

		public RenderAtClient getRenderAtClient() {
			return renderAtClient;
		}

		public RenderPdfFormParameters setRenderAtClient(String renderAtClient) {
			this.renderAtClient = RenderAtClient.valueOf(renderAtClient);
			return this;
		}

		public List<AbsoluteOrRelativeUrl> getSubmitUrls() {
			return submitUrls;
		}

		private RenderPdfFormParameters setSubmitUrls(RequestParameter[] submitUrlParms) throws BadRequestException {
			this.submitUrls = new ArrayList<>();
			for (RequestParameter str : submitUrlParms) {
				try {
					this.submitUrls.add(AbsoluteOrRelativeUrl.fromString(str.getString()));
				} catch (IllegalArgumentException e) {
					throw new BadRequestException("Bad sumbit Url (" + str.getString() + ").", e);
				}
			}
			return this;
		}

		public Boolean getTaggedPDF() {
			return taggedPDF;
		}

		private RenderPdfFormParameters setTaggedPDF(String taggedPDFStr) {
			this.taggedPDF = Boolean.valueOf(taggedPDFStr);
			return this;
		}

		public byte[] getXci() {
			return xci;
		}

		private RenderPdfFormParameters setXci(byte[] xci) {
			this.xci = xci;
			return this;
		}

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
		public static RenderPdfFormParameters readFormParameters(SlingHttpServletRequest request, boolean validateXml) throws BadRequestException {
			try {
				
				TemplateParameter template = TemplateParameter.readParameter(getMandatoryParameter(request, TEMPLATE_PARAM));

				// Data parameter is optional.  If Data is not supplied, then an empty form is produced.
				Optional<DataParameter> data = getOptionalParameter(request, DATA_PARAM)
																.map(p->DataParameter.readParameter(p,validateXml));
				
				if (data.isPresent() && data.get().getType() != ParameterType.ByteArray) {
					throw new BadRequestException("RenderPdfForm only supports providing data by value at this time.");
				}
				byte[] inputData = data.map(DataParameter::getArray).orElse(null);
	
				RenderPdfFormParameters result = inputData == null ? new RenderPdfFormParameters(template) : new RenderPdfFormParameters(template, inputData);
				
				getOptionalParameter(request, ACROBAT_VERSION_PARAM).ifPresent(rp->result.setAcrobatVersion(rp.getString()));
				getOptionalParameter(request, CACHE_STRATEGY_PARAM).ifPresent(rp->result.setCacheStrategy(rp.getString()));
				getOptionalParameter(request, CONTENT_ROOT_PARAM).ifPresent(rp->result.setContentRoot(rp.getString()));
				getOptionalParameter(request, DEBUG_DIR_PARAM).ifPresent(rp->result.setDebugDir(rp.getString()));
				getOptionalParameter(request, EMBED_FONTS_PARAM).ifPresent(rp->result.setEmbedFonts(rp.getString()));
				getOptionalParameter(request, LOCALE_PARAM).ifPresent(rp->result.setLocale(rp.getString()));
				getOptionalParameter(request, RENDER_AT_CLIENT_PARAM).ifPresent(rp->result.setRenderAtClient(rp.getString()));
				getOptionalParameter(request, TAGGED_PDF_PARAM).ifPresent(rp->result.setTaggedPDF(rp.getString()));
				getOptionalParameter(request, XCI_PARAM).ifPresent(rp->result.setXci(rp.get()));
				// Submit URLs parameter has to be handled differently because it throws exceptions.
				Optional<RequestParameter[]> submitUrlParms = getOptionalParameters(request, SUBMIT_URL_PARAM);
				if (submitUrlParms.isPresent()) {
					result.setSubmitUrls(submitUrlParms.get());
				}
				
				return result;
			} catch (IllegalArgumentException e) {
				throw new BadRequestException("There was a problem with one of the incoming parameters.", e);
			}
		}
	}
}
