package com._4point.aem.fluentforms.impl.forms;

import static com._4point.aem.fluentforms.api.TestUtils.SAMPLE_FORM;
import static com._4point.aem.fluentforms.api.TestUtils.SAMPLE_FORMS_DIR;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.api.TestUtils;
import com._4point.aem.fluentforms.api.forms.FormsService;
import com._4point.aem.fluentforms.api.forms.PDFFormRenderOptions;
import com._4point.aem.fluentforms.api.forms.ValidationOptions;
import com._4point.aem.fluentforms.api.forms.ValidationResult;
import com._4point.aem.fluentforms.api.forms.FormsService.FormsServiceException;
import com._4point.aem.fluentforms.impl.UsageContext;
import com.adobe.fd.forms.api.AcrobatVersion;
import com.adobe.fd.forms.api.CacheStrategy;
import com.adobe.fd.forms.api.DataFormat;
import com.adobe.fd.forms.api.RenderAtClient;

import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
class FormsServiceImplTest {

	@Mock
	private TraditionalFormsService adobeFormsService;

	private FormsService underTest; 

	@BeforeEach
	void setUp() throws Exception {
		underTest = new FormsServiceImpl(adobeFormsService, UsageContext.SERVER_SIDE);
	}

	@Test
	@DisplayName("Test exportData() Happy Path.")
	void testExportData_HappyPath() throws Exception {
		Document result = Mockito.mock(Document.class);
		Mockito.when(result.isEmpty()).thenReturn(Boolean.FALSE);
		ArgumentCaptor<com.adobe.fd.forms.api.DataFormat> dataFormatArg = ArgumentCaptor.forClass(com.adobe.fd.forms.api.DataFormat.class);
		ArgumentCaptor<Document> documenttArg = ArgumentCaptor.forClass(Document.class);
		Mockito.when(adobeFormsService.exportData(documenttArg.capture(), dataFormatArg.capture())).thenReturn(result);

		DataFormat dataFormat = DataFormat.XmlData;
		Document pdfOrXdp = Mockito.mock(Document.class);

		Optional<Document> exportedData = underTest.exportData(pdfOrXdp, dataFormat);

		// Verify that all the results are correct.
		assertSame(pdfOrXdp, documenttArg.getValue(), "Expected the Document passed to AEM would match the Document object argument.");
		assertSame(dataFormat, dataFormatArg.getValue(), "Expected the DataFormat passed to AEM would match the DataFormat object argument.");
		assertTrue(exportedData.isPresent(), "Expected the result to be present");
		assertTrue(exportedData.get() == result, "Expected the Document returned by AEM would match the Document result.");
	}

	@Test
	@DisplayName("Test exportData() Empty Result.")
	void testExportData_EmptyResult() throws Exception {
		Document result = Mockito.mock(Document.class);
		Mockito.when(result.isEmpty()).thenReturn(Boolean.TRUE);
		ArgumentCaptor<com.adobe.fd.forms.api.DataFormat> dataFormatArg = ArgumentCaptor.forClass(com.adobe.fd.forms.api.DataFormat.class);
		ArgumentCaptor<Document> documenttArg = ArgumentCaptor.forClass(Document.class);
		Mockito.when(adobeFormsService.exportData(documenttArg.capture(), dataFormatArg.capture())).thenReturn(result);

		DataFormat dataFormat = DataFormat.XmlData;
		Document pdfOrXdp = Mockito.mock(Document.class);

		Optional<Document> exportedData = underTest.exportData(pdfOrXdp, dataFormat);

		// Verify that all the results are correct.
		assertSame(pdfOrXdp, documenttArg.getValue(), "Expected the Document passed to AEM would match the Document object argument.");
		assertSame(dataFormat, dataFormatArg.getValue(), "Expected the DataFormat passed to AEM would match the DataFormat object argument.");
		assertFalse(exportedData.isPresent(), "Expected the result to not be present");
	}
	@Test
	@DisplayName("Test exportData() with null arguments throws exception and the exception contains the argument name.")
	void testExportData_nullArguments() throws Exception {
		DataFormat dataFormat = DataFormat.XmlData;
		NullPointerException ex1 = assertThrows(NullPointerException.class, ()->underTest.exportData(null, dataFormat));
		assertTrue(ex1.getMessage().contains("pdfOrXdp"));
		
		Document pdfOrXdp = Mockito.mock(Document.class);
		NullPointerException ex2 = assertThrows(NullPointerException.class, ()->underTest.exportData(pdfOrXdp, null));
		assertTrue(ex2.getMessage().contains("dataFormat"));
	}

	@Test
	@DisplayName("Test exportData() when exception is thrown.")
	void testExportData_FormsServiceExceptionThrown() throws Exception {
		Mockito.when(adobeFormsService.exportData(Mockito.any(), Mockito.any())).thenThrow(FormsServiceException.class);
		DataFormat dataFormat = DataFormat.XmlData;
		Document pdfOrXdp = Mockito.mock(Document.class);
		
		assertThrows(FormsServiceException.class, ()->underTest.exportData(pdfOrXdp, dataFormat));
	}

	@Test
	@DisplayName("Test importData() Happy Path.")
	void testImportData() throws FormsServiceException {
		Document result = Mockito.mock(Document.class);
		ArgumentCaptor<Document> pdfArg = ArgumentCaptor.forClass(Document.class);
		ArgumentCaptor<Document> dataArg = ArgumentCaptor.forClass(Document.class);
		Mockito.when(adobeFormsService.importData(pdfArg.capture(), dataArg.capture())).thenReturn(result);

		Document pdf = Mockito.mock(Document.class);
		Document data = Mockito.mock(Document.class);

		Document pdfResult = underTest.importData(pdf, data);

		// Verify that all the results are correct.
		assertSame(pdf, pdfArg.getValue(), "Expected the pdf Document passed to AEM would match the pdf Document used.");
		assertSame(data, dataArg.getValue(), "Expected the data Document passed to AEM would match the data Docyment used.");
		assertSame(pdfResult, result, "Expected the Document returned by AEM would match the Document result.");
	}

