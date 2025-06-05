package com._4point.aem.docservices.rest_services.server.assembler;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.xmlunit.matchers.CompareMatcher.isIdenticalTo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import javax.servlet.ServletException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.xmlunit.builder.Input;

import com._4point.aem.docservices.rest_services.server.Exceptions.InternalServerErrorException;
import com._4point.aem.docservices.rest_services.server.TestUtils;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.assembler.AssemblerOptionsSpec;
import com._4point.aem.fluentforms.api.assembler.AssemblerResult;
import com._4point.aem.fluentforms.api.assembler.LogLevel;
import com._4point.aem.fluentforms.impl.assembler.AssemblerResultImpl;
import com._4point.aem.fluentforms.impl.assembler.TraditionalDocAssemblerService;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;
import com._4point.aem.fluentforms.testing.assembler.ExceptionalMockTraditionalAssemblerService;
import com._4point.aem.fluentforms.testing.assembler.MockTraditionalAssemblerService;
import com._4point.aem.fluentforms.testing.assembler.MockTraditionalAssemblerService.GenerateAssemblerResultArgs;
import com.adobe.fd.assembler.client.OperationException;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
public class AssembleDocumentsTest {
	private static final String APPLICATION_XML = "application/xml";
	private static final String APPLICATION_PDF = "application/pdf";
	private static final String TEXT_HTML = "text/html";
	private static final String DATA_PARAM_NAME = "ddx";
	private static final String SOURCE_DOCUMENT_KEY = "sourceDocumentMap.key";
	private static final String SOURCE_DOCUMENT_VALUE = "sourceDocumentMap.value";
	private static final String IS_FAIL_ON_ERROR = "isFailOnError";
	private static final String IS_TAKE_OWNER_SHIP = "isTakeOwnerShip";
	private static final String DEFAULT_STYLE = "defaultStyle";
	private static final String FIRST_BATES_NUMBER = "firstBatesNumber";
	private static final String LOG_LEVEL = "logLevel";
	private static final String IS_VALIDATE_ONLY = "isValidatedOnly";
	
	private final AemContext aemContext = new AemContext();

	private final AssembleDocuments underTest =  new AssembleDocuments();

	private MockDocumentFactory mockDocumentFactory = new MockDocumentFactory();
	
	@BeforeEach
	void setUp() throws Exception {
		// Always use the MockDocumentFactory() in the class that's under test because the Adobe Document object has unresolved dependencies.
		junitx.util.PrivateAccessor.setField(underTest, "docFactory",  (DocumentFactory)mockDocumentFactory);
	}
	
