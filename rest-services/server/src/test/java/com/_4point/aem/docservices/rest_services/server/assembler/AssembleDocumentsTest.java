package com._4point.aem.docservices.rest_services.server.assembler;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com._4point.aem.docservices.rest_services.server.TestUtils;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.assembler.AssemblerOptionsSpec;
import com._4point.aem.fluentforms.api.assembler.AssemblerResult;
import com._4point.aem.fluentforms.impl.assembler.AdobeAssemblerServiceAdapter;
import com._4point.aem.fluentforms.impl.assembler.TraditionalDocAssemblerService;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;
import com._4point.aem.fluentforms.testing.assembler.MockTraditionalAssemblerService;
import com._4point.aem.fluentforms.testing.assembler.MockTraditionalAssemblerService.GenerateAssemblerResultArgs;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
public class AssembleDocumentsTest {
	private static final String APPLICATION_XML = "application/xml";
	private static final String APPLICATION_PDF = "application/pdf";
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
		String resultData = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><assemblerResult><resultDocument documentName=\"concatenatedPDF.pdf\"><mergedDoc>dGVzdERvUG9zdCBIYXBweSBQYXRoIFJlc3VsdA==</mergedDoc></resultDocument><failedBlockNames/><latestBatesNumber Number=\"-1\" numRequestedBlock=\"0\"/><numRequestedBlocks/><successfulDocumentNames/><successfulBlocNames/><multipleResultBlocs/></assemblerResult>" ;
		String data = "testDoPost Happy Path Result";
		String templateData = TestUtils.SAMPLE_DDX.toString();
		byte[] samplePdf1 = TestUtils.SAMPLE_PDF.toString().getBytes();
		byte[] samplePdf2 = TestUtils.SAMPLE_PDF.toString().getBytes();
		
		Boolean isFailOnError = false;
		Boolean isTakeOwnerShip = true;
		Boolean isValidateOnly = true;
		int firstBatesNumber = 0;
		String defaultStyle = "test";
		String logLevel = "DEBUG";

	    Map<String, Document> sourceDocuments = new HashMap<String, Document>();
	    sourceDocuments.put("concatenatedPDF.pdf", mockDocumentFactory.create(data.getBytes()));
		com.adobe.fd.assembler.client.AssemblerResult assemblerResult = new com.adobe.fd.assembler.client.AssemblerResult();
		AssemblerResult assemblerResult1 = new AdobeAssemblerServiceAdapter(sourceDocuments, assemblerResult);
		MockTraditionalAssemblerService assemblePdfMock = mockAssemblePdf(assemblerResult1);
		Map<String, Object> inputs = new HashMap<String, Object>();
		inputs.put("File0.pdf", samplePdf1);
		inputs.put("File1.pdf", samplePdf2);
		
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();

		request.addRequestParameter(DATA_PARAM_NAME, templateData);
		request.addRequestParameter(IS_FAIL_ON_ERROR, Boolean.toString(isFailOnError));
		request.addRequestParameter(IS_TAKE_OWNER_SHIP,  Boolean.toString(isTakeOwnerShip));
		request.addRequestParameter(FIRST_BATES_NUMBER, String.valueOf(firstBatesNumber));
		request.addRequestParameter(DEFAULT_STYLE, defaultStyle);
		request.addRequestParameter(LOG_LEVEL, logLevel);
		request.addRequestParameter(IS_VALIDATE_ONLY,  Boolean.toString(isValidateOnly));
		
		inputs.forEach((docName, doc) -> {
			request.addRequestParameter(SOURCE_DOCUMENT_KEY, docName);
			request.addRequestParameter(SOURCE_DOCUMENT_VALUE, (byte[]) doc, APPLICATION_PDF);
		});
		
		underTest.doPost(request, response);
		
		// Validate the result
		assertEquals(SlingHttpServletResponse.SC_OK, response.getStatus());
		assertEquals(APPLICATION_XML, response.getContentType());
		System.out.println(response.getOutputAsString());
		assertEquals(resultData.trim(), response.getOutputAsString());
		// Validate that the correct parameters were passed
		GenerateAssemblerResultArgs generateAssemblerResultArgs = assemblePdfMock.getGenerateAssemblerResultArgs();
		assertNotNull(generateAssemblerResultArgs.getDdx());
        AssemblerOptionsSpec assemblerOptionsSpec =
		  generateAssemblerResultArgs.getAssemblerOptionsSpec(); 
        assertAll(
		  ()->assertNotNull(assemblerOptionsSpec.getLogLevel()),
		  ()->assertNotNull(assemblerOptionsSpec.isFailOnError()),
		  ()->assertNotNull(assemblerOptionsSpec.isTakeOwnership()),
		  ()->assertNotNull(assemblerOptionsSpec.isValidateOnly()),
		  ()->assertNotNull(assemblerOptionsSpec.getDefaultStyle()),
		  ()->assertNotNull(assemblerOptionsSpec.getFirstBatesNumber()));	 
	}
	
	
	public MockTraditionalAssemblerService mockAssemblePdf(AssemblerResult assemblerResult) throws NoSuchFieldException {
		MockTraditionalAssemblerService assemblerMock = MockTraditionalAssemblerService.createAssemblerMock(assemblerResult);
		junitx.util.PrivateAccessor.setField(underTest, "assemblerServiceFactory", (Supplier<TraditionalDocAssemblerService>)()->(TraditionalDocAssemblerService)assemblerMock);
		return assemblerMock;
	}




}
