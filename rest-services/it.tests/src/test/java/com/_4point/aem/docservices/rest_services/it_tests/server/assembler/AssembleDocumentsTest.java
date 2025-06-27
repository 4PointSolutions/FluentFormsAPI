package com._4point.aem.docservices.rest_services.it_tests.server.assembler;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Map;
import java.util.Map.Entry;

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

import com._4point.aem.docservices.rest_services.client.assembler.AssemblerServiceTestHelper;
import com._4point.aem.docservices.rest_services.it_tests.AemInstance;
import com._4point.aem.docservices.rest_services.it_tests.TestUtils;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.assembler.AssemblerResult;
import com._4point.aem.fluentforms.api.assembler.LogLevel;
import com._4point.testing.matchers.aem.Pdf;
import com._4point.testing.matchers.aem.Pdf.PdfException;

@Tag("server-tests")
public class AssembleDocumentsTest {
	
	private static final String ASSEMBLE_DOCUMENT_URL = "http://" + AemInstance.AEM_1.aemHost() + ":" + AemInstance.AEM_1.aemPort() + TEST_MACHINE_AEM_TYPE.pathPrefix() + "/services/AssemblerService/AssembleDocuments";
	private static final MediaType APPLICATION_PDF = new MediaType("application", "pdf");
	private static final MediaType APPLICATION_XML = new MediaType("application", "xml");

	private static final String DATA_PARAM_NAME = "ddx";
	private static final String IS_FAIL_ON_ERROR = "isFailOnError";
	private static final String IS_VALIDATE_ONLY = "isValidateOnly";
	private static final String IS_TAKE_OWNER_SHIP = "isTakeOwnerShip";
	private static final String JOB_LOG_LEVEL = "jobLogLevel";
	private static final String DEFAULT_STYLE = "defaultStyle";
	private static final String FIRST_BATES_NUMBER = "firstBatesNum";	
	private static final String SOURCE_DOCUMENT_KEY = "sourceDocumentMap.key";
	private static final String SOURCE_DOCUMENT_VALUE = "sourceDocumentMap.value";

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
				.target(ASSEMBLE_DOCUMENT_URL);
	}

	@Test
	void testAssembleDocuments_AllArgs() throws Exception {
		byte[] samplePdf1 = Files.readAllBytes(LOCAL_SAMPLE_FORM_WITHOUT_DATA_PDF);
		byte[] samplePdf2 = Files.readAllBytes(LOCAL_SAMPLE_FORM_WITHOUT_DATA_PDF);
		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			multipart.field(DATA_PARAM_NAME, LOCAL_SAMPLE_FORM_DDX.toFile(), MediaType.APPLICATION_XML_TYPE)
			.field(IS_FAIL_ON_ERROR, String.valueOf(Boolean.FALSE))
			.field(IS_TAKE_OWNER_SHIP, String.valueOf(Boolean.FALSE))
			.field(IS_VALIDATE_ONLY, String.valueOf(Boolean.FALSE))
			.field(JOB_LOG_LEVEL, LogLevel.FINER.toString())
			.field(DEFAULT_STYLE, "")
			.field(FIRST_BATES_NUMBER, Integer.toString(0));

			Map.of("File0.pdf", samplePdf1, "File1.pdf", samplePdf2)
			   .forEach((docName, doc) -> {
				   multipart.field(SOURCE_DOCUMENT_KEY, docName);
				   multipart.field(SOURCE_DOCUMENT_VALUE, (byte[]) doc, APPLICATION_PDF);
			   });

			Response result = target.request()
					.accept(APPLICATION_XML)
					.post(Entity.entity(multipart, multipart.getMediaType()));
			assertTrue(result.hasEntity(), "Expected the response to have an entity.");
			assertEquals(Response.Status.OK.getStatusCode(), result.getStatus(), () -> "Expected response to be 'OK', entity='" + TestUtils.readEntityToString(result) + "'.");

			AssemblerResult assemblerResult = AssemblerServiceTestHelper.convertXmlToAssemblerResult((InputStream) result.getEntity());
			byte[] resultByte = validateAssemblerResult(assemblerResult);
			if (SAVE_RESULTS) {
				IOUtils.write(resultByte, Files.newOutputStream(TestUtils.ACTUAL_RESULTS_DIR.resolve("testAssembleDocumentPDFWithAllArguments_result.pdf")));
			}		

		}
	}
	
	@Test
	void testAssembleDocuments_JustWithDDXandInputDocuments() throws Exception {
		byte[] samplePdf1 = Files.readAllBytes(LOCAL_SAMPLE_FORM_WITHOUT_DATA_PDF);
		byte[] samplePdf2 = Files.readAllBytes(LOCAL_SAMPLE_FORM_WITHOUT_DATA_PDF);
		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			multipart.field(DATA_PARAM_NAME, LOCAL_SAMPLE_FORM_DDX.toFile(), MediaType.APPLICATION_XML_TYPE);
			Map.of("File0.pdf", samplePdf1, "File1.pdf", samplePdf2)
			   .forEach((docName, doc) -> {
				   multipart.field(SOURCE_DOCUMENT_KEY, docName);
				   multipart.field(SOURCE_DOCUMENT_VALUE, (byte[]) doc, APPLICATION_PDF);
			   });
			Response result = target.request()
					.accept(APPLICATION_XML)
					.post(Entity.entity(multipart, multipart.getMediaType()));
			assertTrue(result.hasEntity(), "Expected the response to have an entity.");
			assertEquals(Response.Status.OK.getStatusCode(), result.getStatus(), () -> "Expected response to be 'OK', entity='" + TestUtils.readEntityToString(result) + "'.");
		
			AssemblerResult assemblerResult = AssemblerServiceTestHelper.convertXmlToAssemblerResult((InputStream) result.getEntity());
			byte[] resultByte = validateAssemblerResult(assemblerResult);
			if (SAVE_RESULTS) {
				IOUtils.write(resultByte, Files.newOutputStream(TestUtils.ACTUAL_RESULTS_DIR.resolve("testAssembleDocumentPDF_result.pdf")));
			}		

		}
	}

	private byte[] validateAssemblerResult(AssemblerResult assemblerResult) throws IOException, PdfException {
		Map<String, Document> sourceDocuments = assemblerResult.getDocuments();
		for(Entry<String, Document> entry: sourceDocuments.entrySet()) {
			if(entry.getKey().equals("concatenatedPDF.pdf")) {
				byte[] resultByte = entry.getValue().getInputStream().readAllBytes();
				assertNotNull(resultByte);
				assertEquals(APPLICATION_PDF.toString(),entry.getValue().getContentType());
				assertThat(resultByte.length, greaterThan(0));
				Pdf.from(resultByte);	// Ensure that the byte array is a valid PDF
				return resultByte;
			}			
		}
		fail("Expected the assembler result to contain a document named 'concatenatedPDF.pdf'.");
		throw new IllegalStateException("Routine should fail before this point.");
	}
}
