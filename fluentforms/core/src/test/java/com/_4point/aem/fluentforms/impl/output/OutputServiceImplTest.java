package com._4point.aem.fluentforms.impl.output;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
import com._4point.aem.fluentforms.api.output.OutputService;
import com._4point.aem.fluentforms.api.output.OutputService.OutputServiceException;
import com._4point.aem.fluentforms.api.output.PDFOutputOptions;
import com._4point.aem.fluentforms.api.output.PrintedOutputOptions;
import com._4point.aem.fluentforms.impl.UsageContext;
import com.adobe.fd.output.api.AcrobatVersion;

import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
class OutputServiceImplTest {

	@Mock
	private TraditionalOutputService adobeOutputService;

	private OutputService underTest; 

	@BeforeEach
	void setUp() throws Exception {
		underTest = new OutputServiceImpl(adobeOutputService, UsageContext.SERVER_SIDE);
	}

	@Test
	@DisplayName("Test GeneratePDFOutput(Document,...) Happy Path.")
	void testGeneratePDFOutputDocumentDocumentPDFOutputOptions() throws Exception {
		MockPdfOutputService svc = new MockPdfOutputService();
		
		Document template = Mockito.mock(Document.class);
		Document data = Mockito.mock(Document.class);
		PDFOutputOptions options = Mockito.mock(PDFOutputOptions.class);
		Document result = underTest.generatePDFOutput(template, data, options);
		
		// Verify that all the results are correct.
		assertEquals(template, svc.getTemplateDocArg(), "Expected the template filename passed to AEM would match the filename used.");
		assertTrue(svc.getDataArg() == data, "Expected the data Document passed to AEM would match the data Docyment used.");
		assertTrue(svc.getOptionsArg() == options, "Expected the pdfRenderOptions passed to AEM would match the pdfOutputOptions used.");
		assertTrue(result == svc.getResult(), "Expected the Document returned by AEM would match the Document result.");
	}

	@Test
	@DisplayName("Test GeneratePDFOutput(Document,...) null arguments.")
	void testRenderPDFOutputDocument_nullArguments() throws Exception {
		Document template = Mockito.mock(Document.class);
		Document data = Mockito.mock(Document.class);
		PDFOutputOptions pdfFormRenderOptions = Mockito.mock(PDFOutputOptions.class);
		Document nullDocument = null;
		
		NullPointerException ex1 = assertThrows(NullPointerException.class, ()->underTest.generatePDFOutput(nullDocument, data, pdfFormRenderOptions));
		assertTrue(ex1.getMessage().contains("Template"), ()->"'" + ex1.getMessage() + "' does not contain 'Template'");

		// Null Data is allowed.
//		NullPointerException ex2 = assertThrows(NullPointerException.class, ()->underTest.renderPDFForm(filename, null, pdfFormRenderOptions));
//		assertTrue(ex2.getMessage().contains("data"), ()->"'" + ex2.getMessage() + "' does not contain 'data'");
		
		NullPointerException ex3 = assertThrows(NullPointerException.class, ()->underTest.generatePDFOutput(template, data, null));
		assertTrue(ex3.getMessage().contains("PDFOutputOptions"), ()->"'" + ex3.getMessage() + "' does not contain 'PDFOutputOptions'");
	}

	@Test
	@DisplayName("Test GeneratePDFOutput(Document,...) throws OutputServiceException.")
	void testRenderPDFOutputDocument__OutputServiceExceptionThrown() throws Exception {
		Mockito.when(adobeOutputService.generatePDFOutput(Mockito.any(Document.class), Mockito.any(), Mockito.any())).thenThrow(OutputServiceException.class);

		Document template = Mockito.mock(Document.class);
		Document data = Mockito.mock(Document.class);
		PDFOutputOptions pdfFormRenderOptions = Mockito.mock(PDFOutputOptions.class);
		
		assertThrows(OutputServiceException.class, ()->underTest.generatePDFOutput(template, data, pdfFormRenderOptions));
	}

