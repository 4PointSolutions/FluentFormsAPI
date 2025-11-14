package com._4point.aem.fluentforms.spring;

import static com._4point.testing.matchers.javalang.ExceptionMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Function;

import org.hamcrest.Description;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com._4point.aem.fluentforms.spring.AemProxyAfSubmission.AfSubmitAemProxyProcessor;
import com._4point.aem.fluentforms.spring.AemProxyAfSubmission.AfSubmitLocalProcessor;
import com._4point.aem.fluentforms.spring.AemProxyAfSubmission.AfSubmissionHandler;
import com._4point.aem.fluentforms.spring.AemProxyAfSubmission.SpringAfSubmitProcessor;
import com._4point.aem.fluentforms.spring.AemProxyAfSubmission.AfSubmissionHandler.SubmitResponse;
import com._4point.aem.fluentforms.spring.AemProxyAfSubmission.AfSubmitLocalProcessor.InternalAfSubmitAemProxyProcessor;
import com._4point.aem.fluentforms.spring.AemProxyAfSubmissionTest.AemProxyAfSubmissionTestWithLocalAfSubmitProcessorTest.MockAemProxy;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;

/**
 * Tests for AemProxyAfSubmissions classes.
 * 
 * Includes inner classes that test the different SubmitProcessor implementations. 
 * 
 */
class AemProxyAfSubmissionTest {
	public static final String AF_TEMPLATE_NAME = "sample00002test";
	private static final String SUBMIT_ADAPTIVE_FORM_SERVICE_PATH = "/aem/content/forms/af/" + AF_TEMPLATE_NAME + "/jcr:content/guideContainer.af.submit.jsp";
	private static final String AEM_SUBMIT_ADAPTIVE_FORM_SERVICE_PATH = SUBMIT_ADAPTIVE_FORM_SERVICE_PATH.substring(4); // Same as above minus "/aem"
	private static final String SAMPLE_RESPONSE_BODY = "body";

//	record JakartaRestClient(WebTarget target, URI uri) {};
//
//	public static JakartaRestClient setUpRestClient(int port) {
//		var uri = getBaseUri(port);
//		var target = ClientBuilder.newClient() //newClient(clientConfig)
//				 .property(ClientProperties.FOLLOW_REDIRECTS, Boolean.FALSE)	// Disable re-directs so that we can test for "thank you page" redirection.
//				 .register(MultiPartFeature.class)
//				 .target(uri);
//		return new JakartaRestClient(target, uri);
//	}

	private static RestClient createRestClient(int port) {
		return RestClient.builder()
						 .baseUrl(getBaseUri(port))		
						 .build();
	}
	
	record FormDataMultiPart(MultiValueMap<String, HttpEntity<?>> parts) {
		public FormDataMultiPart() {
			this(new LinkedMultiValueMap<>());
		}
		
		public FormDataMultiPart field(String fieldName, String fieldData) {
			internalAdd(fieldName, fieldData, MediaType.TEXT_PLAIN);
			return this;
		}

		public FormDataMultiPart field(String fieldName, byte[] fieldData) {
			internalAdd(fieldName, fieldData, MediaType.APPLICATION_OCTET_STREAM);
			return this;
		}

		private void internalAdd(String fieldName, Object fieldData, MediaType contentType) {
			parts.add(fieldName, new HttpEntity<>(fieldData, new HttpHeaders() {
				{
					setContentType(contentType);
				}
			}));
		}
	}
	

	/* package */ static FormDataMultiPart mockFormData(String redirect, String data) {
		var getPdfForm = new FormDataMultiPart();
		getPdfForm.field("guideContainerPath", "/aem/content/forms/af/" + AF_TEMPLATE_NAME + "/jcr:content/guideContainer")
				  .field("aemFormComponentPath", "")
				  .field("_asyncSubmit", "false")
				  .field("_charset_", "UTF-8")
				  .field("runtimeLocale", "en")
				  .field("fileAttachmentMap", "{}")
				  .field("afSubmissionInfo", "{\"computedMetaInfo\":{},\"stateOverrides\":{},\"signers\":{}}")
				  .field("TextField1", "TextField1 Contents")
				  .field("TextField2", "TextField2 Contents")
				  .field("jcr:data", data)
				  .field(":redirect", redirect)
				  .field(":selfUrl", "/aem/content/forms/af/" + AF_TEMPLATE_NAME)
				  .field("_guideValueMap", "yes")
				  .field("_guideValuesMap", "{\"textdraw1555538078737\":\"<p style=\\\"text-align: center;\\\"><b>Sample Form</b></p>\\n\",\"TextField1\":\"DFGDFG\",\"TextField2\":\"DFGDG 233\",\"submit\":null}")
				  .field("_guideAttachments", "")
				  .field(":cq_csrf_token", "eyJleHAiOjE1NjU2MzUzNzcsImlhdCI6MTU2NTYzNDc3N30.9KB9yPr_mvIfyiwzn5S8mMh-yUzD0-BF99cJR7vW49M");
		return getPdfForm;
	}

