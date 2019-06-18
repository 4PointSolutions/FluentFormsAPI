package com._4point.aem.fluentforms.testing.docassurance;

import java.util.List;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.docassurance.DocAssuranceService.DocAssuranceServiceException;
import com._4point.aem.fluentforms.api.docassurance.EncryptionOptions;
import com._4point.aem.fluentforms.api.docassurance.ReaderExtensionOptions;
import com._4point.aem.fluentforms.impl.docassurance.TraditionalDocAssuranceService;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;
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

public class MockTraditionalDocAssuranceService implements TraditionalDocAssuranceService {
	private final DocumentFactory documentFactory;
	private final Document DUMMY_DOCUMENT;
	private Document secureDocumentResult = null;
	private SecureDocumentArgs secureDocumentArgs = null;
	
	public MockTraditionalDocAssuranceService() {
		super();
		this.documentFactory = new MockDocumentFactory();
		this.DUMMY_DOCUMENT = documentFactory.create(new byte[0]);
	}
	
	public MockTraditionalDocAssuranceService(DocumentFactory documentFactory) {
		super();
		this.documentFactory = documentFactory;
		this.DUMMY_DOCUMENT = documentFactory.create(new byte[0]);
	}

	public static MockTraditionalDocAssuranceService createSecureDocumentMock(Document secureDocumentResult) {
		return new MockTraditionalDocAssuranceService().setSecureDocumentResult(secureDocumentResult);
	}

	public static MockTraditionalDocAssuranceService createSecureDocumentMock(DocumentFactory documentFactory, Document secureDocumentResult) {
		return new MockTraditionalDocAssuranceService(documentFactory).setSecureDocumentResult(secureDocumentResult);
	}

	public MockTraditionalDocAssuranceService setSecureDocumentResult(Document secureDocumentResult) {
		this.secureDocumentResult = secureDocumentResult;
		return this;
	}

	public SecureDocumentArgs getSecureDocumentArgs() {
		return secureDocumentArgs;
	}

	public static class SecureDocumentArgs {
		private final Document inDoc;
		private final EncryptionOptions encryptionOptions;
		private final SignatureOptions signatureOptions;
		private final ReaderExtensionOptions readerExtensionOptions;
		private final UnlockOptions unlockOptions;
		
		public SecureDocumentArgs(Document inDoc, EncryptionOptions encryptionOptions, SignatureOptions signatureOptions, ReaderExtensionOptions readerExtensionOptions,
				UnlockOptions unlockOptions) {
			super();
			this.inDoc = inDoc;
			this.encryptionOptions = encryptionOptions;
			this.signatureOptions = signatureOptions;
			this.readerExtensionOptions = readerExtensionOptions;
			this.unlockOptions = unlockOptions;
		}

		public Document getInDoc() {
			return inDoc;
		}

		public EncryptionOptions getEncryptionOptions() {
			return encryptionOptions;
		}

		public SignatureOptions getSignatureOptions() {
			return signatureOptions;
		}

		public ReaderExtensionOptions getReaderExtensionOptions() {
			return readerExtensionOptions;
		}

		public UnlockOptions getUnlockOptions() {
			return unlockOptions;
		}
	}

	@Override
	public Document secureDocument(Document inDoc, EncryptionOptions encryptionOptions, SignatureOptions signatureOptions, ReaderExtensionOptions readerExtensionOptions,
			UnlockOptions unlockOptions) throws DocAssuranceServiceException {
		this.secureDocumentArgs = new SecureDocumentArgs(inDoc, encryptionOptions, signatureOptions, readerExtensionOptions, unlockOptions);
		return secureDocumentResult == null ? DUMMY_DOCUMENT : secureDocumentResult;
	}

	@Override
	public Document addInvisibleSignatureField(Document inDoc, String signatureFieldName,
			FieldMDPOptionSpec fieldMDPOptionsSpec, PDFSeedValueOptionSpec seedValueOptionsSpec,
			UnlockOptions unlockOptions) throws DocAssuranceServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document addSignatureField(Document inDoc, String signatureFieldName, Integer pageNo,
			PositionRectangle positionRectangle, FieldMDPOptionSpec fieldMDPOptionsSpec,
			PDFSeedValueOptionSpec seedValueOptionsSpec, UnlockOptions unlockOptions)
			throws DocAssuranceServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document clearSignatureField(Document inDoc, String signatureFieldName, UnlockOptions unlockOptions)
			throws DocAssuranceServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PDFSignatureField getCertifyingSignatureField(Document inDoc, UnlockOptions unlockOptions)
			throws DocAssuranceServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PDFSignature getSignature(Document inDoc, String signatureFieldName, UnlockOptions unlockOptions)
			throws DocAssuranceServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PDFSignatureField> getSignatureFieldList(Document inDoc, UnlockOptions unlockOptions)
			throws DocAssuranceServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document modifySignatureField(Document inDoc, String signatureFieldName,
			PDFSignatureFieldProperties pdfSignatureFieldProperties, UnlockOptions unlockOptions)
			throws DocAssuranceServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document removeSignatureField(Document inDoc, String signatureFieldName, UnlockOptions unlockOptions)
			throws DocAssuranceServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PDFSignatureVerificationInfo verify(Document inDoc, String signatureFieldName,
			RevocationCheckStyle revocationCheckStyle, VerificationTime verificationTime,
			ValidationPreferences dssPrefs) throws DocAssuranceServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EncryptionTypeResult getPDFEncryption(Document inDoc) throws DocAssuranceServiceException {
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
	public PDFDocumentVerificationInfo verifyDocument(Document inDoc, RevocationCheckStyle revocationCheckStyle,
			VerificationTime verificationTime, ValidationPreferences prefStore) throws DocAssuranceServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GetUsageRightsResult getCredentialUsageRights(String credentialAlias) throws DocAssuranceServiceException {
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
	public Document removeUsageRights(Document inDoc, UnlockOptions unlockOptions) throws DocAssuranceServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document applyDocumentTimeStamp(Document inDoc, VerificationTime verificationTime,
			ValidationPreferences validationPreferences, UnlockOptions unlockOptions)
			throws DocAssuranceServiceException {
		// TODO Auto-generated method stub
		return null;
	}
}
