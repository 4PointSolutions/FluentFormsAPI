package com._4point.aem.docservices.rest_services.client.af;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.containsStringIgnoringCase;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.StatusType;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com._4point.aem.docservices.rest_services.client.af.AdaptiveFormsService.AdaptiveFormsServiceBuilder;
import com._4point.aem.docservices.rest_services.client.af.AdaptiveFormsService.AdaptiveFormsServiceException;
import com._4point.aem.docservices.rest_services.client.helpers.ReplacingInputStream;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;

@ExtendWith(MockitoExtension.class)
class AdaptiveFormsServiceTest {
	private static final Document EMPTY_DOCUMENT = SimpleDocumentFactoryImpl.emptyDocument();
	private final static String DUMMY_TEMPLATE_STR = "TemplateString";
	private final static String DUMMY_TEMPLATE_URL = "http://TemplateString/";
	private final static Document DUMMY_DATA = MockDocumentFactory.GLOBAL_INSTANCE.create("Dummy Data".getBytes());

	private static final String CORRELATION_ID_HTTP_HDR = "X-Correlation-ID";
	private static final String CORRELATION_ID = "correlationId";
	private static final String TEST_MACHINE_NAME = "testmachinename";
	private static final int TEST_MACHINE_PORT = 8080;
	private static final String CRX_CONTENT_ROOT = "crx:/content/dam/formsanddocuments/sample-forms";

	@Mock(answer = Answers.RETURNS_SELF) Client afClient;	// answers used to mock Client's fluent interface for the Adaptive Forms Service
	@Mock WebTarget afTarget;
	@Mock Response afResponse;
	@Mock Builder afBuilder;
	@Mock StatusType afStatusType;
	
	@Captor ArgumentCaptor<String> afMachineName;
	@Captor ArgumentCaptor<String> afPath;
	@SuppressWarnings("rawtypes")
	@Captor ArgumentCaptor<String> afCorrelationId;

	@Mock(answer = Answers.RETURNS_SELF) Client dcClient;	// answers used to mock Client's fluent interface for the Data Cache Service. 
	@Mock WebTarget dcTarget;
	@Mock Response dcResponse;
	@Mock Builder dcBuilder;
	@Mock StatusType dcStatusType;
	
	@Captor ArgumentCaptor<String> dcMachineName;
	@Captor ArgumentCaptor<String> dcPath;
	@SuppressWarnings("rawtypes")
	@Captor ArgumentCaptor<Entity> dcEntity;
	@Captor ArgumentCaptor<String> dcCorrelationId;
	@Captor ArgumentCaptor<String> afQueryParamName;
	@Captor ArgumentCaptor<String> afQueryParamValue;


	@BeforeEach
	void setUp() throws Exception {
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

		public boolean hasData() {
			return hasData;
		}
		
		public Document callRenderAdaptiveForm(Boolean hasData, AdaptiveFormsService underTest) throws AdaptiveFormsServiceException {
			return templateType.render(hasData, underTest);
		}

		private enum TemplateType {
			STRING(TemplateType::renderAdaptiveFormWithString), 
			PATH(TemplateType::renderAdaptiveFormWithPath),
			URL(TemplateType::renderAdaptiveFormWithUrl),
			PATH_OR_URL(TemplateType::renderAdaptiveFormWithPathOrUrl);
			
			private final BiFunction_WithExceptions<Boolean, AdaptiveFormsService, Document, AdaptiveFormsServiceException> renderAdaptiveFormFn;

			private TemplateType(BiFunction_WithExceptions<Boolean, AdaptiveFormsService, Document, AdaptiveFormsServiceException> renderAdaptiveFormFn) {
				this.renderAdaptiveFormFn = renderAdaptiveFormFn;
			}

			public Document render(Boolean hasData, AdaptiveFormsService underTest) throws AdaptiveFormsServiceException {
				return renderAdaptiveFormFn.apply(hasData, underTest);
			}

			private static Document renderAdaptiveFormWithString(Boolean hasData, AdaptiveFormsService underTest) throws AdaptiveFormsServiceException {
				return hasData.booleanValue() ? underTest.renderAdaptiveForm(DUMMY_TEMPLATE_STR, DUMMY_DATA) 
											  : underTest.renderAdaptiveForm(DUMMY_TEMPLATE_STR);
			}
			private static Document renderAdaptiveFormWithPath(Boolean hasData, AdaptiveFormsService underTest) throws AdaptiveFormsServiceException {
				
				return hasData.booleanValue() ? underTest.renderAdaptiveForm(Paths.get(DUMMY_TEMPLATE_STR), DUMMY_DATA) 
											  : underTest.renderAdaptiveForm(Paths.get(DUMMY_TEMPLATE_STR));
			}
			private static Document renderAdaptiveFormWithUrl(Boolean hasData, AdaptiveFormsService underTest) throws AdaptiveFormsServiceException {
				try {
					return hasData.booleanValue() ? underTest.renderAdaptiveForm(new URL(DUMMY_TEMPLATE_URL), DUMMY_DATA) 
												  : underTest.renderAdaptiveForm(new URL(DUMMY_TEMPLATE_URL));
				} catch (MalformedURLException e) {
					throw new IllegalStateException("Error while turning DUMMY_TEMPLATE_STR into URL.", e);
				}
			}
			private static Document renderAdaptiveFormWithPathOrUrl(Boolean hasData, AdaptiveFormsService underTest) throws AdaptiveFormsServiceException {
				return hasData.booleanValue() ? underTest.renderAdaptiveForm(PathOrUrl.from(DUMMY_TEMPLATE_STR), DUMMY_DATA) 
								 			  : underTest.renderAdaptiveForm(PathOrUrl.from(DUMMY_TEMPLATE_STR));
			}

		}
	};
	


