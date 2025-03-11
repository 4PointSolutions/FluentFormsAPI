package com._4point.aem.docservices.rest_services.client.docassurance;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com._4point.aem.docservices.rest_services.client.RestClient;
import com._4point.aem.docservices.rest_services.client.RestClient.ContentType;
import com._4point.aem.docservices.rest_services.client.RestClient.MultipartPayload;
import com._4point.aem.docservices.rest_services.client.RestClient.Response;
import com._4point.aem.docservices.rest_services.client.RestClient.RestClientException;
import com._4point.aem.docservices.rest_services.client.RestClient.GetRequest.Builder;
import com._4point.aem.docservices.rest_services.client.helpers.AemConfig;
import com._4point.aem.docservices.rest_services.client.helpers.AemServerType;
import com._4point.aem.docservices.rest_services.client.helpers.BuilderImpl.TriFunction;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.docassurance.DocAssuranceService.DocAssuranceServiceException;
import com._4point.aem.fluentforms.api.docassurance.EncryptionOptions;
import com._4point.aem.fluentforms.api.docassurance.ReaderExtensionOptions;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;
import com._4point.testing.matchers.javalang.ExceptionMatchers;
import com.adobe.fd.docassurance.client.api.SignatureOptions;
import com.adobe.fd.readerextensions.client.ReaderExtensionsOptionSpec;
import com.adobe.fd.readerextensions.client.UsageRights;
import com.adobe.fd.signatures.client.types.FieldMDPOptionSpec;
import com.adobe.fd.signatures.client.types.PDFSeedValueOptionSpec;
import com.adobe.fd.signatures.client.types.PDFSignatureFieldProperties;
import com.adobe.fd.signatures.client.types.PositionRectangle;
import com.adobe.fd.signatures.client.types.VerificationTime;
import com.adobe.fd.signatures.pdf.inputs.UnlockOptions;
import com.adobe.fd.signatures.pki.client.types.common.RevocationCheckStyle;

@ExtendWith(MockitoExtension.class)
public class RestServicesDocAssuranceServiceAdapterTest {

	private static final String TEST_MACHINE_NAME = "testmachinename";
	private static final int TEST_MACHINE_PORT = 8080;
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
	

	@Mock(stubOnly = true) TriFunction<AemConfig, String, Supplier<String>, RestClient> mockClientFactory;
	@Mock(stubOnly = true) RestClient mockClient;
	@Mock(stubOnly = true) MultipartPayload mockPayload;
	@Mock(stubOnly = true) MultipartPayload.Builder mockPayloadBuilder;
	@Mock(stubOnly = true) Response mockResponse;

	@Captor ArgumentCaptor<AemConfig> aemConfig;
	@Captor ArgumentCaptor<String> servicePath;
	@Captor ArgumentCaptor<InputStream> postBodyBytes;
	@Captor ArgumentCaptor<ContentType> acceptableContentType;
	@Captor ArgumentCaptor<Supplier<String>> correlationIdFn;

	// Functions called by RestClient transform methods
	@Captor ArgumentCaptor<Function<ReaderExtensionOptions, String>> getCredentialAliasFn;
	@Captor ArgumentCaptor<Function<ReaderExtensionsOptionSpec, String>> getMessageFn;
	@Captor ArgumentCaptor<Function<ReaderExtensionsOptionSpec, Boolean>> isModeFinalFn;
	@Captor ArgumentCaptor<Function<UsageRights, Boolean>> isEnabledBarcodeDecodingFn;
	@Captor ArgumentCaptor<Function<UsageRights, Boolean>> isEnabledCommentsFn;
	@Captor ArgumentCaptor<Function<UsageRights, Boolean>> isEnabledCommentsOnlineFn;
	@Captor ArgumentCaptor<Function<UsageRights, Boolean>> isEnabledDigitalSignaturesFn;
	@Captor ArgumentCaptor<Function<UsageRights, Boolean>> isEnabledDynamicFormFieldsFn;
	@Captor ArgumentCaptor<Function<UsageRights, Boolean>> isEnabledDynamicFormPagesFn;
	@Captor ArgumentCaptor<Function<UsageRights, Boolean>> isEnabledEmbeddedFilesFn;
	@Captor ArgumentCaptor<Function<UsageRights, Boolean>> isEnabledFormDataImportExportFn;
	@Captor ArgumentCaptor<Function<UsageRights, Boolean>> isEnabledFormFillInFn;
	@Captor ArgumentCaptor<Function<UsageRights, Boolean>> isEnabledOnlineFormsFn;
	@Captor ArgumentCaptor<Function<UsageRights, Boolean>> isEnabledSubmitStandaloneFn;
		

