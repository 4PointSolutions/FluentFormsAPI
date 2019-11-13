package com._4point.aem.docservices.rest_services.server.output;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.containsStringIgnoringCase;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

import javax.servlet.ServletException;

import org.apache.sling.api.SlingConstants;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com._4point.aem.docservices.rest_services.server.ContentType;
import com._4point.aem.docservices.rest_services.server.TestUtils;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.output.PDFOutputOptions;
import com._4point.aem.fluentforms.impl.output.TraditionalOutputService;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;
import com._4point.aem.fluentforms.testing.output.ExceptionalMockTraditionalOutputService;
import com._4point.aem.fluentforms.testing.output.MockTraditionalOutputService;
import com._4point.aem.fluentforms.testing.output.MockTraditionalOutputService.GeneratePdfArgs;
import com.adobe.fd.output.api.AcrobatVersion;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
class GeneratePdfOutputTest {
	private static final String APPLICATION_XML = "application/xml";
	private static final String APPLICATION_PDF = "application/pdf";
	private static final String APPLICATION_XDP = "application/vnd.adobe.xdp+xml";
	private static final String TEXT_PLAIN = "text/plain";
	private static final String TEXT_HTML = "text/html";

	private static final String TEMPLATE_PARAM = "template";
	private static final String DATA_PARAM = "data";
	private static final String ACROBAT_VERSION_PARAM = "outputOptions.acrobatVersion";
	private static final String CONTENT_ROOT_PARAM = "outputOptions.contentRoot";
	private static final String DEBUG_DIR_PARAM = "outputOptions.debugDir";
	private static final String EMBED_FONTS_PARAM = "outputOptions.embedFonts";
	private static final String LINEARIZED_PDF_PARAM = "outputOptions.linearizedPdf";
	private static final String LOCALE_PARAM = "outputOptions.locale";
	private static final String RETAIN_PDF_FORM_STATE_PARAM = "outputOptions.retainPdfFormState";
	private static final String RETAIN_UNSIGNED_SIGNATURE_FIELDS_PARAM = "outputOptions.retainUnsignedSignatureFields";
	private static final String TAGGED_PDF_PARAM = "outputOptions.taggedPdf";
	private static final String XCI_PARAM = "outputOptions.xci";


	private final GeneratePdfOutput underTest =  new GeneratePdfOutput();

	private final AemContext aemContext = new AemContext();

	private TestLogger loggerCapture = TestLoggerFactory.getTestLogger(GeneratePdfOutput.class);

	private MockDocumentFactory mockDocumentFactory = new MockDocumentFactory();

	@BeforeEach
	void setUp() throws Exception {
		// Always use the MockDocumentFactory() in the class that's under test because the Adobe Document object has unresolved dependencies.
		junitx.util.PrivateAccessor.setField(underTest, "docFactory",  (DocumentFactory)mockDocumentFactory);
	}

	@Test
	void testDoPost_HappyPath_JustForm() throws ServletException, IOException, NoSuchFieldException {
		String resultData = "testDoPost Happy Path Result";
		String templateData = TestUtils.SAMPLE_FORM.toString();
		
		byte[] resultDataBytes = resultData.getBytes();
		MockTraditionalOutputService generatePdfMock = mockGeneratePdf(resultDataBytes);

		
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();

		request.addRequestParameter(TEMPLATE_PARAM, templateData);
		
		underTest.doPost(request, response);
		
		// Validate the result
		assertEquals(SlingHttpServletResponse.SC_OK, response.getStatus());
		assertEquals(APPLICATION_PDF, response.getContentType());
		assertEquals(resultData, response.getOutputAsString());
		assertEquals(resultDataBytes.length, response.getContentLength());
	
		// Validate that the correct parameters were passed in to renderPdf
		GeneratePdfArgs generatePdfArgs = generatePdfMock.getGeneratePdfArgs();
		assertNull(generatePdfArgs.getData());
		assertEquals(TestUtils.SAMPLE_FORM.getFileName().toString(), generatePdfArgs.getUrlOrFilename());
		PDFOutputOptions pdfOutputOptions = generatePdfArgs.getPdfOutputOptions();
		assertAll(
				()->assertNull(pdfOutputOptions.getAcrobatVersion()),
				()->assertEquals(TestUtils.SAMPLE_FORM.getParent(), pdfOutputOptions.getContentRoot().getPath()),
				()->assertNull(pdfOutputOptions.getDebugDir()),
				()->assertNull(pdfOutputOptions.getEmbedFonts()),
				()->assertNull(pdfOutputOptions.getLinearizedPDF()),
				()->assertNull(pdfOutputOptions.getLocale()),
				()->assertNull(pdfOutputOptions.getRetainPDFFormState()),
				()->assertNull(pdfOutputOptions.getRetainUnsignedSignatureFields()),
				()->assertNull(pdfOutputOptions.getTaggedPDF()),
				()->assertNull(pdfOutputOptions.getXci())
			);
	}

