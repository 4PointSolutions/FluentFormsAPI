package com._4point.aem.docservices.rest_services.server.forms;

import java.io.IOException;
import java.util.Objects;
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
import com._4point.aem.fluentforms.impl.AdobeDocumentFactoryImpl;
import com._4point.aem.fluentforms.impl.UsageContext;
import com._4point.aem.fluentforms.impl.forms.AdobeFormsServiceAdapter;
import com._4point.aem.fluentforms.impl.forms.FormsServiceImpl;
import com._4point.aem.fluentforms.impl.forms.TraditionalFormsService;
import com.adobe.fd.forms.api.DataFormat;

@Component(service=Servlet.class, property={Constants.SERVICE_DESCRIPTION + "=FormsService.ExportData Service",
											"sling.servlet.methods=" + HttpConstants.METHOD_POST})
@SlingServletPaths(ServletUtils.SERVICES_PREFIX + "/FormsService/ExportData")
public class ExportData extends SlingAllMethodsServlet {
	
	
	//Document pdfOrXdp, DataFormat dataFormat
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(ExportData.class);
	
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
		
			// In the following call to the formsService, we only set the parameters if they are not null.
			
			RequestParameter pdforxdpParameter = FormParameters.getMandatoryParameter(request, "pdforxdp");
			DataFormat dataformat	=DataFormat.valueOf(FormParameters.getMandatoryParameter(request,"dataformat").getString());
			byte[] pdfBytes = getPdforxdpBytes(pdforxdpParameter);
			
			Document pdforxdp = docFactory.create(pdfBytes);
			
		
			try {
				Optional<Document> optResult = formsService.exportData(pdforxdp, dataformat);
				if (optResult.isPresent()) {
					try (Document result = optResult.get()) {
						result.setContentType(ContentType.APPLICATION_XML.toString());	// We know the result is always XDP.
						ServletUtils.transferDocumentToResponse(request, response, result, false);
				}
				} else {
					response.setStatus(SlingHttpServletResponse.SC_NO_CONTENT);
				}
			} catch (FormsServiceException | IOException ex1) {
				throw new InternalServerErrorException("Internal Error while importing data", ex1);
			} catch (IllegalArgumentException ex2) {
				throw new BadRequestException("Bad arguments while importing data", ex2);
			}
				
			
		}
	
	
	private byte[] getPdforxdpBytes(RequestParameter pdforxdpParameter) throws BadRequestException {
		byte[] pdforxdpBytes;
		ContentType pdforxdpParamContentType =ContentType.valueOf(Objects.requireNonNull(pdforxdpParameter.getContentType(), "PDForxdp Parameter content-type must be provided."));
		if (pdforxdpParamContentType.equals(ContentType.APPLICATION_PDF)) {
			pdforxdpBytes = pdforxdpParameter.get();
		}	
		 else {
			// Throw bad request error.
			throw new BadRequestException("Invalid content-type on pdf param '" + pdforxdpParamContentType.getContentTypeStr() + "'.");
		}
		
		return pdforxdpBytes;
		
		
	}

	private TraditionalFormsService getAdobeFormsService() {
		return new AdobeFormsServiceAdapter(adobeFormsService);
	}
	
}