	@Test
	@DisplayName("Test GeneratePDFOutput(Path,...) Happy Path.")
	void testGeneratePDFOutputPathDocumentPDFOutputOptions() throws Exception {
		MockPdfOutputService svc = new MockPdfOutputService();
		
		Path filename = TestUtils.SAMPLE_FORM;
		Document data = Mockito.mock(Document.class);
		PDFOutputOptions options = Mockito.mock(PDFOutputOptions.class);
		Document result = underTest.generatePDFOutput(filename, data, options);
		
		// Verify that all the results are correct.
		assertEquals(filename.getFileName(), Paths.get(svc.getTemplateStringArg()), "Expected the template filename passed to AEM would match the filename used.");
		assertTrue(svc.getDataArg() == data, "Expected the data Document passed to AEM would match the data Docyment used.");
		assertTrue(svc.getOptionsArg() == options, "Expected the pdfRenderOptions passed to AEM would match the pdfOutputOptions used.");
		assertTrue(result == svc.getResult(), "Expected the Document returned by AEM would match the Document result.");
	}

	@Test
	@DisplayName("Test GeneratePDFOutput(Path,...) null arguments.")
	void testGeneratePDFOutputPath_nullArguments() throws Exception {
		Path filename = TestUtils.SAMPLE_FORM;
		Document data = Mockito.mock(Document.class);
		PDFOutputOptions pdfFormRenderOptions = Mockito.mock(PDFOutputOptions.class);
		Path nullFilename = null;
		
		NullPointerException ex1 = assertThrows(NullPointerException.class, ()->underTest.generatePDFOutput(nullFilename, data, pdfFormRenderOptions));
		assertNotNull(ex1.getMessage());	// Exception should contain a message.
		assertTrue(ex1.getMessage().contains("template"), ()->"'" + ex1.getMessage() + "' does not contain 'template'");

		// Null Data is allowed.
//		NullPointerException ex2 = assertThrows(NullPointerException.class, ()->underTest.renderPDFForm(filename, null, pdfFormRenderOptions));
//		assertTrue(ex2.getMessage().contains("data"), ()->"'" + ex2.getMessage() + "' does not contain 'data'");
		
		NullPointerException ex3 = assertThrows(NullPointerException.class, ()->underTest.generatePDFOutput(filename, data, null));
		assertNotNull(ex1.getMessage());	// Exception should contain a message.
		assertTrue(ex3.getMessage().contains("pdfOutputOptions"), ()->"'" + ex3.getMessage() + "' does not contain 'pdfOutputOptions'");
	}

	@Test
	@DisplayName("Test GeneratePDFOutput(Path,...) throws OutputServiceException.")
	void testGeneratePDFOutputPath__FormsServiceExceptionThrown() throws Exception {
		Mockito.when(adobeOutputService.generatePDFOutput(Mockito.any(String.class), Mockito.any(), Mockito.any())).thenThrow(OutputServiceException.class);

		Path filename = TestUtils.SAMPLE_FORM;
		Document data = Mockito.mock(Document.class);
		PDFOutputOptions pdfFormRenderOptions = Mockito.mock(PDFOutputOptions.class);
		
		assertThrows(OutputServiceException.class, ()->underTest.generatePDFOutput(filename, data, pdfFormRenderOptions));
	}

	@Test
	@DisplayName("Test GeneratePDFOutput(Path,...) throws FileNotFoundException.")
	void testGeneratePDFOutputPath__BadTemplate() throws Exception {
		String filename = "foo/bar.xdp";
		Path filePath = Paths.get(filename);
		Document data = Mockito.mock(Document.class);
		PDFOutputOptions pdfFormRenderOptions = Mockito.mock(PDFOutputOptions.class);
		
		FileNotFoundException ex1 = assertThrows(FileNotFoundException.class, ()->underTest.generatePDFOutput(filePath, data, pdfFormRenderOptions));
		String message = ex1.getMessage();
		assertTrue(message.contains("template"), ()->"Expected exception message to contain a mention of the template. (" + message + ").");
		assertTrue(message.contains(filePath.toString()), "Expected exception message to contain the filepath provided. (" + message + ").");
	}
	
