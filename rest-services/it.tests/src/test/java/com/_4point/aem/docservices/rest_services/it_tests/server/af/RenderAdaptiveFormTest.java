package com._4point.aem.docservices.rest_services.it_tests.server.af;

import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com._4point.aem.docservices.rest_services.it_tests.AemInstance;
import com._4point.aem.docservices.rest_services.it_tests.TestUtils;

@Tag("server-tests")
public class RenderAdaptiveFormTest {

	private static final String TEMPLATE_PARAM_NAME = "template";
	private static final String DATA_SERVICE_DATA_PARAM = "Data";
	private static final String DATA_PARAM_NAME = "data";
	private static final String DATA_REF_PARAM = "dataRef";
//	private static final String RENDER_ADAPTIVE_FORM_URL = "http://" + TEST_MACHINE_NAME + ":" + TEST_MACHINE_PORT_STR + "/services/AdaptiveForms/RenderAdaptiveForm";
//	private static final String RENDER_ADAPTIVE_FORM_URL = "http://" + TEST_MACHINE_NAME + ":" + TEST_MACHINE_PORT_STR + "/services/AdaptiveForms/RenderAdaptiveForm";
	// 
	private static final String DATA_CACHE_SERVICE_URL = "http://" + AemInstance.AEM_1.aemHost() + ":" + AemInstance.AEM_1.aemPort() + TEST_MACHINE_AEM_TYPE.pathPrefix() + "/services/DataServices/DataCache";
	private static final String SAMPLE_AF_NAME = "sample00002test";
	private static final boolean SAVE_RESULTS = false;
	
	private Client client;
	private WebTarget dataCacheTarget;

	@BeforeAll
	static void setUpAll() throws Exception {
		AemInstance.AEM_1.prepareForTests();
	}

	@BeforeEach
	void setUp() throws Exception {
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(TEST_USER, TEST_USER_PASSWORD);	// default AEM passwords
		client = ClientBuilder.newClient()
							  .register(feature)
							  .register(MultiPartFeature.class);
		dataCacheTarget = client.target(DATA_CACHE_SERVICE_URL);
	}

	@Test
	void testRenderAdaptiveForm_FormRef() throws IOException {
		Response result = client.target(constructAfUrl(SAMPLE_AF_NAME))
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
	@Test
	void testRenderAdaptiveForm_FormRefAndData() throws IOException {
		Path sampleFormDataPath = TestUtils.LOCAL_SAMPLE_FORM_DATA_XML;
		String dataKey = postDataToDataCacheService(sampleFormDataPath);

		Response result = client.target(constructAfUrl(SAMPLE_AF_NAME))
				.queryParam("wcmmode", "disabled")
				.queryParam(DATA_REF_PARAM, constructDataRef(dataKey))
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
	@Test
	void postData() throws Exception {
		Path sampleFormDataPath = TestUtils.LOCAL_SAMPLE_FORM_DATA_XML;
		String dataKey = postDataToDataCacheService(sampleFormDataPath);
		System.out.println("Data stored.  DataKey='" + dataKey + "'.");
	}
	
	private String constructAfUrl(String formName) {
		return "http://" + AemInstance.AEM_1.aemHost() + ":" + AemInstance.AEM_1.aemPort() + TEST_MACHINE_AEM_TYPE.pathPrefix() + "/content/forms/af/" + formName + ".html";
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
			String contentType = result.getHeaderString(HttpHeaders.CONTENT_TYPE);
			assertTrue(MediaType.TEXT_PLAIN_TYPE.isCompatible(MediaType.valueOf(contentType)), "Unexpected content type returned from DataCache service.  Expected 'text/plain' but was '" + contentType + "'.");
			dataKey = new String(resultBytes, StandardCharsets.UTF_8);
		}
		
		assertNotNull(dataKey);
		return dataKey;
	}

	private static String constructDataRef(String key) {
		return "service://FFPrefillService/" + key;
	}

}
