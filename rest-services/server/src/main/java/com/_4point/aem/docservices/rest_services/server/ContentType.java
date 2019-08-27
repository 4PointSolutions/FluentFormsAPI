package com._4point.aem.docservices.rest_services.server;

import java.util.Objects;

public class ContentType {
	private static final String WILDCARD_CHAR = "*";
	private static final String SEPARATOR_CHAR = "/";
	
	public static final ContentType APPLICATION_XML = new ContentType("application/xml");
	public static final ContentType APPLICATION_PDF = new ContentType("application/pdf");
	public static final ContentType APPLICATION_XDP = new ContentType("application/vnd.adobe.xdp+xml");
	public static final ContentType MULTIPART_FORMDATA = new ContentType("multipart/form-data");
	public static final ContentType TEXT_HTML = new ContentType("text/html");
	public static final ContentType TEXT_PLAIN = new ContentType("text/plain");
	public static final ContentType TEXT_WILDCARD = new ContentType("text/*");
	public static final ContentType APPLICATION_WILDCARD = new ContentType("application/*");
	public static final ContentType WILDCARD = new ContentType("*/*");

	private final String type;
	private final String subtype;

	private ContentType(String contentTypeStr) {
		String[] parts = contentTypeStr.split("/");
		if (parts.length != 2) {
			throw new IllegalArgumentException("Invalid content type string - '" + contentTypeStr + "'.  Expected exactly one separator character ('" + SEPARATOR_CHAR + "').");
		}
		type = parts[0].trim().toLowerCase();
		subtype = parts[1].trim().toLowerCase();
		if (WILDCARD_CHAR.equals(type) && !WILDCARD_CHAR.equalsIgnoreCase(subtype)) {
			throw new IllegalArgumentException("Invalid content type string - '" + contentTypeStr + "'.  Main type cannot be a wildcard if the subtype is not also a wildcard.");
		}
	}
	
	public static ContentType valueOf(String contentTypeStr) {
		String trimmedVersion = Objects.requireNonNull(contentTypeStr, "Content Type String cannot be null.").trim();
		if (trimmedVersion.isEmpty()) {
			throw new IllegalArgumentException("Content Type String cannot be empty.");
		}
		return new ContentType(trimmedVersion);
	}

	public String getContentTypeStr() {
		return type + SEPARATOR_CHAR + subtype;
	}
	
	public boolean isCompatibleWith(ContentType candidate) {
		return areTypesCompatible(this.type, candidate.type) && areTypesCompatible(this.subtype, candidate.subtype);
	}

	private static boolean areTypesCompatible(String type1, String type2) {
		return type1.equals(WILDCARD_CHAR) || type2.equals(WILDCARD_CHAR) || type1.equals(type2); 
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((subtype == null) ? 0 : subtype.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ContentType other = (ContentType) obj;
		if (subtype == null) {
			if (other.subtype != null)
				return false;
		} else if (!subtype.equals(other.subtype))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return getContentTypeStr();
	}

}
