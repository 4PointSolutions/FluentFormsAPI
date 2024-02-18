package com._4point.aem.docservices.rest_services.it_tests.client.assembler;

import static org.hamcrest.MatcherAssert.assertThat; 
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import jakarta.ws.rs.core.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com._4point.aem.docservices.rest_services.client.assembler.RestServicesDocAssemblerServiceAdapter;
import com._4point.aem.docservices.rest_services.client.helpers.XmlDocument;
import com._4point.aem.docservices.rest_services.it_tests.Pdf;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.assembler.AssemblerResult;
import com._4point.aem.fluentforms.api.assembler.AssemblerService;
import com._4point.aem.fluentforms.api.assembler.LogLevel;
import com._4point.aem.fluentforms.api.assembler.PDFAConversionResult;
import com._4point.aem.fluentforms.api.assembler.AssemblerService.AssemblerServiceException;
import com._4point.aem.fluentforms.api.assembler.AssemblerService.EitherDocumentOrDocumentList;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;
import com._4point.aem.fluentforms.impl.UsageContext;
import com._4point.aem.fluentforms.impl.assembler.AssemblerOptionsSpecImpl;
import com._4point.aem.fluentforms.impl.assembler.AssemblerServiceImpl;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.ColorSpace;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.Compliance;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.OptionalContent;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.ResultLevel;
import com.adobe.fd.assembler.client.PDFAConversionOptionSpec.Signatures;


public class AssembleDocumentsTest {
	private static final DocumentFactory DOC_FACTORY = SimpleDocumentFactoryImpl.getFactory();
	private static final MediaType APPLICATION_PDF = new MediaType("application", "pdf");
	private AssemblerService underTest;
	@BeforeEach
	void setUp() throws Exception {
		RestServicesDocAssemblerServiceAdapter adapter = RestServicesDocAssemblerServiceAdapter.builder()
				.machineName(TEST_MACHINE_NAME)
				.port(TEST_MACHINE_PORT)
				.basicAuthentication(TEST_USER, TEST_USER_PASSWORD)
				.useSsl(false)
				.aemServerType(TEST_MACHINE_AEM_TYPE)
				.build();

		underTest = new AssemblerServiceImpl(adapter, UsageContext.CLIENT_SIDE);
	}

