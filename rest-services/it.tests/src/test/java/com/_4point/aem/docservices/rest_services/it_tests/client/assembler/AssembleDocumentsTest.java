package com._4point.aem.docservices.rest_services.it_tests.client.assembler;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.SAMPLE_FORM_DDX;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.SAMPLE_FORM_DOCX;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.SAMPLE_FORM_PDF;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.TEST_MACHINE_NAME;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.TEST_MACHINE_PORT;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.TEST_USER;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.TEST_USER_PASSWORD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com._4point.aem.docservices.rest_services.client.assembler.RestServicesDocAssemblerServiceAdapter;
import com._4point.aem.docservices.rest_services.it_tests.TestUtils;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.assembler.AssemblerResult;
import com._4point.aem.fluentforms.api.assembler.AssemblerService;
import com._4point.aem.fluentforms.api.assembler.AssemblerService.AssemblerServiceException;
import com._4point.aem.fluentforms.api.generatePDF.GeneratePDFService.GeneratePDFServiceException;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;
import com._4point.aem.fluentforms.impl.UsageContext;
import com._4point.aem.fluentforms.impl.assembler.AssemblerOptionsSpecImpl;
import com._4point.aem.fluentforms.impl.assembler.AssemblerServiceImpl;
import com._4point.aem.fluentforms.impl.assembler.LogLevel;


public class AssembleDocumentsTest {
	private static final MediaType APPLICATION_PDF = new MediaType("application", "pdf");
	private AssemblerService underTest;
	@BeforeEach
	void setUp() throws Exception {
		RestServicesDocAssemblerServiceAdapter adapter = RestServicesDocAssemblerServiceAdapter.builder()
				.machineName(TEST_MACHINE_NAME)
				.port(TEST_MACHINE_PORT)
				.basicAuthentication(TEST_USER, TEST_USER_PASSWORD)
				.useSsl(false)
				.build();

		underTest = new AssemblerServiceImpl(adapter, UsageContext.CLIENT_SIDE);
	}

	@Test
	@DisplayName("Test AssembleDocuments() Happy Path.")
	void testAssembleDocuments() throws Exception {
		byte[] samplePdf1 = SAMPLE_FORM_PDF.toString().getBytes();
		byte[] samplePdf2 = SAMPLE_FORM_PDF.toString().getBytes();
		Map<String, Object> inputs = new HashMap<String, Object>();
		inputs.put("File0.pdf", SimpleDocumentFactoryImpl.getFactory().create(samplePdf1));
		inputs.put("File1.pdf", SimpleDocumentFactoryImpl.getFactory().create(samplePdf2));	

		AssemblerResult assemblerResult	= underTest.invoke().executeOn(SimpleDocumentFactoryImpl.getFactory().create(SAMPLE_FORM_DDX.toFile()), inputs);
		Map<String, Document> resultDocument = assemblerResult.getDocuments();
		byte[] resultByte = null;
		for(Entry<String, Document> entry: resultDocument.entrySet()){
			if(entry.getKey().equals("concatenatedPDF.pdf")) {
				resultByte = entry.getValue().getInlineData();
				assertNotNull(resultByte);
				assertEquals(APPLICATION_PDF.toString(), entry.getValue().getContentType());
			}		
		}
	}
	
	@Test
	@DisplayName("Test AssembleDocuments() Happy Path.")
	void testAssembleDocuments_withAllArgs() throws Exception {
		byte[] samplePdf1 = SAMPLE_FORM_PDF.toString().getBytes();
		byte[] samplePdf2 = SAMPLE_FORM_PDF.toString().getBytes();
		Map<String, Object> inputs = new HashMap<String, Object>();
		inputs.put("File0.pdf", SimpleDocumentFactoryImpl.getFactory().create(samplePdf1));
		inputs.put("File1.pdf", SimpleDocumentFactoryImpl.getFactory().create(samplePdf2));	
		
		AssemblerResult assemblerResult	= underTest.invoke()
				.setDefaultStyle("")
				.setFailOnError(Boolean.FALSE)
				.setFirstBatesNumber(0)
				.setLogLevel(LogLevel.ALL)
				.setTakeOwnership(Boolean.FALSE)
				.setValidateOnly(Boolean.FALSE)
				.executeOn(SimpleDocumentFactoryImpl.getFactory().create(SAMPLE_FORM_DDX.toFile()), inputs);
		Map<String, Document> resultDocument = assemblerResult.getDocuments();
		byte[] resultByte = null;
		for(Entry<String, Document> entry: resultDocument.entrySet()){
			if(entry.getKey().equals("concatenatedPDF.pdf")) {
				resultByte = entry.getValue().getInlineData();
				assertNotNull(resultByte);
				assertEquals(APPLICATION_PDF.toString(), entry.getValue().getContentType());
			}
		
		}
	}
	
	@Test
	@DisplayName("Test testAssembleDocuments with bad data.")
	void testAssembleDocuments_BadData() throws Exception {
		byte[] samplePdf1 = SAMPLE_FORM_PDF.toString().getBytes();
		byte[] samplePdf2 = SAMPLE_FORM_PDF.toString().getBytes();
		Map<String, Object> sourceDocuments = new HashMap<String, Object>();
		sourceDocuments.put("File0.pdf", SimpleDocumentFactoryImpl.getFactory().create(samplePdf1));
		sourceDocuments.put("File1.pdf", SimpleDocumentFactoryImpl.getFactory().create(samplePdf2));	
		AssemblerOptionsSpecImpl assemblerOptionsSpecImpl = new AssemblerOptionsSpecImpl();
		assemblerOptionsSpecImpl.setLogLevel(LogLevel.ALL);
		AssemblerServiceException ex = assertThrows(AssemblerServiceException.class, ()->underTest.invoke(SimpleDocumentFactoryImpl.getFactory().create(SAMPLE_FORM_DOCX.toFile()), sourceDocuments, assemblerOptionsSpecImpl));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertTrue(msg.contains("Call to server failed"));
	}
}