	@Test
	void testDoPost_HappyPath_JustForm_Doc() throws ServletException, IOException, NoSuchFieldException {
		String resultData = "testDoPost Happy Path Result";
		byte[] templateData = Files.readAllBytes(TestUtils.SAMPLE_FORM);
		
		byte[] resultDataBytes = resultData.getBytes();
		MockTraditionalOutputService generatePdfMock = mockGeneratePdf(resultDataBytes);

		
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();

		request.addRequestParameter(TEMPLATE_PARAM, templateData, ContentType.APPLICATION_XDP.toString());
		
		underTest.doPost(request, response);
		
		// Validate the result
		assertEquals(SlingHttpServletResponse.SC_OK, response.getStatus());
		assertEquals(APPLICATION_PDF, response.getContentType());
		assertEquals(resultData, response.getOutputAsString());
		assertEquals(resultDataBytes.length, response.getContentLength());
	
		// Validate that the correct parameters were passed in to renderPdf
		GeneratePdfArgs generatePdfArgs = generatePdfMock.getGeneratePdfArgs();
		assertNull(generatePdfArgs.getData());
		assertEquals(templateData, generatePdfArgs.getTemplate().getInlineData());
		PDFOutputOptions pdfOutputOptions = generatePdfArgs.getPdfOutputOptions();
		assertAll(
				()->assertNull(pdfOutputOptions.getAcrobatVersion()),
				()->assertNull(pdfOutputOptions.getContentRoot()),
				()->assertNull(pdfOutputOptions.getDebugDir()),
				()->assertNull(pdfOutputOptions.getEmbedFonts()),
				()->assertNull(pdfOutputOptions.getLinearizedPDF()),
				()->assertNull(pdfOutputOptions.getLocale()),
				()->assertNull(pdfOutputOptions.getRetainPDFFormState()),
				()->assertNull(pdfOutputOptions.getRetainUnsignedSignatureFields()),
				()->assertNull(pdfOutputOptions.getTaggedPDF()),
				()->assertNull(pdfOutputOptions.getXci())
			);
	}

	@Test
	void testDoPost_HappyPath_JustFormAndData() throws ServletException, IOException, NoSuchFieldException {
		String formData = "formData";
		String resultData = "testDoPost Happy Path Result";
		String templateData = TestUtils.SAMPLE_FORM.toString();
		
		byte[] resultDataBytes = resultData.getBytes();
		MockTraditionalOutputService generatePdfMock = mockGeneratePdf(resultDataBytes);

		
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();
		

		request.addRequestParameter(TEMPLATE_PARAM, templateData);
		request.addRequestParameter(DATA_PARAM, formData);
		
		underTest.doPost(request, response);
		
		// Validate the result
		assertEquals(SlingHttpServletResponse.SC_OK, response.getStatus());
		assertEquals(APPLICATION_PDF, response.getContentType());
		assertEquals(resultData, response.getOutputAsString());
		assertEquals(resultDataBytes.length, response.getContentLength());
	
		// Validate that the correct parameters were passed in to renderPdf
		GeneratePdfArgs generatePdfArgs = generatePdfMock.getGeneratePdfArgs();
		assertArrayEquals(formData.getBytes(), generatePdfArgs.getData().getInlineData());
		assertEquals(TestUtils.SAMPLE_FORM.getFileName().toString(), generatePdfArgs.getUrlOrFilename());
		PDFOutputOptions pdfOutputOptions = generatePdfArgs.getPdfOutputOptions();
		assertAll(
				()->assertNull(pdfOutputOptions.getAcrobatVersion()),
				()->assertEquals(TestUtils.SAMPLE_FORM.getParent(), pdfOutputOptions.getContentRoot().getPath()),
				()->assertNull(pdfOutputOptions.getDebugDir()),
				()->assertNull(pdfOutputOptions.getEmbedFonts()),
				()->assertNull(pdfOutputOptions.getLinearizedPDF()),
				()->assertNull(pdfOutputOptions.getLocale()),
				()->assertNull(pdfOutputOptions.getRetainPDFFormState()),
				()->assertNull(pdfOutputOptions.getRetainUnsignedSignatureFields()),
				()->assertNull(pdfOutputOptions.getTaggedPDF()),
				()->assertNull(pdfOutputOptions.getXci())
			);
	}

