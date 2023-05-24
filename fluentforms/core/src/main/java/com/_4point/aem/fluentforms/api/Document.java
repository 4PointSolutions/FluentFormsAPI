package com._4point.aem.fluentforms.api;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * This interface is based on AEM's Document object (com.adobe.aemfd.docmanager.Document).  When running
 * on the AEM stack it is typically a wrapper around an DocManager Document object.  When running on a
 * client it is backed by a simplistic implementation that keeps everything in memory, but client code
 * should not count on this implementation as it could change in the future.  
 *
 */
public interface Document extends AutoCloseable, Closeable, HasAttributes {

	/**
	 * Disposes of the document.
	 */
	void close() throws IOException;

	Document copyToFile(File arg0) throws IOException;

	/**
	 * Disposes of the Document, and cleans up any managed resources. These resources could be those passed 
	 * to the Document at initialization time (e.g. files/JCR resource-resolvers having the management flag set) 
	 * or those created by the Document itself as part of its life-cycle (e.g. temporary files as part of passivation). 
	 * It is of the utmost importance that Documents be disposed once they are done with, so that this clean-up can occur.
	 * 
	 * Once a Document has been disposed, it becomes unusable and any attempt to invoke an operation on a disposed 
	 * Document results in an IllegalStateException being thrown.
	 * 
	 * Calling dispose() on an already disposed document has no effect.
	 * 
	 */
	void dispose();

//	Object getAttribute(String name);		// moved to HasAttributes interface

	/**
	 * Gets the content type attribute for this Document.  Most AEM services will set this attribute for
	 * Documents they produce.  Often the FluentForms libraries will supply a content type if the AEM
	 * service does not.  
	 * 
	 * @return
	 * @throws IOException
	 */
	String getContentType() throws IOException;

	/**
	 * Returns the Document contents as a byte-array, only if the contents are small enough 
	 * to fit in memory as decided by the max-inline-size property (see setMaxInlineSize(int)).
	 * 
	 * @return
	 * @throws IOException
	 */
	byte[] getInlineData() throws IOException;

	/**
	 * Returns an input-stream over the Document contents
	 * 
	 * @return
	 * @throws IOException
	 */
	InputStream getInputStream() throws IOException;

	/**
	 * Returns the value of the max-inline-size property which decides whether the Document contents can be loaded into memory during passivation.
	 * 
	 * @return
	 */
	int getMaxInlineSize();

	/**
	 * Returns the length of the Document contents
	 * 
	 * @return
	 * @throws IOException
	 */
	long length() throws IOException;

	/**
	 * Triggers passivation on the Document, as a result of which one of the following happens to the 
	 * contents of the content-source from which the Document was initialized: The contents are loaded 
	 * into memory if they are small enough, as dictated by the max-inline-size property 
	 * (see setMaxInlineSize(int)), or The contents are potentially saved to a location from which fresh 
	 * content-streams can be created every time the application calls getInputStream().
	 * 
	 * @return
	 * @throws IOException
	 */
	Document passivate() throws IOException;

	Document removeAttribute(String name);

//	void setAttribute(String name, Object val); 		// moved to HasAttributes interface

	/**
	 * Sets the content type attribute.
	 * 
	 * @param contentType
	 * @return
	 */
	Document setContentType(String contentType);

	/**
	 * Sets the value of the max-inline-size property to be used during passivation for deciding whether the Document contents can be loaded into memory.
	 * 
	 * @param maxInlineSize
	 * @return
	 */
	Document setMaxInlineSize(int maxInlineSize);
	
	/**
	 * Sets the content type attribute if it has not already been set.
	 * 
	 * @param contentType
	 * @return
	 * @throws IOException
	 */
	public default Document setContentTypeIfEmpty(String contentType) throws IOException {
		if (this.getContentType() == null || this.getContentType().isEmpty()) {
			this.setContentType(contentType);
		}
		return this;
	}
	
	/**
	 * Tests to see if the document is empty (i.e. length <= 0).
	 * 
	 * @return
	 */
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