package com._4point.aem.docservices.rest_services.it_tests.server.workflow;

import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.TEST_MACHINE_AEM_TYPE;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.TEST_MACHINE_NAME;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.TEST_MACHINE_PORT_STR;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.TEST_USER;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.TEST_USER_PASSWORD;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com._4point.aem.docservices.rest_services.it_tests.TestUtils;

class StartWorkflowTest {
	private static final String START_WORKFLOW_PATH = TEST_MACHINE_AEM_TYPE.pathPrefix() + "/services/WorkflowService/StartWorkflow";
	private static final String START_WORKFLOW_URL = "http://" + TEST_MACHINE_NAME + ":" + TEST_MACHINE_PORT_STR + START_WORKFLOW_PATH;
	private static final MediaType APPLICATION_PDF = new MediaType("application", "pdf");
	private static final MediaType APPLICATION_XDP = new MediaType("application", "vnd.adobe.xdp+xml");

	private static final boolean SAVE_OUTPUT = false;
	
	private WebTarget target;

	@BeforeEach
	void setUp() throws Exception {
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(TEST_USER, TEST_USER_PASSWORD); // default AEM passwords
		target = ClientBuilder.newClient().register(feature).register(MultiPartFeature.class)
				.target(START_WORKFLOW_URL);
	}

	@Test
	void testStartWorkflow() throws Exception {
		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			Response result = target.request()
					.accept(APPLICATION_PDF)	// TODO:  Need to change this to what's appropriate
					.post(Entity.entity(multipart, multipart.getMediaType()));

			assertTrue(result.hasEntity(), "Expected the response to have an entity.");
			assertEquals(Response.Status.OK.getStatusCode(), result.getStatus(), () -> "Expected response to be 'OK', entity='" + TestUtils.readEntityToString(result) + "'.");
			// TODO:  Need to change this to what's appropriate
			assertEquals(APPLICATION_PDF, MediaType.valueOf(result.getHeaderString(HttpHeaders.CONTENT_TYPE)));
			
			byte[] resultBytes = IOUtils.toByteArray((InputStream) result.getEntity());			
		}
	}

}