	@Test
	@DisplayName("Test GeneratePDFOutput(URL,...) Happy Path.")
	void testGeneratePDFOutputUrlDocumentPDFOutputOptions() throws Exception {
		MockPdfOutputService svc = new MockPdfOutputService();
		
		URL filename = new URL("http://www.example.com/foo/bar.xdp");
		Document data = Mockito.mock(Document.class);
		PDFOutputOptions options = Mockito.mock(PDFOutputOptions.class);
		Document result = underTest.generatePDFOutput(filename, data, options);
		
		// Verify that all the results are correct.
		assertEquals(filename, new URL(svc.getTemplateStringArg()), "Expected the template filename passed to AEM would match the filename used.");
		assertTrue(svc.getDataArg() == data, "Expected the data Document passed to AEM would match the data Docyment used.");
		assertTrue(svc.getOptionsArg() == options, "Expected the pdfRenderOptions passed to AEM would match the pdfOutputOptions used.");
		assertTrue(result == svc.getResult(), "Expected the Document returned by AEM would match the Document result.");
	}

	@Test
	@DisplayName("Test GeneratePDFOutput(URL,...) null arguments.")
	void testGeneratePDFOutputUrl_nullArguments() throws Exception {
		URL filename = new URL("http://www.example.com/foo/bar.xdp");
		Document data = Mockito.mock(Document.class);
		PDFOutputOptions pdfFormRenderOptions = Mockito.mock(PDFOutputOptions.class);
		Path nullFilename = null;
		
		NullPointerException ex1 = assertThrows(NullPointerException.class, ()->underTest.generatePDFOutput(nullFilename, data, pdfFormRenderOptions));
		assertNotNull(ex1.getMessage());	// Exception should contain a message.
		assertTrue(ex1.getMessage().contains("template"), ()->"'" + ex1.getMessage() + "' does not contain 'template'");

		// Null Data is allowed.
//		NullPointerException ex2 = assertThrows(NullPointerException.class, ()->underTest.generatePDFOutput(filename, null, pdfFormRenderOptions));
//		assertTrue(ex2.getMessage().contains("data"), ()->"'" + ex2.getMessage() + "' does not contain 'data'");
		
		NullPointerException ex3 = assertThrows(NullPointerException.class, ()->underTest.generatePDFOutput(filename, data, null));
		assertNotNull(ex1.getMessage());	// Exception should contain a message.
		assertTrue(ex3.getMessage().contains("PDFOutputOptions"), ()->"'" + ex3.getMessage() + "' does not contain 'PDFOutputOptions'");
	}

	@Test
	@DisplayName("Test GeneratePDFOutput(URL,...) throws OutputServiceException.")
	void testGeneratePDFOutputUrl__FormsServiceExceptionThrown() throws Exception {
		Mockito.when(adobeOutputService.generatePDFOutput(Mockito.any(String.class), Mockito.any(), Mockito.any())).thenThrow(OutputServiceException.class);

		URL filename = new URL("http://www.example.com/foo/bar.xdp");
		Document data = Mockito.mock(Document.class);
		PDFOutputOptions pdfFormRenderOptions = Mockito.mock(PDFOutputOptions.class);
		
		assertThrows(OutputServiceException.class, ()->underTest.generatePDFOutput(filename, data, pdfFormRenderOptions));
	}

	@Test
	@DisplayName("Test GeneratePDFOutput(PathOrUrl,...) Happy Path.")
	void testGeneratePDFOutputPathOrUrlDocumentPDFOutputOptions() throws Exception {
		MockPdfOutputService svc = new MockPdfOutputService();
		
		PathOrUrl filename = PathOrUrl.from("file:foo/bar.xdp");
		Document data = Mockito.mock(Document.class);
		PDFOutputOptions options = Mockito.mock(PDFOutputOptions.class);
		Document result = underTest.generatePDFOutput(filename, data, options);
		
		// Verify that all the results are correct.
		assertEquals(filename.getUrl(), new URL(svc.getTemplateStringArg()), "Expected the template filename passed to AEM would match the filename used.");
		assertTrue(svc.getDataArg() == data, "Expected the data Document passed to AEM would match the data Docyment used.");
		assertTrue(svc.getOptionsArg() == options, "Expected the pdfRenderOptions passed to AEM would match the pdfOutputOptions used.");
		assertTrue(result == svc.getResult(), "Expected the Document returned by AEM would match the Document result.");
	}

