
package com._4point.aem.docservices.rest_services.client.generatePDF;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
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
import com._4point.aem.docservices.rest_services.client.helpers.AemConfig;
import com._4point.aem.docservices.rest_services.client.helpers.AemServerType;
import com._4point.aem.docservices.rest_services.client.helpers.Builder.RestClientFactory;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.generatePDF.CreatePDFResult;
import com._4point.aem.fluentforms.api.generatePDF.GeneratePDFService.GeneratePDFServiceException;
import com._4point.aem.fluentforms.impl.generatePDF.PDFSettings;
import com._4point.aem.fluentforms.impl.generatePDF.SecuritySettings;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;

@ExtendWith(MockitoExtension.class)
public class RestServicesGeneratePDFServiceAdapterTest {
	private static final String CORRELATION_ID = "correlationId";
	private static final String TEST_MACHINE_NAME = "testmachinename";
	private static final int TEST_MACHINE_PORT = 8080;

	@Mock(stubOnly = true) RestClientFactory mockClientFactory;
	@Mock(stubOnly = true) RestClient mockClient;
	@Mock(stubOnly = true) MultipartPayload mockPayload;
	@Mock(stubOnly = true) MultipartPayload.Builder mockPayloadBuilder;
	@Mock(stubOnly = true) Response mockResponse;

	@Captor ArgumentCaptor<AemConfig> aemConfig;
	@Captor ArgumentCaptor<String> servicePath;
	@Captor ArgumentCaptor<InputStream> postBodyBytes;
	@Captor ArgumentCaptor<ContentType> acceptableContentType;
	@Captor ArgumentCaptor<Supplier<String>> correlationIdFn;
	
	@Captor ArgumentCaptor<Document> inputDoc;
	@Captor ArgumentCaptor<String> inputFileExtension;
	@Captor ArgumentCaptor<String> fileTypeSettings;
	@Captor ArgumentCaptor<PDFSettings> pdfSettings;
	@Captor ArgumentCaptor<SecuritySettings> securitySettings;
	@Captor ArgumentCaptor<Document> settingsDoc;
	@Captor ArgumentCaptor<Document> xmpDoc;
	

	@BeforeEach
	void setup() {
		when(mockClientFactory.apply(aemConfig.capture(), servicePath.capture(), correlationIdFn.capture())).thenReturn(mockClient);
	}


	RestServicesGeneratePDFServiceAdapter underTest;
	// Result documents.  Populated version has "src\test\resources\SampleForms\SampleForm.docx" in it.
	private static final String POPULATED_CREATE_PDF_RESULT_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><createPDFResult><createdDoc createdDocValue=\"c3JjXHRlc3RccmVzb3VyY2VzXFNhbXBsZUZvcm1zXFNhbXBsZUZvcm0uZG9jeA==\"/><logDoc/></createPDFResult>";
	private static final String EMPTY_CREATE_PDF_RESULT_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><createPDFResult><createdDoc/><logDoc/></createPDFResult>";

	@Test
	void testCreatePDF() throws Exception {
		// Given
		byte[] responseData = POPULATED_CREATE_PDF_RESULT_XML.getBytes();
		Document inputDoc = MockDocumentFactory.GLOBAL_INSTANCE.create("document Data".getBytes());
		Document settingsDoc = null;
		Document xmpDoc = null;


		underTest = RestServicesGeneratePDFServiceAdapter.builder(mockClientFactory)
														 .machineName(TEST_MACHINE_NAME)
														 .port(TEST_MACHINE_PORT)
														 .basicAuthentication("username", "password")
														 .useSsl(false)
														 .aemServerType(AemServerType.StandardType.OSGI)
														 .correlationId(() -> CORRELATION_ID)
														 .build();
		setupPayloadBuilderMocks(inputDoc, "docx", "Filetype Settings",
				PDFSettings.PDFA1b_2005_RGB, SecuritySettings.Certificate_Security, settingsDoc, xmpDoc);
		setupMocksForSuccess(setupMockResponse(responseData, ContentType.APPLICATION_XML));

		// when
		CreatePDFResult result = underTest.createPDF2(inputDoc, "docx", "Filetype Settings",
						PDFSettings.PDFA1b_2005_RGB, SecuritySettings.Certificate_Security, settingsDoc, xmpDoc);

		// then 
		// check that result was returned as expected
		validatePopulatedResult(result); // Validate that the arguments were populated as expected.

		// Make sure the correct URL is called.
		assertThat("Expected target url contains 'GeneratePDFService' and 'CreatePDF'", servicePath.getValue(), allOf(containsString("GeneratePDFService"), containsString("CreatePDF")));

		// Make sure the response is returned transparently in the returned Document.
		assertArrayEquals("src\\test\\resources\\SampleForms\\SampleForm.docx".getBytes(), result.getCreatedDocument().getInputStream().readAllBytes());
		assertEquals(ContentType.APPLICATION_PDF.contentType(), result.getCreatedDocument().getContentType());

		// Make sure correlation ID fn was passed correctly.
		assertEquals(CORRELATION_ID, correlationIdFn.getValue().get());
	}

