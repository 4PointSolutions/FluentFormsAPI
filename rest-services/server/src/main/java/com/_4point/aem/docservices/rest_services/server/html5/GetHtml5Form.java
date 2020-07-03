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
import com._4point.aem.docservices.rest_services.server.Exceptions.BadRequestException;
import com._4point.aem.docservices.rest_services.server.Exceptions.InternalServerErrorException;
import com._4point.aem.docservices.rest_services.server.Exceptions.NotAcceptableException;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.impl.TemplateValues;
import com._4point.aem.fluentforms.impl.UsageContext;

@SuppressWarnings("serial")
@Component(service=Servlet.class, property={Constants.SERVICE_DESCRIPTION + "=Html5 Service",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST})
@SlingServletPaths("/services/Html5/GetHtml5Form")
public class GetHtml5Form extends SlingAllMethodsServlet {
	private static final String DATA_REF_ATTRIBUTE_NAME = "dataRef";
	private static final String DATA_ATTRIBUTE_NAME = "data";
	private static final String CONTENT_ROOT_ATTRIBUTE_NAME = "contentRoot";
	private static final String TEMPLATE_ATTRIBUTE_NAME = "template";
	private static final Logger log = LoggerFactory.getLogger(GetHtml5Form.class);

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
		GetHtml5FormParameters parameters = GetHtml5FormParameters.readParameter(request, false);
		
