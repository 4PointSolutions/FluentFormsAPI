package com._4point.aem.fluentforms.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import org.apache.sling.api.resource.ResourceResolver;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;


public enum AdobeDocumentFactoryImpl implements DocumentFactory {
	
	INSTANCE;
	
	public static DocumentFactory getFactory() {
		return INSTANCE;
	}

	/* (non-Javadoc)
	 * @see com._4point.aem.fluentforms.api.DocumentFactory#create(byte[])
	 */
	@Override
	public Document create(byte[] data) {
		return new AdobeDocumentImpl(data);
	}

	/* (non-Javadoc)
	 * @see com._4point.aem.fluentforms.api.DocumentFactory#create(java.io.File, boolean)
	 */
	@Override
	public Document create(File file, boolean ownFile) {
		return new AdobeDocumentImpl(file, ownFile);
	}

	/* (non-Javadoc)
	 * @see com._4point.aem.fluentforms.api.DocumentFactory#create(java.io.File)
	 */
	@Override
	public Document create(File file) {
		return new AdobeDocumentImpl(file);
	}

	/* (non-Javadoc)
	 * @see com._4point.aem.fluentforms.api.DocumentFactory#create(java.nio.file.Path)
	 */
	@Override
	public Document create(Path path) {
		return new AdobeDocumentImpl(path.toFile());
	}
	
	/* (non-Javadoc)
	 * @see com._4point.aem.fluentforms.api.DocumentFactory#create(java.io.InputStream)
	 */
	@Override
	public Document create(InputStream is) {
		return new AdobeDocumentImpl(is);
	}

	/* (non-Javadoc)
	 * @see com._4point.aem.fluentforms.api.DocumentFactory#create(java.lang.String, org.apache.sling.api.resource.ResourceResolver, boolean)
	 */
	@Override
	public Document create(String jcrPath, ResourceResolver resolver, boolean manageResolver) {
		return new AdobeDocumentImpl(jcrPath, resolver, manageResolver);
	}

	/* (non-Javadoc)
	 * @see com._4point.aem.fluentforms.api.DocumentFactory#create(java.lang.String, org.apache.sling.api.resource.ResourceResolver)
	 */
	@Override
	public Document create(String jcrPath, ResourceResolver resolver) {
		return new AdobeDocumentImpl(jcrPath, resolver);
	}

	/* (non-Javadoc)
	 * @see com._4point.aem.fluentforms.api.DocumentFactory#create(java.lang.String)
	 */
	@Override
	public Document create(String jcrPath) {
		return new AdobeDocumentImpl(jcrPath);
	}

	/* (non-Javadoc)
	 * @see com._4point.aem.fluentforms.api.DocumentFactory#create(java.net.URL)
	 */
	@Override
	public Document create(URL url) {
		return new AdobeDocumentImpl(url);
	}

	public Document create(com.adobe.aemfd.docmanager.Document document) {
		return new AdobeDocumentImpl(document);
	}

	
	private static class AdobeDocumentImpl implements Document {

		private final com.adobe.aemfd.docmanager.Document doc;
		
		private AdobeDocumentImpl(byte[] data) {
			doc = new com.adobe.aemfd.docmanager.Document(data);
		}

		private AdobeDocumentImpl(File file, boolean ownFile) {
			doc = new com.adobe.aemfd.docmanager.Document(file, ownFile);
		}

		private AdobeDocumentImpl(File file) {
			doc = new com.adobe.aemfd.docmanager.Document(file);
		}

		private AdobeDocumentImpl(InputStream is) {
			doc = new com.adobe.aemfd.docmanager.Document(is);
		}

		private AdobeDocumentImpl(String jcrPath, ResourceResolver resolver, boolean manageResolver) {
			doc = new com.adobe.aemfd.docmanager.Document(jcrPath, resolver, manageResolver);
		}

		private AdobeDocumentImpl(String jcrPath, ResourceResolver resolver) {
			doc = new com.adobe.aemfd.docmanager.Document(jcrPath, resolver);
		}

		private AdobeDocumentImpl(String jcrPath) {
			doc = new com.adobe.aemfd.docmanager.Document(jcrPath);
		}

		private AdobeDocumentImpl(URL url) {
			doc = new com.adobe.aemfd.docmanager.Document(url);
		}

		private AdobeDocumentImpl(com.adobe.aemfd.docmanager.Document document) {
			doc = document;
		}
		

		/* (non-Javadoc)
		 * @see com._4point.aem.fluentforms.api.Document#close()
		 */
		@Override
		public void close() throws IOException {
			doc.close();
		}

		/* (non-Javadoc)
		 * @see com._4point.aem.fluentforms.api.Document#copyToFile(java.io.File)
		 */
		@Override
		public void copyToFile(File arg0) throws IOException {
			doc.copyToFile(arg0);
		}

