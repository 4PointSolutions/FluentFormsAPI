package com._4point.aem.docservices.rest_services.server.pdfUtility;

import static com._4point.aem.docservices.rest_services.server.FormParameters.getMandatoryParameter;

import java.io.IOException;
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
import com._4point.aem.docservices.rest_services.server.Exceptions.BadRequestException;
import com._4point.aem.docservices.rest_services.server.Exceptions.InternalServerErrorException;
import com._4point.aem.docservices.rest_services.server.Exceptions.NotAcceptableException;
import com._4point.aem.docservices.rest_services.server.ServletUtils;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.pdfUtility.PdfUtilityService;
import com._4point.aem.fluentforms.api.pdfUtility.PdfUtilityService.PdfUtilityException;
import com._4point.aem.fluentforms.impl.pdfUtility.AdobePdfUtilityServiceAdapter;
import com._4point.aem.fluentforms.impl.pdfUtility.PdfUtilityServiceImpl;
import com._4point.aem.fluentforms.impl.pdfUtility.TraditionalPdfUtilityService;

@SuppressWarnings("serial")
@Component(service=Servlet.class, property={Constants.SERVICE_DESCRIPTION + "=PdfUtility.ConvertPdfToXdp Service",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST})
@SlingServletPaths(ServletUtils.SERVICES_PREFIX + "/PdfUtility/ConvertPdfToXdp")
public class ConvertPdfToXdp extends SlingAllMethodsServlet {
	private static final Logger log = LoggerFactory.getLogger(ConvertPdfToXdp.class);
	
	private static final String APPLICATION_XDP = "application/vnd.adobe.xdp+xml";
	private static final String DOCUMENT_PARAM_NAME = "document";
	
	private final DocumentFactory docFactory = DocumentFactory.getDefault();
	private final Supplier<TraditionalPdfUtilityService> pdfUtilityServiceFactory = this::getAdobePdfUtilityService;

	@Reference
	private com.adobe.fd.pdfutility.services.PDFUtilityService adobePdfUtilityService;

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

	private void processInput(SlingHttpServletRequest request, SlingHttpServletResponse response) throws BadRequestException, InternalServerErrorException, NotAcceptableException, IOException {
		PdfUtilityService pdfUtilityService = new PdfUtilityServiceImpl(pdfUtilityServiceFactory.get());
		
		RequestParameter document = getMandatoryParameter(request, DOCUMENT_PARAM_NAME);
		Document documentParameter = docFactory.create(document.getInputStream());
		try (Document result = pdfUtilityService.convertPDFtoXDP(documentParameter)){
			String contentType = requireNonNullOrElse(result.getContentType(), APPLICATION_XDP);
			ServletUtils.validateAcceptHeader(request.getHeader(AcceptHeaders.ACCEPT_HEADER_STR), contentType);
			response.setContentType(contentType);
			response.setContentLength((int)result.length());
			ServletUtils.transfer(result.getInputStream(), response.getOutputStream());
		} catch (PdfUtilityException e) {
			throw new InternalServerErrorException("Internal Error while converting Pdf to Xdp. (" + e.getMessage() + ").", e);
		}
		
	}
	
	private TraditionalPdfUtilityService getAdobePdfUtilityService() {
		return new AdobePdfUtilityServiceAdapter(adobePdfUtilityService);
	}

	private String requireNonNullOrElse(String possible, String orElse) {
		return possible != null ? possible : orElse;
	}
}