	private static URI getBaseUri(int port) {
		return URI.create("http://localhost:" + port);
	}

	// Supporting mock application class that limits the amount of classes to be loaded.
	@SpringBootApplication()
	@EnableConfigurationProperties({AemConfiguration.class,AemProxyConfiguration.class})
	public static class TestApplication {
		public static void main(String[] args) {
			SpringApplication.run(TestApplication.class, args);
		}
	}

	/**
	 * Tests the AemAfSubmitProcessor.  It utilizes an SSL connection to test the SslBundle code.
	 * 
	 */
	@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, 
					classes = {TestApplication.class, AfSubmitAemProxyProcessor.class},
					properties = {
						"debug",
						"fluentforms.aem.servername=" + "localhost", 
						"fluentforms.aem.port=" + "8502", 
						"fluentforms.aem.user=admin",		 
						"fluentforms.aem.password=admin",
						"fluentforms.aem.useSsl=true",
						"spring.ssl.bundle.jks.aem.truststore.location=file:src/test/resources/aemforms.p12",
						"spring.ssl.bundle.jks.aem.truststore.password=Pa$$123",
						"spring.ssl.bundle.jks.aem.truststore.type=PKCS12"
						}
					)
	public static class AemProxyAfSubmissionTestWithAemAfSubmitProcessorTest {

		@RegisterExtension
	    static WireMockExtension wm1 = WireMockExtension.newInstance()
	            .options(WireMockConfiguration.wireMockConfig().httpsPort(8502)
	            											   .httpDisabled(true)
	            											   .keystorePath("src/test/resources/aemforms.p12")
	            											   .keyManagerPassword("Pa$$123")
	            											   .keystorePassword("Pa$$123")
	            											   .keystoreType("PKCS12")
	               		)
	            .configureStaticDsl(true)		// Use with Static DSL
	            .build();
		
		@LocalServerPort
		private int port;
		
//		private JakartaRestClient jrc;
		private RestClient restClient;
	
		@BeforeEach
		public void setUp() throws Exception {
//			jrc = setUpRestClient(port);
			restClient = createRestClient(port);
		}
	
		@Test
		void test() {
			// given
			String expectedResponseString = "<html><body>Dummy Response</body></html>";
			WireMock.stubFor(WireMock.post(AEM_SUBMIT_ADAPTIVE_FORM_SERVICE_PATH)	
					 .willReturn(WireMock.okForContentType("text/html", expectedResponseString))
			 );
			final FormDataMultiPart getPdfForm = mockFormData("foo", "bar");
	
			// when
			MultiValueMap<String, HttpEntity<?>> parts = getPdfForm.parts();
			ResponseEntity<byte[]> response = restClient.post()
					.uri(SUBMIT_ADAPTIVE_FORM_SERVICE_PATH)
//					.contentType(MediaType.MULTIPART_FORM_DATA)
					.body(parts)
					.accept(MediaType.APPLICATION_PDF)
					.retrieve()
					.toEntity(byte[].class)
					;
					
//					.target.path().request().accept(APPLICATION_PDF)
//					.post(Entity.entity(getPdfForm, getPdfForm.getMediaType()));
	
			// then
			assertThat(response, allOf(hasStatus(HttpStatus.OK), hasEntityMatching(equalTo(expectedResponseString.getBytes()))));
			WireMock.verify(
//						  	WireMock.postRequestedFor(WireMock.urlMatching(AEM_SUBMIT_ADAPTIVE_FORM_SERVICE_PATH))
//						  		    .withAnyRequestBodyPart(WireMock.aMultipart("jcr:data").withBody(WireMock.equalTo("bar")))
				  	WireMock.postRequestedFor(WireMock.urlMatching(AEM_SUBMIT_ADAPTIVE_FORM_SERVICE_PATH))
//				  	.withAnyRequestBodyPart(WireMock.aMultipart("jcr:data"))
					);
			
			System.out.println("Writing to: " + SUBMIT_ADAPTIVE_FORM_SERVICE_PATH);
			LoggedRequest loggedRequest = WireMock.findAll(WireMock.postRequestedFor(WireMock.urlEqualTo(AEM_SUBMIT_ADAPTIVE_FORM_SERVICE_PATH))).get(0);
			String requestBody = loggedRequest.getBodyAsString();
			System.out.println("Request Body:\n" + requestBody);
			loggedRequest.getAllHeaderKeys().forEach(headerName -> {
				System.out.println("Header: " + headerName + " = " + loggedRequest.getHeader(headerName));
			});
		}
		
	}

	/**
	 * Tests the AemLocalSubmitProcessor
	 * 
	 */
	@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, 
					classes = {TestApplication.class, AfSubmitLocalProcessor.class, MockAemProxy.class,
							   AemProxyAfSubmissionTestWithLocalAfSubmitProcessorTest.MockSubmissionProcessor.class, 
							   AemProxyAfSubmissionTestWithLocalAfSubmitProcessorTest.MockSubmissionProcessor2.class}
					,properties={
//							"debug", 
							"logging.level.com._4point.aem.fluentforms.spring=DEBUG"
						}
					)
	public static class AemProxyAfSubmissionTestWithLocalAfSubmitProcessorTest {
		private static final String AF_SUBMIT_LOCAL_PROCESSOR_RESPONSE = "AfSubmitLocalProcessor Response";

		private final static Logger logger = LoggerFactory.getLogger(AemProxyAfSubmissionTestWithLocalAfSubmitProcessorTest.class);

		@LocalServerPort
		private int port;

//		private JakartaRestClient jrc;
		private RestClient restClient;

		@BeforeEach
		public void setUp() throws Exception {
//			jrc = setUpRestClient(port);
			restClient = createRestClient(port);
		}


		@Test
		void testResponse() {
			final FormDataMultiPart getPdfForm = mockFormData("foo1", "bar");

			ResponseEntity<byte[]> response = restClient.post()
					.uri(SUBMIT_ADAPTIVE_FORM_SERVICE_PATH)
					.contentType(MediaType.MULTIPART_FORM_DATA)
					.body(getPdfForm.parts())
					.accept(MediaType.TEXT_PLAIN)
					.retrieve()
					.toEntity(byte[].class)
					;

//			Response response = jrc.target
//								   .path(SUBMIT_ADAPTIVE_FORM_SERVICE_PATH)
//								   .request()
//								   .accept(MediaType.TEXT_PLAIN_TYPE)
//								   .post(Entity.entity(getPdfForm, getPdfForm.getMediaType()));

			assertThat(response, allOf(hasStatus(HttpStatus.OK),hasEntityMatching(equalTo(AF_SUBMIT_LOCAL_PROCESSOR_RESPONSE.getBytes()))));
		}
		
		@Test
		void testRedirect() {
			final FormDataMultiPart getPdfForm = mockFormData("foo2", "bar");

			ResponseEntity<byte[]> response = restClient.post()
					.uri(SUBMIT_ADAPTIVE_FORM_SERVICE_PATH)
					.contentType(MediaType.MULTIPART_FORM_DATA)
					.body(getPdfForm.parts())
					.accept(MediaType.TEXT_PLAIN)
					.retrieve()
					.toEntity(byte[].class)
					;
//			Response response = jrc.target
//								   .path(SUBMIT_ADAPTIVE_FORM_SERVICE_PATH)
//								   .request()
//								   .accept(MediaType.TEXT_PLAIN_TYPE)
//								   .post(Entity.entity(getPdfForm, getPdfForm.getMediaType()));

			assertThat(response, allOf(hasStatus(HttpStatus.TEMPORARY_REDIRECT), doesNotHaveEntity()));
		}
		
		@Test
		void testSeeOther() {
			final FormDataMultiPart getPdfForm = mockFormData("foo3", "bar");

			ResponseEntity<byte[]> response = restClient.post()
					.uri(SUBMIT_ADAPTIVE_FORM_SERVICE_PATH)
					.contentType(MediaType.MULTIPART_FORM_DATA)
					.body(getPdfForm.parts())
					.accept(MediaType.TEXT_PLAIN)
					.retrieve()
					.toEntity(byte[].class)
					;
//			Response response = jrc.target
//								   .path(SUBMIT_ADAPTIVE_FORM_SERVICE_PATH)
//								   .request()
//								   .accept(MediaType.TEXT_PLAIN_TYPE)
//								   .post(Entity.entity(getPdfForm, getPdfForm.getMediaType()));

			assertThat(response, allOf(hasStatus(HttpStatus.SEE_OTHER), doesNotHaveEntity()));
		}
		
		@Test
		void testProxy() {
			final FormDataMultiPart getPdfForm = mockFormData("foo2", "bar");

			ResponseEntity<byte[]> response = restClient.post()
					.uri(SUBMIT_ADAPTIVE_FORM_SERVICE_PATH+"anythingElse")
					.contentType(MediaType.MULTIPART_FORM_DATA)
					.body(getPdfForm.parts())
					.accept(MediaType.TEXT_PLAIN)
					.retrieve()
					.toEntity(byte[].class)
					;

//			Response response = jrc.target
//								   .path(SUBMIT_ADAPTIVE_FORM_SERVICE_PATH+"anythingElse")
//								   .request()
//								   .accept(MediaType.TEXT_PLAIN_TYPE)
//								   .post(Entity.entity(getPdfForm, getPdfForm.getMediaType()));

			assertThat(response, allOf(hasStatus(HttpStatus.OK), doesNotHaveEntity()));
		}
		
		@Component
		public static class MockSubmissionProcessor implements AfSubmissionHandler {

			@Override
			public boolean canHandle(String formName) {
				logger.atDebug().log(()->"I can handle form name '" + formName + "'!!!!");
				assertEquals(AF_TEMPLATE_NAME, formName);
				return true;	// Can always handle.
			}
			

			@Override
			public SubmitResponse processSubmission(Submission submission) {
				// Validate the arguments passed in.

				assertAll(
						()->assertEquals(AF_TEMPLATE_NAME, submission.formName()),
						()->assertEquals("bar", submission.formData()),
						()->assertThat(submission.redirectUrl(), anyOf(equalTo("foo1"), equalTo("foo2"), equalTo("foo3"))),
						()->assertEquals(MediaType.TEXT_PLAIN, submission.headers().getFirst("accept")),
						()->assertTrue(MediaType.MULTIPART_FORM_DATA.isCompatibleWith(MediaType.valueOf(submission.headers().getFirst("content-type"))))
						);
				try {
					String redirectUrl = submission.redirectUrl();
					return switch(redirectUrl) {
						case "foo1" -> new SubmitResponse.Response(AF_SUBMIT_LOCAL_PROCESSOR_RESPONSE.getBytes(), "text/plain");
						case "foo2" -> new SubmitResponse.Redirect(new URI("http://localhost/"));
						case "foo3" -> new SubmitResponse.SeeOther(new URI("http://localhost/"));
						default -> throw new UnsupportedOperationException("Unexpected value in redirectUrl (%s)".formatted(redirectUrl));
					};
				} catch (URISyntaxException e) {
					throw new IllegalStateException("Bad URI -- ", e);
				}
			}
		}

		@Component
		public static class MockSubmissionProcessor2 implements AfSubmissionHandler {

			@Override
			public boolean canHandle(String formName) {
				return false;	// Can never handle.
			}
			

			@Override
			public SubmitResponse processSubmission(Submission submission) {
				fail("MockSubmissionProcessor2.processSubmission should never be called");
				return null;
			}
		}

		@Configuration
		public static class MockAemProxy {
			@Bean()
			public InternalAfSubmitAemProxyProcessor aemProxyProcessor() {
				AfSubmitAemProxyProcessor mock = Mockito.mock(AfSubmitAemProxyProcessor.class);
				Mockito.when(mock.processRequest(Mockito.any(), Mockito.any())).thenReturn(ResponseEntity.ok().build());
				return ()->mock;
			}
		}
	}

	/**
	 * Tests a custom AfSubmitProcessor
	 * 
	 */
	@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, 
					classes = {TestApplication.class, AemProxyAfSubmissionTestWithCustomAfSubmitProcessorTest.MockSubmitProcessor.class}
