package com._4point.aem.docservices.rest_services.client.helpers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;

public abstract class RestServicesServiceAdapter {
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
		javax.ws.rs.client.Invocation.Builder invokeBuilder = localTarget.request().accept(acceptType);
		if (this.correlationIdFn != null) {
			invokeBuilder.header(CORRELATION_ID_HTTP_HDR, this.correlationIdFn.get());
		}
		Response result;
		try {
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

	protected String constructStandardPath(String serviceName, String methodName) {
		return this.aemServerType.pathPrefix() + SERVICES_URL_PREFIX + "/" + serviceName + "/" + methodName;
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
