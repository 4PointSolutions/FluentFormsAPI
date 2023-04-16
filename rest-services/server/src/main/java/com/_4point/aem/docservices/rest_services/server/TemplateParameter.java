package com._4point.aem.docservices.rest_services.server;

import org.apache.sling.api.request.RequestParameter;

import com._4point.aem.fluentforms.api.PathOrUrl;

/**
 * Holds a Template Parameter (i.e. an incoming request parameter that specifies template for a form).  It can be either 
 * by reference (so a pathOrUrl) or by value (so a byte array).   
 *
 */
public class TemplateParameter {

	public enum ParameterType {
		ByteArray, PathOrUrl
	};

	private final byte[] array;
	private final PathOrUrl pathOrUrl;
	private final ParameterType type;

	private TemplateParameter(byte[] array) {
		super();
		this.array = array;
		this.pathOrUrl = null;
		this.type = ParameterType.ByteArray;
	}

	private TemplateParameter(PathOrUrl pathOrUrl) {
		super();
		this.array = null;
		this.pathOrUrl = pathOrUrl;
		this.type = ParameterType.PathOrUrl;
	}

	public byte[] getArray() {
		return array;
	}

	public PathOrUrl getPathOrUrl() {
		return pathOrUrl;
	}

	public ParameterType getType() {
		return type;
	}

	public static TemplateParameter readParameter(RequestParameter templateParameter) {
		String contentType = templateParameter.getContentType();
		byte[] templateBytes = templateParameter.get();
		ContentType templateContentType = contentType != null ? ContentType.valueOf(contentType) 
															  : ContentType.autoDetect(templateBytes).orElse(ContentType.TEXT_PLAIN);
		if (templateContentType.isCompatibleWith(ContentType.TEXT_PLAIN)) {
			PathOrUrl templateRef = PathOrUrl.from(new String(templateBytes));
			if (!templateRef.getFilename().isPresent()) {
				throw new IllegalArgumentException("Template Parameter must point to an XDP file (" + templateRef.toString() + ").");
			}
			return new TemplateParameter(templateRef);
		} else if (templateContentType.isCompatibleWith(ContentType.APPLICATION_XDP)) {
			return new TemplateParameter(templateBytes);
		} else if (templateContentType.isCompatibleWith(ContentType.APPLICATION_PDF)) {
			return new TemplateParameter(templateBytes);
		} else {
			throw new IllegalArgumentException("Template parmameter has invalid content type. (" + templateContentType.getContentTypeStr() + ").");
		}
	}

}
