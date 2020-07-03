package com._4point.aem.docservices.rest_services.server.forms;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.CoreMatchers.*;
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
import com._4point.aem.fluentforms.testing.forms.MockTraditionalFormsService.ExportDataArgs;
import com._4point.aem.fluentforms.testing.forms.MockTraditionalFormsService.ImportDataArgs;
import com.adobe.fd.forms.api.DataFormat;
import com.adobe.fd.forms.api.FormsServiceException;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;


@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
class ExportDataTest {


	private static final String APPLICATION_XML = "application/xml";
	private static final String APPLICATION_PDF = "application/pdf";

	private final ExportData underTest =  new ExportData();

	private final AemContext aemContext = new AemContext();

	private TestLogger loggerCapture = TestLoggerFactory.getTestLogger(ExportData.class);

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
		MockTraditionalFormsService exportDataMock = mockExportData(resultDataBytes);

		
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();

		// Set request parameters
		String pdfDataBytes = "PDF Bytes";
		String dataformat="XmlData";
		//DataFormat dataformat =com.adobe.fd.forms.api.DataFormat.XmlData;
		
		request.addRequestParameter("pdforxdp", pdfDataBytes.getBytes(), APPLICATION_PDF);
		request.addRequestParameter("dataformat", dataformat.getBytes(), APPLICATION_XML);

		request.setHeader("Accept", APPLICATION_XML);

		
		underTest.doPost(request, response);
		
		// Validate the result
		assertEquals(SlingHttpServletResponse.SC_OK, response.getStatus());
		assertEquals(APPLICATION_XML, response.getContentType());
		assertEquals(resultData, response.getOutputAsString());
		// assertEquals(resultDataBytes.length, response.getContentLength()); // We can't set the length because AEM throws an exception when we do.
		
		// Validate the inputs were used
		ExportDataArgs exportDataArgs = exportDataMock.getExportDataArgs();
		Document pdforxdp = exportDataArgs.getPdfOrXdp();
		DataFormat data=exportDataArgs.getDataFormat();
		byte[] pdforxdpData = pdforxdp.getInlineData();
		assertArrayEquals(pdfDataBytes.getBytes(), pdforxdpData);
		assertEquals(dataformat.toString(), data.toString());		
	}

	
	@Test
	void testDoPost_NoDataArg(AemContext context) throws ServletException, IOException, FormsServiceException, NoSuchFieldException {
		MockTraditionalFormsService exportDataMock = mockExportData(new byte[0]);
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();

		// Create inputs
		Map<String, Object> parameterMap = new HashMap<>();
		String inputDataBytes = "PDF";
		parameterMap.put("pdforxdp", inputDataBytes);
		// Set request parameters
		request.setParameterMap(parameterMap);

		underTest.doPost(request, response);
		
		assertEquals(SlingHttpServletResponse.SC_BAD_REQUEST, response.getStatus());
		//assertThat("Expected message to contain parameter name", response.getStatusMessage(), containsString(DATA_PARAM_NAME));
		assertThat("Expected message to contain 'missing'", response.getStatusMessage(), containsStringIgnoringCase("missing"));
	}
	
	@Test
	void testDoPost_BadAcceptHeader(AemContext context) throws ServletException, IOException, FormsServiceException, NoSuchFieldException {
		String resultData = "testDoPost Bad Accept Header Result";
		byte[] resultDataBytes = resultData.getBytes();
		MockTraditionalFormsService exportDataMock = mockExportData(resultDataBytes);

		
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();

		// Set request parameters
		String pdfDataBytes = "PDF Bytes";
		String xmlDataBytes = "XmlData";
		request.addRequestParameter("pdforxdp", pdfDataBytes.getBytes(), APPLICATION_PDF);
		request.addRequestParameter("dataformat", xmlDataBytes.getBytes(),APPLICATION_XML);

		request.setHeader("Accept", "text/plain");
		
		underTest.doPost(request, response);
		
		assertEquals(SlingHttpServletResponse.SC_NOT_ACCEPTABLE, response.getStatus());
		
	}
	
	public MockTraditionalFormsService mockExportData(byte[] resultDataBytes) throws NoSuchFieldException {
		Document exportDataResult = new MockExportDataResultDoc(resultDataBytes);
		exportDataResult.setContentType(APPLICATION_XML);
		MockTraditionalFormsService exportDataMock = MockTraditionalFormsService.createExportDataMock(exportDataResult);
		junitx.util.PrivateAccessor.setField(underTest, "formServiceFactory", (Supplier<TraditionalFormsService>)()->(TraditionalFormsService)exportDataMock);
		return exportDataMock;
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
	private class MockExportDataResultDoc implements Document {

		Document exportDataResult;
		
		public MockExportDataResultDoc(byte[] resultDataBytes) {
			super();
			this.exportDataResult = mockDocumentFactory.create(resultDataBytes);;
		}

		

		@Override
		public void copyToFile(File arg0) throws IOException {
			this.exportDataResult.copyToFile(arg0);
		}

		@Override
		public void dispose() {
			this.exportDataResult.dispose();
		}

		@Override
		public Object getAttribute(String name) {
			return this.exportDataResult.getAttribute(name);
		}

		@Override
		public String getContentType() throws IOException {
			throw new UnsupportedOperationException("getContentType() not supported on PDF-based documents under AEM 6.5!");
		}

		@Override
		public byte[] getInlineData() throws IOException {
			return this.exportDataResult.getInlineData();
		}

		@Override
		public InputStream getInputStream() throws IOException {
			return this.exportDataResult.getInputStream();
		}

		@Override
		public int getMaxInlineSize() {
			return this.exportDataResult.getMaxInlineSize();
		}

		@Override
		public long length() throws IOException {
			throw new UnsupportedOperationException("getLength() not supported on PDF-based documents under AEM 6.5!");
		}

		@Override
		public void passivate() throws IOException {
			this.exportDataResult.passivate();
		}

		@Override
		public void removeAttribute(String name) {
			this.exportDataResult.removeAttribute(name);
		}

		@Override
		public void setAttribute(String name, Object val) {
			this.exportDataResult.setAttribute(name, val);
		}

		@Override
		public void setContentType(String contentType) {
			this.exportDataResult.setContentType(contentType);
		}

		@Override
		public void setMaxInlineSize(int maxInlineSize) {
			this.exportDataResult.setMaxInlineSize(maxInlineSize);
		}



		@Override
		public void close() throws IOException {
			// TODO Auto-generated method stub
			
		}

	}
}

