package com._4point.aem.fluentforms.impl.docassurance;

import java.util.List;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.docassurance.DocAssuranceService;
import com._4point.aem.fluentforms.api.docassurance.EncryptionOptions;
import com._4point.aem.fluentforms.api.docassurance.ReaderExtensionOptions;
import com.adobe.fd.docassurance.client.api.SignatureOptions;
import com.adobe.fd.encryption.client.EncryptionTypeResult;
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

public class DocAssuranceServiceImpl implements DocAssuranceService  {

	@Override
	public Document addInvisibleSignatureField(Document inDoc, String signatureFieldName, FieldMDPOptionSpec fieldMDPOptionsSpec,
			PDFSeedValueOptionSpec seedValueOptionsSpec, UnlockOptions unlockOptions) throws DocAssuranceServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document addSignatureField(Document inDoc, String signatureFieldName, Integer pageNo, PositionRectangle positionRectangle,
			FieldMDPOptionSpec fieldMDPOptionsSpec, PDFSeedValueOptionSpec seedValueOptionsSpec, UnlockOptions unlockOptions) throws DocAssuranceServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document applyDocumentTimeStamp(Document inDoc, VerificationTime verificationTime, ValidationPreferences validationPreferences,
			UnlockOptions unlockOptions) throws DocAssuranceServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document clearSignatureField(Document inDoc, String signatureFieldName, UnlockOptions unlockOptions) throws DocAssuranceServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PDFSignatureField getCertifyingSignatureField(Document inDoc, UnlockOptions unlockOptions) throws DocAssuranceServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GetUsageRightsResult getCredentialUsageRights(String credentialAlias)
			throws DocAssuranceServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GetUsageRightsResult getDocumentUsageRights(Document inDoc, UnlockOptions unlockOptions)
			throws DocAssuranceServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EncryptionTypeResult getPDFEncryption(Document inDoc) throws DocAssuranceServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PDFSignature getSignature(Document inDoc, String signatureFieldName, UnlockOptions unlockOptions) throws DocAssuranceServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PDFSignatureField> getSignatureFieldList(Document inDoc, UnlockOptions unlockOptions) throws DocAssuranceServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document modifySignatureField(Document inDoc, String signatureFieldName, PDFSignatureFieldProperties pdfSignatureFieldProperties,
			UnlockOptions unlockOptions) throws DocAssuranceServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document removePDFCertificateSecurity(Document inDoc, String alias) throws DocAssuranceServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document removePDFPasswordSecurity(Document inDoc, String password) throws DocAssuranceServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document removeSignatureField(Document inDoc, String signatureFieldName, UnlockOptions unlockOptions) throws DocAssuranceServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document removeUsageRights(Document inDoc, UnlockOptions unlockOptions) throws DocAssuranceServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document secureDocument(Document inDoc, EncryptionOptions encryptionOptions, SignatureOptions signatureOptions,
			ReaderExtensionOptions readerExtensionOptions, UnlockOptions unlockOptions) throws DocAssuranceServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PDFSignatureVerificationInfo verify(Document inDoc, String signatureFieldName, RevocationCheckStyle revocationCheckStyle,
			VerificationTime verificationTime, ValidationPreferences dssPrefs) throws DocAssuranceServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PDFDocumentVerificationInfo verifyDocument(Document inDoc, RevocationCheckStyle revocationCheckStyle, VerificationTime verificationTime,
			ValidationPreferences prefStore) throws DocAssuranceServiceException {
		// TODO Auto-generated method stub
		return null;
	}
}
