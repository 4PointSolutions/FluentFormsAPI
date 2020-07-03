package com._4point.aem.docservices.rest_services.client.forms;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.forms.FormsService.FormsServiceException;
import com._4point.aem.fluentforms.api.forms.PDFFormRenderOptions;
import com._4point.aem.fluentforms.impl.forms.PDFFormRenderOptionsImpl;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;
import com.adobe.fd.forms.api.AcrobatVersion;
import com.adobe.fd.forms.api.CacheStrategy;
import com.adobe.fd.forms.api.DataFormat;
import com.adobe.fd.forms.api.RenderAtClient;

@ExtendWith(MockitoExtension.class)
class RestServicesFormsServiceAdapterTest {

	private static final String CORRELATION_ID_HTTP_HDR = "X-Correlation-ID";
	private static final String CORRELATION_ID = "correlationId";
	private static final String TEST_MACHINE_NAME = "testmachinename";
	private static final int TEST_MACHINE_PORT = 8080;

	private static final MediaType APPLICATION_PDF = new MediaType("application", "pdf");
	private static final MediaType APPLICATION_XML = new MediaType("application", "xml");
	private static final MediaType APPLICATION_XDP = new MediaType("application", "vnd.adobe.xdp+xml");
	
	@Mock(answer = Answers.RETURNS_SELF) Client client;	// answers used to mock Client's fluent interface. 
	@Mock WebTarget target;
	@Mock Response response;
	@Mock Builder builder;
	@Mock StatusType statusType;
	
	@Captor ArgumentCaptor<String> machineName;
	@Captor ArgumentCaptor<String> path;
	@Captor ArgumentCaptor<Entity> entity;
	@Captor ArgumentCaptor<String> correlationId;

	RestServicesFormsServiceAdapter underTest;
	
	@BeforeEach
	void setUp() throws Exception {
	}

	private enum ExportHappyPath {
		SSL_AND_CORRELATION_ID(true, true), NO_SSL_OR_CORRLATION_ID(false, false);
		
		final boolean useSsl;
		final boolean useCorrelationId;
		
		private ExportHappyPath(boolean useSsl, boolean useCorrelationId) {
			this.useSsl = useSsl;
			this.useCorrelationId = useCorrelationId;
		}
	};
	@ParameterizedTest
	@EnumSource(ExportHappyPath.class)
	void testExportData_HappyPaths(ExportHappyPath codePath) throws Exception {

		Document responseData = MockDocumentFactory.GLOBAL_INSTANCE.create("response Document Data".getBytes());

		this.setupRestClientMocks1(codePath.useCorrelationId, responseData);
		
		com._4point.aem.docservices.rest_services.client.forms.RestServicesFormsServiceAdapter.FormsServiceBuilder adapterBuilder = RestServicesFormsServiceAdapter.builder()
						.machineName(TEST_MACHINE_NAME)
						.port(TEST_MACHINE_PORT)
						.basicAuthentication("username", "password")
						.useSsl(codePath.useSsl)
						.clientFactory(()->client);
		if (codePath.useCorrelationId) {
			adapterBuilder.correlationId(()->CORRELATION_ID);
		}
		
		underTest = adapterBuilder
						.build();
				
		Document pdforxdp = MockDocumentFactory.GLOBAL_INSTANCE.create("pdf Document Data".getBytes());
		DataFormat dataformat = com.adobe.fd.forms.api.DataFormat.XmlData;

		Document pdfResult = underTest.exportData(pdforxdp, dataformat);
		
		// Make sure the correct URL is called.
		this.performCommonValidations("ExportData", codePath.useSsl, codePath.useCorrelationId);

		// Make sure that the arguments we passed in are transmitted correctly.
		@SuppressWarnings("unchecked")
		Entity<FormDataMultiPart> postedEntity = (Entity<FormDataMultiPart>)entity.getValue();
		FormDataMultiPart postedData = postedEntity.getEntity();
		
		assertEquals(MediaType.MULTIPART_FORM_DATA_TYPE, postedEntity.getMediaType());
		validateDocumentFormField(postedData, "pdforxdp", new MediaType("application", "pdf"), pdforxdp.getInlineData());
		
		
		// Make sure the response is correct.
		assertArrayEquals(responseData.getInlineData(), pdfResult.getInlineData());
		assertEquals(APPLICATION_XML, MediaType.valueOf(pdfResult.getContentType()));
	}