	@Test
	void testDoPost_HappyPath_JustForm() throws ServletException, IOException, NoSuchFieldException {
		String expectedResultData = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><assemblerResult><resultDocument documentName=\"concatenatedPDF.pdf\" contentType=\"application/pdf\"><mergedDoc>dGVzdERvUG9zdCBIYXBweSBQYXRoIFJlc3VsdA==</mergedDoc></resultDocument><failedBlockNames/><successfulDocumentNames/><successfulBlockNames/><latestBatesNumber value=\"0\"/><numRequestedBlocks value=\"0\"/><jobLog/></assemblerResult>" ;
		String data = "testDoPost Happy Path Result";
		String templateData = TestUtils.SAMPLE_DDX.toString();
		byte[] samplePdf1 = TestUtils.SAMPLE_PDF.toString().getBytes();
		byte[] samplePdf2 = TestUtils.SAMPLE_PDF.toString().getBytes();
		
		Boolean isFailOnError = false;
		Boolean isTakeOwnerShip = true;
		Boolean isValidateOnly = true;
		int firstBatesNumber = 1;
		String defaultStyle = "test";
		LogLevel logLevel = LogLevel.ALL;
		
	    Map<String, Document> sourceDocuments = new HashMap<String, Document>();
	    sourceDocuments.put("concatenatedPDF.pdf", mockDocumentFactory.create(data.getBytes()));
	    AssemblerResultImpl assemblerResult = new AssemblerResultImpl(sourceDocuments, // sourceDocuments
														    		  null, // failedBlockNames
														    		  null, // jobLog
														    		  0, 	// lastBatesNumber
														    		  null, // multipleResultsBlocks
														    		  0, 	// numRequestedBlocks
														    		  null, // successfulBlockNames
														    		  null, // successfulDocumentNames
														    		  null);// throwables
		MockTraditionalAssemblerService assemblePdfMock = mockAssemblePdf(assemblerResult);
		Map<String, Object> inputs = new HashMap<String, Object>();
		inputs.put("File0.pdf", samplePdf1);
		inputs.put("File1.pdf", samplePdf2);
		
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.resourceResolver(), aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();

		request.addRequestParameter(DATA_PARAM_NAME, templateData);
		request.addRequestParameter(IS_FAIL_ON_ERROR, Boolean.toString(isFailOnError));
		request.addRequestParameter(IS_TAKE_OWNER_SHIP,  Boolean.toString(isTakeOwnerShip));
		request.addRequestParameter(FIRST_BATES_NUMBER, String.valueOf(firstBatesNumber));
		request.addRequestParameter(DEFAULT_STYLE, defaultStyle);
		request.addRequestParameter(LOG_LEVEL, logLevel.toString());
		request.addRequestParameter(IS_VALIDATE_ONLY,  Boolean.toString(isValidateOnly));
		
		inputs.forEach((docName, doc) -> {
			request.addRequestParameter(SOURCE_DOCUMENT_KEY, docName);
			request.addRequestParameter(SOURCE_DOCUMENT_VALUE, (byte[]) doc, APPLICATION_PDF);
		});
		
		underTest.doPost(request, response);
		
		// Validate the result
		assertEquals(SlingHttpServletResponse.SC_OK, response.getStatus());
		assertEquals(APPLICATION_XML, response.getContentType());
		assertThat(Input.fromString(response.getOutputAsString()), isIdenticalTo(Input.fromString(expectedResultData.trim())).ignoreWhitespace());
		// Validate that the correct parameters were passed
		GenerateAssemblerResultArgs generateAssemblerResultArgs = assemblePdfMock.getGenerateAssemblerResultArgs();
		assertNotNull(generateAssemblerResultArgs.getDdx());
		assertNotNull(generateAssemblerResultArgs.getSourceDocuments());
        AssemblerOptionsSpec assemblerOptionsSpec =	generateAssemblerResultArgs.getAssemblerOptionsSpec(); 
        assertAll(
              	  ()->assertEquals(LogLevel.ALL, assemblerOptionsSpec.getLogLevel()),
        		  ()->assertFalse(false),
        		  ()->assertTrue(true),
        		  ()->assertTrue(true),
        		  ()->assertEquals("test", assemblerOptionsSpec.getDefaultStyle()),
        		  ()->assertEquals(1,assemblerOptionsSpec.getFirstBatesNumber()));     
	}
	
	@Test
	void testDoPost_HappyPath_AssemblerOptions_Not_Provided() throws ServletException, IOException, NoSuchFieldException {
		String data = "testDoPost Happy Path Result";
		String templateData = TestUtils.SAMPLE_DDX.toString();
		byte[] samplePdf1 = TestUtils.SAMPLE_PDF.toString().getBytes();
		byte[] samplePdf2 = TestUtils.SAMPLE_PDF.toString().getBytes();
		
	    Map<String, Document> sourceDocuments = new HashMap<String, Document>();
	    sourceDocuments.put("concatenatedPDF.pdf", mockDocumentFactory.create(data.getBytes()));
	    AssemblerResultImpl assemblerResult = createDummyResult(sourceDocuments);
		MockTraditionalAssemblerService assemblePdfMock = mockAssemblePdf(assemblerResult);
		Map<String, Object> inputs = new HashMap<String, Object>();
		inputs.put("File0.pdf", samplePdf1);
		inputs.put("File1.pdf", samplePdf2);
		
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.resourceResolver(), aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();

		request.addRequestParameter(DATA_PARAM_NAME, templateData);		
		inputs.forEach((docName, doc) -> {
			request.addRequestParameter(SOURCE_DOCUMENT_KEY, docName);
			request.addRequestParameter(SOURCE_DOCUMENT_VALUE, (byte[]) doc, APPLICATION_PDF);
		});	
		underTest.doPost(request, response);	
		GenerateAssemblerResultArgs generateAssemblerResultArgs = assemblePdfMock.getGenerateAssemblerResultArgs();
        AssemblerOptionsSpec assemblerOptionsSpec =
		  generateAssemblerResultArgs.getAssemblerOptionsSpec(); 
          assertAll(
      	  ()->assertNull(assemblerOptionsSpec.getLogLevel()),
		  ()->assertNull(assemblerOptionsSpec.isFailOnError()),
		  ()->assertNull(assemblerOptionsSpec.isTakeOwnership()),
		  ()->assertNull(assemblerOptionsSpec.isValidateOnly()),
		  ()->assertNull(assemblerOptionsSpec.getDefaultStyle()),
		  ()->assertEquals(assemblerOptionsSpec.getFirstBatesNumber(), 0));	 
	}
	
