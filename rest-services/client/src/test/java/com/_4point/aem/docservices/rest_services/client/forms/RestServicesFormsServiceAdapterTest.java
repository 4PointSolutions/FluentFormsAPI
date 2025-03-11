package com._4point.aem.docservices.rest_services.client.forms;

import static com._4point.testing.matchers.javalang.ExceptionMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com._4point.aem.docservices.rest_services.client.RestClient;
import com._4point.aem.docservices.rest_services.client.RestClient.ContentType;
import com._4point.aem.docservices.rest_services.client.RestClient.MultipartPayload;
import com._4point.aem.docservices.rest_services.client.RestClient.Response;
import com._4point.aem.docservices.rest_services.client.RestClient.RestClientException;
import com._4point.aem.docservices.rest_services.client.helpers.AemConfig;
import com._4point.aem.docservices.rest_services.client.helpers.AemServerType;
import com._4point.aem.docservices.rest_services.client.helpers.BuilderImpl.TriFunction;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.forms.PDFFormRenderOptions;
import com._4point.aem.fluentforms.api.output.PDFOutputOptions;
import com._4point.aem.fluentforms.api.output.PrintedOutputOptions;
import com._4point.aem.fluentforms.impl.forms.PDFFormRenderOptionsImpl;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;
import com.adobe.fd.forms.api.AcrobatVersion;
import com.adobe.fd.forms.api.CacheStrategy;
import com.adobe.fd.forms.api.DataFormat;
import com.adobe.fd.forms.api.RenderAtClient;

@ExtendWith(MockitoExtension.class)
class RestServicesFormsServiceAdapterTest {

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
		
	RestServicesFormsServiceAdapter underTest;

	@BeforeEach
	void setUp() throws Exception {
		when(mockClientFactory.apply(aemConfig.capture(), servicePath.capture(), correlationIdFn.capture())).thenReturn(mockClient);
		underTest = createAdapter(mockClientFactory);
	}

	@Test
	void testExportData_NullDoc() {
		DataFormat dataformat = com.adobe.fd.forms.api.DataFormat.XmlData;
		NullPointerException ex = assertThrows(NullPointerException.class, ()->underTest.exportData(null, dataformat));
		assertThat(ex, exceptionMsgContainsAll("Document cannot be null"));
	}
	
	@Test
	void testExportData_NullDataFormat() {
		Document pdforxdp = MockDocumentFactory.GLOBAL_INSTANCE.create("pdf Document Data".getBytes());
		NullPointerException ex = assertThrows(NullPointerException.class, ()->underTest.exportData(pdforxdp, null));
		assertThat(ex, exceptionMsgContainsAll("Data format cannot be null"));
	}
	
	@Test
	void testExportData_HappyPath() throws Exception {

		byte[] responseData = "ExportData response Document Data".getBytes();
				
		Document pdforxdp = MockDocumentFactory.GLOBAL_INSTANCE.create("pdf Document Data".getBytes());
		DataFormat dataformat = com.adobe.fd.forms.api.DataFormat.XmlData;

		when(mockPayloadBuilder.add(eq("pdforxdp"), same(pdforxdp), eq(ContentType.APPLICATION_PDF))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStringVersion(eq("dataformat"), same(dataformat))).thenReturn(mockPayloadBuilder);
		setupMocks(setupMockResponse(responseData, ContentType.APPLICATION_XML));

		Document pdfResult = underTest.exportData(pdforxdp, dataformat);
		
		// Make sure the response is correct.
		assertArrayEquals(responseData, pdfResult.getInputStream().readAllBytes());
		assertEquals(ContentType.APPLICATION_XML.contentType(), pdfResult.getContentType());
	}

	@Test
	void testImportData_NullDoc() {
		Document data = MockDocumentFactory.GLOBAL_INSTANCE.create("Data to be imported".getBytes());
		NullPointerException ex = assertThrows(NullPointerException.class, ()->underTest.importData(null, data));
		assertThat(ex, exceptionMsgContainsAll("PDF document cannot be null"));
	}
	
	@Test
	void testImportData_NullDataFormat() {
		Document targetDoc = MockDocumentFactory.GLOBAL_INSTANCE.create("Import target document".getBytes());
		NullPointerException ex = assertThrows(NullPointerException.class, ()->underTest.importData(targetDoc, null));
		assertThat(ex, exceptionMsgContainsAll("Data document cannot be null."));
	}
	

