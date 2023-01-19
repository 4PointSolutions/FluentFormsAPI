package com._4point.aem.docservices.rest_services.server.output;

import static com._4point.aem.docservices.rest_services.server.FormParameters.getMandatoryParameter;
import static com._4point.aem.docservices.rest_services.server.FormParameters.getOptionalParameter;

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
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com._4point.aem.docservices.rest_services.server.AcceptHeaders;
import com._4point.aem.docservices.rest_services.server.DataParameter;
import com._4point.aem.docservices.rest_services.server.ServletUtils;
import com._4point.aem.docservices.rest_services.server.TemplateParameter;
import com._4point.aem.docservices.rest_services.server.DataParameter.ParameterType;
import com._4point.aem.docservices.rest_services.server.Exceptions.BadRequestException;
import com._4point.aem.docservices.rest_services.server.Exceptions.InternalServerErrorException;
import com._4point.aem.docservices.rest_services.server.Exceptions.NotAcceptableException;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.api.output.OutputService;
import com._4point.aem.fluentforms.api.output.OutputService.GeneratePrintedOutputArgumentBuilder;
import com._4point.aem.fluentforms.api.output.OutputService.OutputServiceException;
import com._4point.aem.fluentforms.api.output.PrintConfig;
import com._4point.aem.fluentforms.impl.UsageContext;
import com._4point.aem.fluentforms.impl.output.AdobeOutputServiceAdapter;
import com._4point.aem.fluentforms.impl.output.OutputServiceImpl;
import com._4point.aem.fluentforms.impl.output.TraditionalOutputService;
import com.adobe.fd.output.api.PaginationOverride;

@SuppressWarnings("serial")
@Component(service=Servlet.class, property={Constants.SERVICE_DESCRIPTION + "=OutputService.GeneratePrintedOutput Service",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST})
@SlingServletPaths(ServletUtils.SERVICES_PREFIX + "/OutputService/GeneratePrintedOutput")
public class GeneratePrintedOutput extends SlingAllMethodsServlet {

	private static final Logger log = LoggerFactory.getLogger(GeneratePrintedOutput.class);
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

		GeneratePrintedOutputParameters reqParameters = GeneratePrintedOutputParameters.readFormParameters(request, false);	// TODO: Make the validation of XML a config parameter.
		TemplateParameter template = reqParameters.getTemplate();
		Document data = reqParameters.getData() != null ? docFactory.create(reqParameters.getData()) : null;
		PathOrUrl contentRoot = reqParameters.getContentRoot();
		Integer copies = reqParameters.getCopies();
		Path debugDir = reqParameters.getDebugDir();
		Locale locale = reqParameters.getLocale();
		PaginationOverride paginationOverride = reqParameters.getPaginationOverride();
		PrintConfig printConfig = reqParameters.getPrintConfig();
		byte[] xci = reqParameters.getXci();
		
