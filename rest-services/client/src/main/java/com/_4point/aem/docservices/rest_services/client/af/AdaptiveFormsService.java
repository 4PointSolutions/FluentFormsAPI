package com._4point.aem.docservices.rest_services.client.af;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import com._4point.aem.docservices.rest_services.client.RestClient;
import com._4point.aem.docservices.rest_services.client.RestClient.ContentType;
import com._4point.aem.docservices.rest_services.client.RestClient.GetRequest;
import com._4point.aem.docservices.rest_services.client.RestClient.MultipartPayload;
import com._4point.aem.docservices.rest_services.client.RestClient.Response;
import com._4point.aem.docservices.rest_services.client.RestClient.RestClientException;
import com._4point.aem.docservices.rest_services.client.helpers.AemServerType;
import com._4point.aem.docservices.rest_services.client.helpers.Builder;
import com._4point.aem.docservices.rest_services.client.helpers.Builder.RestClientFactory;
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
	private static final String AF_FORMS_PATH = "/content/forms/af";
	// private static final String RENDER_ADAPTIVE_FORM_PATH = "/services/AdaptiveForms/RenderAdaptiveForm";  // Not currently being used.
	// private static final String TEMPLATE_PARAM = "template";
	private static final String DATA_CACHE_SERVICE_NAME = "DataServices";
	private static final String DATA_CACHE_METHOD_NAME = "DataCache";
	private static final String DATA_REF_PARAM = "dataRef";
	private static final String DATA_SERVICE_DATA_PARAM = "Data";

	private final Function<InputStream, InputStream> responseFilter; 
	private final RestClient dataCacheRestClient;
	private final RestClient afRestClient;

	AdaptiveFormsService(BuilderImpl builder, Supplier<String> correlationIdFn, Function<InputStream, InputStream> responseFilter) {
        super(correlationIdFn);
		this.dataCacheRestClient = builder.createClient(DATA_CACHE_SERVICE_NAME, DATA_CACHE_METHOD_NAME);
		this.afRestClient = builder.createClient(AF_FORMS_PATH);
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
		if (!template.isRelative()) {
			throw new AdaptiveFormsServiceException("Only relative paths are supported");
		}
		
		String afPath = constructAfPath(template.convertRelativePathToRelativeUrl());
		GetRequest.Builder builder = afRestClient.getRequestBuilder(afPath);
		builder = builder.queryParam("wcmmode", "disabled");
		
		if (!data.isEmpty()) {
			builder = builder.queryParam(DATA_REF_PARAM, generateDataRefParam(postDataToDataCacheService(data)));
		}

		GetRequest getRequest = builder.build();
		
		try {
		return getRequest.getFromServer(ContentType.TEXT_HTML)
						 .map(responseFilter == null ? d->responseToDoc(d) : d->responseToDoc(d, responseFilter))
						 .orElseThrow();
		} catch (RestClientException e) {
            throw new AdaptiveFormsServiceException("Error while performing GET to server (" + afRestClient.target() + ").", e);
		}
	}

	private String constructAfPath(String formName) {
		return "/" + formName + ".html";
	}
	
	private String generateDataRefParam(String uuid) {
		return "service://FFPrefillService/" + uuid;
	}
	
	private String postDataToDataCacheService(Document data) throws AdaptiveFormsServiceException {
        try (MultipartPayload payload = dataCacheRestClient.multipartPayloadBuilder()
        												   .add(DATA_SERVICE_DATA_PARAM, data, getContentType(data))
        												   .build()) {
			return payload.postToServer(ContentType.TEXT_PLAIN)
						  .map(uncheck(AdaptiveFormsService::responseToString))
						  .orElseThrow();

        } catch (IOException e) {
            throw new AdaptiveFormsServiceException("I/O Error while generating Adaptive Form. (" + dataCacheRestClient.target() + ").", e);
        } catch (RestClientException e) {
            throw new AdaptiveFormsServiceException("Error while POSTing to server (" + dataCacheRestClient.target() + ").", e);
		}
	}
	
	private static ContentType getContentType(Document data) {
		try {
			String contentType = data.getContentType();
			return contentType != null ? ContentType.of(contentType) : ContentType.APPLICATION_XML;
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private static String responseToString(Response result) throws IOException {
		return new String(result.data().readAllBytes());
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
	public static AdaptiveFormsServiceBuilder builder(RestClientFactory clientFactory) {
		return new AdaptiveFormsServiceBuilder(clientFactory);
	}
	
	public static class AdaptiveFormsServiceBuilder implements Builder {
		private final BuilderImpl builder;
		private Function<InputStream, InputStream> renderResultFilter; 
		
		// Prevent it from being instantiated outside of this class
		private AdaptiveFormsServiceBuilder(RestClientFactory clientFactory) {
			this.builder = new BuilderImpl(clientFactory);
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

		public AdaptiveFormsServiceBuilder addRenderResultFilter(Function<InputStream, InputStream> filter) {
			this.renderResultFilter = this.renderResultFilter != null 
															? this.renderResultFilter.andThen(filter)
															: filter;  
			return this;
		}
		
		public AdaptiveFormsServiceBuilder addRenderResultFilters(List<Function<InputStream, InputStream>> filters) {
			filters.forEach(this::addRenderResultFilter);
			return this;
		}
		
		public Function<InputStream, InputStream> getRenderResultFilter() {
			return renderResultFilter;
		}

		@Override
		public AdaptiveFormsServiceBuilder aemServerType(AemServerType serverType) {
			builder.aemServerType(serverType);
			return this;
		}
		
		@Override
		public AemServerType getAemServerType() {
			return builder.getAemServerType();
		}

		public AdaptiveFormsService build() {
			return new AdaptiveFormsService(this.builder, this.getCorrelationIdFn(), this.getRenderResultFilter());
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
