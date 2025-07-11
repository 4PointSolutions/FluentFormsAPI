package com._4point.aem.docservices.rest_services.it_tests.server.forms;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

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
import com._4point.aem.fluentforms.api.PathOrUrl;

@Tag("server-tests")
class RenderPdfFormTest {

	private static final String CRX_CONTENT_ROOT = "crx:/content/dam/formsanddocuments/sample-forms";
	private static final String TEMPLATE_PARAM = "template";
	private static final String DATA_PARAM = "data";
	private static final String ACROBAT_VERSION_PARAM = "renderOptions.acrobatVersion";
	private static final String CACHE_STRATEGY_PARAM = "renderOptions.cacheStrategy";
	private static final String CONTENT_ROOT_PARAM = "renderOptions.contentRoot";
	private static final String DEBUG_DIR_PARAM = "renderOptions.debugDir";
	private static final String EMBED_FONTS_PARAM = "renderOptions.embedFonts";
	private static final String LOCALE_PARAM = "renderOptions.locale";
	private static final String SUBMIT_URL_PARAM = "renderOptions.submitUrl";
	private static final String TAGGED_PDF_PARAM = "renderOptions.taggedPdf";
	private static final String XCI_PARAM = "renderOptions.xci";
	private static final String RENDER_PDF_FORM_URL = "http://" + AemInstance.AEM_1.aemHost() + ":" + AemInstance.AEM_1.aemPort() + TEST_MACHINE_AEM_TYPE.pathPrefix() + "/services/FormsService/RenderPdfForm";
	private static final MediaType APPLICATION_PDF = new MediaType("application", "pdf");
	private static final MediaType APPLICATION_XDP = new MediaType("application", "vnd.adobe.xdp+xml");

	private static final boolean SAVE_OUTPUT = false;
	
	private WebTarget target;

	@BeforeAll
	static void setUpAll() throws Exception {
		AemInstance.AEM_1.prepareForTests();
	}

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
			multipart.field(DATA_PARAM, LOCAL_SAMPLE_FORM_DATA_XML.toFile(), MediaType.APPLICATION_XML_TYPE)
					 .field(TEMPLATE_PARAM, REMOTE_SAMPLE_FORM_XDP.getFileName().toString())
					 .field(ACROBAT_VERSION_PARAM, "Acrobat_10")
//					 .field(CACHE_STRATEGY_PARAM, "CONSERVATIVE")
					 .field(EMBED_FONTS_PARAM, "true")
					 .field(LOCALE_PARAM, "en-CA")
					 .field(TAGGED_PDF_PARAM, "true")
					 .field(CONTENT_ROOT_PARAM, PathOrUrl.from(REMOTE_SAMPLE_FORM_XDP.getParent()).toUnixString())
//					 .field(SUBMIT_URL_PARAM, "/submit/url")
					 .field(XCI_PARAM, RESOURCES_DIR.resolve("pa.xci").toFile(), MediaType.APPLICATION_XML_TYPE)
					 ;

			Response result = target.request()
									.accept(APPLICATION_PDF)
									.post(Entity.entity(multipart, multipart.getMediaType()));

			assertTrue(result.hasEntity(), "Expected the response to have an entity.");
			assertEquals(Response.Status.OK.getStatusCode(), result.getStatus(), () -> "Expected response to be 'OK', entity='" + TestUtils.readEntityToString(result) + "'.");
			assertEquals(APPLICATION_PDF, MediaType.valueOf(result.getHeaderString(HttpHeaders.CONTENT_TYPE)));

			byte[] resultBytes = IOUtils.toByteArray((InputStream) result.getEntity());
			// Not sure why the PDF being generated is dynamic instead of static because the pa.xci says that <dynamicRender>Forbidden</dynamicRender>
			// TODO:  Look at this and reconcile the difference between this behaviour and the client version's behaviour.
			TestUtils.validatePdfResult(resultBytes, "RenderPdfFormsServer_AllArgsResult.pdf", true, true, false);
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
			multipart.field(DATA_PARAM, LOCAL_SAMPLE_FORM_DATA_XML.toFile(), MediaType.APPLICATION_XML_TYPE)
					 .field(TEMPLATE_PARAM, REMOTE_SAMPLE_FORM_XDP.toString());

