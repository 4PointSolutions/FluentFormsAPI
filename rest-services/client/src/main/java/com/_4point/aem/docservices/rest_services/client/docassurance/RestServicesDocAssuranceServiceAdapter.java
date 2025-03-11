package com._4point.aem.docservices.rest_services.client.docassurance;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import com._4point.aem.docservices.rest_services.client.RestClient;
import com._4point.aem.docservices.rest_services.client.RestClient.ContentType;
import com._4point.aem.docservices.rest_services.client.RestClient.MultipartPayload;
import com._4point.aem.docservices.rest_services.client.RestClient.RestClientException;
import com._4point.aem.docservices.rest_services.client.helpers.AemConfig;
import com._4point.aem.docservices.rest_services.client.helpers.AemServerType;
import com._4point.aem.docservices.rest_services.client.helpers.Builder;
import com._4point.aem.docservices.rest_services.client.helpers.BuilderImpl;
import com._4point.aem.docservices.rest_services.client.helpers.BuilderImpl.TriFunction;
import com._4point.aem.docservices.rest_services.client.helpers.RestServicesServiceAdapter;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.docassurance.DocAssuranceService.DocAssuranceServiceException;
import com._4point.aem.fluentforms.api.docassurance.EncryptionOptions;
import com._4point.aem.fluentforms.api.docassurance.ReaderExtensionOptions;
import com._4point.aem.fluentforms.impl.docassurance.TraditionalDocAssuranceService;
import com.adobe.fd.docassurance.client.api.SignatureOptions;
import com.adobe.fd.encryption.client.EncryptionTypeResult;
import com.adobe.fd.readerextensions.client.GetUsageRightsResult;
import com.adobe.fd.readerextensions.client.ReaderExtensionsOptionSpec;
import com.adobe.fd.readerextensions.client.UsageRights;
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

public class RestServicesDocAssuranceServiceAdapter extends RestServicesServiceAdapter implements TraditionalDocAssuranceService {

	private static final String SECURE_DOCUMENT_SERVICE_NAME = "DocAssuranceService";
	private static final String SECURE_DOCUMENT_METHOD_NAME = "SecureDocument";

	private static final String CREDENTIAL_ALIAS_PARAM = "credentialAlias";
	private static final String DOCUMENT_PARAM = "inDoc";
	private static final String MESSAGE_PARAM = "message";
	private static final String IS_MODE_FINAL_PARAM = "isModeFinal";
	private static final String ENABLED_BARCODED_DECODING_PARAM = "usageRights.enabledBarcodedDecoding";
	private static final String ENABLED_COMMENTS_PARAM = "usageRights.enabledComments";
	private static final String ENABLED_COMMENTS_ONLINE_PARAM = "usageRights.enabledCommentsOnline";
	private static final String ENABLED_DIGITAL_SIGNATURES_PARAM = "usageRights.enabledDigitalSignatures";
	private static final String ENABLED_DYNAMIC_FORM_FIELDS_PARAM = "usageRights.enabledDynamicFormFields";
	private static final String ENABLED_DYNAMIC_FORM_PAGES_PARAM = "usageRights.enabledDynamicFormPages";
	private static final String ENABLED_EMBEDDED_FILES_PARAM = "usageRights.enabledEmbeddedFiles";
	private static final String ENABLED_FORM_DATA_IMPORT_EXPORT_PARAM = "usageRights.enabledFormDataImportExport";
	private static final String ENABLED_FORM_FILL_IN_PARAM = "usageRights.enabledFormFillIn";
	private static final String ENABLED_ONLINE_FORMS_PARAM = "usageRights.enabledOnlineForms";
	private static final String ENABLED_SUBMIT_STANDALONE_PARAM = "usageRights.enabledSubmitStandalone";


	private final RestClient secureDocumentRestClient;

	RestServicesDocAssuranceServiceAdapter(BuilderImpl builder, Supplier<String> correlationIdFn) {
        super(correlationIdFn);;
		this.secureDocumentRestClient = builder.createClient(SECURE_DOCUMENT_SERVICE_NAME, SECURE_DOCUMENT_METHOD_NAME);
	}


