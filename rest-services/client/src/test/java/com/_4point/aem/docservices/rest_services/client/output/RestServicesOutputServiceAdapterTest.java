package com._4point.aem.docservices.rest_services.client.output;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static com._4point.aem.docservices.rest_services.client.helpers.AemConfigMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Locale;
import java.util.Optional;
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
import com._4point.aem.docservices.rest_services.client.helpers.BuilderImpl.TriFunction;
import com._4point.aem.docservices.rest_services.client.output.RestServicesOutputServiceAdapter.OutputServiceBuilder;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.api.output.BatchOptions;
import com._4point.aem.fluentforms.api.output.OutputService.OutputServiceException;
import com._4point.aem.fluentforms.api.output.PDFOutputOptions;
import com._4point.aem.fluentforms.api.output.PrintConfig;
import com._4point.aem.fluentforms.api.output.PrintedOutputOptions;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;
import com.adobe.fd.output.api.AcrobatVersion;
import com.adobe.fd.output.api.PaginationOverride;

@ExtendWith(MockitoExtension.class)
class RestServicesOutputServiceAdapterTest {

	private final static Document DUMMY_TEMPLATE_DOC = MockDocumentFactory.GLOBAL_DUMMY_DOCUMENT;
	private final static String DUMMY_TEMPLATE_STR = "TemplateString";
	private final static Document DUMMY_DATA = MockDocumentFactory.GLOBAL_DUMMY_DOCUMENT;

	private static final String CORRELATION_ID = "correlationId";
	private static final String TEST_MACHINE_NAME = "testmachinename";
	private static final int TEST_MACHINE_PORT = 8080;

	@Mock(stubOnly = true) TriFunction<AemConfig, String, Supplier<String>, RestClient> mockClientFactory;
	@Mock(stubOnly = true) RestClient mockClient;
	@Mock(stubOnly = true) MultipartPayload mockPayload;
	@Mock(stubOnly = true) MultipartPayload.Builder mockPayloadBuilder;
	@Mock(stubOnly = true) Response mockResponse;
	@Mock(stubOnly = true) PDFOutputOptions pdfOutputOptions;
	@Mock(stubOnly = true) PrintedOutputOptions printedOutputOptions;

	@Captor ArgumentCaptor<AemConfig> aemConfig;
	@Captor ArgumentCaptor<String> servicePath;
	@Captor ArgumentCaptor<InputStream> postBodyBytes;
	@Captor ArgumentCaptor<ContentType> acceptableContentType;
	@Captor ArgumentCaptor<Supplier<String>> correlationIdFn;
		
	@BeforeEach
	void setup() {
		when(mockClientFactory.apply(aemConfig.capture(), servicePath.capture(), correlationIdFn.capture())).thenReturn(mockClient);
	}


	@Test
	void testGeneratePDFOutput_NullArguments() throws Exception {
		RestServicesOutputServiceAdapter underTest = RestServicesOutputServiceAdapter.builder(mockClientFactory).build();
		
		NullPointerException ex1 = assertThrows(NullPointerException.class, ()->underTest.generatePDFOutput((Document)null, null, null));
		assertThat(ex1.getMessage(), containsStringIgnoringCase("template"));
		assertThat(ex1.getMessage(), containsStringIgnoringCase("cannot be null"));
		NullPointerException ex2 = assertThrows(NullPointerException.class, ()->underTest.generatePDFOutput((String)null, null, null));
		assertThat(ex2.getMessage(), containsStringIgnoringCase("template"));
		assertThat(ex2.getMessage(), containsStringIgnoringCase("cannot be null"));
		NullPointerException ex3 = assertThrows(NullPointerException.class, ()->underTest.generatePDFOutput(DUMMY_TEMPLATE_DOC, null, null));
		assertThat(ex3.getMessage(), containsStringIgnoringCase("PdfOutputOptions"));
		assertThat(ex3.getMessage(), containsStringIgnoringCase("cannot be null"));
		NullPointerException ex4 = assertThrows(NullPointerException.class, ()->underTest.generatePDFOutput(DUMMY_TEMPLATE_STR, null, null));
		assertThat(ex4.getMessage(), containsStringIgnoringCase("PdfOutputOptions"));
		assertThat(ex4.getMessage(), containsStringIgnoringCase("cannot be null"));
	}

