package com._4point.aem.docservices.rest_services.server.af;

import static com._4point.aem.docservices.rest_services.server.FormParameters.getMandatoryParameter;
import static com._4point.aem.docservices.rest_services.server.FormParameters.getOptionalParameter;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com._4point.aem.docservices.rest_services.server.DataParameter;
import com._4point.aem.docservices.rest_services.server.TemplateParameter;
import com._4point.aem.docservices.rest_services.server.data.DataCache;
import com._4point.aem.docservices.rest_services.server.data.DataCache.Entry;
import com._4point.aem.docservices.rest_services.server.DataParameter.ParameterType;
import com._4point.aem.docservices.rest_services.server.Exceptions.BadRequestException;
import com._4point.aem.docservices.rest_services.server.Exceptions.InternalServerErrorException;
import com._4point.aem.docservices.rest_services.server.Exceptions.NotAcceptableException;
import com._4point.aem.docservices.rest_services.server.html5.RenderHtml5Form;
import com._4point.aem.fluentforms.api.PathOrUrl;

@SuppressWarnings("serial")
@Component(service=Servlet.class, property={Constants.SERVICE_DESCRIPTION + "=Adaptive Form Service",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST})
@SlingServletPaths("/services/AdaptiveForms/RenderAdaptiveForm")
public class RenderAdaptiveForm extends SlingSafeMethodsServlet {
	private static final String DATA_ATTRIBUTE_NAME = "data";
	private static final String TEMPLATE_ATTRIBUTE_NAME = "template";

	private static final Logger log = LoggerFactory.getLogger(RenderHtml5Form.class);

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		try {
			this.processInput(request, response);
		} catch (BadRequestException br) {
			log.warn("Bad Request from the user.", br);
			response.sendError(SlingHttpServletResponse.SC_BAD_REQUEST, br.getMessage());
		} catch (InternalServerErrorException ise) {
			log.error("Internal server error.", ise);
			response.sendError(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR, ise.getMessage());
		} catch (NotAcceptableException nae) {
			log.error("NotAcceptable error.", nae);
			response.sendError(SlingHttpServletResponse.SC_NOT_ACCEPTABLE, nae.getMessage());
		} catch (Exception e) {  			// Some exception we haven't anticipated.
			log.error(e.getMessage() != null ? e.getMessage() : e.getClass().getName() , e);	// Make sure this gets into our log.
			throw e;
		}
	}

	private void processInput(SlingHttpServletRequest request, SlingHttpServletResponse response) throws BadRequestException, InternalServerErrorException, NotAcceptableException {
		// Parse the Parameters
		RenderAdaptiveFormParameters parameters = RenderAdaptiveFormParameters.readParameters(request, false);
		
		// https://helpx.adobe.com/experience-manager/6-2/forms/using/prepopulate-adaptive-form-fields.html
		// Validate the parameters.
		// I would like to allow for rendering templates by value in the future, but we don't need that
		// immediately, so this code does not support it yet.
		Path templatePath = Paths.get(parameters.getTemplate());	// Converting to Path and back eliminates any wayward slashes.

		// Set the appropriate attributes in the request before dispatching it.
//		setRequestAttribute(request, TEMPLATE_ATTRIBUTE_NAME, templateParam.getPathOrUrl().toString());

		// I would like to allow passing data by reference in the future, but we don't need that
		// immediately, so this code does not support it yet. Instead, we pass data by value to the cache and then 
		// retrieve it from the cache.
		Optional<String> dataKey = parameters.getDataKey();
		if (dataKey.isPresent()) {
			String dataKeyStr = dataKey.get();
			Entry cacheEntry = DataCache.getDataFromCache(dataKeyStr)
										.orElseThrow(()->new BadRequestException("Unable to locate data for key '" + dataKeyStr + "'."));
			setRequestAttribute(request, DATA_ATTRIBUTE_NAME, cacheEntry.data());
		}

//		setRequestAttribute(request, "wcmmode", "disabled");
//		.path()
//		.queryParam("wcmmode", "disabled");

		// AEM needs to have "protected mode" turned off for this to work.
		try {
			request.getRequestDispatcher("/content/forms/af/" + convertPathToRelativeUrl(templatePath) + ".html").include(request, response);
		} catch (ServletException | IOException e) {
			throw new InternalServerErrorException("Error while redirecting to Adaptive Form url. (" + (e.getMessage() == null ? e.getClass().getName() : e.getMessage()) + ")" , e);
		}
	}

	// This is not currently used, but I'm leaving it here so that it matches the RenderHtml5Form functions.
	@SuppressWarnings("unused")
	private static SlingHttpServletRequest setRequestAttribute(SlingHttpServletRequest request, String attributeName, String attributeValue) {
		log.info("Setting '" + attributeName + "' attribute in request to '" + attributeValue  + "'.");
		request.setAttribute(attributeName, attributeValue);
		return request;
	}
	
	private static SlingHttpServletRequest setRequestAttribute(SlingHttpServletRequest request, String attributeName, byte[] attributeValue) {
		log.info("Setting '" + attributeName + "' attribute in request.");
		request.setAttribute(attributeName, attributeValue);
		return request;
	}

	private static String convertPathToRelativeUrl(java.nio.file.Path template) {
		return template.toString().replace('\\', '/');
	}
	
	private static class RenderAdaptiveFormParameters {
		private static final String TEMPLATE_PARAM = TEMPLATE_ATTRIBUTE_NAME;
		private static final String DATA_PARAM = DATA_ATTRIBUTE_NAME;
		private static final String DATA_KEY_PARAM = "dataKey";

		private final String template;
		private final Optional<String> dataKey;
		
		private RenderAdaptiveFormParameters(String template, Optional<String> dataKey) {
			super();
			this.template = template;
			this.dataKey = dataKey;
		}

		public String getTemplate() {
			return template;
		}

		public Optional<String> getDataKey() {
			return dataKey;
		}

		public static RenderAdaptiveFormParameters readParameters(SlingHttpServletRequest request, boolean validateXml) throws BadRequestException {

			try {
				String template = getMandatoryParameter(request, TEMPLATE_PARAM).getString();
				Optional<String> dataKey = getOptionalParameter(request, DATA_KEY_PARAM)
														.map(RequestParameter::getString);
				return new RenderAdaptiveFormParameters(template, dataKey);
			} catch (IllegalArgumentException | BadRequestException e) {
				String msg = e.getMessage();
				throw new BadRequestException("There was a problem with one of the incoming parameters. (" + (msg == null ? e.getClass().getName() : msg) + ").", e);
			}
		}
	}
}
