package com._4point.aem.docservices.rest_services.server.af;

import static com._4point.aem.docservices.rest_services.server.FormParameters.getMandatoryParameter;
import static com._4point.aem.docservices.rest_services.server.FormParameters.getOptionalParameter;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com._4point.aem.docservices.rest_services.server.DataParameter;
import com._4point.aem.docservices.rest_services.server.TemplateParameter;
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
public class RenderAdaptiveForm extends SlingAllMethodsServlet {
	private static final String DATA_ATTRIBUTE_NAME = "data";
	private static final String TEMPLATE_ATTRIBUTE_NAME = "template";

	private static final Logger log = LoggerFactory.getLogger(RenderHtml5Form.class);

	@Override
	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
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
		TemplateParameter templateParam = parameters.getTemplate();
		if (templateParam.getType() != TemplateParameter.ParameterType.PathOrUrl) {
			// I would like to fix this in the future to allow for rendering templates by reference, but we don't need that
			// immediately, so this code does not support it yet.
			throw new BadRequestException("RenderAdaptiveForm only supports rendering templates by reference at this time.");
		}
		PathOrUrl templatePathOrUrl = templateParam.getPathOrUrl();
		if (!templatePathOrUrl.isPath()) {
			throw new BadRequestException("RenderAdaptiveForm only supports rendering templates by relative reference at this time.");
		}
		Optional<DataParameter> dataParam = parameters.getData();
		if (dataParam.isPresent() && dataParam.get().getType() != ParameterType.ByteArray) {
			throw new BadRequestException("GeneratePdfOutput only supports providing data by value at this time.");
		}

		// Set the appropriate attributes in the request before dispatching it.
//		setRequestAttribute(request, TEMPLATE_ATTRIBUTE_NAME, templateParam.getPathOrUrl().toString());

		if (dataParam.isPresent()) {
			setRequestAttribute(request, DATA_ATTRIBUTE_NAME, dataParam.get().getArray());
		}
		
//		.path()
//		.queryParam("wcmmode", "disabled");

		// AEM needs to have "protected mode" turned off for this to work.
		try {
			request.getRequestDispatcher("/content/forms/af/" + convertPathToRelativeUrl(templatePathOrUrl.getPath()) + ".html?wcmmode=disabled").include(request, response);
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

		private final TemplateParameter template;
		private final Optional<DataParameter> data;
		
		private RenderAdaptiveFormParameters(TemplateParameter template, Optional<DataParameter> data) {
			super();
			this.template = template;
			this.data = data;
		}

		public TemplateParameter getTemplate() {
			return template;
		}

		public Optional<DataParameter> getData() {
			return data;
		}

		public static RenderAdaptiveFormParameters readParameters(SlingHttpServletRequest request, boolean validateXml) throws BadRequestException {

			try {
				TemplateParameter template = TemplateParameter.readParameter(getMandatoryParameter(request, TEMPLATE_PARAM));
				Optional<DataParameter> data = getOptionalParameter(request, DATA_PARAM)
																	.map(p->DataParameter.readParameter(p,validateXml));
				return new RenderAdaptiveFormParameters(template, data);
			} catch (IllegalArgumentException | BadRequestException e) {
				String msg = e.getMessage();
				throw new BadRequestException("There was a problem with one of the incoming parameters. (" + (msg == null ? e.getClass().getName() : msg) + ").", e);
			}
		}
	}
}
