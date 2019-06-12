package com._4point.aem.fluentforms.impl.docassurance;

import java.util.List;
import java.util.Objects;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.docassurance.DocAssuranceService.DocAssuranceServiceException;
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

public class SafeDocAssuranceServiceAdapterWrapper implements TraditionalDocAssuranceService {

	private final TraditionalDocAssuranceService docAssuranceService;
	
	public SafeDocAssuranceServiceAdapterWrapper(TraditionalDocAssuranceService docAssuranceService) {
		super();
		this.docAssuranceService = docAssuranceService;
	}

	@Override
	public Document secureDocument(Document inDoc, EncryptionOptions encryptionOptions,
			SignatureOptions signatureOptions, ReaderExtensionOptions readerExtensionOptions,
			UnlockOptions unlockOptions) throws DocAssuranceServiceException {
		Objects.requireNonNull(inDoc, "inDoc cannot be null");
		return docAssuranceService.secureDocument(inDoc, encryptionOptions, signatureOptions, readerExtensionOptions, unlockOptions);
	}

	@Override
	public Document addInvisibleSignatureField(Document inDoc, String signatureFieldName,
			FieldMDPOptionSpec fieldMDPOptionsSpec, PDFSeedValueOptionSpec seedValueOptionsSpec,
			UnlockOptions unlockOptions) throws DocAssuranceServiceException {
		Objects.requireNonNull(inDoc, "input Document cannot be null.");
		Objects.requireNonNull(signatureFieldName, "signature field name cannot be null.");
		Objects.requireNonNull(fieldMDPOptionsSpec, "field MDP options cannot be null.");
		Objects.requireNonNull(seedValueOptionsSpec, "seed value options cannot be null.");
		Objects.requireNonNull(unlockOptions, "unlock options cannot be null.");
		return docAssuranceService.addInvisibleSignatureField(inDoc, signatureFieldName, fieldMDPOptionsSpec, seedValueOptionsSpec, unlockOptions);
	}

	@Override
	public Document addSignatureField(Document inDoc, String signatureFieldName, Integer pageNo,
			PositionRectangle positionRectangle, FieldMDPOptionSpec fieldMDPOptionsSpec,
			PDFSeedValueOptionSpec seedValueOptionsSpec, UnlockOptions unlockOptions)
			throws DocAssuranceServiceException {
		Objects.requireNonNull(inDoc, "input Document cannot be null.");
		Objects.requireNonNull(signatureFieldName, "signature field name cannot be null.");
		Objects.requireNonNull(pageNo, "page number cannot be null.");
		Objects.requireNonNull(positionRectangle, "position rectangle cannot be null.");
		Objects.requireNonNull(fieldMDPOptionsSpec, "field MDP options cannot be null.");
		Objects.requireNonNull(seedValueOptionsSpec, "seed value options cannot be null.");
		Objects.requireNonNull(unlockOptions, "unlock options cannot be null.");
		return docAssuranceService.addSignatureField(inDoc, signatureFieldName, pageNo, positionRectangle, fieldMDPOptionsSpec, seedValueOptionsSpec, unlockOptions);
	}

	@Override
	public Document clearSignatureField(Document inDoc, String signatureFieldName, UnlockOptions unlockOptions)
			throws DocAssuranceServiceException {
		Objects.requireNonNull(inDoc, "input Document cannot be null.");
		Objects.requireNonNull(signatureFieldName, "signature field name cannot be null.");
		Objects.requireNonNull(unlockOptions, "unlock options cannot be null.");
		return docAssuranceService.clearSignatureField(inDoc, signatureFieldName, unlockOptions);
	}

	@Override
	public PDFSignatureField getCertifyingSignatureField(Document inDoc, UnlockOptions unlockOptions)
			throws DocAssuranceServiceException {
		Objects.requireNonNull(inDoc, "input Document cannot be null.");
		Objects.requireNonNull(unlockOptions, "unlock options cannot be null.");
		return docAssuranceService.getCertifyingSignatureField(inDoc, unlockOptions);
	}

	@Override
	public PDFSignature getSignature(Document inDoc, String signatureFieldName, UnlockOptions unlockOptions)
			throws DocAssuranceServiceException {
		Objects.requireNonNull(inDoc, "input Document cannot be null.");
		Objects.requireNonNull(signatureFieldName, "signature field name cannot be null.");
		Objects.requireNonNull(unlockOptions, "unlock options cannot be null.");
		return docAssuranceService.getSignature(inDoc, signatureFieldName, unlockOptions);
	}

	@Override
	public List<PDFSignatureField> getSignatureFieldList(Document inDoc, UnlockOptions unlockOptions)
			throws DocAssuranceServiceException {
		Objects.requireNonNull(inDoc, "input Document cannot be null.");
		Objects.requireNonNull(unlockOptions, "unlock options cannot be null.");
		return docAssuranceService.getSignatureFieldList(inDoc, unlockOptions);
	}

