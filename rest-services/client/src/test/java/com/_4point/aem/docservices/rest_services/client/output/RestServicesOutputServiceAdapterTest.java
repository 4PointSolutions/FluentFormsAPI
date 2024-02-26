package com._4point.aem.docservices.rest_services.client.output;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.StatusType;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.allOf;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com._4point.aem.docservices.rest_services.client.helpers.AemServerType;
import com._4point.aem.docservices.rest_services.client.output.RestServicesOutputServiceAdapter.OutputServiceBuilder;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.api.output.BatchOptions;
import com._4point.aem.fluentforms.api.output.PDFOutputOptions;
import com._4point.aem.fluentforms.api.output.PrintConfig;
import com._4point.aem.fluentforms.api.output.PrintedOutputOptions;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;
import com.adobe.fd.output.api.AcrobatVersion;
import com.adobe.fd.output.api.PaginationOverride;

@ExtendWith(MockitoExtension.class)
class RestServicesOutputServiceAdapterTest {

	private static final String PAGE_COUNT_HEADER = "com._4point.aem.rest_services.page_count";
	private static final long EXPECTED_PAGE_COUNT = 23L;
	private final static Document DUMMY_TEMPLATE_DOC = MockDocumentFactory.GLOBAL_DUMMY_DOCUMENT;
	private final static String DUMMY_TEMPLATE_STR = "TemplateString";
	private final static Document DUMMY_DATA = MockDocumentFactory.GLOBAL_DUMMY_DOCUMENT;

	private static final String CORRELATION_ID_HTTP_HDR = "X-Correlation-ID";
	private static final String CORRELATION_ID = "correlationId";
	private static final String TEST_MACHINE_NAME = "testmachinename";
	private static final int TEST_MACHINE_PORT = 8080;

	private static final MediaType APPLICATION_PDF = new MediaType("application", "pdf");
	private static final MediaType APPLICATION_PCL = new MediaType("application", "vnd.hp-pcl");
//	private static final MediaType APPLICATION_DPL = new MediaType("application", "vnd.datamax-dpl");
//	private static final MediaType APPLICATION_IPL = new MediaType("application", "vnd.intermec-ipl");
//	private static final MediaType APPLICATION_PS = new MediaType("application", "postscript");
//	private static final MediaType APPLICATION_TPCL = new MediaType("application", "vnd.toshiba-tpcl");
//	private static final MediaType APPLICATION_ZPL = new MediaType("x-application", "zpl");

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

	
	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testGeneratePDFOutput_NullArguments() throws Exception {
		RestServicesOutputServiceAdapter underTest = RestServicesOutputServiceAdapter.builder().build();
		
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
		RestServicesOutputServiceAdapter underTest = RestServicesOutputServiceAdapter.builder().build();
		
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

		public boolean isSsl() {
			return ssl;
		}

		public boolean hasData() {
			return hasData;
		}

		public boolean isTemplateString() {
			return templateString;
		}

		public boolean hasemptyOptions() {
			return emptyOptions;
		}
	};
	