	@Test
	@DisplayName("Test importData() with null arguments throws exception and the exception contains the argument name.")
	void testImportData_nullArguments() {
		Document pdf = Mockito.mock(Document.class);
		NullPointerException ex1 = assertThrows(NullPointerException.class, ()->underTest.importData(pdf, null));
		assertTrue(ex1.getMessage().contains("data"));

		Document data = Mockito.mock(Document.class);
		NullPointerException ex2 = assertThrows(NullPointerException.class, ()->underTest.importData(null, data));
		assertTrue(ex2.getMessage().contains("pdf"));
	}

	@Test
	@DisplayName("Test importData() when exception is thrown.")
	void testImportData_FormsServiceExceptionThrown() throws FormsServiceException {
		Mockito.when(adobeFormsService.importData(Mockito.any(), Mockito.any())).thenThrow(FormsServiceException.class);
		Document pdf = Mockito.mock(Document.class);
		Document data = Mockito.mock(Document.class);
		
		assertThrows(FormsServiceException.class, ()->underTest.importData(pdf, data));
	}

	@Test
	@DisplayName("Test RenderPDFForm(Path,...) Happy Path, no contextRoot supplied.")
	void testRenderPDFFormPathDocumentDefaultPDFFormRenderOptions() throws FormsServiceException, FileNotFoundException {
		MockPdfRenderService svc = new MockPdfRenderService();
		
		Path filePath = SAMPLE_FORM;
		Document data = Mockito.mock(Document.class);
		PDFFormRenderOptions pdfFormRenderOptions = new PDFFormRenderOptionsImpl();
		Document pdfResult = underTest.renderPDFForm(filePath, data, pdfFormRenderOptions);
		
		// Verify that all the results are correct.
		assertEquals(filePath.getFileName(), Paths.get(svc.getTemplateArg()), "Expected the template filename passed to AEM would match the filename used.");
		assertEquals(filePath.getParent(), Paths.get(svc.getOptionsArg().getContentRoot().toString()), "Expected the template parent passed to AEM would match the contextRoot used.");
		assertSame(svc.getDataArg(), data, "Expected the data Document passed to AEM would match the data Document used.");
		assertSame(pdfFormRenderOptions, svc.getOptionsArg(), "Expected the pdfRenderOptions passed to AEM would match the pdfRenderOptions used.");
		assertSame(pdfResult, svc.getResult(), "Expected the Document returned by AEM would match the Document result.");
	}

	@Test
	@DisplayName("Test RenderPDFForm(Path,...) Happy Path, with contextRoot supplied.")
	void testRenderPDFFormPathDocumentWithPDFFormRenderOptions() throws FormsServiceException, FileNotFoundException {
		MockPdfRenderService svc = new MockPdfRenderService();
		
		Path filePath = SAMPLE_FORM.getFileName();
		Document data = Mockito.mock(Document.class);
		PDFFormRenderOptions pdfFormRenderOptions = new PDFFormRenderOptionsImpl();
		Path contextRoot = TestUtils.SAMPLE_FORMS_DIR;
		pdfFormRenderOptions.setContentRoot(contextRoot);
		Document pdfResult = underTest.renderPDFForm(filePath, data, pdfFormRenderOptions);
		
		// Verify that all the results are correct.
		assertEquals(filePath, Paths.get(svc.getTemplateArg()), "Expected the template filename passed to AEM would match the filename used.");
		assertEquals(contextRoot, Paths.get(svc.getOptionsArg().getContentRoot().toString()), "Expected the template parent passed to AEM would match the contextRoot used.");
		assertSame(svc.getDataArg(), data, "Expected the data Document passed to AEM would match the data Document used.");
		assertSame(pdfFormRenderOptions, svc.getOptionsArg(), "Expected the pdfRenderOptions passed to AEM would match the pdfRenderOptions used.");
		assertSame(pdfResult, svc.getResult(), "Expected the Document returned by AEM would match the Document result.");
	}

	@Test
	@DisplayName("Test RenderPDFForm(Path,...) null arguments.")
	void testRenderPDFFormPath_nullArguments() throws Exception {
		Path filename = SAMPLE_FORM;
		Document data = Mockito.mock(Document.class);
		PDFFormRenderOptions pdfFormRenderOptions = Mockito.mock(PDFFormRenderOptions.class);
		Path nullFilename = null;
		
		NullPointerException ex1 = assertThrows(NullPointerException.class, ()->underTest.renderPDFForm(nullFilename, data, pdfFormRenderOptions));
		assertTrue(ex1.getMessage().contains("template"), ()->"'" + ex1.getMessage() + "' does not contain 'template'");

		// Null Data is allowed.
//		NullPointerException ex2 = assertThrows(NullPointerException.class, ()->underTest.renderPDFForm(filename, null, pdfFormRenderOptions));
//		assertTrue(ex2.getMessage().contains("data"), ()->"'" + ex2.getMessage() + "' does not contain 'data'");
		
		NullPointerException ex3 = assertThrows(NullPointerException.class, ()->underTest.renderPDFForm(filename, data, null));
		assertTrue(ex3.getMessage().contains("pdfFormRenderOptions"), ()->"'" + ex3.getMessage() + "' does not contain 'pdfFormRenderOptions'");
	}

