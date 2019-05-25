package com._4point.aem.docservices.rest_services.it_tests.server.forms;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com._4point.aem.docservices.rest_services.it_tests.server.ByteArrayString;
import com._4point.aem.docservices.rest_services.it_tests.server.TestUtils;

class RenderPdfFormTest {

	private static final String TEMPLATE_PARAM_NAME = "template";
	private static final String DATA_PARAM_NAME = "data";
	private static final String RENDER_PDF_FORM_URL = "http://localhost:4502/services/FormsService/RenderPdfForm";
	private static final MediaType APPLICATION_PDF = new MediaType("application", "pdf");

	private WebTarget target;

	@BeforeEach
	void setUp() throws Exception {
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("admin", "admin"); // default AEM passwords
		target = ClientBuilder.newClient().register(feature).register(MultiPartFeature.class)
				.target(RENDER_PDF_FORM_URL);
	}

	@Test
	void testRenderFormsPDF_HappyPath() throws IOException {
		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			multipart.field(DATA_PARAM_NAME, TestUtils.SAMPLE_FORM_DATA_XML.toFile(), MediaType.APPLICATION_XML_TYPE)
					 .field(TEMPLATE_PARAM_NAME, TestUtils.SERVER_FORMS_DIR.resolve(TestUtils.SAMPLE_FORM_XDP).toString());

			Response result = target.request()
									.accept(APPLICATION_PDF)
									.post(Entity.entity(multipart, multipart.getMediaType()));

			assertTrue(result.hasEntity(), "Expected the response to have an entity.");
			assertEquals(Response.Status.OK.getStatusCode(), result.getStatus(), () -> "Expected response to be 'OK', entity='" + TestUtils.readEntityToString(result) + "'.");
			byte[] resultBytes = IOUtils.toByteArray((InputStream) result.getEntity());
			assertThat("Expected a PDF to be returned.", ByteArrayString.toString(resultBytes, 8), containsString("%, P, D, F, -, 1, ., 7"));
			IOUtils.write(resultBytes, Files.newOutputStream(TestUtils.ACTUAL_RESULTS_DIR.resolve("RenderPdfForms_HappyPathResult.pdf")));
		}

	}

	@Test
	void testRenderFormsPDF_JustFormAndData() throws IOException {
		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			multipart.field(DATA_PARAM_NAME, TestUtils.SAMPLE_FORM_DATA_XML.toFile(), MediaType.APPLICATION_XML_TYPE)
					 .field(TEMPLATE_PARAM_NAME, TestUtils.SERVER_FORMS_DIR.resolve(TestUtils.SAMPLE_FORM_XDP).toString());

			Response result = target.request()
									.accept(APPLICATION_PDF)
									.post(Entity.entity(multipart, multipart.getMediaType()));

			assertTrue(result.hasEntity(), "Expected the response to have an entity.");
			assertEquals(Response.Status.OK.getStatusCode(), result.getStatus(), () -> "Expected response to be 'OK', entity='" + TestUtils.readEntityToString(result) + "'.");
			byte[] resultBytes = IOUtils.toByteArray((InputStream) result.getEntity());
			assertThat("Expected a PDF to be returned.", ByteArrayString.toString(resultBytes, 8), containsString("%, P, D, F, -, 1, ., 7"));
//			IOUtils.write(resultBytes, Files.newOutputStream(TestUtils.ACTUAL_RESULTS_DIR.resolve("RenderPdfForms_JustFormAndData.pdf")));
		}

	}

	@Test
	void testRenderFormsPDF_BadXDP() throws IOException {
		String badFormName = "BadForm.xdp";
		try (final FormDataMultiPart multipart = new FormDataMultiPart()
				.field(DATA_PARAM_NAME, TestUtils.SAMPLE_FORM_DATA_XML.toFile(), MediaType.APPLICATION_XML_TYPE)
				.field(TEMPLATE_PARAM_NAME, TestUtils.SERVER_FORMS_DIR.resolve(badFormName).toString())) {

			Response result = target.request()
									.accept(APPLICATION_PDF)
									.post(Entity.entity(multipart, multipart.getMediaType()));

			assertTrue(result.hasEntity(), "Expected the response to have an entity.");
			assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), result.getStatus(), () -> "Expected response to be 'OK', entity='" + TestUtils.readEntityToString(result) + "'.");
			
			String statusMsg = IOUtils.toString((InputStream)result.getEntity(), StandardCharsets.UTF_8); 
			assertThat(statusMsg, containsStringIgnoringCase("Bad request parameter"));
			assertThat(statusMsg, containsStringIgnoringCase("unable to find template"));
			assertThat(statusMsg, containsString(badFormName));
		}

	}

}
