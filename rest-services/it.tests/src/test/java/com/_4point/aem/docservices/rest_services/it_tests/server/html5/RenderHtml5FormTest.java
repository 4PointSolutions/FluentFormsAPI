package com._4point.aem.docservices.rest_services.it_tests.server.html5;

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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import com._4point.aem.docservices.rest_services.it_tests.AemInstance;
import com._4point.aem.docservices.rest_services.it_tests.TestUtils;

@Tag("server-tests")
public class RenderHtml5FormTest {

	private static final String TEMPLATE_PARAM_NAME = "template";
	private static final String CONTEXT_ROOT_PARAM_NAME = "contentRoot";
	private static final String DATA_PARAM_NAME = "data";
	private static final String RENDER_HTML5_FORM_URL = "http://" + AemInstance.AEM_1.aemHost() + ":" + AemInstance.AEM_1.aemPort() + TEST_MACHINE_AEM_TYPE.pathPrefix() + "/services/Html5/RenderHtml5Form";
	private static final String AEM_RENDER_HTML5_URL = "http://" + AemInstance.AEM_1.aemHost() + ":" + AemInstance.AEM_1.aemPort() + TEST_MACHINE_AEM_TYPE.pathPrefix() + "/content/xfaforms/profiles/default.html";
	private static final String APPLICATION_XDP = "application/vnd.adobe.xdp+xml";

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
							  .target(RENDER_HTML5_FORM_URL);
	}

	@Test
	void testRenderHtml5Form_FormRef() throws IOException {

		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			multipart.field(TEMPLATE_PARAM_NAME, SERVER_FORMS_DIR.resolve(SAMPLE_FORM_XDP).toString());

			Response result = target
					.request()
					.accept(MediaType.TEXT_HTML_TYPE)
					.post(Entity.entity(multipart, multipart.getMediaType()));
						
			assertTrue(result.hasEntity(), "Expected the response to have an entity.");
			assertEquals(Response.Status.OK.getStatusCode(), result.getStatus(), ()->"Expected response to be 'OK', entity='" + TestUtils.readEntityToString(result) + "'.");
			byte[] resultBytes = IOUtils.toByteArray((InputStream)result.getEntity());
			if (SAVE_RESULTS) {
				IOUtils.write(resultBytes, Files.newOutputStream(TestUtils.ACTUAL_RESULTS_DIR.resolve("testGetHtml5Form_Get_result.html")));
			}
			assertTrue(MediaType.TEXT_HTML_TYPE.isCompatible(MediaType.valueOf(result.getHeaderString(HttpHeaders.CONTENT_TYPE))));
			assertThat(new String(resultBytes, StandardCharsets.UTF_8), not(anyOf(containsString("Text Field1 Data"), containsString("Text Field2 Data"))));
		}
	}
		
	private enum FormType { BY_VALUE, BY_REFERENCE, BY_CRX_REFERENCE };
	private enum DataType { NO_DATA, BY_VALUE, BY_REFERENCE };
	
	private enum HappyPathScenario {
		FORM_REF_ONLY(FormType.BY_REFERENCE, DataType.NO_DATA),
//		FORM_CRX_REF_ONLY(FormType.BY_CRX_REFERENCE, DataType.NO_DATA),
//		FORM_VAL_ONLY(FormType.BY_VALUE, DataType.NO_DATA),		// Not supported at this time
//		FORM_REF_DATA_REF(FormType.BY_REFERENCE, DataType.BY_REFERENCE),	// Doesn't work for some reason.  Isn't needed at this time.
		FORM_REF_DATA_VAL(FormType.BY_REFERENCE, DataType.BY_VALUE),
//		FORM_CRX_REF_DATA_VAL(FormType.BY_CRX_REFERENCE, DataType.BY_VALUE),
//		FORM_VAL_DATA_REF(FormType.BY_VALUE, DataType.BY_REFERENCE),		// Not supported at this time
//		FORM_VAL_DATA_VAL(FormType.BY_VALUE, DataType.BY_VALUE)		// Not supported at this time
		;
		
		private final FormType formType;
		private final DataType dataType;

		private HappyPathScenario(FormType formType, DataType dataType) {
			this.formType = formType;
			this.dataType = dataType;
		}
	}
	
	@ParameterizedTest
	@EnumSource
	void testDoPost_HappyPath(HappyPathScenario scenario) throws Exception {

		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			switch (scenario.formType) {
			case BY_REFERENCE:
				multipart.field(TEMPLATE_PARAM_NAME, SAMPLE_FORM_XDP.toAbsolutePath().toString());
				break;
			case BY_CRX_REFERENCE:
				multipart.field(TEMPLATE_PARAM_NAME, "sample-forms/ " + SAMPLE_FORM_XDP.getFileName().toString());
				multipart.field(CONTEXT_ROOT_PARAM_NAME, "crx:/content/dam/formsanddocuments");
				break;
			case BY_VALUE:
				multipart.field(TEMPLATE_PARAM_NAME, SAMPLE_FORM_XDP.toFile(), MediaType.valueOf(APPLICATION_XDP));
				break;
			default:
				throw new IllegalStateException("Unknown FormType (" + scenario.formType.toString() + ").");
			}
			switch (scenario.dataType) {
			case NO_DATA:
				break;	// Don't do anything w.r.t. data if NO_DATA is specified.
			case BY_REFERENCE:
				multipart.field(DATA_PARAM_NAME, SAMPLE_FORM_DATA_XML.toString());
				break;
			case BY_VALUE:
				multipart.field(DATA_PARAM_NAME, SAMPLE_FORM_DATA_XML.toFile(), MediaType.APPLICATION_XML_TYPE);
				break;
			default:
				throw new IllegalStateException("Unknown DataType (" + scenario.dataType.toString() + ").");
			}

			Response result = target
						.request()
						.accept(MediaType.TEXT_HTML_TYPE)
						.post(Entity.entity(multipart, multipart.getMediaType()));
		
			assertTrue(result.hasEntity(), "Expected the response to have an entity.");
			assertEquals(Response.Status.OK.getStatusCode(), result.getStatus(), ()->"Expected response to be 'OK', entity='" + TestUtils.readEntityToString(result) + "'.");
			byte[] resultBytes = IOUtils.toByteArray((InputStream)result.getEntity());
			if (SAVE_RESULTS) {
				IOUtils.write(resultBytes, Files.newOutputStream(TestUtils.ACTUAL_RESULTS_DIR.resolve("testDoPost_HappyPath_" + scenario.toString() + "_result.html")));
			}
			assertTrue(MediaType.TEXT_HTML_TYPE.isCompatible(MediaType.valueOf(result.getHeaderString(HttpHeaders.CONTENT_TYPE))));
			
			if (scenario.dataType == DataType.NO_DATA) {
				assertThat(new String(resultBytes, StandardCharsets.UTF_8), not(anyOf(containsString("Text Field1 Data"), containsString("Text Field2 Data"))));
			} else {
				assertThat(new String(resultBytes, StandardCharsets.UTF_8), allOf(containsString("Text Field1 Data"), containsString("Text Field2 Data")));
			}

		}
	}

}
