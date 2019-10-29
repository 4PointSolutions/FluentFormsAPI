package com._4point.aem.fluentforms.impl.docassurance;

import java.util.List;
import java.util.Objects;

import org.apache.sling.api.resource.ResourceResolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com._4point.aem.fluentforms.impl.BuilderUtils.setIfNotNull;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.docassurance.DocAssuranceService.DocAssuranceServiceException;
import com._4point.aem.fluentforms.api.docassurance.EncryptionOptions;
import com._4point.aem.fluentforms.api.docassurance.ReaderExtensionOptions;
import com._4point.aem.fluentforms.impl.AdobeDocumentFactoryImpl;
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

public class AdobeDocAssuranceServiceAdapter implements TraditionalDocAssuranceService {
	private static final String APPLICATION_PDF = "application/pdf";

	private static final Logger log = LoggerFactory.getLogger(AdobeDocAssuranceServiceAdapter.class);

	private final DocumentFactory documentFactory;

	private final com.adobe.fd.docassurance.client.api.DocAssuranceService adobeDocAssuranceService;
	
	private final ResourceResolver resourceResolver;
	
	public AdobeDocAssuranceServiceAdapter(com.adobe.fd.docassurance.client.api.DocAssuranceService adobeDocAssuranceService, ResourceResolver resourceResolver) {
		super();
		this.documentFactory = DocumentFactory.getDefault();
		this.adobeDocAssuranceService = Objects.requireNonNull(adobeDocAssuranceService, "adobeDocAssuranceService is null.");
		this.resourceResolver = Objects.requireNonNull(resourceResolver, "resourceResolver is null");
	}

	public AdobeDocAssuranceServiceAdapter(DocumentFactory documentFactory, com.adobe.fd.docassurance.client.api.DocAssuranceService adobeDocAssuranceService, ResourceResolver resourceResolver) {
		super();
		this.documentFactory = Objects.requireNonNull(documentFactory, "documentFactory is null");
		this.adobeDocAssuranceService = Objects.requireNonNull(adobeDocAssuranceService, "adobeDocAssuranceService is null.");
		this.resourceResolver = Objects.requireNonNull(resourceResolver, "resourceResolver is null");
	}

	@Override
	public Document secureDocument(Document inDoc, EncryptionOptions encryptionOptions, SignatureOptions signatureOptions, ReaderExtensionOptions readerExtensionOptions,
			UnlockOptions unlockOptions) throws DocAssuranceServiceException {
		try {
//			log.info("secureDocument called");
//			com.adobe.fd.docassurance.client.api.EncryptionOptions eOptions = toAdobeEncryptionOptions(encryptionOptions);
//			return documentFactory.create(adobeDocAssuranceService.secureDocument(inDoc.getAdobeDocument(), eOptions, signatureOptions, toAdobeReaderExtensionOptions(readerExtensionOptions), unlockOptions));
			com.adobe.aemfd.docmanager.Document adobeDoc = adobeDocAssuranceService.secureDocument(AdobeDocumentFactoryImpl.getAdobeDocument(inDoc), null, null, toAdobeReaderExtensionOptions(readerExtensionOptions), null);
			adobeDoc.setContentType(APPLICATION_PDF);
			return documentFactory.create(adobeDoc);
		} catch (Exception e) {
			throw new DocAssuranceServiceException(e);
		}
	}

	@Override
	public Document addInvisibleSignatureField(Document inDoc, String signatureFieldName,
			FieldMDPOptionSpec fieldMDPOptionsSpec, PDFSeedValueOptionSpec seedValueOptionsSpec,
			UnlockOptions unlockOptions) throws DocAssuranceServiceException {
		try {
			return documentFactory.create(adobeDocAssuranceService.addInvisibleSignatureField(AdobeDocumentFactoryImpl.getAdobeDocument(inDoc), signatureFieldName, fieldMDPOptionsSpec, seedValueOptionsSpec, unlockOptions));
		} catch (Exception e) {
			throw new DocAssuranceServiceException(e);
		}
	}

	@Override
	public Document addSignatureField(Document inDoc, String signatureFieldName, Integer pageNo,
			PositionRectangle positionRectangle, FieldMDPOptionSpec fieldMDPOptionsSpec,
			PDFSeedValueOptionSpec seedValueOptionsSpec, UnlockOptions unlockOptions)
			throws DocAssuranceServiceException {
		try {
			return documentFactory.create(adobeDocAssuranceService.addSignatureField(AdobeDocumentFactoryImpl.getAdobeDocument(inDoc), signatureFieldName, pageNo, positionRectangle, fieldMDPOptionsSpec,
					seedValueOptionsSpec, unlockOptions));
		} catch (Exception e) {
			throw new DocAssuranceServiceException(e);
		}
	}

