package com._4point.aem.fluentforms.impl.docassurance;

import java.util.ArrayList;
import java.util.List;

import com._4point.aem.fluentforms.api.docassurance.EncryptionOptions;
import com.adobe.fd.docassurance.client.api.DocAssuranceServiceOperationTypes;
import com.adobe.fd.encryption.client.CertificateEncryptionIdentity;
import com.adobe.fd.encryption.client.CertificateEncryptionOptionSpec;
import com.adobe.fd.encryption.client.PasswordEncryptionOptionSpec;

public class EncryptionOptionsImpl implements EncryptionOptions {
	private final com.adobe.fd.docassurance.client.api.EncryptionOptions options = com.adobe.fd.docassurance.client.api.EncryptionOptions.getInstance();
	private List<CertificateEncryptionIdentity> pkiIdentities = new ArrayList<>();

	@Override
	public CertificateEncryptionOptionSpec getCertOptionSpec() {
		return this.options.getCertOptionSpec();
	}

	@Override
	public String getCredentialAlias() {
		return this.options.getCredentialAlias();
	}

	@Override
	public DocAssuranceServiceOperationTypes getEncryptionType() {
		return this.options.getEncryptionType();
	}

	@Override
	public PasswordEncryptionOptionSpec getPasswordEncryptionOptionSpec() {
		return this.options.getPasswordEncryptionOptionSpec();
	}

	@Override
	public List<CertificateEncryptionIdentity> getPkiIdentities() {
		return this.options.getPkiIdentities();
	}

	@Override
	public EncryptionOptions setCertOptionSpec(CertificateEncryptionOptionSpec certOptionSpec) {
		this.options.setCertOptionSpec(certOptionSpec);
		return this;
	}

	@Override
	public EncryptionOptions setCredentialAlias(String credentialAlias) {
		this.options.setCredentialAlias(credentialAlias);
		return this;
	}

	@Override
	public EncryptionOptions setEncryptionType(DocAssuranceServiceOperationTypes encryptionType) {
		this.options.setEncryptionType(encryptionType);
		return this;
	}

	@Override
	public EncryptionOptions setPasswordEncryptionOptionSpec(PasswordEncryptionOptionSpec passwordEncryptionOptionSpec) {
		this.options.setPasswordEncryptionOptionSpec(passwordEncryptionOptionSpec);
		return this;
	}
	
	public EncryptionOptions addPkiIdentities(List<CertificateEncryptionIdentity> pkiIdentities) {
		this.pkiIdentities.addAll(pkiIdentities);
		return this;
	}

	public EncryptionOptions addPkiIdentity(CertificateEncryptionIdentity pkiIdentity) {
		this.pkiIdentities.add(pkiIdentity);
		return this;
	}

	@Override
	public EncryptionOptions setPkiIdentities(List<CertificateEncryptionIdentity> pkiIdentities) {
		this.options.setPkiIdentities(pkiIdentities);
		return this;
	}

}
