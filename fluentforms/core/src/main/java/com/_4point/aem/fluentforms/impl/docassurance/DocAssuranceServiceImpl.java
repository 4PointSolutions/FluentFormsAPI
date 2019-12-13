package com._4point.aem.fluentforms.impl.docassurance;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.Transformable;
import com._4point.aem.fluentforms.api.docassurance.DocAssuranceService;
import com._4point.aem.fluentforms.api.docassurance.EncryptionOptions;
import com._4point.aem.fluentforms.api.docassurance.ReaderExtensionOptions;
import com._4point.aem.fluentforms.impl.docassurance.SafeDocAssuranceServiceAdapterWrapper;
import com._4point.aem.fluentforms.impl.docassurance.TraditionalDocAssuranceService;
import com.adobe.fd.docassurance.client.api.DocAssuranceServiceOperationTypes;
import com.adobe.fd.docassurance.client.api.SignatureOptions;
import com.adobe.fd.encryption.client.CertificateEncryptionIdentity;
import com.adobe.fd.encryption.client.CertificateEncryptionOptionSpec;
import com.adobe.fd.encryption.client.EncryptionTypeResult;
import com.adobe.fd.encryption.client.PasswordEncryptionCompatability;
import com.adobe.fd.encryption.client.PasswordEncryptionOption;
import com.adobe.fd.encryption.client.PasswordEncryptionOptionSpec;
import com.adobe.fd.readerextensions.client.GetUsageRightsResult;
import com.adobe.fd.readerextensions.client.ReaderExtensionsOptionSpec;
import com.adobe.fd.readerextensions.client.UsageRights;
import com.adobe.fd.signatures.client.types.CertificateSeedValueOptionSpec;
import com.adobe.fd.signatures.client.types.FieldMDPAction;
import com.adobe.fd.signatures.client.types.FieldMDPOptionSpec;
import com.adobe.fd.signatures.client.types.MDPPermissions;
import com.adobe.fd.signatures.client.types.PDFDocumentVerificationInfo;
import com.adobe.fd.signatures.client.types.PDFSeedValueOptionSpec;
import com.adobe.fd.signatures.client.types.PDFSignature;
import com.adobe.fd.signatures.client.types.PDFSignatureField;
import com.adobe.fd.signatures.client.types.PDFSignatureFieldProperties;
import com.adobe.fd.signatures.client.types.PDFSignatureVerificationInfo;
import com.adobe.fd.signatures.client.types.PDFTimeStampSeed;
import com.adobe.fd.signatures.client.types.PositionRectangle;
import com.adobe.fd.signatures.client.types.VerificationTime;
import com.adobe.fd.signatures.pdf.inputs.CredentialContext;
import com.adobe.fd.signatures.pdf.inputs.DSSPreferences;
import com.adobe.fd.signatures.pdf.inputs.DSSPreferencesImpl;
import com.adobe.fd.signatures.pdf.inputs.JavascriptPreferences;
import com.adobe.fd.signatures.pdf.inputs.PDFSignatureAppearenceOptions;
import com.adobe.fd.signatures.pdf.inputs.UnlockOptions;
import com.adobe.fd.signatures.pdf.inputs.ValidationPreferences;
import com.adobe.fd.signatures.pki.client.spi.PKICredential;
import com.adobe.fd.signatures.pki.client.types.common.HashAlgorithm;
import com.adobe.fd.signatures.pki.client.types.common.OCSPURLtoConsultOption;
import com.adobe.fd.signatures.pki.client.types.common.RevocationCheckOrder;
import com.adobe.fd.signatures.pki.client.types.common.RevocationCheckStyle;
import com.adobe.fd.signatures.pki.client.types.prefs.CRLPreferencesImpl;
import com.adobe.fd.signatures.pki.client.types.prefs.GeneralPreferencesImpl;
import com.adobe.fd.signatures.pki.client.types.prefs.OCSPPreferencesImpl;
import com.adobe.fd.signatures.pki.client.types.prefs.PKIPreferences;
import com.adobe.fd.signatures.pki.client.types.prefs.PKIPreferencesImpl;
import com.adobe.fd.signatures.pki.client.types.prefs.PathValidationPreferencesImpl;
import com.adobe.fd.signatures.pki.client.types.prefs.TSPPreferencesImpl;
import com.adobe.fd.signatures.pki.client.types.prefs.TransportPreferencesImpl;

public class DocAssuranceServiceImpl implements DocAssuranceService  {

	private final TraditionalDocAssuranceService adobeDocAssuranceService;

	public DocAssuranceServiceImpl(TraditionalDocAssuranceService adobeDocAssuranceService) {
		super();
		this.adobeDocAssuranceService = new SafeDocAssuranceServiceAdapterWrapper(adobeDocAssuranceService);
	}

	@Override
	public Document addInvisibleSignatureField(Document inDoc, String signatureFieldName, FieldMDPOptionSpec fieldMDPOptionsSpec,
			PDFSeedValueOptionSpec seedValueOptionsSpec, UnlockOptions unlockOptions) throws DocAssuranceServiceException {
		Objects.requireNonNull(inDoc, "input Document cannot be null.");
		Objects.requireNonNull(signatureFieldName, "signature field name cannot be null.");
		Objects.requireNonNull(fieldMDPOptionsSpec, "field MDP options cannot be null.");
		Objects.requireNonNull(seedValueOptionsSpec, "seed value options cannot be null.");
		Objects.requireNonNull(unlockOptions, "unlock options cannot be null.");
		try {
			return adobeDocAssuranceService.addInvisibleSignatureField(inDoc, signatureFieldName, fieldMDPOptionsSpec, seedValueOptionsSpec, unlockOptions);
		} catch (Exception e) {
			throw new DocAssuranceServiceException(e);
		}
	}