	@ParameterizedTest
	@EnumSource
	void testRenderAdaptiveForm(HappyPath scenario) throws Exception {

		
		Document dcResponseData = scenario.hasData() ? SimpleDocumentFactoryImpl.getFactory().create("response Document Data".getBytes()) : EMPTY_DOCUMENT;
		Document afResponseData = SimpleDocumentFactoryImpl.getFactory().create("response Document Data".getBytes());
		setUpJaxRsClientMocksForAf(dcResponseData, MediaType.TEXT_PLAIN_TYPE, Response.Status.OK, 
								   afResponseData, MediaType.TEXT_HTML_TYPE, Response.Status.OK, true);
		
		boolean useSSL = false;
		boolean useCorrelationId = false;
		if (scenario.useSsl()) {
			useSSL = true;
			useCorrelationId = true;
			when(afBuilder.header(eq(CORRELATION_ID_HTTP_HDR), afCorrelationId.capture())).thenReturn(afBuilder);
		} else {
			useSSL = false;
			useCorrelationId = false;
		}

		AdaptiveFormsServiceBuilder svcBuilder = AdaptiveFormsService.builder()
															   .machineName(TEST_MACHINE_NAME)
															   .port(TEST_MACHINE_PORT)
															   .basicAuthentication("username", "password")
															   .useSsl(useSSL)
															   .clientFactory(()->afClient);
		if (useCorrelationId) {
			svcBuilder.correlationId(()->CORRELATION_ID);
		}


		AdaptiveFormsService underTest = svcBuilder.build();
		
		
		Document result = scenario.callRenderAdaptiveForm(scenario.hasData(), underTest);
		
		
		// Make sure the correct URL is called.
		final String expectedPrefix = useSSL ? "https://" : "http://";
		assertAll(
				()->assertThat("Expected target url contains '" + expectedPrefix + "'", afMachineName.getValue(), containsString(expectedPrefix)),
				()->assertThat("Expected target url contains TEST_MACHINE_NAME", afMachineName.getValue(), containsString(TEST_MACHINE_NAME)),
				()->assertThat("Expected target url contains TEST_MACHINE_PORT", afMachineName.getValue(), containsString(Integer.toString(TEST_MACHINE_PORT))),
				()->assertThat("Expected target url contains '/services/AdaptiveForms/RenderAdaptiveForm'", afPath.getValue(), containsString("/services/AdaptiveForms/RenderAdaptiveForm"))
		);

		// Make sure that the arguments we passed in are transmitted correctly.
		if (scenario.hasData()) {
			@SuppressWarnings("unchecked")
			Entity<FormDataMultiPart> postedEntity = (Entity<FormDataMultiPart>)dcEntity.getValue();
			FormDataMultiPart postedData = postedEntity.getEntity();

			assertEquals(MediaType.MULTIPART_FORM_DATA_TYPE, postedEntity.getMediaType());
			validateDocumentFormField(postedData, "data", new MediaType("application", "xml"),DUMMY_DATA.getInlineData());
		}
		
		if (useCorrelationId) {
			assertEquals(CORRELATION_ID, afCorrelationId.getValue());
		}
		
		// Make sure the response is correct.
		assertArrayEquals(afResponseData.getInlineData(), result.getInlineData());
		assertEquals(MediaType.TEXT_HTML_TYPE, MediaType.valueOf(result.getContentType()));
	}