		try {
			// In the following call to the formsService, we only set the parameters if they are not null.
			// PrintConfig is a required parameter.
			GeneratePrintedOutputArgumentBuilder argBuilder = outputService.generatePrintedOutput()
													.transform(b->contentRoot == null ? b : b.setContentRoot(contentRoot))
													.transform(b->copies == null ? b : b.setCopies(copies))
													.transform(b->debugDir == null ? b : b.setDebugDir(debugDir))
													.transform(b->locale == null ? b : b.setLocale(locale))
													.transform(b->paginationOverride == null ? b : b.setPaginationOverride(paginationOverride))
													.transform(b->b.setPrintConfig(printConfig))
													.transform(b->xci == null ? b : b.setXci(docFactory.create(xci)));
			try (Document result = executeOn(template, data, argBuilder)) {
				String contentType = result.getContentType();
				ServletUtils.validateAcceptHeader(request.getHeader(AcceptHeaders.ACCEPT_HEADER_STR), contentType);
				response.setContentType(contentType);
				ServletUtils.transfer(result.getInputStream(), response.getOutputStream());
			}
		} catch (FileNotFoundException fnfex) {
			throw new BadRequestException("Bad request parameter while rendering print output (" + fnfex.getMessage() + ").", fnfex);
		} catch (OutputServiceException | IOException ex1) {
			throw new InternalServerErrorException("Internal Error while rendering print output. (" + ex1.getMessage() + ").", ex1);
		} catch (IllegalArgumentException ex2) {
			throw new BadRequestException("Bad arguments while rendering print output", ex2);
		}
	}

	private Document executeOn(TemplateParameter template, Document data, GeneratePrintedOutputArgumentBuilder argBuilder) throws OutputServiceException, FileNotFoundException {
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

	private static class GeneratePrintedOutputParameters {
		private static final String TEMPLATE_PARAM = "template";
		private static final String DATA_PARAM = "data";
		private static final String CONTENT_ROOT_PARAM = "outputOptions.contentRoot";
		private static final String COPIES_PARAM = "outputOptions.copies";
		private static final String DEBUG_DIR_PARAM = "outputOptions.debugDir";
		private static final String LOCALE_PARAM = "outputOptions.locale";
		private static final String PAGINATION_OVERRIDE_PARAM = "outputOptions.paginationOverride";
		private static final String PRINT_CONFIG_PARAM = "outputOptions.printConfig";
		private static final String XCI_PARAM = "outputOptions.xci";
		
		private PathOrUrl contentRoot = null;
		private Integer copies = null;
		private Path debugDir = null;
		private Locale locale = null;
		private PaginationOverride paginationOverride = null;
		private PrintConfig printConfig = null;
		private byte[] xci = null;
		private final TemplateParameter template;
		private final byte[] data;
		
		public GeneratePrintedOutputParameters(TemplateParameter template, byte[] data) {
			super();
			this.template = template;
			this.data = data;
		}

		public GeneratePrintedOutputParameters(TemplateParameter template) {
			super();
			this.template = template;
			this.data = null;
		}

		public PathOrUrl getContentRoot() {
			return contentRoot;
		}

		private GeneratePrintedOutputParameters setContentRoot(String contentRootStr) {
			this.contentRoot = PathOrUrl.from(contentRootStr);
			return this;
		}

		public Integer getCopies() {
			return copies;
		}

		private GeneratePrintedOutputParameters setCopies(String copies) {
			this.copies = Integer.valueOf(copies);
			return this;
		}

		public Path getDebugDir() {
			return debugDir;
		}

		private GeneratePrintedOutputParameters setDebugDir(String debugDirStr) {
			this.debugDir = Paths.get(debugDirStr);
			return this;
		}

		public Locale getLocale() {
			return locale;
		}

		private GeneratePrintedOutputParameters setLocale(String localeStr) {
			this.locale = Locale.forLanguageTag(localeStr);
			return this;
		}
		
		public PaginationOverride getPaginationOverride() {
			return paginationOverride;
		}
		
		private GeneratePrintedOutputParameters setPaginationOverride(String paginationOverrideStr) {
			this.paginationOverride = PaginationOverride.valueOf(paginationOverrideStr);
			return this;
		}
		
		public PrintConfig getPrintConfig() {
			return printConfig;
		}
		
		private GeneratePrintedOutputParameters setPrintConfig(String printConfigStr) {
			log.info("PRINTCONFIG:  {}", printConfigStr);
			switch (printConfigStr) {
			case "DPL300":
				this.printConfig = PrintConfig.DPL300;
				break;
			case "DPL406":
				this.printConfig = PrintConfig.DPL406;
				break;
			case "DPL600":
				this.printConfig = PrintConfig.DPL600;
				break;
			case "Generic_PS_L3" :
				this.printConfig = PrintConfig.Generic_PS_L3;
				break;
			case "GenericColor_PCL_5c":
				this.printConfig = PrintConfig.GenericColor_PCL_5c;
				break;
			case "HP_PCL_5e":
				this.printConfig = PrintConfig.HP_PCL_5e;
				break;
			case "IPL300":
				this.printConfig = PrintConfig.IPL300;
				break;
			case "IPL400" :
				this.printConfig = PrintConfig.IPL400;
				break;
			case "PS_PLAIN":
				this.printConfig = PrintConfig.PS_PLAIN;
				break;
			case "TPCL305":
				this.printConfig = PrintConfig.TPCL305;
				break;
			case "TPCL600":
				this.printConfig = PrintConfig.TPCL600;
				break;
			case "ZPL300" :
				this.printConfig = PrintConfig.ZPL300;
				break;
			case "ZPL600" :
				this.printConfig = PrintConfig.ZPL600;
				break;
			default:
				log.warn("Custom Print Configurations are not supported ({}).  Assuming PS_PLAIN instead.", printConfigStr);
				this.printConfig = PrintConfig.PS_PLAIN;
			}
			return this;
		}

		public byte[] getXci() {
			return xci;
		}

		private GeneratePrintedOutputParameters setXci(byte[] xci) {
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
		public static GeneratePrintedOutputParameters readFormParameters(SlingHttpServletRequest request, boolean validateXml) throws BadRequestException {
			try {
				
				TemplateParameter template = TemplateParameter.readParameter(getMandatoryParameter(request, TEMPLATE_PARAM));

				// Data parameter is optional. If Data is not supplied, then an empty form is produced.
				Optional<DataParameter> data = getOptionalParameter(request, DATA_PARAM)
															.map(p -> DataParameter.readParameter(p, validateXml));

				if (data.isPresent() && data.get().getType() != ParameterType.ByteArray) {
					throw new BadRequestException("GeneratePrintedOutput only supports providing data by value at this time.");
				}
				byte[] inputData = data.map(DataParameter::getArray).orElse(null);

				GeneratePrintedOutputParameters result = inputData == null ? new GeneratePrintedOutputParameters(template) : new GeneratePrintedOutputParameters(template, inputData);
				
				getOptionalParameter(request, CONTENT_ROOT_PARAM).ifPresent(rp->result.setContentRoot(rp.getString()));
				getOptionalParameter(request, COPIES_PARAM).ifPresent(rp->result.setCopies(rp.getString()));
				getOptionalParameter(request, DEBUG_DIR_PARAM).ifPresent(rp->result.setDebugDir(rp.getString()));
				getOptionalParameter(request, LOCALE_PARAM).ifPresent(rp->result.setLocale(rp.getString()));
				getOptionalParameter(request, PAGINATION_OVERRIDE_PARAM).ifPresent(rp->result.setPaginationOverride(rp.getString()));
				result.setPrintConfig(getMandatoryParameter(request, PRINT_CONFIG_PARAM).getString());
				getOptionalParameter(request, XCI_PARAM).ifPresent(rp->result.setXci(rp.get()));
				
				return result;
			} catch (IllegalArgumentException e) {
				throw new BadRequestException("There was a problem with one of the incoming parameters.", e);
			}
		}	
	}

}
