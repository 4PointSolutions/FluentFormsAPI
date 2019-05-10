package com._4point.aem.docservices.rest_services.server.forms;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
import com._4point.aem.fluentforms.impl.forms.TraditionalFormsService;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;
import com._4point.aem.fluentforms.testing.forms.MockTraditionalFormsService;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
class RenderPdfFormTest {
	private static final String APPLICATION_XML = "application/xml";
	private static final String APPLICATION_PDF = "application/pdf";


	private final RenderPdfForm underTest =  new RenderPdfForm();

	private final AemContext aemContext = new AemContext();

	private TestLogger loggerCapture = TestLoggerFactory.getTestLogger(RenderPdfForm.class);

	private MockDocumentFactory mockDocumentFactory = new MockDocumentFactory();

	@BeforeEach
	void setUp() throws Exception {
		// Always use the MockDocumentFactory() in the class that's under test because the Adobe Document object has unresolved dependencies.
		junitx.util.PrivateAccessor.setField(underTest, "docFactory",  (DocumentFactory)mockDocumentFactory);
	}

	@Test
	void testDoPost_HappyPath() throws ServletException, IOException, NoSuchFieldException {
		String resultData = "testDoPost Happy Path Result";
		byte[] resultDataBytes = resultData.getBytes();
		MockTraditionalFormsService renderPdfMock = mockRenderForm(resultDataBytes);

		
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();
		
		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put("template", TestUtils.SAMPLE_FORM.toString());
		parameterMap.put("data", "formData");
		request.setParameterMap(parameterMap );
		
		underTest.doPost(request, response);
		
		// Validate the result
		assertEquals(SlingHttpServletResponse.SC_OK, response.getStatus());
		assertEquals(APPLICATION_PDF, response.getContentType());
		assertEquals(resultData, response.getOutputAsString());
		assertEquals(resultDataBytes.length, response.getContentLength());
	
	}
	
	public MockTraditionalFormsService mockRenderForm(byte[] resultDataBytes) throws NoSuchFieldException {
		Document renderPdfResult = mockDocumentFactory.create(resultDataBytes);
		renderPdfResult.setContentType(APPLICATION_PDF);
		MockTraditionalFormsService renderPdfMock = MockTraditionalFormsService.createRenderFormMock(renderPdfResult);
		junitx.util.PrivateAccessor.setField(underTest, "adobeFormsService",  (TraditionalFormsService)renderPdfMock);
		return renderPdfMock;
	}

}