	@Override
	public Document addSignatureField(Document inDoc, String signatureFieldName, Integer pageNo, PositionRectangle positionRectangle,
			FieldMDPOptionSpec fieldMDPOptionsSpec, PDFSeedValueOptionSpec seedValueOptionsSpec, UnlockOptions unlockOptions) throws DocAssuranceServiceException {
		Objects.requireNonNull(inDoc, "input Document cannot be null.");
		Objects.requireNonNull(signatureFieldName, "signature field name cannot be null.");
		Objects.requireNonNull(pageNo, "page number cannot be null.");
		Objects.requireNonNull(positionRectangle, "position rectangle cannot be null.");
		Objects.requireNonNull(fieldMDPOptionsSpec, "field MDP options cannot be null.");
		Objects.requireNonNull(seedValueOptionsSpec, "seed value options cannot be null.");
		Objects.requireNonNull(unlockOptions, "unlock options cannot be null.");
		try {
			return adobeDocAssuranceService.addSignatureField(inDoc, signatureFieldName, pageNo, positionRectangle, fieldMDPOptionsSpec, seedValueOptionsSpec, unlockOptions);
		} catch (Exception e) {
			throw new DocAssuranceServiceException(e);
		}
	}

	@Override
	public Document clearSignatureField(Document inDoc, String signatureFieldName, UnlockOptions unlockOptions) throws DocAssuranceServiceException {
		Objects.requireNonNull(inDoc, "input Document cannot be null.");
		Objects.requireNonNull(signatureFieldName, "signature field name cannot be null.");
		Objects.requireNonNull(unlockOptions, "unlock options cannot be null.");
		try {
			return adobeDocAssuranceService.clearSignatureField(inDoc, signatureFieldName, unlockOptions);
		} catch (Exception e) {
			throw new DocAssuranceServiceException(e);
		}
	}

	@Override
	public PDFSignatureField getCertifyingSignatureField(Document inDoc, UnlockOptions unlockOptions) throws DocAssuranceServiceException {
		Objects.requireNonNull(inDoc, "input Document cannot be null.");
		Objects.requireNonNull(unlockOptions, "unlock options cannot be null.");
		try {
			return adobeDocAssuranceService.getCertifyingSignatureField(inDoc, unlockOptions);
		} catch (Exception e) {
			throw new DocAssuranceServiceException(e);
		}
	}

	@Override
	public PDFSignature getSignature(Document inDoc, String signatureFieldName, UnlockOptions unlockOptions) throws DocAssuranceServiceException {
		Objects.requireNonNull(inDoc, "input Document cannot be null.");
		Objects.requireNonNull(signatureFieldName, "signature field name cannot be null.");
		Objects.requireNonNull(unlockOptions, "unlock options cannot be null.");
		try {
			return adobeDocAssuranceService.getSignature(inDoc, signatureFieldName, unlockOptions);
		} catch (Exception e) {
			throw new DocAssuranceServiceException(e);
		}
	}

	@Override
	public List<PDFSignatureField> getSignatureFieldList(Document inDoc, UnlockOptions unlockOptions) throws DocAssuranceServiceException {
		Objects.requireNonNull(inDoc, "input Document cannot be null.");
		Objects.requireNonNull(unlockOptions, "unlock options cannot be null.");
		try {
			return adobeDocAssuranceService.getSignatureFieldList(inDoc, unlockOptions);
		} catch (Exception e) {
			throw new DocAssuranceServiceException(e);
		}
	}

	@Override
	public Document modifySignatureField(Document inDoc, String signatureFieldName, PDFSignatureFieldProperties pdfSignatureFieldProperties,
			UnlockOptions unlockOptions) throws DocAssuranceServiceException {
		Objects.requireNonNull(inDoc, "input Document cannot be null.");
		Objects.requireNonNull(signatureFieldName, "signature field name cannot be null.");
		Objects.requireNonNull(pdfSignatureFieldProperties, "pdf signature field properties cannot be null.");
		Objects.requireNonNull(unlockOptions, "unlock options cannot be null.");
		try {
			return adobeDocAssuranceService.modifySignatureField(inDoc, signatureFieldName, pdfSignatureFieldProperties, unlockOptions);
		} catch (Exception e) {
			throw new DocAssuranceServiceException(e);
		}
	}

	@Override
	public Document removeSignatureField(Document inDoc, String signatureFieldName, UnlockOptions unlockOptions) throws DocAssuranceServiceException {
		Objects.requireNonNull(inDoc, "input Document cannot be null.");
		Objects.requireNonNull(signatureFieldName, "signature field name cannot be null.");
		Objects.requireNonNull(unlockOptions, "unlock options cannot be null.");
		try {
			return adobeDocAssuranceService.removeSignatureField(inDoc, signatureFieldName, unlockOptions);
		} catch (Exception e) {
			throw new DocAssuranceServiceException(e);
		}
	}

	@Override
	public PDFSignatureVerificationInfo verify(Document inDoc, String signatureFieldName, RevocationCheckStyle revocationCheckStyle,
			VerificationTime verificationTime, ValidationPreferences dssPrefs) throws DocAssuranceServiceException {
		Objects.requireNonNull(inDoc, "input Document cannot be null.");
		Objects.requireNonNull(signatureFieldName, "signature field name cannot be null.");
		Objects.requireNonNull(revocationCheckStyle, "revocation check style cannot be null.");
		Objects.requireNonNull(verificationTime, "verification time cannot be null.");
		Objects.requireNonNull(dssPrefs, "validation preferences cannot be null.");
		return adobeDocAssuranceService.verify(inDoc, signatureFieldName, revocationCheckStyle, verificationTime, dssPrefs);
	}

	@Override
	public EncryptionTypeResult getPDFEncryption(Document inDoc) throws DocAssuranceServiceException {
		Objects.requireNonNull(inDoc, "input Document cannot be null.");
		return adobeDocAssuranceService.getPDFEncryption(inDoc);
	}

	@Override
	public Document removePDFCertificateSecurity(Document inDoc, String alias) throws DocAssuranceServiceException {
		Objects.requireNonNull(inDoc, "input Document cannot be null.");
		Objects.requireNonNull(alias, "credential alias cannot be null.");
		return adobeDocAssuranceService.removePDFCertificateSecurity(inDoc, alias);
	}

	@Override
	public Document removePDFPasswordSecurity(Document inDoc, String password) throws DocAssuranceServiceException {
		Objects.requireNonNull(inDoc, "input Document cannot be null.");
		Objects.requireNonNull(password, "pdf password cannot be null.");
		return adobeDocAssuranceService.removePDFPasswordSecurity(inDoc, password);
	}

