package com._4point.aem.docservices.rest_services.client.assembler;

import static org.hamcrest.CoreMatchers.containsStringIgnoringCase;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.Response.StatusType;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com._4point.aem.docservices.rest_services.client.assembler.RestServicesDocAssemblerServiceAdapter.AssemblerServiceBuilder;
import com._4point.aem.docservices.rest_services.client.helpers.AemServerType;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.assembler.AssemblerOptionsSpec;
import com._4point.aem.fluentforms.api.assembler.AssemblerResult;
import com._4point.aem.fluentforms.api.assembler.LogLevel;
import com._4point.aem.fluentforms.api.assembler.PDFAConversionOptionSpec;
import com._4point.aem.fluentforms.api.assembler.PDFAConversionResult;
import com._4point.aem.fluentforms.api.assembler.PDFAValidationOptionSpec;
import com._4point.aem.fluentforms.api.assembler.AssemblerService.AssemblerServiceException;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;
import com._4point.aem.fluentforms.impl.assembler.AssemblerOptionsSpecImpl;
import com._4point.aem.fluentforms.impl.assembler.PDFAConversionOptionSpecImpl;
import com._4point.aem.fluentforms.impl.assembler.PDFAValidationOptionSpecImpl;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;
import com.adobe.fd.assembler.client.OperationException;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.ColorSpace;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.Compliance;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.OptionalContent;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.ResultLevel;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.Signatures;

@ExtendWith(MockitoExtension.class)
class RestServicesDocAssemblerServiceAdapterTest {
	private static final String CORRELATION_ID_HTTP_HDR = "X-Correlation-ID";
	private static final String CORRELATION_ID = "correlationId";
	private static final String TEST_MACHINE_NAME = "testmachinename";
	private static final int TEST_MACHINE_PORT = 8080;

	private static final MediaType APPLICATION_PDF = new MediaType("application", "pdf");
	private static final MediaType APPLICATION_XML = new MediaType("application", "xml");
	
	@Mock(answer = Answers.RETURNS_SELF) Client client;	// answers used to mock Client's fluent interface. 
	@Mock WebTarget target;
	@Mock Response response;
	@Mock Builder builder;
	@Mock StatusType statusType;
	
	@Captor ArgumentCaptor<String> machineName;
	@Captor ArgumentCaptor<String> path;
	@SuppressWarnings("rawtypes")
	@Captor ArgumentCaptor<Entity> entity;
	@Captor ArgumentCaptor<String> correlationId;

	private static final String POPULATED_ASSEMBLER_RESULT_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><assemblerResult><resultDocument documentName=\"concatenatedPDF.pdf\"><mergedDoc>dGVzdERvUG9zdCBIYXBweSBQYXRoIFJlc3VsdA==</mergedDoc></resultDocument><failedBlockNames><failedBlockName>failedBlock1</failedBlockName><failedBlockName>failedBlock2</failedBlockName></failedBlockNames><successfulDocumentNames><successfulDocumentName>successDocument1</successfulDocumentName><successfulDocumentName>successDocument2</successfulDocumentName></successfulDocumentNames><successfulBlockNames><successfulBlockName>successBlock1</successfulBlockName><successfulBlockName>successBlock2</successfulBlockName></successfulBlockNames><latestBatesNumber value=\"2\"/><numRequestedBlocks value=\"3\"/><multipleResultBlocks name=\"document\"><documentNames><documentName>test1</documentName><documentName>test2</documentName></documentNames></multipleResultBlocks><jobLog joblogValue=\"SU5GTw==\"/></assemblerResult>";
	private static final String EMPTY_ASSEMBLER_RESULT_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><assemblerResult><failedBlockNames/><successfulDocumentNames/><successfulBlockNames/><latestBatesNumber value=\"0\"/><numRequestedBlocks value=\"0\"/><jobLog/></assemblerResult>";
	private static final String POPULATED_ASSEMBLER_RESULT_XML_WITH_CONTENT_TYPE = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><assemblerResult><resultDocument documentName=\"concatenatedPDF.pdf\" contentType=\"application/xml\"><mergedDoc>dGVzdERvUG9zdCBIYXBweSBQYXRoIFJlc3VsdA==</mergedDoc></resultDocument><failedBlockNames><failedBlockName>failedBlock1</failedBlockName><failedBlockName>failedBlock2</failedBlockName></failedBlockNames><successfulDocumentNames><successfulDocumentName>successDocument1</successfulDocumentName><successfulDocumentName>successDocument2</successfulDocumentName></successfulDocumentNames><successfulBlockNames><successfulBlockName>successBlock1</successfulBlockName><successfulBlockName>successBlock2</successfulBlockName></successfulBlockNames><latestBatesNumber value=\"2\"/><numRequestedBlocks value=\"3\"/><multipleResultBlocks name=\"document\"><documentNames><documentName>test1</documentName><documentName>test2</documentName></documentNames></multipleResultBlocks><jobLog joblogValue=\"SU5GTw==\"/></assemblerResult>";

