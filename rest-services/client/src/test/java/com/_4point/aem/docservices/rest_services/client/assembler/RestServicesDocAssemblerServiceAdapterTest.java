package com._4point.aem.docservices.rest_services.client.assembler;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Answers;
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
import com._4point.aem.docservices.rest_services.client.RestClient.MultipartPayload.Builder;
import com._4point.aem.docservices.rest_services.client.helpers.AemConfig;
import com._4point.aem.docservices.rest_services.client.helpers.AemServerType;
import com._4point.aem.docservices.rest_services.client.helpers.BuilderImpl.TriFunction;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.assembler.AssemblerOptionsSpec;
import com._4point.aem.fluentforms.api.assembler.AssemblerResult;
import com._4point.aem.fluentforms.api.assembler.AssemblerService.AssemblerServiceException;
import com._4point.aem.fluentforms.api.assembler.LogLevel;
import com._4point.aem.fluentforms.api.assembler.PDFAConversionOptionSpec;
import com._4point.aem.fluentforms.api.assembler.PDFAConversionResult;
import com._4point.aem.fluentforms.api.assembler.PDFAValidationOptionSpec;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;
import com._4point.aem.fluentforms.impl.assembler.AssemblerOptionsSpecImpl;
import com._4point.aem.fluentforms.impl.assembler.PDFAValidationOptionSpecImpl;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;
import com._4point.testing.matchers.javalang.ExceptionMatchers;
import com.adobe.fd.assembler.client.OperationException;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.ColorSpace;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.Compliance;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.OptionalContent;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.ResultLevel;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.Signatures;

@ExtendWith(MockitoExtension.class)
class RestServicesDocAssemblerServiceAdapterTest {
//	private static final String CORRELATION_ID_HTTP_HDR = "X-Correlation-ID";
//	private static final String CORRELATION_ID = "correlationId";
	private static final String TEST_MACHINE_NAME = "testmachinename";
	private static final int TEST_MACHINE_PORT = 8080;

//	private static final MediaType APPLICATION_PDF = new MediaType("application", "pdf");
//	private static final MediaType APPLICATION_XML = new MediaType("application", "xml");
	
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
	
	@Captor ArgumentCaptor<Stream<String>> sourceDocMapKeys;
	@Captor ArgumentCaptor<Stream<Document>> sourceDocMapDocs;
	@Captor ArgumentCaptor<Function<AssemblerOptionsSpec, Boolean>> isFailOnErrorFn;
	@Captor ArgumentCaptor<Function<AssemblerOptionsSpec, Boolean>> isValidateOnlyFn;
	@Captor ArgumentCaptor<Function<AssemblerOptionsSpec, Boolean>> isTakeOwnershipFn;
	@Captor ArgumentCaptor<Function<AssemblerOptionsSpec, LogLevel>> getLogLevelFn;
	@Captor ArgumentCaptor<Function<AssemblerOptionsSpec, Integer>> getFirstBatesNumberFn;
	@Captor ArgumentCaptor<Function<AssemblerOptionsSpec, String>> getDefaultStyleFn;


	private static final String POPULATED_ASSEMBLER_RESULT_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><assemblerResult><resultDocument documentName=\"concatenatedPDF.pdf\"><mergedDoc>dGVzdERvUG9zdCBIYXBweSBQYXRoIFJlc3VsdA==</mergedDoc></resultDocument><failedBlockNames><failedBlockName>failedBlock1</failedBlockName><failedBlockName>failedBlock2</failedBlockName></failedBlockNames><successfulDocumentNames><successfulDocumentName>successDocument1</successfulDocumentName><successfulDocumentName>successDocument2</successfulDocumentName></successfulDocumentNames><successfulBlockNames><successfulBlockName>successBlock1</successfulBlockName><successfulBlockName>successBlock2</successfulBlockName></successfulBlockNames><latestBatesNumber value=\"2\"/><numRequestedBlocks value=\"3\"/><multipleResultBlocks name=\"document\"><documentNames><documentName>test1</documentName><documentName>test2</documentName></documentNames></multipleResultBlocks><jobLog joblogValue=\"SU5GTw==\"/></assemblerResult>";
	private static final String EMPTY_ASSEMBLER_RESULT_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><assemblerResult><failedBlockNames/><successfulDocumentNames/><successfulBlockNames/><latestBatesNumber value=\"0\"/><numRequestedBlocks value=\"0\"/><jobLog/></assemblerResult>";
	private static final String POPULATED_ASSEMBLER_RESULT_XML_WITH_CONTENT_TYPE = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><assemblerResult><resultDocument documentName=\"concatenatedPDF.pdf\" contentType=\"application/xml\"><mergedDoc>dGVzdERvUG9zdCBIYXBweSBQYXRoIFJlc3VsdA==</mergedDoc></resultDocument><failedBlockNames><failedBlockName>failedBlock1</failedBlockName><failedBlockName>failedBlock2</failedBlockName></failedBlockNames><successfulDocumentNames><successfulDocumentName>successDocument1</successfulDocumentName><successfulDocumentName>successDocument2</successfulDocumentName></successfulDocumentNames><successfulBlockNames><successfulBlockName>successBlock1</successfulBlockName><successfulBlockName>successBlock2</successfulBlockName></successfulBlockNames><latestBatesNumber value=\"2\"/><numRequestedBlocks value=\"3\"/><multipleResultBlocks name=\"document\"><documentNames><documentName>test1</documentName><documentName>test2</documentName></documentNames></multipleResultBlocks><jobLog joblogValue=\"SU5GTw==\"/></assemblerResult>";