	private AssemblerResultImpl createDummyResult(Map<String, Document> documents) {
			return new AssemblerResultImpl(documents, Collections.emptyList(), null, 0, Collections.emptyMap(), 0, Collections.emptyList(), Collections.emptyList(), Collections.emptyMap());
	}

	@Test
	void testDoPost_BadLog() throws ServletException, IOException, NoSuchFieldException {
		
		String data = "testDoPost Happy Path Result";
		String templateData = TestUtils.SAMPLE_DDX.toString();
		byte[] samplePdf1 = TestUtils.SAMPLE_PDF.toString().getBytes();
		byte[] samplePdf2 = TestUtils.SAMPLE_PDF.toString().getBytes();
		
		Boolean isFailOnError = false;
	
		String logLevel = "foo";
		
	    Map<String, Document> sourceDocuments = new HashMap<String, Document>();
	    sourceDocuments.put("concatenatedPDF.pdf", mockDocumentFactory.create(data.getBytes()));
	    AssemblerResultImpl assemblerResult = createDummyResult(sourceDocuments);
		MockTraditionalAssemblerService assemblePdfMock = mockAssemblePdf(assemblerResult);
		Map<String, Object> inputs = new HashMap<String, Object>();
		inputs.put("File0.pdf", samplePdf1);
		inputs.put("File1.pdf", samplePdf2);
		
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.resourceResolver(), aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();

		request.addRequestParameter(DATA_PARAM_NAME, templateData);
		request.addRequestParameter(IS_FAIL_ON_ERROR, Boolean.toString(isFailOnError));
		request.addRequestParameter(LOG_LEVEL, logLevel.toString());
		
		inputs.forEach((docName, doc) -> {
			request.addRequestParameter(SOURCE_DOCUMENT_KEY, docName);
			request.addRequestParameter(SOURCE_DOCUMENT_VALUE, (byte[]) doc, APPLICATION_PDF);
		});
		
		underTest.doPost(request, response);
		assertEquals(SlingHttpServletResponse.SC_BAD_REQUEST, response.getStatus());
		assertThat(response.getStatusMessage(), containsStringIgnoringCase("Bad arguments"));
	}
	
	@Test
	void testDoPost_Bad_AssemblerServiceException() throws ServletException, IOException, NoSuchFieldException {
	
		String templateData = TestUtils.SAMPLE_DDX.toString();
		byte[] samplePdf1 = TestUtils.SAMPLE_PDF.toString().getBytes();
		byte[] samplePdf2 = TestUtils.SAMPLE_PDF.toString().getBytes();
		
		Boolean isFailOnError = false;
	
		LogLevel logLevel = LogLevel.ALL;
		
		String exceptionMessage = "Exception Message";
		junitx.util.PrivateAccessor.setField(underTest, "assemblerServiceFactory", (Supplier<TraditionalDocAssemblerService>)()->(TraditionalDocAssemblerService)ExceptionalMockTraditionalAssemblerService.create(exceptionMessage));
		
		Map<String, Object> inputs = new HashMap<String, Object>();
		inputs.put("File0.pdf", samplePdf1);
		inputs.put("File1.pdf", samplePdf2);
		
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.resourceResolver(), aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();

	    request.addRequestParameter(DATA_PARAM_NAME, templateData);
		request.addRequestParameter(IS_FAIL_ON_ERROR, Boolean.toString(isFailOnError));
		request.addRequestParameter(LOG_LEVEL, logLevel.toString());
		inputs.forEach((docName, doc) -> {
			request.addRequestParameter(SOURCE_DOCUMENT_KEY, docName);
			request.addRequestParameter(SOURCE_DOCUMENT_VALUE, (byte[]) doc, APPLICATION_PDF);
		});
		
		underTest.doPost(request, response);
		assertEquals(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR, response.getStatus());
		String statusMsg = response.getStatusMessage();
		assertThat(statusMsg, containsStringIgnoringCase("Internal Error while merging PDF"));
		assertThat(statusMsg, containsStringIgnoringCase(exceptionMessage));
	}
	