	@Test
	@DisplayName("Test RenderPDFForm(Path,...) throws FormsServiceException.")
	void testRenderPDFFormPath__FormsServiceExceptionThrown() throws Exception {
		Mockito.when(adobeFormsService.renderPDFForm(Mockito.any(String.class), Mockito.any(), Mockito.any())).thenThrow(FormsServiceException.class);

		Path filename = SAMPLE_FORM;
		Document data = Mockito.mock(Document.class);
		PDFFormRenderOptions pdfFormRenderOptions = Mockito.mock(PDFFormRenderOptions.class);
		
		assertThrows(FormsServiceException.class, ()->underTest.renderPDFForm(filename, data, pdfFormRenderOptions));
	}

	@Test
	@DisplayName("Test RenderPDFForm(Path,...) throws FormsServiceException.")
	void testRenderPDFFormPath__BadTemplate() throws Exception {
		String filename = "foo/bar.xdp";
		Path filePath = Paths.get(filename);
		Document data = Mockito.mock(Document.class);
		PDFFormRenderOptions pdfFormRenderOptions = Mockito.mock(PDFFormRenderOptions.class);
		
		FileNotFoundException ex1 = assertThrows(FileNotFoundException.class, ()->underTest.renderPDFForm(filePath, data, pdfFormRenderOptions));
		String message = ex1.getMessage();
		assertTrue(message.contains("template"), ()->"Expected exception message to contain a mention of the template. (" + message + ").");
		assertTrue(message.contains(filePath.toString()), "Expected exception message to contain the filepath provided. (" + message + ").");
	}
	
	@Test
	@DisplayName("Test RenderPDFForm(URL,...) Happy Path, no contextRoot.")
	void testRenderPDFFormURLDocumentDefaultPDFFormRenderOptions() throws Exception {
		MockPdfRenderService svc = new MockPdfRenderService();

		String expectedContentRoot = "file:foo/";
		String expectedFilename = "bar.xdp";
		URL fileUrl = new URL(expectedContentRoot + expectedFilename);
		Document data = Mockito.mock(Document.class);
		PDFFormRenderOptions pdfFormRenderOptions = new PDFFormRenderOptionsImpl();
		Document pdfResult = underTest.renderPDFForm(fileUrl, data, pdfFormRenderOptions);
		
		// Verify that all the results are correct.
		assertEquals(expectedFilename, svc.getTemplateArg(), "Expected the template filename passed to AEM would match the filename used.");
		assertEquals(expectedContentRoot, svc.getOptionsArg().getContentRoot().toString(), "Expected the contextRoot passed to AEM would match the contextRoot used.");
		assertSame(data, svc.getDataArg(), "Expected the data Document passed to AEM would match the data Docyment used.");
		assertSame(pdfFormRenderOptions, svc.getOptionsArg(), "Expected the pdfRenderOptions passed to AEM would match the pdfRenderOptions used.");
		assertSame(pdfResult, svc.getResult(), "Expected the Document returned by AEM would match the Document result.");
	}

	@Test
	@DisplayName("Test RenderPDFForm(URL,...) Happy Path, with contextRoot.")
	void testRenderPDFFormURLDocumentNonDefaultPDFFormRenderOptions() throws Exception {
		MockPdfRenderService svc = new MockPdfRenderService();

		String expectedFilename = "http://www.example.com/foo/bar.xdp";
		String expectedContentRoot = "http://www.otherexample.com/foo/";
		URL fileUrl = new URL(expectedFilename);
		Document data = Mockito.mock(Document.class);
		PDFFormRenderOptions pdfFormRenderOptions = new PDFFormRenderOptionsImpl();
		pdfFormRenderOptions.setContentRoot(new URL(expectedContentRoot));
		Document pdfResult = underTest.renderPDFForm(fileUrl, data, pdfFormRenderOptions);
		
		// Verify that all the results are correct.
		assertEquals(expectedFilename, svc.getTemplateArg(), "Expected the template filename passed to AEM would match the filename used.");
		assertEquals(expectedContentRoot, svc.getOptionsArg().getContentRoot().toString(), "Expected the contextRoot passed to AEM would match the contextRoot used.");
		assertSame(data, svc.getDataArg(), "Expected the data Document passed to AEM would match the data Docyment used.");
		assertSame(pdfFormRenderOptions, svc.getOptionsArg(), "Expected the pdfRenderOptions passed to AEM would match the pdfRenderOptions used.");
		assertSame(pdfResult, svc.getResult(), "Expected the Document returned by AEM would match the Document result.");
	}

	@Test
	@DisplayName("Test RenderPDFForm(URL,...) null arguments.")
	void testRenderPDFFormURL_nullArguments() throws Exception {
		URL filename = new URL("http://www.example.com/docs/resource1.html");
		Document data = Mockito.mock(Document.class);
		PDFFormRenderOptions pdfFormRenderOptions = Mockito.mock(PDFFormRenderOptions.class);
		URL nullFilename = null;
		
		NullPointerException ex1 = assertThrows(NullPointerException.class, ()->underTest.renderPDFForm(nullFilename, data, pdfFormRenderOptions));
		assertTrue(ex1.getMessage().contains("url"), ()->"'" + ex1.getMessage() + "' does not contain 'url'");
		
		// Null Data is allowed.
//		NullPointerException ex2 = assertThrows(NullPointerException.class, ()->underTest.renderPDFForm(filename, null, pdfFormRenderOptions));
//		assertTrue(ex2.getMessage().contains("data"), ()->"'" + ex2.getMessage() + "' does not contain 'data'");
		
		NullPointerException ex3 = assertThrows(NullPointerException.class, ()->underTest.renderPDFForm(filename, data, null));
		assertTrue(ex3.getMessage().contains("pdfFormRenderOptions"), ()->"'" + ex3.getMessage() + "' does not contain 'pdfFormRenderOptions'");
	}
	
