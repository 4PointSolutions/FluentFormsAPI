package com._4point.aem.fluentforms.testing;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.apache.sling.api.resource.ResourceResolver;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com.adobe.aemfd.docmanager.passivation.DocumentPassivationHandler;

public class MockDocumentFactory implements DocumentFactory {

	public final Document DUMMY_DOCUMENT = DocumentFactory.getDefault().create(new byte[0]);

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

}