	private enum InvokeHappyPathScenario {
		NULL_ASSEMBLER_OPTIONS(()->null, InvokeHappyPathScenario::validateEmptySpec),
		EMPTY_ASSEMBLER_OPTIONS(InvokeHappyPathScenario::emptySpec, InvokeHappyPathScenario::validateEmptySpec),
		FULL_ASSEMBLER_OPTIONS(InvokeHappyPathScenario::fullSpec, InvokeHappyPathScenario::validateFullSpec);
		
		private static final boolean FAIL_ON_ERROR_VALUE = false;
		private static final boolean VALIDATE_ONLY_VALUE = false;
		private static final boolean TAKE_OWNERSHIP_VALUE = false;
		private static final LogLevel LOG_LEVEL_VALUE = LogLevel.FINE;
		private static final String DEFAULT_STYLE_VALUE = "DefaultStyleValue";
		private static final int FIRST_BATES_NUMBER_VALUE = 23;
		
		Supplier<AssemblerOptionsSpec> assemblerSpec;
		Consumer<FormDataMultiPart> assemblerSpecValidator;

		private InvokeHappyPathScenario(Supplier<AssemblerOptionsSpec> assemblerSpec,
				Consumer<FormDataMultiPart> assemblerSpecValidator) {
			this.assemblerSpec = assemblerSpec;
			this.assemblerSpecValidator = assemblerSpecValidator;
		}

		private static final AssemblerOptionsSpec emptySpec() {
			return new AssemblerOptionsSpecImpl();
		}
		
		private static final void validateEmptySpec(FormDataMultiPart postedData) {
			// Since we passed in an uninitialized structure, we expect these fields not to be passed in.
			validateFormFieldDoesNotExist(postedData, "isFailOnError");
			validateFormFieldDoesNotExist(postedData, "isValidateOnly");
			validateFormFieldDoesNotExist(postedData, "isTakeOwnerShip");
			validateFormFieldDoesNotExist(postedData, "jobLogLevel");
			validateFormFieldDoesNotExist(postedData, "defaultStyle");
			validateFormFieldDoesNotExist(postedData, "firstBatesNum");
		}
		
		private static final AssemblerOptionsSpec fullSpec() {
			AssemblerOptionsSpecImpl options = new AssemblerOptionsSpecImpl();
			options.setFailOnError(FAIL_ON_ERROR_VALUE);
			options.setValidateOnly(VALIDATE_ONLY_VALUE);
			options.setTakeOwnership(TAKE_OWNERSHIP_VALUE);
			options.setLogLevel(LOG_LEVEL_VALUE);
			options.setDefaultStyle(DEFAULT_STYLE_VALUE);
			options.setFirstBatesNumber(FIRST_BATES_NUMBER_VALUE);
			return options;
		}

		private static final void validateFullSpec(FormDataMultiPart postedData) {
			try {
				validateTextFormField(postedData, "isFailOnError", Boolean.toString(FAIL_ON_ERROR_VALUE));
				validateTextFormField(postedData, "isValidateOnly", Boolean.toString(VALIDATE_ONLY_VALUE));
				validateTextFormField(postedData, "isTakeOwnerShip", Boolean.toString(TAKE_OWNERSHIP_VALUE));
				validateTextFormField(postedData, "jobLogLevel", LOG_LEVEL_VALUE.toString());
				validateTextFormField(postedData, "defaultStyle", DEFAULT_STYLE_VALUE);
				validateTextFormField(postedData, "firstBatesNum", Integer.toString(FIRST_BATES_NUMBER_VALUE));
			} catch (IOException e) {
				throw new IllegalStateException("IO Exception while validating options spec fields.", e);
			}
		}
	}
	
