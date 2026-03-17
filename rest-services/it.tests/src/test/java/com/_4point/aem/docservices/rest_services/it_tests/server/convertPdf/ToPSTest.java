package com._4point.aem.docservices.rest_services.it_tests.server.convertPdf;

import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.LOCAL_SAMPLE_FORM_WITHOUT_DATA_PDF;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.TEST_MACHINE_AEM_TYPE;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.TEST_USER;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.TEST_USER_PASSWORD;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com._4point.aem.docservices.rest_services.it_tests.AemInstance;
import com._4point.aem.docservices.rest_services.it_tests.ByteArrayString;
import com._4point.aem.docservices.rest_services.it_tests.TestUtils;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Tag("server-tests")
public class ToPSTest {
	private static final String TO_PS_URL = "http://" + AemInstance.AEM_1.aemHost() + ":" + AemInstance.AEM_1.aemPort() + TEST_MACHINE_AEM_TYPE.pathPrefix() + "/services/ConvertPdfService/ToPS";

	private static final String PDF_PARAM = "inPdfDoc";

	private static final boolean SAVE_RESULTS = false;

	@BeforeAll
	static void setUpAll() throws Exception {
		AemInstance.AEM_1.prepareForTests();
	}

	private WebTarget target;

	@BeforeEach
	void setUp() throws Exception {
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(TEST_USER, TEST_USER_PASSWORD); // default AEM passwords
		target = ClientBuilder.newClient().register(feature).register(MultiPartFeature.class)
							  .target(TO_PS_URL);
	}

	@Test
	void testToPS_NoArgs() throws Exception {
		byte[] samplePdf = Files.readAllBytes(LOCAL_SAMPLE_FORM_WITHOUT_DATA_PDF);
		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			multipart.field(PDF_PARAM, samplePdf, new MediaType("application", "pdf"));
			
			Response result = target.request()
					.accept(MediaType.valueOf("application/postscript"))
					.post(Entity.entity(multipart, multipart.getMediaType()));
			
			assertTrue(result.hasEntity(), "Expected the response to have an entity.");
			assertEquals(Response.Status.OK.getStatusCode(), result.getStatus(), () -> "Expected response to be 'OK', entity='" + TestUtils.readEntityToString(result) + "'.");

			byte[] resultBytes = result.readEntity(byte[].class);
			if (SAVE_RESULTS) {
				IOUtils.write(resultBytes, Files.newOutputStream(TestUtils.ACTUAL_RESULTS_DIR.resolve("testToPS_NoArgs_result.ps")));
			}

			assertThat("Expected PostScript to be returned.", ByteArrayString.toString(resultBytes, 10), containsString("%, !, P, S, -, A, d, o, b, e"));
		}
	}
}