	@Test
	@DisplayName("Test RenderPDFForm(URL,...) throws FormsServiceException.")
	void testRenderPDFFormURL___FormsServiceExceptionThrown() throws Exception {
		Mockito.when(adobeFormsService.renderPDFForm(Mockito.any(String.class), Mockito.any(), Mockito.any())).thenThrow(FormsServiceException.class);

		URL filename = new URL("http://www.example.com/docs/resource1.html");
		Document data = Mockito.mock(Document.class);
		PDFFormRenderOptions pdfFormRenderOptions = Mockito.mock(PDFFormRenderOptions.class);
		
		assertThrows(FormsServiceException.class, ()->underTest.renderPDFForm(filename, data, pdfFormRenderOptions));
	}

	@Test
	@DisplayName("Test RenderPDFForm(PathOrUrl,...) Happy Path, no contentRoot.")
	void testRenderPDFFormPathOrUrlDocumentDefaultPDFFormRenderOptions() throws Exception {
		MockPdfRenderService svc = new MockPdfRenderService();

		String expectedFilename = "bar.xdp";
		String expectedContentRoot = "file:foo/";
		PathOrUrl filename = PathOrUrl.from(expectedContentRoot + expectedFilename);
		Document data = Mockito.mock(Document.class);
		PDFFormRenderOptions pdfFormRenderOptions = new PDFFormRenderOptionsImpl();
		Document pdfResult = underTest.renderPDFForm(filename, data, pdfFormRenderOptions);
		
		// Verify that all the results are correct.
		assertEquals(expectedFilename, svc.getTemplateArg(), "Expected the template filename passed to AEM would match the filename used.");
		assertEquals(expectedContentRoot, svc.getOptionsArg().getContentRoot().toString(), "Expected the contextRoot passed to AEM would match the contextRoot used.");
		assertSame(data, svc.getDataArg(), "Expected the data Document passed to AEM would match the data Docyment used.");
		assertSame(pdfFormRenderOptions, svc.getOptionsArg(), "Expected the pdfRenderOptions passed to AEM would match the pdfRenderOptions used.");
		assertSame(pdfResult, svc.getResult(), "Expected the Document returned by AEM would match the Document result.");
	}

	@Test
	@DisplayName("Test RenderPDFForm(PathOrUrl,...) Happy Path, with contentRoot.")
	void testRenderPDFFormPathOrUrlDocumentNonDefaultPDFFormRenderOptions() throws Exception {
		MockPdfRenderService svc = new MockPdfRenderService();

		String expectedFilename = "file:foo/bar.xdp";
		String expectedContentRoot = "file:foobarbar/";
		PathOrUrl filename = PathOrUrl.from(expectedFilename);
		Document data = Mockito.mock(Document.class);
		PDFFormRenderOptions pdfFormRenderOptions = new PDFFormRenderOptionsImpl();
		pdfFormRenderOptions.setContentRoot(PathOrUrl.from(expectedContentRoot));
		Document pdfResult = underTest.renderPDFForm(filename, data, pdfFormRenderOptions);
		
		// Verify that all the results are correct.
		assertEquals(expectedFilename, svc.getTemplateArg(), "Expected the template filename passed to AEM would match the filename used.");
		assertEquals(expectedContentRoot, svc.getOptionsArg().getContentRoot().toString(), "Expected the contextRoot passed to AEM would match the contextRoot used.");
		assertSame(data, svc.getDataArg(), "Expected the data Document passed to AEM would match the data Docyment used.");
		assertSame(pdfFormRenderOptions, svc.getOptionsArg(), "Expected the pdfRenderOptions passed to AEM would match the pdfRenderOptions used.");
		assertSame(pdfResult, svc.getResult(), "Expected the Document returned by AEM would match the Document result.");
	}

	@Test
	@DisplayName("Test RenderPDFForm(PathOrUrl,...) Happy Path.")
	void testRenderPDFFormPathOrUrlPathDocumentPDFFormRenderOptions() throws Exception {
		MockPdfRenderService svc = new MockPdfRenderService();
		
		PathOrUrl filePath = PathOrUrl.from(SAMPLE_FORM.toString());
		Document data = Mockito.mock(Document.class);
		PDFFormRenderOptions pdfFormRenderOptions = Mockito.mock(PDFFormRenderOptions.class);
		Document pdfResult = underTest.renderPDFForm(filePath, data, pdfFormRenderOptions);
		
		// Verify that all the results are correct.
		assertEquals(filePath.getPath().getFileName(), Paths.get(svc.getTemplateArg()), "Expected the template filename passed to AEM would match the filename used.");
		assertSame(data, svc.getDataArg(), "Expected the data Document passed to AEM would match the data Document used.");
		assertSame(pdfFormRenderOptions, svc.getOptionsArg(), "Expected the pdfRenderOptions passed to AEM would match the pdfRenderOptions used.");
		assertSame(pdfResult, svc.getResult(), "Expected the Document returned by AEM would match the Document result.");
	}

