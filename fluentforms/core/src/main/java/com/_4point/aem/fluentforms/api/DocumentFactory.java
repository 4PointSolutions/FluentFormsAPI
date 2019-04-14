package com._4point.aem.fluentforms.api;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.apache.sling.api.resource.ResourceResolver;

import com._4point.aem.fluentforms.impl.DocumentFactoryImpl;
import com.adobe.aemfd.docmanager.passivation.DocumentPassivationHandler;

public interface DocumentFactory {

	public Document create(byte[] data);

	public Document create(DocumentPassivationHandler passivationHandler);

	public Document create(File file, boolean ownFile);

	public Document create(File file);

	public Document create(InputStream is);

	public Document create(String jcrPath, ResourceResolver resolver, boolean manageResolver);

	public Document create(String jcrPath, ResourceResolver resolver);

	public Document create(String jcrPath);

	public Document create(URL url);

	public Document create(com.adobe.aemfd.docmanager.Document document);	

	public static DocumentFactory getDefault() {
		return DocumentFactoryImpl.getFactory();
	}

}