	@Override
	public GetUsageRightsResult getCredentialUsageRights(String credentialAlias)
			throws DocAssuranceServiceException {
		Objects.requireNonNull(credentialAlias, "credential alias cannot be null.");
		return adobeDocAssuranceService.getCredentialUsageRights(credentialAlias);
	}

	@Override
	public GetUsageRightsResult getDocumentUsageRights(Document inDoc, UnlockOptions unlockOptions)
			throws DocAssuranceServiceException {
		Objects.requireNonNull(inDoc, "input Document cannot be null.");
		Objects.requireNonNull(unlockOptions, "unlock options cannot be null.");
		return adobeDocAssuranceService.getDocumentUsageRights(inDoc, unlockOptions);
	}

	@Override
	public Document removeUsageRights(Document inDoc, UnlockOptions unlockOptions) throws DocAssuranceServiceException {
		Objects.requireNonNull(inDoc, "input Document cannot be null.");
		Objects.requireNonNull(unlockOptions, "unlock options cannot be null.");
		return adobeDocAssuranceService.removeUsageRights(inDoc, unlockOptions);
	}

	@Override
	public Document secureDocument(Document inDoc, EncryptionOptions encryptionOptions, SignatureOptions signatureOptions,
			ReaderExtensionOptions readerExtensionOptions, UnlockOptions unlockOptions) throws DocAssuranceServiceException {
		Objects.requireNonNull(inDoc, "input Document cannot be null.");
		// All other options can be null individually, but not all together
		if (encryptionOptions == null && signatureOptions == null && readerExtensionOptions == null && unlockOptions == null) {
			throw new IllegalArgumentException("Must supply at least one set of options in SecureDocument call (all options arguments were null).");
		}
		return adobeDocAssuranceService.secureDocument(inDoc, encryptionOptions, signatureOptions, readerExtensionOptions, unlockOptions);
	}

	@Override
	public SecureDocumentArgumentBuilder secureDocument() {
		return new SecureDocumentArgumentBuilder();
	}

	@Override
	public PDFDocumentVerificationInfo verifyDocument(Document inDoc, RevocationCheckStyle revocationCheckStyle, VerificationTime verificationTime,
			ValidationPreferences prefStore) throws DocAssuranceServiceException {
		Objects.requireNonNull(inDoc, "input Document cannot be null.");
		Objects.requireNonNull(revocationCheckStyle, "revocation check style cannot be null.");
		Objects.requireNonNull(verificationTime, "verification time cannot be null.");
		Objects.requireNonNull(prefStore, "validation preferences cannot be null.");
		return adobeDocAssuranceService.verifyDocument(inDoc, revocationCheckStyle, verificationTime, prefStore);
	}

	@Override
	public Document applyDocumentTimeStamp(Document inDoc, VerificationTime verificationTime, ValidationPreferences validationPreferences,
			UnlockOptions unlockOptions) throws DocAssuranceServiceException {
		Objects.requireNonNull(inDoc, "input Document cannot be null.");
		Objects.requireNonNull(verificationTime, "verification time cannot be null.");
		Objects.requireNonNull(validationPreferences, "validation preferences cannot be null.");
		return adobeDocAssuranceService.applyDocumentTimeStamp(inDoc, verificationTime, validationPreferences, unlockOptions);
	}
	
	public static class FieldMDPOptionSpecBuilder {
		private final FieldMDPOptionSpec options = new FieldMDPOptionSpec();

		private FieldMDPOptionSpecBuilder() {
			super();
		}
		
		public static FieldMDPOptionSpecBuilder getInstance() {
			return new FieldMDPOptionSpecBuilder();
		}

		public FieldMDPOptionSpecBuilder setAction(FieldMDPAction action) {
			options.setAction(action);
			return this;
		}

		public FieldMDPOptionSpecBuilder setFields(String[] fields) {
			options.setFields(fields);
			return this;
		}

		public FieldMDPOptionSpecBuilder process(Function<FieldMDPOptionSpecBuilder, FieldMDPOptionSpecBuilder> function) {
	        return function.apply(this);
	    }
		
		public FieldMDPOptionSpec build() {
			return options;
		}
	}
	
	public static class PDFSeedValueOptionSpecBuilder {
		private final PDFSeedValueOptionSpec options = new PDFSeedValueOptionSpec();
		
		private PDFSeedValueOptionSpecBuilder() {
			super();
		}

		public static PDFSeedValueOptionSpecBuilder getInstance() {
			return new PDFSeedValueOptionSpecBuilder();
		}
		
		public PDFSeedValueOptionSpecBuilder setAddRevInfo(Boolean addRevInfo) {
			options.setAddRevInfo(addRevInfo);
			return this;
		}
		
		public PDFSeedValueOptionSpecBuilder setCertificateSeedValueOptions(CertificateSeedValueOptionSpec certSeedValue) {
			options.setCertificateSeedValueOptions(certSeedValue);
			return this;
		}
		
		public PDFSeedValueOptionSpecBuilder setDigestMethod(HashAlgorithm[] digestMethod) {
			options.setDigestMethod(digestMethod);
			return this;
		}

		public PDFSeedValueOptionSpecBuilder setFilterEx(String filterEx) {
			options.setFilterEx(filterEx);
			return this;
		}

		public PDFSeedValueOptionSpecBuilder setFlags(int flags) {
			options.setFlags(flags);
			return this;
		}

		public PDFSeedValueOptionSpecBuilder setLegalAttestations(String[] legalAttestations) {
			options.setLegalAttestations(legalAttestations);
			return this;
		}

		public PDFSeedValueOptionSpecBuilder setMdpValue(MDPPermissions mdpValue) {
			options.setMdpValue(mdpValue);
			return this;
		}

		public PDFSeedValueOptionSpecBuilder setReasons(String[] reasons) {
			options.setReasons(reasons);
			return this;
		}
		
		public PDFSeedValueOptionSpecBuilder setSubFilterEx(String[] subFilterEx) {
			options.setSubFilterEx(subFilterEx);
			return this;
		}
		
		public PDFSeedValueOptionSpecBuilder setTimeStampSeed(PDFTimeStampSeed timeStampSeed) {
			options.setTimeStampSeed(timeStampSeed);
			return this;
		}
		
		public PDFSeedValueOptionSpecBuilder setVersion(Double version) {
			options.setVersion(version);
			return this;
		}