	@Override
	public Document clearSignatureField(Document inDoc, String signatureFieldName, UnlockOptions unlockOptions)
			throws DocAssuranceServiceException {
		try {
			return documentFactory.create(adobeDocAssuranceService.clearSignatureField(AdobeDocumentFactoryImpl.getAdobeDocument(inDoc), signatureFieldName, unlockOptions));
		} catch (Exception e) {
			throw new DocAssuranceServiceException(e);
		}
	}

	@Override
	public PDFSignatureField getCertifyingSignatureField(Document inDoc, UnlockOptions unlockOptions)
			throws DocAssuranceServiceException {
		try {
			return adobeDocAssuranceService.getCertifyingSignatureField(AdobeDocumentFactoryImpl.getAdobeDocument(inDoc), unlockOptions);
		} catch (Exception e) {
			throw new DocAssuranceServiceException(e);
		}
	}

	@Override
	public PDFSignature getSignature(Document inDoc, String signatureFieldName, UnlockOptions unlockOptions)
			throws DocAssuranceServiceException {
		try {
			return adobeDocAssuranceService.getSignature(AdobeDocumentFactoryImpl.getAdobeDocument(inDoc), signatureFieldName, unlockOptions);
		} catch (Exception e) {
			throw new DocAssuranceServiceException(e);
		}
	}

	@Override
	public List<PDFSignatureField> getSignatureFieldList(Document inDoc, UnlockOptions unlockOptions)
			throws DocAssuranceServiceException {
		try {
			return adobeDocAssuranceService.getSignatureFieldList(AdobeDocumentFactoryImpl.getAdobeDocument(inDoc), unlockOptions);
		} catch (Exception e) {
			throw new DocAssuranceServiceException(e);
		}
	}

	@Override
	public Document modifySignatureField(Document inDoc, String signatureFieldName,
			PDFSignatureFieldProperties pdfSignatureFieldProperties, UnlockOptions unlockOptions)
			throws DocAssuranceServiceException {
		try {
			return documentFactory.create(adobeDocAssuranceService.modifySignatureField(AdobeDocumentFactoryImpl.getAdobeDocument(inDoc), signatureFieldName, pdfSignatureFieldProperties, unlockOptions));
		} catch (Exception e) {
			throw new DocAssuranceServiceException(e);
		}
	}

	@Override
	public Document removeSignatureField(Document inDoc, String signatureFieldName, UnlockOptions unlockOptions)
			throws DocAssuranceServiceException {
		try {
			return documentFactory.create(adobeDocAssuranceService.removeSignatureField(AdobeDocumentFactoryImpl.getAdobeDocument(inDoc), signatureFieldName, unlockOptions));
		} catch (Exception e) {
			throw new DocAssuranceServiceException(e);
		}
	}

	@Override
	public PDFSignatureVerificationInfo verify(Document inDoc, String signatureFieldName, RevocationCheckStyle revocationCheckStyle, VerificationTime verificationTime,
			ValidationPreferences dssPrefs) throws DocAssuranceServiceException {
		try {
			return adobeDocAssuranceService.verify(AdobeDocumentFactoryImpl.getAdobeDocument(inDoc), signatureFieldName, revocationCheckStyle, verificationTime, dssPrefs, this.resourceResolver);
		} catch (Exception e) {
			throw new DocAssuranceServiceException(e);
		}
	}

	@Override
	public EncryptionTypeResult getPDFEncryption(Document inDoc) throws DocAssuranceServiceException {
		try {
			return adobeDocAssuranceService.getPDFEncryption(AdobeDocumentFactoryImpl.getAdobeDocument(inDoc));
		} catch (Exception e) {
			throw new DocAssuranceServiceException(e);
		}
	}

	@Override
	public Document removePDFCertificateSecurity(Document inDoc, String alias) throws DocAssuranceServiceException {
		try {
			return documentFactory.create(adobeDocAssuranceService.removePDFCertificateSecurity(AdobeDocumentFactoryImpl.getAdobeDocument(inDoc), alias, this.resourceResolver));
		} catch (Exception e) {
			throw new DocAssuranceServiceException(e);
		}
	}