	@ParameterizedTest
	@EnumSource(HappyPaths.class)
	void testGeneratePDFOutput_HappyPath(HappyPaths codePath) throws Exception {
		Document responseData = MockDocumentFactory.GLOBAL_INSTANCE.create("response Document Data".getBytes())
																   .setPageCount(EXPECTED_PAGE_COUNT);

		setUpMocks(responseData);
		
		boolean useSSL = false;
		boolean useCorrelationId = false;
		if (codePath.isSsl()) {
			useSSL = true;
			useCorrelationId = true;
			when(builder.header(eq(CORRELATION_ID_HTTP_HDR), correlationId.capture())).thenReturn(builder);
		} else {
			useSSL = false;
			useCorrelationId = false;
		}

		OutputServiceBuilder adapterBuilder = RestServicesOutputServiceAdapter.builder()
					.machineName(TEST_MACHINE_NAME)
					.port(TEST_MACHINE_PORT)
					.basicAuthentication("username", "password")
					.useSsl(useSSL)
					.aemServerType(AemServerType.StandardType.JEE)
					.clientFactory(()->client);

		if (useCorrelationId) {
			adapterBuilder.correlationId(()->CORRELATION_ID);
		}

		RestServicesOutputServiceAdapter underTest = adapterBuilder.build();

		Document pdf = MockDocumentFactory.GLOBAL_INSTANCE.create("pdf Document Data".getBytes());

		PDFOutputOptions pdfOutputOptions = Mockito.mock(PDFOutputOptions.class);
		if (!codePath.hasemptyOptions()) {
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
		}
		
		Document pdfResult;
		if (codePath.isTemplateString()) {
			pdfResult = underTest.generatePDFOutput(DUMMY_TEMPLATE_STR, codePath.hasData() ? DUMMY_DATA : null, pdfOutputOptions);
		} else {
			pdfResult = underTest.generatePDFOutput(DUMMY_TEMPLATE_DOC, codePath.hasData() ? DUMMY_DATA : null, pdfOutputOptions);
		}
		
		// Make sure the correct URL is called.
		final String expectedPrefix = useSSL ? "https://" : "http://";
		assertAll(
				()->assertThat("Expected target url contains '" + expectedPrefix + "'", machineName.getValue(), containsString(expectedPrefix)),
				()->assertThat("Expected target url contains TEST_MACHINE_NAME", machineName.getValue(), containsString(TEST_MACHINE_NAME)),
				()->assertThat("Expected target url contains TEST_MACHINE_PORT", machineName.getValue(), containsString(Integer.toString(TEST_MACHINE_PORT))),
				()->assertThat("Expected target url contains 'GeneratePdfOutput'", path.getValue(), containsString("GeneratePdfOutput"))
		);

		// Make sure that the arguments we passed in are transmitted correctly.
		@SuppressWarnings("unchecked")
		Entity<FormDataMultiPart> postedEntity = (Entity<FormDataMultiPart>)entity.getValue();
		FormDataMultiPart postedData = postedEntity.getEntity();
		
		assertEquals(MediaType.MULTIPART_FORM_DATA_TYPE, postedEntity.getMediaType());
		if (codePath.isTemplateString()) {
			validateTextFormField(postedData, "template", DUMMY_TEMPLATE_STR);
		} else {
			validateDocumentFormField(postedData, "template", new MediaType("application", "vnd.adobe.xdp+xml"), DUMMY_TEMPLATE_DOC.getInlineData());
		}
		if (codePath.hasData()) {
			validateDocumentFormField(postedData, "data", new MediaType("application", "xml"), DUMMY_DATA.getInlineData());
		} else {
			assertNull(postedData.getFields("data"));
		}
		
		if (useCorrelationId) {
			assertEquals(CORRELATION_ID, correlationId.getValue());
		}
		
		// Make sure the response is correct.
		assertArrayEquals(responseData.getInlineData(), pdfResult.getInlineData());
		assertEquals(APPLICATION_PDF, MediaType.valueOf(pdfResult.getContentType()));
		assertEquals(EXPECTED_PAGE_COUNT, pdfResult.getPageCount().get());
	}
	
	private void setUpMocks(Document responseData) throws IOException {
		// TODO: Change this based on https://maciejwalkowiak.com/mocking-fluent-interfaces/
		when(client.target(machineName.capture())).thenReturn(target);
		when(target.path(path.capture())).thenReturn(target);
		when(target.request()).thenReturn(builder);
		when(builder.accept(APPLICATION_PDF)).thenReturn(builder);
		when(builder.post(entity.capture())).thenReturn(response);
		when(response.getStatusInfo()).thenReturn(statusType);
		when(statusType.getFamily()).thenReturn(Response.Status.Family.SUCCESSFUL);	// return Successful
		when(response.hasEntity()).thenReturn(true);
		when(response.getEntity()).thenReturn(new ByteArrayInputStream(responseData.getInlineData()));
		when(response.getHeaderString(HttpHeaders.CONTENT_TYPE)).thenReturn("application/pdf");
		when(response.getHeaderString(PAGE_COUNT_HEADER)).thenReturn(Long.toString(EXPECTED_PAGE_COUNT));
	}
	