		public PDFSeedValueOptionSpecBuilder process(Function<PDFSeedValueOptionSpecBuilder, PDFSeedValueOptionSpecBuilder> function) {
	        return function.apply(this);
	    }
		
		public PDFSeedValueOptionSpec build() {
			return options;
		}
	}
	
	public static class UnlockOptionsBuilder implements UnlockOptionsSetter {
		private final UnlockOptions options = new UnlockOptions();
		
		private UnlockOptionsBuilder() {
			super();
		}

		public static UnlockOptionsBuilder createInstance() {
			return new UnlockOptionsBuilder();
		}
		
		@Override
		public UnlockOptionsBuilder setAlias(String alias) {
			options.setAlias(alias);
			return this;
		}

		@Override
		public UnlockOptionsBuilder setPassword(String password) {
			options.setPassword(password);
			return this;
		}

		public UnlockOptionsBuilder process(Function<UnlockOptionsBuilder, UnlockOptionsBuilder> function) {
	        return function.apply(this);
	    }
		
		public UnlockOptions build() {
			return options;
		}
	}
	
	public static class PositionRectangleBuilder {
		private final PositionRectangle rect = new PositionRectangle();
		
		private PositionRectangleBuilder() {
			super();
		}
		
		public static PositionRectangleBuilder getInstance() {
			return new PositionRectangleBuilder();
		}

		public PositionRectangleBuilder setHeight(int height) {
			rect.setHeight(height);
			return this;
		}

		public PositionRectangleBuilder setLowerLeftX(int lowerLeftX) {
			rect.setLowerLeftX(lowerLeftX);
			return this;
		}

		public PositionRectangleBuilder setLowerLeftY(int lowerLeftY) {
			rect.setLowerLeftY(lowerLeftY);
			return this;
		}

		public PositionRectangleBuilder setWidth(int width) {
			rect.setWidth(width);
			return this;
		}
		
		public PositionRectangleBuilder process(Function<PositionRectangleBuilder, PositionRectangleBuilder> function) {
	        return function.apply(this);
	    }
		
		public PositionRectangle build() {
			return rect;
		}
	}

	public class SecureDocumentArgumentBuilder {
		private EncryptionOptions encryptionOptions;
		private SignatureOptions signatureOptions;
		private ReaderExtensionOptions readerExtensionOptions;
		private UnlockOptions unlockOptions;
		
		public EncryptionOptionsBuilder encryptionOptions() {
			return new EncryptionOptionsBuilder();
		}

		public SignatureOptionsBuilder signatureOptions() {
			return new SignatureOptionsBuilder();
		}
		
		public ReaderExtensionsOptionsBuilder readerExtensionsOptions(String credentialAlias) {
			Objects.requireNonNull(credentialAlias, "Credential Alias provided in Reader Extension options cannot be null.");
			return new ReaderExtensionsOptionsBuilder(credentialAlias);
		}

		public InnerUnlockOptionsBuilder unlockOptions() {
			return new InnerUnlockOptionsBuilder();
		}
		
		public SecureDocumentArgumentBuilder process(Function<SecureDocumentArgumentBuilder, SecureDocumentArgumentBuilder> function) {
	        return function.apply(this);
	    }
		
		public Document executeOn(Document inDoc) throws DocAssuranceServiceException {
			// This is here to remind everyone to leave these options alone.  Any options that are not used
			// will be have null passed.  The secureDocument() method skips any steps that have null passed for them.
			// if (this.encryptionOptions == null) this.encryptionOptions().done();
			// if (this.unlockOptions == null) this.unlockOptions().done();
			// if (this.readerExtensionOptions == null) this.readerExtensionsOptions().done();
			// if (this.signatureOptions == null) this.signatureOptions().done();
			return secureDocument(inDoc, encryptionOptions, signatureOptions, readerExtensionOptions, unlockOptions); 
		}
		
		// EncryptionOptions, SignatureOptions and ReaderExtensionOptions are only used by secureDocument.
		public class EncryptionOptionsBuilder implements EncryptionOptions {
			private final EncryptionOptionsImpl options = new EncryptionOptionsImpl();
			private List<CertificateEncryptionIdentity> pkiIdentities = new ArrayList<>();

			private EncryptionOptionsBuilder() {
				super();
			}
			
			public EncryptionOptionsBuilder createInstance() {
				return new EncryptionOptionsBuilder();
			}
			
			public EncryptionOptionsBuilder process(Function<EncryptionOptionsBuilder, EncryptionOptionsBuilder> function) {
		        return function.apply(this);
		    }
			
			public EncryptionOptionsImpl build() {
				this.options.setPkiIdentities(this.pkiIdentities);
				return options;
			}

			public SecureDocumentArgumentBuilder done() {
				SecureDocumentArgumentBuilder.this.encryptionOptions = this.options;
				return SecureDocumentArgumentBuilder.this;
			}
			
			public PasswordEncryptionOptionSpecBuilder setPasswordEncryptionOptions() {
				return new PasswordEncryptionOptionSpecBuilder();
			}

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
			public EncryptionOptionsBuilder setCertOptionSpec(CertificateEncryptionOptionSpec certOptionSpec) {
				this.options.setCertOptionSpec(certOptionSpec);
				return this;
			}

			@Override
			public EncryptionOptionsBuilder setCredentialAlias(String credentialAlias) {
				this.options.setCredentialAlias(credentialAlias);
				return this;
			}

			@Override
			public EncryptionOptionsBuilder setEncryptionType(DocAssuranceServiceOperationTypes encryptionType) {
				this.options.setEncryptionType(encryptionType);
				return this;
			}

			@Override
			public EncryptionOptionsBuilder setPasswordEncryptionOptionSpec(PasswordEncryptionOptionSpec passwordEncryptionOptionSpec) {
				this.options.setPasswordEncryptionOptionSpec(passwordEncryptionOptionSpec);
				return this;
			}

