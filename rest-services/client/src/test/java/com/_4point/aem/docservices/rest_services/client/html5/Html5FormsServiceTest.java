package com._4point.aem.docservices.rest_services.client.html5;

import static com._4point.testing.matchers.javalang.ExceptionMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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
import com._4point.aem.docservices.rest_services.client.helpers.ReplacingInputStream;
import com._4point.aem.docservices.rest_services.client.html5.Html5FormsService.Html5FormsServiceBuilder;
import com._4point.aem.docservices.rest_services.client.html5.Html5FormsService.Html5FormsServiceException;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;

@ExtendWith(MockitoExtension.class)
class Html5FormsServiceTest {

	private final static String DUMMY_TEMPLATE_STR = "TemplateString";
	private final static String DUMMY_TEMPLATE_URL = "http://TemplateString/";
	private final static Document DUMMY_DATA = MockDocumentFactory.GLOBAL_INSTANCE.create("Dummy Data".getBytes());

	private static final String CORRELATION_ID = "correlationId";
	private static final String TEST_MACHINE_NAME = "testmachinename";
	private static final int TEST_MACHINE_PORT = 8080;
	private static final String CRX_CONTENT_ROOT = "crx:/content/dam/formsanddocuments/sample-forms";

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

	@BeforeEach
	void setUp() throws Exception {
		when(mockClientFactory.apply(aemConfig.capture(), servicePath.capture(), correlationIdFn.capture())).thenReturn(mockClient);
	}

	@FunctionalInterface
	public interface BiFunction_WithExceptions<T, U, R, E extends Exception> {
		R apply(T t, U u) throws E;
	}

	private enum HappyPath {

		SSL_NODATA_STR(true, false, TemplateType.STRING),
		SSL_DATA_STR(true, true, TemplateType.STRING),
		NOSSL_NODATA_URL(false, false, TemplateType.URL),
		NOSSL_DATA_URL(false, true, TemplateType.URL),
		SSL_NODATA_PATH(true, false, TemplateType.PATH),
		SSL_DATA_PATH(true, true, TemplateType.PATH),
		NOSSL_NODATA_PATHORURL(false, false, TemplateType.PATH_OR_URL),
		NOSSL_DATA_PATHORURL(false, true, TemplateType.PATH_OR_URL);
		
		private final boolean ssl;
		private final boolean hasData;
		private final TemplateType templateType;
		
		private HappyPath(boolean ssl, boolean hasData, TemplateType templateType) {
			this.ssl = ssl;
			this.hasData = hasData;
			this.templateType = templateType;
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
		
		public Document callRenderHtml5Form(Boolean hasData, Html5FormsService underTest) throws Html5FormsServiceException {
			return templateType.render(hasData, underTest);
		}

		private enum TemplateType {
			STRING(TemplateType::renderHtml5FormWithString), 
			PATH(TemplateType::renderHtml5FormWithPath),
			URL(TemplateType::renderHtml5FormWithUrl),
			PATH_OR_URL(TemplateType::renderHtml5FormWithPathOrUrl);
			
			private final BiFunction_WithExceptions<Boolean, Html5FormsService, Document, Html5FormsServiceException> renderHtml5FormFn;

			private TemplateType(BiFunction_WithExceptions<Boolean, Html5FormsService, Document, Html5FormsServiceException> renderHtml5FormFn) {
				this.renderHtml5FormFn = renderHtml5FormFn;
			}

			public Document render(Boolean hasData, Html5FormsService underTest) throws Html5FormsServiceException {
				return renderHtml5FormFn.apply(hasData, underTest);
			}

			private static Document renderHtml5FormWithString(Boolean hasData, Html5FormsService underTest) throws Html5FormsServiceException {
				return hasData.booleanValue() ? underTest.renderHtml5Form(DUMMY_TEMPLATE_STR, DUMMY_DATA) 
											  : underTest.renderHtml5Form(DUMMY_TEMPLATE_STR);
			}
			private static Document renderHtml5FormWithPath(Boolean hasData, Html5FormsService underTest) throws Html5FormsServiceException {
				
				return hasData.booleanValue() ? underTest.renderHtml5Form(Paths.get(DUMMY_TEMPLATE_STR), DUMMY_DATA) 
											  : underTest.renderHtml5Form(Paths.get(DUMMY_TEMPLATE_STR));
			}
			private static Document renderHtml5FormWithUrl(Boolean hasData, Html5FormsService underTest) throws Html5FormsServiceException {
				try {
					return hasData.booleanValue() ? underTest.renderHtml5Form(new URL(DUMMY_TEMPLATE_URL), DUMMY_DATA) 
												  : underTest.renderHtml5Form(new URL(DUMMY_TEMPLATE_URL));
				} catch (MalformedURLException e) {
					throw new IllegalStateException("Error while turning DUMMY_TEMPLATE_STR into URL.", e);
				}
			}
			private static Document renderHtml5FormWithPathOrUrl(Boolean hasData, Html5FormsService underTest) throws Html5FormsServiceException {
				return hasData.booleanValue() ? underTest.renderHtml5Form(PathOrUrl.from(DUMMY_TEMPLATE_STR), DUMMY_DATA) 
								 			  : underTest.renderHtml5Form(PathOrUrl.from(DUMMY_TEMPLATE_STR));
			}

		}
	};
	