	@ParameterizedTest
	@EnumSource
	void testInvoke(InvokeHappyPathScenario scenario) throws Exception {
		Document responseData = MockDocumentFactory.GLOBAL_INSTANCE.create(POPULATED_ASSEMBLER_RESULT_XML.getBytes());

		RestServicesDocAssemblerServiceAdapter underTest = createAdapter(true, responseData, APPLICATION_XML);
		
		AssemblerOptionsSpec assemblerOptionSpec = scenario.assemblerSpec.get();
		
		byte[] srcDocumentDataBytes = "source Document Data".getBytes();
		Map<String, Object> sourceDocuments = 
				Collections.singletonMap("SourceDocument1", MockDocumentFactory.GLOBAL_INSTANCE.create(srcDocumentDataBytes));
		byte[] ddxBytes = "ddx Document Data".getBytes();
		Document ddx = MockDocumentFactory.GLOBAL_INSTANCE.create(ddxBytes);
		
		AssemblerResult result = underTest.invoke(ddx, sourceDocuments, assemblerOptionSpec);
		
		// Validate the result was returned as expected.
		validatePopulatedResult(result, "application/pdf");
		
		// Validate that the arguments were populated as expected.
		// Make sure that the arguments we passed in are transmitted correctly.
		@SuppressWarnings("unchecked")
		Entity<FormDataMultiPart> postedEntity = (Entity<FormDataMultiPart>)entity.getValue();
		FormDataMultiPart postedData = postedEntity.getEntity();
		
		assertEquals(MediaType.MULTIPART_FORM_DATA_TYPE, postedEntity.getMediaType());
		validateDocumentFormField(postedData, "ddx", MediaType.APPLICATION_XML_TYPE, ddxBytes);
		
		// validate the source documents
		validateTextFormField(postedData, "sourceDocumentMap.key", "SourceDocument1");
		validateDocumentFormField(postedData, "sourceDocumentMap.value", APPLICATION_PDF, srcDocumentDataBytes);
		
		// validate the assemblerOptions
		scenario.assemblerSpecValidator.accept(postedData);
	}

	private RestServicesDocAssemblerServiceAdapter createAdapter(boolean setupCorrelationId, Document responseData, MediaType produces) throws IOException {
		return createAdapter(setupCorrelationId, responseData, produces, produces, Response.Status.OK);
	}
		
	private RestServicesDocAssemblerServiceAdapter createAdapter(boolean setupCorrelationId, Document responseData, MediaType accepts, MediaType produces, Response.Status status) throws IOException {
		setupRestClientMocks(setupCorrelationId, responseData, accepts, produces, status);
		
		return 	RestServicesDocAssemblerServiceAdapter.builder()
													  .machineName(TEST_MACHINE_NAME)
													  .port(TEST_MACHINE_PORT)
													  .basicAuthentication("username", "password")
													  .useSsl(false)
													  .aemServerType(AemServerType.StandardType.JEE)
													  .correlationId(()->CORRELATION_ID)
													  .clientFactory(()->client)
													  .build();
	}
	
	private void setupRestClientMocks(boolean setupCorrelationId, Document responseData, MediaType produces) throws IOException {
		setupRestClientMocks(setupCorrelationId, responseData, produces, produces, Response.Status.OK);
	}

	private void setupRestClientMocks(boolean setupCorrelationId, Document responseData, MediaType accepts, MediaType produces, Response.Status status) throws IOException {
		// TODO: Change this based on https://maciejwalkowiak.com/mocking-fluent-interfaces/
		when(client.target(machineName.capture())).thenReturn(target);
		when(target.path(path.capture())).thenReturn(target);
		when(target.request()).thenReturn(builder);
		when(builder.accept(accepts)).thenReturn(builder);
		when(builder.post(entity.capture())).thenReturn(response);
		when(response.getStatusInfo()).thenReturn(status);
		if (responseData != null) {
			when(response.hasEntity()).thenReturn(true);
			when(response.getEntity()).thenReturn(new ByteArrayInputStream(responseData.getInlineData()));
			if (Status.OK.getFamily().equals(status.getFamily())) {
				when(response.getMediaType()).thenReturn(produces);
			}
		} else {
			when(response.hasEntity()).thenReturn(false);
		}

		if (setupCorrelationId) {
			when(builder.header(eq(CORRELATION_ID_HTTP_HDR), correlationId.capture())).thenReturn(builder);
		}
		
		
	}
	
	private static void validateTextFormField(FormDataMultiPart postedData, String fieldName, String expectedData) throws IOException {
		List<FormDataBodyPart> pdfFields = postedData.getFields(fieldName);
		assertEquals(1, pdfFields.size());

		FormDataBodyPart pdfPart = pdfFields.get(0);
		assertEquals(MediaType.TEXT_PLAIN_TYPE, pdfPart.getMediaType());
		String value = (String) pdfPart.getEntity();
		assertEquals(expectedData, value);
	}
	
	private static void validateDocumentFormField(FormDataMultiPart postedData, String fieldName, MediaType expectedMediaType, byte[] expectedData) throws IOException {
		List<FormDataBodyPart> pdfFields = postedData.getFields(fieldName);
		assertEquals(1, pdfFields.size());
		
		FormDataBodyPart pdfPart = pdfFields.get(0);
		assertEquals(expectedMediaType, pdfPart.getMediaType());
		byte[] pdfBytes = IOUtils.toByteArray((InputStream) pdfPart.getEntity());
		assertArrayEquals(expectedData, pdfBytes);  // TODO: Need to figure out how to test for entity.
	}
	
