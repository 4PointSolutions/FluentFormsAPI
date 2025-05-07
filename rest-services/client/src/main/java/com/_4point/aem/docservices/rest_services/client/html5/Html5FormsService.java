package com._4point.aem.docservices.rest_services.client.html5;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import com._4point.aem.docservices.rest_services.client.RestClient;
import com._4point.aem.docservices.rest_services.client.RestClient.ContentType;
import com._4point.aem.docservices.rest_services.client.RestClient.MultipartPayload;
import com._4point.aem.docservices.rest_services.client.RestClient.RestClientException;
import com._4point.aem.docservices.rest_services.client.helpers.AemServerType;
import com._4point.aem.docservices.rest_services.client.helpers.Builder;
import com._4point.aem.docservices.rest_services.client.helpers.Builder.RestClientFactory;
import com._4point.aem.docservices.rest_services.client.helpers.BuilderImpl;
import com._4point.aem.docservices.rest_services.client.helpers.RestServicesServiceAdapter;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;

public class Html5FormsService extends RestServicesServiceAdapter {

	private static final String HTML5_SERVICE_NAME = "Html5";
	private static final String RENDER_HTML5_METHOD_NAME = "RenderHtml5Form";
	private static final String TEMPLATE_PARAM = "template";
	private static final String DATA_PARAM = "data";

	private final RestClient renderHtml5FormRestClient;
	private final Function<InputStream, InputStream> responseFilter; 

	private Html5FormsService(BuilderImpl builder, Supplier<String> correlationIdFn, Function<InputStream, InputStream> responseFilter) {
		super(correlationIdFn);
		this.renderHtml5FormRestClient = builder.createClient(HTML5_SERVICE_NAME, RENDER_HTML5_METHOD_NAME);
		this.responseFilter = responseFilter != null ? responseFilter : Function.identity();
	}
	
	public Document renderHtml5Form(PathOrUrl template) throws Html5FormsServiceException {
		return renderHtml5Form(template, SimpleDocumentFactoryImpl.emptyDocument());
	}

	public Document renderHtml5Form(String template) throws Html5FormsServiceException {
		return renderHtml5Form(PathOrUrl.from(template), SimpleDocumentFactoryImpl.emptyDocument());
	}
	
	public Document renderHtml5Form(Path template) throws Html5FormsServiceException {
		return renderHtml5Form(PathOrUrl.from(template), SimpleDocumentFactoryImpl.emptyDocument());
	}
	
	public Document renderHtml5Form(URL template) throws Html5FormsServiceException {
		return renderHtml5Form(PathOrUrl.from(template), SimpleDocumentFactoryImpl.emptyDocument());
	}

	@SuppressWarnings("resource")
	public Document renderHtml5Form(PathOrUrl template, Document data) throws Html5FormsServiceException {
		Objects.requireNonNull(template, "Template parameter cannot be null.");
		Objects.requireNonNull(data, "Data parameter cannot be null.");

		
		try(MultipartPayload payload = renderHtml5FormRestClient.multipartPayloadBuilder()
																.addStringVersion(TEMPLATE_PARAM, template)
																.addIfNotNull(DATA_PARAM, data.isEmpty() ? null : data.getInputStream(), ContentType.APPLICATION_XML)
																.build()) {
			
			return payload.postToServer(ContentType.TEXT_HTML)
					  .map(r->responseToDoc(r, responseFilter))
					  .orElseThrow(()->new Html5FormsServiceException("Error - empty response from AEM server."));
		} catch (RestClientException e) {
			throw new Html5FormsServiceException("Error while POSTing to AEM server.", e);
		} catch (IOException e) {
			String msg = e.getMessage();
			throw new Html5FormsServiceException("I/O Error while rendering Html5 Form. (" + renderHtml5FormRestClient.target() + ") (" + (msg == null ? e.getClass().getName() : msg) + ").", e);
		}
	}

	public Document renderHtml5Form(String template, Document data) throws Html5FormsServiceException {
		return renderHtml5Form(PathOrUrl.from(template), data);
	}
	