			@Override
			public EncryptionOptionsBuilder setPkiIdentities(List<CertificateEncryptionIdentity> pkiIdentities) {
				this.options.setPkiIdentities(pkiIdentities);
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
			
			public class PasswordEncryptionOptionSpecBuilder implements PasswordEncryptionOptionSpecSetter {
				private final PasswordEncryptionOptionSpec options = new PasswordEncryptionOptionSpec();

				private PasswordEncryptionOptionSpecBuilder() {
					super();
				}

				@Override
				public PasswordEncryptionOptionSpecBuilder setCompatability(PasswordEncryptionCompatability compat) {
					this.options.setCompatability(compat);
					return this;
				}

				@Override
				public PasswordEncryptionOptionSpecBuilder setEncryptOption(PasswordEncryptionOption encoptions) {
					this.options.setEncryptOption(encoptions);
					return this;
				}
				
				@Override
				public PasswordEncryptionOptionSpecBuilder setDocumentOpenPassword(String password) {
					this.options.setDocumentOpenPassword(password);
					return this;
				}
				
				@Override
				public PasswordEncryptionOptionSpecBuilder setPermissionPassword(String password) {
					this.options.setPermissionPassword(password);
					return this;
				}
				
				@Override
				public PasswordEncryptionOptionSpecBuilder setPermissionsRequested(@SuppressWarnings("rawtypes") List p) {	// Adobe API uses raw types so we have to follow suit.
					this.options.setPermissionsRequested(p);
					return this;
				}
				
				public PasswordEncryptionOptionSpecBuilder process(Function<PasswordEncryptionOptionSpecBuilder, PasswordEncryptionOptionSpecBuilder> function) {
			        return function.apply(this);
			    }

				public EncryptionOptionsBuilder done() {
					EncryptionOptionsBuilder.this.options.setPasswordEncryptionOptionSpec(this.options);
					return EncryptionOptionsBuilder.this;
				}
				
				// Have both done() and build() methods so that this builder can be used both internally and externally.
				public PasswordEncryptionOptionSpec build() {
					return this.options;
				}
			}

		}
		
		public class SignatureOptionsBuilder {
			private final SignatureOptions options = SignatureOptions.getInstance();
			
			public SignatureOptionsBuilder setAlgo(HashAlgorithm algo) {
				options.setAlgo(algo);
				return this;
			}

			public SignatureOptionsBuilder setContactInfo(String contactInfo) {
				options.setContactInfo(contactInfo);
				return this;
			}

			public SignatureOptionsBuilder setCredential(CredentialContext credential) {
				options.setCredential(credential);
				return this;
			}

			public DSSPreferencesBuilder setDssPref() {
				return new DSSPreferencesBuilder();
			}

			public SignatureOptionsBuilder setLegalAttestation(String legalAttestation) {
				options.setLegalAttestation(legalAttestation);
				return this;
			}

			public SignatureOptionsBuilder setLocation(String location) {
				options.setLocation(location);
				return this;
			}

			public SignatureOptionsBuilder setLockCertifyingField(boolean lockCertifyingField) {
				options.setLockCertifyingField(lockCertifyingField);
				return this;
			}

			public SignatureOptionsBuilder setMdpPermissions(MDPPermissions mdpPermissions) {
				options.setMdpPermissions(mdpPermissions);
				return this;
			}

			public SignatureOptionsBuilder setOperationType(DocAssuranceServiceOperationTypes operationType) {
				options.setOperationType(operationType);
				return this;
			}

			public SignatureOptionsBuilder setReason(String reason) {
				options.setReason(reason);
				return this;
			}

			public SignatureOptionsBuilder setSigAppearence(PDFSignatureAppearenceOptions sigAppearence) {
				options.setSigAppearence(sigAppearence);
				return this;
			}

			public SignatureOptionsBuilder setSignatureFieldName(String signatureFieldName) {
				options.setSignatureFieldName(signatureFieldName);
				return this;
			}

			public SecureDocumentArgumentBuilder done() {
				if (this.options.getDssPref() == null) this.setDssPref().done();	// Initialize if the user hasn't
				SecureDocumentArgumentBuilder.this.signatureOptions = options;
				return SecureDocumentArgumentBuilder.this;
			}
			
			public class DSSPreferencesBuilder {
				private DSSPreferences prefs = DSSPreferencesImpl.getInstance();
				
				public DSSPreferencesBuilder setCreateFormDOM(boolean createFormDOM) {
					this.prefs.setCreateFormDOM(createFormDOM);
					return this;
				}

				public DSSPreferencesBuilder setEnableDocumentJavascripts(boolean enableDocumentJavascripts) {
					this.prefs.setEnableDocumentJavascripts(enableDocumentJavascripts);
					return this;
				}

				public DSSPreferencesBuilder setEnforceRevocationEmbedding(boolean enforceRevocationEmbedding) {
					this.prefs.setEnforceRevocationEmbedding(enforceRevocationEmbedding);
					return this;
				}

				public DSSPreferencesBuilder setJSPreferences(JavascriptPreferences prefs) {
					this.prefs.setJSPreferences(prefs);
					return this;
				}

				public DSSPreferencesBuilder setMaxRevInfoArchiveSize(int maxRevInfoArchiveSize) {
					this.prefs.setMaxRevInfoArchiveSize(maxRevInfoArchiveSize);
					return this;
				}

				public PKIPreferencesBuilder setPKIPreferences() {
					return new PKIPreferencesBuilder();
				}

				public DSSPreferencesBuilder setProperty(String key, Object value) {
					this.prefs.setProperty(key, value);
					return this;
				}

				public DSSPreferencesBuilder setSupportPreReleaseSignatures(boolean supportPreReleaseSignatures) {
					this.prefs.setSupportPreReleaseSignatures(supportPreReleaseSignatures);
					return this;
				}

				public DSSPreferencesBuilder setUnlockOptions(UnlockOptions unlockOptions) {
					this.prefs.setUnlockOptions(unlockOptions);
					return this;
				}

				public DSSPreferencesBuilder setUseArchivedRevocationInfo(boolean useArchivedRevocationInfo) {
					this.prefs.setUseArchivedRevocationInfo(useArchivedRevocationInfo);
					return this;
				}

				public DSSPreferencesBuilder setUseVRI(boolean useVRI) {
					this.prefs.setUseVRI(useVRI);
					return this;
				}

				public DSSPreferencesBuilder setVerificationTimeClockSkew(long skew) {
					this.setVerificationTimeClockSkew(skew);
					return this;
				}
				
				public SignatureOptionsBuilder done() {
					SignatureOptionsBuilder.this.options.setDssPref(this.prefs);
					return SignatureOptionsBuilder.this;
				}
				
