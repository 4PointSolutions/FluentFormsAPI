package com._4point.aem.docservices.rest_services.server.html5;

import static com._4point.aem.docservices.rest_services.server.FormParameters.getMandatoryParameter;
import static com._4point.aem.docservices.rest_services.server.FormParameters.getOptionalParameter;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com._4point.aem.docservices.rest_services.server.ContentType;
import com._4point.aem.docservices.rest_services.server.DataParameter;
import com._4point.aem.docservices.rest_services.server.ServletUtils;
import com._4point.aem.docservices.rest_services.server.Exceptions.BadRequestException;
import com._4point.aem.docservices.rest_services.server.Exceptions.InternalServerErrorException;
import com._4point.aem.docservices.rest_services.server.Exceptions.NotAcceptableException;
import com._4point.aem.docservices.rest_services.server.TemplateParameter;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.impl.TemplateValues;
import com._4point.aem.fluentforms.impl.UsageContext;

@SuppressWarnings("serial")
@Component(service=Servlet.class, property={Constants.SERVICE_DESCRIPTION + "=Html5 Service",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST})
@SlingServletPaths(ServletUtils.SERVICES_PREFIX + "/Html5/RenderHtml5Form")
public class RenderHtml5Form extends SlingAllMethodsServlet {
	private static final String DATA_REF_ATTRIBUTE_NAME = "dataRef";
	private static final String DATA_ATTRIBUTE_NAME = "data";
	private static final String CONTENT_ROOT_ATTRIBUTE_NAME = "contentRoot";
	private static final String TEMPLATE_ATTRIBUTE_NAME = "template";
	private static final String SUBMIT_URL_ATTRIBUTE_NAME = "submitUrl";

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
		RenderHtml5FormParameters parameters = RenderHtml5FormParameters.readParameter(request, false);
		
		try {
			// Set the template and contentRoot parameters.
			TemplateParameter templateParam = parameters.getTemplate();
			if (templateParam.getType() == TemplateParameter.ParameterType.PathOrUrl) {
				PathOrUrl templateLocation = templateParam.getPathOrUrl();

				// Fix up the content root and filename.  If the filename has a directory in front, move it to the content root.
				PathOrUrl contentRoot = parameters.getContentRoot().orElse(null);
				Optional<TemplateValues> otvs = TemplateValues.determineTemplateValues(templateLocation, contentRoot, UsageContext.CLIENT_SIDE);
				if (otvs.isPresent()) {
					TemplateValues tvs = otvs.get();
					templateLocation = PathOrUrl.from(tvs.getTemplate());
					contentRoot = tvs.getContentRoot();
				}
				setRequestAttribute(request, TEMPLATE_ATTRIBUTE_NAME, templateLocation.toString());

				if (contentRoot != null) {
					setRequestAttribute(request, CONTENT_ROOT_ATTRIBUTE_NAME, contentRoot.toString());
				}
			} else {
				// I would like to fix this in the future to allow for rendering templates by reference, but we don't need that
				// immediately, so this code does not support it yet.
				throw new BadRequestException("RenderHtml5Form only supports rendering templates by reference at this time.");
			}
			
			// Set the data parameter
			parameters.getData()
					  .ifPresent((dp)->setDataRequestParameter(request, dp));
			
			parameters.getSubmitUrl()
					  .ifPresent(url->request.setAttribute(SUBMIT_URL_ATTRIBUTE_NAME, url.toString()));

			// AEM needs to have "protected mode" turned off for this to work.
			request.getRequestDispatcher("/content/xfaforms/profiles/default.html").include(request, response);
		} catch (ServletException | IOException e) {
			throw new InternalServerErrorException("Error while redirecting to html5 profile. (" + (e.getMessage() == null ? e.getClass().getName() : e.getMessage()) + ")" , e);
		}
	}

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
	

	private void setDataRequestParameter(SlingHttpServletRequest request, DataParameter dp) {
		switch(dp.getType()) {
		case ByteArray:
			setRequestAttribute(request, DATA_ATTRIBUTE_NAME, dp.getArray());
			break;
		case PathOrUrl:
			PathOrUrl dPathOrUrl = dp.getPathOrUrl();
			log.info("PathOrUrl isUrl='" + dPathOrUrl.isUrl() + "', isPath='" + dPathOrUrl.isPath() + "'.");
			String dataPath = dPathOrUrl.isPath() ? dPathOrUrl.getPath().toUri().toString() : dPathOrUrl.toString();
			setRequestAttribute(request, DATA_REF_ATTRIBUTE_NAME, dataPath);
			break;
		default:
			throw new IllegalStateException("Unknown DataParameter Type (" + dp.getType().toString() + ").");
		}
	}
	
	/**
	 * Parameters for rendering an HTML5 Form 
	 * - Based on https://docs.adobe.com/content/help/en/experience-manager-65/forms/html5-forms/rendering-form-template.html
	 *
	 */
	private static class RenderHtml5FormParameters {
		private static final String TEMPLATE_PARAM = TEMPLATE_ATTRIBUTE_NAME;
		private static final String DATA_PARAM = DATA_ATTRIBUTE_NAME;
		private static final String CONTENT_ROOT_PARAM = CONTENT_ROOT_ATTRIBUTE_NAME;
		private static final String SUBMIT_URL_PARAM = "submitUrl";
		
		private final TemplateParameter template;
		private final Optional<PathOrUrl> contentRoot;
		private final Optional<URL> submitUrl;
		private final Optional<DataParameter> data;
		
		private RenderHtml5FormParameters(TemplateParameter template, Optional<PathOrUrl> contentRoot, Optional<URL> submitUrl, Optional<DataParameter> data) {
			super();
			this.template = template;
			this.contentRoot = contentRoot;
			this.submitUrl = submitUrl;
			this.data = data;
		}

		public TemplateParameter getTemplate() {
			return template;
		}

		public Optional<PathOrUrl> getContentRoot() {
			return contentRoot;
		}

		public Optional<URL> getSubmitUrl() {
			return submitUrl;
		}

		public Optional<DataParameter> getData() {
			return data;
		}

		public static RenderHtml5FormParameters readParameter(SlingHttpServletRequest request, boolean validateXml) throws BadRequestException {
			try {
				TemplateParameter template = TemplateParameter.readParameter(getMandatoryParameter(request, TEMPLATE_PARAM));
				Optional<PathOrUrl> contentRoot = getOptionalParameter(request, CONTENT_ROOT_PARAM)
														.map(RequestParameter::getString)
														.map(PathOrUrl::from);
														
				Optional<URL> submitUrl = getOptionalParameter(request, SUBMIT_URL_PARAM)
														.map(RequestParameter::getString)
														.map(RenderHtml5FormParameters::createSubmitUrl);
				Optional<DataParameter> data = getOptionalParameter(request, DATA_PARAM)
														.map(p->DataParameter.readParameter(p,validateXml));

				return new RenderHtml5FormParameters(template, contentRoot, submitUrl, data);
			} catch (IllegalArgumentException e) {
				String msg = e.getMessage();
				throw new BadRequestException("There was a problem with one of the incoming parameters. (" + (msg == null ? e.getClass().getName() : msg) + ").", e);
			}
		}
		
		private static URL createSubmitUrl(String urlString){
			try {
				return new URL(urlString);
			} catch (MalformedURLException e) {
				throw new IllegalArgumentException("Malformed URL in submitUrl (" + urlString + ").", e);
			}
		}
	}
}