	private static void validateFormFieldDoesNotExist(FormDataMultiPart postedData, String fieldName) {
		List<FormDataBodyPart> pdfFields = postedData.getFields(fieldName);
		assertTrue(pdfFields.isEmpty(), "Expected field '" + fieldName + "' to not exist, but it did.");
	}

	private enum ErrorResponseScenario {
		INTERNAL_SERVER_ERROR(ErrorResponseScenario::setupInternalServerError, ErrorResponseScenario::validateInternalServerError),
		NO_ENTITY(ErrorResponseScenario::setupNoEntity, ErrorResponseScenario::validateNoEntity),
		HTML_RESPONSE(ErrorResponseScenario::setupHtmlResponse, ErrorResponseScenario::validateHtmlResponse),
		NO_CONTENT_TYPE_RESPONSE(ErrorResponseScenario::noContentTypeInResponse, ErrorResponseScenario::validateNoContentTypeInResponse);
		
		
		private static final String HTML_ERROR_PAGE_HTML = "<HTML>ErrorPage</HTML>";
		Consumer<RestServicesDocAssemblerServiceAdapterTest> restClientMocksSetup;
		Consumer<String> validateExceptionMessage;

		private ErrorResponseScenario(Consumer<RestServicesDocAssemblerServiceAdapterTest> restClientMocksSetup,
				Consumer<String> validateExceptionMessage) {
			this.restClientMocksSetup = restClientMocksSetup;
			this.validateExceptionMessage = validateExceptionMessage;
		}

		private static void setupInternalServerError(RestServicesDocAssemblerServiceAdapterTest test) {
			try {
				test.setupRestClientMocks(false, MockDocumentFactory.GLOBAL_INSTANCE.create(HTML_ERROR_PAGE_HTML.getBytes()), APPLICATION_XML, null, Response.Status.INTERNAL_SERVER_ERROR);
			} catch (IOException e) {
				throw new IllegalStateException("IO Exception while setting up RestClientMocks.", e);
			}
		}
		
		private static void setupNoEntity(RestServicesDocAssemblerServiceAdapterTest test) {
			try {
				test.setupRestClientMocks(false, null, APPLICATION_XML);
			} catch (IOException e) {
				throw new IllegalStateException("IO Exception while setting up RestClientMocks.", e);
			}
		}
		
		private static void setupHtmlResponse(RestServicesDocAssemblerServiceAdapterTest test) {
			try {
				test.setupRestClientMocks(false, MockDocumentFactory.GLOBAL_DUMMY_DOCUMENT, APPLICATION_XML, MediaType.TEXT_HTML_TYPE, Response.Status.OK);
			} catch (IOException e) {
				throw new IllegalStateException("IO Exception while setting up RestClientMocks.", e);
			}
		}

		private static void noContentTypeInResponse(RestServicesDocAssemblerServiceAdapterTest test) {
			try {
				test.setupRestClientMocks(false, MockDocumentFactory.GLOBAL_DUMMY_DOCUMENT, APPLICATION_XML, null, Response.Status.OK);
			} catch (IOException e) {
				throw new IllegalStateException("IO Exception while setting up RestClientMocks.", e);
			}
		}
		
		private static void validateInternalServerError(String exMsg) {
			assertAll(
					()->assertThat(exMsg, containsStringIgnoringCase("Call to server failed")),
					()->assertThat(exMsg, containsStringIgnoringCase(Integer.toString(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))),
					()->assertThat(exMsg, containsStringIgnoringCase(Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase())),
					()->assertThat(exMsg, containsStringIgnoringCase(HTML_ERROR_PAGE_HTML))
					);
		}

		private static void validateNoEntity(String exMsg) {
			assertAll(
					()->assertThat(exMsg, containsStringIgnoringCase("Call to server succeeded")),
					()->assertThat(exMsg, containsStringIgnoringCase("server failed to return assemblerResult xml"))
					);
		}

		private static void validateHtmlResponse(String exMsg) {
			assertAll(
					()->assertThat(exMsg, containsStringIgnoringCase("Response from AEM server was")),
					()->assertThat(exMsg, containsStringIgnoringCase("content-type")),
					()->assertThat(exMsg, containsStringIgnoringCase(MediaType.TEXT_HTML))
					);
		}

		private static void validateNoContentTypeInResponse(String exMsg) {
			assertAll(
					()->assertThat(exMsg, containsStringIgnoringCase("Response from AEM server was")),
					()->assertThat(exMsg, containsStringIgnoringCase("content-type")),
					()->assertThat(exMsg, containsStringIgnoringCase("null"))
					);
		}
	}
	
