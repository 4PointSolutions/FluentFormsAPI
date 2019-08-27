package com._4point.aem.docservices.rest_services.server.dor;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.function.Supplier;

import javax.el.MethodNotFoundException;
import javax.servlet.ServletException;

import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com._4point.aem.docservices.rest_services.server.ContentType;
import com._4point.aem.docservices.rest_services.server.dor.DocumentOfRecordService.DocumentOfRecordResult;
import com._4point.aem.docservices.rest_services.server.forms.RenderPdfForm;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.impl.forms.TraditionalFormsService;
import com._4point.aem.fluentforms.testing.forms.MockTraditionalFormsService;

import io.wcm.testing.mock.aem.junit5.AemContext;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

class DocumentOfRecordTest {

	private static final String TEMPLATE_PARAM = "template";
	private static final String DATA_PARAM = "data";

	private final DocumentOfRecord underTest =  new DocumentOfRecord();

	private final AemContext aemContext = new AemContext();

	private TestLogger loggerCapture = TestLoggerFactory.getTestLogger(DocumentOfRecord.class);

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testDoPostSlingHttpServletRequestSlingHttpServletResponse() throws Exception {
		String formUrl = "formUrl";
		String formData = "<formRoot><formData/></formRoot>";	// Needs to be valid XML.
		String expectedResult = "PDF Result"; 
		
		mockDorService(expectedResult.getBytes(), ContentType.APPLICATION_PDF);
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();

		request.addRequestParameter(TEMPLATE_PARAM, formUrl);
		request.addRequestParameter(DATA_PARAM, formData);

		// TODO: Make this work.
//		underTest.doPost(request, response);
		
		// Validate the result

	}
	
	private DocumentOfRecordService mockDorService(byte[] resultDataBytes, ContentType contentType) throws NoSuchFieldException {
		DocumentOfRecordResult dorResult = new MockDocumentOfRecordResult(resultDataBytes, contentType.toString());
		DocumentOfRecordService renderPdfMock = new MockDocumentOfRecordService(dorResult);
		junitx.util.PrivateAccessor.setField(underTest, "dorServiceFactory", (Supplier<DocumentOfRecordService>)()->(DocumentOfRecordService)renderPdfMock);
		return renderPdfMock;
	}

	private static class MockDocumentOfRecordService implements DocumentOfRecordService {

		private DocumentOfRecordResult dorResult;

		public MockDocumentOfRecordService(DocumentOfRecordResult dorResult) {
			this.dorResult = dorResult;
		}

		@Override
		public DocumentOfRecordResult render(DocumentOfRecordOptions dorOptions) throws DocumentOfRecordException {
			return dorResult;
		}
	}
	
	private static class MockDocumentOfRecordResult implements DocumentOfRecordResult {

		private final byte[] content;
		private final String contentType;
		
		private MockDocumentOfRecordResult(byte[] content, String contentType) {
			super();
			this.content = content;
			this.contentType = contentType;
		}

		@Override
		public byte[] getContent() {
			return this.content;
		}

		@Override
		public String getContentType() {
			return this.contentType;
		}

		@Override
		public Object getValue(String arg0) {
			throw new MethodNotFoundException("getValue method has not been implemented.");
		}
		
	}
}
