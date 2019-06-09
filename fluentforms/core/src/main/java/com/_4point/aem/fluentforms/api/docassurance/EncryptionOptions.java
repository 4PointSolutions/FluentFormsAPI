package com._4point.aem.fluentforms.api.docassurance;

import java.util.List;

import com.adobe.fd.docassurance.client.api.DocAssuranceServiceOperationTypes;
import com.adobe.fd.encryption.client.CertificateEncryptionIdentity;
import com.adobe.fd.encryption.client.CertificateEncryptionOptionSpec;
import com.adobe.fd.encryption.client.PasswordEncryptionOptionSpec;

public interface EncryptionOptions {

	CertificateEncryptionOptionSpec getCertOptionSpec();

	String getCredentialAlias();

	DocAssuranceServiceOperationTypes getEncryptionType();

	PasswordEncryptionOptionSpec getPasswordEncryptionOptionSpec();

	List<CertificateEncryptionIdentity> getPkiIdentities();

	void setCertOptionSpec(CertificateEncryptionOptionSpec certOptionSpec);

	void setCredentialAlias(String credentialAlias);

	void setEncryptionType(DocAssuranceServiceOperationTypes encryptionType);

	void setPasswordEncryptionOptionSpec(PasswordEncryptionOptionSpec passwordEncryptionOptionSpec);

	void setPkiIdentities(List<CertificateEncryptionIdentity> pkiIdentities);

}