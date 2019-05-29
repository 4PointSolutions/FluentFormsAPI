package com._4point.aem.fluentforms.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

// import org.apache.sling.api.resource.ResourceResolver;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;


public enum SimpleDocumentFactoryImpl implements DocumentFactory {
	
	INSTANCE;
	
	public static DocumentFactory getFactory() {
		return INSTANCE;
	}

	/* (non-Javadoc)
	 * @see com._4point.aem.fluentforms.api.DocumentFactory#create(byte[])
	 */
	@Override
	public Document create(byte[] data) {
		return new SimpleDocumentImpl(data);
	}

	/* (non-Javadoc)
	 * @see com._4point.aem.fluentforms.api.DocumentFactory#create(java.io.File, boolean)
	 */
	@Override
	public Document create(File file, boolean ownFile) {
		return new SimpleDocumentImpl(file, ownFile);
	}

	/* (non-Javadoc)
	 * @see com._4point.aem.fluentforms.api.DocumentFactory#create(java.io.File)
	 */
	@Override
	public Document create(File file) {
		return new SimpleDocumentImpl(file);
	}

	/* (non-Javadoc)
	 * @see com._4point.aem.fluentforms.api.DocumentFactory#create(java.nio.file.Path)
	 */
	@Override
	public Document create(Path path) {
		return new SimpleDocumentImpl(path.toFile());
	}
	
	/* (non-Javadoc)
	 * @see com._4point.aem.fluentforms.api.DocumentFactory#create(java.io.InputStream)
	 */
	@Override
	public Document create(InputStream is) {
		return new SimpleDocumentImpl(is);
	}

//  Removed because this won't work with the client version of this library.  This may be re-instated later
//	as part of a separate class however until its requirement is proven, we're going to leave it out.
//	
	/* (non-Javadoc)
	 * @see com._4point.aem.fluentforms.api.DocumentFactory#create(java.lang.String, org.apache.sling.api.resource.ResourceResolver, boolean)
	 */
//	@Override
//	public Document create(String jcrPath, ResourceResolver resolver, boolean manageResolver) {
//		throw new UnsupportedOperationException("constructing from jcrPath is not supported.");
//	}

	/* (non-Javadoc)
	 * @see com._4point.aem.fluentforms.api.DocumentFactory#create(java.lang.String, org.apache.sling.api.resource.ResourceResolver)
	 */
//	@Override
//	public Document create(String jcrPath, ResourceResolver resolver) {
//		throw new UnsupportedOperationException("constructing from jcrPath is not supported.");
//	}
//
	/* (non-Javadoc)
	 * @see com._4point.aem.fluentforms.api.DocumentFactory#create(java.lang.String)
	 */
	@Override
	public Document create(String jcrPath) {
		throw new UnsupportedOperationException("constructing from jcrPath is not supported.");
	}

	/* (non-Javadoc)
	 * @see com._4point.aem.fluentforms.api.DocumentFactory#create(java.net.URL)
	 */
	@Override
	public Document create(URL url) {
		return new SimpleDocumentImpl(url);
	}

	// Simplistic implementation of Document interface.
	private static class SimpleDocumentImpl implements Document {
		int maxInlineSize;		// Currently has no effect.  Just here to satisfy interface.
		byte[] inlineData;
		String contentType;
		Map<String, Object> attributes = new TreeMap<>();
		
		private SimpleDocumentImpl(byte[] data) {
			this.inlineData = Arrays.copyOf(data, data.length);
		}

		private SimpleDocumentImpl(File file, boolean ownFile) {
			// We ignore the ownFile parameter because we don't care.  We are going to just read it in anyway.
			try {
				this.inlineData = readToByteArray(new FileInputStream(file));
			} catch (FileNotFoundException e) {
				// Convert to runtime exception.
				throw new IllegalArgumentException("File not found. (" + file.toString() + ").", e);
			}
		}

		private SimpleDocumentImpl(File file) {
			try {
				this.inlineData = readToByteArray(new FileInputStream(file));
			} catch (FileNotFoundException e) {
				// Convert to runtime exception.
				throw new IllegalArgumentException("File not found. (" + file.toString() + ").", e);
			}
		}

		private SimpleDocumentImpl(InputStream is) {
			this.inlineData = readToByteArray(is);
		}

		private SimpleDocumentImpl(URL url) {
			throw new UnsupportedOperationException("constructing from URL is not supported at this time.");
		}

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
		
		private static byte[] readToByteArray(InputStream is) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try {
				transfer(is, bos);
			} catch (IOException e) {
				// Convert to runtime exception.
				throw new IllegalArgumentException("I/O Error while reading inputstream.", e);
			}
			return bos.toByteArray();
		}

		private static void transfer(InputStream in, OutputStream out) throws IOException {
			byte[] buffer = new byte[1024];
			int len;

			// read bytes from the input stream and store them in buffer
			while ((len = in.read(buffer)) != -1) {
				// write bytes from the buffer into output stream
				out.write(buffer, 0, len);
			}
		}

		
	}

	@Override
	public Document create(com.adobe.aemfd.docmanager.Document document) {
		throw new UnsupportedOperationException("constructing from adobe Document object is not supported.");
	}

}