	@Test
	void testGeneratePrintedOutput_NullArguments() throws Exception {
		RestServicesOutputServiceAdapter underTest = RestServicesOutputServiceAdapter.builder(mockClientFactory).build();
		
		NullPointerException ex1 = assertThrows(NullPointerException.class, ()->underTest.generatePrintedOutput((Document)null, null, null));
		assertThat(ex1.getMessage(), containsStringIgnoringCase("template"));
		assertThat(ex1.getMessage(), containsStringIgnoringCase("cannot be null"));
		NullPointerException ex2 = assertThrows(NullPointerException.class, ()->underTest.generatePrintedOutput((String)null, null, null));
		assertThat(ex2.getMessage(), containsStringIgnoringCase("template"));
		assertThat(ex2.getMessage(), containsStringIgnoringCase("cannot be null"));
		NullPointerException ex3 = assertThrows(NullPointerException.class, ()->underTest.generatePrintedOutput(DUMMY_TEMPLATE_DOC, null, null));
		assertThat(ex3.getMessage(), containsStringIgnoringCase("PrintedOutputOptions"));
		assertThat(ex3.getMessage(), containsStringIgnoringCase("cannot be null"));
		NullPointerException ex4 = assertThrows(NullPointerException.class, ()->underTest.generatePrintedOutput(DUMMY_TEMPLATE_STR, null, null));
		assertThat(ex4.getMessage(), containsStringIgnoringCase("PrintedOutputOptions"));
		assertThat(ex4.getMessage(), containsStringIgnoringCase("cannot be null"));
	}

	private enum HappyPaths { 
		SSL_WithData_TemplateString_EmptyOptions(true, true, true, true), 
		NoSSL_WithData_TemplateString_EmptyOptions(false, true, true, true),
		SSL_NoData_TemplateString_EmptyOptions(true, false, true, true), 
		NoSSL_NoData_TemplateString_EmptyOptions(false, false, true, true),
		SSL_WithData_TemplateDoc_EmptyOptions(true, true, false, true), 
		NoSSL_WithData_TemplateDoc_EmptyOptions(false, true, false, true),
		SSL_NoData_TemplateDoc_EmptyOptions(true, false, false, true), 
		NoSSL_NoData_TemplateDoc_EmptyOptions(false, false, false, true),
		SSL_WithData_TemplateString_FullOptions(true, true, true, false), 
		NoSSL_WithData_TemplateString_FullOptions(false, true, true, false),
		SSL_NoData_TemplateString_FullOptions(true, false, true, false), 
		NoSSL_NoData_TemplateString_FullOptions(false, false, true, false),
		SSL_WithData_TemplateDoc_FullOptions(true, true, false, false), 
		NoSSL_WithData_TemplateDoc_FullOptions(false, true, false, false),
		SSL_NoData_TemplateDoc_FullOptions(true, false, false, false), 
		NoSSL_NoData_TemplateDoc_FullOptions(false, false, false, false);
		
		private final boolean ssl;
		private final boolean hasData;
		private final boolean templateString;
		private final boolean emptyOptions;
		
		private HappyPaths(boolean ssl, boolean hasData, boolean templateString, boolean emptyOptions) {
			this.ssl = ssl;
			this.hasData = hasData;
			this.templateString = templateString;
			this.emptyOptions = emptyOptions;
		}

		public boolean useSsl() {
			return ssl;
		}

		public boolean useCorrelationId() {	// We test correlationId and SSL at the same time.
			return ssl;
		}

		public boolean hasData() {
			return hasData;
		}

		public boolean isTemplateString() {
			return templateString;
		}

		public boolean hasEmptyOptions() {
			return emptyOptions;
		}
	};
	