	RestServicesDocAssuranceServiceAdapter underTest;
	
	@BeforeEach
	void setUp() throws Exception {
		when(mockClientFactory.apply(aemConfig.capture(), servicePath.capture(), correlationIdFn.capture())).thenReturn(mockClient);
		underTest = createAdapter(mockClientFactory);
	}

	@Test
	void testSecureDocument_NonNullEncryptionOptions() throws Exception {
		Document pdf = MockDocumentFactory.GLOBAL_INSTANCE.create("pdf Document Data".getBytes());
		EncryptionOptions encryptionOptions = mock(EncryptionOptions.class);
		
		UnsupportedOperationException ex = assertThrows(UnsupportedOperationException.class,()->underTest.secureDocument(pdf, encryptionOptions , null, null, null));
		assertThat(ex, ExceptionMatchers.exceptionMsgContainsAll("Encryption", "support has not yet been added to FluentForms library"));
	}

	@Test
	void testSecureDocument_NonNullSignatureOptions() throws Exception {
		Document pdf = MockDocumentFactory.GLOBAL_INSTANCE.create("pdf Document Data".getBytes());
		SignatureOptions signatureOptions = mock(SignatureOptions.class);
		
		UnsupportedOperationException ex = assertThrows(UnsupportedOperationException.class,()->underTest.secureDocument(pdf, null, signatureOptions, null, null));
		assertThat(ex, ExceptionMatchers.exceptionMsgContainsAll("Digital Signature", "support has not yet been added to FluentForms library"));
	}

	@Test
	void testSecureDocument_NonNullUnlockOptions() throws Exception {
		Document pdf = MockDocumentFactory.GLOBAL_INSTANCE.create("pdf Document Data".getBytes());
		UnlockOptions unlockOptions = mock(UnlockOptions.class);
		
		UnsupportedOperationException ex = assertThrows(UnsupportedOperationException.class,()->underTest.secureDocument(pdf, null, null, null, unlockOptions));
		assertThat(ex, ExceptionMatchers.exceptionMsgContainsAll("Unlock Options", "support has not yet been added to FluentForms library"));
	}
	
	// Not passing in an input document will generate an error on AEM so we should catch it before we send it to AEM and produce a good error message.
	@Test
	void testSecureDocument_NullInputDocument() throws Exception {
		NullPointerException ex = assertThrows(NullPointerException.class,()->underTest.secureDocument(null, null, null, null, null));
		assertThat(ex, ExceptionMatchers.exceptionMsgContainsAll("Input document", "cannot be null"));
	}

	// With all options omitted, it should just pass the original document to AEM and receive back the original document.  No other parameters are passed.
	// No errors should be thrown.
	@Test
	void testSecureDocument_NullAllOptions() throws Exception {
		byte[] responseData = "Secure Document response Document Data".getBytes();
		Document pdf = MockDocumentFactory.GLOBAL_INSTANCE.create("pdf Document Data".getBytes());
		
		RestClient mockRestClient = mockRestClient(responseData);
		
		RestServicesDocAssuranceServiceAdapter localUnderTest = createAdapter((AemConfig __, String ___, Supplier<String> ____)->mockRestClient);
		Document pdfResult = localUnderTest.secureDocument(pdf, null, null, null, null);

		// Make sure the response is correct.
		assertArrayEquals(responseData, pdfResult.getInputStream().readAllBytes());
		assertEquals(ContentType.APPLICATION_PDF.contentType(), pdfResult.getContentType());
	}

