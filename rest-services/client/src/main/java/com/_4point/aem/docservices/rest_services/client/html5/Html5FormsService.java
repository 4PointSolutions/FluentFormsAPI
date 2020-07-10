package com._4point.aem.docservices.rest_services.client.html5;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Supplier;

import javax.ws.rs.client.Client;
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

public class Html5FormsService extends RestServicesServiceAdapter {

	private static final String RENDER_HTML5_PATH = "/services/Html5/RenderHtml5Form";
	private static final String TEMPLATE_PARAM = "template";
	private static final String DATA_PARAM = "data";

	protected Html5FormsService(WebTarget baseTarget) {
		super(baseTarget);
	}

	protected Html5FormsService(WebTarget baseTarget, Supplier<String> correlationIdFn) {
		super(baseTarget, correlationIdFn);
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

	public Document renderHtml5Form(PathOrUrl template, Document data) throws Html5FormsServiceException {
		Objects.requireNonNull(template, "Template parameter cannot be null.");
		Objects.requireNonNull(data, "Data parameter cannot be null.");
		
		WebTarget renderHtml5Target = baseTarget.path(RENDER_HTML5_PATH);
		
		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			multipart.field(TEMPLATE_PARAM, template.toString());
			
			if (!data.isEmpty()) {
				multipart.field(DATA_PARAM, data.getInputStream(), MediaType.APPLICATION_XML_TYPE);
			}
					 

			Response result = postToServer(renderHtml5Target, multipart, MediaType.TEXT_HTML_TYPE);
			
			StatusType resultStatus = result.getStatusInfo();
			if (!Family.SUCCESSFUL.equals(resultStatus.getFamily())) {
				String message = "Call to server failed, statusCode='" + resultStatus.getStatusCode() + "', reason='" + resultStatus.getReasonPhrase() + "'.";
				if (result.hasEntity()) {
					InputStream entityStream = (InputStream) result.getEntity();
					message += "\n" + inputStreamtoString(entityStream);
				}
				throw new Html5FormsServiceException(message);
			}
			
			if (!result.hasEntity()) {
				throw new Html5FormsServiceException("Call to server succeeded but server failed to return document.  This should never happen.");
			}

			String responseContentType = result.getHeaderString(HttpHeaders.CONTENT_TYPE);
			if ( responseContentType == null || !MediaType.TEXT_HTML_TYPE.isCompatible(MediaType.valueOf(responseContentType))) {
				String msg = "Response from AEM server was not HTML.  " + (responseContentType != null ? "content-type='" + responseContentType + "'" : "content-type was null") + ".";
				InputStream entityStream = (InputStream) result.getEntity();
				msg += "\n" + inputStreamtoString(entityStream);
				throw new Html5FormsServiceException(msg);
			}

			Document resultDoc = SimpleDocumentFactoryImpl.getFactory().create((InputStream) result.getEntity());
			resultDoc.setContentType(responseContentType);
			return resultDoc;
			
		} catch (IOException e) {
			String msg = e.getMessage();
			throw new Html5FormsServiceException("I/O Error while rendering Html5 Form. (" + baseTarget.getUri().toString() + ") (" + (msg == null ? e.getClass().getName() : msg) + ").", e);
		} catch (RestServicesServiceException e) {
			throw new Html5FormsServiceException("Error while POSTing to AEM server.", e);
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
	public static Html5FormsServiceBuilder builder() {
		return new Html5FormsServiceBuilder();
	}
	
	public static class Html5FormsServiceBuilder implements Builder {
		private BuilderImpl builder = new BuilderImpl();
		
		private Html5FormsServiceBuilder() {
			super();
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
		public Html5FormsServiceBuilder clientFactory(Supplier<Client> clientFactory) {
			builder.clientFactory(clientFactory);
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

		@Override
		public WebTarget createLocalTarget() {
			return builder.createLocalTarget();
		}
		
		public Html5FormsService build() {
			return new Html5FormsService(this.createLocalTarget(), this.getCorrelationIdFn());
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
