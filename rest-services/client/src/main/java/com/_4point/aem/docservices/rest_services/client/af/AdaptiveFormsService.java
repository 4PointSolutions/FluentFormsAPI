package com._4point.aem.docservices.rest_services.client.af;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.Response.StatusType;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import com._4point.aem.docservices.rest_services.client.helpers.Builder;
import com._4point.aem.docservices.rest_services.client.helpers.BuilderImpl;
import com._4point.aem.docservices.rest_services.client.helpers.RestServicesServiceAdapter;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;

/**
 * Adaptive Forms Service for rendering Adaptive Forms.  The forms can be pre-populated with data by passing in an XML data file.
 *
 */
public class AdaptiveFormsService extends RestServicesServiceAdapter {
	private static final String RENDER_ADAPTIVE_FORM_PATH = "/services/AdaptiveForms/RenderAdaptiveForm";
	private static final String TEMPLATE_PARAM = "template";
	private static final String DATA_PARAM = "data";

	private final Function<InputStream, InputStream> responseFilter; 

	public AdaptiveFormsService(WebTarget baseTarget, Supplier<String> correlationIdFn, Function<InputStream, InputStream> responseFilter) {
		super(baseTarget, correlationIdFn);
		this.responseFilter = responseFilter;
	}

	public AdaptiveFormsService(WebTarget baseTarget, Function<InputStream, InputStream> responseFilter) {
		super(baseTarget);
		this.responseFilter = responseFilter;
	}

	/**
	 * See renderAdaptiveForm(PathOrUrl template, Document data), except no data is passed in.
	 * 
	 * @param template
	 * @return
	 * @throws AdaptiveFormsServiceException
	 */
	public Document renderAdaptiveForm(PathOrUrl template) throws AdaptiveFormsServiceException {
		return renderAdaptiveForm(template, SimpleDocumentFactoryImpl.emptyDocument());
	}

	/**
	 * See renderAdaptiveForm(PathOrUrl template, Document data), except no data is passed in.
	 * 
	 * @param template
	 * @return
	 * @throws AdaptiveFormsServiceException
	 */
	public Document renderAdaptiveForm(String template) throws AdaptiveFormsServiceException {
		return renderAdaptiveForm(PathOrUrl.from(template), SimpleDocumentFactoryImpl.emptyDocument());
	}
	
	/**
	 * See renderAdaptiveForm(PathOrUrl template, Document data), except no data is passed in.
	 * 
	 * @param template
	 * @return
	 * @throws AdaptiveFormsServiceException
	 */
	public Document renderAdaptiveForm(Path template) throws AdaptiveFormsServiceException {
		return renderAdaptiveForm(PathOrUrl.from(template), SimpleDocumentFactoryImpl.emptyDocument());
	}
	
