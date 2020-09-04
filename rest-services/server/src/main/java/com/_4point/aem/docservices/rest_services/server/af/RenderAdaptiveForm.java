//package com._4point.aem.docservices.rest_services.server.af;
//
//import static com._4point.aem.docservices.rest_services.server.FormParameters.getMandatoryParameter;
//
//import java.io.IOException;
//import java.nio.file.Paths;
//import java.util.Optional;
//
//import javax.servlet.Servlet;
//import javax.servlet.ServletException;
//
//import org.apache.sling.api.SlingHttpServletRequest;
//import org.apache.sling.api.SlingHttpServletResponse;
//import org.apache.sling.api.request.RequestParameter;
//import org.apache.sling.api.servlets.HttpConstants;
//import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
//import org.apache.sling.servlets.annotations.SlingServletPaths;
//import org.osgi.framework.Constants;
//import org.osgi.service.component.annotations.Component;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com._4point.aem.docservices.rest_services.server.Exceptions.BadRequestException;
//import com._4point.aem.docservices.rest_services.server.Exceptions.InternalServerErrorException;
//import com._4point.aem.docservices.rest_services.server.Exceptions.NotAcceptableException;
//import com._4point.aem.docservices.rest_services.server.html5.RenderHtml5Form;
//
//// This class is disabled, but I've left it included in case we need to develop it later.  For now, the client library is calling
//// the Adaptive Form links directly in AEM without calling this routine.  This routine may become necessary if we want to accept 
//// POSTs with data in them.
//
//// The main issue I encountered is that parameters were lost when I transferred control from this servlet to the main af location 
//// ("/content/forms/af/" + relativeTemplateUrl + ".html") using the requestDispatcher's include() method.
//// Any request attributes I added seemed to be ignored, despite having been documented here:  https://docs.adobe.com/content/help/en/experience-manager-65/forms/adaptive-forms-advanced-authoring/prepopulate-adaptive-form-fields.html
//// Also, query parameters were ignored, despite what the spec says:  see  
//// https://stackoverflow.com/questions/21882485/how-to-pass-a-request-parameter-from-one-servlet-to-a-other-while-forwaring#:~:text=include(request%2C%20response)%3B,the%20include%20or%20forward%20call.
//// https://download.oracle.com/otndocs/jcp/servlet-3.0-fr-eval-oth-JSpec/
//// Maybe this is something that can be fixed later...
//
//@SuppressWarnings("serial")
////@Component(service=Servlet.class, property={Constants.SERVICE_DESCRIPTION + "=Adaptive Form Service",
////		"sling.servlet.methods=" + HttpConstants.METHOD_POST})
////@SlingServletPaths("/services/AdaptiveForms/RenderAdaptiveForm")
//public class RenderAdaptiveForm extends SlingSafeMethodsServlet {
//	private static final String DATA_REF_PARAM = "dataRef";
//
//	private static final String TEMPLATE_ATTRIBUTE_NAME = "template";
//
//	private static final Logger log = LoggerFactory.getLogger(RenderHtml5Form.class);
//
//	@Override
//	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
//			throws ServletException, IOException {
//		try {
//			this.processInput(request, response);
//		} catch (BadRequestException br) {
//			log.warn("Bad Request from the user.", br);
//			response.sendError(SlingHttpServletResponse.SC_BAD_REQUEST, br.getMessage());
//		} catch (InternalServerErrorException ise) {
//			log.error("Internal server error.", ise);
//			response.sendError(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR, ise.getMessage());
//		} catch (NotAcceptableException nae) {
//			log.error("NotAcceptable error.", nae);
//			response.sendError(SlingHttpServletResponse.SC_NOT_ACCEPTABLE, nae.getMessage());
//		} catch (Exception e) {  			// Some exception we haven't anticipated.
//			log.error(e.getMessage() != null ? e.getMessage() : e.getClass().getName() , e);	// Make sure this gets into our log.
//			throw e;
//		}
//	}
//
//	private void processInput(SlingHttpServletRequest request, SlingHttpServletResponse response) throws BadRequestException, InternalServerErrorException, NotAcceptableException {
//		// Parse the Parameters
//		RenderAdaptiveFormParameters parameters = RenderAdaptiveFormParameters.readParameters(request, false);
//		
//		// https://helpx.adobe.com/experience-manager/6-2/forms/using/prepopulate-adaptive-form-fields.html
//		// Validate the parameters.
//		// I would like to allow for rendering templates by value in the future, but we don't need that
//		// immediately, so this code does not support it yet.
//			// Converting to Path and back eliminates any wayward slashes.
//		String relativeTemplateUrl = convertPathToRelativeUrl(Paths.get(parameters.getTemplate()));
//		log.info("Received request to render Adaptive Form '" + relativeTemplateUrl + "'.");
//		// Set the appropriate attributes in the request before dispatching it.
//
//		// I would like to allow passing data by reference in the future, but we don't need that
//		// immediately, so this code does not support it yet. Instead, we expect that the incoming request has a dataRef
//		// that references the FFPrefillService (using dataRef=service://FFPrefillService/{uuid}
//		Optional<RequestParameter> dataRefParameter = Optional.ofNullable(request.getRequestParameter(DATA_REF_PARAM));
//		log.info(dataRefParameter.map((p)->"Found dataRef parameter '" + p.getString() + "'.")
//								 .orElse("No dataRef parameter was provided."));
//		String queryParam = dataRefParameter.map((p)->"?"+ DATA_REF_PARAM + "=" + p).orElse("");
//		
//		// AEM needs to have "protected mode" turned off for this to work.
//		try {
//			request.getRequestDispatcher("/content/forms/af/" + relativeTemplateUrl + ".html" + queryParam).include(request, response);
//		} catch (ServletException | IOException e) {
//			throw new InternalServerErrorException("Error while redirecting to Adaptive Form url. (" + (e.getMessage() == null ? e.getClass().getName() : e.getMessage()) + ")" , e);
//		}
//	}
//
//	// This is not currently used, but I'm leaving it here so that it matches the RenderHtml5Form functions.
//	@SuppressWarnings("unused")
//	private static SlingHttpServletRequest setRequestAttribute(SlingHttpServletRequest request, String attributeName, String attributeValue) {
//		log.info("Setting '" + attributeName + "' attribute in request to '" + attributeValue  + "'.");
//		request.setAttribute(attributeName, attributeValue);
//		return request;
//	}
//	
//	// This is not currently used, but I'm leaving it here for now.
//	@SuppressWarnings("unused")
//	private static SlingHttpServletRequest setRequestAttribute(SlingHttpServletRequest request, String attributeName, byte[] attributeValue) {
//		log.info("Setting '" + attributeName + "' attribute in request.");
//		request.setAttribute(attributeName, attributeValue);
//		return request;
//	}
//
//	private static String convertPathToRelativeUrl(java.nio.file.Path template) {
//		return template.toString().replace('\\', '/');
//	}
//	
//	private static class RenderAdaptiveFormParameters {
//		private static final String TEMPLATE_PARAM = TEMPLATE_ATTRIBUTE_NAME;
//
//		private final String template;
//		
//		private RenderAdaptiveFormParameters(String template) {
//			super();
//			this.template = template;
//		}
//
//		public String getTemplate() {
//			return template;
//		}
//
//		public static RenderAdaptiveFormParameters readParameters(SlingHttpServletRequest request, boolean validateXml) throws BadRequestException {
//			try {
//				String template = getMandatoryParameter(request, TEMPLATE_PARAM).getString();
//				return new RenderAdaptiveFormParameters(template);
//			} catch (IllegalArgumentException | BadRequestException e) {
//				String msg = e.getMessage();
//				throw new BadRequestException("There was a problem with one of the incoming parameters. (" + (msg == null ? e.getClass().getName() : msg) + ").", e);
//			}
//		}
//	}
//}
