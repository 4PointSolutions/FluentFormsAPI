package com._4point.aem.fluentforms.impl.docassurance;

import java.util.List;

import com._4point.aem.fluentforms.api.docassurance.EncryptionOptions;
import com.adobe.fd.docassurance.client.api.DocAssuranceServiceOperationTypes;
import com.adobe.fd.encryption.client.CertificateEncryptionIdentity;
import com.adobe.fd.encryption.client.CertificateEncryptionOptionSpec;
import com.adobe.fd.encryption.client.PasswordEncryptionOptionSpec;

public class EncryptionOptionsImpl implements EncryptionOptions {

	@Override
	public CertificateEncryptionOptionSpec getCertOptionSpec() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCredentialAlias() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DocAssuranceServiceOperationTypes getEncryptionType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PasswordEncryptionOptionSpec getPasswordEncryptionOptionSpec() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CertificateEncryptionIdentity> getPkiIdentities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCertOptionSpec(CertificateEncryptionOptionSpec certOptionSpec) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setCredentialAlias(String credentialAlias) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setEncryptionType(DocAssuranceServiceOperationTypes encryptionType) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setPasswordEncryptionOptionSpec(PasswordEncryptionOptionSpec passwordEncryptionOptionSpec) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setPkiIdentities(List<CertificateEncryptionIdentity> pkiIdentities) {
		// TODO Auto-generated method stub
	}

}
