package com._4point.aem.fluentforms.impl.docassurance;

import com._4point.aem.fluentforms.api.docassurance.ReaderExtensionOptions;
import com.adobe.fd.readerextensions.client.ReaderExtensionsOptionSpec;

public class ReaderExtensionOptionsImpl implements ReaderExtensionOptions {

	private String credentialAlias = null;
	private ReaderExtensionsOptionSpec reOptions = null;
	
	@Override
	public String getCredentialAlias() {
		return credentialAlias;
	}

	@Override
	public ReaderExtensionOptionsImpl setCredentialAlias(String credentialAlias) {
		this.credentialAlias = credentialAlias;
		return this;
	}

	@Override
	public ReaderExtensionsOptionSpec getReOptions() {
		return reOptions;
	}

	@Override
	public ReaderExtensionOptions setReOptions(ReaderExtensionsOptionSpec reOptions) {
		this.reOptions = reOptions;
		return this;
	}

}
