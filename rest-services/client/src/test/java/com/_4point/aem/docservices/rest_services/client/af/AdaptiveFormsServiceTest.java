package com._4point.aem.docservices.rest_services.client.af;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com._4point.aem.docservices.rest_services.client.RestClient;
import com._4point.aem.docservices.rest_services.client.RestClient.ContentType;
import com._4point.aem.docservices.rest_services.client.RestClient.GetRequest;
import com._4point.aem.docservices.rest_services.client.RestClient.MultipartPayload;
import com._4point.aem.docservices.rest_services.client.RestClient.Response;
import com._4point.aem.docservices.rest_services.client.RestClient.RestClientException;
import com._4point.aem.docservices.rest_services.client.af.AdaptiveFormsService.AdaptiveFormsServiceException;
import com._4point.aem.docservices.rest_services.client.helpers.AemConfig;
import com._4point.aem.docservices.rest_services.client.helpers.AemServerType;
import com._4point.aem.docservices.rest_services.client.helpers.Builder.RestClientFactory;
import com._4point.aem.docservices.rest_services.client.helpers.ReplacingInputStream;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;
import com._4point.testing.matchers.javalang.ExceptionMatchers;

@ExtendWith(MockitoExtension.class)
class AdaptiveFormsServiceTest {
	private static final Document EMPTY_DOCUMENT = SimpleDocumentFactoryImpl.emptyDocument();
	private final static String DUMMY_TEMPLATE_STR = "TemplateString";
	private final static byte[] DUMMY_DATA = "Dummy Data".getBytes();
	private final static String DUMMY_GUID = "{dummy-guid}";
	private final static Document DUMMY_DATA_DOC = createDataDocument(DUMMY_DATA);
	
	private static final String TEST_MACHINE_NAME = "testmachinename";
	private static final int TEST_MACHINE_PORT = 8080;

	@Mock(stubOnly = true) RestClientFactory mockClientFactory;
	@Mock(stubOnly = true) RestClient mockClient;
	@Mock(stubOnly = true) MultipartPayload mockPayload;
	@Mock(stubOnly = true) MultipartPayload.Builder mockPayloadBuilder;
	@Mock(stubOnly = true) GetRequest mockGetRequest;
	@Mock(stubOnly = true) GetRequest.Builder mockGetRequestBuilder;
	@Mock(stubOnly = true) Response mockPostResponse;
	@Mock(stubOnly = true) Response mockGetResponse;

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

	@FunctionalInterface
	public interface Function_WithExceptions<T, R, E extends Exception> {
		R apply(T t) throws E;
	}

	// A couple of functions for creating some dummy data Document objects.
	private static Document createDataDocument(byte[] data) {
		return MockDocumentFactory.GLOBAL_INSTANCE.create(data);
	}
	private static Document createDataDocument(byte[] data, ContentType contentType) {
		try {
			Document dataDoc = createDataDocument(data);
			dataDoc.setContentTypeIfEmpty(contentType.contentType());
			return dataDoc;
		} catch (IOException e) {
			throw new IllegalStateException("IO Error occurred whild create data Document.", e);
		}
	}

	private enum HappyPath {

		NODATA_STR(null, TemplateType.STRING),
		DATA_XML_STR(ContentType.APPLICATION_XML, TemplateType.STRING),
		DATA_JSON_STR(ContentType.APPLICATION_JSON, TemplateType.STRING),
		NODATA_PATH(null, TemplateType.PATH),
		DATA_XML_PATH(ContentType.APPLICATION_XML, TemplateType.PATH),
		DATA_JSON_PATH(ContentType.APPLICATION_JSON, TemplateType.PATH),
		;
		
		private final ContentType contentType;				// Data used for this scenario
		private final TemplateType templateType;	// Type of template parameter
		
		private HappyPath(ContentType contentType, TemplateType templateType) {
			this.contentType = contentType;
			this.templateType = templateType;
		}

