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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.Response.StatusType;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
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
import com._4point.aem.fluentforms.api.assembler.AssemblerService.AssemblerServiceException;
import com._4point.aem.fluentforms.impl.assembler.AssemblerOptionsSpecImpl;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;
import com.adobe.fd.assembler.client.OperationException;

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

	RestServicesDocAssemblerServiceAdapter underTest;
	private static final String POPULATED_ASSEMBLER_RESULT_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><assemblerResult><resultDocument documentName=\"concatenatedPDF.pdf\"><mergedDoc>dGVzdERvUG9zdCBIYXBweSBQYXRoIFJlc3VsdA==</mergedDoc></resultDocument><failedBlockNames><failedBlockName>failedBlock1</failedBlockName><failedBlockName>failedBlock2</failedBlockName></failedBlockNames><successfulDocumentNames><successfulDocumentName>successDocument1</successfulDocumentName><successfulDocumentName>successDocument2</successfulDocumentName></successfulDocumentNames><successfulBlockNames><successfulBlockName>successBlock1</successfulBlockName><successfulBlockName>successBlock2</successfulBlockName></successfulBlockNames><latestBatesNumber value=\"2\"/><numRequestedBlocks value=\"3\"/><multipleResultBlocks name=\"document\"><documentNames><documentName>test1</documentName><documentName>test2</documentName></documentNames></multipleResultBlocks><jobLog joblogValue=\"SU5GTw==\"/></assemblerResult>";
	private static final String EMPTY_ASSEMBLER_RESULT_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><assemblerResult><failedBlockNames/><successfulDocumentNames/><successfulBlockNames/><latestBatesNumber value=\"0\"/><numRequestedBlocks value=\"0\"/><jobLog/></assemblerResult>";

	private enum HappyPathScenario {
		NULL_ASSEMBLER_OPTIONS(()->null, HappyPathScenario::validateEmptySpec),
		EMPTY_ASSEMBLER_OPTIONS(HappyPathScenario::emptySpec, HappyPathScenario::validateEmptySpec),
		FULL_ASSEMBLER_OPTIONS(HappyPathScenario::fullSpec, HappyPathScenario::validateFullSpec);
		
		private static final boolean FAIL_ON_ERROR_VALUE = false;
		private static final boolean VALIDATE_ONLY_VALUE = false;
		private static final boolean TAKE_OWNERSHIP_VALUE = false;
		private static final LogLevel LOG_LEVEL_VALUE = LogLevel.FINE;
		private static final String DEFAULT_STYLE_VALUE = "DefaultStyleValue";
		private static final int FIRST_BATES_NUMBER_VALUE = 23;
		
		Supplier<AssemblerOptionsSpec> assemblerSpec;
		Consumer<FormDataMultiPart> assemblerSpecValidator;

		private HappyPathScenario(Supplier<AssemblerOptionsSpec> assemblerSpec,
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
	void testInvoke(HappyPathScenario scenario) throws Exception {
		Document responseData = MockDocumentFactory.GLOBAL_INSTANCE.create(POPULATED_ASSEMBLER_RESULT_XML.getBytes());

		setupRestClientMocks(true, responseData, APPLICATION_XML);

		AssemblerServiceBuilder assemblerServiceBuilder = RestServicesDocAssemblerServiceAdapter.builder()
								.machineName(TEST_MACHINE_NAME)
								.port(TEST_MACHINE_PORT)
								.basicAuthentication("username", "password")
								.useSsl(false)
								.aemServerType(AemServerType.StandardType.JEE)
								.correlationId(()->CORRELATION_ID)
								.clientFactory(()->client);
		
		underTest = assemblerServiceBuilder.build();
		
		AssemblerOptionsSpec assemblerOptionSpec = scenario.assemblerSpec.get();
		
		byte[] srcDocumentDataBytes = "source Document Data".getBytes();
		Map<String, Object> sourceDocuments = 
				Collections.singletonMap("SourceDocument1", MockDocumentFactory.GLOBAL_INSTANCE.create(srcDocumentDataBytes));
		byte[] ddxBytes = "ddx Document Data".getBytes();
		Document ddx = MockDocumentFactory.GLOBAL_INSTANCE.create(ddxBytes);
		
		AssemblerResult result = underTest.invoke(ddx, sourceDocuments, assemblerOptionSpec);
		
		// Validate the result was returned as expected.
		validatePopulatedResult(result);
		
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
		assertTrue(CollectionUtils.isEmpty(pdfFields), "Expected field '" + fieldName + "' to not exist, but it did.");
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
		
		underTest = assemblerServiceBuilder.build();
		
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

	@Test
	void testConvertXmlToAssemblerResult() throws Exception {
		AssemblerResult result = RestServicesDocAssemblerServiceAdapter.convertXmlToAssemblerResult(new ByteArrayInputStream(POPULATED_ASSEMBLER_RESULT_XML.getBytes()));
		
		validatePopulatedResult(result);
	}

	private void validatePopulatedResult(AssemblerResult result) {
		Map<String, Document> documents = result.getDocuments();
		assertEquals(1, documents.size());
		assertTrue(documents.containsKey("concatenatedPDF.pdf"));
		
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
		underTest = RestServicesDocAssemblerServiceAdapter.builder().build();
		
		NullPointerException ex = assertThrows(NullPointerException.class,()->underTest.invoke(scenario.ddx, scenario.sourceDocuments, scenario.assemblerOptionSpec));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertAll(
				()->assertThat(msg, containsStringIgnoringCase(scenario.argName)),
				()->assertThat(msg, containsStringIgnoringCase("can not be null"))
				);
	}

}
