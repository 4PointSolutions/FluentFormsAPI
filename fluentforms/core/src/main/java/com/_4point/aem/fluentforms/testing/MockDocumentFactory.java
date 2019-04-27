package com._4point.aem.fluentforms.testing;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.sling.api.resource.ResourceResolver;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com.adobe.aemfd.docmanager.passivation.DocumentPassivationHandler;

public class MockDocumentFactory implements DocumentFactory {

	public final static Document GLOBAL_DUMMY_DOCUMENT = new MockDocumentImpl();
	public final Document DUMMY_DOCUMENT = new MockDocumentImpl();

	@Override
	public Document create(byte[] data) {
		return DUMMY_DOCUMENT;
	}

	@Override
	public Document create(DocumentPassivationHandler passivationHandler) {
		return DUMMY_DOCUMENT;
	}

	@Override
	public Document create(File file, boolean ownFile) {
		return DUMMY_DOCUMENT;
	}

	@Override
	public Document create(File file) {
		return DUMMY_DOCUMENT;
	}

	@Override
	public Document create(InputStream is) {
		return DUMMY_DOCUMENT;
	}

	@Override
	public Document create(String jcrPath, ResourceResolver resolver, boolean manageResolver) {
		return DUMMY_DOCUMENT;
	}

	@Override
	public Document create(String jcrPath, ResourceResolver resolver) {
		return DUMMY_DOCUMENT;
	}

	@Override
	public Document create(String jcrPath) {
		return DUMMY_DOCUMENT;
	}

	@Override
	public Document create(URL url) {
		return DUMMY_DOCUMENT;
	}

	@Override
	public Document create(com.adobe.aemfd.docmanager.Document document) {
		return DUMMY_DOCUMENT;
	}

	private static class MockDocumentImpl implements Document {

		@Override
		public void close() throws IOException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void copyToFile(File arg0) throws IOException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void dispose() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Object getAttribute(String name) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getContentType() throws IOException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public byte[] getInlineData() throws IOException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public InputStream getInputStream() throws IOException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getMaxInlineSize() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public long length() throws IOException {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void passivate() throws IOException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void removeAttribute(String name) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setAttribute(String name, Object val) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setContentType(String contentType) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setMaxInlineSize(int maxInlineSize) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public com.adobe.aemfd.docmanager.Document getAdobeDocument() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}