		public boolean hasData() {
			return this.contentType != null;
		}
		
		public Document callRenderAdaptiveForm(AdaptiveFormsService underTest) throws AdaptiveFormsServiceException {
			return hasData() ? templateType.render(createDataDocument(DUMMY_DATA, contentType), underTest) : templateType.render(underTest);
		}

		private enum TemplateType {
			STRING(
					(data, underTest)->underTest.renderAdaptiveForm(DUMMY_TEMPLATE_STR, data), 
					(underTest)->underTest.renderAdaptiveForm(DUMMY_TEMPLATE_STR)
				  ), 
			PATH(
					(data, underTest)->underTest.renderAdaptiveForm(Paths.get(DUMMY_TEMPLATE_STR), data), 
					(underTest)->underTest.renderAdaptiveForm(Paths.get(DUMMY_TEMPLATE_STR))
				),
			PATH_OR_URL(
					(data, underTest)->underTest.renderAdaptiveForm(PathOrUrl.from(DUMMY_TEMPLATE_STR), data), 
					(underTest)->underTest.renderAdaptiveForm(PathOrUrl.from(DUMMY_TEMPLATE_STR))
					);
			
			private final BiFunction_WithExceptions<Document, AdaptiveFormsService, Document, AdaptiveFormsServiceException> renderAdaptiveFormWithDataFn;
			private final Function_WithExceptions<AdaptiveFormsService, Document, AdaptiveFormsServiceException> renderAdaptiveFormWithoutDataFn;

			private TemplateType(BiFunction_WithExceptions<Document, AdaptiveFormsService, Document, AdaptiveFormsServiceException> renderAdaptiveFormWithDataFn,
					Function_WithExceptions<AdaptiveFormsService, Document, AdaptiveFormsServiceException> renderAdaptiveFormWithoutDataFn) {
				this.renderAdaptiveFormWithDataFn = renderAdaptiveFormWithDataFn;
				this.renderAdaptiveFormWithoutDataFn = renderAdaptiveFormWithoutDataFn;
			}

			public Document render(Document data, AdaptiveFormsService underTest) throws AdaptiveFormsServiceException {
				return renderAdaptiveFormWithDataFn.apply(data, underTest);
			}

			public Document render(AdaptiveFormsService underTest) throws AdaptiveFormsServiceException {
				return renderAdaptiveFormWithoutDataFn.apply(underTest);
			}
		}
	};
	


	@ParameterizedTest
	@EnumSource
	void testRenderAdaptiveForm(HappyPath scenario) throws Exception {
		byte[] responseData = "Adaptive Form response Document Data".getBytes();

		setupAfServiceMocks("/" + DUMMY_TEMPLATE_STR + ".html", setupMockGetResponse(responseData, ContentType.TEXT_HTML));
		when(mockGetRequestBuilder.queryParam("wcmmode", "disabled")).thenReturn(mockGetRequestBuilder);
		
		if (scenario.hasData()) {
			setupDataServiceMocks(setupMockPostResponse(DUMMY_GUID.getBytes(), ContentType.TEXT_PLAIN));
			when(mockPayloadBuilder.add(eq("Data"), Mockito.any(Document.class), eq(scenario.contentType))).thenReturn(mockPayloadBuilder);
			when(mockGetRequestBuilder.queryParam(eq("dataRef"), eq("service://FFPrefillService/" + DUMMY_GUID))).thenReturn(mockGetRequestBuilder);
		}
		
		AdaptiveFormsService underTest = createAdapter(mockClientFactory);
		
		Document result = scenario.callRenderAdaptiveForm(underTest);
		
		// Make sure the response is correct.
		assertArrayEquals(responseData, result.getInputStream().readAllBytes());
		assertEquals(ContentType.TEXT_HTML.contentType(), result.getContentType());
	}