	@ParameterizedTest
	@EnumSource
	void testInvoke(ErrorResponseScenario scenario) throws Exception {
		Document responseData = MockDocumentFactory.GLOBAL_INSTANCE.create(POPULATED_ASSEMBLER_RESULT_XML.getBytes());

		scenario.restClientMocksSetup.accept(this);

		AssemblerServiceBuilder assemblerServiceBuilder = RestServicesDocAssemblerServiceAdapter.builder()
								.machineName(TEST_MACHINE_NAME)
								.port(TEST_MACHINE_PORT)
								.basicAuthentication("username", "password")
								.useSsl(false)
								.clientFactory(()->client);
		
		RestServicesDocAssemblerServiceAdapter underTest = assemblerServiceBuilder.build();
		
		byte[] srcDocumentDataBytes = "source Document Data".getBytes();
		Map<String, Object> sourceDocuments = 
				Collections.singletonMap("SourceDocument1", MockDocumentFactory.GLOBAL_INSTANCE.create(srcDocumentDataBytes));
		byte[] ddxBytes = "ddx Document Data".getBytes();
		Document ddx = MockDocumentFactory.GLOBAL_INSTANCE.create(ddxBytes);
		
		AssemblerServiceException ex = assertThrows(AssemblerServiceException.class,()->underTest.invoke(ddx, sourceDocuments, null));
		String msg = ex.getMessage();
		assertNotNull(msg);
		scenario.validateExceptionMessage.accept(msg);
	}

	@ParameterizedTest
	@MethodSource("testXmls")
	void testConvertXmlToAssemblerResult(String xml, String expectedContentType) throws Exception {
		AssemblerResult result = RestServicesDocAssemblerServiceAdapter.convertXmlToAssemblerResult(new ByteArrayInputStream(xml.getBytes()));
		
		validatePopulatedResult(result, expectedContentType);
	}

	static Stream<Arguments> testXmls() {
		return Stream.of(
				Arguments.of(POPULATED_ASSEMBLER_RESULT_XML, "application/pdf"),
				Arguments.of(POPULATED_ASSEMBLER_RESULT_XML_WITH_CONTENT_TYPE, "application/xml")
				);
				
	}
	
	private void validatePopulatedResult(AssemblerResult result, String expectedContentType) throws IOException {
		Map<String, Document> documents = result.getDocuments();
		assertEquals(1, documents.size());
		String expectedDpcumentName = "concatenatedPDF.pdf";
		assertTrue(documents.containsKey(expectedDpcumentName));
		Document doc = documents.get(expectedDpcumentName);
		assertEquals(expectedContentType, doc.getContentType());
		
		List<String> failedBlockNames = result.getFailedBlockNames();
		assertEquals(2, failedBlockNames.size());
		assertEquals("failedBlock1", failedBlockNames.get(0));
		assertEquals("failedBlock2", failedBlockNames.get(1));
		
		Document jobLog = result.getJobLog();
		assertTrue(jobLog.isEmpty());
		
		int lastBatesNumber = result.getLastBatesNumber();
		assertEquals(2, lastBatesNumber);
		
		Map<String, List<String>> multipleResultsBlocks = result.getMultipleResultsBlocks();
		assertEquals(1, multipleResultsBlocks.size());
		List<String> multipleResultBlocksList = multipleResultsBlocks.get("document");
		assertEquals(2, multipleResultBlocksList.size());
		assertEquals("test1", multipleResultBlocksList.get(0));
		assertEquals("test2", multipleResultBlocksList.get(1));
		
		int numRequestedBlocks = result.getNumRequestedBlocks();
		assertEquals(3, numRequestedBlocks);

		List<String> successfulBlockNames = result.getSuccessfulBlockNames();
		assertEquals(2, successfulBlockNames.size());
		assertEquals("successBlock1", successfulBlockNames.get(0));
		assertEquals("successBlock2", successfulBlockNames.get(1));
		
		List<String> successfulDocumentNames = result.getSuccessfulDocumentNames();
		assertEquals(2, successfulDocumentNames.size());
		assertEquals("successDocument1", successfulDocumentNames.get(0));
		assertEquals("successDocument2", successfulDocumentNames.get(1));
		
		Map<String, OperationException> throwables = result.getThrowables();
		assertEquals(0, throwables.size());
	}

	@Test
	void testConvertXmlToAssemblerEmptyResult() throws Exception {
		AssemblerResult result = RestServicesDocAssemblerServiceAdapter.convertXmlToAssemblerResult(new ByteArrayInputStream(EMPTY_ASSEMBLER_RESULT_XML.getBytes()));
		
		validateEmptyResult(result);
	}