	@ParameterizedTest
	@EnumSource
	void testRenderHtml5Form(HappyPath scenario) throws Exception {
		
		byte[] expectedResponseData = "response Document Data".getBytes();
		setupMocks(setupMockResponse(expectedResponseData, ContentType.TEXT_HTML));

		if (scenario.hasData()) {
			when(mockPayloadBuilder.addIfNotNull(eq("data"), Mockito.any(InputStream.class), eq(ContentType.APPLICATION_XML))).thenReturn(mockPayloadBuilder);
		} else {
			when(mockPayloadBuilder.addIfNotNull(eq("data"), Mockito.<InputStream>isNull(), eq(ContentType.APPLICATION_XML))).thenReturn(mockPayloadBuilder);
		}

		if (scenario.templateType == HappyPath.TemplateType.URL) {
			when(mockPayloadBuilder.addStringVersion(eq("template"), eq(PathOrUrl.from(DUMMY_TEMPLATE_URL)))).thenReturn(mockPayloadBuilder);			
		} else {
			when(mockPayloadBuilder.addStringVersion(eq("template"), eq(PathOrUrl.from(DUMMY_TEMPLATE_STR)))).thenReturn(mockPayloadBuilder);
		}
		
		Html5FormsService underTest = createAdapter(scenario);
		
		Document result = scenario.callRenderHtml5Form(scenario.hasData(), underTest);
				
		// Make sure the correct URL is called.
		assertThat("Expected target url contains 'Html5' and 'RenderHtml5Form'", servicePath.getValue(), allOf(containsString("Html5"), containsString("RenderHtml5Form")));

		if (scenario.useCorrelationId()) {
			assertEquals(CORRELATION_ID, correlationIdFn.getValue().get());
		}
		
		// Make sure the response is correct.
		assertArrayEquals(expectedResponseData, result.getInputStream().readAllBytes());
		assertEquals(ContentType.TEXT_HTML.contentType(), result.getContentType());

		// Make sure we sent the correct contentTyoe
		assertEquals(ContentType.TEXT_HTML, acceptableContentType.getValue());
	}

	private Html5FormsService createAdapter(HappyPath scenario) {
		Html5FormsServiceBuilder svcBuilder = Html5FormsService.builder(mockClientFactory)
															   .machineName(TEST_MACHINE_NAME)
															   .port(TEST_MACHINE_PORT)
															   .basicAuthentication("username", "password")
															   .useSsl(scenario.useSsl());
		
		if (scenario.useCorrelationId()) {
			svcBuilder.correlationId(()->CORRELATION_ID);
		}

		return svcBuilder.build();
	}