	@ParameterizedTest
	@EnumSource(HappyPaths.class)
	void testGeneratePDFOutput_HappyPath(HappyPaths codePath) throws Exception {
		if (!codePath.hasEmptyOptions()) {
			// Stub the values for pdfOutputOptions
			when(pdfOutputOptions.getAcrobatVersion()).thenReturn(AcrobatVersion.Acrobat_10_1);
			when(pdfOutputOptions.getContentRoot()).thenReturn(Mockito.mock(PathOrUrl.class));
			when(pdfOutputOptions.getDebugDir()).thenReturn(Mockito.mock(Path.class));
			when(pdfOutputOptions.getEmbedFonts()).thenReturn(Boolean.TRUE);
			when(pdfOutputOptions.getLinearizedPDF()).thenReturn(Boolean.TRUE);
			when(pdfOutputOptions.getLocale()).thenReturn(Locale.CANADA_FRENCH);
			when(pdfOutputOptions.getRetainPDFFormState()).thenReturn(Boolean.TRUE);
			when(pdfOutputOptions.getRetainUnsignedSignatureFields()).thenReturn(Boolean.TRUE);
			when(pdfOutputOptions.getTaggedPDF()).thenReturn(Boolean.TRUE);
			when(pdfOutputOptions.getXci()).thenReturn(Mockito.mock(Document.class));
			
			// Setup the expected calls to mockPayloadBuilder
			when(mockPayloadBuilder.addStringVersion(eq("outputOptions.acrobatVersion"), eq(AcrobatVersion.Acrobat_10_1))).thenReturn(mockPayloadBuilder);
			when(mockPayloadBuilder.addStringVersion(eq("outputOptions.contentRoot"), Mockito.any(PathOrUrl.class))).thenReturn(mockPayloadBuilder);
			when(mockPayloadBuilder.addStringVersion(eq("outputOptions.debugDir"), Mockito.any(Path.class))).thenReturn(mockPayloadBuilder);
			when(mockPayloadBuilder.addStringVersion(eq("outputOptions.embedFonts"), eq(Boolean.TRUE))).thenReturn(mockPayloadBuilder);
			when(mockPayloadBuilder.addStringVersion(eq("outputOptions.linearizedPdf"), eq(Boolean.TRUE))).thenReturn(mockPayloadBuilder);
			when(mockPayloadBuilder.addStringVersion(eq("outputOptions.locale"), eq(Locale.CANADA_FRENCH))).thenReturn(mockPayloadBuilder);
			when(mockPayloadBuilder.addStringVersion(eq("outputOptions.retainPdfFormState"), eq(Boolean.TRUE))).thenReturn(mockPayloadBuilder);
			when(mockPayloadBuilder.addStringVersion(eq("outputOptions.retainUnsignedSignatureFields"), eq(Boolean.TRUE))).thenReturn(mockPayloadBuilder);
			when(mockPayloadBuilder.addStringVersion(eq("outputOptions.taggedPdf"), eq(Boolean.TRUE))).thenReturn(mockPayloadBuilder);
			when(mockPayloadBuilder.addIfNotNull(eq("outputOptions.xci"), Mockito.any(Document.class), eq(ContentType.APPLICATION_XML))).thenReturn(mockPayloadBuilder);
		} else {
			when(mockPayloadBuilder.addStringVersion(eq("outputOptions.acrobatVersion"), isNull(AcrobatVersion.class))).thenReturn(mockPayloadBuilder);
			when(mockPayloadBuilder.addStringVersion(eq("outputOptions.contentRoot"), isNull(PathOrUrl.class))).thenReturn(mockPayloadBuilder);
			when(mockPayloadBuilder.addStringVersion(eq("outputOptions.debugDir"), isNull(Path.class))).thenReturn(mockPayloadBuilder);
			when(mockPayloadBuilder.addStringVersion(eq("outputOptions.embedFonts"), eq(Boolean.FALSE))).thenReturn(mockPayloadBuilder);
			when(mockPayloadBuilder.addStringVersion(eq("outputOptions.linearizedPdf"), eq(Boolean.FALSE))).thenReturn(mockPayloadBuilder);
			when(mockPayloadBuilder.addStringVersion(eq("outputOptions.locale"), isNull(Locale.class))).thenReturn(mockPayloadBuilder);
			when(mockPayloadBuilder.addStringVersion(eq("outputOptions.retainPdfFormState"), eq(Boolean.FALSE))).thenReturn(mockPayloadBuilder);
			when(mockPayloadBuilder.addStringVersion(eq("outputOptions.retainUnsignedSignatureFields"), eq(Boolean.FALSE))).thenReturn(mockPayloadBuilder);
			when(mockPayloadBuilder.addStringVersion(eq("outputOptions.taggedPdf"), eq(Boolean.FALSE))).thenReturn(mockPayloadBuilder);
			when(mockPayloadBuilder.addIfNotNull(eq("outputOptions.xci"), isNull(Document.class), eq(ContentType.APPLICATION_XML))).thenReturn(mockPayloadBuilder);
		}

		RestServicesOutputServiceAdapter underTest = createAdapter(codePath);

		runTest(codePath, pdfOutputOptions, underTest::generatePDFOutput, underTest::generatePDFOutput, ContentType.APPLICATION_PDF);
	}

