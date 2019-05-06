package com._4point.aem.docservices.rest_services.server.forms;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.impl.forms.AdobeFormsServiceAdapter;
import com._4point.aem.fluentforms.impl.forms.TraditionalFormsService;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;
import com._4point.aem.fluentforms.testing.forms.MockTraditionalFormsService;
import com._4point.aem.fluentforms.testing.forms.MockTraditionalFormsService.ImportDataArgs;
import com.adobe.fd.forms.api.FormsServiceException;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;


@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
class ImportDataTest {

	private static final String APPLICATION_XML = "application/xml";

	private final ImportData underTest =  new ImportData();

	private final AemContext aemContext = new AemContext();

	private TestLogger loggerCapture = TestLoggerFactory.getTestLogger(ImportData.class);

	private MockDocumentFactory mockDocumentFactory = new MockDocumentFactory();

	@BeforeEach
	void setUp() throws Exception {
	}
	

	@Test
	void testDoPost_HappyPath(AemContext context) throws ServletException, IOException, FormsServiceException, NoSuchFieldException {

		String resultData = "testDoPost Happy Path Result";
		byte[] resultDataBytes = resultData.getBytes();
		MockTraditionalFormsService importDataMock = mockImportData(resultDataBytes);

		
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();

		// Create inputs
		Map<String, Object> parameterMap = new HashMap<>();
		String inputDataBytes = "InputData";
		parameterMap.put("pdf", inputDataBytes);
		
		// Set reqeust parameters
		request.setParameterMap(parameterMap);

		
		underTest.doPost(request, response);
		
		// Validate the result
		assertEquals(200, response.getStatus());
		assertEquals(APPLICATION_XML, response.getContentType());
		assertEquals(resultData, response.getOutputAsString());
		assertEquals(resultDataBytes.length, response.getContentLength());
		
		// Validate the inputs were used
		ImportDataArgs importDataArgs = importDataMock.getImportDataArgs();
		Document pdf = importDataArgs.getPdf();
		byte[] inlineData = pdf.getInlineData();
		assertArrayEquals(inputDataBytes.getBytes(), inlineData);
		assertEquals(new String(inputDataBytes), new String(inlineData));
	}


	@Test
	void testDoPost_NoPdfArg(AemContext context) throws ServletException, IOException, FormsServiceException, NoSuchFieldException {
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();

		underTest.doPost(request, response);
		
		assertEquals(400, response.getStatus());
		assertThat("", response.geStatusMessage(), containsString("pdf"));
		assertThat("", response.geStatusMessage(), containsStringIgnoringCase("missing"));
	}
	
	public MockTraditionalFormsService mockImportData(byte[] resultDataBytes) throws NoSuchFieldException {
		Document importDataResult = mockDocumentFactory.create(resultDataBytes);
		importDataResult.setContentType(APPLICATION_XML);
		MockTraditionalFormsService importDataMock = MockTraditionalFormsService.createImportDataMock(importDataResult);
		junitx.util.PrivateAccessor.setField(underTest, "adobeFormsService",  (TraditionalFormsService)importDataMock);
		junitx.util.PrivateAccessor.setField(underTest, "docFactory",  (DocumentFactory)mockDocumentFactory);
		return importDataMock;
	}
	
}