	@Test
	void testRenderHtml5FormWithFilter() throws Exception {
		setupMocks(setupMockResponse("response Document Data".getBytes(), ContentType.TEXT_HTML));
		when(mockPayloadBuilder.addStringVersion(eq("template"), eq(PathOrUrl.from(DUMMY_TEMPLATE_STR)))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addIfNotNull(eq("data"), Mockito.any(InputStream.class), eq(ContentType.APPLICATION_XML))).thenReturn(mockPayloadBuilder);
		
		Html5FormsService underTest = Html5FormsService.builder(mockClientFactory)
													   .machineName(TEST_MACHINE_NAME)
													   .port(TEST_MACHINE_PORT)
													   .basicAuthentication("username", "password")
													   .useSsl(false)
													   .aemServerType(AemServerType.StandardType.OSGI)
													   .addRenderResultFilter(is->new ReplacingInputStream(is, "Document", "tnemucoD"))
													   .build();
	
		Document result = underTest.renderHtml5Form(DUMMY_TEMPLATE_STR, DUMMY_DATA);
		
		assertEquals("response tnemucoD Data", new String(result.getInlineData()));
		assertEquals(ContentType.TEXT_HTML.contentType(), result.getContentType());
	}
	
	@Test
	void testRenderHtml5FormRestClientException() throws Exception {
		RestClientException expectedException = new RestClientException("Dummy Exception Message");
		when(mockPayloadBuilder.addStringVersion(eq("template"), eq(PathOrUrl.from(DUMMY_TEMPLATE_STR)))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addIfNotNull(eq("data"), Mockito.<InputStream>isNull(), eq(ContentType.APPLICATION_XML))).thenReturn(mockPayloadBuilder);

		when(mockClient.multipartPayloadBuilder()).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.build()).thenReturn(mockPayload);
		when(mockPayload.postToServer(acceptableContentType.capture())).thenThrow(expectedException);
		
		Html5FormsServiceBuilder svcBuilder = Html5FormsService.builder(mockClientFactory)
				   .machineName(TEST_MACHINE_NAME)
				   .port(TEST_MACHINE_PORT)
				   .basicAuthentication("username", "password");

		Html5FormsService underTest = svcBuilder.build();

		Html5FormsServiceException ex = assertThrows(Html5FormsServiceException.class, ()->underTest.renderHtml5Form(DUMMY_TEMPLATE_STR));
		assertThat(ex, allOf(exceptionMsgContainsAll("Error while POSTing to AEM server"), hasCause(expectedException)));
	}

