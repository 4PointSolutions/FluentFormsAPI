package com._4point.aem.fluentforms.api.docassurance;

import java.util.List;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.impl.docassurance.DocAssuranceServiceImpl.SecureDocumentArgumentBuilder;
import com.adobe.fd.docassurance.client.api.SignatureOptions;
import com.adobe.fd.encryption.client.EncryptionTypeResult;
import com.adobe.fd.encryption.client.PasswordEncryptionCompatability;
import com.adobe.fd.encryption.client.PasswordEncryptionOption;
import com.adobe.fd.readerextensions.client.GetUsageRightsResult;
import com.adobe.fd.signatures.client.types.FieldMDPOptionSpec;
import com.adobe.fd.signatures.client.types.PDFDocumentVerificationInfo;
import com.adobe.fd.signatures.client.types.PDFSeedValueOptionSpec;
import com.adobe.fd.signatures.client.types.PDFSignature;
import com.adobe.fd.signatures.client.types.PDFSignatureField;
import com.adobe.fd.signatures.client.types.PDFSignatureFieldProperties;
import com.adobe.fd.signatures.client.types.PDFSignatureVerificationInfo;
import com.adobe.fd.signatures.client.types.PositionRectangle;
import com.adobe.fd.signatures.client.types.VerificationTime;
import com.adobe.fd.signatures.pdf.inputs.UnlockOptions;
import com.adobe.fd.signatures.pdf.inputs.ValidationPreferences;
import com.adobe.fd.signatures.pki.client.types.common.RevocationCheckStyle;

public interface DocAssuranceService {

	Document secureDocument(Document inDoc, EncryptionOptions encryptionOptions, SignatureOptions signatureOptions, ReaderExtensionOptions readerExtensionOptions,
			UnlockOptions unlockOptions) throws DocAssuranceServiceException;

	SecureDocumentArgumentBuilder secureDocument();
	
	Document addInvisibleSignatureField(Document inDoc, String signatureFieldName, FieldMDPOptionSpec fieldMDPOptionsSpec, PDFSeedValueOptionSpec seedValueOptionsSpec,
			UnlockOptions unlockOptions) throws DocAssuranceServiceException;

	Document addSignatureField(Document inDoc, String signatureFieldName, Integer pageNo, PositionRectangle positionRectangle, FieldMDPOptionSpec fieldMDPOptionsSpec,
			PDFSeedValueOptionSpec seedValueOptionsSpec, UnlockOptions unlockOptions) throws DocAssuranceServiceException;

	Document clearSignatureField(Document inDoc, String signatureFieldName, UnlockOptions unlockOptions) throws DocAssuranceServiceException;

	PDFSignatureField getCertifyingSignatureField(Document inDoc, UnlockOptions unlockOptions) throws DocAssuranceServiceException;

	PDFSignature getSignature(Document inDoc, String signatureFieldName, UnlockOptions unlockOptions) throws DocAssuranceServiceException;

	List<PDFSignatureField> getSignatureFieldList(Document inDoc, UnlockOptions unlockOptions) throws DocAssuranceServiceException;

	Document modifySignatureField(Document inDoc, String signatureFieldName, PDFSignatureFieldProperties pdfSignatureFieldProperties, UnlockOptions unlockOptions)
			throws DocAssuranceServiceException;

	Document removeSignatureField(Document inDoc, String signatureFieldName, UnlockOptions unlockOptions) throws DocAssuranceServiceException;

	PDFSignatureVerificationInfo verify(Document inDoc, String signatureFieldName, RevocationCheckStyle revocationCheckStyle, VerificationTime verificationTime,
			ValidationPreferences dssPrefs) throws DocAssuranceServiceException;

	EncryptionTypeResult getPDFEncryption(Document inDoc) throws DocAssuranceServiceException;

	Document removePDFCertificateSecurity(Document inDoc, String alias) throws DocAssuranceServiceException;

	Document removePDFPasswordSecurity(Document inDoc, String password) throws DocAssuranceServiceException;

	GetUsageRightsResult getCredentialUsageRights(String credentialAlias) throws DocAssuranceServiceException;

	GetUsageRightsResult getDocumentUsageRights(Document inDoc, UnlockOptions unlockOptions) throws DocAssuranceServiceException;

	Document removeUsageRights(Document inDoc, UnlockOptions unlockOptions) throws DocAssuranceServiceException;

	PDFDocumentVerificationInfo verifyDocument(Document inDoc, RevocationCheckStyle revocationCheckStyle, VerificationTime verificationTime, ValidationPreferences prefStore)
			throws DocAssuranceServiceException;

	Document applyDocumentTimeStamp(Document inDoc, VerificationTime verificationTime, ValidationPreferences validationPreferences, UnlockOptions unlockOptions)
			throws DocAssuranceServiceException;

	@SuppressWarnings("serial")
	public static class DocAssuranceServiceException extends Exception {

		public DocAssuranceServiceException() {
			super();
		}

		public DocAssuranceServiceException(String message, Throwable cause) {
			super(message, cause);
		}

		public DocAssuranceServiceException(String message) {
			super(message);
		}

		public DocAssuranceServiceException(Throwable cause) {
			super(cause);
		}		
	}
	
	public static interface UnlockOptionsSetter {

		public UnlockOptionsSetter setAlias(String alias);

		public UnlockOptionsSetter setPassword(String password);
	}
	
	public static interface PasswordEncryptionOptionSpecSetter {
		
		public PasswordEncryptionOptionSpecSetter setCompatability(PasswordEncryptionCompatability compat);
		
		public PasswordEncryptionOptionSpecSetter setEncryptOption(PasswordEncryptionOption encoptions);
		
		public PasswordEncryptionOptionSpecSetter setDocumentOpenPassword(String password);
		
		public PasswordEncryptionOptionSpecSetter setPermissionPassword(String password);
		
		public PasswordEncryptionOptionSpecSetter setPermissionsRequested(@SuppressWarnings("rawtypes") List p);
		
	}

}