		/* (non-Javadoc)
		 * @see com._4point.aem.fluentforms.api.Document#dispose()
		 */
		@Override
		public void dispose() {
			doc.dispose();
		}

		/* (non-Javadoc)
		 * @see com._4point.aem.fluentforms.api.Document#getAttribute(java.lang.String)
		 */
		@Override
		public Object getAttribute(String name) {
			return doc.getAttribute(name);
		}

		/* (non-Javadoc)
		 * @see com._4point.aem.fluentforms.api.Document#getContentType()
		 */
		@Override
		public String getContentType() throws IOException {
			return doc.getContentType();
		}

		/* (non-Javadoc)
		 * @see com._4point.aem.fluentforms.api.Document#getInlineData()
		 */
		@Override
		public byte[] getInlineData() throws IOException {
			return doc.getInlineData();
		}

		/* (non-Javadoc)
		 * @see com._4point.aem.fluentforms.api.Document#getInputStream()
		 */
		@Override
		public InputStream getInputStream() throws IOException {
			return doc.getInputStream();
		}

		/* (non-Javadoc)
		 * @see com._4point.aem.fluentforms.api.Document#getMaxInlineSize()
		 */
		@Override
		public int getMaxInlineSize() {
			return doc.getMaxInlineSize();
		}

		/* (non-Javadoc)
		 * @see com._4point.aem.fluentforms.api.Document#length()
		 */
		@Override
		public long length() throws IOException {
			return doc.length();
		}

		/* (non-Javadoc)
		 * @see com._4point.aem.fluentforms.api.Document#passivate()
		 */
		@Override
		public void passivate() throws IOException {
			doc.passivate();
		}

		/* (non-Javadoc)
		 * @see com._4point.aem.fluentforms.api.Document#removeAttribute(java.lang.String)
		 */
		@Override
		public void removeAttribute(String name) {
			doc.removeAttribute(name);
		}

		/* (non-Javadoc)
		 * @see com._4point.aem.fluentforms.api.Document#setAttribute(java.lang.String, java.lang.Object)
		 */
		@Override
		public void setAttribute(String name, Object val) {
			doc.setAttribute(name, val);
		}

		/* (non-Javadoc)
		 * @see com._4point.aem.fluentforms.api.Document#setContentType(java.lang.String)
		 */
		@Override
		public void setContentType(String contentType) {
			doc.setContentType(contentType);
		}

		/* (non-Javadoc)
		 * @see com._4point.aem.fluentforms.api.Document#setMaxInlineSize(int)
		 */
		@Override
		public void setMaxInlineSize(int maxInlineSize) {
			doc.setMaxInlineSize(maxInlineSize);
		}

		/* (non-Javadoc)
		 * @see com._4point.aem.fluentforms.api.Document#getAdobeDocument()
		 */
		@Override
		public com.adobe.aemfd.docmanager.Document getAdobeDocument() {
			return doc;
		}
	}

	// Simplistic implementation of Document interface.
	private static class MyDocumentImpl implements Document {
		int maxInlineSize;
		byte[] inlineData;
		String contentType;
		Map<String, Object> attributes = new TreeMap<>();
		
		@Override
		public void close() throws IOException {
			this.dispose();
		}

		@Override
		public void copyToFile(File arg0) throws IOException {
			// TODO: Implement this at some future date.
			throw new UnsupportedOperationException("copyToFile is not supported at this time.");
		}

		@Override
		public void dispose() {
			this.inlineData = new byte[0]; 
		}

		@Override
		public Object getAttribute(String name) {
			return attributes.get(name);
		}

		@Override
		public String getContentType() throws IOException {
			return this.contentType;
		}

		@Override
		public byte[] getInlineData() throws IOException {
			return Arrays.copyOf(this.inlineData, this.inlineData.length);
		}

		@Override
		public InputStream getInputStream() throws IOException {
			return new ByteArrayInputStream(getInlineData());
		}

		@Override
		public int getMaxInlineSize() {
			return maxInlineSize;
		}

		@Override
		public long length() throws IOException {
			return this.inlineData.length;
		}

		@Override
		public void passivate() throws IOException {
			// Do nothing.
		}

		@Override
		public void removeAttribute(String name) {
			this.attributes.remove(name);
		}

		@Override
		public void setAttribute(String name, Object val) {
			this.attributes.put(name, val);
		}

		@Override
		public void setContentType(String contentType) {
			this.contentType = contentType;
		}

		@Override
		public void setMaxInlineSize(int maxInlineSize) {
			this.maxInlineSize = maxInlineSize;
		}

		@Override
		public com.adobe.aemfd.docmanager.Document getAdobeDocument() {
			return null;
		}
		
	}

}