	public Document renderHtml5Form(Path template, Document data) throws Html5FormsServiceException {
		return renderHtml5Form(PathOrUrl.from(template), data);
	}
	
	public Document renderHtml5Form(URL template, Document data) throws Html5FormsServiceException {
		return renderHtml5Form(PathOrUrl.from(template), data);
	}


	// The following methods have not been implemented yet for various reasons:
	//  - The Adobe dataRef attribute is failing on the server
	//  - Adobe doesn't support passing the value in directly, so we need to write additional code to make this variation work.
	// They remain here "aspirationally" (i.e. I would like to see them implemented at some point in the future).
//	public Document renderHtml5Form(PathOrUrl template, PathOrUrl data) throws Html5FormsServiceException {
//		throw new UnsupportedOperationException("This method has not been implemented yet.");
//	}
//
//	public Document renderHtml5Form(Document template) throws Html5FormsServiceException {
//		throw new UnsupportedOperationException("This method has not been implemented yet.");
//	}
//
//	public Document renderHtml5Form(Document template, Document data) throws Html5FormsServiceException {
//		throw new UnsupportedOperationException("This method has not been implemented yet.");
//	}
//
//	public Document renderHtml5Form(Document template, PathOrUrl data) throws Html5FormsServiceException {
//		throw new UnsupportedOperationException("This method has not been implemented yet.");
//	}

	/**
	 * Creates a Builder object for building a Html5FormsServiceBuilder object.
	 * 
	 * @return builder object
	 */
	public static Html5FormsServiceBuilder builder(RestClientFactory clientFactory) {
		return new Html5FormsServiceBuilder(clientFactory);
	}
	
	public static class Html5FormsServiceBuilder implements Builder {
		private final BuilderImpl builder;
		private Function<InputStream, InputStream> renderResultFilter; 

		// Prevent it from being instantiated outside of this class
		private Html5FormsServiceBuilder(RestClientFactory clientFactory) {
			this.builder = new BuilderImpl(clientFactory);
		}

		@Override
		public Html5FormsServiceBuilder machineName(String machineName) {
			builder.machineName(machineName);
			return this;
		}

		@Override
		public Html5FormsServiceBuilder port(int port) {
			builder.port(port);
			return this;
		}

		@Override
		public Html5FormsServiceBuilder useSsl(boolean useSsl) {
			builder.useSsl(useSsl);
			return this;
		}

		@Override
		public Html5FormsServiceBuilder basicAuthentication(String username, String password) {
			builder.basicAuthentication(username, password);
			return this;
		}

		@Override
		public Html5FormsServiceBuilder correlationId(Supplier<String> correlationIdFn) {
			builder.correlationId(correlationIdFn);
			return this;
		}

		@Override
		public Supplier<String> getCorrelationIdFn() {
			return builder.getCorrelationIdFn();
		}

		public Html5FormsServiceBuilder addRenderResultFilter(Function<InputStream, InputStream> filter) {
			this.renderResultFilter = this.renderResultFilter != null 
															? this.renderResultFilter.andThen(filter)
															: filter;  
			return this;
		}
		
		public Function<InputStream, InputStream> getRenderResultFilter() {
			return renderResultFilter;
		}

		@Override
		public Html5FormsServiceBuilder aemServerType(AemServerType serverType) {
			builder.aemServerType(serverType);
			return this;
		}
		
		@Override
		public AemServerType getAemServerType() {
			return builder.getAemServerType();
		}

		public Html5FormsService build() {
			return new Html5FormsService(this.builder, this.getCorrelationIdFn(), this.getRenderResultFilter());
		}
		
	}
	
	@SuppressWarnings("serial")
	public static class Html5FormsServiceException extends Exception {

		public Html5FormsServiceException() {
			super();
		}

		public Html5FormsServiceException(String message, Throwable cause) {
			super(message, cause);
		}

		public Html5FormsServiceException(String message) {
			super(message);
		}

		public Html5FormsServiceException(Throwable cause) {
			super(cause);
		}
	}
}
