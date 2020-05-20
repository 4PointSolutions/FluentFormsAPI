package com._4point.aem.docservices.rest_services.server.forms;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.containsStringIgnoringCase;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com._4point.aem.docservices.rest_services.server.ContentType;
import com._4point.aem.docservices.rest_services.server.TestUtils;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.forms.PDFFormRenderOptions;
import com._4point.aem.fluentforms.impl.forms.TraditionalFormsService;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;
import com._4point.aem.fluentforms.testing.forms.ExceptionalMockTraditionalFormsService;
import com._4point.aem.fluentforms.testing.forms.MockTraditionalFormsService;
import com._4point.aem.fluentforms.testing.forms.MockTraditionalFormsService.RenderPDFFormArgs;
import com._4point.aem.fluentforms.testing.forms.MockTraditionalFormsService.RenderPDFFormArgs2;
import com.adobe.fd.forms.api.AcrobatVersion;
import com.adobe.fd.forms.api.CacheStrategy;
import com.adobe.fd.forms.api.RenderAtClient;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
class RenderPdfFormTest {
	private static final String APPLICATION_XML = "application/xml";
	private static final String APPLICATION_PDF = "application/pdf";
	private static final String APPLICATION_XDP = "application/vnd.adobe.xdp+xml";
	private static final String TEXT_PLAIN = "text/plain";
	private static final String TEXT_HTML = "text/html";

	private static final String TEMPLATE_PARAM = "template";
	private static final String DATA_PARAM = "data";
	private static final String ACROBAT_VERSION_PARAM = "renderOptions.acrobatVersion";
	private static final String CACHE_STRATEGY_PARAM = "renderOptions.cacheStrategy";
	private static final String CONTENT_ROOT_PARAM = "renderOptions.contentRoot";
	private static final String DEBUG_DIR_PARAM = "renderOptions.debugDir";
	private static final String EMBED_FONTS_PARAM = "renderOptions.embedFonts";
	private static final String LOCALE_PARAM = "renderOptions.locale";
	private static final String RENDER_AT_CLIENT_PARAM = "renderOptions.renderAtClient";
	private static final String SUBMIT_URL_PARAM = "renderOptions.submitUrl";
	private static final String TAGGED_PDF_PARAM = "renderOptions.taggedPdf";
	private static final String XCI_PARAM = "renderOptions.xci";


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
	void testDoPost_HappyPath_JustForm() throws ServletException, IOException, NoSuchFieldException {
		String resultData = "testDoPost Happy Path Result";
		String templateData = TestUtils.SAMPLE_FORM.toString();
		
		byte[] resultDataBytes = resultData.getBytes();
		MockTraditionalFormsService renderPdfMock = mockRenderForm(resultDataBytes);

		
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
		RenderPDFFormArgs renderPDFFormArgs = renderPdfMock.getRenderPDFFormArgs();
		assertNull(renderPDFFormArgs.getData());
		assertEquals(TestUtils.SAMPLE_FORM.getFileName().toString(), renderPDFFormArgs.getUrlOrfilename());
		PDFFormRenderOptions pdfFormRenderOptions = renderPDFFormArgs.getPdfFormRenderOptions();
		assertAll(
				()->assertNull(pdfFormRenderOptions.getAcrobatVersion()),
				()->assertNull(pdfFormRenderOptions.getCacheStrategy()),
				()->assertEquals(TestUtils.SAMPLE_FORM.getParent(), pdfFormRenderOptions.getContentRoot().getPath()),
				()->assertNull(pdfFormRenderOptions.getDebugDir()),
				()->assertNull(pdfFormRenderOptions.getEmbedFonts()),
				()->assertNull(pdfFormRenderOptions.getLocale()),
				()->assertNull(pdfFormRenderOptions.getRenderAtClient()),
				()->assertNull(pdfFormRenderOptions.getSubmitUrls()),
				()->assertNull(pdfFormRenderOptions.getTaggedPDF()),
				()->assertNull(pdfFormRenderOptions.getXci())
			);
	}