		try {
			// Set the template and contentRoot parameters.
			GetHtml5FormParameters.TemplateParameter templateParam = parameters.getTemplate();
			if (templateParam.getType() == GetHtml5FormParameters.TemplateParameter.ParameterType.PathOrUrl) {
				PathOrUrl templateLocation = templateParam.getPathOrUrl();
				if (templateLocation.isPath()) {
					// template is a Path, so Rationalize template and contentRoot
					TemplateValues templateValues = TemplateValues.determineTemplateValues(templateLocation.getPath(), parameters.getContentRoot(), UsageContext.CLIENT_SIDE);
					String templateLocationStr = templateValues.getTemplate().toString();
					String contentRootLocationStr = templateValues.getContentRoot().toString();
					log.info("Setting '" + TEMPLATE_ATTRIBUTE_NAME + "' attribute in request to '" + templateLocationStr + "'.");
					log.info("Setting '" + CONTENT_ROOT_ATTRIBUTE_NAME + "' attribute in request to '" + contentRootLocationStr + "'.");
					request.setAttribute(TEMPLATE_ATTRIBUTE_NAME, templateLocationStr);
					request.setAttribute(CONTENT_ROOT_ATTRIBUTE_NAME, contentRootLocationStr);
				} else {
					// template is an URL of some sort (either crx: or http:) so no context root.
					String templateLocationStr = templateLocation.toString();
					log.info("Setting '" + TEMPLATE_ATTRIBUTE_NAME + "' attribute in request to '" + templateLocationStr + "'.");
					request.setAttribute(TEMPLATE_ATTRIBUTE_NAME, templateLocationStr);
				}
			} else {
				// I would like to fix this in the future to allow for rendering templates by reference, but we don't need that
				// immediately, so this code does not support it yet.
				throw new BadRequestException("GetHtml5 only supports rendering templates by reference at this time.");
			}
			
			// Set the data parameter
			parameters.getData()
					  .ifPresent((dp)->setDataRequestParameter(request, dp));

			// AEM needs to have "protected mode" turned off for this to work.
			request.getRequestDispatcher("/content/xfaforms/profiles/default.html").include(request, response);
		} catch (ServletException | IOException e) {
			throw new InternalServerErrorException("Error while redirecting to html5 profile." + e.getMessage() == null ? "" : " (" + e.getMessage() + ")" , e);
		}
	}

	private void setDataRequestParameter(SlingHttpServletRequest request, GetHtml5FormParameters.DataParameter dp) {
		switch(dp.getType()) {
		case ByteArray:
			log.info("Setting '" + DATA_ATTRIBUTE_NAME + "' attribute in request.");
			request.setAttribute(DATA_ATTRIBUTE_NAME, dp.getArray());
			break;
		case PathOrUrl:
			PathOrUrl dPathOrUrl = dp.getPathOrUrl();
			log.info("PathOrUrl isUrl='" + dPathOrUrl.isUrl() + "', isPath='" + dPathOrUrl.isPath() + "'.");
			String dataPath = dPathOrUrl.isPath() ? dPathOrUrl.getPath().toUri().toString() : dPathOrUrl.toString();
			log.info("Setting '" + DATA_REF_ATTRIBUTE_NAME + "' attribute in request to '" + dataPath + "'.");
			request.setAttribute(DATA_REF_ATTRIBUTE_NAME, dataPath);
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
	private static class GetHtml5FormParameters {
		private static final String TEMPLATE_PARAM = TEMPLATE_ATTRIBUTE_NAME;
		private static final String DATA_PARAM = DATA_ATTRIBUTE_NAME;
		private static final String CONTENT_ROOT_PARAM = CONTENT_ROOT_ATTRIBUTE_NAME;
		private static final String SUBMIT_URL_PARAM = "submitUrl";
		
		private static final PathOrUrl DEFAULT_CONTENT_ROOT = PathOrUrl.from("/content/dam/formsanddocuments");

		private final TemplateParameter template;
		private final PathOrUrl contentRoot;
		private final Optional<URL> submitUrl;
		private final Optional<DataParameter> data;
		
		public GetHtml5FormParameters(TemplateParameter template, PathOrUrl contentRoot, Optional<URL> submitUrl, Optional<DataParameter> data) {
			super();
			this.template = template;
			this.contentRoot = contentRoot;
			this.submitUrl = submitUrl;
			this.data = data;
		}

		public TemplateParameter getTemplate() {
			return template;
		}

		public PathOrUrl getContentRoot() {
			return contentRoot;
		}

		public Optional<URL> getSubmitUrl() {
			return submitUrl;
		}

		public Optional<DataParameter> getData() {
			return data;
		}

		public static GetHtml5FormParameters readParameter(SlingHttpServletRequest request, boolean validateXml) throws BadRequestException {
			try {
				TemplateParameter template = TemplateParameter.readParameter(getMandatoryParameter(request, TEMPLATE_PARAM));
				PathOrUrl contentRoot = getOptionalParameter(request, CONTENT_ROOT_PARAM)
														.map(RequestParameter::getString)
														.map(PathOrUrl::from)
														.orElse(DEFAULT_CONTENT_ROOT);	// If it wasn't supplied, provide a default.
				Optional<URL> submitUrl = getOptionalParameter(request, SUBMIT_URL_PARAM)
														.map(RequestParameter::getString)
														.map(GetHtml5FormParameters::createSubmitUrl);
				Optional<DataParameter> data = getOptionalParameter(request, DATA_PARAM)
														.map(DataParameter::readParameter);

				return new GetHtml5FormParameters(template, contentRoot, submitUrl, data);
			} catch (IllegalArgumentException e) {
				throw new BadRequestException("There was a problem with one of the incoming parameters.", e);
			}
		}
		
		private static URL createSubmitUrl(String urlString){
			try {
				return new URL(urlString);
			} catch (MalformedURLException e) {
				throw new IllegalArgumentException("Malformed URL in submitUrl", e);
			}
		}
		
		public static class TemplateParameter {
			public enum ParameterType { ByteArray, PathOrUrl };
			
			private final byte[] array;
			private final PathOrUrl pathOrUrl;
			private final ParameterType type;
			
			private TemplateParameter(byte[] array) {
				super();
				this.array = array;
				this.pathOrUrl = null;
				this.type = ParameterType.ByteArray;
			}
			
			private TemplateParameter(PathOrUrl pathOrUrl) {
				super();
				this.array = null;
				this.pathOrUrl = pathOrUrl;
				this.type = ParameterType.PathOrUrl;
			}

			public byte[] getArray() {
				return array;
			}

			public PathOrUrl getPathOrUrl() {
				return pathOrUrl;
			}

			public ParameterType getType() {
				return type;
			}
			
			public static TemplateParameter readParameter(RequestParameter templateParameter) {
				ContentType templateContentType = ContentType.valueOf(templateParameter.getContentType());
				if (templateContentType.isCompatibleWith(ContentType.TEXT_PLAIN)) {
					return new TemplateParameter(PathOrUrl.from(templateParameter.getString()));
				} else if (templateContentType.isCompatibleWith(ContentType.APPLICATION_XDP)) {
					return new TemplateParameter(templateParameter.get());
				} else {
					throw new IllegalArgumentException("Template parmameter has invalid content type. (" + templateContentType.getContentTypeStr() + ").");
				}
			}
		}

		public static class DataParameter {
			public enum ParameterType { ByteArray, PathOrUrl };
			
			private final byte[] array;
			private final PathOrUrl pathOrUrl;
			private final ParameterType type;
			
			private DataParameter(byte[] array) {
				super();
				this.array = array;
				this.pathOrUrl = null;
				this.type = ParameterType.ByteArray;
			}
			
			private DataParameter(PathOrUrl pathOrUrl) {
				super();
				this.array = null;
				this.pathOrUrl = pathOrUrl;
				this.type = ParameterType.PathOrUrl;
			}

			public byte[] getArray() {
				return array;
			}

			public PathOrUrl getPathOrUrl() {
				return pathOrUrl;
			}

			public ParameterType getType() {
				return type;
			}
			
			public static DataParameter readParameter(RequestParameter dataParameter) {
				ContentType dataContentType = ContentType.valueOf(dataParameter.getContentType());
				if (dataContentType.isCompatibleWith(ContentType.TEXT_PLAIN)) {
					return new DataParameter(PathOrUrl.from(dataParameter.getString()));
				} else if (dataContentType.isCompatibleWith(ContentType.APPLICATION_XML)) {
					return new DataParameter(dataParameter.get());
				} else {
					throw new IllegalArgumentException("Data parmameter has invalid content type. (" + dataContentType.getContentTypeStr() + ").");
				}
			}

		}

	}
}