	/**
	 * This routine posts makes an HTTP GET to the Adaptive Forms Service URL to retrieve the Adaptive form.  If a non-empty data 
	 * Document is passed in, then it makes an HTTP POST to a DataCache service first, gets back a data key and then passes that
	 * data key as a query parameter to the Adaptive Forms Service.
	 * 
	 * This approach does *not* work well with a load balancer in front of multiple AEM server (because the POST and GET have to
	 * go to the same server), but it's good enough for now.  The current approach could work if sticky sessions are enabled.  
	 * 
	 * @param template
	 * @param data
	 * @return
	 * @throws AdaptiveFormsServiceException
	 */
	public Document renderAdaptiveForm(PathOrUrl template, Document data) throws AdaptiveFormsServiceException {
		Objects.requireNonNull(template, "Template parameter cannot be null.");
		Objects.requireNonNull(data, "Data parameter cannot be null.");
		if (!isRelative(template)) {
			throw new AdaptiveFormsServiceException("Only relative paths are supported");
		}
		
		WebTarget renderAdaptiveFormTarget = baseTarget.path(RENDER_ADAPTIVE_FORM_PATH);
		
		WebTarget target = renderAdaptiveFormTarget.queryParam(TEMPLATE_PARAM, template.toString());
		
		if (!data.isEmpty()) {
			target.queryParam(DATA_PARAM, postDataToDataCacheService(data));
		}

		Invocation.Builder invokeBuilder = target.request()
								.accept(MediaType.TEXT_HTML_TYPE);
		
		if (this.correlationIdFn != null) {
			invokeBuilder.header(CORRELATION_ID_HTTP_HDR, this.correlationIdFn.get());
		}

		Response result = invokeBuilder.get();

		try {
			StatusType resultStatus = result.getStatusInfo();
			if (!Family.SUCCESSFUL.equals(resultStatus.getFamily())) {
				String message = "Call to server failed, statusCode='" + resultStatus.getStatusCode() + "', reason='" + resultStatus.getReasonPhrase() + "'.";
				if (result.hasEntity()) {
					InputStream entityStream = (InputStream) result.getEntity();
					message += "\n" + inputStreamtoString(entityStream);
				}
				throw new AdaptiveFormsServiceException(message);
			}
			
			if (!result.hasEntity()) {
				throw new AdaptiveFormsServiceException("Call to server succeeded but server failed to return document.  This should never happen.");
			}

			String responseContentType = result.getHeaderString(HttpHeaders.CONTENT_TYPE);
			if ( responseContentType == null || !MediaType.TEXT_HTML_TYPE.isCompatible(MediaType.valueOf(responseContentType))) {
				String msg = "Response from AEM server was not HTML.  " + (responseContentType != null ? "content-type='" + responseContentType + "'" : "content-type was null") + ".";
				InputStream entityStream = (InputStream) result.getEntity();
				msg += "\n" + inputStreamtoString(entityStream);
				throw new AdaptiveFormsServiceException(msg);
			}

			InputStream entityInputStream = this.responseFilter != null ? this.responseFilter.apply((InputStream) result.getEntity()) : (InputStream) result.getEntity();
			Document resultDoc = SimpleDocumentFactoryImpl.getFactory().create(entityInputStream);
			resultDoc.setContentType(responseContentType);
			return resultDoc;
			
		} catch (IOException e) {
			String msg = e.getMessage();
			throw new AdaptiveFormsServiceException("Error while reading Adaptive Form (" + (msg == null ? e.getClass().getName() : msg) + ").", e );
		}
	}

	private static boolean isRelative(PathOrUrl pathOrUrl) {
		if (!pathOrUrl.isPath()) { 	// If it's an URL or a CRX: path, then it's absolute
			return false; 
		}
		Path path = pathOrUrl.getPath();
		if (path.isAbsolute()) { 	// If Path thinks it's absolute, then it is
			return false;
		}
		Path root = path.getRoot();
		if (root != null && root.toString().equals("\\")) {	// If we're on windows and it starts with \, then Path doesn't consider it to be absolute, but we do.
			return false;
		}
		return true;
	}
	