	@Test
	void testRenderAdaptiveFormWithFilter() throws Exception {
		byte[] responseData = "response Document Data".getBytes();

		setupFilterMocks(responseData);
		
		AdaptiveFormsService underTest = AdaptiveFormsService.builder(mockClientFactory)
													   .machineName(TEST_MACHINE_NAME)
													   .port(TEST_MACHINE_PORT)
													   .basicAuthentication("username", "password")
													   .useSsl(false)
													   .addRenderResultFilter(is->new ReplacingInputStream(is, "Document", "tnemucoD"))
													   .build();
	
		Document result = underTest.renderAdaptiveForm(DUMMY_TEMPLATE_STR, DUMMY_DATA_DOC);
		
		assertEquals("response tnemucoD Data", new String(result.getInlineData()));
		assertEquals(ContentType.TEXT_HTML.contentType(), result.getContentType());
	}

	private void setupFilterMocks(byte[] responseData) throws RestClientException {
		setupAfServiceMocks("/" + DUMMY_TEMPLATE_STR + ".html", setupMockGetResponse(responseData, ContentType.TEXT_HTML));
		when(mockGetRequestBuilder.queryParam("wcmmode", "disabled")).thenReturn(mockGetRequestBuilder);
		
		setupDataServiceMocks(setupMockPostResponse(DUMMY_GUID.getBytes(), ContentType.TEXT_PLAIN));
		when(mockPayloadBuilder.add(eq("Data"), Mockito.any(Document.class), Mockito.any(ContentType.class))).thenReturn(mockPayloadBuilder);
		when(mockGetRequestBuilder.queryParam(eq("dataRef"), eq("service://FFPrefillService/" + DUMMY_GUID))).thenReturn(mockGetRequestBuilder);
	}
	
	@Test
	void testRenderAdaptiveFormWithTwoFilters() throws Exception {
		byte[] responseData = "response Document Data".getBytes();

		setupFilterMocks(responseData);
				
		AdaptiveFormsService underTest = AdaptiveFormsService.builder(mockClientFactory)
													   .machineName(TEST_MACHINE_NAME)
													   .port(TEST_MACHINE_PORT)
													   .basicAuthentication("username", "password")
													   .useSsl(false)
													   .addRenderResultFilter(is->new ReplacingInputStream(is, "Document", "tnemucoD"))
													   .addRenderResultFilter(is->new ReplacingInputStream(is, "coD", "rtm"))
													   .build();
	
		Document result = underTest.renderAdaptiveForm(DUMMY_TEMPLATE_STR, DUMMY_DATA_DOC);
		
		assertEquals("response tnemurtm Data", new String(result.getInlineData()));
		assertEquals(ContentType.TEXT_HTML.contentType(), result.getContentType());
	}	
	
	private static AdaptiveFormsService createAdapter(RestClientFactory clientFactory) {
		return AdaptiveFormsService.builder(clientFactory)
											  .machineName(TEST_MACHINE_NAME)
											  .port(TEST_MACHINE_PORT)
											  .basicAuthentication("username", "password")
											  .useSsl(true)
											  .aemServerType(AemServerType.StandardType.JEE)
											  .build();
	}