	@Test
	void testImportData_HappyPaths() throws Exception {

		byte[] responseData = "ImportData response Document Data".getBytes();
		Document pdf = MockDocumentFactory.GLOBAL_INSTANCE.create("pdf Document Data".getBytes());
		Document data = MockDocumentFactory.GLOBAL_INSTANCE.create("data Document Data".getBytes());

		when(mockPayloadBuilder.add(eq("pdf"), same(pdf), eq(ContentType.APPLICATION_PDF))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.add(eq("data"), same(data), eq(ContentType.APPLICATION_XML))).thenReturn(mockPayloadBuilder);
		setupMocks(setupMockResponse(responseData, ContentType.APPLICATION_PDF));

		Document pdfResult = underTest.importData(pdf, data);
		
		// Make sure the response is correct.
		assertArrayEquals(responseData, pdfResult.getInputStream().readAllBytes());
		assertEquals(ContentType.APPLICATION_PDF.contentType(), pdfResult.getContentType());
	}

	// Scenarios:
	// 1.  String template vs Document template
	// 2.  Data vs No Data
	// 3.  Empty options vs Full options
	private enum RenderFormsHappyPath { 
		WithData_TemplateString_EmptyOptions(true, true, true), 
		NoData_TemplateString_EmptyOptions(false, true, true), 
		WithData_TemplateDoc_EmptyOptions(true, false, true), 
		NoData_TemplateDoc_EmptyOptions(false, false, true), 
		WithData_TemplateString_FullOptions(true, true, false), 
		NoData_TemplateString_FullOptions(false, true, false), 
		WithData_TemplateDoc_FullOptions(true, false, false), 
		NoData_TemplateDoc_FullOptions(false, false, false);
		
		private final boolean hasData;
		private final boolean templateString;
		private final boolean emptyOptions;
		
		private RenderFormsHappyPath(boolean hasData, boolean templateString, boolean emptyOptions) {
			this.hasData = hasData;
			this.templateString = templateString;
			this.emptyOptions = emptyOptions;
		}
	};

	@ParameterizedTest
	@EnumSource
	void testRenderPDFForm_HappyPaths(RenderFormsHappyPath codePath) throws Exception {
		byte[] responseData = "RenderPDFForm response Document Data".getBytes();
		String templateFilename = "pdf FileName";
		Document templateDoc = MockDocumentFactory.GLOBAL_DUMMY_DOCUMENT;
		Document data = codePath.hasData ? MockDocumentFactory.GLOBAL_INSTANCE.create("data Document Data".getBytes()) : null;
		PDFFormRenderOptions pdfFormRenderOptions = codePath.emptyOptions ? new PDFFormRenderOptionsImpl() : getNonDefaultPdfRenderOptions();
		
		when(mockPayloadBuilder.addIfNotNull(eq("template"), codePath.templateString ? eq(templateFilename) : isNull())).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addIfNotNull(eq("template"), codePath.templateString ? isNull() : same(templateDoc), eq(ContentType.APPLICATION_XML))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addIfNotNull(eq("data"), codePath.hasData ? same(data) : isNull(), eq(ContentType.APPLICATION_XML))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStringVersion(eq("renderOptions.acrobatVersion"), eq(pdfFormRenderOptions.getAcrobatVersion()))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStringVersion(eq("renderOptions.cacheStrategy"), eq(pdfFormRenderOptions.getCacheStrategy()))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStringVersion(eq("renderOptions.contentRoot"), eq(pdfFormRenderOptions.getContentRoot()))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStringVersion(eq("renderOptions.debugDir"), eq(pdfFormRenderOptions.getDebugDir()))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStringVersion(eq("renderOptions.embedFonts"), eq(pdfFormRenderOptions.getEmbedFonts()))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStringVersion(eq("renderOptions.locale"), eq(pdfFormRenderOptions.getLocale()))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStringVersion(eq("renderOptions.renderAtClient"), eq(pdfFormRenderOptions.getRenderAtClient()))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStringVersion(eq("renderOptions.submitUrl"), eq(pdfFormRenderOptions.getSubmitUrls()))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addStringVersion(eq("renderOptions.taggedPdf"), eq(pdfFormRenderOptions.getTaggedPDF()))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addIfNotNull(eq("renderOptions.xci"), isNull(Document.class), eq(ContentType.APPLICATION_XML))).thenReturn(mockPayloadBuilder);
		
		setupMocks(setupMockResponse(responseData, ContentType.APPLICATION_PDF));
		
		Document pdfResult = codePath.templateString ? underTest.renderPDFForm(templateFilename, data, pdfFormRenderOptions) 
													 : underTest.renderPDFForm(templateDoc, data, pdfFormRenderOptions);
		
