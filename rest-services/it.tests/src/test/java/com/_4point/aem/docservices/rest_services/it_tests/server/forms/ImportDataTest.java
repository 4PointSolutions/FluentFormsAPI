package com._4point.aem.docservices.rest_services.it_tests.server.forms;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.*;

import java.io.IOException;
import java.io.InputStream;
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
import org.junit.jupiter.api.Test;

import com._4point.aem.docservices.rest_services.it_tests.ByteArrayString;
import com._4point.aem.docservices.rest_services.it_tests.TestUtils;

class ImportDataTest {

	private static final String PDF_PARAM_NAME = "pdf";
	private static final String DATA_PARAM_NAME = "data";
	private static final String IMPORT_DATA_URL = "http://" + TEST_MACHINE_NAME + ":" + TEST_MACHINE_PORT_STR + TEST_MACHINE_AEM_TYPE.pathPrefix() + "/services/FormsService/ImportData";
	private static final MediaType APPLICATION_PDF = new MediaType("application", "pdf");
	
	private static final boolean SAVE_RESULTS = false;
	
	private WebTarget target;

	@BeforeEach
	void setUp() throws Exception {
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(TEST_USER, TEST_USER_PASSWORD);	// default AEM passwords
		target = ClientBuilder.newClient()
							  .register(feature)
							  .register(MultiPartFeature.class)
							  .target(IMPORT_DATA_URL);
	}

	@Test
	void testImportData_Bytes() throws IOException {

		try (final FormDataMultiPart multipart = new FormDataMultiPart()
													.field(DATA_PARAM_NAME, TestUtils.SAMPLE_FORM_DATA_XML.toFile(), MediaType.APPLICATION_XML_TYPE)
													.field(PDF_PARAM_NAME, TestUtils.SAMPLE_FORM_PDF.toFile(), APPLICATION_PDF)) {
			
			Response result = target.request()
									  .accept(APPLICATION_PDF)
									  .post(Entity.entity(multipart, multipart.getMediaType()));
			
			assertTrue(result.hasEntity(), "Expected the response to have an entity.");
			assertEquals(Response.Status.OK.getStatusCode(), result.getStatus(), ()->"Expected response to be 'OK', entity='" + TestUtils.readEntityToString(result) + "'.");
			byte[] resultBytes = IOUtils.toByteArray((InputStream)result.getEntity());
			assertThat("Expected a PDF to be returned.", ByteArrayString.toString(resultBytes, 8), containsString("%, P, D, F, -, 1, ., 7"));
			if (SAVE_RESULTS) {
				IOUtils.write(resultBytes, Files.newOutputStream(TestUtils.ACTUAL_RESULTS_DIR.resolve("ImportDataServer_BytesResult.pdf")));
			}
			assertEquals(APPLICATION_PDF, MediaType.valueOf(result.getHeaderString(HttpHeaders.CONTENT_TYPE)));
		}
	}

	@Test
	void testImportData_Strings() throws IOException {

		
		// TODO: Set up multipart/form-data
		try (final FormDataMultiPart multipart = new FormDataMultiPart()
													.field(DATA_PARAM_NAME, "Data String")
													.field(PDF_PARAM_NAME, "PDF String")) {
			
			Response result = target.request()
									  .accept(APPLICATION_PDF)
									  .post(Entity.entity(multipart, multipart.getMediaType()));
			
			assertTrue(result.hasEntity(), "Expected the response to have an entity.");
			// For now this isn't supported.
			assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), result.getStatus(), ()->"Expected response to be 'Internal Status Error', entity='" + TestUtils.readEntityToString(result) + "'.");
		}
	}

}