	@Test
	void testRenderHtml5FormBadResponseNoEntity() throws Exception {

		setupMocks(Optional.empty());
		when(mockPayloadBuilder.addStringVersion(eq("template"), eq(PathOrUrl.from(DUMMY_TEMPLATE_STR)))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.addIfNotNull(eq("data"), Mockito.<InputStream>isNull(), eq(ContentType.APPLICATION_XML))).thenReturn(mockPayloadBuilder);
		
		Html5FormsServiceBuilder svcBuilder = Html5FormsService.builder(mockClientFactory)
				   .machineName(TEST_MACHINE_NAME)
				   .port(TEST_MACHINE_PORT)
				   .basicAuthentication("username", "password");

		Html5FormsService underTest = svcBuilder.build();

		Html5FormsServiceException ex = assertThrows(Html5FormsServiceException.class, ()->underTest.renderHtml5Form(DUMMY_TEMPLATE_STR));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, containsStringIgnoringCase("Error - empty response from AEM server."));
	}

	private void setupMocks(Optional<Response> mockedResponse) throws RestClientException {
		when(mockClient.multipartPayloadBuilder()).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.build()).thenReturn(mockPayload);
		when(mockPayload.postToServer(acceptableContentType.capture())).thenReturn(mockedResponse);
	}
	
	private Optional<Response> setupMockResponse(byte[] responseData, ContentType expectedContentType) throws Exception {
		when(mockResponse.contentType()).thenReturn(expectedContentType);
		when(mockResponse.data()).thenReturn(new ByteArrayInputStream(responseData));
		return Optional.of(mockResponse);
	}

	
	@Nested
	class NullArgumentTests {

		Html5FormsService underTest;

		@BeforeEach
		void setUp() throws Exception {
			underTest = Html5FormsService.builder(mockClientFactory).build();
		}

		@Test 
		void testRenderHtml5Form_NullTemplateArgumentPathOrUrl() throws Exception { 
			NullPointerException ex = assertThrows(NullPointerException.class, ()->underTest.renderHtml5Form((PathOrUrl)null));
			String msg = ex.getMessage();
			assertNotNull(msg);
			assertThat(ex.getMessage(), containsStringIgnoringCase("template"));
			assertThat(ex.getMessage(), containsStringIgnoringCase("cannot be null"));
		}

		@Test 
		void testRenderHtml5Form_NullTemplateArgumentWIthData() throws Exception { 
			NullPointerException ex = assertThrows(NullPointerException.class, ()->underTest.renderHtml5Form((PathOrUrl)null, SimpleDocumentFactoryImpl.emptyDocument()));
			String msg = ex.getMessage();
			assertNotNull(msg);
			assertThat(ex.getMessage(), containsStringIgnoringCase("template"));
			assertThat(ex.getMessage(), containsStringIgnoringCase("cannot be null"));
		}

		@Test 
		void testRenderHtml5Form_NullDataArgumentOnly() throws Exception { 
			NullPointerException ex = assertThrows(NullPointerException.class, ()->underTest.renderHtml5Form(PathOrUrl.from(DUMMY_TEMPLATE_STR), null));
			String msg = ex.getMessage();
			assertNotNull(msg);
			assertThat(ex.getMessage(), containsStringIgnoringCase("data"));
			assertThat(ex.getMessage(), containsStringIgnoringCase("cannot be null"));
		}

		@Test 
		void testRenderHtml5Form_NullTemplatePath() throws Exception { 
			NullPointerException ex = assertThrows(NullPointerException.class, ()->underTest.renderHtml5Form((Path)null));
			String msg = ex.getMessage();
			assertNotNull(msg);
			// Caught by PathOrUrl code.  We don't need to check the contents of the message.
		}

		@Test 
		void testRenderHtml5Form_NullTemplateUrl() throws Exception { 
			NullPointerException ex = assertThrows(NullPointerException.class, ()->underTest.renderHtml5Form((URL)null));
			String msg = ex.getMessage();
			assertNotNull(msg);
			// Caught by PathOrUrl code.  We don't need to check the contents of the message.
		}

		@Test 
		void testRenderHtml5Form_NullTemplateString() throws Exception { 
			NullPointerException ex = assertThrows(NullPointerException.class, ()->underTest.renderHtml5Form((String)null));
			String msg = ex.getMessage();
			assertNotNull(msg);
			// Caught by PathOrUrl code.  We don't need to check the contents of the message.
		}

	}

//	private void validateTextFormField(FormDataMultiPart postedData, String fieldName, String expectedData) throws IOException {
//		List<FormDataBodyPart> pdfFields = postedData.getFields(fieldName);
//		assertEquals(1, pdfFields.size());
//
//		FormDataBodyPart pdfPart = pdfFields.get(0);
//		assertEquals(MediaType.TEXT_PLAIN_TYPE, pdfPart.getMediaType());
//		String value = (String) pdfPart.getEntity();
//		assertEquals(expectedData, value);
//	}
//	
//	private void validateDocumentFormField(FormDataMultiPart postedData, String fieldName, MediaType expectedMediaType, byte[] expectedData) throws IOException {
//		List<FormDataBodyPart> pdfFields = postedData.getFields(fieldName);
//		assertEquals(1, pdfFields.size());
//		
//		FormDataBodyPart pdfPart = pdfFields.get(0);
//		assertEquals(expectedMediaType, pdfPart.getMediaType());
//		byte[] pdfBytes = IOUtils.toByteArray((InputStream) pdfPart.getEntity());
//		assertArrayEquals(expectedData, pdfBytes);  // TODO: Need to figure out how to test for entity.
//	}

}