	@Override
	public Document removePDFPasswordSecurity(Document inDoc, String password) throws DocAssuranceServiceException {
		try {
			return documentFactory.create(adobeDocAssuranceService.removePDFPasswordSecurity(AdobeDocumentFactoryImpl.getAdobeDocument(inDoc), password));
		} catch (Exception e) {
			throw new DocAssuranceServiceException(e);
		}
	}

	@Override
	public PDFDocumentVerificationInfo verifyDocument(Document inDoc, RevocationCheckStyle revocationCheckStyle,
			VerificationTime verificationTime, ValidationPreferences prefStore) throws DocAssuranceServiceException {
		try {
			return adobeDocAssuranceService.verifyDocument(AdobeDocumentFactoryImpl.getAdobeDocument(inDoc), revocationCheckStyle, verificationTime, prefStore, this.resourceResolver);
		} catch (Exception e) {
			throw new DocAssuranceServiceException(e);
		}
	}

	@Override
	public GetUsageRightsResult getCredentialUsageRights(String credentialAlias) throws DocAssuranceServiceException {
		try {
			return adobeDocAssuranceService.getCredentialUsageRights(credentialAlias, this.resourceResolver);
		} catch (com.adobe.fd.readerextensions.client.ReaderExtensionsException e) {
			throw new DocAssuranceServiceException(e);
		}
	}

	@Override
	public GetUsageRightsResult getDocumentUsageRights(Document inDocument, UnlockOptions unlockOptions)
			throws DocAssuranceServiceException {
		try {
			return adobeDocAssuranceService.getDocumentUsageRights(AdobeDocumentFactoryImpl.getAdobeDocument(inDocument), unlockOptions);
		} catch (Exception e) {
			throw new DocAssuranceServiceException(e);
		}
	}

	@Override
	public Document removeUsageRights(Document inDoc, UnlockOptions unlockOptions) throws DocAssuranceServiceException {
		try {
			return documentFactory.create(adobeDocAssuranceService.removeUsageRights(AdobeDocumentFactoryImpl.getAdobeDocument(inDoc), unlockOptions));
		} catch (Exception e) {
			throw new DocAssuranceServiceException(e);
		}
	}

	@Override
	public Document applyDocumentTimeStamp(Document inDoc, VerificationTime verificationTime, ValidationPreferences validationPreferences, UnlockOptions unlockOptions)
			throws DocAssuranceServiceException {
		try {
			return documentFactory.create(adobeDocAssuranceService.applyDocumentTimeStamp(AdobeDocumentFactoryImpl.getAdobeDocument(inDoc), verificationTime, validationPreferences, this.resourceResolver, unlockOptions));
		} catch (Exception e) {
			throw new DocAssuranceServiceException(e);
		}
	}
	
	public com.adobe.fd.docassurance.client.api.EncryptionOptions toAdobeEncryptionOptions(EncryptionOptions options) {
		com.adobe.fd.docassurance.client.api.EncryptionOptions adobeOptions = com.adobe.fd.docassurance.client.api.EncryptionOptions.getInstance();
		// log.info("About to call options.getCertOptionSpec().  Currently generates a NullPointerException!!!");
		setIfNotNull(adobeOptions::setCertOptionSpec, options.getCertOptionSpec());
		setIfNotNull(adobeOptions::setEncryptionType, options.getEncryptionType());
		setIfNotNull(adobeOptions::setPasswordEncryptionOptionSpec, options.getPasswordEncryptionOptionSpec());
		setIfNotNull(adobeOptions::setPkiIdentities, options.getPkiIdentities());
		adobeOptions.setResourceResolver(this.resourceResolver);
		return adobeOptions;
	}
	
	public com.adobe.fd.docassurance.client.api.ReaderExtensionOptions toAdobeReaderExtensionOptions(ReaderExtensionOptions options) {
		com.adobe.fd.docassurance.client.api.ReaderExtensionOptions adobeOptions = com.adobe.fd.docassurance.client.api.ReaderExtensionOptions.getInstance();
		setIfNotNull(adobeOptions::setCredentialAlias, options.getCredentialAlias());
		setIfNotNull(adobeOptions::setReOptions, options.getReOptions());
		adobeOptions.setResourceResolver(this.resourceResolver);
		return adobeOptions;
	}

}