		// Make sure the response is correct.
		assertArrayEquals(responseData, pdfResult.getInputStream().readAllBytes());
		assertEquals(ContentType.APPLICATION_PDF.contentType(), pdfResult.getContentType());
	}

	@Test
	void testImportRenderPDFForm_NullRenderOptions() {
		NullPointerException ex = assertThrows(NullPointerException.class, ()->underTest.renderPDFForm("filename", null, null));
		assertThat(ex, exceptionMsgContainsAll("PdfFormRenderOptions cannot be null."));
	}
	
	@Test
	void testImportRenderPDFForm_NullTemplateDoc() {
		NullPointerException ex = assertThrows(NullPointerException.class, ()->underTest.renderPDFForm((Document)null, null, new PDFFormRenderOptionsImpl()));
		assertThat(ex, exceptionMsgContainsAll("Template document cannot be null."));
	}

	@Test
	void testImportRenderPDFForm_NullTemplateString() {
		NullPointerException ex = assertThrows(NullPointerException.class, ()->underTest.renderPDFForm((String)null, null, new PDFFormRenderOptionsImpl()));
		assertThat(ex, exceptionMsgContainsAll("Template string cannot be null."));
	}

	private static final AcrobatVersion NON_DEFAULT_ACROBAT_VERSION = AcrobatVersion.Acrobat_10_1;
	private static final CacheStrategy NON_DEFAULT_CACHE_STRATEGY = CacheStrategy.NONE;
	private static final Path NON_DEFAULT_CONTENT_ROOT = Path.of("foo", "bar");
	private static final Path NON_DEFAULT_DEBUG_DIR = Path.of("bar", "foo");
	private static final boolean NON_DEFAULT_EMBED_FONTS = true;
	private static final Locale NON_DEFAULT_LOCALE = Locale.CANADA_FRENCH;
	private static final RenderAtClient NON_DEFAULT_RENDER_AT_CLIENT = RenderAtClient.NO;
	private static final String NON_DEFAULT_SUBMIT_URL = "http://example.com";
	private static final boolean NON_DEFAULT_TAGGED_PDF = true;

	private PDFFormRenderOptions getNonDefaultPdfRenderOptions() throws MalformedURLException {
		PDFFormRenderOptions options = new PDFFormRenderOptionsImpl();
		options.setAcrobatVersion(NON_DEFAULT_ACROBAT_VERSION);
		options.setCacheStrategy(NON_DEFAULT_CACHE_STRATEGY);
		options.setContentRoot(NON_DEFAULT_CONTENT_ROOT);
		options.setDebugDir(NON_DEFAULT_DEBUG_DIR);
		options.setEmbedFonts(NON_DEFAULT_EMBED_FONTS);
		options.setLocale(NON_DEFAULT_LOCALE);
		options.setRenderAtClient(NON_DEFAULT_RENDER_AT_CLIENT);
		options.setSubmitUrl(new URL(NON_DEFAULT_SUBMIT_URL));
		options.setTaggedPDF(NON_DEFAULT_TAGGED_PDF);
		// Omit the creation of XCI document because that would require a real Adobe implementation to be available.
//		options.setXci(new MockDocumentFactory().create(new byte[0]));
		return options;
	}

	@Disabled
	void testValidate() {
		fail("Not yet implemented");
	}

	private static RestServicesFormsServiceAdapter createAdapter(TriFunction<AemConfig, String, Supplier<String>, RestClient> clientFactory) {
		return RestServicesFormsServiceAdapter.builder(clientFactory)
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


	static class FormsServiceBuilderTests {
		@Mock RestClient mockClient;

		@Test
		void testNonDefaultValues() throws Exception {
			RestServicesFormsServiceAdapter.builder(this::validateNonDefaultValues)
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
					()->assertThat(target, anyOf(equalTo("/lc/services/FormsService/RenderPdfForm"),
												 equalTo("/lc/services/FormsService/ExportData"),
												 equalTo("/lc/services/FormsService/ImportData")))
					);
			return mockClient;
		}
		
		@Test
		void testDefaultValues() throws Exception {
			RestServicesFormsServiceAdapter.builder(this::validateDefaultValues).build();
		}

		RestClient validateDefaultValues(AemConfig aemConfig, String target, Supplier<String> correlationidFn) {
			assertAll(
					()->assertNull(correlationidFn),
					()->assertEquals("admin", aemConfig.user()),
					()->assertEquals("admin", aemConfig.password()),
					()->assertEquals("http://localhost:4502/", aemConfig.url()),
					()->assertThat(target, anyOf(equalTo("/services/FormsService/RenderPdfForm"),
							 					 equalTo("/services/FormsService/ExportData"),
							 					 equalTo("/services/FormsService/ImportData")))
					);
			return mockClient;
		}
	}
}