	private void setupMocksForSuccess(Optional<Response> mockedResponse)  {
		try {
			when(mockPayload.postToServer(acceptableContentType.capture())).thenReturn(mockedResponse);
		} catch (RestClientException e) {
			throw new IllegalStateException("Exception while setting up RestClientMocks.", e);
		}
	}

	private void setupMocksForFailure(String exceptionMsg) {
		try {
			when(mockPayload.postToServer(acceptableContentType.capture())).thenThrow(new RestClientException(exceptionMsg));
		} catch (RestClientException e) {
			throw new IllegalStateException("Exception while setting up RestClientMocks.", e);
		}
	}
	
	private void setupPayloadBuilderMocks(Document inputDoc, String inputFileExtension, String fileTypeSettings,
            PDFSettings pdfSettings, SecuritySettings securitySettings, Document settingsDoc, Document xmpDoc) {
		// Using multiple when() calls to mock the fluent interface of the MultipartPayload.Builder instead of Deep Miocito Mocks
		// (as outlined here: https://www.baeldung.com/mockito-fluent-apis) because they are easier to debug and they produce better error messages
		// when things go wrong.
		when(mockClient.multipartPayloadBuilder()).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.add(eq("data"), same(inputDoc), eq(ContentType.APPLICATION_OCTET_STREAM))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.add(eq("fileExtension"), eq(inputFileExtension))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addIfNotNull(eq("fileTypeSettings"), eq(fileTypeSettings))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStringVersion(eq("pdfSettings"), eq(pdfSettings))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStringVersion(eq("securitySettings"), eq(securitySettings))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addIfNotNull(eq("settingDoc"), eq(settingsDoc), eq(ContentType.APPLICATION_XML))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addIfNotNull(eq("xmpDoc"), eq(xmpDoc), eq( ContentType.APPLICATION_XML))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.build()).thenReturn(mockPayload);
	}
	
	private Optional<Response> setupMockResponse(byte[] responseData, ContentType expectedContentType) {
		when(mockResponse.contentType()).thenReturn(expectedContentType);
		when(mockResponse.data()).thenReturn(new ByteArrayInputStream(responseData));
		return Optional.of(mockResponse);
	}


	private void validatePopulatedResult(CreatePDFResult result) {
		assertFalse(result.getCreatedDocument().isEmpty());
		assertTrue(result.getLogDocument().isEmpty());
	}

	private enum ErrorResponseScenario {
		INTERNAL_SERVER_ERROR(ErrorResponseScenario::setupMocksForInternalError,
							  ErrorResponseScenario::validateInternalServerError),
		NO_ENTITY(ErrorResponseScenario::setupMocksForNoEntity, 
				  ErrorResponseScenario::validateNoContent),
		HTML_RESPONSE(ErrorResponseScenario::setupMocksForHtmlResponse, 
					  ErrorResponseScenario::validateHtmlResponse),
		;

		private static final String INTERNAL_SERVER_ERROR_MSG = "InternalServer Error";
		Consumer<RestServicesGeneratePDFServiceAdapterTest> restClientMocksSetup;
		BiConsumer<String, String> validateExceptionMessages;

		private ErrorResponseScenario(Consumer<RestServicesGeneratePDFServiceAdapterTest> restClientMocksSetup,
				BiConsumer<String, String> validateExceptionMessages) {
			this.restClientMocksSetup = restClientMocksSetup;
			this.validateExceptionMessages = validateExceptionMessages;
		}

		private static void setupMocksForInternalError(RestServicesGeneratePDFServiceAdapterTest test) {
			test.setupMocksForFailure(INTERNAL_SERVER_ERROR_MSG);
			when(test.mockClient.target()).thenReturn("http://localhost:8080/services/GeneratePDFService/CreatePDF");
		}
		
		private static void setupMocksForNoEntity(RestServicesGeneratePDFServiceAdapterTest test) {
			test.setupMocksForSuccess(Optional.empty());
		}
		
		private static void setupMocksForHtmlResponse(RestServicesGeneratePDFServiceAdapterTest test) {
			test.setupMocksForFailure("Response from AEM server was not of expected type (" + ContentType.APPLICATION_XML + ").  " + ContentType.TEXT_HTML + "'.");
			when(test.mockClient.target()).thenReturn("http://localhost:8080/services/GeneratePDFService/CreatePDF");
        }
		
		private static void validateInternalServerError(String exMsg, String causeMsg) {
			assertNotNull(causeMsg);
			assertAll(
					()->assertThat(exMsg, allOf(containsStringIgnoringCase("Error while POSTing to server"),	// Contains error msg 
												containsStringIgnoringCase("GeneratePDFService"),				// Contains service name
												containsStringIgnoringCase("CreatePDF")							// Contains operation name
											    )),
					()->assertThat(causeMsg, containsStringIgnoringCase(INTERNAL_SERVER_ERROR_MSG))
					);
		}

		private static void validateNoContent(String exMsg, String causeMsg) {
			assertAll(
					() -> assertThat(exMsg, containsStringIgnoringCase("empty response from AEM server")),
					() -> assertNull(causeMsg)
					);
		}

		private static void validateHtmlResponse(String exMsg, String causeMsg) {
			assertNotNull(causeMsg);
			assertAll(
					()->assertThat(exMsg, allOf(containsStringIgnoringCase("Error while POSTing to server"),	// Contains error msg 
												containsStringIgnoringCase("GeneratePDFService"),				// Contains service name
												containsStringIgnoringCase("CreatePDF")							// Contains operation name
											    )),
					()->assertThat(causeMsg, containsStringIgnoringCase("Response from AEM server was not of expected type"))
					);
		}
	}

	@ParameterizedTest
	@EnumSource
	void testCreatePDFServerErrorScenarios(ErrorResponseScenario scenario) throws Exception {
		// Given
		byte[] inputBytes = "document Data".getBytes();
		Document inputDoc = MockDocumentFactory.GLOBAL_INSTANCE.create(inputBytes);
		Document settingsDoc = null;
		Document xmpDoc = null;
		underTest = RestServicesGeneratePDFServiceAdapter.builder(mockClientFactory)
														 .machineName(TEST_MACHINE_NAME)
														 .port(TEST_MACHINE_PORT)
														 .basicAuthentication("username", "password")
														 .useSsl(false)
														 .build();
		scenario.restClientMocksSetup.accept(this);

		setupPayloadBuilderMocks(inputDoc, "pptx", "Filetype Settings",
				PDFSettings.PDFA1b_2005_RGB, SecuritySettings.Certificate_Security, settingsDoc, xmpDoc);

		// when
		GeneratePDFServiceException ex = assertThrows(GeneratePDFServiceException.class,
				() -> underTest.createPDF2(inputDoc, "pptx", "Filetype Settings", PDFSettings.PDFA1b_2005_RGB,
						SecuritySettings.Certificate_Security, settingsDoc, xmpDoc));

		// then
		String msg = ex.getMessage();
		assertNotNull(msg);
		Throwable cause = ex.getCause();
		scenario.validateExceptionMessages.accept(msg, cause != null ? cause.getMessage() : null);
	}

	@SuppressWarnings("unchecked")
	@Test
	void testConvertXmlToCreatePDFResult() throws Exception {
		Mockito.reset(mockClientFactory);		// Don't need any mocks, so just reset them.
		CreatePDFResult createPDFResult = RestServicesGeneratePDFServiceAdapter
				.convertXmlToCreatePDFResult(new ByteArrayInputStream(POPULATED_CREATE_PDF_RESULT_XML.getBytes()));
		validatePopulatedResult(createPDFResult);
	}

	@SuppressWarnings("unchecked")
	@Test
	void testConvertXmlToAssemblerEmptyResult() throws Exception {
		Mockito.reset(mockClientFactory);		// Don't need any mocks, so just reset them.
		CreatePDFResult result = RestServicesGeneratePDFServiceAdapter
				.convertXmlToCreatePDFResult(new ByteArrayInputStream(EMPTY_CREATE_PDF_RESULT_XML.getBytes()));
		validateEmptyResult(result);
	}

	private void validateEmptyResult(CreatePDFResult result) {
		assertTrue(result.getCreatedDocument().isEmpty());
		assertTrue(result.getLogDocument().isEmpty());
	}
	
	private static final Document INPUT_DOC_ARG = MockDocumentFactory.GLOBAL_DUMMY_DOCUMENT;

	private static final String FILE_EXTENSION_ARG = null;
	
	private static final String  FILE_TYPE_SETTINGS_ARG = null;

    private static final PDFSettings PDF_SETTINGS_ARG = null; 
    
    private static SecuritySettings SECURITY_SETTINGS_ARG = null; 
    
    private static Document SETTING_DOC_ARG = null; 
    
    private static Document XMP_DOC_ARG = null;
    

	private enum NullArgumentTest {
		INPUT_DOC_NULL("inputDoc", null, FILE_EXTENSION_ARG, FILE_TYPE_SETTINGS_ARG,
				PDF_SETTINGS_ARG, SECURITY_SETTINGS_ARG , SETTING_DOC_ARG, XMP_DOC_ARG,
				test -> {
					when(test.mockClient.multipartPayloadBuilder()).thenReturn(test.mockPayloadBuilder);
				}
				),
		FILE_EXTENSION_NULL("file extension", INPUT_DOC_ARG, null, FILE_TYPE_SETTINGS_ARG,
				PDF_SETTINGS_ARG, SECURITY_SETTINGS_ARG , SETTING_DOC_ARG, XMP_DOC_ARG, 
				test->{
					when(test.mockClient.multipartPayloadBuilder()).thenReturn(test.mockPayloadBuilder);
					when(test.mockPayloadBuilder.add(eq("data"), any(Document.class), eq(ContentType.APPLICATION_OCTET_STREAM))).thenReturn(test.mockPayloadBuilder);
				});

		final String argName;
		final Document inputDoc;
		final String inputFileExtension;
		final String fileTypeSettings;
		final PDFSettings pdfSettings;
		final SecuritySettings securitySettings;
		final Document settingsDoc;
		final Document xmpDoc;
		final Consumer<RestServicesGeneratePDFServiceAdapterTest> setupMocks;

		private NullArgumentTest(String argName, Document inputDoc, String inputFileExtension, String fileTypeSettings,
				PDFSettings pdfSettings, SecuritySettings securitySettings, Document settingsDoc, Document xmpDoc, Consumer<RestServicesGeneratePDFServiceAdapterTest> setupMocks) {
			this.argName = argName;
			this.inputDoc = inputDoc;
			this.inputFileExtension = inputFileExtension;
			this.fileTypeSettings = fileTypeSettings;
			this.pdfSettings = pdfSettings;
			this.securitySettings = securitySettings;
			this.settingsDoc = settingsDoc;
			this.xmpDoc = xmpDoc;
			this.setupMocks = setupMocks;
		}		
	}
	
	@ParameterizedTest
	@EnumSource
	void testNullArgs(NullArgumentTest scenario) throws Exception {
		underTest = RestServicesGeneratePDFServiceAdapter.builder(mockClientFactory).build();
		scenario.setupMocks.accept(this);
		NullPointerException ex = assertThrows(NullPointerException.class,()->underTest.createPDF2(scenario.inputDoc, scenario.inputFileExtension,
				scenario.fileTypeSettings, scenario.pdfSettings, scenario.securitySettings, scenario.settingsDoc, scenario.xmpDoc));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertAll(
				()->assertThat(msg, containsStringIgnoringCase(scenario.argName)),
				()->assertThat(msg, containsStringIgnoringCase("can not be null"))
			);
	}
}