	@ParameterizedTest
	@EnumSource(HappyPaths.class)
	void testGeneratePrintedOutput_HappyPath(HappyPaths codePath) throws Exception {
		Document responseData = MockDocumentFactory.GLOBAL_INSTANCE.create("response Document Data".getBytes());

		setUpPrintMocks(responseData, APPLICATION_PCL);
		
		boolean useSSL = false;
		boolean useCorrelationId = false;
		if (codePath.isSsl()) {
			useSSL = true;
			useCorrelationId = true;
			when(builder.header(eq(CORRELATION_ID_HTTP_HDR), correlationId.capture())).thenReturn(builder);
		} else {
			useSSL = false;
			useCorrelationId = false;
		}

		 OutputServiceBuilder adapterBuilder = RestServicesOutputServiceAdapter.builder()
					.machineName(TEST_MACHINE_NAME)
					.port(TEST_MACHINE_PORT)
					.basicAuthentication("username", "password")
					.useSsl(useSSL)
					.aemServerType(AemServerType.StandardType.JEE)
					.clientFactory(()->client);

		if (useCorrelationId) {
			adapterBuilder.correlationId(()->CORRELATION_ID);
		}

		RestServicesOutputServiceAdapter underTest = adapterBuilder.build();

		PrintedOutputOptions printedOutputOptions = Mockito.mock(PrintedOutputOptions.class);
		if (!codePath.hasemptyOptions()) {
			when(printedOutputOptions.getContentRoot()).thenReturn(Mockito.mock(PathOrUrl.class));
			when(printedOutputOptions.getCopies()).thenReturn(1);
			when(printedOutputOptions.getDebugDir()).thenReturn(Mockito.mock(Path.class));
			when(printedOutputOptions.getLocale()).thenReturn(Locale.CANADA_FRENCH);
			when(printedOutputOptions.getPaginationOverride()).thenReturn(PaginationOverride.duplexLongEdge);
			when(printedOutputOptions.getPrintConfig()).thenReturn(PrintConfig.HP_PCL_5e);
			when(printedOutputOptions.getXci()).thenReturn(Mockito.mock(Document.class));
		}
		else {
			// Mandatory entry in PrintedOutputOptions.
			when(printedOutputOptions.getPrintConfig()).thenReturn(PrintConfig.HP_PCL_5e);
		}
		
		Document printResult;
		if (codePath.isTemplateString()) {
			printResult = underTest.generatePrintedOutput(DUMMY_TEMPLATE_STR, codePath.hasData() ? DUMMY_DATA : null, printedOutputOptions);
		} else {
			printResult = underTest.generatePrintedOutput(DUMMY_TEMPLATE_DOC, codePath.hasData() ? DUMMY_DATA : null, printedOutputOptions);
		}
		
		// Make sure the correct URL is called.
		final String expectedPrefix = useSSL ? "https://" : "http://";
		assertAll(
				()->assertThat("Expected target url contains '" + expectedPrefix + "'", machineName.getValue(), containsString(expectedPrefix)),
				()->assertThat("Expected target url contains TEST_MACHINE_NAME", machineName.getValue(), containsString(TEST_MACHINE_NAME)),
				()->assertThat("Expected target url contains TEST_MACHINE_PORT", machineName.getValue(), containsString(Integer.toString(TEST_MACHINE_PORT))),
				()->assertThat("Expected target url contains 'GeneratePrintedOutput'", path.getValue(), containsString("GeneratePrintedOutput"))
		);

		// Make sure that the arguments we passed in are transmitted correctly.
		@SuppressWarnings("unchecked")
		Entity<FormDataMultiPart> postedEntity = (Entity<FormDataMultiPart>)entity.getValue();
		FormDataMultiPart postedData = postedEntity.getEntity();
		
		assertEquals(MediaType.MULTIPART_FORM_DATA_TYPE, postedEntity.getMediaType());
		if (codePath.isTemplateString()) {
			validateTextFormField(postedData, "template", DUMMY_TEMPLATE_STR);
		} else {
			validateDocumentFormField(postedData, "template", new MediaType("application", "vnd.adobe.xdp+xml"), DUMMY_TEMPLATE_DOC.getInlineData());
		}
		if (codePath.hasData()) {
			validateDocumentFormField(postedData, "data", new MediaType("application", "xml"), DUMMY_DATA.getInlineData());
		} else {
			assertNull(postedData.getFields("data"));
		}
		
		if (useCorrelationId) {
			assertEquals(CORRELATION_ID, correlationId.getValue());
		}
		
		// Make sure the response is correct.
		assertArrayEquals(responseData.getInlineData(), printResult.getInlineData());
		assertEquals(APPLICATION_PCL, MediaType.valueOf(printResult.getContentType()));
	}
	
