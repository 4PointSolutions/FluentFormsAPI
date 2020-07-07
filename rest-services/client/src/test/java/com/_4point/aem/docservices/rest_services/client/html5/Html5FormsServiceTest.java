package com._4point.aem.docservices.rest_services.client.html5;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.containsStringIgnoringCase;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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

import com._4point.aem.docservices.rest_services.client.html5.Html5FormsService.Html5FormsServiceBuilder;
import com._4point.aem.docservices.rest_services.client.html5.Html5FormsService.Html5FormsServiceException;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;

@ExtendWith(MockitoExtension.class)
class Html5FormsServiceTest {

	private final static String DUMMY_TEMPLATE_STR = "TemplateString";
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

	
	private enum HappyPath { 

		SSL_NODATA(true, false),
		SSL_DATA(true, true),
		NOSSL_NODATA(false, false),
		NOSSL_DATA(false, true);
		
		private final boolean ssl;
		private final boolean hasData;
		
		private HappyPath(boolean ssl, boolean hasData) {
			this.ssl = ssl;
			this.hasData = hasData;
		}

		public boolean useSsl() {
			return ssl;
		}

		public boolean hasData() {
			return hasData;
		}
	};
	

	@ParameterizedTest
	@EnumSource
	void testRenderHtml5Form(HappyPath scenario) throws Exception {
		
		Document responseData = MockDocumentFactory.GLOBAL_INSTANCE.create("response Document Data".getBytes());

		setUpJaxRsClientMocks(responseData, MediaType.TEXT_HTML_TYPE);
		
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
		
		Document result = scenario.hasData() ? underTest.renderHtml5Form(DUMMY_TEMPLATE_STR, DUMMY_DATA) 
											 : underTest.renderHtml5Form(DUMMY_TEMPLATE_STR);
		
		
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
		validateTextFormField(postedData, "template", DUMMY_TEMPLATE_STR);
		
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


	private void setUpJaxRsClientMocks(Document responseData, MediaType produces) throws IOException {
		// TODO: Change this based on https://maciejwalkowiak.com/mocking-fluent-interfaces/
		when(client.target(machineName.capture())).thenReturn(target);
		when(target.path(path.capture())).thenReturn(target);
		when(target.request()).thenReturn(builder);
		when(builder.accept(produces)).thenReturn(builder);
		when(builder.post(entity.capture())).thenReturn(response);
		when(response.getStatusInfo()).thenReturn(statusType);
		when(statusType.getFamily()).thenReturn(Response.Status.Family.SUCCESSFUL);	// return Successful
		when(response.hasEntity()).thenReturn(true);
		when(response.getEntity()).thenReturn(new ByteArrayInputStream(responseData.getInlineData()));
		when(response.getHeaderString(HttpHeaders.CONTENT_TYPE)).thenReturn(produces.toString());
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
