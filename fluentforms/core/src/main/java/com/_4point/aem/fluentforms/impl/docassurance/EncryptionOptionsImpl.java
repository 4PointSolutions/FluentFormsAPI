package com._4point.aem.fluentforms.impl.docassurance;

import java.util.ArrayList;
import java.util.List;

import com._4point.aem.fluentforms.api.docassurance.EncryptionOptions;
import com.adobe.fd.docassurance.client.api.DocAssuranceServiceOperationTypes;
import com.adobe.fd.encryption.client.CertificateEncryptionIdentity;
import com.adobe.fd.encryption.client.CertificateEncryptionOptionSpec;
import com.adobe.fd.encryption.client.PasswordEncryptionOptionSpec;

public class EncryptionOptionsImpl implements EncryptionOptions {
	private CertificateEncryptionOptionSpec certificateEncryptionOptionSpec = null;
	private String credentialAlias = null;
	private DocAssuranceServiceOperationTypes encryptionType = null;
	private PasswordEncryptionOptionSpec passwordEncryptionOptionSpec = null;
	private List<CertificateEncryptionIdentity> pkiIdentities = new ArrayList<>();

	@Override
	public CertificateEncryptionOptionSpec getCertOptionSpec() {
		return this.certificateEncryptionOptionSpec;
	}

	@Override
	public String getCredentialAlias() {
		return this.credentialAlias;
	}

	@Override
	public DocAssuranceServiceOperationTypes getEncryptionType() {
		return this.encryptionType;
	}

	@Override
	public PasswordEncryptionOptionSpec getPasswordEncryptionOptionSpec() {
		return this.passwordEncryptionOptionSpec;
	}

	@Override
	public List<CertificateEncryptionIdentity> getPkiIdentities() {
		return this.pkiIdentities;
	}

	@Override
	public EncryptionOptions setCertOptionSpec(CertificateEncryptionOptionSpec certOptionSpec) {
		this.certificateEncryptionOptionSpec = certOptionSpec;
		return this;
	}

	@Override
	public EncryptionOptions setCredentialAlias(String credentialAlias) {
		this.credentialAlias = credentialAlias;
		return this;
	}

	@Override
	public EncryptionOptions setEncryptionType(DocAssuranceServiceOperationTypes encryptionType) {
		this.encryptionType = encryptionType;
		return this;
	}

	@Override
	public EncryptionOptions setPasswordEncryptionOptionSpec(PasswordEncryptionOptionSpec passwordEncryptionOptionSpec) {
		this.passwordEncryptionOptionSpec = passwordEncryptionOptionSpec;
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
		this.pkiIdentities = pkiIdentities;
		return this;
	}

}