	@Test
	@DisplayName("Test GeneratePDFOutput(Crx PathOrUrl,...) Happy Path.")
	void testGeneratePDFOutputCrxUrlDocumentPDFOutputOptions() throws Exception {
		MockPdfOutputService svc = new MockPdfOutputService();
		
		PathOrUrl filename = PathOrUrl.from("crx:/content/dam/formsanddocuments/foo/bar.xdp");
		Document data = Mockito.mock(Document.class);
		PDFOutputOptions options = Mockito.mock(PDFOutputOptions.class);
		Document result = underTest.generatePDFOutput(filename, data, options);
		
		// Verify that all the results are correct.
		assertEquals(filename.toString(), svc.getTemplateStringArg(), "Expected the template filename passed to AEM would match the filename used.");
		assertTrue(svc.getDataArg() == data, "Expected the data Document passed to AEM would match the data Docyment used.");
		assertTrue(svc.getOptionsArg() == options, "Expected the pdfRenderOptions passed to AEM would match the pdfOutputOptions used.");
		assertTrue(result == svc.getResult(), "Expected the Document returned by AEM would match the Document result.");
	}

	@Test
	@DisplayName("Test GeneratePDFOutput(PathOrUrl,...) with null arguments.")
	void testGeneratePDFOutputPathOrUrl_nullArguments() throws Exception {
		MockPdfOutputService svc = new MockPdfOutputService();
		
		PathOrUrl filename = PathOrUrl.from("file:foo/bar.xdp");
		Document data = Mockito.mock(Document.class);
		PDFOutputOptions options = Mockito.mock(PDFOutputOptions.class);
		PathOrUrl nullFilename = null;
		
		NullPointerException ex1 = assertThrows(NullPointerException.class, ()->underTest.generatePDFOutput(nullFilename, data, options));
		assertNotNull(ex1.getMessage());	// Exception should contain a message.
		assertTrue(ex1.getMessage().contains("template"), ()->"'" + ex1.getMessage() + "' does not contain 'template'");
		
		// Null Data is allowed.
//		NullPointerException ex2 = assertThrows(NullPointerException.class, ()->underTest.generatePDFOutput(filename, data, options));
//		assertTrue(ex2.getMessage().contains("data"), ()->"'" + ex2.getMessage() + "' does not contain 'data'");
		
		NullPointerException ex3 = assertThrows(NullPointerException.class, ()->underTest.generatePDFOutput(filename, data, null));
		assertNotNull(ex1.getMessage());	// Exception should contain a message.
		assertTrue(ex3.getMessage().contains("PDFOutputOptions"), ()->"'" + ex3.getMessage() + "' does not contain 'PDFOutputOptions'");

	}

	@Test
	void testGeneratePDFOutputDocument() throws Exception {
		MockPdfOutputService svc = new MockPdfOutputService();
		
		Document template = Mockito.mock(Document.class);
		Document data = Mockito.mock(Document.class);
		Document result = underTest.generatePDFOutput()
									.setAcrobatVersion(AcrobatVersion.Acrobat_10_1)
									.setContentRoot(Paths.get("foo", "bar"))
									.setDebugDir(Paths.get("bar", "foo"))
									.setEmbedFonts(true)
									.setLinearizedPDF(true)
									.setLocale(Locale.CANADA_FRENCH)
									.setRetainPDFFormState(true)
									.setRetainUnsignedSignatureFields(true)
									.setTaggedPDF(true)
									// Omit the creation of XCI document because that would require a real Adobe implementation to be available.
//									underTest.setXci(new MockDocumentFactory().create(new byte[0]));
									.executeOn(template, data);
		
		// Verify that all the results are correct.
		assertEquals(template, svc.getTemplateDocArg(), "Expected the template filename passed to AEM would match the filename used.");
		assertTrue(svc.getDataArg() == data, "Expected the data Document passed to AEM would match the data Docyment used.");
		assertTrue(result == svc.getResult(), "Expected the Document returned by AEM would match the Document result.");
		PDFOutputOptionsImplTest.assertNotEmpty(svc.getOptionsArg());
	}