	@Test
	void testDoPost_HappyPath_MaxArgs() throws ServletException, IOException, NoSuchFieldException {
		String formData = "formData";
		String resultData = "testDoPost Happy Path Result";
		String templateData = TestUtils.SAMPLE_FORM.getParent().getFileName().resolve(TestUtils.SAMPLE_FORM.getFileName()).toString();
		String acrobatVersionData = "Acrobat_10";
		String contentRootData = TestUtils.SAMPLE_FORM.getParent().getParent().toString();
		String debugDirData = "/debug/dir";
		boolean embedFonts = true; 
		boolean linearizedPdfData = true;
		String localeData = "en-CA";
		boolean retainPdfFormStateData = true;
		boolean retainUnsignedSignatureFieldsData = true;
		boolean taggedPdfData = true;
		String xciData = "Xci Data";
		
		byte[] resultDataBytes = resultData.getBytes();
		MockTraditionalOutputService generatePdfMock = mockGeneratePdf(resultDataBytes);

		
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();
		
		request.addRequestParameter(TEMPLATE_PARAM, templateData);
		request.addRequestParameter(DATA_PARAM, formData.getBytes(), APPLICATION_XML);
		request.addRequestParameter(ACROBAT_VERSION_PARAM, acrobatVersionData);
		request.addRequestParameter(CONTENT_ROOT_PARAM, contentRootData);
		request.addRequestParameter(DEBUG_DIR_PARAM, debugDirData);
		request.addRequestParameter(EMBED_FONTS_PARAM, Boolean.toString(embedFonts));
		request.addRequestParameter(LINEARIZED_PDF_PARAM, Boolean.toString(linearizedPdfData));
		request.addRequestParameter(LOCALE_PARAM, localeData);
		request.addRequestParameter(RETAIN_PDF_FORM_STATE_PARAM, Boolean.toString(retainPdfFormStateData));
		request.addRequestParameter(RETAIN_UNSIGNED_SIGNATURE_FIELDS_PARAM, Boolean.toString(retainUnsignedSignatureFieldsData));
		request.addRequestParameter(TAGGED_PDF_PARAM, Boolean.toString(taggedPdfData));
		request.addRequestParameter(XCI_PARAM, xciData.getBytes(), APPLICATION_XML);
		
		underTest.doPost(request, response);
		
		// Validate the result
		assertEquals(SlingHttpServletResponse.SC_OK, response.getStatus(), "Expected OK Status code.  Response='" + response.getStatusMessage() + "'");
		assertEquals(APPLICATION_PDF, response.getContentType());
		assertEquals(resultData, response.getOutputAsString());
		assertEquals(resultDataBytes.length, response.getContentLength());
	
		// Validate that the correct parameters were passed in to renderPdf
		GeneratePdfArgs generatePdfArgs = generatePdfMock.getGeneratePdfArgs();
		assertArrayEquals(formData.getBytes(), generatePdfArgs.getData().getInlineData());
		assertEquals(TestUtils.SAMPLE_FORM.getFileName().toString(), generatePdfArgs.getUrlOrFilename());
		PDFOutputOptions pdfOutputOptions = generatePdfArgs.getPdfOutputOptions();
		assertAll(
				()->assertEquals(AcrobatVersion.Acrobat_10, pdfOutputOptions.getAcrobatVersion()),	// AEM 6.5 Default
				()->assertEquals(TestUtils.SAMPLE_FORM.getParent(), pdfOutputOptions.getContentRoot().getPath()),
				()->assertEquals(Paths.get(debugDirData), pdfOutputOptions.getDebugDir()),
				()->assertTrue(pdfOutputOptions.getEmbedFonts()),	// AEM 6.5 Default
				()->assertTrue(pdfOutputOptions.getLinearizedPDF()),
				()->assertEquals(Locale.forLanguageTag(localeData), pdfOutputOptions.getLocale()),
				()->assertTrue(pdfOutputOptions.getRetainPDFFormState()),
				()->assertTrue(pdfOutputOptions.getRetainUnsignedSignatureFields()),
				()->assertTrue(pdfOutputOptions.getTaggedPDF()),
				()->assertArrayEquals(xciData.getBytes(), pdfOutputOptions.getXci().getInlineData())
		);
	}

