package com._4point.aem.docservices.rest_services.it_tests.server.output;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.*;

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

class GeneratePdfOutputTest {

	private static final String GENERATE_PDF_OUTPUT_PATH = "/services/OutputService/GeneratePdfOutput";
	private static final String GENERATE_PDF_OUTPUT_URL = "http://" + TEST_MACHINE_NAME + ":" + TEST_MACHINE_PORT_STR + GENERATE_PDF_OUTPUT_PATH;
	private static final MediaType APPLICATION_PDF = new MediaType("application", "pdf");
	private static final String CRX_CONTENT_ROOT = "crx:/content/dam/formsanddocuments/sample-forms";

	private static final String TEMPLATE_PARAM = "template";
	private static final String DATA_PARAM = "data";
	private static final String ACROBAT_VERSION_PARAM = "outputOptions.acrobatVersion";
	private static final String CONTENT_ROOT_PARAM = "outputOptions.contentRoot";
	private static final String DEBUG_DIR_PARAM = "outputOptions.debugDir";
	private static final String EMBED_FONTS_PARAM = "outputOptions.embedFonts";
	private static final String LINEARIZED_PDF_PARAM = "outputOptions.linearizedPdf";
	private static final String LOCALE_PARAM = "outputOptions.locale";
	private static final String RETAIN_PDF_FORM_STATE_PARAM = "outputOptions.retainPdfFormState";
	private static final String RETAIN_UNSIGNED_SIGNATURE_FIELDS_PARAM = "outputOptions.retainUnsignedSignatureFields";
	private static final String TAGGED_PDF_PARAM = "outputOptions.taggedPdf";
	private static final String XCI_PARAM = "outputOptions.xci";

	private static final boolean SAVE_OUTPUT = false;
	
	private WebTarget target;

	@BeforeEach
	void setUp() throws Exception {
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(TEST_USER, TEST_USER_PASSWORD); // default AEM passwords
		target = ClientBuilder.newClient().register(feature).register(MultiPartFeature.class)
				.target(GENERATE_PDF_OUTPUT_URL);
	}

