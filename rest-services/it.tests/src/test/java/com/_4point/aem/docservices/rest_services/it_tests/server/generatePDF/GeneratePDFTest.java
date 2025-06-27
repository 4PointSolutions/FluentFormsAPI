package com._4point.aem.docservices.rest_services.it_tests.server.generatePDF;

import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.InputStream;
import java.nio.file.Files;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
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

import com._4point.aem.docservices.rest_services.client.generatePDF.GeneratePDFServiceTestHelper;
import com._4point.aem.docservices.rest_services.it_tests.AemInstance;
import com._4point.aem.docservices.rest_services.it_tests.TestUtils;
import com._4point.aem.fluentforms.api.generatePDF.CreatePDFResult;
import com._4point.aem.fluentforms.impl.generatePDF.PDFSettings;
import com._4point.aem.fluentforms.impl.generatePDF.SecuritySettings;

@Tag("server-tests")
@Tag("requiresPdfG")
public class GeneratePDFTest {
	private static final String GENERATE_PDF_URL = "http://" + AemInstance.AEM_1.aemHost() + ":" + AemInstance.AEM_1.aemPort() + TEST_MACHINE_AEM_TYPE.pathPrefix() + "/services/GeneratePDFService/CreatePDF";
	private static final MediaType APPLICATION_PDF = new MediaType("application", "pdf");
	private static final MediaType APPLICATION_XML = new MediaType("application", "xml");
	private static final String DATA_PARAM_NAME = "data";
	private static final String FILE_EXTENSION = "fileExtension";
	private static final String FILE_TYPE_SETTINGS = "fileTypeSettings";
	private static final String PDF_SETTINGS = "pdfSettings";
	private static final String SECURITY_SETTINGS = "securitySettings";

	private static final boolean SAVE_RESULTS = false;
	
	private WebTarget target;

	@BeforeAll
	static void setUpAll() throws Exception {
		AemInstance.AEM_1.prepareForTests();
	}

	@BeforeEach
	void setUp() throws Exception {
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(TEST_USER, TEST_USER_PASSWORD); // default AEM passwords
		target = ClientBuilder.newClient().register(feature).register(MultiPartFeature.class)
				.target(GENERATE_PDF_URL);
	}
	
	@Test
	void testGeneratePdf_AllArgs() throws Exception {
		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			multipart.field(DATA_PARAM_NAME, LOCAL_SAMPLE_FORM_DOCX.toFile(), MediaType.MULTIPART_FORM_DATA_TYPE).
			field(FILE_EXTENSION, "docx")
			.field(FILE_TYPE_SETTINGS, "")
			.field(PDF_SETTINGS, PDFSettings.High_Quality_Print.toString())
			.field(SECURITY_SETTINGS, SecuritySettings.No_Security.toString());
			
			Response result = target.request()
					.accept(APPLICATION_XML)
					.post(Entity.entity(multipart, multipart.getMediaType()));
			assertTrue(result.hasEntity(), "Expected the response to have an entity.");
			assertEquals(Response.Status.OK.getStatusCode(), result.getStatus(), () -> "Expected response to be 'OK', entity='" + TestUtils.readEntityToString(result) + "'.");
		
			CreatePDFResult createPDFResult = GeneratePDFServiceTestHelper.convertXmlToCreatePDFResult((InputStream) result.getEntity());
			byte[] resultBytes = createPDFResult.getCreatedDocument().getInlineData();
			assertNotNull(createPDFResult.getCreatedDocument().getInlineData());
			assertEquals(APPLICATION_PDF.toString(),createPDFResult.getCreatedDocument().getContentType());
			if (SAVE_RESULTS) {
				IOUtils.write(resultBytes, Files.newOutputStream(TestUtils.ACTUAL_RESULTS_DIR.resolve("testGeneratePDF_result.pdf")));
			}
		}
	}
	
	@Test
	void testGeneratePdf_JustDataAndFileExtension() throws Exception {
		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			multipart.field(DATA_PARAM_NAME, LOCAL_SAMPLE_FORM_DOCX.toFile(), MediaType.MULTIPART_FORM_DATA_TYPE).
			field(FILE_EXTENSION, "docx");
			Response result = target.request()
					.accept(APPLICATION_XML)
					.post(Entity.entity(multipart, multipart.getMediaType()));
			assertTrue(result.hasEntity(), "Expected the response to have an entity.");
			assertEquals(Response.Status.OK.getStatusCode(), result.getStatus(), () -> "Expected response to be 'OK', entity='" + TestUtils.readEntityToString(result) + "'.");
		
			CreatePDFResult createPDFResult = GeneratePDFServiceTestHelper.convertXmlToCreatePDFResult((InputStream) result.getEntity());
			byte[] resultBytes = createPDFResult.getCreatedDocument().getInlineData();
			assertNotNull(createPDFResult.getCreatedDocument().getInlineData());
			assertEquals(APPLICATION_PDF.toString(),createPDFResult.getCreatedDocument().getContentType());
			if (SAVE_RESULTS) {
				IOUtils.write(resultBytes, Files.newOutputStream(TestUtils.ACTUAL_RESULTS_DIR.resolve("testGeneratePDF_JustDataAndFileExtensionResult.pdf")));
			}
		}
	}
	
	
}