	@ParameterizedTest
	@EnumSource(HappyPaths.class)
	void testGeneratePrintedOutput_HappyPath(HappyPaths codePath) throws Exception {
		if (!codePath.hasEmptyOptions()) {
			// Stub the values for pdfOutputOptions
			when(printedOutputOptions.getContentRoot()).thenReturn(Mockito.mock(PathOrUrl.class));
			when(printedOutputOptions.getCopies()).thenReturn(1);
			when(printedOutputOptions.getDebugDir()).thenReturn(Mockito.mock(Path.class));
			when(printedOutputOptions.getLocale()).thenReturn(Locale.CANADA_FRENCH);
			when(printedOutputOptions.getPaginationOverride()).thenReturn(PaginationOverride.duplexLongEdge);
			when(printedOutputOptions.getPrintConfig()).thenReturn(PrintConfig.HP_PCL_5e);
			when(printedOutputOptions.getXci()).thenReturn(Mockito.mock(Document.class));
			
			// Setup the expected calls to mockPayloadBuilder
			when(mockPayloadBuilder.addStringVersion(eq("outputOptions.contentRoot"), Mockito.any(PathOrUrl.class))).thenReturn(mockPayloadBuilder);
			when(mockPayloadBuilder.addStringVersion(eq("outputOptions.copies"), eq(Integer.valueOf(1)))).thenReturn(mockPayloadBuilder);
			when(mockPayloadBuilder.addStringVersion(eq("outputOptions.debugDir"), Mockito.any(Path.class))).thenReturn(mockPayloadBuilder);
			when(mockPayloadBuilder.addStringVersion(eq("outputOptions.locale"), eq(Locale.CANADA_FRENCH))).thenReturn(mockPayloadBuilder);
			when(mockPayloadBuilder.addStringVersion(eq("outputOptions.paginationOverride"), eq(PaginationOverride.duplexLongEdge))).thenReturn(mockPayloadBuilder);
			when(mockPayloadBuilder.addStringVersion(eq("outputOptions.printConfig"), eq(PrintConfig.HP_PCL_5e))).thenReturn(mockPayloadBuilder);
			when(mockPayloadBuilder.addIfNotNull(eq("outputOptions.xci"), Mockito.any(Document.class), eq(ContentType.APPLICATION_XML))).thenReturn(mockPayloadBuilder);
		} else {
			// Mandatory entry in PrintedOutputOptions.
			when(printedOutputOptions.getPrintConfig()).thenReturn(PrintConfig.HP_PCL_5e);
			
			when(mockPayloadBuilder.addStringVersion(eq("outputOptions.contentRoot"), isNull(PathOrUrl.class))).thenReturn(mockPayloadBuilder);
			when(mockPayloadBuilder.addStringVersion(eq("outputOptions.copies"), eq(Integer.valueOf(0)))).thenReturn(mockPayloadBuilder);
			when(mockPayloadBuilder.addStringVersion(eq("outputOptions.debugDir"), isNull(Path.class))).thenReturn(mockPayloadBuilder);
			when(mockPayloadBuilder.addStringVersion(eq("outputOptions.locale"), isNull(Locale.class))).thenReturn(mockPayloadBuilder);
			when(mockPayloadBuilder.addStringVersion(eq("outputOptions.paginationOverride"), isNull(PaginationOverride.class))).thenReturn(mockPayloadBuilder);
			when(mockPayloadBuilder.addStringVersion(eq("outputOptions.printConfig"), eq(PrintConfig.HP_PCL_5e))).thenReturn(mockPayloadBuilder);
			when(mockPayloadBuilder.addIfNotNull(eq("outputOptions.xci"), isNull(Document.class), eq(ContentType.APPLICATION_XML))).thenReturn(mockPayloadBuilder);
		}

		RestServicesOutputServiceAdapter underTest = createAdapter(codePath);

		runTest(codePath, printedOutputOptions, underTest::generatePrintedOutput, underTest::generatePrintedOutput, ContentType.APPLICATION_PCL);
	}