	private void setUpJaxRsClientMocksForAf(Document responseData1, MediaType produces1, Status status1, 
											Document responseData2, MediaType produces2, Status status2, boolean gettingHeader) throws IOException {
		// TODO: Change this based on https://maciejwalkowiak.com/mocking-fluent-interfaces/
		when(afClient.target(afMachineName.capture())).thenReturn(afTarget);
		when(afTarget.path(afPath.capture())).thenReturn(afTarget, afTarget);
		when(afTarget.queryParam(afQueryParamName.capture(), afQueryParamValue.capture())).thenReturn(afTarget, afTarget);
		when(afTarget.request()).thenReturn(afBuilder, afBuilder);
		if (!responseData1.isEmpty()) {
			when(afBuilder.accept(MediaType.TEXT_PLAIN_TYPE)).thenReturn(afBuilder, afBuilder);
			when(afBuilder.post(dcEntity.capture())).thenReturn(afResponse);	// Only post to Data Cache
		}
		if (!responseData2.isEmpty()) {
			when(afBuilder.accept(MediaType.TEXT_HTML_TYPE)).thenReturn(afBuilder, afBuilder);
			when(afBuilder.get()).thenReturn(afResponse);						// Perform GET to get AF
		}
		if (status1.getFamily() != Response.Status.Family.SUCCESSFUL && status2.getFamily() != Response.Status.Family.SUCCESSFUL) {
			// If we're not successful, then there's a couple of other calls we need to mock.
			when(afStatusType.getStatusCode()).thenReturn(status1.getStatusCode(), status2.getStatusCode());	// return status
			when(afStatusType.getReasonPhrase()).thenReturn(status1.getReasonPhrase(), status2.getReasonPhrase());	// return status
		} else if (status1.getFamily() != Response.Status.Family.SUCCESSFUL) {
			when(afStatusType.getStatusCode()).thenReturn(status1.getStatusCode());	// return status
			when(afStatusType.getReasonPhrase()).thenReturn(status1.getReasonPhrase());	// return status
		} else if (status2.getFamily() != Response.Status.Family.SUCCESSFUL) {
			when(afStatusType.getStatusCode()).thenReturn(status2.getStatusCode());	// return status
			when(afStatusType.getReasonPhrase()).thenReturn(status2.getReasonPhrase());	// return status
		}
		
		when(afResponse.hasEntity()).thenReturn(!responseData1.isEmpty(), !responseData2.isEmpty());
		if (!responseData1.isEmpty() && !responseData2.isEmpty()) {
			when(afResponse.getStatusInfo()).thenReturn(afStatusType, afStatusType);
			when(afStatusType.getFamily()).thenReturn(status1.getFamily(), status2.getFamily());	// return status
			when(afResponse.hasEntity()).thenReturn(true, true);
			when(afResponse.getEntity()).thenReturn(new ByteArrayInputStream(responseData1.getInlineData()), new ByteArrayInputStream(responseData2.getInlineData()));
			if (gettingHeader) {
				when(afResponse.getHeaderString(HttpHeaders.CONTENT_TYPE)).thenReturn(produces1.toString(), produces2.toString());
			}
		} else if (!responseData1.isEmpty()) {
			when(afResponse.getStatusInfo()).thenReturn(afStatusType, afStatusType);
			when(afStatusType.getFamily()).thenReturn(status1.getFamily());	// return status
			when(afResponse.hasEntity()).thenReturn(true);
			when(afResponse.getEntity()).thenReturn(responseData1.getInputStream());
			if (gettingHeader) {
				when(afResponse.getHeaderString(HttpHeaders.CONTENT_TYPE)).thenReturn(produces1.toString());
			}
		} else if (!responseData2.isEmpty()) {
			when(afResponse.getStatusInfo()).thenReturn(afStatusType, afStatusType);
			when(afStatusType.getFamily()).thenReturn(status2.getFamily());	// return status
			when(afResponse.hasEntity()).thenReturn(true);
			when(afResponse.getEntity()).thenReturn(responseData2.getInputStream());
			if (gettingHeader) {
				when(afResponse.getHeaderString(HttpHeaders.CONTENT_TYPE)).thenReturn(produces2.toString());
			}
		}
	}

