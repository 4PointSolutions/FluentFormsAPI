package com._4point.aem.fluentforms.api;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;

import org.apache.sling.api.resource.ResourceResolver;

import com._4point.aem.fluentforms.impl.AdobeDocumentFactoryImpl;

public interface DocumentFactory {

	public Document create(byte[] data);

	public Document create(File file, boolean ownFile);

	public Document create(File file);

	public Document create(Path file);

	public Document create(InputStream is);

//  Removed because this won't work with the client version of this library.  This may be re-instated later
//	as part of a separate class however until its requirement is proven, we're going to leave it out.
//	
//	public Document create(String jcrPath, ResourceResolver resolver, boolean manageResolver);
//
//	public Document create(String jcrPath, ResourceResolver resolver);
//
	public Document create(String jcrPath);

	public Document create(URL url);

	public Document create(com.adobe.aemfd.docmanager.Document document);	

	public static DocumentFactory getDefault() {
		return AdobeDocumentFactoryImpl.getFactory();
	}

}