	@Test
	@DisplayName("Test RenderPDFForm(PathOrUrl,...) null arguments.")
	void testRenderPDFFormPathOrUrl_nullArguments() throws Exception {
		PathOrUrl filename = PathOrUrl.from("http://www.example.com/docs/resource1.html");
		Document data = Mockito.mock(Document.class);
		PDFFormRenderOptions pdfFormRenderOptions = Mockito.mock(PDFFormRenderOptions.class);
		URL nullFilename = null;
		
		NullPointerException ex1 = assertThrows(NullPointerException.class, ()->underTest.renderPDFForm(nullFilename, data, pdfFormRenderOptions));
		assertTrue(ex1.getMessage().contains("url"), ()->"'" + ex1.getMessage() + "' does not contain 'url'");
		
		// Null Data is allowed.
//		NullPointerException ex2 = assertThrows(NullPointerException.class, ()->underTest.renderPDFForm(filename, null, pdfFormRenderOptions));
//		assertTrue(ex2.getMessage().contains("data"), ()->"'" + ex2.getMessage() + "' does not contain 'data'");
		
		NullPointerException ex3 = assertThrows(NullPointerException.class, ()->underTest.renderPDFForm(filename, data, null));
		assertTrue(ex3.getMessage().contains("pdfFormRenderOptions"), ()->"'" + ex3.getMessage() + "' does not contain 'pdfFormRenderOptions'");
	}
	
	@Test
	@DisplayName("Test RenderPDFForm(PathOrUrl,...) throws FormsServiceException.")
	void testRenderPDFFormPathOrUrl___FormsServiceExceptionThrown() throws Exception {
		Mockito.when(adobeFormsService.renderPDFForm(Mockito.any(String.class), Mockito.any(), Mockito.any())).thenThrow(FormsServiceException.class);

		PathOrUrl filename = PathOrUrl.from("http://www.example.com/docs/resource1.html");
		Document data = Mockito.mock(Document.class);
		PDFFormRenderOptions pdfFormRenderOptions = Mockito.mock(PDFFormRenderOptions.class);
		
		assertThrows(FormsServiceException.class, ()->underTest.renderPDFForm(filename, data, pdfFormRenderOptions));
	}

	@Test
	@DisplayName("Test RenderPDFForm(Document,...) Happy Path.")
	void testRenderPDFFormDocumentDocumentPDFFormRenderOptions() throws Exception {
		MockPdfRenderService2 svc = new MockPdfRenderService2();

		Document template = Mockito.mock(Document.class);
		Document data = Mockito.mock(Document.class);
		PDFFormRenderOptions pdfFormRenderOptions = Mockito.mock(PDFFormRenderOptions.class);
		Document pdfResult = underTest.renderPDFForm(template, data, pdfFormRenderOptions);
		
		assertEquals(template, svc.getTemplateArg(), "Expected the template passed to AEM would match the template used.");
		assertSame(data, svc.getDataArg(), "Expected the data Document passed to AEM would match the data Document used.");
		assertSame(pdfFormRenderOptions, svc.getOptionsArg(), "Expected the pdfRenderOptions passed to AEM would match the pdfRenderOptions used.");
		assertSame(pdfResult, svc.getResult(), "Expected the Document returned by AEM would match the Document result.");
	}

	@Test
	@DisplayName("Test RenderPDFForm(Document,...) null arguments.")
	void testRenderPDFFormD_nullArguments() throws Exception {
		Document template = Mockito.mock(Document.class);
		Document data = Mockito.mock(Document.class);
		PDFFormRenderOptions pdfFormRenderOptions = Mockito.mock(PDFFormRenderOptions.class);
		Document nullDocument = null;
		
		NullPointerException ex1 = assertThrows(NullPointerException.class, ()->underTest.renderPDFForm(nullDocument, data, pdfFormRenderOptions));
		assertTrue(ex1.getMessage().contains("Document"), ()->"'" + ex1.getMessage() + "' does not contain 'Document'");
		
		// Null Data is allowed.
//		NullPointerException ex2 = assertThrows(NullPointerException.class, ()->underTest.renderPDFForm(filename, null, pdfFormRenderOptions));
//		assertTrue(ex2.getMessage().contains("data"), ()->"'" + ex2.getMessage() + "' does not contain 'data'");
		
		NullPointerException ex3 = assertThrows(NullPointerException.class, ()->underTest.renderPDFForm(template, data, null));
		assertTrue(ex3.getMessage().contains("pdfFormRenderOptions"), ()->"'" + ex3.getMessage() + "' does not contain 'pdfFormRenderOptions'");
	}
	
	@Test
	@DisplayName("Test RenderPDFForm(Document,...) throws FormsServiceException.")
	void testRenderPDFFormDocument___FormsServiceExceptionThrown() throws Exception {
		Mockito.when(adobeFormsService.renderPDFForm(Mockito.any(Document.class), Mockito.any(), Mockito.any())).thenThrow(FormsServiceException.class);

		Document template = Mockito.mock(Document.class);
		Document data = Mockito.mock(Document.class);
		PDFFormRenderOptions pdfFormRenderOptions = Mockito.mock(PDFFormRenderOptions.class);
		
		assertThrows(FormsServiceException.class, ()->underTest.renderPDFForm(template, data, pdfFormRenderOptions));
	}

	@Test
	void testValidatePathDocumentValidationOptions() throws Exception {
		ValidationResult result = Mockito.mock(ValidationResult.class);
		ArgumentCaptor<String> templateArg = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Document> dataArg = ArgumentCaptor.forClass(Document.class);
		ArgumentCaptor<ValidationOptions> optionsArg = ArgumentCaptor.forClass(ValidationOptions.class);
		Mockito.when(adobeFormsService.validate(templateArg.capture(), dataArg.capture(), optionsArg.capture())).thenReturn(result);
		
		Path template = SAMPLE_FORM;
		Document data = Mockito.mock(Document.class);
		ValidationOptions validationOptions = Mockito.mock(ValidationOptions.class);
		ValidationResult validationResult = underTest.validate(template, data, validationOptions );
		
		// Verify that all the results are correct.
		assertEquals(template, Paths.get(templateArg.getValue()), "Expected the template filename passed to AEM would match the filename used.");
		assertSame(data, dataArg.getValue(), "Expected the data Document passed to AEM would match the data Document used.");
		assertSame(validationOptions, optionsArg.getValue(), "Expected the validationOptions passed to AEM would match the validationOptions used.");
		assertSame(validationResult, result, "Expected the validation result returned by AEM would match the validation result.");
	}

