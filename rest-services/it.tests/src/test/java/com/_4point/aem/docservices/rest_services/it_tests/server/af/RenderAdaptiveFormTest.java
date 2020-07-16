package com._4point.aem.docservices.rest_services.it_tests.server.af;

import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.SAMPLE_FORM_XDP;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.SERVER_FORMS_DIR;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.TEST_MACHINE_NAME;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.TEST_MACHINE_PORT_STR;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.TEST_USER;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.TEST_USER_PASSWORD;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com._4point.aem.docservices.rest_services.it_tests.TestUtils;

public class RenderAdaptiveFormTest {

	private static final String TEMPLATE_PARAM_NAME = "template";
	private static final String DATA_PARAM_NAME = "data";
	private static final String RENDER_ADAPTIVE_FORM_URL = "http://" + TEST_MACHINE_NAME + ":" + TEST_MACHINE_PORT_STR + "/services/AdaptiveForms/RenderAdaptiveForm";
	private static final String SAMPLE_AF_NAME = "sample00002test";
	private static final boolean SAVE_RESULTS = true;
	
	private WebTarget target;

	@BeforeEach
	void setUp() throws Exception {
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(TEST_USER, TEST_USER_PASSWORD);	// default AEM passwords
		target = ClientBuilder.newClient()
							  .register(feature)
							  .register(MultiPartFeature.class)
							  .target(RENDER_ADAPTIVE_FORM_URL);
	}

	@Test
	void testRenderAdaptiveForm_FormRef() throws IOException {
		Response result = target
				.queryParam(TEMPLATE_PARAM_NAME, SAMPLE_AF_NAME)
				.request()
				.accept(MediaType.TEXT_HTML_TYPE)
				.get();
					
		assertTrue(result.hasEntity(), "Expected the response to have an entity.");
		assertEquals(Response.Status.OK.getStatusCode(), result.getStatus(), ()->"Expected response to be 'OK', entity='" + TestUtils.readEntityToString(result) + "'.");
		byte[] resultBytes = IOUtils.toByteArray((InputStream)result.getEntity());
		if (SAVE_RESULTS) {
			IOUtils.write(resultBytes, Files.newOutputStream(TestUtils.ACTUAL_RESULTS_DIR.resolve("testRenderAdaptiveForm_FormRef_result.html")));
		}
		assertTrue(MediaType.TEXT_HTML_TYPE.isCompatible(MediaType.valueOf(result.getHeaderString(HttpHeaders.CONTENT_TYPE))));
		assertThat(new String(resultBytes, StandardCharsets.UTF_8), not(anyOf(containsString("Text Field1 Data"), containsString("Text Field2 Data"))));
	}
		
	@Disabled
	void testRenderAdaptiveForm_FormRefAndData() throws IOException {
		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			multipart.field(TEMPLATE_PARAM_NAME, SAMPLE_AF_NAME);

			Response result = target
					.queryParam(TEMPLATE_PARAM_NAME, SAMPLE_AF_NAME)
					.request()
					.accept(MediaType.TEXT_HTML_TYPE)
					.get();
						
			assertTrue(result.hasEntity(), "Expected the response to have an entity.");
			assertEquals(Response.Status.OK.getStatusCode(), result.getStatus(), ()->"Expected response to be 'OK', entity='" + TestUtils.readEntityToString(result) + "'.");
			byte[] resultBytes = IOUtils.toByteArray((InputStream)result.getEntity());
			if (SAVE_RESULTS) {
				IOUtils.write(resultBytes, Files.newOutputStream(TestUtils.ACTUAL_RESULTS_DIR.resolve("testRenderAdaptiveForm_FormRef_result.html")));
			}
			assertTrue(MediaType.TEXT_HTML_TYPE.isCompatible(MediaType.valueOf(result.getHeaderString(HttpHeaders.CONTENT_TYPE))));
			assertThat(new String(resultBytes, StandardCharsets.UTF_8), not(anyOf(containsString("Text Field1 Data"), containsString("Text Field2 Data"))));
		}
		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			multipart.field(TEMPLATE_PARAM_NAME, SAMPLE_AF_NAME);

			Response result = target
					.queryParam(TEMPLATE_PARAM_NAME, SAMPLE_AF_NAME)
					.request()
					.accept(MediaType.TEXT_HTML_TYPE)
					.get();
						
			assertTrue(result.hasEntity(), "Expected the response to have an entity.");
			assertEquals(Response.Status.OK.getStatusCode(), result.getStatus(), ()->"Expected response to be 'OK', entity='" + TestUtils.readEntityToString(result) + "'.");
			byte[] resultBytes = IOUtils.toByteArray((InputStream)result.getEntity());
			if (SAVE_RESULTS) {
				IOUtils.write(resultBytes, Files.newOutputStream(TestUtils.ACTUAL_RESULTS_DIR.resolve("testRenderAdaptiveForm_FormRef_result.html")));
			}
			assertTrue(MediaType.TEXT_HTML_TYPE.isCompatible(MediaType.valueOf(result.getHeaderString(HttpHeaders.CONTENT_TYPE))));
			assertThat(new String(resultBytes, StandardCharsets.UTF_8), not(anyOf(containsString("Text Field1 Data"), containsString("Text Field2 Data"))));
		}
	}


}
