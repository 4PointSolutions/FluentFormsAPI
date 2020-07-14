package com._4point.aem.docservices.rest_services.client.html5;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.containsStringIgnoringCase;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
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
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.StatusType;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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

	private static final String CORRELATION_ID_HTTP_HDR = "X-Correlation-ID";
	private static final String CORRELATION_ID = "correlationId";
	private static final String TEST_MACHINE_NAME = "testmachinename";
	private static final int TEST_MACHINE_PORT = 8080;
	private static final String CRX_CONTENT_ROOT = "crx:/content/dam/formsanddocuments/sample-forms";

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
		
		Document responseData = MockDocumentFactory.GLOBAL_INSTANCE.create("response Document Data".getBytes());

		setUpJaxRsClientMocks(responseData, MediaType.TEXT_HTML_TYPE, Response.Status.OK, true);
		
		boolean useSSL = false;
		boolean useCorrelationId = false;
		if (scenario.useSsl()) {
			useSSL = true;
			useCorrelationId = true;
			when(builder.header(eq(CORRELATION_ID_HTTP_HDR), correlationId.capture())).thenReturn(builder);
		} else {
			useSSL = false;
			useCorrelationId = false;
		}

		Html5FormsServiceBuilder svcBuilder = Html5FormsService.builder()
															   .machineName(TEST_MACHINE_NAME)
															   .port(TEST_MACHINE_PORT)
															   .basicAuthentication("username", "password")
															   .useSsl(useSSL)
															   .clientFactory(()->client);
		if (useCorrelationId) {
			svcBuilder.correlationId(()->CORRELATION_ID);
		}


		Html5FormsService underTest = svcBuilder.build();
		
		
		Document result = scenario.callRenderHtml5Form(scenario.hasData(), underTest);
		
		
		// Make sure the correct URL is called.
		final String expectedPrefix = useSSL ? "https://" : "http://";
		assertAll(
				()->assertThat("Expected target url contains '" + expectedPrefix + "'", machineName.getValue(), containsString(expectedPrefix)),
				()->assertThat("Expected target url contains TEST_MACHINE_NAME", machineName.getValue(), containsString(TEST_MACHINE_NAME)),
				()->assertThat("Expected target url contains TEST_MACHINE_PORT", machineName.getValue(), containsString(Integer.toString(TEST_MACHINE_PORT))),
				()->assertThat("Expected target url contains '/services/Html5/RenderHtml5Form'", path.getValue(), containsString("/services/Html5/RenderHtml5Form"))
		);

		// Make sure that the arguments we passed in are transmitted correctly.
		@SuppressWarnings("unchecked")
		Entity<FormDataMultiPart> postedEntity = (Entity<FormDataMultiPart>)entity.getValue();
		FormDataMultiPart postedData = postedEntity.getEntity();
		
		assertEquals(MediaType.MULTIPART_FORM_DATA_TYPE, postedEntity.getMediaType());
		validateTextFormField(postedData, "template", scenario.templateType == HappyPath.TemplateType.URL ? DUMMY_TEMPLATE_URL : DUMMY_TEMPLATE_STR);
		
		if (scenario.hasData()) {
			validateDocumentFormField(postedData, "data", new MediaType("application", "xml"),DUMMY_DATA.getInlineData());
		} else {
			assertNull(postedData.getFields("data"));
		}
		
		if (useCorrelationId) {
			assertEquals(CORRELATION_ID, correlationId.getValue());
		}
		
		// Make sure the response is correct.
		assertArrayEquals(responseData.getInlineData(), result.getInlineData());
		assertEquals(MediaType.TEXT_HTML_TYPE, MediaType.valueOf(result.getContentType()));
	}

	private void setUpJaxRsClientMocks(Document responseData, MediaType produces, Status status, boolean gettingHeader) throws IOException {
		// TODO: Change this based on https://maciejwalkowiak.com/mocking-fluent-interfaces/
		when(client.target(machineName.capture())).thenReturn(target);
		when(target.path(path.capture())).thenReturn(target);
		when(target.request()).thenReturn(builder);
		when(builder.accept(MediaType.TEXT_HTML_TYPE)).thenReturn(builder);
		when(builder.post(entity.capture())).thenReturn(response);
		when(response.getStatusInfo()).thenReturn(statusType);
		when(statusType.getFamily()).thenReturn(status.getFamily());	// return status
		if (status.getFamily() != Response.Status.Family.SUCCESSFUL) {
			// If we're not successful, then there's a couple of other calls we need to mock.
			when(statusType.getStatusCode()).thenReturn(status.getStatusCode());	// return status
			when(statusType.getReasonPhrase()).thenReturn(status.getReasonPhrase());	// return status
		} 
		
		if (responseData.isEmpty()) {
			when(response.hasEntity()).thenReturn(false);
		} else {
			when(response.hasEntity()).thenReturn(true);
			when(response.getEntity()).thenReturn(new ByteArrayInputStream(responseData.getInlineData()));
		}
		if (gettingHeader) {
			when(response.getHeaderString(HttpHeaders.CONTENT_TYPE)).thenReturn(produces.toString());
		}
	}

	@Test
	void testRenderHtml5FormWithFilter() throws Exception {
		Document responseData = MockDocumentFactory.GLOBAL_INSTANCE.create("response Document Data".getBytes());

		setUpJaxRsClientMocks(responseData, MediaType.TEXT_HTML_TYPE, Response.Status.OK, true);
		
		Html5FormsService underTest = Html5FormsService.builder()
													   .machineName(TEST_MACHINE_NAME)
													   .port(TEST_MACHINE_PORT)
													   .basicAuthentication("username", "password")
													   .useSsl(false)
													   .clientFactory(()->client)
													   .addRenderResultFilter(is->new ReplacingInputStream(is, "Document", "tnemucoD"))
													   .build();
	
		Document result = underTest.renderHtml5Form(DUMMY_TEMPLATE_STR, DUMMY_DATA);
		
		assertEquals("response tnemucoD Data", new String(result.getInlineData()));
		assertEquals(MediaType.TEXT_HTML_TYPE, MediaType.valueOf(result.getContentType()));
	}
	
	@Test
	void testRenderHtml5FormFailureStatus() throws Exception {
		String responseString = "response Document Data";
		Document responseData = MockDocumentFactory.GLOBAL_INSTANCE.create(responseString.getBytes());

		setUpJaxRsClientMocks(responseData, MediaType.TEXT_HTML_TYPE, Response.Status.BAD_REQUEST, false);

		Html5FormsServiceBuilder svcBuilder = Html5FormsService.builder()
				   .machineName(TEST_MACHINE_NAME)
				   .port(TEST_MACHINE_PORT)
				   .basicAuthentication("username", "password")
				   .clientFactory(()->client);

		Html5FormsService underTest = svcBuilder.build();

		Html5FormsServiceException ex = assertThrows(Html5FormsServiceException.class, ()->underTest.renderHtml5Form(DUMMY_TEMPLATE_STR));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, allOf(containsStringIgnoringCase("Call to server failed"),
							  containsStringIgnoringCase("400"),
							  containsStringIgnoringCase("Bad Request"),
							  containsStringIgnoringCase(responseString))
							 );
	}

	@Test
	void testRenderHtml5FormBadResponse() throws Exception {
		String responseString = "response Document Data";
		Document responseData = MockDocumentFactory.GLOBAL_INSTANCE.create(responseString.getBytes());

		setUpJaxRsClientMocks(responseData, MediaType.TEXT_PLAIN_TYPE, Response.Status.OK, true);

		Html5FormsServiceBuilder svcBuilder = Html5FormsService.builder()
				   .machineName(TEST_MACHINE_NAME)
				   .port(TEST_MACHINE_PORT)
				   .basicAuthentication("username", "password")
				   .clientFactory(()->client);

		Html5FormsService underTest = svcBuilder.build();

		Html5FormsServiceException ex = assertThrows(Html5FormsServiceException.class, ()->underTest.renderHtml5Form(DUMMY_TEMPLATE_STR));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, allOf(containsStringIgnoringCase("Response from AEM server was not HTML"),
							  containsStringIgnoringCase(responseString),
							  containsStringIgnoringCase("content-type='text/plain'"))
							 );
	}

	@Test
	void testRenderHtml5FormBadResponseNoEntity() throws Exception {

		setUpJaxRsClientMocks(SimpleDocumentFactoryImpl.emptyDocument(), MediaType.TEXT_PLAIN_TYPE, Response.Status.OK, false);

		Html5FormsServiceBuilder svcBuilder = Html5FormsService.builder()
				   .machineName(TEST_MACHINE_NAME)
				   .port(TEST_MACHINE_PORT)
				   .basicAuthentication("username", "password")
				   .clientFactory(()->client);

		Html5FormsService underTest = svcBuilder.build();

		Html5FormsServiceException ex = assertThrows(Html5FormsServiceException.class, ()->underTest.renderHtml5Form(DUMMY_TEMPLATE_STR));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, containsStringIgnoringCase("server failed to return document"));
	}

	
	@Nested
	class NullArgumentTests {

		Html5FormsService underTest = Html5FormsService.builder().build();
		
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

}
