package com._4point.aem.docservices.rest_services.server.forms;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com._4point.aem.docservices.rest_services.server.AcceptHeaders;
import com._4point.aem.docservices.rest_services.server.ByteArrayString;
import com._4point.aem.docservices.rest_services.server.ContentType;
import com._4point.aem.docservices.rest_services.server.Exceptions.BadRequestException;
import com._4point.aem.docservices.rest_services.server.Exceptions.InternalServerErrorException;
import com._4point.aem.docservices.rest_services.server.Exceptions.NotAcceptableException;
import com._4point.aem.docservices.rest_services.server.FormParameters;
import com._4point.aem.docservices.rest_services.server.ServletUtils;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.forms.FormsService;
import com._4point.aem.fluentforms.api.forms.FormsService.FormsServiceException;
import com._4point.aem.fluentforms.impl.UsageContext;
import com._4point.aem.fluentforms.impl.forms.AdobeFormsServiceAdapter;
import com._4point.aem.fluentforms.impl.forms.FormsServiceImpl;
import com._4point.aem.fluentforms.impl.forms.TraditionalFormsService;

@SuppressWarnings("serial")
@Component(service=Servlet.class, property={Constants.SERVICE_DESCRIPTION + "=FormsService.ImportData Service",
											"sling.servlet.methods=" + HttpConstants.METHOD_POST})
@SlingServletPaths("/services/FormsService/ImportData")
public class ImportData extends SlingAllMethodsServlet {

	private static final String PDF_PARAM_NAME = "pdf";
	private static final String DATA_PARAM_NAME = "data";
	private static final Logger log = LoggerFactory.getLogger(ImportData.class);
	private final DocumentFactory docFactory = DocumentFactory.getDefault();
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

	private void processInput(SlingHttpServletRequest request, SlingHttpServletResponse response) throws InternalServerErrorException, BadRequestException, NotAcceptableException {
		FormsService formsService = new FormsServiceImpl(formServiceFactory.get(), UsageContext.SERVER_SIDE);
		
		RequestParameter pdfParameter = FormParameters.getMandatoryParameter(request, PDF_PARAM_NAME);
		RequestParameter dataParameter = FormParameters.getMandatoryParameter(request, DATA_PARAM_NAME);
		byte[] pdfBytes = getPdfBytes(pdfParameter);
		byte[] dataBytes = getDataBytes(dataParameter);

		Document pdf = docFactory.create(pdfBytes);
		Document data = docFactory.create(dataBytes);
		
		try {
			try (Document result = formsService.importData(pdf, data)) {
			
				
				String contentType = ContentType.APPLICATION_PDF.toString();	// We know the result is always PDF.
				ServletUtils.validateAcceptHeader(request.getHeader(AcceptHeaders.ACCEPT_HEADER_STR), contentType);
				response.setContentType(contentType);
//				response.setContentLength((int)result.length());	// Setting the content length seems to throw an UnsupportedOperation exception.
				ServletUtils.transfer(result.getInputStream(), response.getOutputStream());
			}
		} catch (FormsServiceException | IOException ex1) {
			throw new InternalServerErrorException("Internal Error while importing data", ex1);
		} catch (IllegalArgumentException ex2) {
			throw new BadRequestException("Bad arguments while importing data", ex2);
		}
		
	}
	
	private byte[] getPdfBytes(RequestParameter pdfParameter) throws BadRequestException {
		byte[] pdfBytes;
		ContentType pdfParamContentType =ContentType.valueOf(Objects.requireNonNull(pdfParameter.getContentType(), "PDF Parameter content-type must be provided."));
		log.info("pdfContentType={}", pdfParamContentType);
		if (pdfParamContentType.equals(ContentType.TEXT_PLAIN)) {
			// File path was provided.
			// TODO: Implement a read from the local file system.  This will require some configuration
			// parameters as we don't want to give unfettered access to the local hard drive.
			throw new UnsupportedOperationException("Importing data into a PDF residing on the local hard drive is not supported at this time.");
		} else if (pdfParamContentType.equals(ContentType.APPLICATION_PDF)) {
			// Template bytes were provided.
			pdfBytes = pdfParameter.get();
			log.info("pdfBytes={}", ByteArrayString.toString(pdfBytes, 15));
		} else {
			// Throw bad request error.
			throw new BadRequestException("Invalid content-type on pdf param '" + pdfParamContentType.getContentTypeStr() + "'.");
		}
		return pdfBytes;
	}

	private byte[] getDataBytes(RequestParameter dataParameter) throws BadRequestException {
		byte[] dataBytes;
		ContentType dataParamContentType =ContentType.valueOf(Objects.requireNonNull(dataParameter.getContentType(), "PDF Parameter content-type must be provided."));
		log.info("dataContentType={}", dataParamContentType);
		if (dataParamContentType.equals(ContentType.TEXT_PLAIN)) {
			// File path was provided.
			// TODO: Implement a read from the local file system.  This will require some configuration
			// parameters as we don't want to give unfettered access to the local hard drive.
			throw new UnsupportedOperationException("Importing data from the local hard drive is not supported at this time.");
		} else if (dataParamContentType.equals(ContentType.APPLICATION_XML)) {
			// Template bytes were provided.
			dataBytes = dataParameter.get();
			log.info("dataBytes={}", ByteArrayString.toString(dataBytes, 15));
		} else {
			// Throw bad request error.
			throw new BadRequestException("Invalid content-type on data param '" + dataParamContentType.getContentTypeStr() + "'.");
		}
		return dataBytes;
	}

	private TraditionalFormsService getAdobeFormsService() {
		return new AdobeFormsServiceAdapter(adobeFormsService);
	}

}
