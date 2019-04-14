package com._4point.aem.fluentforms.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.sling.api.resource.ResourceResolver;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com.adobe.aemfd.docmanager.passivation.DocumentPassivationHandler;


public enum DocumentFactoryImpl implements DocumentFactory {
	
	INSTANCE;
	
	public static DocumentFactory getFactory() {
		return INSTANCE;
	}

	/* (non-Javadoc)
	 * @see com._4point.aem.fluentforms.api.DocumentFactory#create(byte[])
	 */
	@Override
	public Document create(byte[] data) {
		return new DocumentImpl(data);
	}

	/* (non-Javadoc)
	 * @see com._4point.aem.fluentforms.api.DocumentFactory#create(com.adobe.aemfd.docmanager.passivation.DocumentPassivationHandler)
	 */
	@Override
	public Document create(DocumentPassivationHandler passivationHandler) {
		return new DocumentImpl(passivationHandler);
	}

	/* (non-Javadoc)
	 * @see com._4point.aem.fluentforms.api.DocumentFactory#create(java.io.File, boolean)
	 */
	@Override
	public Document create(File file, boolean ownFile) {
		return new DocumentImpl(file, ownFile);
	}

	/* (non-Javadoc)
	 * @see com._4point.aem.fluentforms.api.DocumentFactory#create(java.io.File)
	 */
	@Override
	public Document create(File file) {
		return new DocumentImpl(file);
	}

	/* (non-Javadoc)
	 * @see com._4point.aem.fluentforms.api.DocumentFactory#create(java.io.InputStream)
	 */
	@Override
	public Document create(InputStream is) {
		return new DocumentImpl(is);
	}

	/* (non-Javadoc)
	 * @see com._4point.aem.fluentforms.api.DocumentFactory#create(java.lang.String, org.apache.sling.api.resource.ResourceResolver, boolean)
	 */
	@Override
	public Document create(String jcrPath, ResourceResolver resolver, boolean manageResolver) {
		return new DocumentImpl(jcrPath, resolver, manageResolver);
	}

	/* (non-Javadoc)
	 * @see com._4point.aem.fluentforms.api.DocumentFactory#create(java.lang.String, org.apache.sling.api.resource.ResourceResolver)
	 */
	@Override
	public Document create(String jcrPath, ResourceResolver resolver) {
		return new DocumentImpl(jcrPath, resolver);
	}

	/* (non-Javadoc)
	 * @see com._4point.aem.fluentforms.api.DocumentFactory#create(java.lang.String)
	 */
	@Override
	public Document create(String jcrPath) {
		return new DocumentImpl(jcrPath);
	}

	/* (non-Javadoc)
	 * @see com._4point.aem.fluentforms.api.DocumentFactory#create(java.net.URL)
	 */
	@Override
	public Document create(URL url) {
		return new DocumentImpl(url);
	}

	public Document create(com.adobe.aemfd.docmanager.Document document) {
		return new DocumentImpl(document);
	}

	
	private static class DocumentImpl implements Document {

		private final com.adobe.aemfd.docmanager.Document doc;
		
		private DocumentImpl(byte[] data) {
			doc = new com.adobe.aemfd.docmanager.Document(data);
		}

		private DocumentImpl(DocumentPassivationHandler passivationHandler) {
			doc = new com.adobe.aemfd.docmanager.Document(passivationHandler);
		}

		private DocumentImpl(File file, boolean ownFile) {
			doc = new com.adobe.aemfd.docmanager.Document(file, ownFile);
		}

		private DocumentImpl(File file) {
			doc = new com.adobe.aemfd.docmanager.Document(file);
		}

		private DocumentImpl(InputStream is) {
			doc = new com.adobe.aemfd.docmanager.Document(is);
		}

		private DocumentImpl(String jcrPath, ResourceResolver resolver, boolean manageResolver) {
			doc = new com.adobe.aemfd.docmanager.Document(jcrPath, resolver, manageResolver);
		}

		private DocumentImpl(String jcrPath, ResourceResolver resolver) {
			doc = new com.adobe.aemfd.docmanager.Document(jcrPath, resolver);
		}

		private DocumentImpl(String jcrPath) {
			doc = new com.adobe.aemfd.docmanager.Document(jcrPath);
		}

		private DocumentImpl(URL url) {
			doc = new com.adobe.aemfd.docmanager.Document(url);
		}

		private DocumentImpl(com.adobe.aemfd.docmanager.Document document) {
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
}
