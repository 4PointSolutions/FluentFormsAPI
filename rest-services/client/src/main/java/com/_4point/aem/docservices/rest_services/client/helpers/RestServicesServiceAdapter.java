package com._4point.aem.docservices.rest_services.client.helpers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

public abstract class RestServicesServiceAdapter {
	protected static final MediaType APPLICATION_PDF = new MediaType("application", "pdf");
	protected static final MediaType APPLICATION_XDP = new MediaType("application", "vnd.adobe.xdp+xml");
	protected static final String CORRELATION_ID_HTTP_HDR = "X-Correlation-ID";

	protected final WebTarget baseTarget;				// used by subclasses
	protected final Supplier<String> correlationIdFn;

	protected RestServicesServiceAdapter(WebTarget baseTarget) {
		super();
		this.baseTarget = baseTarget;
		this.correlationIdFn = null;
	}

	protected RestServicesServiceAdapter(WebTarget baseTarget, Supplier<String> correlationIdFn) {
		super();
		this.baseTarget = baseTarget;
		this.correlationIdFn = correlationIdFn;
	}

	protected Response postToServer(WebTarget localTarget, final FormDataMultiPart multipart, final MediaType acceptType) throws RestServicesServiceException  {
		javax.ws.rs.client.Invocation.Builder invokeBuilder = localTarget.request().accept(acceptType);
		if (this.correlationIdFn != null) {
			invokeBuilder.header(CORRELATION_ID_HTTP_HDR, this.correlationIdFn.get());
		}
		Response result;
		try {
		multipart.getFields().forEach((k, v) ->
		 {
			 System.out.println("Key: "+k);

		 });;
			
			result = invokeBuilder.post(Entity.entity(multipart, multipart.getMediaType()));
		} catch (javax.ws.rs.ProcessingException e) {
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
