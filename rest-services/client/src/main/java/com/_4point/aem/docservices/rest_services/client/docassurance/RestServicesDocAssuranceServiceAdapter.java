package com._4point.aem.docservices.rest_services.client.docassurance;

import java.util.List;
import java.util.function.Supplier;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.Response.StatusType;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import com._4point.aem.docservices.rest_services.client.helpers.Builder;
import com._4point.aem.docservices.rest_services.client.helpers.BuilderImpl;
import com._4point.aem.docservices.rest_services.client.helpers.MultipartTransformer;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.docassurance.DocAssuranceService.DocAssuranceServiceException;
import com._4point.aem.fluentforms.api.docassurance.EncryptionOptions;
import com._4point.aem.fluentforms.api.docassurance.ReaderExtensionOptions;
import com._4point.aem.fluentforms.impl.docassurance.TraditionalDocAssuranceService;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;
import com.adobe.fd.docassurance.client.api.SignatureOptions;
import com.adobe.fd.encryption.client.EncryptionTypeResult;
import com.adobe.fd.readerextensions.client.GetUsageRightsResult;
import com.adobe.fd.readerextensions.client.ReaderExtensionsOptionSpec;
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

public class RestServicesDocAssuranceServiceAdapter implements TraditionalDocAssuranceService {

	private static final String SECURE_DOCUMENT_PATH = "/services/DocAssuranceService/SecureDocument";
	private static final String CORRELATION_ID_HTTP_HDR = "X-Correlation-ID";
	private static final MediaType APPLICATION_PDF = new MediaType("application", "pdf");

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

	private final WebTarget baseTarget;
	private final Supplier<String> correlationIdFn;

	// Only callable from Builder
	private RestServicesDocAssuranceServiceAdapter(WebTarget target) {
		super();
		this.baseTarget = target;
		this.correlationIdFn = null;
	}

	// Only callable from Builder
	private RestServicesDocAssuranceServiceAdapter(WebTarget target, Supplier<String> correlationId) {
		super();
		this.baseTarget = target;
		this.correlationIdFn = correlationId;
	}

