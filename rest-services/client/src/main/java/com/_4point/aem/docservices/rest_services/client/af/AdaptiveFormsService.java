package com._4point.aem.docservices.rest_services.client.af;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.core.Response.Status.Family;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import com._4point.aem.docservices.rest_services.client.helpers.Builder;
import com._4point.aem.docservices.rest_services.client.helpers.BuilderImpl;
import com._4point.aem.docservices.rest_services.client.helpers.RestServicesServiceAdapter;
import com._4point.aem.docservices.rest_services.client.html5.Html5FormsService.Html5FormsServiceException;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;

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

	public Document renderAdaptiveForm(PathOrUrl template) throws AdaptiveFormsServiceException {
		return renderAdaptiveForm(template, SimpleDocumentFactoryImpl.emptyDocument());
	}

	public Document renderAdaptiveForm(String template) throws AdaptiveFormsServiceException {
		return renderAdaptiveForm(PathOrUrl.from(template), SimpleDocumentFactoryImpl.emptyDocument());
	}
	
	public Document renderAdaptiveForm(Path template) throws AdaptiveFormsServiceException {
		return renderAdaptiveForm(PathOrUrl.from(template), SimpleDocumentFactoryImpl.emptyDocument());
	}
	
	public Document renderAdaptiveForm(URL template) throws AdaptiveFormsServiceException {
		return renderAdaptiveForm(PathOrUrl.from(template), SimpleDocumentFactoryImpl.emptyDocument());
	}
	
	public Document renderAdaptiveForm(PathOrUrl template, Document data) throws AdaptiveFormsServiceException {
		Objects.requireNonNull(template, "Template parameter cannot be null.");
		Objects.requireNonNull(data, "Data parameter cannot be null.");
		
		// TODO: Add the guts.
		WebTarget renderAdaptiveFormTarget = baseTarget.path(RENDER_ADAPTIVE_FORM_PATH);
		
		WebTarget target = renderAdaptiveFormTarget.queryParam(TEMPLATE_PARAM, template.toString());
		
		if (!data.isEmpty()) {
			try {
				target.queryParam(DATA_PARAM, postDataToDataCacheService(data));
			} catch (IOException e) {
				String msg = e.getMessage();
				throw new InternalServerErrorException("Error while posting data to dataCache (" + (msg == null ? e.getClass().getName() : msg) + ").", e );
			}
		}

		Response result = target.request()
								.accept(MediaType.TEXT_HTML_TYPE)
								.get();

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

			Document resultDoc = SimpleDocumentFactoryImpl.getFactory().create(this.responseFilter.apply((InputStream) result.getEntity()));
			resultDoc.setContentType(responseContentType);
			return resultDoc;
			
		} catch (IOException e) {
			String msg = e.getMessage();
			throw new InternalServerErrorException("Error while reading Adaptive Form (" + (msg == null ? e.getClass().getName() : msg) + ").", e );
		}
	}

	private String postDataToDataCacheService(Document data) throws IOException { 
		String dataKey = null;
		WebTarget dataCacheServiceTarget = baseTarget.path(RENDER_ADAPTIVE_FORM_PATH);

		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			multipart.field(DATA_PARAM, data.getInputStream(), MediaType.APPLICATION_XML_TYPE);
	
			Response result = dataCacheServiceTarget
					.request()
					.accept(MediaType.TEXT_PLAIN_TYPE)
					.post(Entity.entity(multipart, multipart.getMediaType()));
			
			dataKey = (new BufferedReader(new InputStreamReader(((InputStream)result.getEntity())))).readLine();
		}
		return dataKey;
	}
	
	public Document renderAdaptiveForm(String template, Document data) throws AdaptiveFormsServiceException {
		return renderAdaptiveForm(PathOrUrl.from(template), data);
	}
	
	public Document renderAdaptiveForm(Path template, Document data) throws AdaptiveFormsServiceException {
		return renderAdaptiveForm(PathOrUrl.from(template), data);
	}
	
	public Document renderAdaptiveForm(URL template, Document data) throws AdaptiveFormsServiceException {
		return renderAdaptiveForm(PathOrUrl.from(template), data);
	}
	
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