	@Test
	void testDoPost_HappyPath_JustFormDoc() throws ServletException, IOException, NoSuchFieldException {
		String resultData = "testDoPost Happy Path Result";
		byte[] templateData = Files.readAllBytes(TestUtils.SAMPLE_FORM);
		
		byte[] resultDataBytes = resultData.getBytes();
		MockTraditionalFormsService renderPdfMock = mockRenderForm(resultDataBytes);

		
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
		RenderPDFFormArgs2 renderPDFFormArgs = renderPdfMock.getRenderPDFFormArgs2();
		assertNull(renderPDFFormArgs.getData());
		assertEquals(templateData, renderPDFFormArgs.getTemplate().getInlineData());
		PDFFormRenderOptions pdfFormRenderOptions = renderPDFFormArgs.getPdfFormRenderOptions();
		assertAll(
				()->assertNull(pdfFormRenderOptions.getAcrobatVersion()),
				()->assertNull(pdfFormRenderOptions.getCacheStrategy()),
				()->assertNull(pdfFormRenderOptions.getContentRoot()),
				()->assertNull(pdfFormRenderOptions.getDebugDir()),
				()->assertNull(pdfFormRenderOptions.getEmbedFonts()),
				()->assertNull(pdfFormRenderOptions.getLocale()),
				()->assertNull(pdfFormRenderOptions.getRenderAtClient()),
				()->assertNull(pdfFormRenderOptions.getSubmitUrls()),
				()->assertNull(pdfFormRenderOptions.getTaggedPDF()),
				()->assertNull(pdfFormRenderOptions.getXci())
			);
	}

	@Test
	void testDoPost_HappyPath_JustFormAndData() throws ServletException, IOException, NoSuchFieldException {
		String formData = "formData";
		String resultData = "testDoPost Happy Path Result";
		String templateData = TestUtils.SAMPLE_FORM.toString();
		
		byte[] resultDataBytes = resultData.getBytes();
		MockTraditionalFormsService renderPdfMock = mockRenderForm(resultDataBytes);

		
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
		RenderPDFFormArgs renderPDFFormArgs = renderPdfMock.getRenderPDFFormArgs();
		assertArrayEquals(formData.getBytes(), renderPDFFormArgs.getData().getInlineData());
		assertEquals(TestUtils.SAMPLE_FORM.getFileName().toString(), renderPDFFormArgs.getUrlOrfilename());
		PDFFormRenderOptions pdfFormRenderOptions = renderPDFFormArgs.getPdfFormRenderOptions();
		assertAll(
				()->assertNull(pdfFormRenderOptions.getAcrobatVersion()),
				()->assertNull(pdfFormRenderOptions.getCacheStrategy()),
				()->assertEquals(TestUtils.SAMPLE_FORM.getParent(), pdfFormRenderOptions.getContentRoot().getPath()),
				()->assertNull(pdfFormRenderOptions.getDebugDir()),
				()->assertNull(pdfFormRenderOptions.getEmbedFonts()),
				()->assertNull(pdfFormRenderOptions.getLocale()),
				()->assertNull(pdfFormRenderOptions.getRenderAtClient()),
				()->assertNull(pdfFormRenderOptions.getSubmitUrls()),
				()->assertNull(pdfFormRenderOptions.getTaggedPDF()),
				()->assertNull(pdfFormRenderOptions.getXci())
			);
	}