	private void validateEmptyResult(AssemblerResult result) {
		Map<String, Document> documents = result.getDocuments();
		assertEquals(0, documents.size());
		
		List<String> failedBlockNames = result.getFailedBlockNames();
		assertEquals(0, failedBlockNames.size());
		
		Document jobLog = result.getJobLog();
		assertTrue(jobLog.isEmpty());
		
		int lastBatesNumber = result.getLastBatesNumber();
		assertEquals(0, lastBatesNumber);
		
		Map<String, List<String>> multipleResultsBlocks = result.getMultipleResultsBlocks();
		assertEquals(0, multipleResultsBlocks.size());

		int numRequestedBlocks = result.getNumRequestedBlocks();
		assertEquals(0, numRequestedBlocks);

		List<String> successfulBlockNames = result.getSuccessfulBlockNames();
		assertEquals(0, successfulBlockNames.size());
		
		List<String> successfulDocumentNames = result.getSuccessfulDocumentNames();
		assertEquals(0, successfulDocumentNames.size());
		
		Map<String, OperationException> throwables = result.getThrowables();
		assertEquals(0, throwables.size());
	}


	private static final Document DDX_ARG = MockDocumentFactory.GLOBAL_DUMMY_DOCUMENT;
	private static final Map<String, Object> SOURCE_DOCS_ARG = Collections.emptyMap(); 
	private static final AssemblerOptionsSpec ASSEMBLER_OPTS_ARG = new AssemblerOptionsSpecImpl();

	private enum NullArgumentTest {
		DDX_NULL("ddx", null, SOURCE_DOCS_ARG, ASSEMBLER_OPTS_ARG),
		SOURCE_NULL("source documents map", DDX_ARG, null, ASSEMBLER_OPTS_ARG);
		
		String argName;
		Document ddx;
		Map<String, Object> sourceDocuments;
		AssemblerOptionsSpec assemblerOptionSpec;

		private NullArgumentTest(String argName, Document ddx, Map<String, Object> sourceDocuments, AssemblerOptionsSpec assemblerOptionSpec) {
			this.argName = argName;
			this.ddx = ddx;
			this.sourceDocuments = sourceDocuments;
			this.assemblerOptionSpec = assemblerOptionSpec;
		}
	}

	@ParameterizedTest
	@EnumSource
	void testNullArgs(NullArgumentTest scenario) throws Exception {
		RestServicesDocAssemblerServiceAdapter underTest = RestServicesDocAssemblerServiceAdapter.builder().build();
		
		NullPointerException ex = assertThrows(NullPointerException.class,()->underTest.invoke(scenario.ddx, scenario.sourceDocuments, scenario.assemblerOptionSpec));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertAll(
				()->assertThat(msg, containsStringIgnoringCase(scenario.argName)),
				()->assertThat(msg, containsStringIgnoringCase("can not be null"))
				);
	}

	private static final byte[] PDFA_DOCUMENT_CONTENTS = "PDFA Document Contents".getBytes(StandardCharsets.UTF_8);
	private static final byte[] JOB_LOG_DOCUMENT_CONTENTS = "JOB LOG Document Contents".getBytes(StandardCharsets.UTF_8);
	private static final byte[] CONVERSION_LOG_DOCUMENT_CONTENTS = "Conversion Log Document Contents".getBytes(StandardCharsets.UTF_8);

	private static final String EXPECTED_RESULT_DATA = "<ToPdfAResult>\n"
			+ "  <ConversionLog>" + Base64.getEncoder().encodeToString(CONVERSION_LOG_DOCUMENT_CONTENTS) + "</ConversionLog>\n"
			+ "  <JobLog>" + Base64.getEncoder().encodeToString(JOB_LOG_DOCUMENT_CONTENTS) + "</JobLog>\n"
			+ "  <PdfADocument>" + Base64.getEncoder().encodeToString(PDFA_DOCUMENT_CONTENTS) + "</PdfADocument>\n"
			+ "  <IsPdfA>true</IsPdfA>\n"
			+ "</ToPdfAResult>\n";
	private static final byte[] EXPECTED_INPUT_DOCUMENT_CONTENT = "Pre-PDFA Document Contents".getBytes(StandardCharsets.UTF_8);

	private enum ToPdfaHappyPathScenario {
		NULL_ASSEMBLER_OPTIONS(()->null, InvokeHappyPathScenario::validateEmptySpec),
		EMPTY_ASSEMBLER_OPTIONS(ToPdfaHappyPathScenario::emptySpec, ToPdfaHappyPathScenario::validateEmptySpec),
		FULL_ASSEMBLER_OPTIONS(ToPdfaHappyPathScenario::fullSpec, ToPdfaHappyPathScenario::validateFullSpec);
		