				public class PKIPreferencesBuilder {
					private PKIPreferences prefs = PKIPreferencesImpl.getInstance();
					
					public CRLPreferencesBuilder setCRLPreferences() {
						return new CRLPreferencesBuilder();
					}

					public GeneralPreferencesBuilder setGeneralPreferences() {
						return new GeneralPreferencesBuilder();
					}

					public OCSPPreferencesBuilder setOCSPPreferences() {
						return new OCSPPreferencesBuilder();
					}

					public PathValidationPreferencesBuilder setPathPreferences() {
						return new PathValidationPreferencesBuilder();
					}

					public TSPPreferencesBuilder setTSPPreferences() {
						return new TSPPreferencesBuilder();
					}

					public TransportPreferencesBuilder setTransportPreferences() {
						return new TransportPreferencesBuilder();
					}
					
					public DSSPreferencesBuilder done() {
						if (this.prefs.getCRLPreferences() == null) this.setCRLPreferences().done();		// Initialize substructures if the user hasn't
						if (this.prefs.getGeneralPreferences() == null) this.setGeneralPreferences().done();
						if (this.prefs.getOCSPPreferences() == null) this.setOCSPPreferences().done();
						if (this.prefs.getPathPreferences() == null) this.setPathPreferences().done();
						if (this.prefs.getTSPPreferences() == null) this.setTSPPreferences().done();
						if (this.prefs.getTransportPreferences() == null) this.setTransportPreferences().done();
						DSSPreferencesBuilder.this.prefs.setPKIPreferences(this.prefs);
						return DSSPreferencesBuilder.this;
					}
					
					public class CRLPreferencesBuilder {
						private CRLPreferencesImpl prefs = new CRLPreferencesImpl();

						public CRLPreferencesBuilder setAlwaysConsultLocalURL(boolean alwaysConsultLocalURL) {
							this.prefs.setAlwaysConsultLocalURL(alwaysConsultLocalURL);
							return this;
						}

						public CRLPreferencesBuilder setGoOnline(boolean goOnline) {
							this.prefs.setGoOnline(goOnline);
							return this;
						}

						public CRLPreferencesBuilder setIgnoreValidityDates(boolean ignoreValidityDates) {
							this.prefs.setIgnoreValidityDates(ignoreValidityDates);
							return this;
						}

						public CRLPreferencesBuilder setLDAPServer(String server) {
							this.prefs.setLDAPServer(server);
							return this;
						}

						public CRLPreferencesBuilder setLocalURI(String localURI) {
							this.prefs.setLocalURI(localURI);
							return this;
						}

						public CRLPreferencesBuilder setRequireAKI(boolean requireAKI) {
							this.prefs.setRequireAKI(requireAKI);
							return this;
						}

						public CRLPreferencesBuilder setUseCache(boolean useCache) {
							this.prefs.setUseCache(useCache);
							return this;
						}

						public CRLPreferencesBuilder setValidityWindow(long validityWindow) {
							this.prefs.setValidityWindow(validityWindow);
							return this;
						}

						public CRLPreferencesBuilder process(Function<CRLPreferencesBuilder, CRLPreferencesBuilder> function) {
					        return function.apply(this);
					    }

						public PKIPreferencesBuilder done() {
							PKIPreferencesBuilder.this.prefs.setCRLPreferences(this.prefs);
							return PKIPreferencesBuilder.this;
						}
					}
					
					public class GeneralPreferencesBuilder {
						private final GeneralPreferencesImpl prefs = new GeneralPreferencesImpl();

						public GeneralPreferencesBuilder setCertCacheLifetime(long value) {
							this.prefs.setCertCacheLifetime(value);
							return this;
						}

						public GeneralPreferencesBuilder setDisableCache(boolean isDisabled) {
							this.prefs.setDisableCache(isDisabled);
							return this;
						}

						public GeneralPreferencesBuilder setMaxRevInfoArchiveSize(int size) {
							this.prefs.setMaxRevInfoArchiveSize(size);
							return this;
						}

						public GeneralPreferencesBuilder setMaxSessions(int maxSessions) {
							this.prefs.setMaxSessions(maxSessions);
							return this;
						}

						public GeneralPreferencesBuilder setRevCheckOrder(RevocationCheckOrder revCheckOrder) {
							this.prefs.setRevCheckOrder(revCheckOrder);
							return this;
						}

						public GeneralPreferencesBuilder splitHSMSigning(boolean splitHSMSigning) {
							this.prefs.splitHSMSigning(splitHSMSigning);
							return this;
						}
						
						public GeneralPreferencesBuilder process(Function<GeneralPreferencesBuilder, GeneralPreferencesBuilder> function) {
					        return function.apply(this);
					    }

						public PKIPreferencesBuilder done() {
							PKIPreferencesBuilder.this.prefs.setGeneralPreferences(this.prefs);
							return PKIPreferencesBuilder.this;
						}
					}
					
					public class OCSPPreferencesBuilder {
						private final OCSPPreferencesImpl prefs = new OCSPPreferencesImpl();

						public OCSPPreferencesBuilder setAllowOCSPNoCheck(boolean value) {
							this.prefs.setAllowOCSPNoCheck(value);
							return this;
						}

						public OCSPPreferencesBuilder setDigestAlgorithm(HashAlgorithm algo) {
							this.prefs.setDigestAlgorithm(algo);
							return this;
						}

						public OCSPPreferencesBuilder setDoSignRequest(boolean doSignRequest) {
							this.prefs.setDoSignRequest(doSignRequest);
							return this;
						}

						public OCSPPreferencesBuilder setGoOnline(boolean value) {
							this.prefs.setGoOnline(value);
							return this;
						}

						public OCSPPreferencesBuilder setIgnoreValidityDates(boolean value) {
							this.prefs.setIgnoreValidityDates(value);
							return this;
						}

						public OCSPPreferencesBuilder setMaxClockSkew(long maxClockSkew) {
							this.prefs.setMaxClockSkew(maxClockSkew);
							return this;
						}

						public OCSPPreferencesBuilder setOcspServerURL(String ocspServerURL) {
							this.prefs.setOcspServerURL(ocspServerURL);
							return this;
						}

						public OCSPPreferencesBuilder setRequestSignerCredential(PKICredential cred) {
							this.prefs.setRequestSignerCredential(cred);
							return this;
						}