	@Test
	void testValidate_badTemplate() throws Exception {
		Path template = Paths.get("foo", "bar.xdp");
		Document data = Mockito.mock(Document.class);
		ValidationOptions validationOptions = Mockito.mock(ValidationOptions.class);
		FileNotFoundException ex = assertThrows(FileNotFoundException.class, ()->underTest.validate(template, data, validationOptions));
		
		String message = ex.getMessage();
		assertTrue(message.contains("template"), ()->"Expected exception message to contain a mention of the template. (" + message + ").");
		assertTrue(message.contains(template.toString()), "Expected exception message to contain the filepath provided. (" + message + ").");
	}

	@Test
	void testValidate_nullArguments() throws Exception {
		Path template = SAMPLE_FORM;
		Document data = Mockito.mock(Document.class);
		ValidationOptions validationOptions = Mockito.mock(ValidationOptions.class);
		
		NullPointerException ex1 = assertThrows(NullPointerException.class, ()->underTest.validate(null, data, validationOptions));
		assertTrue(ex1.getMessage().contains("template"), ()->"'" + ex1.getMessage() + "' does not contain 'template'");
		
		NullPointerException ex2 = assertThrows(NullPointerException.class, ()->underTest.validate(template, null, validationOptions));
		assertTrue(ex2.getMessage().contains("data"), ()->"'" + ex2.getMessage() + "' does not contain 'data'");
		
		NullPointerException ex3 = assertThrows(NullPointerException.class, ()->underTest.validate(template, data, null));
		assertTrue(ex3.getMessage().contains("validationOptions"), ()->"'" + ex3.getMessage() + "' does not contain 'validationOptions'");
	}
	
	@Test
	void testRenderPDFFormPath() throws Exception {
		MockPdfRenderService svc = new MockPdfRenderService();

		Path filePath = SAMPLE_FORM.toAbsolutePath();
		Document data = Mockito.mock(Document.class);

		// Double check that the PDFRenderOptions are working by setting all the values to not default values and then
		// checking that they were set (by calling PDFFormRenderOptionsImplTest.assertNotEmpty(). 
		Document pdfResult = underTest.renderPDFForm()
								   .setAcrobatVersion(AcrobatVersion.Acrobat_10)
								   .setCacheStrategy(CacheStrategy.CONSERVATIVE)
								   .setContentRoot(Paths.get("foo", "bar"))
								   .setDebugDir(Paths.get("bar", "foo"))
								   .setEmbedFonts(true)
								   .setLocale(Locale.JAPAN)
								   .setRenderAtClient(RenderAtClient.NO)
								   .setSubmitUrl(new URL("http://example.com"))
								   .setTaggedPDF(true)
								   .xci()
								   		.embedFonts(true)
								   		.done()
								   .executeOn(filePath, data);

		assertEquals(filePath.getFileName(), Paths.get(svc.getTemplateArg()), "Expected the template filename passed to AEM would match the filename used.");
		assertSame(data, svc.getDataArg(), "Expected the data Document passed to AEM would match the data Document used.");
		assertSame(pdfResult, svc.getResult(), "Expected the Document returned by AEM would match the Document result.");

		PDFFormRenderOptionsImplTest.assertNotEmpty(svc.getOptionsArg());
	}

	@Test
	void testRenderPDFFormPath_UrlContentRoot() throws Exception {
		MockPdfRenderService svc = new MockPdfRenderService();

		Path filePath = SAMPLE_FORM.toAbsolutePath();
		Document data = Mockito.mock(Document.class);
		URL contentRootUrl = new URL("http://foo/bar");

		// Double check that the PDFRenderOptions are working by setting all the values to not default values and then
		// checking that they were set (by calling PDFFormRenderOptionsImplTest.assertNotEmpty(). 
		Document pdfResult = underTest.renderPDFForm()
									   .setAcrobatVersion(AcrobatVersion.Acrobat_10)
									   .setCacheStrategy(CacheStrategy.CONSERVATIVE)
									   .setContentRoot(contentRootUrl)
									   .setDebugDir(Paths.get("bar", "foo"))
									   .setEmbedFonts(true)
									   .setLocale(Locale.JAPAN)
									   .setRenderAtClient(RenderAtClient.NO)
									   .setSubmitUrl(new URL("http://example.com"))
									   .setTaggedPDF(true)
									   .xci()
									   		.embedFonts(true)
									   		.done()
									   .executeOn(filePath, data);

		assertEquals(filePath.getFileName(), Paths.get(svc.getTemplateArg()), "Expected the template filename passed to AEM would match the filename used.");
		assertSame(data, svc.getDataArg(), "Expected the data Document passed to AEM would match the data Document used.");
		assertSame(pdfResult, svc.getResult(), "Expected the Document returned by AEM would match the Document result.");

		PDFFormRenderOptionsImplTest.assertNotEmpty(svc.getOptionsArg());
	}

