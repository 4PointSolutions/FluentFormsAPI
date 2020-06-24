
package com._4point.aem.docservices.rest_services.it_tests.server.forms;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

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
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;

public class ExportDataTest{


	private static final String EXPORT_DATA_URL = "http://" + TEST_MACHINE_NAME + ":" + TEST_MACHINE_PORT_STR + "/services/FormsService/ExportData";
	private static final MediaType APPLICATION_PDF = new MediaType("application", "pdf");
	
	private static final boolean SAVE_RESULTS = false;
	
	private WebTarget target;

	@BeforeEach
	void setUp() throws Exception {
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(TEST_USER, TEST_USER_PASSWORD);	// default AEM passwords
		target = ClientBuilder.newClient()
							  .register(feature)
							  .register(MultiPartFeature.class)
							  .target(EXPORT_DATA_URL);
	}
	 
	@Test
	void testExportData_Bytes() throws IOException {
		
	

			

		try (@SuppressWarnings("resource")
		final FormDataMultiPart multipart = new FormDataMultiPart()
													.field("pdforxdp", TestUtils.SAMPLE_FORM_XDP.toFile() ,APPLICATION_PDF)
													.field("dataformat", MediaType.APPLICATION_XML)) {
			
			Response result = target.request()
									  .accept(MediaType.APPLICATION_XML_TYPE)
									  .post(Entity.entity(multipart, multipart.getMediaType()));
			
			assertTrue(result.hasEntity(), "Expected the response to have an entity.");
			assertEquals(Response.Status.OK.getStatusCode(), result.getStatus(), ()->"Expected response to be 'OK', entity='" + TestUtils.readEntityToString(result) + "'.");
			byte[] resultBytes = IOUtils.toByteArray((InputStream)result.getEntity());
			assertThat("Expected a xml to be returned.", ByteArrayString.toString(resultBytes, 8), containsString("%, X, M, L"));
			if (SAVE_RESULTS) {
				IOUtils.write(resultBytes, Files.newOutputStream(TestUtils.ACTUAL_RESULTS_DIR.resolve("ExportedData.xml")));
			}
			assertEquals(MediaType.APPLICATION_XML_TYPE, MediaType.valueOf(result.getHeaderString(HttpHeaders.CONTENT_TYPE)));
		}
	}


}