	private RestClient mockRestClient(byte[] responseData) {
		return new RestClient() {
			
			@Override
			public String target() {
				return null;
			}
			
			@Override
			public MultipartPayload.Builder multipartPayloadBuilder() {
				return new MultipartPayload.Builder() {
					
					@Override
					public MultipartPayload.Builder queryParam(String name, String value) {
						throw new UnsupportedOperationException("queryParam() should never be called.");
					}
					
					@Override
					public MultipartPayload build() {
						return new MultipartPayload() {

							@Override
							public Optional<Response> postToServer(ContentType acceptContentType) {
								return setupMockResponse(responseData, ContentType.APPLICATION_PDF);
							}

							@Override
							public void close() throws IOException {
							}
						};
					}
					
					@Override
					public MultipartPayload.Builder addHeader(String name, String value) {
						throw new UnsupportedOperationException("addHeader() should never be called.");
					}
					
					@Override
					public MultipartPayload.Builder add(String fieldName, InputStream fieldData, ContentType contentType) {
						assertAll(
								()->assertEquals(DOCUMENT_PARAM, fieldName),
								// TODO:  Mock the getting of the input stream and validate it here.
								// ()->assertSame(??, fielData),
								()->assertEquals(ContentType.APPLICATION_PDF, contentType)
								);
						return this;
					}
					
					@Override
					public MultipartPayload.Builder add(String fieldName, byte[] fieldData, ContentType contentType) {
						throw new UnsupportedOperationException("This version of add() should never be called.");
					}
					
					@Override
					public MultipartPayload.Builder add(String fieldName, String fieldData) {
						throw new UnsupportedOperationException("This version of add() should never be called.");
					}
				};
			}
			
			@Override
			public Builder getRequestBuilder() {
				return null;
			}
		};
	}

	@Test
	void testSecureDocument_NullCredentialAlias() throws Exception {
		ReaderExtensionOptions reOptions = mock(ReaderExtensionOptions.class);
		Document pdf = MockDocumentFactory.GLOBAL_INSTANCE.create("pdf Document Data".getBytes());

		NullPointerException ex = assertThrows(NullPointerException.class,()->underTest.secureDocument(pdf, null, null, reOptions, null));
		assertThat(ex, ExceptionMatchers.exceptionMsgContainsAll("Reader Extensions credential alias", "cannot be null"));
	}