	@Test
	void testDoPost_HappyPath_MaxArgs() throws ServletException, IOException, NoSuchFieldException {
		String formData = "formData";
		String resultData = "testDoPost Happy Path Result";
		String templateData = TestUtils.SAMPLE_FORM.getParent().getFileName().resolve(TestUtils.SAMPLE_FORM.getFileName()).toString();
		String acrobatVersionData = "Acrobat_10";
		String cacheStrategyData = "CONSERVATIVE";
		String contentRootData = TestUtils.SAMPLE_FORM.getParent().getParent().toString();
		String debugDirData = "/debug/dir";
		boolean embedFontsData = true;
		String localeData = "en-CA";
		String renderAtClient = "NO";
		String submitUrlsData = "/submit/url";
		boolean taggedPdfData = true;
		String xciData = "Xci Data";
		
		byte[] resultDataBytes = resultData.getBytes();
		MockTraditionalFormsService renderPdfMock = mockRenderForm(resultDataBytes);

		
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();
		
		request.addRequestParameter(TEMPLATE_PARAM, templateData);
		request.addRequestParameter(DATA_PARAM, formData.getBytes(), APPLICATION_XML);
		request.addRequestParameter(ACROBAT_VERSION_PARAM, acrobatVersionData);
		request.addRequestParameter(CACHE_STRATEGY_PARAM, cacheStrategyData);
		request.addRequestParameter(CONTENT_ROOT_PARAM, contentRootData);
		request.addRequestParameter(DEBUG_DIR_PARAM, debugDirData);
		request.addRequestParameter(EMBED_FONTS_PARAM, Boolean.toString(embedFontsData));
		request.addRequestParameter(LOCALE_PARAM, localeData);
		request.addRequestParameter(RENDER_AT_CLIENT_PARAM, renderAtClient);
		request.addRequestParameter(SUBMIT_URL_PARAM, submitUrlsData);
		request.addRequestParameter(TAGGED_PDF_PARAM, Boolean.toString(taggedPdfData));
		request.addRequestParameter(XCI_PARAM, xciData.getBytes(), APPLICATION_XML);
		
		underTest.doPost(request, response);
		
		// Validate the result
		assertEquals(SlingHttpServletResponse.SC_OK, response.getStatus(), "Expected OK Status code.  Response='" + response.getStatusMessage() + "'");
		assertEquals(APPLICATION_PDF, response.getContentType());
		assertEquals(resultData, response.getOutputAsString());
		assertEquals(resultDataBytes.length, response.getContentLength());
	
		// Validate that the correct parameters were passed in to renderPdf
		RenderPDFFormArgs renderPDFFormArgs = renderPdfMock.getRenderPDFFormArgs();
		assertArrayEquals(formData.getBytes(), renderPDFFormArgs.getData().getInlineData());
		assertEquals(TestUtils.SAMPLE_FORM.getFileName().toString(), renderPDFFormArgs.getUrlOrfilename());
		PDFFormRenderOptions pdfFormRenderOptions = renderPDFFormArgs.getPdfFormRenderOptions();
		assertAll(
				()->assertEquals(AcrobatVersion.Acrobat_10, pdfFormRenderOptions.getAcrobatVersion()),	// AEM 6.5 Default
				()->assertEquals(CacheStrategy.CONSERVATIVE, pdfFormRenderOptions.getCacheStrategy()),	// AEM 6.5 Default
				()->assertEquals(TestUtils.SAMPLE_FORM.getParent(), pdfFormRenderOptions.getContentRoot().getPath()),
				()->assertEquals(Paths.get(debugDirData), pdfFormRenderOptions.getDebugDir()),
				()->assertTrue(pdfFormRenderOptions.getEmbedFonts()),
				()->assertEquals(Locale.forLanguageTag(localeData), pdfFormRenderOptions.getLocale()),
				()->assertEquals(RenderAtClient.NO, pdfFormRenderOptions.getRenderAtClient()),
				()->assertEquals(submitUrlsData, pdfFormRenderOptions.getSubmitUrls().get(0).toString()),
				()->assertTrue(pdfFormRenderOptions.getTaggedPDF()),
				()->assertArrayEquals(xciData.getBytes(), pdfFormRenderOptions.getXci().getInlineData())
		);
	}