	@Override
	public Document modifySignatureField(Document inDoc, String signatureFieldName,
			PDFSignatureFieldProperties pdfSignatureFieldProperties, UnlockOptions unlockOptions)
			throws DocAssuranceServiceException {
		Objects.requireNonNull(inDoc, "input Document cannot be null.");
		Objects.requireNonNull(signatureFieldName, "signature field name cannot be null.");
		Objects.requireNonNull(pdfSignatureFieldProperties, "pdf signature field properties cannot be null.");
		Objects.requireNonNull(unlockOptions, "unlock options cannot be null.");
		return docAssuranceService.modifySignatureField(inDoc, signatureFieldName, pdfSignatureFieldProperties, unlockOptions);
	}

	@Override
	public Document removeSignatureField(Document inDoc, String signatureFieldName, UnlockOptions unlockOptions)
			throws DocAssuranceServiceException {
		Objects.requireNonNull(inDoc, "input Document cannot be null.");
		Objects.requireNonNull(signatureFieldName, "signature field name cannot be null.");
		Objects.requireNonNull(unlockOptions, "unlock options cannot be null.");
		return docAssuranceService.removeSignatureField(inDoc, signatureFieldName, unlockOptions);
	}

	@Override
	public PDFSignatureVerificationInfo verify(Document inDoc, String signatureFieldName,
			RevocationCheckStyle revocationCheckStyle, VerificationTime verificationTime,
			ValidationPreferences dssPrefs) throws DocAssuranceServiceException {
		Objects.requireNonNull(inDoc, "input Document cannot be null.");
		Objects.requireNonNull(signatureFieldName, "signature field name cannot be null.");
		Objects.requireNonNull(revocationCheckStyle, "revocation check style cannot be null.");
		Objects.requireNonNull(verificationTime, "verification time cannot be null.");
		Objects.requireNonNull(dssPrefs, "validation preferences cannot be null.");
		return docAssuranceService.verify(inDoc, signatureFieldName, revocationCheckStyle, verificationTime, dssPrefs);
	}

	@Override
	public EncryptionTypeResult getPDFEncryption(Document inDoc) throws DocAssuranceServiceException {
		Objects.requireNonNull(inDoc, "input Document cannot be null.");
		return docAssuranceService.getPDFEncryption(inDoc);
	}

	@Override
	public Document removePDFCertificateSecurity(Document inDoc, String alias) throws DocAssuranceServiceException {
		Objects.requireNonNull(inDoc, "input Document cannot be null.");
		Objects.requireNonNull(alias, "credential alias cannot be null.");
		return docAssuranceService.removePDFCertificateSecurity(inDoc, alias);
	}

	@Override
	public Document removePDFPasswordSecurity(Document inDoc, String password) throws DocAssuranceServiceException {
		Objects.requireNonNull(inDoc, "input Document cannot be null.");
		Objects.requireNonNull(password, "pdf password cannot be null.");
		return docAssuranceService.removePDFPasswordSecurity(inDoc, password);
	}

	@Override
	public PDFDocumentVerificationInfo verifyDocument(Document inDoc, RevocationCheckStyle revocationCheckStyle,
			VerificationTime verificationTime, ValidationPreferences prefStore) throws DocAssuranceServiceException {
		Objects.requireNonNull(inDoc, "input Document cannot be null.");
		Objects.requireNonNull(revocationCheckStyle, "revocation check style cannot be null.");
		Objects.requireNonNull(verificationTime, "verification time cannot be null.");
		Objects.requireNonNull(prefStore, "validation preferences cannot be null.");
		return docAssuranceService.verifyDocument(inDoc, revocationCheckStyle, verificationTime, prefStore);
	}

	@Override
	public GetUsageRightsResult getCredentialUsageRights(String credentialAlias) throws DocAssuranceServiceException {
		Objects.requireNonNull(credentialAlias, "credential alias cannot be null.");
		return docAssuranceService.getCredentialUsageRights(credentialAlias);
	}

	@Override
	public GetUsageRightsResult getDocumentUsageRights(Document inDoc, UnlockOptions unlockOptions)
			throws DocAssuranceServiceException {
		Objects.requireNonNull(inDoc, "input Document cannot be null.");
		Objects.requireNonNull(unlockOptions, "unlock options cannot be null.");
		return docAssuranceService.getDocumentUsageRights(inDoc, unlockOptions);
	}

	@Override
	public Document removeUsageRights(Document inDoc, UnlockOptions unlockOptions) throws DocAssuranceServiceException {
		Objects.requireNonNull(inDoc, "input Document cannot be null.");
		Objects.requireNonNull(unlockOptions, "unlock options cannot be null.");
		return docAssuranceService.removeUsageRights(inDoc, unlockOptions);
	}
	
	@Override
	public Document applyDocumentTimeStamp(Document inDoc, VerificationTime verificationTime, ValidationPreferences validationPreferences,
			UnlockOptions unlockOptions) throws DocAssuranceServiceException {
		Objects.requireNonNull(inDoc, "input Document cannot be null.");
		Objects.requireNonNull(verificationTime, "verification time cannot be null.");
		Objects.requireNonNull(validationPreferences, "validation preferences cannot be null.");
		Objects.requireNonNull(unlockOptions, "unlock options cannot be null.");
		return docAssuranceService.applyDocumentTimeStamp(inDoc, verificationTime, validationPreferences, unlockOptions);
	}

}