	@Test
	void testRenderAdaptiveFormWithFilter() throws Exception {
		Document responseData = MockDocumentFactory.GLOBAL_INSTANCE.create("response Document Data".getBytes());

		setUpJaxRsClientMocksForAf(responseData, MediaType.TEXT_PLAIN_TYPE, Response.Status.OK,
								   responseData, MediaType.TEXT_HTML_TYPE, Response.Status.OK, true);
		
		AdaptiveFormsService underTest = AdaptiveFormsService.builder()
													   .machineName(TEST_MACHINE_NAME)
													   .port(TEST_MACHINE_PORT)
													   .basicAuthentication("username", "password")
													   .useSsl(false)
													   .clientFactory(()->afClient)
													   .addRenderResultFilter(is->new ReplacingInputStream(is, "Document", "tnemucoD"))
													   .build();
	
		Document result = underTest.renderAdaptiveForm(DUMMY_TEMPLATE_STR, DUMMY_DATA);
		
		assertEquals("response tnemucoD Data", new String(result.getInlineData()));
		assertEquals(MediaType.TEXT_HTML_TYPE, MediaType.valueOf(result.getContentType()));
	}
	
	@Test
	void testRenderAdaptiveFormWithTwoFilters() throws Exception {
		Document responseData = MockDocumentFactory.GLOBAL_INSTANCE.create("response Document Data".getBytes());

		setUpJaxRsClientMocksForAf(responseData, MediaType.TEXT_PLAIN_TYPE, Response.Status.OK,
								   responseData, MediaType.TEXT_HTML_TYPE, Response.Status.OK, true);
		
		AdaptiveFormsService underTest = AdaptiveFormsService.builder()
													   .machineName(TEST_MACHINE_NAME)
													   .port(TEST_MACHINE_PORT)
													   .basicAuthentication("username", "password")
													   .useSsl(false)
													   .clientFactory(()->afClient)
													   .addRenderResultFilter(is->new ReplacingInputStream(is, "Document", "tnemucoD"))
													   .addRenderResultFilter(is->new ReplacingInputStream(is, "coD", "rtm"))
													   .build();
	
		Document result = underTest.renderAdaptiveForm(DUMMY_TEMPLATE_STR, DUMMY_DATA);
		
		assertEquals("response tnemurtm Data", new String(result.getInlineData()));
		assertEquals(MediaType.TEXT_HTML_TYPE, MediaType.valueOf(result.getContentType()));
	}
	
