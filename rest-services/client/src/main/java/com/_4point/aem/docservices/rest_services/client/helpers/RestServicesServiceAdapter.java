package com._4point.aem.docservices.rest_services.client.helpers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;
import java.util.function.Supplier;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status.Family;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.Response.StatusType;

public abstract class RestServicesServiceAdapter {
	private static final String PAGE_COUNT_HEADER = "com._4point.aem.rest_services.page_count";
	protected static final MediaType APPLICATION_PDF = new MediaType("application", "pdf");
	protected static final MediaType APPLICATION_XDP = new MediaType("application", "vnd.adobe.xdp+xml");
	protected static final String CORRELATION_ID_HTTP_HDR = "X-Correlation-ID";
	private static final String SERVICES_URL_PREFIX = "/services";
	protected final WebTarget baseTarget;				// used by subclasses
	protected final Supplier<String> correlationIdFn;
	protected final AemServerType aemServerType;
	//protected static final MediaType APPLICATION_OCTET_STREAM_TYPE=new MediaType("application","octet-stream");
//	protected RestServicesServiceAdapter(WebTarget baseTarget) {
//		super();
//		this.baseTarget = baseTarget;
//		this.correlationIdFn = null;
//	}

	protected RestServicesServiceAdapter(WebTarget baseTarget, Supplier<String> correlationIdFn, AemServerType aemServerType) {
		super();
		this.baseTarget = baseTarget;
		this.correlationIdFn = correlationIdFn;
		this.aemServerType = aemServerType;
	}

	protected Response postToServer(WebTarget localTarget, final FormDataMultiPart multipart, final MediaType acceptType) throws RestServicesServiceException  {
		jakarta.ws.rs.client.Invocation.Builder invokeBuilder = localTarget.request().accept(acceptType);
		if (this.correlationIdFn != null) {
			invokeBuilder.header(CORRELATION_ID_HTTP_HDR, this.correlationIdFn.get());
		}
		Response result;
		try {
			result = invokeBuilder.post(Entity.entity(multipart, multipart.getMediaType()));
		} catch (jakarta.ws.rs.ProcessingException e) {
			String msg = e.getMessage();
			throw new RestServicesServiceException("Error when posting to '" + localTarget.getUri().toString() + "'" + (msg != null ? " (" + msg + ")" : "") + ".", e); 
		}
		return result;
	}
	
	protected static String inputStreamtoString(InputStream inputStream) throws IOException {
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length;
		while ((length = inputStream.read(buffer)) != -1) {
		    result.write(buffer, 0, length);
		}
		return result.toString(StandardCharsets.UTF_8.name());
	}

	protected String constructStandardPath(String serviceName, String methodName) {
		return this.aemServerType.pathPrefix() + SERVICES_URL_PREFIX + "/" + serviceName + "/" + methodName;
	}

	protected <E extends Exception> Document responseToDoc(Response result, MediaType expectedMediaType, Function<String, E> exSupplier) throws IOException, E {
		return responseToDoc(result, expectedMediaType, exSupplier, is->is);
	}

	protected <E extends Exception> Document responseToDoc(Response result, MediaType expectedMediaType, Function<String, E> exSupplier, Function<InputStream, InputStream> filter) throws IOException, E {
		// If the server returns a "NO CONTENT" result, that means that it's intentionally returned an empty document.
		if (result.getStatusInfo().getStatusCode() == Status.NO_CONTENT.getStatusCode()) {
			return SimpleDocumentFactoryImpl.emptyDocument();
		}
		String responseContentType = validateResponse(result, expectedMediaType, exSupplier);

		Document resultDoc = SimpleDocumentFactoryImpl.getFactory().create(filter.apply((InputStream) result.getEntity()));
		String pageCount = result.getHeaderString(PAGE_COUNT_HEADER);
		if (pageCount != null) {
			resultDoc.setPageCount(Long.valueOf(pageCount));
		}
		resultDoc.setContentType(responseContentType);
		return resultDoc;
	}

	protected <E extends Exception> String validateResponse(Response response, MediaType expectedMediaType, Function<String, E> exSupplier) throws IOException, E {
		StatusType resultStatus = response.getStatusInfo();
		if (!Family.SUCCESSFUL.equals(resultStatus.getFamily())) {
			String message = "Call to server failed, statusCode='" + resultStatus.getStatusCode() + "', reason='" + resultStatus.getReasonPhrase() + "'.";
			if (response.hasEntity()) {
				InputStream entityStream = (InputStream) response.getEntity();
				message += "\n" + inputStreamtoString(entityStream);
			}
			throw exSupplier.apply(message);
		}
		if (!response.hasEntity()) {
			throw exSupplier.apply("Call to server succeeded but server failed to return document.  This should never happen.");
		}

		String responseContentType = response.getHeaderString(HttpHeaders.CONTENT_TYPE);
		if ( responseContentType == null || !expectedMediaType.isCompatible(MediaType.valueOf(responseContentType))) {
			String msg = "Response from AEM server was not of expected type(" + expectedMediaType.toString() + ").  " + (responseContentType != null ? "content-type='" + responseContentType + "'" : "content-type was null") + ".";
			InputStream entityStream = (InputStream) response.getEntity();
			msg += "\n" + inputStreamtoString(entityStream);
			throw exSupplier.apply(msg);
		}
		return responseContentType;
	}

	
	@SuppressWarnings("serial")
	protected static class RestServicesServiceException extends Exception {

		private RestServicesServiceException() {
			super();
		}

		private RestServicesServiceException(String arg0, Throwable arg1) {
			super(arg0, arg1);
		}

		private RestServicesServiceException(String arg0) {
			super(arg0);
		}

		private RestServicesServiceException(Throwable arg0) {
			super(arg0);
		}
	}
}