						public OCSPPreferencesBuilder setRequireOCSPCertHash(boolean value) {
							this.prefs.setRequireOCSPCertHash(value);
							return this;
						}

						public OCSPPreferencesBuilder setResponseFreshness(long responseFreshness) {
							this.prefs.setResponseFreshness(responseFreshness);
							return this;
						}

						public OCSPPreferencesBuilder setSendNonce(boolean sendNonce) {
							this.prefs.setSendNonce(sendNonce);
							return this;
						}

						public OCSPPreferencesBuilder setURLtoConsult(OCSPURLtoConsultOption urlToConsult) {
							this.prefs.setURLtoConsult(urlToConsult);
							return this;
						}

						public OCSPPreferencesBuilder setUseCache(boolean value) {
							this.prefs.setUseCache(value);
							return this;
						}

						public OCSPPreferencesBuilder process(Function<OCSPPreferencesBuilder, OCSPPreferencesBuilder> function) {
					        return function.apply(this);
					    }

						public PKIPreferencesBuilder done() {
							PKIPreferencesBuilder.this.prefs.setOCSPPreferences(this.prefs);
							return PKIPreferencesBuilder.this;
						}
					}
					
					public class PathValidationPreferencesBuilder {
						private final PathValidationPreferencesImpl prefs = new PathValidationPreferencesImpl();

						public PathValidationPreferencesBuilder setAnyPolicyInhibit(boolean anyPolicyInhibit) {
							this.prefs.setAnyPolicyInhibit(anyPolicyInhibit);
							return this;
						}

						public PathValidationPreferencesBuilder setCheckAllPaths(boolean checkAllPaths) {
							this.prefs.setCheckAllPaths(checkAllPaths);
							return this;
						}

						public PathValidationPreferencesBuilder setCheckCABasicConstraints(boolean checkCABasicConstraints) {
							this.prefs.setCheckCABasicConstraints(checkCABasicConstraints);
							return this;
						}

						public PathValidationPreferencesBuilder setDoValidation(boolean doValidation) {
							this.prefs.setDoValidation(doValidation);
							return this;
						}

						public PathValidationPreferencesBuilder setExplicitPolicy(boolean explicitPolicy) {
							this.prefs.setExplicitPolicy(explicitPolicy);
							return this;
						}

						public PathValidationPreferencesBuilder setFollowAIAURIs(boolean followAIAURIs) {
							this.prefs.setFollowAIAURIs(followAIAURIs);
							return this;
						}

						public PathValidationPreferencesBuilder setLDAPServer(String ldapServer) {
							this.prefs.setLDAPServer(ldapServer);
							return this;
						}

						public PathValidationPreferencesBuilder setPolicyMappingInhibit(boolean policyMappingInhibit) {
							this.prefs.setPolicyMappingInhibit(policyMappingInhibit);
							return this;
						}

						public PathValidationPreferencesBuilder setRequireValidSigForChaining(boolean requireValidSigForChaining) {
							this.prefs.setRequireValidSigForChaining(requireValidSigForChaining);
							return this;
						}
						
						public PathValidationPreferencesBuilder process(Function<PathValidationPreferencesBuilder, PathValidationPreferencesBuilder> function) {
					        return function.apply(this);
					    }
						
						public PKIPreferencesBuilder done() {
							PKIPreferencesBuilder.this.prefs.setPathPreferences(this.prefs);
							return PKIPreferencesBuilder.this;
						}
					}
					
					public class TSPPreferencesBuilder {
						private final TSPPreferencesImpl prefs = new TSPPreferencesImpl();
						
						public TSPPreferencesBuilder setPassword(char[] password) {
							this.prefs.setPassword(password);
							return this;
						}

						public TSPPreferencesBuilder setSendNonce(boolean sendNonce) {
							this.prefs.setSendNonce(sendNonce);
							return this;
						}

						public TSPPreferencesBuilder setSize(int size) {
							this.prefs.setSize(size);
							return this;
						}

						public TSPPreferencesBuilder setTSPHashAlgorithm(HashAlgorithm hashAlgo) {
							this.prefs.setTSPHashAlgorithm(hashAlgo);
							return this;
						}

						public TSPPreferencesBuilder setTspServerURL(String tspServerURL) {
							this.prefs.setTspServerURL(tspServerURL);
							return this;
						}

						public TSPPreferencesBuilder setUseExpiredTimestamps(boolean useExpiredTimestamps) {
							this.prefs.setUseExpiredTimestamps(useExpiredTimestamps);
							return this;
						}

						public TSPPreferencesBuilder setUsername(String userName) {
							this.prefs.setUsername(userName);
							return this;
						}

						public TSPPreferencesBuilder process(Function<TSPPreferencesBuilder, TSPPreferencesBuilder> function) {
					        return function.apply(this);
					    }
						
						public PKIPreferencesBuilder done() {
							PKIPreferencesBuilder.this.prefs.setTSPPreferences(this.prefs);
							return PKIPreferencesBuilder.this;
						}
					}
					
					public class TransportPreferencesBuilder {
						private final TransportPreferencesImpl prefs = new TransportPreferencesImpl();
						
						public TransportPreferencesBuilder setConnectionTimeout(int connectionTimeout) {
							this.prefs.setConnectionTimeout(connectionTimeout);
							return this;
						}

						public TransportPreferencesBuilder setMaxDownloadLimit(int maxDownloadLimit) {
							this.prefs.setMaxDownloadLimit(maxDownloadLimit);
							return this;
						}

						public TransportPreferencesBuilder setProxyHost(String proxyHost) {
							this.prefs.setProxyHost(proxyHost);
							return this;
						}

						public TransportPreferencesBuilder setProxyPassword(String proxyPassword) {
							this.prefs.setProxyPassword(proxyPassword);
							return this;
						}

						public TransportPreferencesBuilder setProxyPort(int proxyPort) {
							this.prefs.setProxyPort(proxyPort);
							return this;
						}

						public TransportPreferencesBuilder setProxyUsername(String proxyUsername) {
							this.prefs.setProxyUsername(proxyUsername);
							return this;
						}

						public TransportPreferencesBuilder setSSLRevCheckStyle(RevocationCheckStyle style) {
							this.prefs.setSSLRevCheckStyle(style);
							return this;
						}

