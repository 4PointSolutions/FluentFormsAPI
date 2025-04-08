package com._4point.aem.docservices.rest_services.it_tests.client.assembler;

import static org.hamcrest.MatcherAssert.assertThat; 
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.*;
import static com._4point.testing.matchers.javalang.ExceptionMatchers.*;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.opentest4j.MultipleFailuresError;

import com._4point.aem.docservices.rest_services.client.RestClient.ContentType;
import com._4point.aem.docservices.rest_services.client.assembler.RestServicesDocAssemblerServiceAdapter;
import com._4point.aem.docservices.rest_services.client.helpers.XmlDocument;
import com._4point.aem.docservices.rest_services.client.jersey.JerseyRestClient;
import com._4point.aem.docservices.rest_services.it_tests.AemInstance;
import com._4point.aem.docservices.rest_services.it_tests.Pdf;
import com._4point.aem.docservices.rest_services.it_tests.Pdf.PdfException;
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

@Tag("client-tests")
public class AssembleDocumentsTest {
	private static final DocumentFactory DOC_FACTORY = SimpleDocumentFactoryImpl.getFactory();
	private AssemblerService underTest;

	@BeforeAll
	static void setUpAll() throws Exception {
		AemInstance.AEM_1.prepareForTests();
	}

	@BeforeEach
	void setUp() throws Exception {
		RestServicesDocAssemblerServiceAdapter adapter = RestServicesDocAssemblerServiceAdapter.builder(JerseyRestClient.factory())
				.machineName(AemInstance.AEM_1.aemHost())
				.port(AemInstance.AEM_1.aemPort())
				.basicAuthentication(TEST_USER, TEST_USER_PASSWORD)
				.useSsl(false)
				.aemServerType(TEST_MACHINE_AEM_TYPE)
				.build();

		underTest = new AssemblerServiceImpl(adapter, UsageContext.CLIENT_SIDE);
	}

	@Test
	@DisplayName("Test AssembleDocuments() Happy Path.")
	void testAssembleDocuments() throws Exception {
		AssemblerResult assemblerResult	= underTest.invoke()
												   .add("File0.pdf", DOC_FACTORY.create(SAMPLE_FORM_WITHOUT_DATA_PDF))
												   .add("File1.pdf", DOC_FACTORY.create(SAMPLE_FORM_WITHOUT_DATA_PDF).setContentType("application/pdf"))
												   .executeOn(DOC_FACTORY.create(SAMPLE_FORM_DDX));

		Map<String, Document> resultDocuments = assemblerResult.getDocuments();
		validateResultMap(resultDocuments);
	}

	private void validateResultMap(Map<String, Document> resultMap) throws IOException, PdfException {
		for(Entry<String, Document> entry: resultMap.entrySet()){
			if(entry.getKey().equals("concatenatedPDF.pdf")) {
				validateResultDoc(entry.getValue());
			}		
		}
	}

	private static void validateResultDoc(Document resultDoc) throws IOException, PdfException, MultipleFailuresError {
		byte[] resultByte = resultDoc.getInputStream().readAllBytes();
		assertNotNull(resultByte);
		Pdf pdfResult = Pdf.from(resultByte);	// Validate that it's a real PDF.
		assertAll(
				()->assertEquals(ContentType.APPLICATION_PDF.contentType(), resultDoc.getContentType()),
				()->assertThat(pdfResult.getProducer(), not(emptyOrNullString()))
				);
	}
	
	@Test
	@DisplayName("Test AssembleDocuments() with all arguments Happy Path.")
	void testAssembleDocuments_withAllArgs() throws Exception {
		AssemblerResult assemblerResult	= underTest.invoke()
				.setDefaultStyle("")
				.setFailOnError(Boolean.FALSE)
				.setFirstBatesNumber(0)
				.setLogLevel(LogLevel.ALL)
				.setTakeOwnership(Boolean.FALSE)
				.setValidateOnly(Boolean.FALSE)
				.add("File0.pdf", DOC_FACTORY.create(SAMPLE_FORM_WITHOUT_DATA_PDF).setContentType("application/pdf"))
				.add("File1.pdf", DOC_FACTORY.create(SAMPLE_FORM_WITHOUT_DATA_PDF))
				.executeOn(DOC_FACTORY.create(SAMPLE_FORM_DDX));

		Map<String, Document> resultDocuments = assemblerResult.getDocuments();
		validateResultMap(resultDocuments);
	}
	
	@Test
	@DisplayName("Test testAssembleDocuments with bad data.")
	void testAssembleDocuments_BadData() throws Exception {
		Map<String, Object> sourceDocuments = Map.of("File0.pdf", DOC_FACTORY.create(SAMPLE_FORM_PDF),
													 "File1.pdf", DOC_FACTORY.create(SAMPLE_FORM_PDF)
													);
		AssemblerOptionsSpecImpl assemblerOptionsSpecImpl = new AssemblerOptionsSpecImpl();
		assemblerOptionsSpecImpl.setLogLevel(LogLevel.ALL);
		AssemblerServiceException ex = assertThrows(AssemblerServiceException.class, ()->underTest.invoke(DOC_FACTORY.create(SAMPLE_FORM_DOCX), sourceDocuments, assemblerOptionsSpecImpl));

		assertThat(ex, exceptionMsgContainsAll("Error while POSTing to server"));
	}
	
	@Test
	@DisplayName("Test ToPdfA() Happy Path.")
	void testToPdfA() throws Exception {
		PDFAConversionResult result = underTest.toPDFA()
											   .executeOn(DOC_FACTORY.create(SAMPLE_FORM_PDF));
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
											   .executeOn(DOC_FACTORY.create(SAMPLE_FORM_PDF));
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