	@Test
	void testDoPost_HappyPath_BadAcrobatVersion() throws ServletException, IOException, NoSuchFieldException {
		String formData = "formData";
		String resultData = "testDoPost Happy Path Result";
		String templateData = TestUtils.SAMPLE_FORM.toString();
		String acrobatVersionData = "Acrobat_5";
		
		byte[] resultDataBytes = resultData.getBytes();
		MockTraditionalOutputService generatePdfMock = mockGeneratePdf(resultDataBytes);

		
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();
		
		request.addRequestParameter(TEMPLATE_PARAM, templateData);
		request.addRequestParameter(DATA_PARAM, formData.getBytes(), APPLICATION_XML);
		request.addRequestParameter(ACROBAT_VERSION_PARAM, acrobatVersionData);
		
		underTest.doPost(request, response);
		
		// Validate the result
		assertEquals(SlingHttpServletResponse.SC_BAD_REQUEST, response.getStatus(), "Expected Bad Status code.  Response='" + response.getStatusMessage() + "'");
		assertThat(response.getStatusMessage(), containsStringIgnoringCase("incoming parameters"));
	}

	@Test
	void testDoPost_HappyPath_BadContentRoot() throws ServletException, IOException, NoSuchFieldException {
		String formData = "formData";
		String resultData = "testDoPost Happy Path Result";
		String templateData = TestUtils.SAMPLE_FORM.toString();
		String contentRootData = "foo:bar:other";
		
		byte[] resultDataBytes = resultData.getBytes();
		MockTraditionalOutputService generatePdfMock = mockGeneratePdf(resultDataBytes);

		
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();
		
		request.addRequestParameter(TEMPLATE_PARAM, templateData);
		request.addRequestParameter(DATA_PARAM, formData.getBytes(), APPLICATION_XML);
		request.addRequestParameter(CONTENT_ROOT_PARAM, contentRootData);
		
		underTest.doPost(request, response);
		
		// Validate the result
		assertEquals(SlingHttpServletResponse.SC_BAD_REQUEST, response.getStatus(), "Expected Bad Status code.  Response='" + response.getStatusMessage() + "'");
		assertThat(response.getStatusMessage(), containsStringIgnoringCase("incoming parameters"));
	}

	@Test
	void testDoPost_HappyPath_BadDebugDir() throws ServletException, IOException, NoSuchFieldException {
		String formData = "formData";
		String resultData = "testDoPost Happy Path Result";
		String templateData = TestUtils.SAMPLE_FORM.toString();
		String debugDirData = "////////";
		
		byte[] resultDataBytes = resultData.getBytes();
		MockTraditionalOutputService generatePdfMock = mockGeneratePdf(resultDataBytes);

		
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();
		
		request.addRequestParameter(TEMPLATE_PARAM, templateData);
		request.addRequestParameter(DATA_PARAM, formData.getBytes(), APPLICATION_XML);
		request.addRequestParameter(DEBUG_DIR_PARAM, debugDirData);
		
		underTest.doPost(request, response);
		
		// Validate the result
		assertEquals(SlingHttpServletResponse.SC_BAD_REQUEST, response.getStatus(), "Expected Bad Status code.  Response='" + response.getStatusMessage() + "'");
		assertThat(response.getStatusMessage(), containsStringIgnoringCase("incoming parameters"));
	}