	@Override
	public Document secureDocument(Document inDocument, EncryptionOptions encryptionOptions, SignatureOptions signatureOptions, ReaderExtensionOptions readerExtensionOptions,
			UnlockOptions unlockOptions) throws DocAssuranceServiceException {
		WebTarget secureDocTarget = baseTarget.path(SECURE_DOCUMENT_PATH);
		
		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			if (encryptionOptions != null) {
				// TODO Auto-generated method stub
			}
			
			if (signatureOptions != null) {
				// TODO Auto-generated method stub
			}

			if (readerExtensionOptions != null) {
				String credentialAlias = readerExtensionOptions.getCredentialAlias();
				ReaderExtensionsOptionSpec reOptionsSpec = readerExtensionOptions.getReOptions();
				
				multipart.field(DOCUMENT_PARAM, inDocument.getInputStream(), APPLICATION_PDF);
				multipart.field(CREDENTIAL_ALIAS_PARAM, credentialAlias);

				if (reOptionsSpec != null) {
					String message = reOptionsSpec.getMessage();
					Boolean isModeFinal = reOptionsSpec.isModeFinal();
					Boolean enabledBarcodeDecoding = reOptionsSpec.getUsageRights().isEnabledBarcodeDecoding();
					Boolean enabledComments = reOptionsSpec.getUsageRights().isEnabledComments();
					Boolean enabledCommentsOnline = reOptionsSpec.getUsageRights().isEnabledCommentsOnline();
					Boolean enabledDigitalSignatures = reOptionsSpec.getUsageRights().isEnabledDigitalSignatures();
					Boolean enabledDynamicFormFields = reOptionsSpec.getUsageRights().isEnabledDynamicFormFields();
					Boolean enabledDynamicFormPages = reOptionsSpec.getUsageRights().isEnabledDynamicFormPages();
					Boolean enabledEmbeddedFiles = reOptionsSpec.getUsageRights().isEnabledEmbeddedFiles();
					Boolean enabledFormDateImportExport = reOptionsSpec.getUsageRights().isEnabledFormDataImportExport();
					Boolean enabledFormFillIn = reOptionsSpec.getUsageRights().isEnabledFormFillIn();
					Boolean enabledOnlineForms = reOptionsSpec.getUsageRights().isEnabledOnlineForms();
					Boolean enabledSubmitStandalone = reOptionsSpec.getUsageRights().isEnabledSubmitStandalone();

					// Set fields for non-null values. 
					MultipartTransformer.create(multipart)
										.transform((t)->message == null ? t : t.field(MESSAGE_PARAM, message))
										.transform((t)->isModeFinal == null ? t : t.field(IS_MODE_FINAL_PARAM, isModeFinal.toString()))
										.transform((t)->enabledBarcodeDecoding == null ? t : t.field(ENABLED_BARCODED_DECODING_PARAM, enabledBarcodeDecoding.toString()))
										.transform((t)->enabledComments == null ? t : t.field(ENABLED_COMMENTS_PARAM, enabledComments.toString()))
										.transform((t)->enabledCommentsOnline == null ? t : t.field(ENABLED_COMMENTS_ONLINE_PARAM, enabledCommentsOnline.toString()))
										.transform((t)->enabledDigitalSignatures == null ? t : t.field(ENABLED_DIGITAL_SIGNATURES_PARAM, enabledDigitalSignatures.toString()))
										.transform((t)->enabledDynamicFormFields == null ? t : t.field(ENABLED_DYNAMIC_FORM_FIELDS_PARAM, enabledDynamicFormFields.toString()))
										.transform((t)->enabledDynamicFormPages == null ? t : t.field(ENABLED_DYNAMIC_FORM_PAGES_PARAM, enabledDynamicFormPages.toString()))
										.transform((t)->enabledEmbeddedFiles == null ? t : t.field(ENABLED_EMBEDDED_FILES_PARAM, enabledEmbeddedFiles.toString()))
										.transform((t)->enabledFormDateImportExport == null ? t : t.field(ENABLED_FORM_DATA_IMPORT_EXPORT_PARAM, enabledFormDateImportExport.toString()))
										.transform((t)->enabledFormFillIn == null ? t : t.field(ENABLED_FORM_FILL_IN_PARAM, enabledFormFillIn.toString()))
										.transform((t)->enabledOnlineForms == null ? t : t.field(ENABLED_ONLINE_FORMS_PARAM, enabledOnlineForms.toString()))
										.transform((t)->enabledSubmitStandalone == null ? t : t.field(ENABLED_SUBMIT_STANDALONE_PARAM, enabledSubmitStandalone.toString()));
				}
			}
			
			if (unlockOptions != null) {
				// TODO Auto-generated method stub
			}

			Response result = postToServer(secureDocTarget, multipart);
			
			StatusType resultStatus = result.getStatusInfo();
			if (!Family.SUCCESSFUL.equals(resultStatus.getFamily())) {
				String msg = "Call to server failed, statusCode='" + resultStatus.getStatusCode() + "', reason='" + resultStatus.getReasonPhrase() + "'.";
				if (result.hasEntity()) {
					InputStream entityStream = (InputStream) result.getEntity();
					msg += "\n" + toString(entityStream);
				}
				throw new DocAssuranceServiceException(msg);
			}
			
			if (!result.hasEntity()) {
				throw new DocAssuranceServiceException("Call to server succeeded but server failed to return document.  This should never happen.");
			}
			
			return SimpleDocumentFactoryImpl.getFactory().create((InputStream) result.getEntity());
			
		} catch (IOException e) {
			throw new DocAssuranceServiceException("I/O Error while reader extending the document. (" + baseTarget.getUri().toString() + ").", e);
		}
	}

	@Override
	public GetUsageRightsResult getDocumentUsageRights(Document inDocument, UnlockOptions unlockOptions)
			throws DocAssuranceServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GetUsageRightsResult getCredentialUsageRights(String credentialAlias) throws DocAssuranceServiceException {
		// TODO Auto-generated method stub
		return null;
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

	private Response postToServer(WebTarget localTarget, final FormDataMultiPart multipart) {
		javax.ws.rs.client.Invocation.Builder invokeBuilder = localTarget.request().accept(APPLICATION_PDF);
		if (this.correlationIdFn != null) {
			invokeBuilder.header(CORRELATION_ID_HTTP_HDR, this.correlationIdFn.get());
		}
		Response result = invokeBuilder.post(Entity.entity(multipart, multipart.getMediaType()));
		return result;
	}
	
	
	/**
	 * Creates a Builder object for building a RestServicesFormServiceAdapter object.
	 * 
	 * @return build object
	 */
	public static DocAssuranceServiceBuilder builder() {
		return new DocAssuranceServiceBuilder();
	}
	
	public static class DocAssuranceServiceBuilder implements Builder {
		private BuilderImpl builder = new BuilderImpl();
//		private final static Supplier<Client> defaultClientFactory = ()->ClientBuilder.newClient();
		
		private DocAssuranceServiceBuilder() {
			super();
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
		public DocAssuranceServiceBuilder clientFactory(Supplier<Client> clientFactory) {
			builder.clientFactory(clientFactory);
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
		public WebTarget createLocalTarget() {
			return builder.createLocalTarget();
		}

		public RestServicesDocAssuranceServiceAdapter build() {
			return new RestServicesDocAssuranceServiceAdapter(this.createLocalTarget(), this.getCorrelationIdFn());
		}
	}
	
	private static String toString(InputStream inputStream) throws IOException {
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length;
		while ((length = inputStream.read(buffer)) != -1) {
		    result.write(buffer, 0, length);
		}
		return result.toString(StandardCharsets.UTF_8.name());
	}

}