	@Override
	public Document secureDocument(Document inDocument, EncryptionOptions encryptionOptions, SignatureOptions signatureOptions, ReaderExtensionOptions readerExtensionOptions,
			UnlockOptions unlockOptions) throws DocAssuranceServiceException {
		Objects.requireNonNull(inDocument, "Input document cannot be null.");
		if (encryptionOptions != null) {
			// TODO Implement this
			throw new UnsupportedOperationException("Encryption support has not yet been added to FluentForms library.");
		}

		if (signatureOptions != null) {
			// TODO Implement this
			throw new UnsupportedOperationException("Digital Signature support has not yet been added to FluentForms library.");
		}

		if (unlockOptions != null) {
			// TODO Implement this
			throw new UnsupportedOperationException("Unlock Options support has not yet been added to FluentForms library.");
		}

		ReaderExtensionsOptionSpec reOptionsSpec = readerExtensionOptions != null ? readerExtensionOptions.getReOptions() : null;
		String reCredentialAlias = readerExtensionOptions != null ? Objects.requireNonNull(readerExtensionOptions.getCredentialAlias(), "Reader Extensions credential alias cannot be null.") : null;
		UsageRights reUsageOptionsSpec = reOptionsSpec != null ? reOptionsSpec.getUsageRights() : null;
        try (MultipartPayload payload = secureDocumentRestClient.multipartPayloadBuilder()
        														.add(DOCUMENT_PARAM, inDocument, ContentType.APPLICATION_PDF)
        														.addIfNotNull(CREDENTIAL_ALIAS_PARAM, reCredentialAlias)
        														.transformAndAdd(MESSAGE_PARAM, reOptionsSpec, ReaderExtensionsOptionSpec::getMessage)
        														.transformAndAddStringVersion(IS_MODE_FINAL_PARAM, reOptionsSpec, ReaderExtensionsOptionSpec::isModeFinal)
        														.transformAndAddStringVersion(ENABLED_BARCODED_DECODING_PARAM, reUsageOptionsSpec, UsageRights::isEnabledBarcodeDecoding)
        														.transformAndAddStringVersion(ENABLED_COMMENTS_PARAM, reUsageOptionsSpec,  UsageRights::isEnabledComments)
        														.transformAndAddStringVersion(ENABLED_COMMENTS_ONLINE_PARAM, reUsageOptionsSpec,  UsageRights::isEnabledCommentsOnline)
        														.transformAndAddStringVersion(ENABLED_DIGITAL_SIGNATURES_PARAM, reUsageOptionsSpec,  UsageRights::isEnabledDigitalSignatures)
        														.transformAndAddStringVersion(ENABLED_DYNAMIC_FORM_FIELDS_PARAM, reUsageOptionsSpec,  UsageRights::isEnabledDynamicFormFields)
        														.transformAndAddStringVersion(ENABLED_DYNAMIC_FORM_PAGES_PARAM, reUsageOptionsSpec,  UsageRights::isEnabledDynamicFormPages)
        														.transformAndAddStringVersion(ENABLED_EMBEDDED_FILES_PARAM, reUsageOptionsSpec,  UsageRights::isEnabledEmbeddedFiles)
        														.transformAndAddStringVersion(ENABLED_FORM_DATA_IMPORT_EXPORT_PARAM, reUsageOptionsSpec,  UsageRights::isEnabledFormDataImportExport)
        														.transformAndAddStringVersion(ENABLED_FORM_FILL_IN_PARAM, reUsageOptionsSpec,  UsageRights::isEnabledFormFillIn)
        														.transformAndAddStringVersion(ENABLED_ONLINE_FORMS_PARAM, reUsageOptionsSpec,  UsageRights::isEnabledOnlineForms)
        														.transformAndAddStringVersion(ENABLED_SUBMIT_STANDALONE_PARAM, reUsageOptionsSpec,  UsageRights::isEnabledSubmitStandalone)
        														.build()) {
            return payload.postToServer(ContentType.APPLICATION_PDF)
      			 		  .map(RestServicesServiceAdapter::responseToDoc)
      			 		  .orElseThrow();
        } catch (IOException e) {
            throw new DocAssuranceServiceException("I/O Error while securing document. (" + secureDocumentRestClient.target() + ").", e);
        } catch (RestClientException e) {
            throw new DocAssuranceServiceException("Error while POSTing to server (" + secureDocumentRestClient.target() + ").", e);
        }
	}

	@Override
	public GetUsageRightsResult getDocumentUsageRights(Document inDocument, UnlockOptions unlockOptions)
			throws DocAssuranceServiceException {
		throw new UnsupportedOperationException("getDocumentUsageRights is not implemented yet.");
	}

	@Override
	public GetUsageRightsResult getCredentialUsageRights(String credentialAlias) throws DocAssuranceServiceException {
		throw new UnsupportedOperationException("getCredentialUsageRights is not implemented yet.");
	}

	@Override
	public Document addInvisibleSignatureField(Document inDoc, String signatureFieldName,
			FieldMDPOptionSpec fieldMDPOptionsSpec, PDFSeedValueOptionSpec seedValueOptionsSpec,
			UnlockOptions unlockOptions) throws DocAssuranceServiceException {
		throw new UnsupportedOperationException("addInvisibleSignatureField is not implemented yet.");
	}

	@Override
	public Document addSignatureField(Document inDoc, String signatureFieldName, Integer pageNo,
			PositionRectangle positionRectangle, FieldMDPOptionSpec fieldMDPOptionsSpec,
			PDFSeedValueOptionSpec seedValueOptionsSpec, UnlockOptions unlockOptions)
			throws DocAssuranceServiceException {
		throw new UnsupportedOperationException("addSignatureField is not implemented yet.");
	}

	@Override
	public Document clearSignatureField(Document inDoc, String signatureFieldName, UnlockOptions unlockOptions)
			throws DocAssuranceServiceException {
		throw new UnsupportedOperationException("clearSignatureField is not implemented yet.");
	}

