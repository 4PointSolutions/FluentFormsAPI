package com._4point.aem.docservices.rest_services.server.pdfUtility;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.function.Supplier;

import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;
import com._4point.aem.fluentforms.impl.pdfUtility.TraditionalPdfUtilityService;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;
import com._4point.aem.fluentforms.testing.pdfUtility.MockPdfUtilityService;
import com._4point.aem.fluentforms.testing.pdfUtility.MockTraditionalPdfUtilityService;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
class ConvertPdfToXdpTest {
	private static final String APPLICATION_PDF = "application/pdf";
	private static final String APPLICATION_XDP = "application/vnd.adobe.xdp+xml";

	private static final String DOCUMENT_PARAM_NAME = "document";
	
	private final ConvertPdfToXdp underTest = new ConvertPdfToXdp();
	
	private final AemContext aemContext = new AemContext();

	private TestLogger loggerCapture = TestLoggerFactory.getTestLogger(ConvertPdfToXdp.class);

	private MockDocumentFactory mockDocumentFactory = new MockDocumentFactory();

	@BeforeEach
	void setUp() throws Exception {
		// Always use the MockDocumentFactory() in the class that's under test because the Adobe Document object has unresolved dependencies.
		junitx.util.PrivateAccessor.setField(underTest, "docFactory",  (DocumentFactory)SimpleDocumentFactoryImpl.getFactory());
	}

	@Test
	void testDoPost_HappyPath() throws Exception {
		String inputData = "testDoPost Happy Path Input";
		String resultData = "testDoPost Happy Path Result";
		byte[] resultDataBytes = resultData.getBytes();
		MockTraditionalPdfUtilityService mockService = mockPdfUtilityService(resultDataBytes);
		
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();
		
		request.addRequestParameter(DOCUMENT_PARAM_NAME, inputData.getBytes(), APPLICATION_PDF, null);
		
		underTest.doPost(request, response);
		
		// Validate the result
		assertEquals(SlingHttpServletResponse.SC_OK, response.getStatus());
		assertEquals(APPLICATION_XDP, response.getContentType());
		assertEquals(resultData, response.getOutputAsString());
		assertEquals(resultDataBytes.length, response.getContentLength());
		
		// Validate that the correct parameters were passed to the mock PdfUtilityService
		Document inputDoc = mockService.getInputDoc();
		assertNotNull(inputDoc);
		assertEquals(inputData, new String(inputDoc.getInlineData()));
	}

	public MockTraditionalPdfUtilityService mockPdfUtilityService(byte[] resultDataBytes) throws NoSuchFieldException {
		Document renderPdfResult = SimpleDocumentFactoryImpl.getFactory().create(resultDataBytes);
		renderPdfResult.setContentType(APPLICATION_XDP);
		MockTraditionalPdfUtilityService service = MockTraditionalPdfUtilityService.createDocumentMock(renderPdfResult);
		junitx.util.PrivateAccessor.setField(underTest, "pdfUtilityServiceFactory", (Supplier<TraditionalPdfUtilityService>)()->(TraditionalPdfUtilityService)service);
		return service;
	}
}
