package com._4point.aem.fluentforms.testing;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.sling.api.resource.ResourceResolver;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;

public class MockDocumentFactory implements DocumentFactory {

	public final static Document GLOBAL_DUMMY_DOCUMENT = new MockDocumentImpl();
	public final MockDocumentImpl DUMMY_DOCUMENT = new MockDocumentImpl();
	public final static MockDocumentFactory GLOBAL_INSTANCE = new MockDocumentFactory();

	private final List<MockDocumentImpl> docsCreated = new ArrayList<>();
	
	public List<MockDocumentImpl> getDocsCreated() {
		return docsCreated;
	}

	public boolean created(Document doc) {
		if (doc instanceof MockDocumentImpl) {
			for(MockDocumentImpl i:docsCreated) {
				if (doc == i) {
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public Document create(byte[] data) {
		MockDocumentImpl createdDoc = new MockDocumentImpl();
		createdDoc.setInlineData(data);
		docsCreated.add(createdDoc);
		return createdDoc;
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
	public Document create(Path file) {
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

		private Map<String, Object> attributes = new TreeMap<>();
		private String contentType;
		private byte[] data = new byte[0];
		private int maxInlineSize = 0;
		
		public void setInlineData(byte[] data) {
			this.data = data;
		}
		
		@Override
		public void close() throws IOException {
			// NO-OP
		}

		@Override
		public void copyToFile(File arg0) throws IOException {
			// NO-OP
		}

		@Override
		public void dispose() {
			// NO-OP
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
			return this.data;
		}

		@Override
		public InputStream getInputStream() throws IOException {
			return new ByteArrayInputStream(this.data);
		}

		@Override
		public int getMaxInlineSize() {
			return this.maxInlineSize;
		}

		@Override
		public long length() throws IOException {
			return this.data.length;
		}

		@Override
		public void passivate() throws IOException {
			// NO-OP
		}

		@Override
		public void removeAttribute(String name) {
			attributes.remove(name);
		}

		@Override
		public void setAttribute(String name, Object val) {
			attributes.put(name, val);
		}

		@Override
		public void setContentType(String contentType) {
			// TODO: Convert this to use the attributes Map with the correct key
			this.contentType = contentType;
		}

		@Override
		public void setMaxInlineSize(int maxInlineSize) {
			this.maxInlineSize = maxInlineSize;
		}

		@Override
		public com.adobe.aemfd.docmanager.Document getAdobeDocument() {
			// NO-OP
			return null;
		}
		
	}
}
