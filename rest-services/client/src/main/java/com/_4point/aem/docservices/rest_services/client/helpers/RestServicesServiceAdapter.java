package com._4point.aem.docservices.rest_services.client.helpers;

import java.util.function.Supplier;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;

public abstract class RestServicesServiceAdapter {
	protected static final MediaType APPLICATION_PDF = new MediaType("application", "pdf");
	protected static final String CORRELATION_ID_HTTP_HDR = "X-Correlation-ID";

	protected final WebTarget baseTarget;
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

	protected Response postToServer(WebTarget localTarget, final FormDataMultiPart multipart, final MediaType acceptType) {
		javax.ws.rs.client.Invocation.Builder invokeBuilder = localTarget.request().accept(acceptType);
		if (this.correlationIdFn != null) {
			invokeBuilder.header(CORRELATION_ID_HTTP_HDR, this.correlationIdFn.get());
		}
		Response result = invokeBuilder.post(Entity.entity(multipart, multipart.getMediaType()));
		return result;
	}
	

}