	@Test
	void testRenderPDFFormPath_CrxUrlContentRoot() throws Exception {
		MockPdfRenderService svc = new MockPdfRenderService();

		Path filePath = SAMPLE_FORM.toAbsolutePath();
		Document data = Mockito.mock(Document.class);
		PathOrUrl contentRootUrl = PathOrUrl.from("crx://foo/bar");

		// Double check that the PDFRenderOptions are working by setting all the values to not default values and then
		// checking that they were set (by calling PDFFormRenderOptionsImplTest.assertNotEmpty(). 
		Document pdfResult = underTest.renderPDFForm()
									   .setAcrobatVersion(AcrobatVersion.Acrobat_10)
									   .setCacheStrategy(CacheStrategy.CONSERVATIVE)
									   .setContentRoot(contentRootUrl)
									   .setDebugDir(Paths.get("bar", "foo"))
									   .setEmbedFonts(true)
									   .setRenderAtClient(RenderAtClient.NO)
									   .setLocale(Locale.JAPAN)
									   .setSubmitUrl(new URL("http://example.com"))
									   .setTaggedPDF(true)
									   .xci()
								   			.embedFonts(true)
								   			.done()
									   .executeOn(filePath, data);

		assertEquals(filePath.getFileName(), Paths.get(svc.getTemplateArg()), "Expected the template filename passed to AEM would match the filename used.");
		assertSame(data, svc.getDataArg(), "Expected the data Document passed to AEM would match the data Document used.");
		assertSame(pdfResult, svc.getResult(), "Expected the Document returned by AEM would match the Document result.");

		PDFFormRenderOptionsImplTest.assertNotEmpty(svc.getOptionsArg());
	}

	@Test
	void testRenderPDFFormPath_NoArgs() throws Exception {
		MockPdfRenderService svc = new MockPdfRenderService();

		Path filePath = SAMPLE_FORM;
		Document data = Mockito.mock(Document.class);
		Document pdfResult = underTest.renderPDFForm()
				 				      .executeOn(filePath, data);

		assertEquals(filePath.getFileName(), Paths.get(svc.getTemplateArg()), "Expected the template filename passed to AEM would match the filename used.");
		assertSame(data, svc.getDataArg(), "Expected the data Document passed to AEM would match the data Document used.");
		assertSame(pdfResult, svc.getResult(), "Expected the Document returned by AEM would match the Document result.");

		PDFFormRenderOptionsImplTest.assertEmpty(svc.getOptionsArg(), filePath.getParent().toAbsolutePath().toString());
	}

	@Test
	void testRenderPDFFormUrl() throws Exception {
		MockPdfRenderService svc = new MockPdfRenderService();

		URL fileUrl = new URL("http://example.com");
		Document data = Mockito.mock(Document.class);

		// Double check that the PDFRenderOptions are working by setting all the values to not default values and then
		// checking that they were set (by calling PDFFormRenderOptionsImplTest.assertNotEmpty(). 
		Document pdfResult = underTest.renderPDFForm()
				   .setAcrobatVersion(AcrobatVersion.Acrobat_10)
				   .setCacheStrategy(CacheStrategy.CONSERVATIVE)
				   .setContentRoot(Paths.get("foo", "bar"))
				   .setDebugDir(Paths.get("bar", "foo"))
				   .setEmbedFonts(true)
				   .setLocale(Locale.JAPAN)
				   .setRenderAtClient(RenderAtClient.NO)
				   .setSubmitUrl(new URL("http://example.com"))
				   .setTaggedPDF(true)
				   .xci()
				   		.embedFonts(true)
				   		.done()
				   .executeOn(fileUrl, data);

		assertEquals(fileUrl, new URL(svc.getTemplateArg()), "Expected the template filename passed to AEM would match the filename used.");
		assertSame(data, svc.getDataArg(), "Expected the data Document passed to AEM would match the data Document used.");
		assertSame(pdfResult,  svc.getResult(), "Expected the Document returned by AEM would match the Document result.");

		PDFFormRenderOptionsImplTest.assertNotEmpty(svc.getOptionsArg());
	}

	@Test
	void testRenderPDFFormUrl_NoArgs() throws Exception {
		MockPdfRenderService svc = new MockPdfRenderService();

		URL fileUrl = new URL("http://example.com");
		Document data = Mockito.mock(Document.class);
		Document pdfResult = underTest.renderPDFForm()
				 				      .executeOn(fileUrl, data);

		assertEquals(fileUrl, new URL(svc.getTemplateArg()), "Expected the template filename passed to AEM would match the filename used.");
		assertSame(data, svc.getDataArg(), "Expected the data Document passed to AEM would match the data Document used.");
		assertSame(pdfResult, svc.getResult(), "Expected the Document returned by AEM would match the Document result.");

		PDFFormRenderOptionsImplTest.assertEmpty(svc.getOptionsArg(), null);
	}

	@Test
	void testValidate() throws Exception {
		ValidationResult result = Mockito.mock(ValidationResult.class);
		ArgumentCaptor<String> templateArg = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Document> dataArg = ArgumentCaptor.forClass(Document.class);
		ArgumentCaptor<ValidationOptions> optionsArg = ArgumentCaptor.forClass(ValidationOptions.class);
		Mockito.when(adobeFormsService.validate(templateArg.capture(), dataArg.capture(), optionsArg.capture())).thenReturn(result);
		
		Path template = SAMPLE_FORM;
		Document data = Mockito.mock(Document.class);
		ValidationResult validationResult = underTest.validate()
		 		 						   .setContentRoot(SAMPLE_FORMS_DIR)
		 		 						   .setDebugDir(SAMPLE_FORMS_DIR)
		 		 						   .executeOn(template, data);

		// Verify that all the results are correct.
		assertEquals(template, Paths.get(templateArg.getValue()), "Expected the template filename passed to AEM would match the filename used.");
		assertSame(data, dataArg.getValue(), "Expected the data Document passed to AEM would match the data Document used.");
		assertSame(validationResult,  result, "Expected the validation result returned by AEM would match the validation result.");
		
		ValidationOptions adobeValidationOptions = optionsArg.getValue();
		assertEquals(SAMPLE_FORMS_DIR, adobeValidationOptions.getContentRoot());
		assertEquals(SAMPLE_FORMS_DIR, adobeValidationOptions.getDebugDir());
	}