						public TransportPreferencesBuilder setSocketTimeout(int socketTimeout) {
							this.prefs.setSocketTimeout(socketTimeout);
							return this;
						}

						public TransportPreferencesBuilder setTransportDisabled(boolean isDisabled) {
							this.prefs.setTransportDisabled(isDisabled);
							return this;
						}

						public TransportPreferencesBuilder process(Function<TransportPreferencesBuilder, TransportPreferencesBuilder> function) {
					        return function.apply(this);
					    }
						
						public PKIPreferencesBuilder done() {
							PKIPreferencesBuilder.this.prefs.setTransportPreferences(this.prefs);
							return PKIPreferencesBuilder.this;
						}
					}
				}
			}
		}
		
		public class ReaderExtensionsOptionsBuilder implements Transformable<ReaderExtensionsOptionsBuilder> {
			private final ReaderExtensionOptionsImpl options = new ReaderExtensionOptionsImpl();
			
			private ReaderExtensionsOptionsBuilder(String credentialAlias) {
				super();
				this.options.setCredentialAlias(credentialAlias);
			}

			public ReaderExtensionsOptionSpecBuilder setReOptions() {
				return new ReaderExtensionsOptionSpecBuilder();
			}

			public ReaderExtensionsOptionsBuilder process(Function<ReaderExtensionsOptionsBuilder, ReaderExtensionsOptionsBuilder> function) {
		        return function.apply(this);
		    }

			public SecureDocumentArgumentBuilder done() {
				if (this.options.getReOptions() == null) this.setReOptions().done();		// Initialize if the user hasn't
				SecureDocumentArgumentBuilder.this.readerExtensionOptions = options;
				return SecureDocumentArgumentBuilder.this;
			}
			
			public class ReaderExtensionsOptionSpecBuilder implements Transformable<ReaderExtensionsOptionSpecBuilder> {
				private final ReaderExtensionsOptionSpec options = new ReaderExtensionsOptionSpec();

				public ReaderExtensionsOptionSpecBuilder setMessage(String message) {
					this.options.setMessage(message);
					return this;
				}

				public ReaderExtensionsOptionSpecBuilder setModeFinal(boolean modeFinal) {
					this.options.setModeFinal(modeFinal);
					return this;
				}

				public UsageRightsBuilder setUsageRights() {
					return new UsageRightsBuilder();
				}
				
				public ReaderExtensionsOptionSpecBuilder process(Function<ReaderExtensionsOptionSpecBuilder, ReaderExtensionsOptionSpecBuilder> function) {
			        return function.apply(this);
			    }

				public ReaderExtensionsOptionsBuilder done() {
					ReaderExtensionsOptionsBuilder.this.options.setReOptions(this.options);
					return ReaderExtensionsOptionsBuilder.this;
				}

				public class UsageRightsBuilder implements Transformable<UsageRightsBuilder> {
					private final UsageRights rights = new UsageRights();

					public UsageRightsBuilder setEnabledBarcodeDecoding(boolean enabledBarcodeDecoding) {
						rights.setEnabledBarcodeDecoding(enabledBarcodeDecoding);
						return this;
					}

					public UsageRightsBuilder setEnabledComments(boolean enabledComments) {
						rights.setEnabledComments(enabledComments);
						return this;
					}

					public UsageRightsBuilder setEnabledCommentsOnline(boolean enabledCommentsOnline) {
						rights.setEnabledCommentsOnline(enabledCommentsOnline);
						return this;
					}

					public UsageRightsBuilder setEnabledDigitalSignatures(boolean enabledDigitalSignatures) {
						rights.setEnabledDigitalSignatures(enabledDigitalSignatures);
						return this;
					}

					public UsageRightsBuilder setEnabledDynamicFormFields(boolean enabledDynamicFormFields) {
						rights.setEnabledDynamicFormFields(enabledDynamicFormFields);
						return this;
					}

					public UsageRightsBuilder setEnabledDynamicFormPages(boolean enabledDynamicFormPages) {
						rights.setEnabledDynamicFormPages(enabledDynamicFormPages);
						return this;
					}

					public UsageRightsBuilder setEnabledEmbeddedFiles(boolean enabledEmbeddedFiles) {
						rights.setEnabledEmbeddedFiles(enabledEmbeddedFiles);
						return this;
					}

					public UsageRightsBuilder setEnabledFormDataImportExport(boolean enabledFormDataImportExport) {
						rights.setEnabledFormDataImportExport(enabledFormDataImportExport);
						return this;
					}

					public UsageRightsBuilder setEnabledFormFillIn(boolean enabledFormFillIn) {
						rights.setEnabledFormFillIn(enabledFormFillIn);
						return this;
					}

					public UsageRightsBuilder setEnabledOnlineForms(boolean enabledOnlineForms) {
						rights.setEnabledOnlineForms(enabledOnlineForms);
						return this;
					}

					public UsageRightsBuilder setEnabledSubmitStandalone(boolean enabledSubmitStandalone) {
						rights.setEnabledSubmitStandalone(enabledSubmitStandalone);
						return this;
					}

					public UsageRightsBuilder process(Function<UsageRightsBuilder, UsageRightsBuilder> function) {
				        return function.apply(this);
				    }

					public ReaderExtensionsOptionSpecBuilder done() {
						ReaderExtensionsOptionSpecBuilder.this.options.setUsageRights(rights);
						return ReaderExtensionsOptionSpecBuilder.this;
					}
				}
			}
			
		}
		
		public class InnerUnlockOptionsBuilder implements UnlockOptionsSetter {
			private UnlockOptionsBuilder builder = new UnlockOptionsBuilder(); 

			public InnerUnlockOptionsBuilder process(Function<InnerUnlockOptionsBuilder, InnerUnlockOptionsBuilder> function) {
		        return function.apply(this);
		    }

			public SecureDocumentArgumentBuilder done() {
				SecureDocumentArgumentBuilder.this.unlockOptions = builder.build();
				return SecureDocumentArgumentBuilder.this;
			}

			@Override
			public InnerUnlockOptionsBuilder setAlias(String alias) {
				builder.setAlias(alias);
				return this;
			}

			@Override
			public InnerUnlockOptionsBuilder setPassword(String password) {
				builder.setPassword(password);
				return this;
			}
		}
	}	

}
