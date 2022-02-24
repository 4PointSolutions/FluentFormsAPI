package com._4point.aem.fluentforms.api;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface Document extends AutoCloseable, Closeable, HasAttributes {

	void close() throws IOException;

	Document copyToFile(File arg0) throws IOException;

	void dispose();

//	Object getAttribute(String name);		// moved to HasAttributes interface

	String getContentType() throws IOException;

	byte[] getInlineData() throws IOException;

	InputStream getInputStream() throws IOException;

	int getMaxInlineSize();

	long length() throws IOException;

	Document passivate() throws IOException;

	Document removeAttribute(String name);

//	void setAttribute(String name, Object val); 		// moved to HasAttributes interface

	Document setContentType(String contentType);

	Document setMaxInlineSize(int maxInlineSize);
	
	public default Document setContentTypeIfEmpty(String contentType) throws IOException {
		if (this.getContentType() == null || this.getContentType().isEmpty()) {
			this.setContentType(contentType);
		}
		return this;
	}
	
	public default boolean isEmpty() {
		try {
			return !(this.length() > 1);
		} catch (IOException e) {		// Convert the checked IOException to an unchecked exception.  This should be rare enough that a user shouldn't have to handle it.
			String exMsg = e.getMessage();
			throw new IllegalStateException("I/O Error while determining the length of document." + (exMsg != null ? " (" + exMsg + ")" : ""), e);
		}
	}
	
	@Override
	default Document setAttribute(String name, Object val) {
		this.setAttribute(name, val);
		return this;
	}

	@Override
	default Document setAttributeAsBoolean(String name, Boolean val) {
		HasAttributes.super.setAttributeAsBoolean(name, val);
		return this;
	}

	@Override
	default Document setAttributeAsByte(String name, Byte val) {
		HasAttributes.super.setAttributeAsByte(name, val);
		return this;
	}

	@Override
	default Document setAttributeAsCharacter(String name, Character val) {
		HasAttributes.super.setAttributeAsCharacter(name, val);
		return this;
	}

	@Override
	default Document setAttributeAsFloat(String name, Float val) {
		HasAttributes.super.setAttributeAsFloat(name, val);
		return this;
	}

	@Override
	default Document setAttributeAsInteger(String name, Integer val) {
		HasAttributes.super.setAttributeAsInteger(name, val);
		return this;
	}

	@Override
	default Document setAttributeAsLong(String name, Long val) {
		HasAttributes.super.setAttributeAsLong(name, val);
		return this;
	}

	@Override
	default Document setAttributeAsShort(String name, Short val) {
		HasAttributes.super.setAttributeAsShort(name, val);
		return this;
	}

	@Override
	default Document setAttributeAsString(String name, String val) {
		HasAttributes.super.setAttributeAsString(name, val);
		return this;
	}

	public static String CONTENT_TYPE_PDF = "application/pdf";
	public static String CONTENT_TYPE_XDP = "application/vnd.adobe.xdp+xml";
	public static String CONTENT_TYPE_DPL = "application/vnd.datamax-dpl";
	public static String CONTENT_TYPE_IPL = "application/vnd.intermec-ipl";
	public static String CONTENT_TYPE_PCL = "application/vnd.hp-pcl";
	public static String CONTENT_TYPE_PS = "application/postscript";
	public static String CONTENT_TYPE_TPCL = "application/vnd.toshiba-tpcl";
	public static String CONTENT_TYPE_ZPL = "x-application/zpl";
}