//					,properties="debug"
					)
	public static class AemProxyAfSubmissionTestWithCustomAfSubmitProcessorTest {
		
		@LocalServerPort
		private int port;

//		private JakartaRestClient jrc;
		private RestClient restClient;

		@BeforeEach
		public void setUp() throws Exception {
//			jrc = setUpRestClient(port);
			restClient = createRestClient(port);
		}

		@Test
		void test() {
			final FormDataMultiPart getPdfForm = mockFormData("foo", "bar");
			
			MultiValueMap<String, HttpEntity<?>> parts = getPdfForm.parts();
			ResponseEntity<byte[]> response = restClient.post()
					.uri(SUBMIT_ADAPTIVE_FORM_SERVICE_PATH)
					.contentType(MediaType.MULTIPART_FORM_DATA)
					.body(parts)
					.accept(MediaType.APPLICATION_PDF)
					.retrieve()
					.toEntity(byte[].class)
					;
//			Response response = jrc.target
//								   .path(SUBMIT_ADAPTIVE_FORM_SERVICE_PATH)
//								   .request()
//								   .accept(APPLICATION_PDF)
//								   .post(Entity.entity(getPdfForm, getPdfForm.getMediaType()));

			assertThat(response, allOf(hasStatus(HttpStatus.OK), hasEntityMatching(equalTo(SAMPLE_RESPONSE_BODY.getBytes()))));
		}

		@Component
		public static class MockSubmitProcessor implements SpringAfSubmitProcessor {

			@Override
			public ResponseEntity<byte[]> processRequest(MultipartHttpServletRequest inFormData, String remainder) {
				return ResponseEntity.ok().body(SAMPLE_RESPONSE_BODY.getBytes());
			}
		}
	}
	
	public static class SubmitResponseResponseTests {
		private static final String SAMPLE_TEXT = "text";
		private static final byte[] SAMPLE_TEXT_BYTES = SAMPLE_TEXT.getBytes(StandardCharsets.UTF_8);
		
		enum TestScenario {
			TEXT("text/plain", SubmitResponse.Response::text),
			HTML("text/html", SubmitResponse.Response::html),
			JSON("application/json", SubmitResponse.Response::json),
			XML("application/xml", SubmitResponse.Response::xml)
			;
			final String expectedContentType;
			final Function<String, SubmitResponse.Response> methodUnderTest;

			private TestScenario(String expectedContentType, Function<String, SubmitResponse.Response> methodUnderTest) {
				this.expectedContentType = expectedContentType;
				this.methodUnderTest = methodUnderTest;
			}
		}
		
		@ParameterizedTest
		@EnumSource
		void testResponseCreationMethod(TestScenario scenario) {
			var result = scenario.methodUnderTest.apply(SAMPLE_TEXT);
			assertAll(
					()->assertArrayEquals(SAMPLE_TEXT_BYTES, result.responseBytes()),
					()->assertEquals(scenario.expectedContentType, result.mediaType())
					);
		}
	}
	
	public static class AfSubmissionHandlerTests {
		// In this class we test the static convenience methods that generate AfSubmissionHandlers.  Since the 
		// processSubmission logic is one line (and trivial) we don't test it, however we do test the canHandle()
		// method generated by the convenience method.
		
		@ParameterizedTest
		@CsvSource({
				"formName, true",
				"notFormName, false"
				})
		void testcanHandleFormNameEquals(String formNameIn, boolean expectedResult) {
			var underTest = AfSubmissionHandler.canHandleFormNameEquals("formName", t->null);
			assertEquals(expectedResult, underTest.canHandle(formNameIn));
		}
		
		@DisplayName("Passing in null should produce a null pointer exception")
		@Test
		void testcanHandleFormNameEquals_Null() {
			NullPointerException ex = assertThrows(NullPointerException.class, ()->AfSubmissionHandler.canHandleFormNameEquals(null, t->null));
			assertThat(ex, exceptionMsgContainsAll("Form Name for submission handler cannot be null"));
		}
		
		@ParameterizedTest
		@CsvSource({
				"formName1, true",
				"formName2, true",
				"notFormName, false"
				})
		void testcanHandleFormNameAnyOf(String formNameIn, boolean expectedResult) {
			var underTest = AfSubmissionHandler.canHandleFormNameAnyOf(t->null, "formName1", "formName2");
			assertEquals(expectedResult, underTest.canHandle(formNameIn));
		}

		@ParameterizedTest
		@CsvSource({
				"formName1, false",
				"formName2, false",
				"notFormName, false"
				})
		void testcanHandleFormNameAnyOf_NoNames(String formNameIn, boolean expectedResult) {
			var underTest = AfSubmissionHandler.canHandleFormNameAnyOf(t->null);
			assertEquals(expectedResult, underTest.canHandle(formNameIn));
		}
		
		@ParameterizedTest
		@CsvSource({
				"formName1, true",
				"formName2, true",
				"notFormName, false"
				})
		void testcanHandleFormNameAnyOf_List(String formNameIn, boolean expectedResult) {
			var underTest = AfSubmissionHandler.canHandleFormNameAnyOf(List.of("formName1", "formName2"), t->null);
			assertEquals(expectedResult, underTest.canHandle(formNameIn));
		}

		@ParameterizedTest
		@CsvSource({
				"formName1, false",
				"formName2, false",
				"notFormName, false"
				})
		void testcanHandleFormNameAnyOf__List_NoNames(String formNameIn, boolean expectedResult) {
			var underTest = AfSubmissionHandler.canHandleFormNameAnyOf(List.of(), t->null);
			assertEquals(expectedResult, underTest.canHandle(formNameIn));
		}
		
		@ParameterizedTest
		@CsvSource({
				"formName1, true",
				"formName2, true",
				"notFormName, false"
				})
		void testcanHandleFormNameMatchesRegEx(String formNameIn, boolean expectedResult) {
			var underTest = AfSubmissionHandler.canHandleFormNameMatchesRegex("formName.*", t->null);
			assertEquals(expectedResult, underTest.canHandle(formNameIn));
		}
	}
	
	private static Matcher<HttpStatusCode> isStatus(HttpStatusCode statusCode) {
		return new TypeSafeDiagnosingMatcher<HttpStatusCode>() {

			@Override
			public void describeTo(Description description) {
				description.appendText("HttpStatus with value " + statusCode.value());				
			}

			@Override
			protected boolean matchesSafely(HttpStatusCode item, Description mismatchDescription) {
				if (statusCode.isSameCodeAs(item)) {
					return true;
				} else {
					mismatchDescription.appendText("was HttpStatus with value " + item.value());
					return false;
				}
			}
		};
	}
	
	private static Matcher<ResponseEntity<?>> doesNotHaveEntity() {
		return new TypeSafeDiagnosingMatcher<ResponseEntity<?>>() {

			@Override
			public void describeTo(Description description) {
				description.appendText("ResponseEntity with no body");				
			}

			@Override
			protected boolean matchesSafely(ResponseEntity<?> item, Description mismatchDescription) {
				if (item.hasBody() == true) {
					return true;
				} else {
					mismatchDescription.appendText("was ResponseEntity with body of size " + ((byte[])item.getBody()).length);
					return false;
				}
			}
			
		};
	}


	private static Matcher<ResponseEntity<?>> hasStatus(HttpStatusCode status) {
		return new FeatureMatcher<ResponseEntity<?>, HttpStatusCode>(isStatus(status), "ResponseEntity with status", "status") {

			@Override
			protected HttpStatusCode featureValueOf(ResponseEntity<?> actual) {
				return actual.getStatusCode();
			}
		};
	}
	
	private static Matcher<ResponseEntity<?>> hasEntityMatching(Matcher<? super byte[]> matcher) {
		return new FeatureMatcher<ResponseEntity<?>, byte[]>(matcher, "ResponseEntity with entity matching", "entity") {

			@Override
			protected byte[] featureValueOf(ResponseEntity<?> actual) {
				return (byte[]) actual.getBody();
			}
		};
	}
}