	@Test
	@DisplayName("Test AssembleDocuments() Happy Path.")
	void testAssembleDocuments() throws Exception {
		byte[] samplePdf1 = SAMPLE_FORM_PDF.toString().getBytes();
		byte[] samplePdf2 = SAMPLE_FORM_PDF.toString().getBytes();
		Map<String, Object> inputs = new HashMap<String, Object>();
		inputs.put("File0.pdf", DOC_FACTORY.create(samplePdf1));
		inputs.put("File1.pdf", DOC_FACTORY.create(samplePdf2));	

		AssemblerResult assemblerResult	= underTest.invoke().executeOn(DOC_FACTORY.create(SAMPLE_FORM_DDX.toFile()), inputs);
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
	@DisplayName("Test AssembleDocuments() with all arguments Happy Path.")
	void testAssembleDocuments_withAllArgs() throws Exception {
		byte[] samplePdf1 = SAMPLE_FORM_PDF.toString().getBytes();
		byte[] samplePdf2 = SAMPLE_FORM_PDF.toString().getBytes();
		Map<String, Object> inputs = new HashMap<String, Object>();
		inputs.put("File0.pdf", DOC_FACTORY.create(samplePdf1));
		inputs.put("File1.pdf", DOC_FACTORY.create(samplePdf2));	
		
		AssemblerResult assemblerResult	= underTest.invoke()
				.setDefaultStyle("")
				.setFailOnError(Boolean.FALSE)
				.setFirstBatesNumber(0)
				.setLogLevel(LogLevel.ALL)
				.setTakeOwnership(Boolean.FALSE)
				.setValidateOnly(Boolean.FALSE)
				.executeOn(DOC_FACTORY.create(SAMPLE_FORM_DDX.toFile()), inputs);
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
		sourceDocuments.put("File0.pdf", DOC_FACTORY.create(samplePdf1));
		sourceDocuments.put("File1.pdf", DOC_FACTORY.create(samplePdf2));	
		AssemblerOptionsSpecImpl assemblerOptionsSpecImpl = new AssemblerOptionsSpecImpl();
		assemblerOptionsSpecImpl.setLogLevel(LogLevel.ALL);
		AssemblerServiceException ex = assertThrows(AssemblerServiceException.class, ()->underTest.invoke(DOC_FACTORY.create(SAMPLE_FORM_DOCX.toFile()), sourceDocuments, assemblerOptionsSpecImpl));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertTrue(msg.contains("Call to server failed"));
	}
	
	@Test
	@DisplayName("Test ToPdfA() Happy Path.")
	void testToPdfA() throws Exception {
		PDFAConversionResult result = underTest.toPDFA()
											   .executeOn(DOC_FACTORY.create(SAMPLE_FORM_PDF.toFile()));
		Document pdfaDocument = result.getPDFADocument();
		Document conversionLog = result.getConversionLog();
		Document jobLog = result.getJobLog();
		Boolean pdfa = result.isPDFA();
		
		assertNotNull(XmlDocument.create(conversionLog.getInputStream()));	// Make sure the conversion log is XML.
		assertNotNull(XmlDocument.create(jobLog.getInputStream()));			// Make sure the job log is XML.
		assertTrue(pdfa);
		Pdf pdfResult = Pdf.from(pdfaDocument.getInputStream());
		assertThat(pdfResult.getProducer(), containsStringIgnoringCase("assembler"));
	}

	@Test
	@DisplayName("Test ToPdfA() All Args Happy Path.")
	void testToPdfA_withAllArgs() throws Exception {
		PDFAConversionResult result = underTest.toPDFA()
											   .setColorSpace(ColorSpace.S_RGB)
											   .setCompliance(Compliance.PDFA_1B)
											   .setLogLevel(LogLevel.ALL)
		//									   .setMetadataSchemaExtensions(null)	// Excluded because not sure what this is.
											   .setOptionalContent(OptionalContent.ALL)
											   .setRemoveInvalidXMPProperties(true)
											   .setResultLevel(ResultLevel.DETAILED)
											   .setRetainPDFFormState(true)
											   .setSignatures(Signatures.ARCHIVE_ALWAYS)
											   .setVerify(true)
											   .executeOn(DOC_FACTORY.create(SAMPLE_FORM_PDF.toFile()));
		Document pdfaDocument = result.getPDFADocument();
		Document conversionLog = result.getConversionLog();
		Document jobLog = result.getJobLog();
		Boolean pdfa = result.isPDFA();
		
		assertNotNull(XmlDocument.create(conversionLog.getInputStream()));	// Make sure the conversion log is XML.
		assertNotNull(XmlDocument.create(jobLog.getInputStream()));			// Make sure the job log is XML.
		assertTrue(pdfa);
		Pdf pdfResult = Pdf.from(pdfaDocument.getInputStream());
		assertThat(pdfResult.getProducer(), containsStringIgnoringCase("assembler"));
	}

	@Test
	void testAssembleDocuments_DocInfoDdx() throws Exception {
		String ddx =  "<DDX xmlns=\"http://ns.adobe.com/DDX/1.0/\">\n"
					+ "    <DocumentInformation result=\"info\" source=\"doc1\"/>\n"
					+ "</DDX>\n";
 
		Map<String, EitherDocumentOrDocumentList> inputDocs = Collections.singletonMap("doc1", EitherDocumentOrDocumentList.from(DOC_FACTORY.create(SAMPLE_FORM_PDF)));
		
		AssemblerResult result = underTest.invoke()
										  .executeOn2(DOC_FACTORY.create(ddx.getBytes()), inputDocs);
		
		Map<String, Document> documents = result.getDocuments();
		assertEquals(1, documents.size());
		Document resultDoc = documents.get("info");
		assertEquals(resultDoc.getContentType(), "application/xml");
		String info = new String(resultDoc.getInlineData());
		String numPages = info.substring(info.indexOf("<NumPages>") + 10, info.indexOf("</NumPages>"));
		assertEquals(1, Integer.valueOf(numPages));
	}

}