	@Test
	void testGeneratePDFOutputPath() throws Exception {
		MockPdfOutputService svc = new MockPdfOutputService();
		
		Path contentRoot = TestUtils.SAMPLE_FORM.getParent().getParent();
		Path filename = contentRoot.relativize(TestUtils.SAMPLE_FORM);
		Document data = Mockito.mock(Document.class);

		Document result = underTest.generatePDFOutput()
									.setAcrobatVersion(AcrobatVersion.Acrobat_10_1)
									.setContentRoot(contentRoot)
									.setDebugDir(Paths.get("bar", "foo"))
									.setEmbedFonts(true)
									.setLinearizedPDF(true)
									.setLocale(Locale.CANADA_FRENCH)
									.setRetainPDFFormState(true)
									.setRetainUnsignedSignatureFields(true)
									.setTaggedPDF(true)
									// Omit the creation of XCI document because that would require a real Adobe implementation to be available.
//									underTest.setXci(new MockDocumentFactory().create(new byte[0]));
									.executeOn(filename, data);
		
		// Verify that all the results are correct.
		assertEquals(filename.getFileName(), Paths.get(svc.getTemplateStringArg()), "Expected the template filename passed to AEM would match the filename used.");
		assertTrue(svc.getDataArg() == data, "Expected the data Document passed to AEM would match the data Docyment used.");
		assertTrue(result == svc.getResult(), "Expected the Document returned by AEM would match the Document result.");
		PDFOutputOptionsImplTest.assertNotEmpty(svc.getOptionsArg());
	}

	@Test
	void testGeneratePDFOutputUrl() throws Exception {
		MockPdfOutputService svc = new MockPdfOutputService();
		
		PathOrUrl filename = PathOrUrl.from("file:foo/bar.xdp");
		Document data = Mockito.mock(Document.class);
		Document result = underTest.generatePDFOutput()
									.setAcrobatVersion(AcrobatVersion.Acrobat_10_1)
									.setContentRoot(Paths.get("foo", "bar"))
									.setDebugDir(Paths.get("bar", "foo"))
									.setEmbedFonts(true)
									.setLinearizedPDF(true)
									.setLocale(Locale.CANADA_FRENCH)
									.setRetainPDFFormState(true)
									.setRetainUnsignedSignatureFields(true)
									.setTaggedPDF(true)
									// Omit the creation of XCI document because that would require a real Adobe implementation to be available.
					//				underTest.setXci(new MockDocumentFactory().create(new byte[0]));
									.executeOn(filename, data);
		
		// Verify that all the results are correct.
		assertEquals(filename.getUrl(), new URL(svc.getTemplateStringArg()), "Expected the template filename passed to AEM would match the filename used.");
		assertTrue(svc.getDataArg() == data, "Expected the data Document passed to AEM would match the data Docyment used.");
		assertTrue(result == svc.getResult(), "Expected the Document returned by AEM would match the Document result.");
		PDFOutputOptionsImplTest.assertNotEmpty(svc.getOptionsArg());
	}

	@Test
	void testGeneratePDFOutputPathOrUrl() throws Exception {
		MockPdfOutputService svc = new MockPdfOutputService();
		
		PathOrUrl filename = PathOrUrl.from("file:foo/bar.xdp");
		Document data = Mockito.mock(Document.class);
		Document result = underTest.generatePDFOutput()
									.setAcrobatVersion(AcrobatVersion.Acrobat_10_1)
									.setContentRoot(Paths.get("foo", "bar"))
									.setDebugDir(Paths.get("bar", "foo"))
									.setEmbedFonts(true)
									.setLinearizedPDF(true)
									.setLocale(Locale.CANADA_FRENCH)
									.setRetainPDFFormState(true)
									.setRetainUnsignedSignatureFields(true)
									.setTaggedPDF(true)
									// Omit the creation of XCI document because that would require a real Adobe implementation to be available.
					//				underTest.setXci(new MockDocumentFactory().create(new byte[0]));
									.executeOn(filename, data);
		
		// Verify that all the results are correct.
		assertEquals(filename.getUrl(), new URL(svc.getTemplateStringArg()), "Expected the template filename passed to AEM would match the filename used.");
		assertTrue(svc.getDataArg() == data, "Expected the data Document passed to AEM would match the data Docyment used.");
		assertTrue(result == svc.getResult(), "Expected the Document returned by AEM would match the Document result.");
		PDFOutputOptionsImplTest.assertNotEmpty(svc.getOptionsArg());
	}

