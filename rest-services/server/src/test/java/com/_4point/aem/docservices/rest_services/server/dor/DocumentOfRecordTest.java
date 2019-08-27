package com._4point.aem.docservices.rest_services.server.dor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.util.function.Supplier;

import javax.el.MethodNotFoundException;

import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.apache.sling.testing.resourceresolver.MockResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com._4point.aem.docservices.rest_services.server.ContentType;
import com._4point.aem.docservices.rest_services.server.dor.DocumentOfRecordService.DocumentOfRecordOptions;
import com._4point.aem.docservices.rest_services.server.dor.DocumentOfRecordService.DocumentOfRecordResult;

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
	void testDoPost() throws Exception {
		String formUrl = "/content/binary/formUrl.xdp";
		String formData = "<formRoot><formData/></formRoot>";	// Needs to be valid XML.
		String expectedResult = "PDF Result"; 
		
		aemContext.load().binaryFile(new ByteArrayInputStream(formUrl.getBytes()), formUrl, ContentType.APPLICATION_XDP.toString());
		MockDocumentOfRecordService mockDorService = mockDorService(expectedResult.getBytes(), ContentType.APPLICATION_PDF);
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.resourceResolver(), aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();

		request.addRequestParameter(TEMPLATE_PARAM, formUrl);
		request.addRequestParameter(DATA_PARAM, formData);

		underTest.doPost(request, response);
		
		// Validate the result
		assertEquals(SlingHttpServletResponse.SC_OK, response.getStatus());
		assertEquals(ContentType.APPLICATION_PDF.toString(), response.getContentType());
		assertEquals(expectedResult, response.getOutputAsString());
		assertEquals(expectedResult.getBytes().length, response.getContentLength());
		
		// Validate that the correct parameters were passed in to DoRService
		DocumentOfRecordOptions dorOptions = mockDorService.getDorOptions();
		assertTrue(dorOptions.getData().contains(formData), "Expected '" + formData + "' to occur within '" + dorOptions.getData() + "'." );
		assertEquals(formUrl, dorOptions.getFormResource().getPath());
	}
	
	@Test
	void testDoPost_RenderException() throws Exception {
		String formUrl = "formUrl";
		String formData = "<formRoot><formData/></formRoot>";	// Needs to be valid XML.
		String expectedMessage = "Exception Message"; 

		junitx.util.PrivateAccessor.setField(underTest, "dorServiceFactory", (Supplier<DocumentOfRecordService>)()->new DocumentOfRecordService() {
			@Override
			public DocumentOfRecordResult render(DocumentOfRecordOptions dorOptions) throws DocumentOfRecordException {
				throw new DocumentOfRecordException(expectedMessage);
			}
		});

		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();

		request.addRequestParameter(TEMPLATE_PARAM, formUrl);
		request.addRequestParameter(DATA_PARAM, formData);

		underTest.doPost(request, response);
		
		// Validate the result
		assertEquals(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR, response.getStatus());
		assertNull(response.getContentType());
	}
	
	@Test
	void testDoPost_InvalidXml() throws Exception {
		String formUrl = "formUrl";
		String formData = "<formRoot><formData></formRoot>";	// Invalid XML (formData element is not closed).
		
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();

		request.addRequestParameter(TEMPLATE_PARAM, formUrl);
		request.addRequestParameter(DATA_PARAM, formData);

		underTest.doPost(request, response);
		
		// Validate the result
		assertEquals(SlingHttpServletResponse.SC_BAD_REQUEST, response.getStatus());
		assertNull(response.getContentType());
	}
	
	private MockDocumentOfRecordService mockDorService(byte[] resultDataBytes, ContentType contentType) throws NoSuchFieldException {
		DocumentOfRecordResult dorResult = new MockDocumentOfRecordResult(resultDataBytes, contentType.toString());
		MockDocumentOfRecordService renderPdfMock = new MockDocumentOfRecordService(dorResult);
		junitx.util.PrivateAccessor.setField(underTest, "dorServiceFactory", (Supplier<DocumentOfRecordService>)()->(DocumentOfRecordService)renderPdfMock);
		return renderPdfMock;
	}

	private static class MockDocumentOfRecordService implements DocumentOfRecordService {

		private DocumentOfRecordResult dorResult;
		private DocumentOfRecordOptions dorOptions;

		public MockDocumentOfRecordService(DocumentOfRecordResult dorResult) {
			this.dorResult = dorResult;
		}

		@Override
		public DocumentOfRecordResult render(DocumentOfRecordOptions dorOptions) throws DocumentOfRecordException {
			this.dorOptions = dorOptions;
			return dorResult;
		}

		public DocumentOfRecordOptions getDorOptions() {
			return dorOptions;
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