	private enum ImportHappyPath {
		SSL_AND_CORRELATION_ID(true, true), NO_SSL_OR_CORRLATION_ID(false, false);
		
		final boolean useSsl;
		final boolean useCorrelationId;
		
		private ImportHappyPath(boolean useSsl, boolean useCorrelationId) {
			this.useSsl = useSsl;
			this.useCorrelationId = useCorrelationId;
		}
	};
	
	@ParameterizedTest
	@EnumSource(ImportHappyPath.class)
	void testImportData_HappyPaths(ImportHappyPath codePath) throws Exception {

		Document responseData = MockDocumentFactory.GLOBAL_INSTANCE.create("response Document Data".getBytes());

		this.setupRestClientMocks(codePath.useCorrelationId, responseData);
		
		com._4point.aem.docservices.rest_services.client.forms.RestServicesFormsServiceAdapter.FormsServiceBuilder adapterBuilder = RestServicesFormsServiceAdapter.builder()
						.machineName(TEST_MACHINE_NAME)
						.port(TEST_MACHINE_PORT)
						.basicAuthentication("username", "password")
						.useSsl(codePath.useSsl)
						.clientFactory(()->client);
		if (codePath.useCorrelationId) {
			adapterBuilder.correlationId(()->CORRELATION_ID);
		}
		
		underTest = adapterBuilder
						.build();
				
		Document pdf = MockDocumentFactory.GLOBAL_INSTANCE.create("pdf Document Data".getBytes());
		Document data = MockDocumentFactory.GLOBAL_INSTANCE.create("data Document Data".getBytes());

		Document pdfResult = underTest.importData(pdf, data);
		
		// Make sure the correct URL is called.
		this.performCommonValidations("ImportData", codePath.useSsl, codePath.useCorrelationId);

		// Make sure that the arguments we passed in are transmitted correctly.
		@SuppressWarnings("unchecked")
		Entity<FormDataMultiPart> postedEntity = (Entity<FormDataMultiPart>)entity.getValue();
		FormDataMultiPart postedData = postedEntity.getEntity();
		
		assertEquals(MediaType.MULTIPART_FORM_DATA_TYPE, postedEntity.getMediaType());
		validateDocumentFormField(postedData, "pdf", new MediaType("application", "pdf"), pdf.getInlineData());
		validateDocumentFormField(postedData, "data", MediaType.APPLICATION_XML_TYPE, data.getInlineData());
		
		// Make sure the response is correct.
		assertArrayEquals(responseData.getInlineData(), pdfResult.getInlineData());
		assertEquals(APPLICATION_PDF, MediaType.valueOf(pdfResult.getContentType()));
	}

	private void performCommonValidations(String expectedUrlLocation, boolean expectSsl, boolean expectCorrelationId) {
		final String expectedPrefix = expectSsl ? "https://" : "http://";
		assertThat("Expected target url contains '" + expectedPrefix + "'", machineName.getValue(), containsString(expectedPrefix));
		assertThat("Expected target url contains TEST_MACHINE_NAME", machineName.getValue(), containsString(TEST_MACHINE_NAME));
		assertThat("Expected target url contains TEST_MACHINE_PORT", machineName.getValue(), containsString(Integer.toString(TEST_MACHINE_PORT)));
		assertThat("Expected target url contains 'ImportData'", path.getValue(), containsString(expectedUrlLocation));
		if (expectCorrelationId) {
			assertEquals(CORRELATION_ID, correlationId.getValue());
		}
	}

	private void setupRestClientMocks(boolean setupCorrelationId, Document responseData) throws IOException {
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
		when(response.getHeaderString(HttpHeaders.CONTENT_TYPE)).thenReturn(APPLICATION_PDF.toString());

		if (setupCorrelationId) {
			when(builder.header(eq(CORRELATION_ID_HTTP_HDR), correlationId.capture())).thenReturn(builder);
		}
	}
	
