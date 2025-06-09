package com._4point.aem.fluentforms.api;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;

import com._4point.aem.fluentforms.impl.AdobeDocumentFactoryImpl;

/**
 * Factory class for creating Document objects.  In AEM server-side code, this is usually an AdobeDocumentFactoryImpl class 
 * and in client code this is usually a SimpleDocumentFactoryImpl.
 */
public interface DocumentFactory {

	/**
	 * Creates a Document from a byte-array.  
	 * 
	 * @param data - The byte-array serving as the content-source for the Document.
	 * @return
	 */
	public Document create(byte[] data);

	/**
	 * Creates a Document from a file. 
	 * 
	 * @deprecated Use create(Path) instead.
	 * 
	 * @param file - The file serving as the content-source for the Document.
	 * @param ownFile - Whether the file is henceforth to be managed by the Document.
	 * @return
	 */
	@Deprecated
	public Document create(File file, boolean ownFile);

	/**
	 * Creates a Document from a file. Equivalent to create(file, false). See create(java.io.File, boolean) for more details.
	 * 
	 * @deprecated Use create(Path) instead.
	 * 
	 * @param file - The file serving as the content-source for the Document.
	 * @return
	 */
	@Deprecated
	public Document create(File file);

	/**
	 * Creates a Document from a file path. Equivalent to create(file, false). See create(java.io.File, boolean) for more details.
	 * 
	 * @param file - The file path serving as the content-source for the Document.
	 * @return
	 */
	public Document create(Path file);

	/**
	 * Creates a Document from an input-stream. 
	 * 
	 * Note that the Document at no point in its life-cycle ever closes the input-stream itself, 
	 * in keeping with the practice of not managing resources passed in externally unless explicitly instructed to do so. 
	 * Hence, it is the responsibility of the application creating the Document to also close the input-stream properly 
	 * when the time comes. 
	 * 
	 * @param is - The input-stream serving as the content-source for the Document.
	 * @return
	 */
	public Document create(InputStream is);

//  Removed because this won't work with the client version of this library.  This may be re-instated later
//	as part of a separate class however until its requirement is proven, we're going to leave it out.
//	
//	public Document create(String jcrPath, ResourceResolver resolver, boolean manageResolver);
//
//	public Document create(String jcrPath, ResourceResolver resolver);
//
	/**
	 * For internal use only.
	 * 
	 * @param jcrPath
	 * @return
	 */
	public Document create(String jcrPath);

	/**
	 * For internal use only.
	 * 
	 * @param url
	 * @return
	 */
	public Document create(URL url);

	/**
	 * For internal use only.
	 * 
	 * @param document
	 * @return
	 */
	public Document create(com.adobe.aemfd.docmanager.Document document);	

	/**
	 * Intended as a convenience method however it returns an AdobeDocumentFactoryImpl which is not useful
	 * for client side code, so it's best to avoid using this unless you know what you are doing.
	 * 
	 * It may be deprecated and removed in the future version of FluentForms.
	 * 
	 * @return
	 */
	@Deprecated
	public static DocumentFactory getDefault() {
		return AdobeDocumentFactoryImpl.getFactory();
	}

}