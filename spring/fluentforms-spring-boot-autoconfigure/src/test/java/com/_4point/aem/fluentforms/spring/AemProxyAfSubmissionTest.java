package com._4point.aem.fluentforms.spring;

import static com._4point.aem.fluentforms.spring.AemProxyAfSubmissionTest.TestApplication.JerseyConfig;
import static com._4point.testing.matchers.jaxrs.ResponseMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.net.URISyntaxException;

import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.stereotype.Component;

import com._4point.aem.fluentforms.spring.AemProxyAfSubmission.AfSubmitAemProxyProcessor;
import com._4point.aem.fluentforms.spring.AemProxyAfSubmission.AfSubmitLocalProcessor;
import com._4point.aem.fluentforms.spring.AemProxyAfSubmission.AfSubmissionHandler;
import com._4point.aem.fluentforms.spring.AemProxyAfSubmission.AfSubmitProcessor;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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
	public static final MediaType APPLICATION_PDF = new MediaType("application", "pdf");
	private static final String SAMPLE_RESPONSE_BODY = "body";

	record JakartaRestClient(WebTarget target, URI uri) {};

	public static JakartaRestClient setUpRestClient(int port) {
		var uri = getBaseUri(port);
		var target = ClientBuilder.newClient() //newClient(clientConfig)
				 .property(ClientProperties.FOLLOW_REDIRECTS, Boolean.FALSE)	// Disable re-directs so that we can test for "thank you page" redirection.
				 .register(MultiPartFeature.class)
				 .target(uri);
		return new JakartaRestClient(target, uri);
	}


	/* package */ static FormDataMultiPart mockFormData(String redirect, String data) {
		final FormDataMultiPart getPdfForm = new FormDataMultiPart();
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

	private static String getBaseUriString(int port) {
		return getBaseUri(port).toString();
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

		@Component
		public static class JerseyConfig extends ResourceConfig {
		}
	}

	/**
	 * Tests the AemAfSubmitProcessor
	 * 
	 */
	@WireMockTest(httpPort = 4502)
	@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, 
					classes = {TestApplication.class, JerseyConfig.class, AfSubmitAemProxyProcessor.class},
					properties = {
//						"debug",
						"fluentforms.aem.servername=" + "localhost", 
						"fluentforms.aem.port=" + "4502", 
						"fluentforms.aem.user=admin",		 
						"fluentforms.aem.password=admin",
						}
					)
	public static class AemProxyAfSubmissionTestWithAemAfSubmitProcessorTest {

		@LocalServerPort
		private int port;
		
		private JakartaRestClient jrc;
	
		@BeforeEach
		public void setUp() throws Exception {
			jrc = setUpRestClient(port);
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
			Response response = jrc.target.path(SUBMIT_ADAPTIVE_FORM_SERVICE_PATH).request().accept(APPLICATION_PDF)
					.post(Entity.entity(getPdfForm, getPdfForm.getMediaType()));
	
			// then
			assertThat(response, allOf(isStatus(Response.Status.OK),hasEntityMatching(equalTo(expectedResponseString.getBytes()))));
			WireMock.verify(
						  	WireMock.postRequestedFor(WireMock.urlEqualTo(AEM_SUBMIT_ADAPTIVE_FORM_SERVICE_PATH))
						  		    .withAnyRequestBodyPart(WireMock.aMultipart("jcr:data").withBody(WireMock.equalTo("bar")))
					);
		}
		
	}

	/**
	 * Tests the AemLocalSubmitProcessor
	 * 
	 */
	@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, 
					classes = {TestApplication.class, JerseyConfig.class, AfSubmitLocalProcessor.class, 
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

		private JakartaRestClient jrc;

		@BeforeEach
		public void setUp() throws Exception {
			jrc = setUpRestClient(port);
		}


		@Test
		void testResponse() {
			final FormDataMultiPart getPdfForm = mockFormData("foo1", "bar");

			Response response = jrc.target
								   .path(SUBMIT_ADAPTIVE_FORM_SERVICE_PATH)
								   .request()
								   .accept(MediaType.TEXT_PLAIN_TYPE)
								   .post(Entity.entity(getPdfForm, getPdfForm.getMediaType()));

			assertThat(response, allOf(isStatus(Response.Status.OK),hasEntityMatching(equalTo(AF_SUBMIT_LOCAL_PROCESSOR_RESPONSE.getBytes()))));
		}
		
		@Test
		void testRedirect() {
			final FormDataMultiPart getPdfForm = mockFormData("foo2", "bar");

			Response response = jrc.target
								   .path(SUBMIT_ADAPTIVE_FORM_SERVICE_PATH)
								   .request()
								   .accept(MediaType.TEXT_PLAIN_TYPE)
								   .post(Entity.entity(getPdfForm, getPdfForm.getMediaType()));

			assertThat(response, allOf(isStatus(Response.Status.TEMPORARY_REDIRECT), doesNotHaveEntity()));
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
						()->assertThat(submission.redirectUrl(), anyOf(equalTo("foo1"), equalTo("foo2"))),
						()->assertEquals(MediaType.TEXT_PLAIN, submission.headers().getFirst("accept")),
						()->assertTrue(MediaType.MULTIPART_FORM_DATA_TYPE.isCompatible(MediaType.valueOf(submission.headers().getFirst("content-type"))))
						);
				try {
					return "foo2".equals(submission.redirectUrl())	? new SubmitResponse.Redirect(new URI("http://localhost/"))
																	: new SubmitResponse.Response(AF_SUBMIT_LOCAL_PROCESSOR_RESPONSE.getBytes(), "text/plain");
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

	}

	/**
	 * Tests a custom AfSubmitProcessor
	 * 
	 */
	@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, 
					classes = {TestApplication.class, JerseyConfig.class, AemProxyAfSubmissionTestWithCustomAfSubmitProcessorTest.MockSubmitProcessor.class}
//					,properties="debug"
					)
	public static class AemProxyAfSubmissionTestWithCustomAfSubmitProcessorTest {
		
		@LocalServerPort
		private int port;

		private JakartaRestClient jrc;

		@BeforeEach
		public void setUp() throws Exception {
			jrc = setUpRestClient(port);
		}

		@Test
		void test() {
			final FormDataMultiPart getPdfForm = mockFormData("foo", "bar");
			
			Response response = jrc.target
								   .path(SUBMIT_ADAPTIVE_FORM_SERVICE_PATH)
								   .request()
								   .accept(APPLICATION_PDF)
								   .post(Entity.entity(getPdfForm, getPdfForm.getMediaType()));

			assertThat(response, allOf(isStatus(Response.Status.OK), hasEntityMatching(equalTo(SAMPLE_RESPONSE_BODY.getBytes()))));
		}

		@Component
		public static class MockSubmitProcessor implements AfSubmitProcessor {

			@Override
			public Response processRequest(FormDataMultiPart inFormData, HttpHeaders headers, String remainder) {
				return Response.ok().entity(SAMPLE_RESPONSE_BODY).build();
			}
		}
	}
	
}