	@Test
	void testDoPost_HappyPath_BadAcrobatVersion() throws ServletException, IOException, NoSuchFieldException {
		String formData = "formData";
		String resultData = "testDoPost Happy Path Result";
		String templateData = TestUtils.SAMPLE_FORM.toString();
		String acrobatVersionData = "Acrobat_5";
		
		byte[] resultDataBytes = resultData.getBytes();
		MockTraditionalFormsService renderPdfMock = mockRenderForm(resultDataBytes);

		
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
	void testDoPost_HappyPath_BadCacheStrategy() throws ServletException, IOException, NoSuchFieldException {
		String formData = "formData";
		String resultData = "testDoPost Happy Path Result";
		String templateData = TestUtils.SAMPLE_FORM.toString();
		String cacheStrategyData = "foobar";
		
		byte[] resultDataBytes = resultData.getBytes();
		MockTraditionalFormsService renderPdfMock = mockRenderForm(resultDataBytes);

		
		MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(aemContext.bundleContext());
		MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();
		
		request.addRequestParameter(TEMPLATE_PARAM, templateData);
		request.addRequestParameter(DATA_PARAM, formData.getBytes(), APPLICATION_XML);
		request.addRequestParameter(CACHE_STRATEGY_PARAM, cacheStrategyData);
		
		underTest.doPost(request, response);
		
		// Validate the result
		assertEquals(SlingHttpServletResponse.SC_BAD_REQUEST, response.getStatus(), "Expected Bad Status code.  Response='" + response.getStatusMessage() + "'");
		assertThat(response.getStatusMessage(), containsStringIgnoringCase("incoming parameters"));
	}

	@EnabledOnOs(OS.WINDOWS)	// Only enabled on Windows because Linux is so permissive with respect to filenames.
	@Test
	void testDoPost_HappyPath_BadContentRoot() throws ServletException, IOException, NoSuchFieldException {
		String formData = "formData";
		String resultData = "testDoPost Happy Path Result";
		String templateData = TestUtils.SAMPLE_FORM.toString();
		String contentRootData = "foo:bar:other";
		
		byte[] resultDataBytes = resultData.getBytes();
		MockTraditionalFormsService renderPdfMock = mockRenderForm(resultDataBytes);

		
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

	@EnabledOnOs(OS.WINDOWS)	// Only enabled on Windows because Linux is so permissive with respect to filenames.
	@Test
	void testDoPost_HappyPath_BadDebugDir() throws ServletException, IOException, NoSuchFieldException {
		String formData = "formData";
		String resultData = "testDoPost Happy Path Result";
		String templateData = TestUtils.SAMPLE_FORM.toString();
		String debugDirData = "////////";
		
		byte[] resultDataBytes = resultData.getBytes();
		MockTraditionalFormsService renderPdfMock = mockRenderForm(resultDataBytes);

		
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
		MockTraditionalFormsService renderPdfMock = mockRenderForm(resultDataBytes);

		
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
	void testDoPost_Exception() throws ServletException, IOException, NoSuchFieldException {
		String formData = "formData";
		String resultData = "testDoPost Happy Path Result";
		String templateData = TestUtils.SAMPLE_FORM.toString();
		
		byte[] resultDataBytes = resultData.getBytes();
		String exceptionMessage = "Exception Message";
		junitx.util.PrivateAccessor.setField(underTest, "formServiceFactory", (Supplier<TraditionalFormsService>)()->(TraditionalFormsService)ExceptionalMockTraditionalFormsService.create(exceptionMessage));

		
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
	void testDoPost_BadAccept() throws ServletException, IOException, NoSuchFieldException {
		String formData = "formData";
		String resultData = "testDoPost Happy Path Result";
		String templateData = TestUtils.SAMPLE_FORM.toString();
		
		byte[] resultDataBytes = resultData.getBytes();
		MockTraditionalFormsService renderPdfMock = mockRenderForm(resultDataBytes);

		
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



	public MockTraditionalFormsService mockRenderForm(byte[] resultDataBytes) throws NoSuchFieldException {
		Document renderPdfResult = mockDocumentFactory.create(resultDataBytes);
		renderPdfResult.setContentType(APPLICATION_PDF);
		MockTraditionalFormsService renderPdfMock = MockTraditionalFormsService.createRenderFormMock(renderPdfResult);
		junitx.util.PrivateAccessor.setField(underTest, "formServiceFactory", (Supplier<TraditionalFormsService>)()->(TraditionalFormsService)renderPdfMock);
		return renderPdfMock;
	}

}
