package com._4point.aem.fluentforms.impl.docassurance.server;

import org.apache.sling.api.resource.ResourceResolver;

import com._4point.aem.fluentforms.api.docassurance.ReaderExtensionOptions;
import com.adobe.fd.readerextensions.client.ReaderExtensionsOptionSpec;

public class ReaderExtensionOptionsImpl implements ReaderExtensionOptions {

	private final ResourceResolver resourceResolver;

	private ReaderExtensionOptionsImpl(ResourceResolver resourceResolver) {
		super();
		this.resourceResolver = resourceResolver;
	}

	@Override
	public String getCredentialAlias() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReaderExtensionsOptionSpec getReOptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCredentialAlias(String credentialAlias) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setReOptions(ReaderExtensionsOptionSpec reOptions) {
		// TODO Auto-generated method stub
	}

//  These are commented out because I expect to use the constructor instead.  I don't foresee the need to change
//  the resource resolver after the object has been constructed (nor to get the resource resolver afterwards).
//
//	public ResourceResolver getResourceResolver() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	public void setResourceResolver(ResourceResolver resourceResolver) {
//		// TODO Auto-generated method stub
//	}

}
