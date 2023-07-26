package com._4point.aem.fluentforms.spring;

import static com._4point.aem.fluentforms.spring.AemProxyAfSubmissionTest.TestApplication.JerseyConfig;
import static com._4point.aem.fluentforms.spring.AemProxyAfSubmissionTest.MockSubmitProcessor;
import static com._4point.testing.matchers.jaxrs.ResponseMatchers.isStatus;
import static com._4point.testing.matchers.jaxrs.ResponseMatchers.hasEntity;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;

import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.stereotype.Component;

import com._4point.aem.fluentforms.spring.AemProxyAfSubmission.AfSubmitProcessor;
import com._4point.aem.fluentforms.spring.AemProxyAutoConfigurationTest.TestApplication;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, 
				classes = {TestApplication.class, JerseyConfig.class, MockSubmitProcessor.class}
//				,properties = "debug"
				)
class AemProxyAfSubmissionTest {
	public static final String AF_TEMPLATE_NAME = "sample00002test";
	private static final String SUBMIT_ADAPTIVE_FORM_SERVICE_PATH = "/aem/content/forms/af/" + AF_TEMPLATE_NAME + "/jcr:content/guideContainer.af.submit.jsp";
	public static final MediaType APPLICATION_PDF = new MediaType("application", "pdf");

	@LocalServerPort
	private int port;

	private URI uri;

	private WebTarget target;

	@BeforeEach
	public void setUp() throws Exception {
		this.uri = getBaseUri(port);
		target = ClientBuilder.newClient() //newClient(clientConfig)
				 .property(ClientProperties.FOLLOW_REDIRECTS, Boolean.FALSE)	// Disable re-directs so that we can test for "thank you page" redirection.
				 .register(MultiPartFeature.class)
				 .target(this.uri);
	}


	@Test
	void test() {
		final FormDataMultiPart getPdfForm = mockFormData("foo", "bar");
		
		Response response = target
				 .path(SUBMIT_ADAPTIVE_FORM_SERVICE_PATH)
				 .request()
				 .accept(APPLICATION_PDF)
				 .post(Entity.entity(getPdfForm, getPdfForm.getMediaType()));

		assertThat(response, allOf(isStatus(Response.Status.OK), hasEntity()));
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
	
	@Component
	public static class MockSubmitProcessor implements AfSubmitProcessor {
		@Override
		public Response processRequest(FormDataMultiPart inFormData, HttpHeaders headers, String remainder) {
			return Response.ok().entity("body").build();
		}
	}
}