	private void setUpPrintMocks(Document responseData, MediaType mimeType) throws IOException {
		// TODO: Change this based on https://maciejwalkowiak.com/mocking-fluent-interfaces/
		when(client.target(machineName.capture())).thenReturn(target);
		when(target.path(path.capture())).thenReturn(target);
		when(target.request()).thenReturn(builder);
		when(builder.accept(mimeType)).thenReturn(builder);
		when(builder.post(entity.capture())).thenReturn(response);
		when(response.getStatusInfo()).thenReturn(statusType);
		when(statusType.getFamily()).thenReturn(Response.Status.Family.SUCCESSFUL);	// return Successful
		when(response.hasEntity()).thenReturn(true);
		when(response.getEntity()).thenReturn(new ByteArrayInputStream(responseData.getInlineData()));
		when(response.getHeaderString(HttpHeaders.CONTENT_TYPE)).thenReturn(mimeType.toString());
	}
	
	private void validateTextFormField(FormDataMultiPart postedData, String fieldName, String expectedData) throws IOException {
		List<FormDataBodyPart> pdfFields = postedData.getFields(fieldName);
		assertEquals(1, pdfFields.size());

		FormDataBodyPart pdfPart = pdfFields.get(0);
		assertEquals(MediaType.TEXT_PLAIN_TYPE, pdfPart.getMediaType());
		String value = (String) pdfPart.getEntity();
		assertEquals(expectedData, value);
	}
	
	private void validateDocumentFormField(FormDataMultiPart postedData, String fieldName, MediaType expectedMediaType, byte[] expectedData) throws IOException {
		List<FormDataBodyPart> pdfFields = postedData.getFields(fieldName);
		assertEquals(1, pdfFields.size());
		
		FormDataBodyPart pdfPart = pdfFields.get(0);
		assertEquals(expectedMediaType, pdfPart.getMediaType());
		byte[] pdfBytes = IOUtils.toByteArray((InputStream) pdfPart.getEntity());
		assertArrayEquals(expectedData, pdfBytes);  // TODO: Need to figure out how to test for entity.
	}
	
	@Test
	void testGeneratePDFOutputBatch() {
		RestServicesOutputServiceAdapter underTest = RestServicesOutputServiceAdapter.builder().build();
		UnsupportedOperationException ex = assertThrows(UnsupportedOperationException.class, ()->underTest.generatePDFOutputBatch(Collections.emptyMap(), Collections.emptyMap(), mock(PDFOutputOptions.class), mock(BatchOptions.class)));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, allOf(containsString("generatePDFOutputBatch"), containsString("is not implemented yet")));
	}

	@Test
	void testGeneratePrintedOutputBatch() {
		RestServicesOutputServiceAdapter underTest = RestServicesOutputServiceAdapter.builder().build();
		UnsupportedOperationException ex = assertThrows(UnsupportedOperationException.class, ()->underTest.generatePrintedOutputBatch(Collections.emptyMap(), Collections.emptyMap(), mock(PrintedOutputOptions.class), mock(BatchOptions.class)));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, allOf(containsString("generatePrintedOutputBatch"), containsString("is not implemented yet")));
	}
}