	@Test
	void testRenderAdaptiveFormDataCacheFailureStatus() throws Exception {
		String responseString = "response Document Data";
		Document responseData = MockDocumentFactory.GLOBAL_INSTANCE.create(responseString.getBytes());

		setUpJaxRsClientMocksForAf(responseData, MediaType.TEXT_PLAIN_TYPE, Response.Status.BAD_REQUEST, 
								   EMPTY_DOCUMENT, MediaType.TEXT_HTML_TYPE, Response.Status.BAD_REQUEST, false);

		AdaptiveFormsServiceBuilder svcBuilder = AdaptiveFormsService.builder()
				   .machineName(TEST_MACHINE_NAME)
				   .port(TEST_MACHINE_PORT)
				   .basicAuthentication("username", "password")
				   .clientFactory(()->afClient);

		AdaptiveFormsService underTest = svcBuilder.build();

		AdaptiveFormsServiceException ex = assertThrows(AdaptiveFormsServiceException.class, ()->underTest.renderAdaptiveForm(DUMMY_TEMPLATE_STR, DUMMY_DATA));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, allOf(containsStringIgnoringCase("Call to server failed"),
							  containsStringIgnoringCase("400"),
							  containsStringIgnoringCase("Bad Request"),
							  containsStringIgnoringCase(responseString))
							 );
	}

	@Test
	void testRenderAdaptiveFormFailureStatus() throws Exception {
		String responseString = "response Document Data";
		Document responseData = MockDocumentFactory.GLOBAL_INSTANCE.create(responseString.getBytes());

		setUpJaxRsClientMocksForAf(EMPTY_DOCUMENT, MediaType.TEXT_PLAIN_TYPE, Response.Status.OK, 
								   responseData, MediaType.TEXT_HTML_TYPE, Response.Status.BAD_REQUEST, false);

		AdaptiveFormsServiceBuilder svcBuilder = AdaptiveFormsService.builder()
				   .machineName(TEST_MACHINE_NAME)
				   .port(TEST_MACHINE_PORT)
				   .basicAuthentication("username", "password")
				   .clientFactory(()->afClient);

		AdaptiveFormsService underTest = svcBuilder.build();

		AdaptiveFormsServiceException ex = assertThrows(AdaptiveFormsServiceException.class, ()->underTest.renderAdaptiveForm(DUMMY_TEMPLATE_STR));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, allOf(containsStringIgnoringCase("Call to server failed"),
							  containsStringIgnoringCase("400"),
							  containsStringIgnoringCase("Bad Request"),
							  containsStringIgnoringCase(responseString))
							 );
	}

	@Test
	void testRenderAdaptiveFormDataCacheBadResponse() throws Exception {
		String responseString = "response Document Data";
		Document responseData = MockDocumentFactory.GLOBAL_INSTANCE.create(responseString.getBytes());

		setUpJaxRsClientMocksForAf(responseData, MediaType.TEXT_HTML_TYPE, Response.Status.OK, 
								   EMPTY_DOCUMENT, MediaType.TEXT_PLAIN_TYPE, Response.Status.OK, true);

		AdaptiveFormsServiceBuilder svcBuilder = AdaptiveFormsService.builder()
				   .machineName(TEST_MACHINE_NAME)
				   .port(TEST_MACHINE_PORT)
				   .basicAuthentication("username", "password")
				   .clientFactory(()->afClient);

		AdaptiveFormsService underTest = svcBuilder.build();

		AdaptiveFormsServiceException ex = assertThrows(AdaptiveFormsServiceException.class, ()->underTest.renderAdaptiveForm(DUMMY_TEMPLATE_STR, DUMMY_DATA));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, allOf(containsStringIgnoringCase("response from AEM server was not plain text"),
							  containsStringIgnoringCase("datacache"),
							  containsStringIgnoringCase(responseString),
							  containsStringIgnoringCase("content-type='text/html'"))
							 );
	}

	@Test
	void testRenderAdaptiveFormBadResponse() throws Exception {
		String responseString = "response Document Data";
		Document responseData = MockDocumentFactory.GLOBAL_INSTANCE.create(responseString.getBytes());

		setUpJaxRsClientMocksForAf(EMPTY_DOCUMENT, MediaType.TEXT_PLAIN_TYPE, Response.Status.OK, 
								   responseData, MediaType.TEXT_PLAIN_TYPE, Response.Status.OK, true);

		AdaptiveFormsServiceBuilder svcBuilder = AdaptiveFormsService.builder()
				   .machineName(TEST_MACHINE_NAME)
				   .port(TEST_MACHINE_PORT)
				   .basicAuthentication("username", "password")
				   .clientFactory(()->afClient);

		AdaptiveFormsService underTest = svcBuilder.build();

		AdaptiveFormsServiceException ex = assertThrows(AdaptiveFormsServiceException.class, ()->underTest.renderAdaptiveForm(DUMMY_TEMPLATE_STR));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, allOf(containsStringIgnoringCase("Response from AEM server was not HTML"),
							  containsStringIgnoringCase(responseString),
							  containsStringIgnoringCase("content-type='text/plain'"))
							 );
	}

	@Test
	void testRenderAdaptiveFormDataCacheBadResponseNoEntity() throws Exception {

//		setUpJaxRsClientMocksForAf(EMPTY_DOCUMENT, MediaType.TEXT_PLAIN_TYPE, Response.Status.OK, 
//								   EMPTY_DOCUMENT, MediaType.TEXT_PLAIN_TYPE, Response.Status.OK, false);
		when(afClient.target(afMachineName.capture())).thenReturn(afTarget);
		when(afTarget.path(afPath.capture())).thenReturn(afTarget, afTarget);
		when(afTarget.queryParam(afQueryParamName.capture(), afQueryParamValue.capture())).thenReturn(afTarget, afTarget);
		when(afTarget.request()).thenReturn(afBuilder, afBuilder);
		when(afBuilder.accept(MediaType.TEXT_PLAIN_TYPE)).thenReturn(afBuilder);
		when(afBuilder.post(dcEntity.capture())).thenReturn(afResponse);	// Only post to Data Cache
		when(afResponse.getStatusInfo()).thenReturn(afStatusType);
		when(afStatusType.getFamily()).thenReturn(Response.Status.OK.getFamily());	// return status
		when(afResponse.hasEntity()).thenReturn(false);

		AdaptiveFormsServiceBuilder svcBuilder = AdaptiveFormsService.builder()
				   .machineName(TEST_MACHINE_NAME)
				   .port(TEST_MACHINE_PORT)
				   .basicAuthentication("username", "password")
				   .clientFactory(()->afClient);

		AdaptiveFormsService underTest = svcBuilder.build();

		AdaptiveFormsServiceException ex = assertThrows(AdaptiveFormsServiceException.class, ()->underTest.renderAdaptiveForm(DUMMY_TEMPLATE_STR, DUMMY_DATA));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertAll(
				()->assertThat(msg, containsStringIgnoringCase("server failed to return document")),
				()->assertThat(msg, containsStringIgnoringCase("dataCache"))
				);
	}

	@Test
	void testRenderAdaptiveFormBadResponseNoEntity() throws Exception {

//		setUpJaxRsClientMocksForAf(EMPTY_DOCUMENT, MediaType.TEXT_PLAIN_TYPE, Response.Status.OK, 
//								   EMPTY_DOCUMENT, MediaType.TEXT_PLAIN_TYPE, Response.Status.OK, false);
		when(afClient.target(afMachineName.capture())).thenReturn(afTarget);
		when(afTarget.path(afPath.capture())).thenReturn(afTarget, afTarget);
		when(afTarget.queryParam(afQueryParamName.capture(), afQueryParamValue.capture())).thenReturn(afTarget, afTarget);
		when(afTarget.request()).thenReturn(afBuilder, afBuilder);
		when(afBuilder.accept(MediaType.TEXT_HTML_TYPE)).thenReturn(afBuilder);
		when(afBuilder.get()).thenReturn(afResponse);						// Perform GET to get AF
		when(afResponse.getStatusInfo()).thenReturn(afStatusType);
		when(afStatusType.getFamily()).thenReturn(Response.Status.OK.getFamily());	// return status
		when(afResponse.hasEntity()).thenReturn(false);

		AdaptiveFormsServiceBuilder svcBuilder = AdaptiveFormsService.builder()
				   .machineName(TEST_MACHINE_NAME)
				   .port(TEST_MACHINE_PORT)
				   .basicAuthentication("username", "password")
				   .clientFactory(()->afClient);

		AdaptiveFormsService underTest = svcBuilder.build();

		AdaptiveFormsServiceException ex = assertThrows(AdaptiveFormsServiceException.class, ()->underTest.renderAdaptiveForm(DUMMY_TEMPLATE_STR));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertAll(
				()->assertThat(msg, containsStringIgnoringCase("server failed to return document")),
				()->assertThat(msg, not(containsStringIgnoringCase("dataCache")))
				);
	}

	
	@Nested
	class NullArgumentTests {

		AdaptiveFormsService underTest = AdaptiveFormsService.builder().build();
		
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
		void testRenderAdaptiveForm_NullTemplateUrl() throws Exception { 
			NullPointerException ex = assertThrows(NullPointerException.class, ()->underTest.renderAdaptiveForm((URL)null));
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

	private void validateDocumentFormField(FormDataMultiPart postedData, String fieldName, MediaType expectedMediaType, byte[] expectedData) throws IOException {
		List<FormDataBodyPart> pdfFields = postedData.getFields(fieldName);
		assertEquals(1, pdfFields.size());
		
		FormDataBodyPart pdfPart = pdfFields.get(0);
		assertEquals(expectedMediaType, pdfPart.getMediaType());
		byte[] pdfBytes = IOUtils.toByteArray((InputStream) pdfPart.getEntity());
		assertArrayEquals(expectedData, pdfBytes);  // TODO: Need to figure out how to test for entity.
	}

}
