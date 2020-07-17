package com._4point.aem.docservices.rest_services.it_tests.server.af;

import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.SAMPLE_FORM_XDP;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.SERVER_FORMS_DIR;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.TEST_MACHINE_NAME;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.TEST_MACHINE_PORT_STR;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.TEST_USER;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.TEST_USER_PASSWORD;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.ws.rs.client.Client;
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
	private static final String DATA_SERVICE_DATA_PARAM = "Data";
	private static final String DATA_PARAM_NAME = "data";
	private static final String DATA_KEY_PARAM = "dataKey";
	private static final String RENDER_ADAPTIVE_FORM_URL = "http://" + TEST_MACHINE_NAME + ":" + TEST_MACHINE_PORT_STR + "/services/AdaptiveForms/RenderAdaptiveForm";
	private static final String DATA_CACHE_SERVICE_URL = "http://" + TEST_MACHINE_NAME + ":" + TEST_MACHINE_PORT_STR + "/services/DataServices/DataCache";
	private static final String SAMPLE_AF_NAME = "sample00002test";
	private static final boolean SAVE_RESULTS = false;
	
	private WebTarget renderAfTarget;
	private WebTarget dataCacheTarget;

	@BeforeEach
	void setUp() throws Exception {
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(TEST_USER, TEST_USER_PASSWORD);	// default AEM passwords
		Client client = ClientBuilder.newClient()
							  .register(feature)
							  .register(MultiPartFeature.class);
		renderAfTarget = client.target(RENDER_ADAPTIVE_FORM_URL);
		dataCacheTarget = client.target(DATA_CACHE_SERVICE_URL);
	}

	@Test
	void testRenderAdaptiveForm_FormRef() throws IOException {
		Response result = renderAfTarget
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
		
	// This does not currently work.  I'm not sure why but I think it's because of the way the form is configured rather than
	// because the code isn't working (I can't get a manual test using the browser and the dataRef attribute to work either).
	@Disabled
	void testRenderAdaptiveForm_FormRefAndData() throws IOException {
		Path sampleFormDataPath = TestUtils.SAMPLE_FORM_DATA_XML;
		String dataKey = postDataToDataCacheService(sampleFormDataPath);
		
		Response result = renderAfTarget
				.queryParam(TEMPLATE_PARAM_NAME, SAMPLE_AF_NAME)
				.queryParam(DATA_KEY_PARAM, dataKey)
				.request()
				.accept(MediaType.TEXT_HTML_TYPE)
				.get();
					
		assertTrue(result.hasEntity(), "Expected the response to have an entity.");
		assertEquals(Response.Status.OK.getStatusCode(), result.getStatus(), ()->"Expected response to be 'OK', entity='" + TestUtils.readEntityToString(result) + "'.");
		byte[] resultBytes = IOUtils.toByteArray((InputStream)result.getEntity());
		if (SAVE_RESULTS) {
			IOUtils.write(resultBytes, Files.newOutputStream(TestUtils.ACTUAL_RESULTS_DIR.resolve("testRenderAdaptiveForm_FormRefAndData_result.html")));
		}
		assertTrue(MediaType.TEXT_HTML_TYPE.isCompatible(MediaType.valueOf(result.getHeaderString(HttpHeaders.CONTENT_TYPE))));
		assertThat(new String(resultBytes, StandardCharsets.UTF_8), allOf(containsString("Text Field1 Data"), containsString("Text Field2 Data")));
	}

	// This is just used to load in some data into the DataCache service for manual testing.
	// It should not be executed as part of an automated test suite.
	@Disabled
	void postData() throws Exception {
		Path sampleFormDataPath = TestUtils.SAMPLE_FORM_DATA_XML;
		String dataKey = postDataToDataCacheService(sampleFormDataPath);
		System.out.println("Data stored.  DataKey='" + dataKey + "'.");
	}
	
	private String postDataToDataCacheService(Path sampleFormDataPath) throws IOException {
		String dataKey = null;
		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			multipart.field(DATA_SERVICE_DATA_PARAM, sampleFormDataPath.toFile(), MediaType.APPLICATION_XML_TYPE);

			Response result = dataCacheTarget
					.request()
					.accept(MediaType.TEXT_PLAIN_TYPE)
					.post(Entity.entity(multipart, multipart.getMediaType()));
						
			assertTrue(result.hasEntity(), "Expected the response to have an entity.");
			assertEquals(Response.Status.OK.getStatusCode(), result.getStatus(), ()->"Expected response to be 'OK', entity='" + TestUtils.readEntityToString(result) + "'.");
			byte[] resultBytes = IOUtils.toByteArray((InputStream)result.getEntity());
			if (SAVE_RESULTS) {
				IOUtils.write(resultBytes, Files.newOutputStream(TestUtils.ACTUAL_RESULTS_DIR.resolve("testRenderAdaptiveForm_FormRefAndData_DataKey_result.txt")));
			}
			assertTrue(MediaType.TEXT_PLAIN_TYPE.isCompatible(MediaType.valueOf(result.getHeaderString(HttpHeaders.CONTENT_TYPE))));
			dataKey = new String(resultBytes, StandardCharsets.UTF_8);
		}
		
		assertNotNull(dataKey);
		return dataKey;
	}


}