	@BeforeEach
	void setUp() throws Exception {
		when(mockClientFactory.apply(aemConfig.capture(), servicePath.capture(), correlationIdFn.capture())).thenReturn(mockClient);
	}

	@Test
	void testInvoke_NullDdx() throws Exception {
		RestServicesDocAssemblerServiceAdapter underTest = RestServicesDocAssemblerServiceAdapter.builder(mockClientFactory).build();
		
		NullPointerException ex = assertThrows(NullPointerException.class,()->underTest.invoke(null, Collections.emptyMap(), new AssemblerOptionsSpecImpl()));
		
		assertThat(ex, ExceptionMatchers.exceptionMsgContainsAll("ddx", "can not be null"));
	}

	@Test
	void testInvoke_NullSrcDoc() throws Exception {
		RestServicesDocAssemblerServiceAdapter underTest = RestServicesDocAssemblerServiceAdapter.builder(mockClientFactory).build();
		
		NullPointerException ex = assertThrows(NullPointerException.class,()->underTest.invoke(MockDocumentFactory.GLOBAL_DUMMY_DOCUMENT, null, new AssemblerOptionsSpecImpl()));
		
		assertThat(ex, ExceptionMatchers.exceptionMsgContainsAll("source documents map", "can not be null"));
	}

	@Test
	void testInvoke_HappyPath() throws Exception {
		RestServicesDocAssemblerServiceAdapter underTest = createAdapter(mockClientFactory);
		
		byte[] srcDocumentDataBytes = "source Document Data".getBytes();
		Map<String, Object> sourceDocuments = 
				Collections.singletonMap("SourceDocument1", MockDocumentFactory.GLOBAL_INSTANCE.create(srcDocumentDataBytes));
		byte[] ddxBytes = "ddx Document Data".getBytes();
		Document ddx = MockDocumentFactory.GLOBAL_INSTANCE.create(ddxBytes);
		AssemblerOptionsSpec assemblerOptionSpec = mock(AssemblerOptionsSpec.class);

		when(assemblerOptionSpec.isFailOnError()).thenReturn(false);
		when(assemblerOptionSpec.isValidateOnly()).thenReturn(false);
		when(assemblerOptionSpec.isTakeOwnership()).thenReturn(false);
		when(assemblerOptionSpec.getLogLevel()).thenReturn(LogLevel.FINE);
		when(assemblerOptionSpec.getDefaultStyle()).thenReturn("DefaultStyleValue");
		when(assemblerOptionSpec.getFirstBatesNumber()).thenReturn(23);
		
		when(mockPayloadBuilder.add(eq("ddx"), same(ddx), eq(ContentType.APPLICATION_XML))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStrings(eq("sourceDocumentMap.key"), sourceDocMapKeys.capture())).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addDocs(eq("sourceDocumentMap.value"), sourceDocMapDocs.capture())).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.transformAndAddStringVersion(eq("isFailOnError"), same(assemblerOptionSpec), isFailOnErrorFn.capture())).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.transformAndAddStringVersion(eq("isValidateOnly"), same(assemblerOptionSpec), isValidateOnlyFn.capture())).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.transformAndAddStringVersion(eq("isTakeOwnerShip"), same(assemblerOptionSpec), isTakeOwnershipFn.capture())).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.transformAndAddStringVersion(eq("jobLogLevel"), same(assemblerOptionSpec), getLogLevelFn.capture())).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.transformAndAddStringVersion(eq("firstBatesNum"), same(assemblerOptionSpec), getFirstBatesNumberFn.capture())).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.transformAndAdd(eq("defaultStyle"), same(assemblerOptionSpec), getDefaultStyleFn.capture())).thenReturn(mockPayloadBuilder);

		setupMocks(setupMockResponse(POPULATED_ASSEMBLER_RESULT_XML_WITH_CONTENT_TYPE.getBytes(), ContentType.APPLICATION_XML));
		
		AssemblerResult result = underTest.invoke(ddx, sourceDocuments, assemblerOptionSpec);
		
		// Validate the result was returned as expected.
		validatePopulatedResult(result, "application/xml");
		
		// Validate that the source docs were passed as expected
		List<String> sourceDocKeys = sourceDocMapKeys.getValue().toList();
		List<Document> sourceDocDocs = sourceDocMapDocs.getValue().toList();
		assertEquals(sourceDocuments.size(), sourceDocKeys.size());
		Map<String, Document> sourceDocMap = IntStream.range(0, sourceDocKeys.size()).boxed().collect(Collectors.toUnmodifiableMap(sourceDocKeys::get, sourceDocDocs::get));
		assertEquals(sourceDocuments, sourceDocMap);

		// Validate that the functions were passed as expected.
		isFailOnErrorFn.getValue().apply(assemblerOptionSpec);
		verify(assemblerOptionSpec).isFailOnError();
		isValidateOnlyFn.getValue().apply(assemblerOptionSpec);
		verify(assemblerOptionSpec).isValidateOnly();
		isTakeOwnershipFn.getValue().apply(assemblerOptionSpec);
		verify(assemblerOptionSpec).isTakeOwnership();
		getLogLevelFn.getValue().apply(assemblerOptionSpec);
		verify(assemblerOptionSpec).getLogLevel();
		getFirstBatesNumberFn.getValue().apply(assemblerOptionSpec);
		verify(assemblerOptionSpec).getFirstBatesNumber();
		getDefaultStyleFn.getValue().apply(assemblerOptionSpec);
		verify(assemblerOptionSpec).getDefaultStyle();
	}


	@Nested
	static class ConvertXmlToAssemblerResultTests {
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
		
		@Test
		void testConvertXmlToAssemblerEmptyResult() throws Exception {
			AssemblerResult result = RestServicesDocAssemblerServiceAdapter.convertXmlToAssemblerResult(new ByteArrayInputStream(EMPTY_ASSEMBLER_RESULT_XML.getBytes()));
			
			validateEmptyResult(result);
		}

	}
	
	private static void validatePopulatedResult(AssemblerResult result, String expectedContentType) throws IOException {
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

	private static void validateEmptyResult(AssemblerResult result) {
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

	@Test
	void testInvoke_RestClientException() throws Exception {
		// Mock just enough to get to get to the point of the exception
		var cause = new RestClientException("cause exception");
		byte[] srcDocumentDataBytes = "source Document Data".getBytes();
		Map<String, Object> sourceDocuments = 
				Collections.singletonMap("SourceDocument1", MockDocumentFactory.GLOBAL_INSTANCE.create(srcDocumentDataBytes));
		byte[] ddxBytes = "ddx Document Data".getBytes();
		Document ddx = MockDocumentFactory.GLOBAL_INSTANCE.create(ddxBytes);
		var assemblerOptionSpec = mock(AssemblerOptionsSpec.class);
		var underTest = createAdapter(mockClientFactory);

		var ex = mockForException(cause, ()->underTest.invoke(ddx, sourceDocuments, assemblerOptionSpec), POPULATED_ASSEMBLER_RESULT_XML.getBytes());
		
		assertThat(ex, allOf(ExceptionMatchers.exceptionMsgContainsAll("Error while POSTing to server"),
							 ExceptionMatchers.hasCause(cause)
							));
	}
	
	@Test
	void testInvoke_IOException() throws Exception {
		// Mock just enough to get to get to the point of the exception
		var cause = new IOException("cause exception");
		byte[] srcDocumentDataBytes = "source Document Data".getBytes();
		Map<String, Object> sourceDocuments = 
				Collections.singletonMap("SourceDocument1", MockDocumentFactory.GLOBAL_INSTANCE.create(srcDocumentDataBytes));
		byte[] ddxBytes = "ddx Document Data".getBytes();
		Document ddx = MockDocumentFactory.GLOBAL_INSTANCE.create(ddxBytes);
		var assemblerOptionSpec = mock(AssemblerOptionsSpec.class);
		var underTest = createAdapter(mockClientFactory);
		
		var ex = mockForException(cause, ()->underTest.invoke(ddx, sourceDocuments, assemblerOptionSpec), POPULATED_ASSEMBLER_RESULT_XML.getBytes());

		assertThat(ex, allOf(ExceptionMatchers.exceptionMsgContainsAll("I/O Error while securing document"),
				 			 ExceptionMatchers.hasCause(cause)
						     ));
	}

	@Test
	void testInvoke_XmlParseException() throws Exception {
		// Mock just enough to get to get to the point of the exception
		byte[] srcDocumentDataBytes = "source Document Data".getBytes();
		Map<String, Object> sourceDocuments = 
				Collections.singletonMap("SourceDocument1", MockDocumentFactory.GLOBAL_INSTANCE.create(srcDocumentDataBytes));
		byte[] ddxBytes = "ddx Document Data".getBytes();
		Document ddx = MockDocumentFactory.GLOBAL_INSTANCE.create(ddxBytes);

		when(mockPayloadBuilder.add(eq("ddx"), same(ddx), eq(ContentType.APPLICATION_XML))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStrings(eq("sourceDocumentMap.key"), sourceDocMapKeys.capture())).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addDocs(eq("sourceDocumentMap.value"), sourceDocMapDocs.capture())).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.transformAndAddStringVersion(eq("isFailOnError"), isNull(), any())).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.transformAndAddStringVersion(eq("isValidateOnly"), isNull(), any())).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.transformAndAddStringVersion(eq("isTakeOwnerShip"), isNull(), any())).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.transformAndAddStringVersion(eq("jobLogLevel"), isNull(), any())).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.transformAndAddStringVersion(eq("firstBatesNum"), isNull(), any())).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.transformAndAdd(eq("defaultStyle"), isNull(), any())).thenReturn(mockPayloadBuilder);

		setupMocks(setupMockResponse("This is not parseable XML, so will generate an XML Parsing Exception".getBytes(), ContentType.APPLICATION_XML));

		var underTest = createAdapter(mockClientFactory);
		
		var ex = assertThrows(AssemblerServiceException.class, ()->underTest.invoke(ddx, sourceDocuments, null));

		assertThat(ex, ExceptionMatchers.exceptionMsgContainsAll("Error while parsing xml response"));
	}

	private <T extends Exception> AssemblerServiceException mockForException(T exception, Executable test, byte[] response) throws Exception {
		
		Builder mockPayloadBuilder2 = Mockito.mock(Builder.class, Answers.RETURNS_SELF);
		when(mockClient.multipartPayloadBuilder()).thenReturn(mockPayloadBuilder2);
		when(mockPayloadBuilder2.build()).thenReturn(mockPayload);
		
		if (exception instanceof IOException) {
//			when(mockResponse.contentType()).thenReturn(ContentType.APPLICATION_XML);
			when(mockResponse.data()).thenReturn(new ByteArrayInputStream(response));
			when(mockPayload.postToServer(any())).thenReturn(Optional.of(mockResponse));
			Mockito.doThrow(exception).when(mockPayload).close();
		} else {
			when(mockPayload.postToServer(any())).thenThrow(exception);
		}
		return assertThrows(AssemblerServiceException.class, test);
	}
	
	
	private static final byte[] PDFA_DOCUMENT_CONTENTS = "PDFA Document Contents".getBytes(StandardCharsets.UTF_8);
	private static final byte[] JOB_LOG_DOCUMENT_CONTENTS = "JOB LOG Document Contents".getBytes(StandardCharsets.UTF_8);
	private static final byte[] CONVERSION_LOG_DOCUMENT_CONTENTS = "Conversion Log Document Contents".getBytes(StandardCharsets.UTF_8);

	private static final String EXPECTED_TOPDFA_RESULT_DATA = "<ToPdfAResult>\n"
			+ "  <ConversionLog>" + Base64.getEncoder().encodeToString(CONVERSION_LOG_DOCUMENT_CONTENTS) + "</ConversionLog>\n"
			+ "  <JobLog>" + Base64.getEncoder().encodeToString(JOB_LOG_DOCUMENT_CONTENTS) + "</JobLog>\n"
			+ "  <PdfADocument>" + Base64.getEncoder().encodeToString(PDFA_DOCUMENT_CONTENTS) + "</PdfADocument>\n"
			+ "  <IsPdfA>true</IsPdfA>\n"
			+ "</ToPdfAResult>\n";
	private static final byte[] EXPECTED_INPUT_DOCUMENT_CONTENT = "Pre-PDFA Document Contents".getBytes(StandardCharsets.UTF_8);

