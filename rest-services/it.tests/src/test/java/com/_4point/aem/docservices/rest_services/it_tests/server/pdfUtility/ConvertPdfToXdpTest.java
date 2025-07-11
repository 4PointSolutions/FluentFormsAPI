package com._4point.aem.docservices.rest_services.it_tests.server.pdfUtility;

import static org.junit.jupiter.api.Assertions.*;

import java.io.InputStream;
import java.nio.file.Files;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.*;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com._4point.aem.docservices.rest_services.it_tests.AemInstance;
import com._4point.aem.docservices.rest_services.it_tests.TestUtils;

@Tag("server-tests")
class ConvertPdfToXdpTest {
	private static final String CONVERT_PDF_TO_XDP_PATH = TEST_MACHINE_AEM_TYPE.pathPrefix() + "/services/PdfUtility/ConvertPdfToXdp";
	private static final String CONVERT_PDF_TO_XDP_URL = "http://" + AemInstance.AEM_1.aemHost() + ":" + AemInstance.AEM_1.aemPort() + CONVERT_PDF_TO_XDP_PATH;
	private static final MediaType APPLICATION_PDF = new MediaType("application", "pdf");
	private static final MediaType APPLICATION_XDP = new MediaType("application", "vnd.adobe.xdp+xml");

	private static final String DOCUMENT_PARAM = "document";

	private static final boolean SAVE_OUTPUT = false;
	
	private WebTarget target;

	@BeforeEach
	void setUp() throws Exception {
		AemInstance.AEM_1.prepareForTests();
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(TEST_USER, TEST_USER_PASSWORD); // default AEM passwords
		target = ClientBuilder.newClient().register(feature).register(MultiPartFeature.class)
				.target(CONVERT_PDF_TO_XDP_URL);
	}

	@Test
	void testConvertPdfToXdp() throws Exception {
		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			multipart.field(DOCUMENT_PARAM, LOCAL_SAMPLE_FORM_PDF.toFile(), APPLICATION_PDF);
			
			Response result = target.request()
					.accept(APPLICATION_XDP)
					.post(Entity.entity(multipart, multipart.getMediaType()));

			assertTrue(result.hasEntity(), "Expected the response to have an entity.");
			assertEquals(Response.Status.OK.getStatusCode(), result.getStatus(), () -> "Expected response to be 'OK', entity='" + TestUtils.readEntityToString(result) + "'.");
			assertEquals(APPLICATION_XDP, MediaType.valueOf(result.getHeaderString(HttpHeaders.CONTENT_TYPE)));

			byte[] resultBytes = IOUtils.toByteArray((InputStream) result.getEntity());
			if (SAVE_OUTPUT) {
				IOUtils.write(resultBytes, Files.newOutputStream(ACTUAL_RESULTS_DIR.resolve("testConvertPdfToXdp_results.xdp")));
			}
		}
	}

}