	@Test
	void testGeneratePDFOutputCrxUrl() throws Exception {
		MockPdfOutputService svc = new MockPdfOutputService();
		
		PathOrUrl filename = PathOrUrl.from("crx:/content/dam/formsanddocuments/foo/bar.xdp");
		Document data = Mockito.mock(Document.class);
		Document result = underTest.generatePDFOutput()
									.setAcrobatVersion(AcrobatVersion.Acrobat_10_1)
									.setContentRoot(Paths.get("foo", "bar"))
									.setDebugDir(Paths.get("bar", "foo"))
									.setEmbedFonts(true)
									.setLinearizedPDF(true)
									.setLocale(Locale.CANADA_FRENCH)
									.setRetainPDFFormState(true)
									.setRetainUnsignedSignatureFields(true)
									.setTaggedPDF(true)
									// Omit the creation of XCI document because that would require a real Adobe implementation to be available.
					//				underTest.setXci(new MockDocumentFactory().create(new byte[0]));
									.executeOn(filename, data);
		
		// Verify that all the results are correct.
		assertEquals(filename.toString(), svc.getTemplateStringArg(), "Expected the template filename passed to AEM would match the filename used.");
		assertTrue(svc.getDataArg() == data, "Expected the data Document passed to AEM would match the data Docyment used.");
		assertTrue(result == svc.getResult(), "Expected the Document returned by AEM would match the Document result.");
		PDFOutputOptionsImplTest.assertNotEmpty(svc.getOptionsArg());
	}

	@Disabled
	void testGeneratePDFOutputBatch() {
		fail("Not yet implemented");
	}

	@Disabled
	void testGeneratePrintedOutputDocumentDocumentPrintedOutputOptions() {
		fail("Not yet implemented");
	}

	@Disabled
	void testGeneratePrintedOutputPathDocumentPrintedOutputOptions() {
		fail("Not yet implemented");
	}

	@Disabled
	void testGeneratePrintedOutputURLDocumentPrintedOutputOptions() {
		fail("Not yet implemented");
	}

	@Disabled
	void testGeneratePrintedOutputPathOrUrlDocumentPrintedOutputOptions() {
		fail("Not yet implemented");
	}

	@Disabled
	void testGeneratePrintedOutput() {
		fail("Not yet implemented");
	}

	@Disabled
	void testGeneratePrintedOutputBatch() {
		fail("Not yet implemented");
	}

	private class MockPdfOutputService {
		private final Document result = Mockito.mock(Document.class);
		private final ArgumentCaptor<String> templateStringArg = ArgumentCaptor.forClass(String.class);
		private final ArgumentCaptor<Document> templateDocArg = ArgumentCaptor.forClass(Document.class);
		private final ArgumentCaptor<Document> dataArg = ArgumentCaptor.forClass(Document.class);
		private final ArgumentCaptor<PDFOutputOptions> optionsArg = ArgumentCaptor.forClass(PDFOutputOptions.class);
		
		protected MockPdfOutputService() throws OutputServiceException {
			super();
			// These are "lenient" because we only expect one or the other to be called.  Also, in some of the exceptional cases,
			// neither are called.
			Mockito.lenient().when(adobeOutputService.generatePDFOutput(templateStringArg.capture(), dataArg.capture(), optionsArg.capture())).thenReturn(result);
			Mockito.lenient().when(adobeOutputService.generatePDFOutput(templateDocArg.capture(), dataArg.capture(), optionsArg.capture())).thenReturn(result);
		}

		protected Document getResult() {
			return result;
		}

		protected String getTemplateStringArg() {
			return templateStringArg.getValue();
		}

		protected Document getTemplateDocArg() {
			return templateDocArg.getValue();
		}

		protected Document getDataArg() {
			return dataArg.getValue();
		}

		protected PDFOutputOptions getOptionsArg() {
			return optionsArg.getValue();
		}
	}

	private class MockPrintedOutputService {
		private final Document result = Mockito.mock(Document.class);
		private final ArgumentCaptor<String> templateArg = ArgumentCaptor.forClass(String.class);
		private final ArgumentCaptor<Document> dataArg = ArgumentCaptor.forClass(Document.class);
		private final ArgumentCaptor<PrintedOutputOptions> optionsArg = ArgumentCaptor.forClass(PrintedOutputOptions.class);
		
		protected MockPrintedOutputService() throws OutputServiceException {
			super();
			Mockito.lenient().when(adobeOutputService.generatePrintedOutput(templateArg.capture(), dataArg.capture(), optionsArg.capture())).thenReturn(result);
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

		protected PrintedOutputOptions getOptionsArg() {
			return optionsArg.getValue();
		}
	}


}