			Response result = target.request()
									.accept(APPLICATION_PDF)
									.post(Entity.entity(multipart, multipart.getMediaType()));

			assertTrue(result.hasEntity(), "Expected the response to have an entity.");
			assertEquals(Response.Status.OK.getStatusCode(), result.getStatus(), () -> "Expected response to be 'OK', entity='" + TestUtils.readEntityToString(result) + "'.");
			assertEquals(APPLICATION_PDF, MediaType.valueOf(result.getHeaderString(HttpHeaders.CONTENT_TYPE)));

			byte[] resultBytes = IOUtils.toByteArray((InputStream) result.getEntity());
			TestUtils.validatePdfResult(resultBytes, "RenderPdfFormsServer_JustFormAndData.pdf", true, true, false);
		}

	}

	/**
	 * Tests RenderForms passing in just form (as a Document) and data arguments.  It leaves the rest at defaults
	 * (to makes sure that using the defaults works).
	 * 
	 * Assumes that the AEM server is on the same machine as the test runner since it uses files in the
	 * local directories.
	 * 
	 * @throws Exception
	 */
	@Test
	void testRenderFormsPDF_JustFormDocAndData() throws Exception {
		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			multipart.field(DATA_PARAM, LOCAL_SAMPLE_FORM_DATA_XML.toFile(), MediaType.APPLICATION_XML_TYPE)
					 .field(TEMPLATE_PARAM, LOCAL_SAMPLE_FORM_XDP.toFile(), APPLICATION_XDP);

			Response result = target.request()
									.accept(APPLICATION_PDF)
									.post(Entity.entity(multipart, multipart.getMediaType()));

			assertTrue(result.hasEntity(), "Expected the response to have an entity.");
			assertEquals(Response.Status.OK.getStatusCode(), result.getStatus(), () -> "Expected response to be 'OK', entity='" + TestUtils.readEntityToString(result) + "'.");
			assertEquals(APPLICATION_PDF, MediaType.valueOf(result.getHeaderString(HttpHeaders.CONTENT_TYPE)));

			byte[] resultBytes = IOUtils.toByteArray((InputStream) result.getEntity());
			TestUtils.validatePdfResult(resultBytes, "RenderPdfFormsServer_JustFormAndData.pdf", true, true, false);
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
			multipart.field(DATA_PARAM, LOCAL_SAMPLE_FORM_DATA_XML.toFile(), MediaType.APPLICATION_XML_TYPE)
					 .field(TEMPLATE_PARAM, LOCAL_SAMPLE_FORM_XDP.getFileName().toString())
					 .field(CONTENT_ROOT_PARAM, CRX_CONTENT_ROOT);

			Response result = target.request()
									.accept(APPLICATION_PDF)
									.post(Entity.entity(multipart, multipart.getMediaType()));

			assertTrue(result.hasEntity(), "Expected the response to have an entity.");
			assertEquals(Response.Status.OK.getStatusCode(), result.getStatus(), () -> "Expected response to be 'OK', entity='" + TestUtils.readEntityToString(result) + "'.");
			assertEquals(APPLICATION_PDF, MediaType.valueOf(result.getHeaderString(HttpHeaders.CONTENT_TYPE)));

			byte[] resultBytes = IOUtils.toByteArray((InputStream) result.getEntity());
			TestUtils.validatePdfResult(resultBytes, "RenderPdfFormsServer_JustFormAndData.pdf", true, true, false);
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
		try (final FormDataMultiPart fdmp = new FormDataMultiPart(); final FormDataMultiPart multipart = fdmp
				.field(DATA_PARAM, TestUtils.LOCAL_SAMPLE_FORM_DATA_XML.toFile(), MediaType.APPLICATION_XML_TYPE)
				.field(TEMPLATE_PARAM, PathOrUrl.from(TestUtils.REMOTE_SAMPLE_FORM_XDP.getParent().resolve(badFormName)).toUnixString())) {

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