	private void setupDataServiceMocks(Optional<Response> mockedResponse) throws RestClientException {
		when(mockClient.multipartPayloadBuilder()).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.build()).thenReturn(mockPayload);
		when(mockPayload.postToServer(acceptableContentType.capture())).thenReturn(mockedResponse);
	}

	private void setupAfServiceMocks(String form, Optional<Response> mockedResponse) throws RestClientException {
		when(mockClient.getRequestBuilder(eq(form))).thenReturn(mockGetRequestBuilder);
		when(mockGetRequestBuilder.build()).thenReturn(mockGetRequest);
		when(mockGetRequest.getFromServer(acceptableContentType.capture())).thenReturn(mockedResponse);
	}

	private Optional<Response> setupMockPostResponse(byte[] responseData, ContentType expectedContentType) {
//		when(mockPostResponse.contentType()).thenReturn(expectedContentType);	// ContentType is not queries.
		when(mockPostResponse.data()).thenReturn(new ByteArrayInputStream(responseData));
		return Optional.of(mockPostResponse);
	}

	private Optional<Response> setupMockGetResponse(byte[] responseData, ContentType expectedContentType) {
		when(mockGetResponse.contentType()).thenReturn(expectedContentType);
		when(mockGetResponse.data()).thenReturn(new ByteArrayInputStream(responseData));
		return Optional.of(mockGetResponse);
	}

	@Test
	void testRenderAdaptiveForm_GetRestException() throws Exception {
		var cause = new RestClientException("cause exception");

		AdaptiveFormsService underTest = AdaptiveFormsService.builder(mockClientFactory)
															 .build();

		when(mockClient.getRequestBuilder(anyString())).thenReturn(mockGetRequestBuilder);
		when(mockGetRequestBuilder.queryParam("wcmmode", "disabled")).thenReturn(mockGetRequestBuilder);
		when(mockGetRequestBuilder.build()).thenReturn(mockGetRequest);
		when(mockGetRequest.getFromServer(any())).thenThrow(cause);
		
		var ex = assertThrows(AdaptiveFormsServiceException.class, ()->underTest.renderAdaptiveForm(DUMMY_TEMPLATE_STR));
		
		assertThat(ex, allOf(ExceptionMatchers.exceptionMsgContainsAll("Error while performing GET to server"),
							 ExceptionMatchers.hasCause(cause)
							));
	}

	@Test
	void testRenderAdaptiveForm_PostRestException() throws Exception {
		var cause = new RestClientException("cause exception");
		AdaptiveFormsService underTest = AdaptiveFormsService.builder(mockClientFactory)
															 .build();
		
		when(mockClient.getRequestBuilder(anyString())).thenReturn(mockGetRequestBuilder);
		when(mockGetRequestBuilder.queryParam("wcmmode", "disabled")).thenReturn(mockGetRequestBuilder);
		when(mockClient.multipartPayloadBuilder()).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.add(eq("Data"), Mockito.any(Document.class), Mockito.any(ContentType.class))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.build()).thenReturn(mockPayload);
		when(mockPayload.postToServer(any())).thenThrow(cause);
		
		
		var ex = assertThrows(AdaptiveFormsServiceException.class, ()->underTest.renderAdaptiveForm(DUMMY_TEMPLATE_STR, createDataDocument(DUMMY_DATA)));
		
		assertThat(ex, allOf(ExceptionMatchers.exceptionMsgContainsAll("Error while POSTing to server"),
							 ExceptionMatchers.hasCause(cause)
							));
	}

	@Test
	void testRenderAdaptiveForm_PostIOException() throws Exception {
		var cause = new IOException("cause exception");
		AdaptiveFormsService underTest = AdaptiveFormsService.builder(mockClientFactory)
															 .build();
		
		when(mockClient.getRequestBuilder(anyString())).thenReturn(mockGetRequestBuilder);
		when(mockGetRequestBuilder.queryParam("wcmmode", "disabled")).thenReturn(mockGetRequestBuilder);
		when(mockClient.multipartPayloadBuilder()).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.add(eq("Data"), Mockito.any(Document.class), Mockito.any(ContentType.class))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.build()).thenReturn(mockPayload);
		when(mockPayload.postToServer(any())).thenReturn(Optional.of(mockPostResponse));
		when(mockPostResponse.data()).thenReturn(new ByteArrayInputStream("Dummy response".getBytes()));
		Mockito.doThrow(cause).when(mockPayload).close();
		
		
		var ex = assertThrows(AdaptiveFormsServiceException.class, ()->underTest.renderAdaptiveForm(DUMMY_TEMPLATE_STR, createDataDocument(DUMMY_DATA)));
		
		assertThat(ex, allOf(ExceptionMatchers.exceptionMsgContainsAll("I/O Error while generating Adaptive Form"),
							 ExceptionMatchers.hasCause(cause)
							));
	}

	@Nested
	@ExtendWith(MockitoExtension.class)
	static class NullArgumentTests {

		@Mock(stubOnly = true) RestClientFactory mockClientFactory;
		@Mock(stubOnly = true) RestClient mockClient;

		AdaptiveFormsService underTest;
		
		@BeforeEach
		void setUp() throws Exception {
			when(mockClientFactory.apply(Mockito.any(AemConfig.class), Mockito.anyString(), Mockito.<Supplier<String>>any())).thenReturn(mockClient);
			underTest = AdaptiveFormsService.builder(mockClientFactory).build();
		}

		@Test 
		void testRenderAdaptiveForm_NullTemplateArgumentPathOrUrl() throws Exception { 
			NullPointerException ex = assertThrows(NullPointerException.class, ()->underTest.renderAdaptiveForm((PathOrUrl)null));
			String msg = ex.getMessage();
			assertNotNull(msg);
			assertThat(ex.getMessage(), containsStringIgnoringCase("template"));
			assertThat(ex.getMessage(), containsStringIgnoringCase("cannot be null"));
		}

		@Test 
		void testRenderAdaptiveForm_NullTemplateArgumentWIthData() throws Exception { 
			NullPointerException ex = assertThrows(NullPointerException.class, ()->underTest.renderAdaptiveForm((PathOrUrl)null, EMPTY_DOCUMENT));
			String msg = ex.getMessage();
			assertNotNull(msg);
			assertThat(ex.getMessage(), containsStringIgnoringCase("template"));
			assertThat(ex.getMessage(), containsStringIgnoringCase("cannot be null"));
		}

		@Test 
		void testRenderAdaptiveForm_NullDataArgumentOnly() throws Exception { 
			NullPointerException ex = assertThrows(NullPointerException.class, ()->underTest.renderAdaptiveForm(PathOrUrl.from(DUMMY_TEMPLATE_STR), null));
			String msg = ex.getMessage();
			assertNotNull(msg);
			assertThat(ex.getMessage(), containsStringIgnoringCase("data"));
			assertThat(ex.getMessage(), containsStringIgnoringCase("cannot be null"));
		}

		@Test 
		void testRenderAdaptiveForm_NullTemplatePath() throws Exception { 
			NullPointerException ex = assertThrows(NullPointerException.class, ()->underTest.renderAdaptiveForm((Path)null));
			String msg = ex.getMessage();
			assertNotNull(msg);
			// Caught by PathOrUrl code.  We don't need to check the contents of the message.
		}

		@Test 
		void testRenderAdaptiveForm_NullTemplateString() throws Exception { 
			NullPointerException ex = assertThrows(NullPointerException.class, ()->underTest.renderAdaptiveForm((String)null));
			String msg = ex.getMessage();
			assertNotNull(msg);
			// Caught by PathOrUrl code.  We don't need to check the contents of the message.
		}

	}

	@ParameterizedTest
	@ValueSource(strings = {"http://foo/bar/sampleform", "/foo/bar/sampleform", "crx://foo/bar/sampleform"})
	void testRenderAdaptiveForm_NonRelativePath(String testString) throws Exception { 
		AdaptiveFormsService underTest = AdaptiveFormsService.builder(mockClientFactory).build();
		Executable[] executableTest =  {
				()->underTest.renderAdaptiveForm(PathOrUrl.from(testString)),
				()->underTest.renderAdaptiveForm(testString)
		};
		
		for (Executable test : executableTest) {
			AdaptiveFormsServiceException ex = assertThrows(AdaptiveFormsServiceException.class, test);
			String msg = ex.getMessage();
			assertNotNull(msg);
			assertThat(ex.getMessage(), containsStringIgnoringCase("Only relative paths are supported"));
		}
	}
}