	@Test
	void testDoPost_BadForm() throws ServletException, IOException, NoSuchFieldException {
		String resultData = "testDoPost Happy Path Result";
		byte[] resultDataBytes = resultData.getBytes();
		MockTraditionalOutputService generatePdfMock = mockGeneratePdf(resultDataBytes);

		
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();
		
		Map<String, Object> parameterMap = new HashMap<>();
		String badFormName = "bar.xdp";
		parameterMap.put("template", "foo/" + badFormName);
		parameterMap.put("data", "formData");
		request.setParameterMap(parameterMap );
		
		underTest.doPost(request, response);
		
		// Validate the result
		assertEquals(SlingHttpServletResponse.SC_BAD_REQUEST, response.getStatus());
		String statusMsg = response.getStatusMessage();
		assertThat(statusMsg, containsStringIgnoringCase("Bad request parameter"));
		assertThat(statusMsg, containsStringIgnoringCase("unable to find template"));
		assertThat(statusMsg, containsString(badFormName));
	}

	@Test
	void testDoPost_OutputServiceException() throws ServletException, IOException, NoSuchFieldException {
		String formData = "formData";
		String resultData = "testDoPost Happy Path Result";
		String templateData = TestUtils.SAMPLE_FORM.toString();
		byte[] resultDataBytes = resultData.getBytes();
		String exceptionMessage = "Exception Message";
		junitx.util.PrivateAccessor.setField(underTest, "outputServiceFactory", (Supplier<TraditionalOutputService>)()->(TraditionalOutputService)ExceptionalMockTraditionalOutputService.create(exceptionMessage));

		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();
		
		request.addRequestParameter(TEMPLATE_PARAM, templateData);
		request.addRequestParameter(DATA_PARAM, formData);
		
		underTest.doPost(request, response);
		
		// Validate the result
		assertEquals(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR, response.getStatus());
		String statusMsg = response.getStatusMessage();
		assertThat(statusMsg, containsStringIgnoringCase("Internal Error while rendering PDF"));
		assertThat(statusMsg, containsStringIgnoringCase(exceptionMessage));
	}

	@Test
	void testDoPost_HappyPath_BadAccept() throws ServletException, IOException, NoSuchFieldException {
		String formData = "formData";
		String resultData = "testDoPost Happy Path Result";
		String templateData = TestUtils.SAMPLE_FORM.toString();
		
		byte[] resultDataBytes = resultData.getBytes();
		MockTraditionalOutputService generatePdfMock = mockGeneratePdf(resultDataBytes);

		
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();
		

		request.addRequestParameter(TEMPLATE_PARAM, templateData);
		request.addRequestParameter(DATA_PARAM, formData);
		request.addHeader("Accept", TEXT_HTML);
		
		underTest.doPost(request, response);
		
		// Validate the result
		assertEquals(SlingHttpServletResponse.SC_NOT_ACCEPTABLE, response.getStatus());
		String statusMsg = response.getStatusMessage();
		assertThat(statusMsg, containsStringIgnoringCase(TEXT_HTML));
		assertThat(statusMsg, containsStringIgnoringCase(APPLICATION_PDF));
	}

	public MockTraditionalOutputService mockGeneratePdf(byte[] resultDataBytes) throws NoSuchFieldException {
		Document renderPdfResult = mockDocumentFactory.create(resultDataBytes);
		renderPdfResult.setContentType(APPLICATION_PDF);
		MockTraditionalOutputService generatePdfMock = MockTraditionalOutputService.createDocumentMock(renderPdfResult);
		junitx.util.PrivateAccessor.setField(underTest, "outputServiceFactory", (Supplier<TraditionalOutputService>)()->(TraditionalOutputService)generatePdfMock);
		return generatePdfMock;
	}

}
