package com._4point.aem.docservices.rest_services.it_tests.forms;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class ImportDataTest {

	private static final String PDF_PARAM_NAME = "pdf";
	private static final String DATA_PARAM_NAME = "data";
	private static final String IMPORT_DATA_URL = "http://localhost:4502/services/FormsService/ImportData";
	private static final MediaType APPLICATION_PDF = new MediaType("application", "pdf");
	
	private WebTarget target;

	@BeforeEach
	void setUp() throws Exception {
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("admin", "admin");	// default AEM passwords
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
			assertEquals(Response.Status.OK.getStatusCode(), result.getStatus(), ()->"Expected response to be 'OK', entity='" + readEntityToString(result) + "'.");
			byte[] resultBytes = IOUtils.toByteArray((InputStream)result.getEntity());
			assertThat("Expected a PDF to be returned.", ByteArrayString.toString(resultBytes, 8), containsString("%, P, D, F, -, 1, ., 7"));
			IOUtils.write(resultBytes, Files.newOutputStream(TestUtils.ACTUAL_RESULTS_DIR.resolve("ImportData_BytesResult.pdf")));
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
			assertEquals(Response.Status.OK.getStatusCode(), result.getStatus(), ()->"Expected response to be 'OK', entity='" + readEntityToString(result) + "'.");

		}
	}

	public String readEntityToString(Response result) {
		try {
			return IOUtils.toString((InputStream)result.getEntity(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new IllegalStateException("Exception while reading response stream.", e);
		}
	}
}
