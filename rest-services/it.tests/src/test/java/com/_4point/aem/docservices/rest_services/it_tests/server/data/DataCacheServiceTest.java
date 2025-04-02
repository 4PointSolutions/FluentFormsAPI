package com._4point.aem.docservices.rest_services.it_tests.server.data;

import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

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
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com._4point.aem.docservices.rest_services.it_tests.AemInstance;
import com._4point.aem.docservices.rest_services.it_tests.TestUtils;

@Tag("server-tests")
public class DataCacheServiceTest {
	private static final String DATA_KEY_PARAM = "DataKey";
	private static final String DATA_PARAM = "Data";
	private static final String DATA_CACHE_SERVICE_URL = "http://" + AemInstance.AEM_1.aemHost() + ":" + AemInstance.AEM_1.aemPort() + TEST_MACHINE_AEM_TYPE.pathPrefix() + "/services/DataServices/DataCache";

	private static final boolean SAVE_RESULTS = false;

	private WebTarget target;

	@BeforeAll
	static void setUpAll() throws Exception {
		AemInstance.AEM_1.prepareForTests();
	}

	@BeforeEach
	void setUp() throws Exception {
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(TEST_USER, TEST_USER_PASSWORD);	// default AEM passwords
		target = ClientBuilder.newClient()
							  .register(feature)
							  .register(MultiPartFeature.class)
							  .target(DATA_CACHE_SERVICE_URL);
	}

	@Test
	void testDataService_WriteRead() throws IOException {
		String dataKey = null;
		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			multipart.field(DATA_PARAM, TestUtils.SAMPLE_FORM_DATA_XML.toFile(), MediaType.APPLICATION_XML_TYPE);

			Response result = target
					.request()
					.accept(MediaType.TEXT_PLAIN_TYPE)
					.post(Entity.entity(multipart, multipart.getMediaType()));
						
			assertTrue(result.hasEntity(), "Expected the response to have an entity.");
			assertEquals(Response.Status.OK.getStatusCode(), result.getStatus(), ()->"Expected response to be 'OK', entity='" + TestUtils.readEntityToString(result) + "'.");
			byte[] resultBytes = IOUtils.toByteArray((InputStream)result.getEntity());
			if (SAVE_RESULTS) {
				IOUtils.write(resultBytes, Files.newOutputStream(TestUtils.ACTUAL_RESULTS_DIR.resolve("testDataService_WriteRead_WRITE_result.txt")));
			}
			assertTrue(MediaType.TEXT_PLAIN_TYPE.isCompatible(MediaType.valueOf(result.getHeaderString(HttpHeaders.CONTENT_TYPE))));
			dataKey = new String(resultBytes, StandardCharsets.UTF_8);
		}
		
		Response result = target
				.queryParam(DATA_KEY_PARAM, dataKey)
				.request()
				.accept(MediaType.TEXT_HTML_TYPE)
				.get();
					
		assertTrue(result.hasEntity(), "Expected the response to have an entity.");
		assertEquals(Response.Status.OK.getStatusCode(), result.getStatus(), ()->"Expected response to be 'OK', entity='" + TestUtils.readEntityToString(result) + "'.");
		byte[] resultBytes = IOUtils.toByteArray((InputStream)result.getEntity());
		if (SAVE_RESULTS) {
			IOUtils.write(resultBytes, Files.newOutputStream(TestUtils.ACTUAL_RESULTS_DIR.resolve("testDataService_WriteRead_READ_result.xml")));
		}
		assertTrue(MediaType.APPLICATION_XML_TYPE.isCompatible(MediaType.valueOf(result.getHeaderString(HttpHeaders.CONTENT_TYPE))));
		assertThat(new String(resultBytes, StandardCharsets.UTF_8), allOf(containsString("Text Field1 Data"), containsString("Text Field2 Data")));
	}
		

}
