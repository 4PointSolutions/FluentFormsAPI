package com._4point.aem.docservices.rest_services.server.forms;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
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

	private static final String DATA_PARAM_NAME = "data";
	private static final String PDF_PARAM_NAME = "pdf";

	private static final String APPLICATION_XML = "application/xml";
	private static final String APPLICATION_PDF = "application/pdf";

	private final ImportData underTest =  new ImportData();

	private final AemContext aemContext = new AemContext();

	@SuppressWarnings("unused")
	private TestLogger loggerCapture = TestLoggerFactory.getTestLogger(ImportData.class);

	private MockDocumentFactory mockDocumentFactory = new MockDocumentFactory();

	@BeforeEach
	void setUp() throws Exception {
		// Always use the MockDocumentFactory() in the class that's under test because the Adobe Document object has unresolved dependencies.
		junitx.util.PrivateAccessor.setField(underTest, "docFactory",  (DocumentFactory)mockDocumentFactory);
	}
	
	@AfterEach
	void tearDown() throws Exception {
//		ImmutableList<LoggingEvent> loggingEvents = loggerCapture.getLoggingEvents();
//		for (LoggingEvent event:loggingEvents) {
//			System.out.println(event.toString());
//		}
	}

	@Test
	void testDoPost_HappyPath_Bytes(AemContext context) throws ServletException, IOException, FormsServiceException, NoSuchFieldException {

		String resultData = "testDoPost Happy Path Result";
		byte[] resultDataBytes = resultData.getBytes();
		MockTraditionalFormsService importDataMock = mockImportData(resultDataBytes);

		
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.resourceResolver(), aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();

		// Set request parameters
		String pdfDataBytes = "PDF Bytes";
		String xmlDataBytes = "XML Data";
		request.addRequestParameter(PDF_PARAM_NAME, pdfDataBytes.getBytes(), APPLICATION_PDF);
		request.addRequestParameter(DATA_PARAM_NAME, xmlDataBytes.getBytes(), APPLICATION_XML);

		request.setHeader("Accept", APPLICATION_PDF);

		
		underTest.doPost(request, response);
		
		// Validate the result
		assertEquals(SlingHttpServletResponse.SC_OK, response.getStatus());
		assertEquals(APPLICATION_PDF, response.getContentType());
		assertEquals(resultData, response.getOutputAsString());
		// assertEquals(resultDataBytes.length, response.getContentLength()); // We can't set the length because AEM throws an exception when we do.
		
		// Validate the inputs were used
		ImportDataArgs importDataArgs = importDataMock.getImportDataArgs();
		Document pdf = importDataArgs.getPdf();
		byte[] pdfData = pdf.getInlineData();
		assertArrayEquals(pdfDataBytes.getBytes(), pdfData);
		assertEquals(new String(pdfDataBytes), new String(pdfData));
		Document data = importDataArgs.getData();
		byte[] xmlData = data.getInlineData();
		assertArrayEquals(xmlDataBytes.getBytes(), xmlData);
		assertEquals(new String(xmlDataBytes), new String(xmlData));
		
		
	}

	@Disabled
	void testDoPost_HappyPath_Filenames(AemContext context) throws ServletException, IOException, FormsServiceException, NoSuchFieldException {

		String resultData = "testDoPost Happy Path Result";
		byte[] resultDataBytes = resultData.getBytes();
		MockTraditionalFormsService importDataMock = mockImportData(resultDataBytes);

		
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.resourceResolver(), aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();

		// Create inputs
		Map<String, Object> parameterMap = new HashMap<>();
		String pdfDataBytes = "PDF Bytes";
		parameterMap.put(PDF_PARAM_NAME, pdfDataBytes);
		String xmlDataBytes = "XML Data";
		parameterMap.put(DATA_PARAM_NAME, xmlDataBytes);
		
		// Set request parameters
		request.setParameterMap(parameterMap);

		request.setHeader("Accept", APPLICATION_PDF);

		
		underTest.doPost(request, response);
		
		// Validate the result
		assertEquals(SlingHttpServletResponse.SC_OK, response.getStatus());
		assertEquals(APPLICATION_PDF, response.getContentType());
		assertEquals(resultData, response.getOutputAsString());
		// assertEquals(resultDataBytes.length, response.getContentLength()); // We can't set the length because AEM throws an exception when we do.
		
		// Validate the inputs were used
		ImportDataArgs importDataArgs = importDataMock.getImportDataArgs();
		Document pdf = importDataArgs.getPdf();
		byte[] pdfData = pdf.getInlineData();
		assertArrayEquals(pdfDataBytes.getBytes(), pdfData);
		assertEquals(new String(pdfDataBytes), new String(pdfData));
		Document data = importDataArgs.getData();
		byte[] xmlData = data.getInlineData();
		assertArrayEquals(xmlDataBytes.getBytes(), xmlData);
		assertEquals(new String(xmlDataBytes), new String(xmlData));
	}


	@Test
	void testDoPost_NoPdfArg(AemContext context) throws ServletException, IOException, FormsServiceException, NoSuchFieldException {
		mockImportData(new byte[0]);
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.resourceResolver(), aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();

		// Create inputs
		Map<String, Object> parameterMap = new HashMap<>();
		String inputDataBytes = "XML Data";
		parameterMap.put(DATA_PARAM_NAME, inputDataBytes);
		// Set request parameters
		request.setParameterMap(parameterMap);

		underTest.doPost(request, response);
		
		assertEquals(SlingHttpServletResponse.SC_BAD_REQUEST, response.getStatus());
		assertThat("Expected message to contain parameter name", response.getStatusMessage(), containsString(PDF_PARAM_NAME));
		assertThat("Expected message to contain 'missing'", response.getStatusMessage(), containsStringIgnoringCase("missing"));
	}
	
	@Test
	void testDoPost_NoDataArg(AemContext context) throws ServletException, IOException, FormsServiceException, NoSuchFieldException {
		mockImportData(new byte[0]);
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.resourceResolver(), aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();

		// Create inputs
		Map<String, Object> parameterMap = new HashMap<>();
		String inputDataBytes = "PDF";
		parameterMap.put(PDF_PARAM_NAME, inputDataBytes);
		// Set request parameters
		request.setParameterMap(parameterMap);

		underTest.doPost(request, response);
		
		assertEquals(SlingHttpServletResponse.SC_BAD_REQUEST, response.getStatus());
		assertThat("Expected message to contain parameter name", response.getStatusMessage(), containsString(DATA_PARAM_NAME));
		assertThat("Expected message to contain 'missing'", response.getStatusMessage(), containsStringIgnoringCase("missing"));
	}
	
	@Test
	void testDoPost_BadAcceptHeader(AemContext context) throws ServletException, IOException, FormsServiceException, NoSuchFieldException {
		String resultData = "testDoPost Bad Accept Header Result";
		byte[] resultDataBytes = resultData.getBytes();
		mockImportData(resultDataBytes);

		
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.resourceResolver(), aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();

		// Set request parameters
		String pdfDataBytes = "PDF Bytes";
		String xmlDataBytes = "XML Data";
		request.addRequestParameter(PDF_PARAM_NAME, pdfDataBytes.getBytes(), APPLICATION_PDF);
		request.addRequestParameter(DATA_PARAM_NAME, xmlDataBytes.getBytes(), APPLICATION_XML);

		request.setHeader("Accept", "text/plain");
		
		underTest.doPost(request, response);
		
		assertEquals(SlingHttpServletResponse.SC_NOT_ACCEPTABLE, response.getStatus());
		
	}
	
	public MockTraditionalFormsService mockImportData(byte[] resultDataBytes) throws NoSuchFieldException {
		Document importDataResult = new MockImportDataResultDoc(resultDataBytes);
		importDataResult.setContentType(APPLICATION_XML);
		MockTraditionalFormsService importDataMock = MockTraditionalFormsService.createImportDataMock(importDataResult);
		junitx.util.PrivateAccessor.setField(underTest, "formServiceFactory", (Supplier<TraditionalFormsService>)()->(TraditionalFormsService)importDataMock);
		return importDataMock;
	}
	
	/**
	 * I created the following class to emulate the behaviour I am seeing when I perform integration testing.
	 * It seems that Adobe's code does not like performing certain methods (like getting ContentType and Length),
	 * it generates a UnsupportedOperationException.  This makes no sense to me (they know this is a PDF!) however
	 * I will try and emulate the Adobe behaviour in the unit test mock so that future maintainers will be alerted 
	 * earlier.  Be aware, however, that the Adobe behavior may change, so don't be afraid to allow the operation
	 * here - just be aware it may not work during integration testing.
	 * 
	 * My integration tests were conducted under AEM 6.5.  It may warrant a re-test every now and then to see if the
	 * Adobe behaviour has changed.
	 */
	private class MockImportDataResultDoc implements Document {

		Document importDataResult;
		boolean contentTypeSet = false;
		
		public MockImportDataResultDoc(byte[] resultDataBytes) {
			super();
			this.importDataResult = mockDocumentFactory.create(resultDataBytes);;
		}

		@Override
		public void close() throws IOException {
			this.importDataResult.close();
		}

		@Override
		public Document copyToFile(File arg0) throws IOException {
			this.importDataResult.copyToFile(arg0);
			return this;
		}

		@Override
		public void dispose() {
			this.importDataResult.dispose();
		}

		@Override
		public Object getAttribute(String name) {
			return this.importDataResult.getAttribute(name);
		}

		@Override
		public String getContentType() throws IOException {
			if (contentTypeSet) {
				return this.importDataResult.getContentType();
			}
			throw new UnsupportedOperationException("getContentType() not supported on PDF-based documents under AEM 6.5!");
		}

		@Override
		public byte[] getInlineData() throws IOException {
			return this.importDataResult.getInlineData();
		}

		@Override
		public InputStream getInputStream() throws IOException {
			return this.importDataResult.getInputStream();
		}

		@Override
		public int getMaxInlineSize() {
			return this.importDataResult.getMaxInlineSize();
		}

		@Override
		public long length() throws IOException {
			throw new UnsupportedOperationException("getLength() not supported on PDF-based documents under AEM 6.5!");
		}

		@Override
		public Document passivate() throws IOException {
			this.importDataResult.passivate();
			return this;
		}

		@Override
		public Document removeAttribute(String name) {
			this.importDataResult.removeAttribute(name);
			return this;
		}

		@Override
		public Document setAttribute(String name, Object val) {
			this.importDataResult.setAttribute(name, val);
			return this;
		}

		@Override
		public Document setContentType(String contentType) {
			this.contentTypeSet = true;
			this.importDataResult.setContentType(contentType);
			return this;
		}

		@Override
		public Document setMaxInlineSize(int maxInlineSize) {
			this.importDataResult.setMaxInlineSize(maxInlineSize);
			return this;
		}

	}
}