//	private enum ToPdfaHappyPathScenario {
//		NULL_ASSEMBLER_OPTIONS(()->null, InvokeHappyPathScenario::validateEmptySpec),
//		EMPTY_ASSEMBLER_OPTIONS(ToPdfaHappyPathScenario::emptySpec, ToPdfaHappyPathScenario::validateEmptySpec),
//		FULL_ASSEMBLER_OPTIONS(ToPdfaHappyPathScenario::fullSpec, ToPdfaHappyPathScenario::validateFullSpec);
//		
//		private static final String INPUT_DOCUMENT_PARAM = "inDoc";
//		private static final String COLOR_SPACE_PARAM = "colorSpace";
//		private static final String COMPLIANCE_PARAM = "compliance";
//		private static final String LOG_LEVEL_PARAM = "logLevel";
//		private static final String METADATA_EXTENSION_PARAM = "metadataExtension";
//		private static final String OPTIONAL_CONTENT_PARAM = "optionalContent";
//		private static final String RESULT_LEVEL_PARAM = "resultLevel";
//		private static final String SIGNATURES_PARAM = "signatures";
//		private static final String REMOVE_INVALID_XMP_PARAM = "removeInvalidXmlProperties";
//		private static final String RETAIN_PDF_FORM_STATE_PARAM = "retainPdfFormState";
//		private static final String VERIFY_PARAM = "verify";
//
//		private static final Document METADATA_DOCUMENT_VALUE = SimpleDocumentFactoryImpl.getFactory().create("metadataExtension Contents".getBytes(StandardCharsets.UTF_8));
//		private static final ColorSpace COLOR_SPACE_VALUE = ColorSpace.JAPAN_COLOR_COATED;
//		private static final Compliance COMPLIANCE_VALUE = Compliance.PDFA_2B;
//		private static final LogLevel LOG_LEVEL_VALUE = LogLevel.FINER;
//		private static final List<Document> METADATA_EXTENSION_VALUE = Collections.singletonList(METADATA_DOCUMENT_VALUE);
//		private static final OptionalContent OPTIONAL_CONTENT_VALUE = OptionalContent.VISIBLE;
//		private static final ResultLevel RESULT_LEVEL_VALUE = ResultLevel.SUMMARY;
//		private static final Signatures SIGNATURES_VALUE = Signatures.ARCHIVE_AS_NEEDED;
//		private static final boolean REMOVE_INVALID_XMP_VALUE = true;
//		private static final boolean RETAIN_PDF_FORM_STATE_VALUE = false;
//		private static final boolean VERIFY_VALUE = false;
//
//		Supplier<PDFAConversionOptionSpec> pdfaConversionOptionsSpec;
//		Consumer<FormDataMultiPart> pdfaConversionOptionsValidator;
//
//		private ToPdfaHappyPathScenario(Supplier<PDFAConversionOptionSpec> pdfaConversionOptionsSpec,
//				Consumer<FormDataMultiPart> pdfaConversionOptionsValidator) {
//			this.pdfaConversionOptionsSpec = pdfaConversionOptionsSpec;
//			this.pdfaConversionOptionsValidator = pdfaConversionOptionsValidator;
//		}
//
//		private static final PDFAConversionOptionSpec emptySpec() {
//			return new PDFAConversionOptionSpecImpl();
//		}
//		
//		private static final void validateEmptySpec(FormDataMultiPart postedData) {
//			// Since we passed in an uninitialized structure, we expect these fields not to be passed in.
//			validateFormFieldDoesNotExist(postedData, COLOR_SPACE_PARAM);
//			validateFormFieldDoesNotExist(postedData, COMPLIANCE_PARAM);
//			validateFormFieldDoesNotExist(postedData, LOG_LEVEL_PARAM);
//			validateFormFieldDoesNotExist(postedData, METADATA_EXTENSION_PARAM);
//			validateFormFieldDoesNotExist(postedData, OPTIONAL_CONTENT_PARAM);
//			validateFormFieldDoesNotExist(postedData, RESULT_LEVEL_PARAM);
//			validateFormFieldDoesNotExist(postedData, SIGNATURES_PARAM);
//			validateFormFieldDoesNotExist(postedData, REMOVE_INVALID_XMP_PARAM);
//			validateFormFieldDoesNotExist(postedData, RETAIN_PDF_FORM_STATE_PARAM);
//			validateFormFieldDoesNotExist(postedData, VERIFY_PARAM);
//		}
//		
//		private static final PDFAConversionOptionSpec fullSpec() {
//			PDFAConversionOptionSpecImpl options = new PDFAConversionOptionSpecImpl();
//			// TODO: Fill these in with values
//			options.setColorSpace(COLOR_SPACE_VALUE);
//			options.setCompliance(COMPLIANCE_VALUE);
//			options.setLogLevel(LOG_LEVEL_VALUE);
//			options.setMetadataSchemaExtensions(METADATA_EXTENSION_VALUE);
//			options.setOptionalContent(OPTIONAL_CONTENT_VALUE);
//			options.setRemoveInvalidXMPProperties(REMOVE_INVALID_XMP_VALUE);
//			options.setResultLevel(RESULT_LEVEL_VALUE);
//			options.setRetainPDFFormState(RETAIN_PDF_FORM_STATE_VALUE);
//			options.setSignatures(SIGNATURES_VALUE);
//			options.setVerify(VERIFY_VALUE);
//			return options;
//		}
//
//		private static final void validateFullSpec(FormDataMultiPart postedData) {
//			try {
//				validateTextFormField(postedData, COLOR_SPACE_PARAM, COLOR_SPACE_VALUE.toString());
//				validateTextFormField(postedData, COMPLIANCE_PARAM, COMPLIANCE_VALUE.toString());
//				validateTextFormField(postedData, LOG_LEVEL_PARAM, LOG_LEVEL_VALUE.toString());
//				validateDocumentFormField(postedData, METADATA_EXTENSION_PARAM, APPLICATION_XML, IOUtils.toByteArray(METADATA_DOCUMENT_VALUE.getInputStream()));
//				validateTextFormField(postedData, OPTIONAL_CONTENT_PARAM, OPTIONAL_CONTENT_VALUE.toString());
//				validateTextFormField(postedData, RESULT_LEVEL_PARAM, RESULT_LEVEL_VALUE.toString());
//				validateTextFormField(postedData, SIGNATURES_PARAM, SIGNATURES_VALUE.toString());
//				validateTextFormField(postedData, REMOVE_INVALID_XMP_PARAM, Boolean.toString(REMOVE_INVALID_XMP_VALUE));
//				validateTextFormField(postedData, RETAIN_PDF_FORM_STATE_PARAM, Boolean.toString(RETAIN_PDF_FORM_STATE_VALUE));
//				validateTextFormField(postedData, VERIFY_PARAM, Boolean.toString(VERIFY_VALUE));
//			} catch (IOException e) {
//				throw new IllegalStateException("IO Exception while validating options spec fields.", e);
//			}
//		}
//	}
//	
//	@ParameterizedTest
//	@EnumSource
//	void testToPdfA(ToPdfaHappyPathScenario scenario) throws Exception {
//		Document responseData = SimpleDocumentFactoryImpl.getFactory().create(EXPECTED_RESULT_DATA.getBytes(StandardCharsets.UTF_8));
//		RestServicesDocAssemblerServiceAdapter underTest = createAdapter(true, responseData, APPLICATION_XML);
//	
//		Document inPdf = SimpleDocumentFactoryImpl.getFactory().create(EXPECTED_INPUT_DOCUMENT_CONTENT);;
//		PDFAConversionOptionSpec options = scenario.pdfaConversionOptionsSpec.get();
//		PDFAConversionResult result = underTest.toPDFA(inPdf, options);
//		
//		// Validate the Response
//		assertAll(
//				()->assertArrayEquals(CONVERSION_LOG_DOCUMENT_CONTENTS, IOUtils.toByteArray(result.getConversionLog().getInputStream())),
//				()->assertArrayEquals(JOB_LOG_DOCUMENT_CONTENTS, IOUtils.toByteArray(result.getJobLog().getInputStream())),
//				()->assertArrayEquals(PDFA_DOCUMENT_CONTENTS, IOUtils.toByteArray(result.getPDFADocument().getInputStream())),
//				()->assertTrue(result.isPDFA())
//				);
//		
//		// Validate the inputs to the REST client captured
//		assertEquals("http://" + TEST_MACHINE_NAME + ":" + TEST_MACHINE_PORT, machineName.getValue());
//		assertEquals("/lc/services/AssemblerService/ToPdfA", path.getValue());
//
//		// Make sure that the arguments we passed in are transmitted correctly.
//		@SuppressWarnings("unchecked")
//		Entity<FormDataMultiPart> postedEntity = (Entity<FormDataMultiPart>)entity.getValue();
//		FormDataMultiPart postedData = postedEntity.getEntity();
//		
//		assertEquals(MediaType.MULTIPART_FORM_DATA_TYPE, postedEntity.getMediaType());
//		validateDocumentFormField(postedData, ToPdfaHappyPathScenario.INPUT_DOCUMENT_PARAM, APPLICATION_PDF, EXPECTED_INPUT_DOCUMENT_CONTENT);
//		// validate the assemblerOptions
//		scenario.pdfaConversionOptionsValidator.accept(postedData);
//	}
//	
	
	@Captor ArgumentCaptor<Function<PDFAConversionOptionSpec, ColorSpace>> getColorSpaceFn;
	@Captor ArgumentCaptor<Function<PDFAConversionOptionSpec, Compliance>> getComplianceFn;
	@Captor ArgumentCaptor<Function<PDFAConversionOptionSpec, LogLevel>> getLogLevelPdfaFn;
	@Captor ArgumentCaptor<Function<PDFAConversionOptionSpec, OptionalContent>> getOptionalContentFn;
	@Captor ArgumentCaptor<Function<PDFAConversionOptionSpec, ResultLevel>> getResultLevelFn;
	@Captor ArgumentCaptor<Function<PDFAConversionOptionSpec, Signatures>> getSignaturesFn;
	@Captor ArgumentCaptor<Function<PDFAConversionOptionSpec, Boolean>> isRemoveInvalidXMPPropertiesFn;
	@Captor ArgumentCaptor<Function<PDFAConversionOptionSpec, Boolean>> isRetainPDFFormStateFn;
	@Captor ArgumentCaptor<Function<PDFAConversionOptionSpec, Boolean>> isVerifyFn;
	@Captor ArgumentCaptor<Function<PDFAConversionOptionSpec, List<Document>>> getMetadataSchemaExtensionsFn;
	

	@Test
	void testToPdfA_HappyPath() throws Exception {
		RestServicesDocAssemblerServiceAdapter underTest = createAdapter(mockClientFactory);
	
		Document inPdf = SimpleDocumentFactoryImpl.getFactory().create(EXPECTED_INPUT_DOCUMENT_CONTENT);;
		PDFAConversionOptionSpec options = mock(PDFAConversionOptionSpec.class);

		when(mockPayloadBuilder.add(eq("inDoc"), same(inPdf), eq(ContentType.APPLICATION_DPL))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.transformAndAddStringVersion(eq("colorSpace"), same(options), getColorSpaceFn.capture())).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.transformAndAddStringVersion(eq("compliance"), same(options), getComplianceFn.capture())).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.transformAndAddStringVersion(eq("logLevel"), same(options), getLogLevelPdfaFn.capture())).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.transformAndAddStringVersion(eq("optionalContent"), same(options), getOptionalContentFn.capture())).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.transformAndAddStringVersion(eq("resultLevel"), same(options), getResultLevelFn.capture())).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.transformAndAddStringVersion(eq("signatures"), same(options), getSignaturesFn.capture())).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.transformAndAddStringVersion(eq("removeInvalidXmlProperties"), same(options), isRemoveInvalidXMPPropertiesFn.capture())).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.transformAndAddStringVersion(eq("retainPdfFormState"), same(options), isRetainPDFFormStateFn.capture())).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.transformAndAddStringVersion(eq("verify"), same(options), isVerifyFn.capture())).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.transformAndAddDocs(eq("metadataExtension"), same(options), getMetadataSchemaExtensionsFn.capture())).thenReturn(mockPayloadBuilder);

		setupMocks(setupMockResponse(EXPECTED_TOPDFA_RESULT_DATA.getBytes(StandardCharsets.UTF_8), ContentType.APPLICATION_XML));
	
		PDFAConversionResult result = underTest.toPDFA(inPdf, options);
		
		// Validate the Response
		assertAll(
				() -> assertArrayEquals(CONVERSION_LOG_DOCUMENT_CONTENTS,
						result.getConversionLog().getInputStream().readAllBytes()),
				() -> assertArrayEquals(JOB_LOG_DOCUMENT_CONTENTS, result.getJobLog().getInputStream().readAllBytes()),
				() -> assertArrayEquals(PDFA_DOCUMENT_CONTENTS,
						result.getPDFADocument().getInputStream().readAllBytes()),
				() -> assertTrue(result.isPDFA()));
		
		// Validate the functions passed as inputs to the REST client are correct by calling them and verifying that the correct methods were called.
		getColorSpaceFn.getValue().apply(options);
		verify(options).getColorSpace();
		getComplianceFn.getValue().apply(options);
		verify(options).getCompliance();
		getLogLevelPdfaFn.getValue().apply(options);
		verify(options).getLogLevel();
		getOptionalContentFn.getValue().apply(options);
		verify(options).getOptionalContent();
		getResultLevelFn.getValue().apply(options);
		verify(options).getResultLevel();
		getSignaturesFn.getValue().apply(options);
		verify(options).getSignatures();
		isRemoveInvalidXMPPropertiesFn.getValue().apply(options);
		verify(options).isRemoveInvalidXMPProperties();
		isRetainPDFFormStateFn.getValue().apply(options);
		verify(options).isRetainPDFFormState();
		isVerifyFn.getValue().apply(options);
		verify(options).isVerify();
		getMetadataSchemaExtensionsFn.getValue().apply(options);
		verify(options).getMetadataSchemaExtensions();
	}

	@Test
	void testToPdfA_RestClientException() throws Exception {
		// Mock just enough to get to get to the point of the exception
		var cause = new RestClientException("cause exception");
		Document inPdf = SimpleDocumentFactoryImpl.getFactory().create(EXPECTED_INPUT_DOCUMENT_CONTENT);;
		PDFAConversionOptionSpec options = mock(PDFAConversionOptionSpec.class);

		var underTest = createAdapter(mockClientFactory);

		var ex = mockForException(cause, ()->underTest.toPDFA(inPdf, options), EXPECTED_TOPDFA_RESULT_DATA.getBytes());
		
		assertThat(ex, allOf(ExceptionMatchers.exceptionMsgContainsAll("Error while POSTing to server"),
							 ExceptionMatchers.hasCause(cause)
							));
	}
	
	@Test
	void testToPdfA_IOException() throws Exception {
		// Mock just enough to get to get to the point of the exception
		var cause = new IOException("cause exception");
		Document inPdf = SimpleDocumentFactoryImpl.getFactory().create(EXPECTED_INPUT_DOCUMENT_CONTENT);;
		PDFAConversionOptionSpec options = mock(PDFAConversionOptionSpec.class);
		var underTest = createAdapter(mockClientFactory);
		
		var ex = mockForException(cause, ()->underTest.toPDFA(inPdf, options), EXPECTED_TOPDFA_RESULT_DATA.getBytes());

		assertThat(ex, allOf(ExceptionMatchers.exceptionMsgContainsAll("I/O Error while securing document"),
				 			 ExceptionMatchers.hasCause(cause)
						     ));
	}

	@Test
	void testToPdfA_XmlParseException() throws Exception {
		// Mock just enough to get to get to the point of the exception
		Document inPdf = SimpleDocumentFactoryImpl.getFactory().create(EXPECTED_INPUT_DOCUMENT_CONTENT);;
		PDFAConversionOptionSpec options = mock(PDFAConversionOptionSpec.class);

		when(mockPayloadBuilder.add(eq("inDoc"), same(inPdf), eq(ContentType.APPLICATION_DPL))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.transformAndAddStringVersion(eq("colorSpace"), same(options), getColorSpaceFn.capture())).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.transformAndAddStringVersion(eq("compliance"), same(options), getComplianceFn.capture())).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.transformAndAddStringVersion(eq("logLevel"), same(options), getLogLevelPdfaFn.capture())).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.transformAndAddStringVersion(eq("optionalContent"), same(options), getOptionalContentFn.capture())).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.transformAndAddStringVersion(eq("resultLevel"), same(options), getResultLevelFn.capture())).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.transformAndAddStringVersion(eq("signatures"), same(options), getSignaturesFn.capture())).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.transformAndAddStringVersion(eq("removeInvalidXmlProperties"), same(options), isRemoveInvalidXMPPropertiesFn.capture())).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.transformAndAddStringVersion(eq("retainPdfFormState"), same(options), isRetainPDFFormStateFn.capture())).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.transformAndAddStringVersion(eq("verify"), same(options), isVerifyFn.capture())).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.transformAndAddDocs(eq("metadataExtension"), same(options), getMetadataSchemaExtensionsFn.capture())).thenReturn(mockPayloadBuilder);

		setupMocks(setupMockResponse("This is not parseable XML, so will generate an XML Parsing Exception".getBytes(), ContentType.APPLICATION_XML));

		var underTest = createAdapter(mockClientFactory);
		
		var ex = assertThrows(AssemblerServiceException.class, ()->underTest.toPDFA(inPdf, options));

		assertThat(ex, ExceptionMatchers.exceptionMsgContainsAll("Error while parsing xml response"));
	}

	@Test
	void testIsPdfA() throws Exception {
		RestServicesDocAssemblerServiceAdapter underTest =createAdapter(mockClientFactory);
		
		Document inPdf = SimpleDocumentFactoryImpl.getFactory().create(EXPECTED_INPUT_DOCUMENT_CONTENT);;
		PDFAValidationOptionSpec options = new PDFAValidationOptionSpecImpl();
		UnsupportedOperationException ex = assertThrows(UnsupportedOperationException.class, ()->underTest.isPDFA(inPdf, options));
		
		assertThat(ex, ExceptionMatchers.exceptionMsgContainsAll("isPDFA has not been implemented yet."));
	}
	
	private static RestServicesDocAssemblerServiceAdapter createAdapter(TriFunction<AemConfig, String, Supplier<String>, RestClient> clientFactory) {
		return RestServicesDocAssemblerServiceAdapter.builder(clientFactory)
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
//		when(mockResponse.contentType()).thenReturn(expectedContentType);	// Not used in the current implementation.
		when(mockResponse.data()).thenReturn(new ByteArrayInputStream(responseData));
		return Optional.of(mockResponse);
	}
}
