package com._4point.aem.docservices.rest_services.client.html5;

import java.io.IOException;
import java.io.InputStream;
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
		return renderHtml5Form(template, /* SimpleDocumentFactoryImpl.emptyDocument() */ null);
	}

	public Document renderHtml5Form(PathOrUrl template, Document data) throws Html5FormsServiceException {
		
		WebTarget importDataTarget = baseTarget.path(RENDER_HTML5_PATH);
		
		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			multipart.field(TEMPLATE_PARAM, template.toString());
			
			if (/* !data.isEmpty() */ data != null) {
				multipart.field(DATA_PARAM, data.getInputStream(), MediaType.valueOf(data.getContentType()));
			}
					 

			Response result = postToServer(importDataTarget, multipart, APPLICATION_PDF);
			
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
			if ( responseContentType == null || !APPLICATION_PDF.isCompatible(MediaType.valueOf(responseContentType))) {
				String msg = "Response from AEM server was not a PDF.  " + (responseContentType != null ? "content-type='" + responseContentType + "'" : "content-type was null") + ".";
				InputStream entityStream = (InputStream) result.getEntity();
				msg += "\n" + inputStreamtoString(entityStream);
				throw new Html5FormsServiceException(msg);
			}

			Document resultDoc = SimpleDocumentFactoryImpl.getFactory().create((InputStream) result.getEntity());
			resultDoc.setContentType(APPLICATION_PDF.toString());
			return resultDoc;
			
		} catch (IOException e) {
			throw new Html5FormsServiceException("I/O Error while importing data. (" + baseTarget.getUri().toString() + ").", e);
		} catch (RestServicesServiceException e) {
			throw new Html5FormsServiceException("Error while POSTing to server", e);
		}
	}

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