	@Test
	void testSecureDocument() throws Exception {
		byte[] responseData = "Secure Document response Document Data".getBytes();

		Document pdf = MockDocumentFactory.GLOBAL_INSTANCE.create("pdf Document Data".getBytes());
		ReaderExtensionOptions reOptions = mock(ReaderExtensionOptions.class);
		ReaderExtensionsOptionSpec reOptionsSpec = mock(ReaderExtensionsOptionSpec.class);
		UsageRights reUsageOptionsSpec = mock(UsageRights.class);
		when(reOptions.getReOptions()).thenReturn(reOptionsSpec);
		when(reOptionsSpec.getUsageRights()).thenReturn(reUsageOptionsSpec);
		//
		final String CREDENTIAL_ALIAS = "credentialAlias";
		final String USAGE_MESSAGE = "message";
		when(reOptions.getCredentialAlias()).thenReturn(CREDENTIAL_ALIAS);
		when(reOptionsSpec.getMessage()).thenReturn(USAGE_MESSAGE);
		when(reOptionsSpec.isModeFinal()).thenReturn(true);
		when(reUsageOptionsSpec.isEnabledBarcodeDecoding()).thenReturn(true);
		when(reUsageOptionsSpec.isEnabledComments()).thenReturn(true);
		when(reUsageOptionsSpec.isEnabledCommentsOnline()).thenReturn(true);
		when(reUsageOptionsSpec.isEnabledDigitalSignatures()).thenReturn(true);
		when(reUsageOptionsSpec.isEnabledDynamicFormFields()).thenReturn(true);
		when(reUsageOptionsSpec.isEnabledDynamicFormPages()).thenReturn(true);
		when(reUsageOptionsSpec.isEnabledEmbeddedFiles()).thenReturn(true);
		when(reUsageOptionsSpec.isEnabledFormDataImportExport()).thenReturn(true);
		when(reUsageOptionsSpec.isEnabledFormFillIn()).thenReturn(true);
		when(reUsageOptionsSpec.isEnabledOnlineForms()).thenReturn(true);
		when(reUsageOptionsSpec.isEnabledSubmitStandalone()).thenReturn(true);
		
		when(mockPayloadBuilder.add(eq(DOCUMENT_PARAM), same(pdf), eq(ContentType.APPLICATION_PDF))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addIfNotNull(eq(CREDENTIAL_ALIAS_PARAM), eq(CREDENTIAL_ALIAS))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.transformAndAdd(eq(MESSAGE_PARAM), same(reOptionsSpec), getMessageFn.capture())).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.transformAndAddStringVersion(eq(IS_MODE_FINAL_PARAM), same(reOptionsSpec), isModeFinalFn.capture())).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.transformAndAddStringVersion(eq(ENABLED_BARCODED_DECODING_PARAM), same(reUsageOptionsSpec), isEnabledBarcodeDecodingFn.capture())).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.transformAndAddStringVersion(eq(ENABLED_COMMENTS_PARAM), same(reUsageOptionsSpec), isEnabledCommentsFn.capture())).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.transformAndAddStringVersion(eq(ENABLED_COMMENTS_ONLINE_PARAM), same(reUsageOptionsSpec), isEnabledCommentsOnlineFn.capture())).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.transformAndAddStringVersion(eq(ENABLED_DIGITAL_SIGNATURES_PARAM), same(reUsageOptionsSpec), isEnabledDigitalSignaturesFn.capture())).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.transformAndAddStringVersion(eq(ENABLED_DYNAMIC_FORM_FIELDS_PARAM), same(reUsageOptionsSpec), isEnabledDynamicFormFieldsFn.capture())).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.transformAndAddStringVersion(eq(ENABLED_DYNAMIC_FORM_PAGES_PARAM), same(reUsageOptionsSpec), isEnabledDynamicFormPagesFn.capture())).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.transformAndAddStringVersion(eq(ENABLED_EMBEDDED_FILES_PARAM), same(reUsageOptionsSpec), isEnabledEmbeddedFilesFn.capture())).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.transformAndAddStringVersion(eq(ENABLED_FORM_DATA_IMPORT_EXPORT_PARAM), same(reUsageOptionsSpec), isEnabledFormDataImportExportFn.capture())).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.transformAndAddStringVersion(eq(ENABLED_FORM_FILL_IN_PARAM), same(reUsageOptionsSpec), isEnabledFormFillInFn.capture())).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.transformAndAddStringVersion(eq(ENABLED_ONLINE_FORMS_PARAM), same(reUsageOptionsSpec), isEnabledOnlineFormsFn.capture())).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.transformAndAddStringVersion(eq(ENABLED_SUBMIT_STANDALONE_PARAM), same(reUsageOptionsSpec), isEnabledSubmitStandaloneFn.capture())).thenReturn(mockPayloadBuilder);
		
		setupMocks(setupMockResponse(responseData, ContentType.APPLICATION_PDF));
	
		Document pdfResult = underTest.secureDocument(pdf, null, null, reOptions, null);

		// Make sure the response is correct.
		assertArrayEquals(responseData, pdfResult.getInputStream().readAllBytes());
		assertEquals(ContentType.APPLICATION_PDF.contentType(), pdfResult.getContentType());
		
		// Validate the correct RestClient transformation functions were passed in by calling the function passed in and then validating that the correct mock function was called..
		getMessageFn.getValue().apply(reOptionsSpec);
		verify(reOptionsSpec).getMessage();
		isModeFinalFn.getValue().apply(reOptionsSpec);
		verify(reOptionsSpec).isModeFinal();
		isEnabledBarcodeDecodingFn.getValue().apply(reUsageOptionsSpec);
		verify(reUsageOptionsSpec).isEnabledBarcodeDecoding();
		isEnabledCommentsFn.getValue().apply(reUsageOptionsSpec);
		verify(reUsageOptionsSpec).isEnabledComments();
		isEnabledCommentsOnlineFn.getValue().apply(reUsageOptionsSpec);
		verify(reUsageOptionsSpec).isEnabledCommentsOnline();
		isEnabledDigitalSignaturesFn.getValue().apply(reUsageOptionsSpec);
		verify(reUsageOptionsSpec).isEnabledDigitalSignatures();
		isEnabledDynamicFormFieldsFn.getValue().apply(reUsageOptionsSpec);
		verify(reUsageOptionsSpec).isEnabledDynamicFormFields();
		isEnabledDynamicFormPagesFn.getValue().apply(reUsageOptionsSpec);
		verify(reUsageOptionsSpec).isEnabledDynamicFormPages();
		isEnabledEmbeddedFilesFn.getValue().apply(reUsageOptionsSpec);
		verify(reUsageOptionsSpec).isEnabledEmbeddedFiles();
		isEnabledFormDataImportExportFn.getValue().apply(reUsageOptionsSpec);
		verify(reUsageOptionsSpec).isEnabledFormDataImportExport();
		isEnabledFormFillInFn.getValue().apply(reUsageOptionsSpec);
		verify(reUsageOptionsSpec).isEnabledFormFillIn();
		isEnabledOnlineFormsFn.getValue().apply(reUsageOptionsSpec);
		verify(reUsageOptionsSpec).isEnabledOnlineForms();
		isEnabledSubmitStandaloneFn.getValue().apply(reUsageOptionsSpec);
		verify(reUsageOptionsSpec).isEnabledSubmitStandalone();
	}

	@Test
	void testSecureDocument_RestClientException() throws Exception {
		var cause = new RestClientException("cause exception");
		var ex = mockForException(cause);
		
		assertThat(ex, allOf(ExceptionMatchers.exceptionMsgContainsAll("Error while POSTing to server"),
							 ExceptionMatchers.hasCause(cause)
							));
	}
	
	@Test
	void testSecureDocument_IOException() throws Exception {
		var cause = new IOException("cause exception");
		var ex = mockForException(cause);
		
		assertThat(ex, allOf(ExceptionMatchers.exceptionMsgContainsAll("I/O Error while securing document"),
							 ExceptionMatchers.hasCause(cause)
							));
	}
	
	<T extends Exception> DocAssuranceServiceException mockForException(T exception) throws Exception {
		Document pdf = MockDocumentFactory.GLOBAL_INSTANCE.create("pdf Document Data".getBytes());
		ReaderExtensionOptions reOptions = mock(ReaderExtensionOptions.class);
		ReaderExtensionsOptionSpec reOptionsSpec = mock(ReaderExtensionsOptionSpec.class);
		UsageRights reUsageOptionsSpec = mock(UsageRights.class);
		when(reOptions.getReOptions()).thenReturn(reOptionsSpec);
		when(reOptionsSpec.getUsageRights()).thenReturn(reUsageOptionsSpec);
		//
		final String CREDENTIAL_ALIAS = "credentialAlias";
		when(reOptions.getCredentialAlias()).thenReturn(CREDENTIAL_ALIAS);

		when(mockPayloadBuilder.add(eq(DOCUMENT_PARAM), same(pdf), eq(ContentType.APPLICATION_PDF))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addIfNotNull(eq(CREDENTIAL_ALIAS_PARAM), eq(CREDENTIAL_ALIAS))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.transformAndAdd(eq(MESSAGE_PARAM), same(reOptionsSpec), any())).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.transformAndAddStringVersion(any(), any(), any())).thenReturn(mockPayloadBuilder);

		when(mockClient.multipartPayloadBuilder()).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.build()).thenReturn(mockPayload);
		
		if (exception instanceof IOException) {
			when(mockResponse.contentType()).thenReturn(ContentType.APPLICATION_PDF);
			when(mockResponse.data()).thenReturn(new ByteArrayInputStream("Dummy response".getBytes()));
			when(mockPayload.postToServer(any())).thenReturn(Optional.of(mockResponse));
			Mockito.doThrow(exception).when(mockPayload).close();
		} else {
			when(mockPayload.postToServer(any())).thenThrow(exception);
		}
		
		return assertThrows(DocAssuranceServiceException.class, ()->underTest.secureDocument(pdf, null, null, reOptions, null));
	}

	private static RestServicesDocAssuranceServiceAdapter createAdapter(TriFunction<AemConfig, String, Supplier<String>, RestClient> clientFactory) {
		return RestServicesDocAssuranceServiceAdapter.builder(clientFactory)
											  .machineName(TEST_MACHINE_NAME)
											  .port(TEST_MACHINE_PORT)
											  .basicAuthentication("username", "password")
											  .useSsl(true)
											  .aemServerType(AemServerType.StandardType.JEE)
											  .build();
	}

	private void setupMocks(Optional<Response> mockedResponse) throws RestClientException {
		when(mockClient.multipartPayloadBuilder()).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.build()).thenReturn(mockPayload);
		when(mockPayload.postToServer(acceptableContentType.capture())).thenReturn(mockedResponse);
	}

	private Optional<Response> setupMockResponse(byte[] responseData, ContentType expectedContentType) {
		when(mockResponse.contentType()).thenReturn(expectedContentType);
		when(mockResponse.data()).thenReturn(new ByteArrayInputStream(responseData));
		return Optional.of(mockResponse);
	}

	@FunctionalInterface
	public interface Consumer_WithExceptions<T, E extends Exception> {
		void accept(T t) throws E;
	}
	   
	private void testForNotImplementedYetException(String fnName, 
			Consumer_WithExceptions<RestServicesDocAssuranceServiceAdapter, DocAssuranceServiceException> fn) {
		testForUnsupportedException(fn, allOf(containsString(fnName), containsString("is not implemented yet")));
	}

	private void testForUnsupportedException(
			Consumer_WithExceptions<RestServicesDocAssuranceServiceAdapter, DocAssuranceServiceException> fn,
			Matcher<String> matcher) {
		RestServicesDocAssuranceServiceAdapter underTest = RestServicesDocAssuranceServiceAdapter.builder(mockClientFactory).build();
		UnsupportedOperationException ex = assertThrows(UnsupportedOperationException.class, ()->fn.accept(underTest));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, matcher);
	}

	@Test
	void testGetDocumentUsageRights() {
		testForNotImplementedYetException("getDocumentUsageRights", underTest->underTest.getDocumentUsageRights(mock(Document.class), mock(UnlockOptions.class)));
	}

	@Test
	void testGetCredentialUsageRights() {
		testForNotImplementedYetException("getCredentialUsageRights", underTest->underTest.getCredentialUsageRights("credential"));
	}

	@Test
	void testAddInvisibleSignatureField() {
		testForNotImplementedYetException("addInvisibleSignatureField", underTest->underTest.addInvisibleSignatureField(mock(Document.class), "FieldName", mock(FieldMDPOptionSpec.class), mock(PDFSeedValueOptionSpec.class), mock(UnlockOptions.class)));
	}

	@Test
	void testAddSignatureField() {
		testForNotImplementedYetException("addSignatureField", underTest->underTest.addSignatureField(mock(Document.class), "FieldName", 2, mock(PositionRectangle.class), mock(FieldMDPOptionSpec.class), mock(PDFSeedValueOptionSpec.class), mock(UnlockOptions.class)));
	}

	@Test
	void testClearSignatureField() {
		testForNotImplementedYetException("clearSignatureField", underTest->underTest.clearSignatureField(mock(Document.class), "FieldName", mock(UnlockOptions.class)));
	}

	@Test
	void testGetCertifyingSignatureField() {
		testForNotImplementedYetException("getCertifyingSignatureField", underTest->underTest.getCertifyingSignatureField(mock(Document.class), mock(UnlockOptions.class)));
	}

	@Test
	void testGetSignature() {
		testForNotImplementedYetException("getSignature", underTest->underTest.getSignature(mock(Document.class), "FieldName", mock(UnlockOptions.class)));
	}

	@Test
	void testGetSignatureFieldList() {
		testForNotImplementedYetException("getSignatureFieldList", underTest->underTest.getSignatureFieldList(mock(Document.class), mock(UnlockOptions.class) ));
	}

	@Test
	void testModifySignatureField() {
		testForNotImplementedYetException("modifySignatureField", underTest->underTest.modifySignatureField(mock(Document.class), "FieldName", mock(PDFSignatureFieldProperties.class), mock(UnlockOptions.class)));
	}

	@Test
	void testRemoveSignatureField() {
		testForNotImplementedYetException("removeSignatureField", underTest->underTest.removeSignatureField(mock(Document.class), "FieldName", mock(UnlockOptions.class)));
	}

	@ParameterizedTest
	@MethodSource("verifyEnums")
	void testVerify(RevocationCheckStyle revocationCheckStyle, VerificationTime verificationTime) {
		testForNotImplementedYetException("verify", underTest->underTest.verify(mock(Document.class), "FieldName",  revocationCheckStyle, verificationTime, null ));
	}

	@Test
	void testGetPDFEncryption() {
		testForNotImplementedYetException("getPDFEncryption", underTest->underTest.getPDFEncryption(mock(Document.class)));
	}

	@Test
	void testRemovePDFCertificateSecurity() {
		testForNotImplementedYetException("removePDFCertificateSecurity", underTest->underTest.removePDFCertificateSecurity(mock(Document.class), "alias"));
	}

	@Test
	void testRemovePDFPasswordSecurity() {
		testForNotImplementedYetException("removePDFPasswordSecurity", underTest->underTest.removePDFPasswordSecurity(mock(Document.class), "password" ));
	}

	@ParameterizedTest
	@MethodSource("verifyEnums")
	void testVerifyDocument(RevocationCheckStyle revocationCheckStyle, VerificationTime verificationTime) {
		testForNotImplementedYetException("verifyDocument", underTest->underTest.verifyDocument(mock(Document.class), revocationCheckStyle, verificationTime, null));
	}

	@Test
	void testRemoveUsageRights() {
		testForNotImplementedYetException("removeUsageRights", underTest->underTest.removeUsageRights(mock(Document.class), mock(UnlockOptions.class)));
	}

	@Test
	void testApplyDocumentTimeStamp() {
		testForNotImplementedYetException("applyDocumentTimeStamp", underTest->underTest.applyDocumentTimeStamp(mock(Document.class), VerificationTime.SIGNING_TIME, null, mock(UnlockOptions.class)));
	}

	static Stream<Arguments> verifyEnums() {
		java.util.stream.Stream.Builder<Arguments> builder = Stream.builder();
		for (RevocationCheckStyle rcs : RevocationCheckStyle.values()) {
			for (VerificationTime vt: VerificationTime.values()) {
				builder.accept(arguments(rcs, vt));
			}
		}
		return builder.build();
	}
	
	static class DocAssuranceServiceBuilderTests {
		@Mock RestClient mockClient;

		@Test
		void testNonDefaultValues() throws Exception {
			RestServicesDocAssuranceServiceAdapter.builder(this::validateNonDefaultValues)
					.machineName(TEST_MACHINE_NAME)
					.port(TEST_MACHINE_PORT)
					.basicAuthentication("username", "password")
					.useSsl(true)
					.aemServerType(AemServerType.StandardType.JEE)
					.correlationId(()->"correlationid")
					.build();
		}
		
		RestClient validateNonDefaultValues(AemConfig aemConfig, String target, Supplier<String> correlationidFn) {
			assertAll(
					()->assertEquals("correlationid", correlationidFn.get()),
					()->assertEquals("username", aemConfig.user()),
					()->assertEquals("password", aemConfig.password()),
					()->assertEquals("https://testmachinename:8080/", aemConfig.url()),
					()->assertThat(target, anyOf(equalTo("/lc/services/DocAssuranceService/SecureDocument")
//												 equalTo("/lc/services/DocAssuranceService/GetDocumentUsageRights"),
												 ))
					);
			return mockClient;
		}
		
		@Test
		void testDefaultValues() throws Exception {
			RestServicesDocAssuranceServiceAdapter.builder(this::validateDefaultValues).build();
		}

		RestClient validateDefaultValues(AemConfig aemConfig, String target, Supplier<String> correlationidFn) {
			assertAll(
					()->assertNull(correlationidFn),
					()->assertEquals("admin", aemConfig.user()),
					()->assertEquals("admin", aemConfig.password()),
					()->assertEquals("http://localhost:4502/", aemConfig.url()),
					()->assertThat(target, anyOf(equalTo("/services/DocAssuranceService/SecureDocument")
//							 					 equalTo("/services/DocAssuranceService/GetDocumentUsageRights"),
							 					 ))
					);
			return mockClient;
		}
	}

}
