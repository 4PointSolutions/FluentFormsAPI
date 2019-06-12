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
	
	EncryptionOptions setCertOptionSpec(CertificateEncryptionOptionSpec certOptionSpec);
	
	EncryptionOptions setCredentialAlias(String credentialAlias);
	
	EncryptionOptions setEncryptionType(DocAssuranceServiceOperationTypes encryptionType);
	
	EncryptionOptions setPasswordEncryptionOptionSpec(PasswordEncryptionOptionSpec passwordEncryptionOptionSpec);
	
	EncryptionOptions setPkiIdentities(List<CertificateEncryptionIdentity> pkiIdentities);

}