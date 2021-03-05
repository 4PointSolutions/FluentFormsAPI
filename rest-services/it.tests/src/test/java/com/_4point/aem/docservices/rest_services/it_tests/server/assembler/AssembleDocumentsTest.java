package com._4point.aem.docservices.rest_services.it_tests.server.assembler;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.SAMPLE_FORM_DDX;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.SAMPLE_FORM_PDF;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.TEST_MACHINE_AEM_TYPE;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.TEST_MACHINE_NAME;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.TEST_MACHINE_PORT_STR;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.TEST_USER;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.TEST_USER_PASSWORD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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

import com._4point.aem.docservices.rest_services.client.assembler.RestServicesDocAssemblerServiceAdapter;
import com._4point.aem.docservices.rest_services.it_tests.TestUtils;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.assembler.AssemblerResult;
import com._4point.aem.fluentforms.impl.assembler.LogLevel;
public class AssembleDocumentsTest {
	
	private static final String ASSEMBLE_DOCUMENT_URL = "http://" + TEST_MACHINE_NAME + ":" + TEST_MACHINE_PORT_STR + TEST_MACHINE_AEM_TYPE.pathPrefix() + "/services/AssemblerService/AssembleDocuments";
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

	@BeforeEach
	void setUp() throws Exception {
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(TEST_USER, TEST_USER_PASSWORD); // default AEM passwords
		target = ClientBuilder.newClient().register(feature).register(MultiPartFeature.class)
				.target(ASSEMBLE_DOCUMENT_URL);
	}

	@Test
	void testAssembleDocuments_AllArgs() throws Exception {
		byte[] samplePdf1 = SAMPLE_FORM_PDF.toString().getBytes();
		byte[] samplePdf2 = SAMPLE_FORM_PDF.toString().getBytes();
		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			multipart.field(DATA_PARAM_NAME, SAMPLE_FORM_DDX.toFile(), MediaType.APPLICATION_XML_TYPE)
			.field(IS_FAIL_ON_ERROR, String.valueOf(Boolean.FALSE))
			.field(IS_TAKE_OWNER_SHIP, String.valueOf(Boolean.FALSE))
			.field(IS_VALIDATE_ONLY, String.valueOf(Boolean.FALSE))
			.field(JOB_LOG_LEVEL, LogLevel.FINER.toString())
			.field(DEFAULT_STYLE, "")
			.field(FIRST_BATES_NUMBER, Integer.toString(0));

			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put("File0.pdf", samplePdf1);
			inputs.put("File1.pdf", samplePdf2);	
			inputs.forEach((docName, doc) -> {
				multipart.field(SOURCE_DOCUMENT_KEY, docName);
				multipart.field(SOURCE_DOCUMENT_VALUE, (byte[]) doc, APPLICATION_PDF);
			});

			Response result = target.request()
					.accept(APPLICATION_XML)
					.post(Entity.entity(multipart, multipart.getMediaType()));
			assertTrue(result.hasEntity(), "Expected the response to have an entity.");
			assertEquals(Response.Status.OK.getStatusCode(), result.getStatus(), () -> "Expected response to be 'OK', entity='" + TestUtils.readEntityToString(result) + "'.");

			AssemblerResult assemblerResult = RestServicesDocAssemblerServiceAdapter.convertXmlToAssemblerResult((InputStream) result.getEntity());
			Map<String, Document> resultDocument = assemblerResult.getDocuments();
			byte[] resultByte = null;
			for(Entry<String, Document> entry: resultDocument.entrySet()) {
				if(entry.getKey().equals("concatenatedPDF.pdf")) {
					resultByte = entry.getValue().getInlineData();
					assertNotNull(resultByte);
					assertEquals(APPLICATION_PDF.toString(),entry.getValue().getContentType());
				}			
			}
			if (SAVE_RESULTS) {
				IOUtils.write(resultByte, Files.newOutputStream(TestUtils.ACTUAL_RESULTS_DIR.resolve("testAssembleDocumentPDFWithAllArguments_result.pdf")));
			}		

		}
	}
	
	@Test
	void testAssembleDocuments_JustWithDDXandInputDocuments() throws Exception {
		byte[] samplePdf1 = SAMPLE_FORM_PDF.toString().getBytes();
		byte[] samplePdf2 = SAMPLE_FORM_PDF.toString().getBytes();
		try (final FormDataMultiPart multipart = new FormDataMultiPart()) {
			multipart.field(DATA_PARAM_NAME, SAMPLE_FORM_DDX.toFile(), MediaType.APPLICATION_XML_TYPE);
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put("File0.pdf", samplePdf1);
			inputs.put("File1.pdf", samplePdf2);	
			inputs.forEach((docName, doc) -> {
				multipart.field(SOURCE_DOCUMENT_KEY, docName);
				multipart.field(SOURCE_DOCUMENT_VALUE, (byte[]) doc, APPLICATION_PDF);
			});
			Response result = target.request()
					.accept(APPLICATION_XML)
					.post(Entity.entity(multipart, multipart.getMediaType()));
			assertTrue(result.hasEntity(), "Expected the response to have an entity.");
			assertEquals(Response.Status.OK.getStatusCode(), result.getStatus(), () -> "Expected response to be 'OK', entity='" + TestUtils.readEntityToString(result) + "'.");
		
			AssemblerResult assemblerResult = RestServicesDocAssemblerServiceAdapter.convertXmlToAssemblerResult((InputStream) result.getEntity());
			Map<String, Document> sourceDocuments = assemblerResult.getDocuments();
			byte[] resultByte = null;
			for(Entry<String, Document> entry: sourceDocuments.entrySet()) {
				if(entry.getKey().equals("concatenatedPDF.pdf")) {
					resultByte = entry.getValue().getInlineData();
					assertNotNull(resultByte);
					assertEquals(APPLICATION_PDF.toString(),entry.getValue().getContentType());
				}			
			}
			if (SAVE_RESULTS) {
				IOUtils.write(resultByte, Files.newOutputStream(TestUtils.ACTUAL_RESULTS_DIR.resolve("testAssembleDocumentPDF_result.pdf")));
			}		

		}
	}
}