	@Test
	void testGeneratePdfOutput_AllArgs() throws Exception {
		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			multipart.field(DATA_PARAM, SAMPLE_FORM_DATA_XML.toFile(), MediaType.APPLICATION_XML_TYPE)
					 .field(TEMPLATE_PARAM, SERVER_FORMS_DIR.resolve(SAMPLE_FORM_XDP).toString())
					 .field(ACROBAT_VERSION_PARAM, "Acrobat_10")
//					 .field(DEBUG_DIR_PARAM, "")	We don't want to generate debug outputs.
					 .field(EMBED_FONTS_PARAM, Boolean.toString(false))
					 .field(LINEARIZED_PDF_PARAM, Boolean.toString(false))
					 .field(LOCALE_PARAM, "en-CA")
					 .field(RETAIN_PDF_FORM_STATE_PARAM, Boolean.toString(false))
					 .field(RETAIN_UNSIGNED_SIGNATURE_FIELDS_PARAM, Boolean.toString(false))
					 .field(TAGGED_PDF_PARAM, "true")
					 .field(CONTENT_ROOT_PARAM, SERVER_FORMS_DIR.toString())
					 .field(XCI_PARAM, RESOURCES_DIR.resolve("pa.xci").toFile(), MediaType.APPLICATION_XML_TYPE)
					 ;

			Response result = target.request()
									.accept(APPLICATION_PDF)
									.post(Entity.entity(multipart, multipart.getMediaType()));

			assertTrue(result.hasEntity(), "Expected the response to have an entity.");
			assertEquals(Response.Status.OK.getStatusCode(), result.getStatus(), () -> "Expected response to be 'OK', entity='" + TestUtils.readEntityToString(result) + "'.");
			byte[] resultBytes = IOUtils.toByteArray((InputStream) result.getEntity());
			assertThat("Expected a PDF to be returned.", ByteArrayString.toString(resultBytes, 8), containsString("%, P, D, F, -, 1, ., 7"));
			assertEquals(APPLICATION_PDF, MediaType.valueOf(result.getHeaderString(HttpHeaders.CONTENT_TYPE)));
			// It would be nice if we used a PDF library to verify the attributes that were set earlier (things like
			// tagging, locale, etc.)  For now, we are just going to write the results out and check manually.
			if (SAVE_OUTPUT) {
				IOUtils.write(resultBytes, Files.newOutputStream(TestUtils.ACTUAL_RESULTS_DIR.resolve("GeneratePdfOutput_AllArgsResult.pdf")));
			}
		}
	}
	
	@Test
	void testGeneratePdfOutput_JustFormAndData() throws Exception {
		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			multipart.field(DATA_PARAM, SAMPLE_FORM_DATA_XML.toFile(), MediaType.APPLICATION_XML_TYPE)
					 .field(TEMPLATE_PARAM, SERVER_FORMS_DIR.resolve(SAMPLE_FORM_XDP).toString());

			Response result = target.request()
									.accept(APPLICATION_PDF)
									.post(Entity.entity(multipart, multipart.getMediaType()));

			assertTrue(result.hasEntity(), "Expected the response to have an entity.");
			assertEquals(Response.Status.OK.getStatusCode(), result.getStatus(), () -> "Expected response to be 'OK', entity='" + TestUtils.readEntityToString(result) + "'.");
			byte[] resultBytes = IOUtils.toByteArray((InputStream) result.getEntity());
			assertThat("Expected a PDF to be returned.", ByteArrayString.toString(resultBytes, 8), containsString("%, P, D, F, -, 1, ., 7"));
			if (SAVE_OUTPUT) {
				IOUtils.write(resultBytes, Files.newOutputStream(TestUtils.ACTUAL_RESULTS_DIR.resolve("GeneratePdfOutput_JustFormAndData.pdf")));
			}
		}
	}
		
	@Test
	void testGeneratePdfOutput_CRXFormAndData() throws Exception {
		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			multipart.field(DATA_PARAM, SAMPLE_FORM_DATA_XML.toFile(), MediaType.APPLICATION_XML_TYPE)
					 .field(TEMPLATE_PARAM, SAMPLE_FORM_XDP.getFileName().toString())
					 .field(CONTENT_ROOT_PARAM, CRX_CONTENT_ROOT);

			Response result = target.request()
									.accept(APPLICATION_PDF)
									.post(Entity.entity(multipart, multipart.getMediaType()));

			
			assertTrue(result.hasEntity(), "Expected the response to have an entity.");
			assertEquals(Response.Status.OK.getStatusCode(), result.getStatus(), ()->"Expected response to be 'OK', entity='" + TestUtils.readEntityToString(result) + "'.");
			byte[] resultBytes = IOUtils.toByteArray((InputStream) result.getEntity());
			assertThat("Expected a PDF to be returned.", ByteArrayString.toString(resultBytes, 8), containsString("%, P, D, F, -, 1, ., 7"));
			if (SAVE_OUTPUT) {
				IOUtils.write(resultBytes, Files.newOutputStream(TestUtils.ACTUAL_RESULTS_DIR.resolve("GeneratePdfOutput_JustFormAndData.pdf")));
			}
		}
	}

	@Test
	void testGeneratePdfOutput_CRXBadXDP() throws Exception {
		String badFormName = "BadForm.xdp";
		try (final FormDataMultiPart multipart = new FormDataMultiPart()
				.field(DATA_PARAM, TestUtils.SAMPLE_FORM_DATA_XML.toFile(), MediaType.APPLICATION_XML_TYPE)
				.field(TEMPLATE_PARAM, TestUtils.SERVER_FORMS_DIR.resolve(badFormName).toString())) {

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