	private void setupRestClientMocks1(boolean setupCorrelationId, Document responseData) throws IOException {
		// TODO: Change this based on https://maciejwalkowiak.com/mocking-fluent-interfaces/
		when(client.target(machineName.capture())).thenReturn(target);
		when(target.path(path.capture())).thenReturn(target);
		when(target.request()).thenReturn(builder);
		when(builder.accept(APPLICATION_XML)).thenReturn(builder);
		when(builder.post(entity.capture())).thenReturn(response);
		when(response.getStatusInfo()).thenReturn(statusType);
		when(statusType.getFamily()).thenReturn(Response.Status.Family.SUCCESSFUL);	// return Successful
		when(response.hasEntity()).thenReturn(true);
		when(response.getEntity()).thenReturn(new ByteArrayInputStream(responseData.getInlineData()));
		when(response.getHeaderString(HttpHeaders.CONTENT_TYPE)).thenReturn(APPLICATION_PDF.toString());

		if (setupCorrelationId) {
			when(builder.header(eq(CORRELATION_ID_HTTP_HDR), correlationId.capture())).thenReturn(builder);
		}
	}
	private void validateDocumentFormField(FormDataMultiPart postedData, String fieldName, MediaType expectedMediaType, byte[] expectedData) throws IOException {
		List<FormDataBodyPart> fieldValues = postedData.getFields(fieldName);
		assertEquals(1, fieldValues.size());
		
		FormDataBodyPart pdfPart = fieldValues.get(0);
		assertEquals(expectedMediaType, pdfPart.getMediaType());
		byte[] fieldBytes = expectedMediaType.isCompatible(MediaType.TEXT_PLAIN_TYPE) 
				? ((String)pdfPart.getEntity()).getBytes()					// If the field is text, then treat the entity as a String. 
				: IOUtils.toByteArray((InputStream) pdfPart.getEntity());	// Otherwise, treat as InputStream.
		assertArrayEquals(expectedData, fieldBytes);  // TODO: Need to figure out how to test for entity.
	}

	@Test
	void testImportData_SuccessButNoEntity() throws Exception {

		when(client.target(machineName.capture())).thenReturn(target);
		when(target.path(path.capture())).thenReturn(target);
		when(target.request()).thenReturn(builder);
		when(builder.accept(APPLICATION_PDF)).thenReturn(builder);
		when(builder.post(entity.capture())).thenReturn(response);
		when(response.getStatusInfo()).thenReturn(statusType);
		when(statusType.getFamily()).thenReturn(Response.Status.Family.SUCCESSFUL);	// return Successful
		
		underTest = RestServicesFormsServiceAdapter.builder()
				.machineName(TEST_MACHINE_NAME)
				.port(TEST_MACHINE_PORT)
				.basicAuthentication("username", "password")
				.useSsl(false)
				.clientFactory(()->client)
				.build();
		
		
		Document pdf = MockDocumentFactory.GLOBAL_INSTANCE.create("pdf Document Data".getBytes());
		Document data = MockDocumentFactory.GLOBAL_INSTANCE.create("data Document Data".getBytes());

		FormsServiceException ex = assertThrows(FormsServiceException.class, ()->underTest.importData(pdf, data));
		assertThat(ex.getMessage(), containsString("should never happen"));
	}
	
	@Test
	void testImportData_FailureWithHTMLResponse() throws Exception {
		final String SAMPLE_HTML_RESPONSE = "<html><head><title>Content modified /services/DocAssuranceService/SecureDocument</title></head><body><h1>Content modified /services/DocAssuranceService/SecureDocument</h1></body></html>";
		final String HTML_CONTENT_TYPE = "text/html;charset=utf-8";
		
		when(client.target(machineName.capture())).thenReturn(target);
		when(target.path(path.capture())).thenReturn(target);
		when(target.request()).thenReturn(builder);
		when(builder.accept(APPLICATION_PDF)).thenReturn(builder);
		when(builder.post(entity.capture())).thenReturn(response);
		when(response.getStatusInfo()).thenReturn(statusType);
		when(statusType.getFamily()).thenReturn(Response.Status.Family.SUCCESSFUL);	// return Successful
		when(response.hasEntity()).thenReturn(true);
		when(response.getEntity()).thenReturn(new ByteArrayInputStream(SAMPLE_HTML_RESPONSE.getBytes()));
		when(response.getHeaderString(HttpHeaders.CONTENT_TYPE)).thenReturn(HTML_CONTENT_TYPE);
	
		underTest = RestServicesFormsServiceAdapter.builder()
				.machineName(TEST_MACHINE_NAME)
				.port(TEST_MACHINE_PORT)
				.basicAuthentication("username", "password")
				.useSsl(false)
				.clientFactory(()->client)
				.build();
		
		
		Document pdf = MockDocumentFactory.GLOBAL_INSTANCE.create("pdf Document Data".getBytes());
		Document data = MockDocumentFactory.GLOBAL_INSTANCE.create("data Document Data".getBytes());

		FormsServiceException ex = assertThrows(FormsServiceException.class, ()->underTest.importData(pdf, data));
		assertThat(ex.getMessage(), containsString("was not a PDF"));
		assertThat(ex.getMessage(), containsString(HTML_CONTENT_TYPE));
	}
	