	@Test
	void testValidate_NoArgs() throws Exception {
		ValidationResult result = Mockito.mock(ValidationResult.class);
		ArgumentCaptor<String> templateArg = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Document> dataArg = ArgumentCaptor.forClass(Document.class);
		ArgumentCaptor<ValidationOptions> optionsArg = ArgumentCaptor.forClass(ValidationOptions.class);
		Mockito.when(adobeFormsService.validate(templateArg.capture(), dataArg.capture(), optionsArg.capture())).thenReturn(result);
		
		Path template = SAMPLE_FORM;
		Document data = Mockito.mock(Document.class);
		ValidationResult validationResult = underTest.validate()
													 .executeOn(template, data);
		
		// Verify that all the results are correct.
		assertEquals(template, Paths.get(templateArg.getValue()), "Expected the template filename passed to AEM would match the filename used.");
		assertSame(data, dataArg.getValue(), "Expected the data Document passed to AEM would match the data Document used.");
		assertSame(validationResult,  result, "Expected the validation result returned by AEM would match the validation result.");

		ValidationOptions adobeValidationOptions = optionsArg.getValue();
		assertNull(adobeValidationOptions.getContentRoot());
		assertNull(adobeValidationOptions.getDebugDir());
	}

	@Test
	void testToAdobeValidationOptions() throws Exception {
		ValidationOptionsImpl vo = new ValidationOptionsImpl(SAMPLE_FORMS_DIR, SAMPLE_FORMS_DIR);
		com.adobe.fd.forms.api.ValidationOptions adobeValidationOptions = vo.toAdobeValidationOptions();
		assertEquals(SAMPLE_FORMS_DIR.toString(), adobeValidationOptions.getContentRoot());
		assertEquals(SAMPLE_FORMS_DIR.toString(), adobeValidationOptions.getDebugDir());
	}

	@Test
	@DisplayName("Make sure bad filenames throw FileNotFoundExceptions")
	void testValidationOptions_nullArguments() {
		Path badFilename = Paths.get("foo", "bar");
		FileNotFoundException ex1 = assertThrows(FileNotFoundException.class, ()->new ValidationOptionsImpl(badFilename, SAMPLE_FORMS_DIR));
		String msg1 = ex1.getMessage();
		assertThat(msg1, Matchers.containsString("content root"));
		assertThat(msg1, Matchers.containsString(badFilename.toString()));
		
		FileNotFoundException ex2 = assertThrows(FileNotFoundException.class, ()->new ValidationOptionsImpl(SAMPLE_FORMS_DIR, badFilename));
		String msg2 = ex2.getMessage();
		assertThat(msg2, Matchers.containsString("debug dumps"));
		assertThat(msg2, Matchers.containsString(badFilename.toString()));

	}

	@Test
	void testToAdobeValidationOptions_nullArguments() throws Exception {
		ValidationOptionsImpl vo = new ValidationOptionsImpl(null, null);
		com.adobe.fd.forms.api.ValidationOptions adobeValidationOptions = vo.toAdobeValidationOptions();
		assertNull(adobeValidationOptions.getContentRoot());
		assertNull(adobeValidationOptions.getDebugDir());
	}


	// TODO: Test the Forms Service interface default methods here
	//       They are not currently being tested...

	private class MockPdfRenderService {
		private final Document result = Mockito.mock(Document.class);
		private final ArgumentCaptor<String> templateArg = ArgumentCaptor.forClass(String.class);
		private final ArgumentCaptor<Document> dataArg = ArgumentCaptor.forClass(Document.class);
		private final ArgumentCaptor<PDFFormRenderOptions> optionsArg = ArgumentCaptor.forClass(PDFFormRenderOptions.class);
		
		protected MockPdfRenderService() throws FormsServiceException {
			super();
			Mockito.lenient().when(adobeFormsService.renderPDFForm(templateArg.capture(), dataArg.capture(), optionsArg.capture())).thenReturn(result);
		}

		protected Document getResult() {
			return result;
		}

		protected String getTemplateArg() {
			return templateArg.getValue();
		}

		protected Document getDataArg() {
			return dataArg.getValue();
		}

		protected PDFFormRenderOptions getOptionsArg() {
			return optionsArg.getValue();
		}
	}
	private class MockPdfRenderService2 {
		private final Document result = Mockito.mock(Document.class);
		private final ArgumentCaptor<Document> templateArg = ArgumentCaptor.forClass(Document.class);
		private final ArgumentCaptor<Document> dataArg = ArgumentCaptor.forClass(Document.class);
		private final ArgumentCaptor<PDFFormRenderOptions> optionsArg = ArgumentCaptor.forClass(PDFFormRenderOptions.class);
		
		protected MockPdfRenderService2() throws FormsServiceException {
			super();
			Mockito.lenient().when(adobeFormsService.renderPDFForm(templateArg.capture(), dataArg.capture(), optionsArg.capture())).thenReturn(result);
		}

		protected Document getResult() {
			return result;
		}

		protected Document getTemplateArg() {
			return templateArg.getValue();
		}

		protected Document getDataArg() {
			return dataArg.getValue();
		}

		protected PDFFormRenderOptions getOptionsArg() {
			return optionsArg.getValue();
		}
	}
}