	@Test
	void testDoPost_Bad_Accept() throws ServletException, IOException, NoSuchFieldException {
		String data = "testDoPost Happy Path Result";
		String templateData = TestUtils.SAMPLE_DDX.toString();
		byte[] samplePdf1 = TestUtils.SAMPLE_PDF.toString().getBytes();
		byte[] samplePdf2 = TestUtils.SAMPLE_PDF.toString().getBytes();
		
		Boolean isFailOnError = false;
		LogLevel logLevel = LogLevel.ALL;
		
	    Map<String, Document> sourceDocuments = new HashMap<String, Document>();
	    sourceDocuments.put("concatenatedPDF.pdf", mockDocumentFactory.create(data.getBytes()));
	    AssemblerResultImpl assemblerResult = createDummyResult(sourceDocuments);
	    MockTraditionalAssemblerService assemblePdfMock = mockAssemblePdf(assemblerResult);
		Map<String, Object> inputs = new HashMap<String, Object>();
		inputs.put("File0.pdf", samplePdf1);
		inputs.put("File1.pdf", samplePdf2);
		
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.resourceResolver(), aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();

		request.addRequestParameter(DATA_PARAM_NAME, templateData);
		request.addRequestParameter(IS_FAIL_ON_ERROR, Boolean.toString(isFailOnError));
		request.addRequestParameter(LOG_LEVEL, logLevel.toString());
		request.addHeader("Accept", TEXT_HTML);
		inputs.forEach((docName, doc) -> {
			request.addRequestParameter(SOURCE_DOCUMENT_KEY, docName);
			request.addRequestParameter(SOURCE_DOCUMENT_VALUE, (byte[]) doc, APPLICATION_PDF);
		});
		
		underTest.doPost(request, response);
		assertEquals(SlingHttpServletResponse.SC_NOT_ACCEPTABLE, response.getStatus());
		String statusMsg = response.getStatusMessage();
		assertThat(statusMsg, containsStringIgnoringCase(TEXT_HTML));
		assertThat(statusMsg, containsStringIgnoringCase(APPLICATION_XML));
	}
	
	@Test
	void testConvertAssemblerResultToXml_EmptyResult() throws InternalServerErrorException, ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException{
	    AssemblerResultImpl assemblerResult = new AssemblerResultImpl(null, // sourceDocuments
														    		  null, // failedBlockNames
														    		  null, // jobLog
														    		  0, 	// lastBatesNumber
														    		  null, // multipleResultsBlocks
														    		  0, 	// numRequestedBlocks
														    		  null, // successfulBlockNames
														    		  null, // successfulDocumentNames
														    		  null);// throwables
		String resultXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><assemblerResult><failedBlockNames/><successfulDocumentNames/><successfulBlockNames/><latestBatesNumber value=\"0\"/><numRequestedBlocks value=\"0\"/><jobLog/></assemblerResult>";
		String responseXml = AssembleDocuments.convertAssemblerResultToxml(assemblerResult);
		assertThat(Input.fromString(responseXml), isIdenticalTo(Input.fromString(resultXml)).ignoreWhitespace());
	}
	