	// TODO:  Add more importData tests for exceptional case (i.e. those cases where exceptions are thrown.
	
	private enum RenderFormsHappyPath { 
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
		
		private RenderFormsHappyPath(boolean ssl, boolean hasData, boolean templateString, boolean emptyOptions) {
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
	@EnumSource()
	void testRenderPDFForm_HappyPaths(RenderFormsHappyPath codePath) throws Exception {

		Document responseData = MockDocumentFactory.GLOBAL_INSTANCE.create("response Document Data".getBytes());

		this.setupRestClientMocks(codePath.isSsl(), responseData);
		
		com._4point.aem.docservices.rest_services.client.forms.RestServicesFormsServiceAdapter.FormsServiceBuilder adapterBuilder = RestServicesFormsServiceAdapter.builder()
						.machineName(TEST_MACHINE_NAME)
						.port(TEST_MACHINE_PORT)
						.basicAuthentication("username", "password")
						.useSsl(codePath.isSsl())
						.clientFactory(()->client);
		if (codePath.isSsl()) {
			adapterBuilder.correlationId(()->CORRELATION_ID);
		}
		
		underTest = adapterBuilder
						.build();
				
		String templateFilename = "pdf FileName";
		Document templateDoc = MockDocumentFactory.GLOBAL_DUMMY_DOCUMENT;
		Document data = MockDocumentFactory.GLOBAL_INSTANCE.create("data Document Data".getBytes());

		Document pdfResult;
		if (codePath.isTemplateString()) {
			pdfResult = underTest.renderPDFForm(templateFilename, codePath.hasData() ? data : null, codePath.hasemptyOptions() ? new PDFFormRenderOptionsImpl() : getNonDefaultPdfRenderOptions());
		} else {
			pdfResult = underTest.renderPDFForm(templateDoc, codePath.hasData() ? data : null, codePath.hasemptyOptions() ? new PDFFormRenderOptionsImpl() : getNonDefaultPdfRenderOptions());
		}
		
		// Make sure the correct URL is called.
		this.performCommonValidations("RenderPdfForm", codePath.isSsl(), codePath.isSsl());

		// Make sure that the arguments we passed in are transmitted correctly.
		@SuppressWarnings("unchecked")
		Entity<FormDataMultiPart> postedEntity = (Entity<FormDataMultiPart>)entity.getValue();
		FormDataMultiPart postedData = postedEntity.getEntity();
		
		assertEquals(MediaType.MULTIPART_FORM_DATA_TYPE, postedEntity.getMediaType());
		if (codePath.isTemplateString()) {
			validateDocumentFormField(postedData, "template", MediaType.TEXT_PLAIN_TYPE,templateFilename.getBytes());
		} else {
			validateDocumentFormField(postedData, "template", APPLICATION_XDP, MockDocumentFactory.GLOBAL_DUMMY_DOCUMENT.getInlineData());
		}
		if (codePath.hasData()) {
			validateDocumentFormField(postedData, "data", MediaType.APPLICATION_XML_TYPE, data.getInlineData());
		} else {
			assertNull(postedData.getFields("data"));
		}
		if (codePath.hasemptyOptions()) {
			assertNull(postedData.getFields("renderOptions.acrobatVersion"));
			assertNull(postedData.getFields("renderOptions.cacheStrategy"));
			assertNull(postedData.getFields("renderOptions.contentRoot"));
			assertNull(postedData.getFields("renderOptions.debugDir"));
			assertNull(postedData.getFields("renderOptions.embedFonts"));
			assertNull(postedData.getFields("renderOptions.locale"));
			assertNull(postedData.getFields("renderOptions.renderAtClient"));
			assertNull(postedData.getFields("renderOptions.submitUrl"));
			assertNull(postedData.getFields("renderOptions.taggedPdf"));
		} else {
			validateDocumentFormField(postedData, "renderOptions.acrobatVersion", MediaType.TEXT_PLAIN_TYPE, NON_DEFAULT_ACROBAT_VERSION.toString().getBytes());
			validateDocumentFormField(postedData, "renderOptions.cacheStrategy", MediaType.TEXT_PLAIN_TYPE, NON_DEFAULT_CACHE_STRATEGY.toString().getBytes());
			validateDocumentFormField(postedData, "renderOptions.contentRoot", MediaType.TEXT_PLAIN_TYPE, NON_DEFAULT_CONTENT_ROOT.toString().getBytes());
			validateDocumentFormField(postedData, "renderOptions.debugDir", MediaType.TEXT_PLAIN_TYPE, NON_DEFAULT_DEBUG_DIR.toString().getBytes());
			validateDocumentFormField(postedData, "renderOptions.embedFonts", MediaType.TEXT_PLAIN_TYPE, Boolean.valueOf(NON_DEFAULT_EMBED_FONTS).toString().getBytes());
			validateDocumentFormField(postedData, "renderOptions.locale", MediaType.TEXT_PLAIN_TYPE, NON_DEFAULT_LOCALE.toString().getBytes());
			validateDocumentFormField(postedData, "renderOptions.renderAtClient", MediaType.TEXT_PLAIN_TYPE, NON_DEFAULT_RENDER_AT_CLIENT.toString().getBytes());
			// The submitUrl parameter is currently commented out in the class under test, so it is commented out here as well.
//			validateDocumentFormField(postedData, "renderOptions.submitUrl", MediaType.TEXT_PLAIN_TYPE, NON_DEFAULT_SUBMIT_URL.getBytes());
			validateDocumentFormField(postedData, "renderOptions.taggedPdf", MediaType.TEXT_PLAIN_TYPE, Boolean.valueOf(NON_DEFAULT_TAGGED_PDF).toString().getBytes());
		}

		// Make sure the response is correct.
		assertArrayEquals(responseData.getInlineData(), pdfResult.getInlineData());
		assertEquals(APPLICATION_PDF, MediaType.valueOf(pdfResult.getContentType()));
	}


	private static final AcrobatVersion NON_DEFAULT_ACROBAT_VERSION = AcrobatVersion.Acrobat_10_1;
	private static final CacheStrategy NON_DEFAULT_CACHE_STRATEGY = CacheStrategy.NONE;
	private static final Path NON_DEFAULT_CONTENT_ROOT = Paths.get("foo", "bar");
	private static final Path NON_DEFAULT_DEBUG_DIR = Paths.get("bar", "foo");
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

	@Test
	void testExportData_SuccessButNoEntity() throws Exception {

		when(client.target(machineName.capture())).thenReturn(target);
		when(target.path(path.capture())).thenReturn(target);
		when(target.request()).thenReturn(builder);
		when(builder.accept(APPLICATION_XML)).thenReturn(builder);
		when(builder.post(entity.capture())).thenReturn(response);
		when(response.getStatusInfo()).thenReturn(statusType);
		when(statusType.getFamily()).thenReturn(Response.Status.Family.SUCCESSFUL);	// return Successful
		
		underTest = RestServicesFormsServiceAdapter.builder()
				.machineName(TEST_MACHINE_NAME)
				.port(TEST_MACHINE_PORT)
				.basicAuthentication("username", "password")
				.useSsl(false)
				.clientFactory(()->client)
				.build();
		
		
		Document pdforxdp = MockDocumentFactory.GLOBAL_INSTANCE.create("pdf Document Data".getBytes());
		DataFormat  dataformat = com.adobe.fd.forms.api.DataFormat.XmlData;

		FormsServiceException ex = assertThrows(FormsServiceException.class, ()->underTest.exportData(pdforxdp, dataformat));
		assertThat(ex.getMessage(), containsString("should never happen"));
	}
	
}