	@Override
	public PDFSignatureField getCertifyingSignatureField(Document inDoc, UnlockOptions unlockOptions)
			throws DocAssuranceServiceException {
		throw new UnsupportedOperationException("getCertifyingSignatureField is not implemented yet.");
	}

	@Override
	public PDFSignature getSignature(Document inDoc, String signatureFieldName, UnlockOptions unlockOptions)
			throws DocAssuranceServiceException {
		throw new UnsupportedOperationException("getSignature is not implemented yet.");
	}

	@Override
	public List<PDFSignatureField> getSignatureFieldList(Document inDoc, UnlockOptions unlockOptions)
			throws DocAssuranceServiceException {
		throw new UnsupportedOperationException("getSignatureFieldList is not implemented yet.");
	}

	@Override
	public Document modifySignatureField(Document inDoc, String signatureFieldName,
			PDFSignatureFieldProperties pdfSignatureFieldProperties, UnlockOptions unlockOptions)
			throws DocAssuranceServiceException {
		throw new UnsupportedOperationException("modifySignatureField is not implemented yet.");
	}

	@Override
	public Document removeSignatureField(Document inDoc, String signatureFieldName, UnlockOptions unlockOptions)
			throws DocAssuranceServiceException {
		throw new UnsupportedOperationException("removeSignatureField is not implemented yet.");
	}

	@Override
	public PDFSignatureVerificationInfo verify(Document inDoc, String signatureFieldName,
			RevocationCheckStyle revocationCheckStyle, VerificationTime verificationTime,
			ValidationPreferences dssPrefs) throws DocAssuranceServiceException {
		throw new UnsupportedOperationException("verify is not implemented yet.");
	}

	@Override
	public EncryptionTypeResult getPDFEncryption(Document inDoc) throws DocAssuranceServiceException {
		throw new UnsupportedOperationException("getPDFEncryption is not implemented yet.");
	}

	@Override
	public Document removePDFCertificateSecurity(Document inDoc, String alias) throws DocAssuranceServiceException {
		throw new UnsupportedOperationException("removePDFCertificateSecurity is not implemented yet.");
	}

	@Override
	public Document removePDFPasswordSecurity(Document inDoc, String password) throws DocAssuranceServiceException {
		throw new UnsupportedOperationException("removePDFPasswordSecurity is not implemented yet.");
	}

	@Override
	public PDFDocumentVerificationInfo verifyDocument(Document inDoc, RevocationCheckStyle revocationCheckStyle,
			VerificationTime verificationTime, ValidationPreferences prefStore) throws DocAssuranceServiceException {
		throw new UnsupportedOperationException("verifyDocument is not implemented yet.");
	}

	@Override
	public Document removeUsageRights(Document inDoc, UnlockOptions unlockOptions) throws DocAssuranceServiceException {
		throw new UnsupportedOperationException("removeUsageRights is not implemented yet.");
	}

	@Override
	public Document applyDocumentTimeStamp(Document inDoc, VerificationTime verificationTime,
			ValidationPreferences validationPreferences, UnlockOptions unlockOptions)
			throws DocAssuranceServiceException {
		throw new UnsupportedOperationException("applyDocumentTimeStamp is not implemented yet.");
	}

	/**
	 * Creates a Builder object for building a RestServicesFormServiceAdapter object.
	 * 
	 * @return build object
	 */
	public static DocAssuranceServiceBuilder builder(TriFunction<AemConfig, String, Supplier<String>, RestClient> clientFactory) {
		return new DocAssuranceServiceBuilder(clientFactory);
	}
	
	public static class DocAssuranceServiceBuilder implements Builder {
		private final BuilderImpl builder;
		
		private DocAssuranceServiceBuilder(TriFunction<AemConfig, String, Supplier<String>, RestClient> clientFactory) {
			builder = new BuilderImpl(clientFactory);
		}

		@Override
		public DocAssuranceServiceBuilder machineName(String machineName) {
			builder.machineName(machineName);
			return this;
		}

		@Override
		public DocAssuranceServiceBuilder port(int port) {
			builder.port(port);
			return this;
		}

		@Override
		public DocAssuranceServiceBuilder useSsl(boolean useSsl) {
			builder.useSsl(useSsl);
			return this;
		}

		@Override
		public DocAssuranceServiceBuilder basicAuthentication(String username, String password) {
			builder.basicAuthentication(username, password);
			return this;
		}
		
		@Override
		public DocAssuranceServiceBuilder correlationId(Supplier<String> correlationIdFn) {
			builder.correlationId(correlationIdFn);
			return this;
		}

		@Override
		public Supplier<String> getCorrelationIdFn() {
			return builder.getCorrelationIdFn();
		}

		@Override
		public DocAssuranceServiceBuilder aemServerType(AemServerType serverType) {
			builder.aemServerType(serverType);
			return this;
		}
		
		@Override
		public AemServerType getAemServerType() {
			return builder.getAemServerType();
		}

		public RestServicesDocAssuranceServiceAdapter build() {
			return new RestServicesDocAssuranceServiceAdapter(builder, this.getCorrelationIdFn());
		}
	}
}
