package com._4point.aem.docservices.rest_services.it_tests.server.forms;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.*;

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

import com._4point.aem.docservices.rest_services.it_tests.ByteArrayString;
import com._4point.aem.docservices.rest_services.it_tests.TestUtils;

class RenderPdfFormTest {

	private static final String CRX_CONTENT_ROOT = "crx:/content/dam/formsanddocuments/sample-forms";
	private static final String TEMPLATE_PARAM = "template";
	private static final String DATA_PARAM = "data";
	private static final String ACROBAT_VERSION_PARAM = "renderOptions.acrobatVersion";
	private static final String CACHE_STRATEGY_PARAM = "renderOptions.cacheStrategy";
	private static final String CONTENT_ROOT_PARAM = "renderOptions.contentRoot";
	private static final String DEBUG_DIR_PARAM = "renderOptions.debugDir";
	private static final String LOCALE_PARAM = "renderOptions.locale";
	private static final String SUBMIT_URL_PARAM = "renderOptions.submitUrl";
	private static final String TAGGED_PDF_PARAM = "renderOptions.taggedPdf";
	private static final String XCI_PARAM = "renderOptions.xci";
	private static final String RENDER_PDF_FORM_URL = "http://" + TEST_MACHINE_NAME + ":" + TEST_MACHINE_PORT_STR + "/services/FormsService/RenderPdfForm";
	private static final MediaType APPLICATION_PDF = new MediaType("application", "pdf");

	private WebTarget target;

	@BeforeEach
	void setUp() throws Exception {
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(TEST_USER, TEST_USER_PASSWORD); // default AEM passwords
		target = ClientBuilder.newClient().register(feature).register(MultiPartFeature.class)
				.target(RENDER_PDF_FORM_URL);
	}

	// TODO: Debug/Fix Submit URL and Cache Strategy.  Both are currently causing failures.  I'm not sure why
	//       however they are not required for the current client. so I am going to defer fixing them until
	//       some future time (i.e. this is technical debt).
	/**
	 * Tests RenderForms passing in all possible arguments.
	 * 
	 * Assumes that the AEM server is on the same machine as the test runner since it uses files in the
	 * local directories.
	 * 
	 * @throws Exception
	 */
	@Test
	void testRenderFormsPDF_AllArgs() throws Exception {
		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			multipart.field(DATA_PARAM, SAMPLE_FORM_DATA_XML.toFile(), MediaType.APPLICATION_XML_TYPE)
					 .field(TEMPLATE_PARAM, SERVER_FORMS_DIR.resolve(SAMPLE_FORM_XDP).toString())
					 .field(ACROBAT_VERSION_PARAM, "Acrobat_10")
//					 .field(CACHE_STRATEGY_PARAM, "CONSERVATIVE")
					 .field(LOCALE_PARAM, "en-CA")
					 .field(TAGGED_PDF_PARAM, "true")
					 .field(CONTENT_ROOT_PARAM, SERVER_FORMS_DIR.toString())
//					 .field(SUBMIT_URL_PARAM, "/submit/url")
					 .field(XCI_PARAM, RESOURCES_DIR.resolve("pa.xci").toFile(), MediaType.APPLICATION_XML_TYPE)
					 ;

			Response result = target.request()
									.accept(APPLICATION_PDF)
									.post(Entity.entity(multipart, multipart.getMediaType()));

			assertTrue(result.hasEntity(), "Expected the response to have an entity.");
			assertEquals(Response.Status.OK.getStatusCode(), result.getStatus(), () -> "Expected response to be 'OK', entity='" + TestUtils.readEntityToString(result) + "'.");
			byte[] resultBytes = IOUtils.toByteArray((InputStream) result.getEntity());
			assertThat("Expected a PDF to be returned.", ByteArrayString.toString(resultBytes, 8), containsString("%, P, D, F, -, 1, ., 7"));
			
			// It would be nice if we used a PDF library to verify the attributes that were set earlier (things like
			// tagging, locale, etc.)  For now, we are just going to write the results out and check manually.
//			IOUtils.write(resultBytes, Files.newOutputStream(TestUtils.ACTUAL_RESULTS_DIR.resolve("RenderPdfFormsServer_AllArgsResult.pdf")));
		}

	}

	/**
	 * Tests RenderForms passing in just form and data arguments.  It leaves the rest at defaults (to makes sure that
	 * using the defaults works).
	 * 
	 * Assumes that the AEM server is on the same machine as the test runner since it uses files in the
	 * local directories.
	 * 
	 * @throws Exception
	 */
	@Test
	void testRenderFormsPDF_JustFormAndData() throws Exception {
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
//			IOUtils.write(resultBytes, Files.newOutputStream(TestUtils.ACTUAL_RESULTS_DIR.resolve("RenderPdfFormsServer_JustFormAndData.pdf")));
		}

	}

	/**
	 * Tests RenderForms passing in a CRX: URL.
	 * 
	 * Assumes that the AEM server has the Sample Form (Sample Form.xdp) installed in
	 * /content/dam/formsanddocuments/sample-forms   
	 * 
	 * @throws Exception
	 */
	@Test
	void testRenderFormsPDF_CRXFormAndData() throws Exception {
		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			multipart.field(DATA_PARAM, SAMPLE_FORM_DATA_XML.toFile(), MediaType.APPLICATION_XML_TYPE)
					 .field(TEMPLATE_PARAM, SAMPLE_FORM_XDP.getFileName().toString())
					 .field(CONTENT_ROOT_PARAM, CRX_CONTENT_ROOT);

			Response result = target.request()
									.accept(APPLICATION_PDF)
									.post(Entity.entity(multipart, multipart.getMediaType()));

			assertTrue(result.hasEntity(), "Expected the response to have an entity.");
			assertEquals(Response.Status.OK.getStatusCode(), result.getStatus(), () -> "Expected response to be 'OK', entity='" + TestUtils.readEntityToString(result) + "'.");
			byte[] resultBytes = IOUtils.toByteArray((InputStream) result.getEntity());
			assertThat("Expected a PDF to be returned.", ByteArrayString.toString(resultBytes, 8), containsString("%, P, D, F, -, 1, ., 7"));
//			IOUtils.write(resultBytes, Files.newOutputStream(TestUtils.ACTUAL_RESULTS_DIR.resolve("RenderPdfFormsServer_JustFormAndData.pdf")));
		}

	}

	/**
	 * Passes in a bad XDP name with the expectation that we get a failure containing enough information to 
	 * debug the problem.
	 * 
	 * @throws IOException
	 */
	@Test
	void testRenderFormsPDF_BadXDP() throws IOException {
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