	// Since both generatePdfOutput and generatePrintedOutput are so similar and they produce the same results (a Document object),
	// the setup and test code can be combined into into one common function,
	private <T> void runTest(HappyPaths codePath, 
			 		  T mockOptions, 
			 		  TriFunctionWithException<Document, Document, T, Document, OutputServiceException> docFn,
			 		  TriFunctionWithException<String, Document, T, Document, OutputServiceException> stringFn,
			 		  ContentType responseContentType
			 		  ) throws OutputServiceException, RestClientException, IOException {
			byte[] responseData = "response Document Data".getBytes();

			when(mockClient.multipartPayloadBuilder()).thenReturn(mockPayloadBuilder);
			when(mockPayloadBuilder.build()).thenReturn(mockPayload);
			when(mockPayload.postToServer(acceptableContentType.capture())).thenReturn(Optional.of(mockResponse));
			when(mockResponse.contentType()).thenReturn(responseContentType);
			when(mockResponse.data()).thenReturn(new ByteArrayInputStream(responseData));
			
			if (codePath.hasData()) {
				when(mockPayloadBuilder.addIfNotNull(eq("data"), eq(DUMMY_DATA), eq(ContentType.APPLICATION_XML))).thenReturn(mockPayloadBuilder);
			} else {
				when(mockPayloadBuilder.addIfNotNull(eq("data"), Mockito.<Document>isNull(), eq(ContentType.APPLICATION_XML))).thenReturn(mockPayloadBuilder);
			}

			if (codePath.isTemplateString()) {
				when(mockPayloadBuilder.addIfNotNull(eq("template"), eq(DUMMY_TEMPLATE_STR))).thenReturn(mockPayloadBuilder);
				when(mockPayloadBuilder.addIfNotNull(eq("template"), Mockito.<Document>isNull(), eq(ContentType.APPLICATION_XDP))).thenReturn(mockPayloadBuilder);
			} else {
				when(mockPayloadBuilder.addIfNotNull(eq("template"), Mockito.<String>isNull())).thenReturn(mockPayloadBuilder);
				when(mockPayloadBuilder.addIfNotNull(eq("template"), eq(DUMMY_TEMPLATE_DOC), eq(ContentType.APPLICATION_XDP))).thenReturn(mockPayloadBuilder);
			}

			// When
			Document printResult = codePath.isTemplateString() 
											? stringFn.apply(DUMMY_TEMPLATE_STR, codePath.hasData() ? DUMMY_DATA : null, mockOptions)
											: docFn.apply(DUMMY_TEMPLATE_DOC, codePath.hasData() ? DUMMY_DATA : null, mockOptions);
			
			// Then
			// Make sure the correct URL is called.
			assertThat("Expected target url contains 'OutputService' and 'GeneratePdfOutput'", servicePath.getAllValues().get(0), allOf(containsString("OutputService"), containsString("GeneratePdfOutput")));

			assertThat(aemConfig.getValue(), allOf(
					useSsl(equalTo(codePath.useSsl())),
					servername(equalTo(TEST_MACHINE_NAME)),
					port(equalTo(TEST_MACHINE_PORT))
					));
			
			// Make sure that the arguments we passed in are transmitted correctly.
			
			if (codePath.useCorrelationId()) {
				assertEquals(CORRELATION_ID, correlationIdFn.getValue().get());
			}
			
			// Make sure the response is correct.
			assertArrayEquals(responseData, printResult.getInputStream().readAllBytes());
			assertEquals(responseContentType.contentType(), printResult.getContentType());
			
			// Make sure we sent the correct contentTyoe
			assertEquals(responseContentType, acceptableContentType.getValue());
		 
	 }


	private RestServicesOutputServiceAdapter createAdapter(HappyPaths codePath) {
		OutputServiceBuilder adapterBuilder = RestServicesOutputServiceAdapter.builder(mockClientFactory)
					.machineName(TEST_MACHINE_NAME)
					.port(TEST_MACHINE_PORT)
					.basicAuthentication("username", "password")
					.useSsl(codePath.useSsl())
					.aemServerType(AemServerType.StandardType.JEE);

		if (codePath.useCorrelationId()) {
			adapterBuilder.correlationId(()->CORRELATION_ID);
		}

		return adapterBuilder.build();
	}
	
	@Test
	void testGeneratePDFOutputBatch() {
		RestServicesOutputServiceAdapter underTest = RestServicesOutputServiceAdapter.builder(mockClientFactory).build();
		UnsupportedOperationException ex = assertThrows(UnsupportedOperationException.class, ()->underTest.generatePDFOutputBatch(Collections.emptyMap(), Collections.emptyMap(), mock(PDFOutputOptions.class), mock(BatchOptions.class)));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, allOf(containsString("generatePDFOutputBatch"), containsString("is not implemented yet")));
	}

	@Test
	void testGeneratePrintedOutputBatch() {
		RestServicesOutputServiceAdapter underTest = RestServicesOutputServiceAdapter.builder(mockClientFactory).build();
		UnsupportedOperationException ex = assertThrows(UnsupportedOperationException.class, ()->underTest.generatePrintedOutputBatch(Collections.emptyMap(), Collections.emptyMap(), mock(PrintedOutputOptions.class), mock(BatchOptions.class)));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, allOf(containsString("generatePrintedOutputBatch"), containsString("is not implemented yet")));
	}
	
	@FunctionalInterface
	public interface TriFunctionWithException<T, U, V, R, E extends Exception> {
	    R apply(T t, U u, V v) throws E;
	}

}