		private static final String INPUT_DOCUMENT_PARAM = "inDoc";
		private static final String COLOR_SPACE_PARAM = "colorSpace";
		private static final String COMPLIANCE_PARAM = "compliance";
		private static final String LOG_LEVEL_PARAM = "logLevel";
		private static final String METADATA_EXTENSION_PARAM = "metadataExtension";
		private static final String OPTIONAL_CONTENT_PARAM = "optionalContent";
		private static final String RESULT_LEVEL_PARAM = "resultLevel";
		private static final String SIGNATURES_PARAM = "signatures";
		private static final String REMOVE_INVALID_XMP_PARAM = "removeInvalidXmlProperties";
		private static final String RETAIN_PDF_FORM_STATE_PARAM = "retainPdfFormState";
		private static final String VERIFY_PARAM = "verify";

		private static final Document METADATA_DOCUMENT_VALUE = SimpleDocumentFactoryImpl.getFactory().create("metadataExtension Contents".getBytes(StandardCharsets.UTF_8));
		private static final ColorSpace COLOR_SPACE_VALUE = ColorSpace.JAPAN_COLOR_COATED;
		private static final Compliance COMPLIANCE_VALUE = Compliance.PDFA_2B;
		private static final LogLevel LOG_LEVEL_VALUE = LogLevel.FINER;
		private static final List<Document> METADATA_EXTENSION_VALUE = Collections.singletonList(METADATA_DOCUMENT_VALUE);
		private static final OptionalContent OPTIONAL_CONTENT_VALUE = OptionalContent.VISIBLE;
		private static final ResultLevel RESULT_LEVEL_VALUE = ResultLevel.SUMMARY;
		private static final Signatures SIGNATURES_VALUE = Signatures.ARCHIVE_AS_NEEDED;
		private static final boolean REMOVE_INVALID_XMP_VALUE = true;
		private static final boolean RETAIN_PDF_FORM_STATE_VALUE = false;
		private static final boolean VERIFY_VALUE = false;

		Supplier<PDFAConversionOptionSpec> pdfaConversionOptionsSpec;
		Consumer<FormDataMultiPart> pdfaConversionOptionsValidator;

		private ToPdfaHappyPathScenario(Supplier<PDFAConversionOptionSpec> pdfaConversionOptionsSpec,
				Consumer<FormDataMultiPart> pdfaConversionOptionsValidator) {
			this.pdfaConversionOptionsSpec = pdfaConversionOptionsSpec;
			this.pdfaConversionOptionsValidator = pdfaConversionOptionsValidator;
		}

		private static final PDFAConversionOptionSpec emptySpec() {
			return new PDFAConversionOptionSpecImpl();
		}
		
		private static final void validateEmptySpec(FormDataMultiPart postedData) {
			// Since we passed in an uninitialized structure, we expect these fields not to be passed in.
			validateFormFieldDoesNotExist(postedData, COLOR_SPACE_PARAM);
			validateFormFieldDoesNotExist(postedData, COMPLIANCE_PARAM);
			validateFormFieldDoesNotExist(postedData, LOG_LEVEL_PARAM);
			validateFormFieldDoesNotExist(postedData, METADATA_EXTENSION_PARAM);
			validateFormFieldDoesNotExist(postedData, OPTIONAL_CONTENT_PARAM);
			validateFormFieldDoesNotExist(postedData, RESULT_LEVEL_PARAM);
			validateFormFieldDoesNotExist(postedData, SIGNATURES_PARAM);
			validateFormFieldDoesNotExist(postedData, REMOVE_INVALID_XMP_PARAM);
			validateFormFieldDoesNotExist(postedData, RETAIN_PDF_FORM_STATE_PARAM);
			validateFormFieldDoesNotExist(postedData, VERIFY_PARAM);
		}
		
		private static final PDFAConversionOptionSpec fullSpec() {
			PDFAConversionOptionSpecImpl options = new PDFAConversionOptionSpecImpl();
			// TODO: Fill these in with values
			options.setColorSpace(COLOR_SPACE_VALUE);
			options.setCompliance(COMPLIANCE_VALUE);
			options.setLogLevel(LOG_LEVEL_VALUE);
			options.setMetadataSchemaExtensions(METADATA_EXTENSION_VALUE);
			options.setOptionalContent(OPTIONAL_CONTENT_VALUE);
			options.setRemoveInvalidXMPProperties(REMOVE_INVALID_XMP_VALUE);
			options.setResultLevel(RESULT_LEVEL_VALUE);
			options.setRetainPDFFormState(RETAIN_PDF_FORM_STATE_VALUE);
			options.setSignatures(SIGNATURES_VALUE);
			options.setVerify(VERIFY_VALUE);
			return options;
		}

