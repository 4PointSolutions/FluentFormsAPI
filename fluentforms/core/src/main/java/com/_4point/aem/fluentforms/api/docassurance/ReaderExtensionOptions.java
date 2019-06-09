package com._4point.aem.fluentforms.api.docassurance;

import com.adobe.fd.readerextensions.client.ReaderExtensionsOptionSpec;

public interface ReaderExtensionOptions {

	String getCredentialAlias();

	ReaderExtensionsOptionSpec getReOptions();

	void setCredentialAlias(String credentialAlias);

	void setReOptions(ReaderExtensionsOptionSpec reOptions);

}