package com._4point.aem.docservices.rest_services.client.helpers;

import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import com._4point.aem.fluentforms.api.Transformable;

/**
 * This class is to add a transform() method on the Multipart object so that we can make
 * the "set if not null" operations more succinct. 
 *
 */
public class MultipartTransformer implements Transformable<MultipartTransformer> {

	private final FormDataMultiPart m;

	private MultipartTransformer(FormDataMultiPart m) {
		super();
		this.m = m;
	}
	
	public static MultipartTransformer create(FormDataMultiPart fdm) { return new MultipartTransformer(fdm); }

	public FormDataMultiPart get() {
		return m;
	}

	public MultipartTransformer field(String name, String value) {
		m.field(name, value);
		return this;
	}

	public MultipartTransformer field(String name, Object entity, MediaType mediaType) {
		m.field(name, entity, mediaType);
		return this;
	}

}