		private static final void validateFullSpec(FormDataMultiPart postedData) {
			try {
				validateTextFormField(postedData, COLOR_SPACE_PARAM, COLOR_SPACE_VALUE.toString());
				validateTextFormField(postedData, COMPLIANCE_PARAM, COMPLIANCE_VALUE.toString());
				validateTextFormField(postedData, LOG_LEVEL_PARAM, LOG_LEVEL_VALUE.toString());
				validateDocumentFormField(postedData, METADATA_EXTENSION_PARAM, APPLICATION_XML, IOUtils.toByteArray(METADATA_DOCUMENT_VALUE.getInputStream()));
				validateTextFormField(postedData, OPTIONAL_CONTENT_PARAM, OPTIONAL_CONTENT_VALUE.toString());
				validateTextFormField(postedData, RESULT_LEVEL_PARAM, RESULT_LEVEL_VALUE.toString());
				validateTextFormField(postedData, SIGNATURES_PARAM, SIGNATURES_VALUE.toString());
				validateTextFormField(postedData, REMOVE_INVALID_XMP_PARAM, Boolean.toString(REMOVE_INVALID_XMP_VALUE));
				validateTextFormField(postedData, RETAIN_PDF_FORM_STATE_PARAM, Boolean.toString(RETAIN_PDF_FORM_STATE_VALUE));
				validateTextFormField(postedData, VERIFY_PARAM, Boolean.toString(VERIFY_VALUE));
			} catch (IOException e) {
				throw new IllegalStateException("IO Exception while validating options spec fields.", e);
			}
		}
	}
	
	@ParameterizedTest
	@EnumSource
	void testToPdfA(ToPdfaHappyPathScenario scenario) throws Exception {
		Document responseData = SimpleDocumentFactoryImpl.getFactory().create(EXPECTED_RESULT_DATA.getBytes(StandardCharsets.UTF_8));
		RestServicesDocAssemblerServiceAdapter underTest = createAdapter(true, responseData, APPLICATION_XML);
	
		Document inPdf = SimpleDocumentFactoryImpl.getFactory().create(EXPECTED_INPUT_DOCUMENT_CONTENT);;
		PDFAConversionOptionSpec options = scenario.pdfaConversionOptionsSpec.get();
		PDFAConversionResult result = underTest.toPDFA(inPdf, options);
		
		// Validate the Response
		assertAll(
				()->assertArrayEquals(CONVERSION_LOG_DOCUMENT_CONTENTS, IOUtils.toByteArray(result.getConversionLog().getInputStream())),
				()->assertArrayEquals(JOB_LOG_DOCUMENT_CONTENTS, IOUtils.toByteArray(result.getJobLog().getInputStream())),
				()->assertArrayEquals(PDFA_DOCUMENT_CONTENTS, IOUtils.toByteArray(result.getPDFADocument().getInputStream())),
				()->assertTrue(result.isPDFA())
				);
		
		// Validate the inputs to the REST client captured
		assertEquals("http://" + TEST_MACHINE_NAME + ":" + TEST_MACHINE_PORT, machineName.getValue());
		assertEquals("/lc/services/AssemblerService/ToPdfA", path.getValue());

		// Make sure that the arguments we passed in are transmitted correctly.
		@SuppressWarnings("unchecked")
		Entity<FormDataMultiPart> postedEntity = (Entity<FormDataMultiPart>)entity.getValue();
		FormDataMultiPart postedData = postedEntity.getEntity();
		
		assertEquals(MediaType.MULTIPART_FORM_DATA_TYPE, postedEntity.getMediaType());
		validateDocumentFormField(postedData, ToPdfaHappyPathScenario.INPUT_DOCUMENT_PARAM, APPLICATION_PDF, EXPECTED_INPUT_DOCUMENT_CONTENT);
		// validate the assemblerOptions
		scenario.pdfaConversionOptionsValidator.accept(postedData);
	}
	
	@Test
	void testIsPdfA() throws Exception {
		RestServicesDocAssemblerServiceAdapter underTest = RestServicesDocAssemblerServiceAdapter.builder()
																								  .machineName(TEST_MACHINE_NAME)
																								  .port(TEST_MACHINE_PORT)
																								  .basicAuthentication("username", "password")
																								  .useSsl(false)
																								  .aemServerType(AemServerType.StandardType.JEE)
																								  .correlationId(()->CORRELATION_ID)
																								  .clientFactory(()->client)
																								  .build();
		
		Document inPdf = SimpleDocumentFactoryImpl.getFactory().create(EXPECTED_INPUT_DOCUMENT_CONTENT);;
		PDFAValidationOptionSpec options = new PDFAValidationOptionSpecImpl();
		UnsupportedOperationException ex = assertThrows(UnsupportedOperationException.class, ()->underTest.isPDFA(inPdf, options));
		
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertEquals("isPDFA has not been implemented yet.", msg);
	}	
}