	@Test
	void testConvertAssemblerResultToXml() throws InternalServerErrorException, ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException{
		AssemblerResultImpl assemblerResult = createAssemblerResultStub();
		String resultXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"
				+ "<assemblerResult>\n"
				+ "    <resultDocument documentName=\"concatenatedPDF.pdf\" contentType=\"application/pdf\">\n"
				+ "        <mergedDoc>dGVzdERvUG9zdCBIYXBweSBQYXRoIFJlc3VsdA==</mergedDoc>\n"
				+ "    </resultDocument>\n"
				+ "    <failedBlockNames>\n"
				+ "        <failedBlockName>failedBlock1</failedBlockName>\n"
				+ "        <failedBlockName>failedBlock2</failedBlockName>\n"
				+ "    </failedBlockNames>\n"
				+ "    <successfulDocumentNames>\n"
				+ "        <successfulDocumentName>successDocument1</successfulDocumentName>\n"
				+ "        <successfulDocumentName>successDocument2</successfulDocumentName>\n"
				+ "    </successfulDocumentNames>\n"
				+ "    <successfulBlockNames>\n"
				+ "        <successfulBlockName>successBlock1</successfulBlockName>\n"
				+ "        <successfulBlockName>successBlock2</successfulBlockName>\n"
				+ "    </successfulBlockNames>\n"
				+ "    <latestBatesNumber value=\"2\" />\n"
				+ "    <numRequestedBlocks value=\"3\" />\n"
				+ "    <multipleResultBlocks name=\"document\">\n"
				+ "        <documentNames>\n"
				+ "            <documentName>test1</documentName>\n"
				+ "            <documentName>test2</documentName>\n"
				+ "        </documentNames>\n"
				+ "    </multipleResultBlocks>\n"
				+ "    <jobLog joblogValue=\"SU5GTw==\" />\n"
				+ "</assemblerResult>";
		System.out.println(resultXml);
		String responseXml = AssembleDocuments.convertAssemblerResultToxml(assemblerResult);
		assertThat(Input.fromString(responseXml), isIdenticalTo(Input.fromString(resultXml)).ignoreWhitespace());
	}
	
	private AssemblerResultImpl createAssemblerResultStub() {
		String data = "testDoPost Happy Path Result";
		Document mockDoc = mockDocumentFactory.create(data.getBytes())
											  .setContentType("application/pdf");
		Map<String, Document> sourceDocuments = Collections.singletonMap("concatenatedPDF.pdf", mockDoc);
		
		List<String> successfulBlockNames = new ArrayList<String>();
		successfulBlockNames.add("successBlock1");
		successfulBlockNames.add("successBlock2");
		
		List<String> successfulDocumentNames = new ArrayList<String>();
		successfulDocumentNames.add("successDocument1");
		successfulDocumentNames.add("successDocument2");

		List<String> failedBlockNames = new ArrayList<String>();
		failedBlockNames.add("failedBlock1");
		failedBlockNames.add("failedBlock2");
		
		List<String> docNames = new ArrayList<String>();
		docNames.add("test1");
		docNames.add("test2");
		
		Map<String,List<String>> multipleResultsBlocks = Collections.singletonMap("document", docNames);
		
		Map<String, OperationException> throwables = Collections.singletonMap("Exception", new OperationException("ExceptionMessage"));
		
		return new AssemblerResultImpl(sourceDocuments, 								// Source Documents
									   failedBlockNames, 								// Failed Block Names
									   mockDocumentFactory.create("INFO".getBytes()),	// Job Log
									   2,												// Last Bates Number 
									   multipleResultsBlocks, 							// Multiple Results Blocks
									   3,												// Num Requested Blocks
									   successfulBlockNames, 							// Successful Block Names
									   successfulDocumentNames,							// Successful Document Names
									   throwables);
	}

	public MockTraditionalAssemblerService mockAssemblePdf(AssemblerResult assemblerResult) throws NoSuchFieldException {
		assemblerResult.getDocuments().forEach((doName, doc) -> {
			doc.setContentType(APPLICATION_PDF);
		});
		MockTraditionalAssemblerService assemblerMock = MockTraditionalAssemblerService.createAssemblerMock(assemblerResult);
		junitx.util.PrivateAccessor.setField(underTest, "assemblerServiceFactory", (Supplier<TraditionalDocAssemblerService>)()->(TraditionalDocAssemblerService)assemblerMock);
		return assemblerMock;
	}
}