	private String postDataToDataCacheService(Document data) throws AdaptiveFormsServiceException { 
		WebTarget dataCacheServiceTarget = baseTarget.path(RENDER_ADAPTIVE_FORM_PATH);

		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			multipart.field(DATA_PARAM, data.getInputStream(), MediaType.APPLICATION_XML_TYPE);
	
			Response result = postToServer(dataCacheServiceTarget, multipart, MediaType.TEXT_PLAIN_TYPE);
			
			StatusType resultStatus = result.getStatusInfo();
			if (!Family.SUCCESSFUL.equals(resultStatus.getFamily())) {
				String message = "Call to server failed while posting data to dataCache, statusCode='" + resultStatus.getStatusCode() + "', reason='" + resultStatus.getReasonPhrase() + "'.";
				if (result.hasEntity()) {
					InputStream entityStream = (InputStream) result.getEntity();
					message += "\n" + inputStreamtoString(entityStream);
				}
				throw new AdaptiveFormsServiceException(message);
			}
			
			if (!result.hasEntity()) {
				throw new AdaptiveFormsServiceException("Call to dataCache on the server succeeded but server failed to return document with dataKey in it.  This should never happen.");
			}

			String responseContentType = result.getHeaderString(HttpHeaders.CONTENT_TYPE);
			if ( responseContentType == null || !MediaType.TEXT_PLAIN_TYPE.isCompatible(MediaType.valueOf(responseContentType))) {
				String msg = "DataCache response from AEM server was not plain text.  " + (responseContentType != null ? "content-type='" + responseContentType + "'" : "content-type was null") + ".";
				InputStream entityStream = (InputStream) result.getEntity();
				msg += "\n" + inputStreamtoString(entityStream);
				throw new AdaptiveFormsServiceException(msg);
			}

			return (new BufferedReader(new InputStreamReader(((InputStream)result.getEntity())))).readLine();
			
		} catch (IOException | RestServicesServiceException e) {
			String msg = e.getMessage();
			throw new AdaptiveFormsServiceException("Error while posting data to dataCache (" + (msg == null ? e.getClass().getName() : msg) + ").", e );
		}
	}
	
	/**
	 * See renderAdaptiveForm(PathOrUrl template, Document data).
	 * 
	 * @param template
	 * @param data
	 * @return
	 * @throws AdaptiveFormsServiceException
	 */
	public Document renderAdaptiveForm(String template, Document data) throws AdaptiveFormsServiceException {
		return renderAdaptiveForm(PathOrUrl.from(template), data);
	}
	
	/**
	 * See renderAdaptiveForm(PathOrUrl template, Document data).
	 * 
	 * @param template
	 * @param data
	 * @return
	 * @throws AdaptiveFormsServiceException
	 */
	public Document renderAdaptiveForm(Path template, Document data) throws AdaptiveFormsServiceException {
		return renderAdaptiveForm(PathOrUrl.from(template), data);
	}
	
	/**
	 * Retrieves a fluent Builder object for building an AdaptiveFormsService object.
	 * 
	 * @return
	 */
	public static AdaptiveFormsServiceBuilder builder() {
		return new AdaptiveFormsServiceBuilder();
	}
	
	public static class AdaptiveFormsServiceBuilder implements Builder {
		private final BuilderImpl builder = new BuilderImpl();
		private Function<InputStream, InputStream> renderResultFilter; 
		
		// Prevent it from being instantiated outside of this class
		private AdaptiveFormsServiceBuilder() {
			super();
		}

		@Override
		public AdaptiveFormsServiceBuilder machineName(String machineName) {
			builder.machineName(machineName);
			return this;
		}

		@Override
		public AdaptiveFormsServiceBuilder port(int port) {
			builder.port(port);
			return this;
		}

		@Override
		public AdaptiveFormsServiceBuilder useSsl(boolean useSsl) {
			builder.useSsl(useSsl);
			return this;
		}

		@Override
		public AdaptiveFormsServiceBuilder clientFactory(Supplier<Client> clientFactory) {
			builder.clientFactory(clientFactory);
			return this;
		}

		@Override
		public AdaptiveFormsServiceBuilder basicAuthentication(String username, String password) {
			builder.basicAuthentication(username, password);
			return this;
		}

		@Override
		public AdaptiveFormsServiceBuilder correlationId(Supplier<String> correlationIdFn) {
			builder.correlationId(correlationIdFn);
			return this;
		}

		@Override
		public Supplier<String> getCorrelationIdFn() {
			return builder.getCorrelationIdFn();
		}

		@Override
		public WebTarget createLocalTarget() {
			return builder.createLocalTarget();
		}

		public AdaptiveFormsServiceBuilder addRenderResultFilter(Function<InputStream, InputStream> filter) {
			this.renderResultFilter = this.renderResultFilter != null 
															? this.renderResultFilter.andThen(filter)
															: filter;  
			return this;
		}
		
		public Function<InputStream, InputStream> getRenderResultFilter() {
			return renderResultFilter;
		}

		public AdaptiveFormsService build() {
			return new AdaptiveFormsService(this.createLocalTarget(), this.getCorrelationIdFn(), this.getRenderResultFilter());
		}
	}

	@SuppressWarnings("serial")
	public static class AdaptiveFormsServiceException extends Exception {

		public AdaptiveFormsServiceException() {
			super();
		}

		public AdaptiveFormsServiceException(String message, Throwable cause) {
			super(message, cause);
		}

		public AdaptiveFormsServiceException(String message) {
			super(message);
		}

		public AdaptiveFormsServiceException(Throwable cause) {
			super(cause);
		}
	}
}
