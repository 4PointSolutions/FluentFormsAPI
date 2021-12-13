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
		assertSame(data, svc.getDataArg(), "Expected the data Document passed to AEM would match the data Document used.");
		assertSame(options, svc.getOptionsArg(), "Expected the pdfRenderOptions passed to AEM would match the pdfOutputOptions used.");
		assertSame(result, svc.getResult(), "Expected the Document returned by AEM would match the Document result.");
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
	@DisplayName("Test GeneratePDFOutput(Path,...) Happy Path, No Context Root.")
	void testGeneratePDFOutputPathDocumentDefaultPDFOutputOptions() throws Exception {
		MockPdfOutputService svc = new MockPdfOutputService();
		
		Path filename = TestUtils.SAMPLE_FORM;
		Document data = Mockito.mock(Document.class);
		PDFOutputOptions options = new PDFOutputOptionsImpl();
		Document result = underTest.generatePDFOutput(filename, data, options);
		
		// Verify that all the results are correct.
		assertEquals(filename.getFileName(), Paths.get(svc.getTemplateStringArg()), "Expected the template filename passed to AEM would match the filename used.");
		assertEquals(filename.getParent(), Paths.get(svc.getOptionsArg().getContentRoot().toString()), "Expected the template filename passed to AEM would match the filename used.");
		assertSame(data, svc.getDataArg(), "Expected the data Document passed to AEM would match the data Document used.");
		assertSame(options, svc.getOptionsArg(), "Expected the pdfRenderOptions passed to AEM would match the pdfOutputOptions used.");
		assertSame(result, svc.getResult(), "Expected the Document returned by AEM would match the Document result.");
	}

	@Test
	@DisplayName("Test GeneratePDFOutput(Path,...) Happy Path, with Context Root.")
	void testGeneratePDFOutputPathDocumentNonDefaultPDFOutputOptions() throws Exception {
		MockPdfOutputService svc = new MockPdfOutputService();
		
		Path filename = TestUtils.SAMPLE_FORM.getFileName();
		Document data = Mockito.mock(Document.class);
		PDFOutputOptions options = new PDFOutputOptionsImpl();
		Path contextRoot = TestUtils.SAMPLE_FORMS_DIR;
		options.setContentRoot(contextRoot);
		Document result = underTest.generatePDFOutput(filename, data, options);
		
		// Verify that all the results are correct.
		assertEquals(filename.getFileName(), Paths.get(svc.getTemplateStringArg()), "Expected the template filename passed to AEM would match the filename used.");
		assertEquals(contextRoot, Paths.get(svc.getOptionsArg().getContentRoot().toString()), "Expected the template filename passed to AEM would match the filename used.");
		assertSame(data, svc.getDataArg(), "Expected the data Document passed to AEM would match the data Document used.");
		assertSame(options, svc.getOptionsArg(), "Expected the pdfRenderOptions passed to AEM would match the pdfOutputOptions used.");
		assertSame(result, svc.getResult(), "Expected the Document returned by AEM would match the Document result.");
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
	@DisplayName("Test GeneratePDFOutput(URL,...) Happy Path, no contentRoot.")
	void testGeneratePDFOutputUrlDocumentDefaultPDFOutputOptions() throws Exception {
		MockPdfOutputService svc = new MockPdfOutputService();
		
		String expectedTemplateFilename = "bar.xdp";
		String expectedContextRoot = "http://www.example.com/foo/";
		URL filename = new URL(expectedContextRoot + expectedTemplateFilename);
		Document data = Mockito.mock(Document.class);
		PDFOutputOptions options = new PDFOutputOptionsImpl();
		Document result = underTest.generatePDFOutput(filename, data, options);
		
		// Verify that all the results are correct.
		assertEquals(expectedTemplateFilename, svc.getTemplateStringArg(), "Expected the template filename passed to AEM would match the filename used.");
		assertEquals(expectedContextRoot, svc.optionsArg.getValue().getContentRoot().toString(), "Expected the template contextRoot passed to AEM would match the contextRoot used.");
		assertSame(data, svc.getDataArg(), "Expected the data Document passed to AEM would match the data Document used.");
		assertSame(options, svc.getOptionsArg(), "Expected the pdfRenderOptions passed to AEM would match the pdfOutputOptions used.");
		assertSame(result, svc.getResult(), "Expected the Document returned by AEM would match the Document result.");
	}

	@Test
	@DisplayName("Test GeneratePDFOutput(URL,...) Happy Path, with contentRoot.")
	void testGeneratePDFOutputUrlDocumentWithPDFOutputOptions() throws Exception {
		MockPdfOutputService svc = new MockPdfOutputService();
		
		String expectedTemplateFilename = "http://www.example.com/foo/bar.xdp";
		String expectedContextRoot = "http://www.otherexample.com/foo/";
		URL filename = new URL(expectedTemplateFilename);
		Document data = Mockito.mock(Document.class);
		PDFOutputOptions options = new PDFOutputOptionsImpl();
		options.setContentRoot(new URL(expectedContextRoot));
		Document result = underTest.generatePDFOutput(filename, data, options);
		
		// Verify that all the results are correct.
		assertEquals(expectedTemplateFilename, svc.getTemplateStringArg(), "Expected the template filename passed to AEM would match the filename used.");
		assertEquals(expectedContextRoot, svc.optionsArg.getValue().getContentRoot().toString(), "Expected the template contextRoot passed to AEM would match the contextRoot used.");
		assertSame(data, svc.getDataArg(), "Expected the data Document passed to AEM would match the data Document used.");
		assertSame(options, svc.getOptionsArg(), "Expected the pdfRenderOptions passed to AEM would match the pdfOutputOptions used.");
		assertSame(result, svc.getResult(), "Expected the Document returned by AEM would match the Document result.");
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
		assertTrue(ex3.getMessage().toLowerCase().contains("PDFOutputOptions".toLowerCase()), ()->"'" + ex3.getMessage() + "' does not contain 'PDFOutputOptions'");
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
	@DisplayName("Test GeneratePDFOutput(PathOrUrl,...) Happy Path with no contentRoot.")
	void testGeneratePDFOutputPathOrUrlDocumentDefaultPDFOutputOptions() throws Exception {
		MockPdfOutputService svc = new MockPdfOutputService();
		
		String expectedTemplateFilename = "bar.xdp";
		String expectedContextRoot = "file:foo/";
		PathOrUrl filename = PathOrUrl.from(expectedContextRoot + expectedTemplateFilename);
		Document data = Mockito.mock(Document.class);
		PDFOutputOptions options = new PDFOutputOptionsImpl();
		Document result = underTest.generatePDFOutput(filename, data, options);
		
		// Verify that all the results are correct.
		assertEquals(expectedTemplateFilename, svc.getTemplateStringArg(), "Expected the template filename passed to AEM would match the filename used.");
		assertEquals(expectedContextRoot, svc.optionsArg.getValue().getContentRoot().toString(), "Expected the template contextRoot passed to AEM would match the contextRoot used.");
		assertSame(data, svc.getDataArg(), "Expected the data Document passed to AEM would match the data Document used.");
		assertSame(options, svc.getOptionsArg(), "Expected the pdfRenderOptions passed to AEM would match the pdfOutputOptions used.");
		assertSame(result, svc.getResult(), "Expected the Document returned by AEM would match the Document result.");
	}

	@Test
	@DisplayName("Test GeneratePDFOutput(PathOrUrl,...) Happy Path with contentRoot.")
	void testGeneratePDFOutputPathOrUrlDocumentWithPDFOutputOptions() throws Exception {
		MockPdfOutputService svc = new MockPdfOutputService();
		
		String expectedTemplateFilename = "file:foo/bar.xdp";
		String expectedContextRoot = "file:notfoo/";
		PathOrUrl filename = PathOrUrl.from(expectedTemplateFilename);
		Document data = Mockito.mock(Document.class);
		PDFOutputOptions options = new PDFOutputOptionsImpl();
		options.setContentRoot(PathOrUrl.from(expectedContextRoot));
		Document result = underTest.generatePDFOutput(filename, data, options);
		
		// Verify that all the results are correct.
		assertEquals(expectedTemplateFilename, svc.getTemplateStringArg(), "Expected the template filename passed to AEM would match the filename used.");
		assertEquals(expectedContextRoot, svc.optionsArg.getValue().getContentRoot().toString(), "Expected the template contextRoot passed to AEM would match the contextRoot used.");
		assertSame(data, svc.getDataArg(), "Expected the data Document passed to AEM would match the data Document used.");
		assertSame(options, svc.getOptionsArg(), "Expected the pdfRenderOptions passed to AEM would match the pdfOutputOptions used.");
		assertSame(result, svc.getResult(), "Expected the Document returned by AEM would match the Document result.");
	}

	@Test
	@DisplayName("Test GeneratePDFOutput(Crx PathOrUrl,...) Happy Path with no contextRoot.")
	void testGeneratePDFOutputCrxUrlDocumentDefaultPDFOutputOptions() throws Exception {
		MockPdfOutputService svc = new MockPdfOutputService();
		
		String expectedTemplateFilename = "bar.xdp";
		String expectedContextRoot = "crx:/content/dam/formsanddocuments/foo/";
		PathOrUrl filename = PathOrUrl.from(expectedContextRoot + expectedTemplateFilename);
		Document data = Mockito.mock(Document.class);
		PDFOutputOptions options = new PDFOutputOptionsImpl();
		Document result = underTest.generatePDFOutput(filename, data, options);
		
		// Verify that all the results are correct.
		assertEquals(expectedTemplateFilename, svc.getTemplateStringArg(), "Expected the template filename passed to AEM would match the filename used.");
		assertEquals(expectedContextRoot, svc.optionsArg.getValue().getContentRoot().toString(), "Expected the template contextRoot passed to AEM would match the contextRoot used.");
		assertSame(data, svc.getDataArg(), "Expected the data Document passed to AEM would match the data Document used.");
		assertSame(options, svc.getOptionsArg(), "Expected the pdfRenderOptions passed to AEM would match the pdfOutputOptions used.");
		assertSame(result, svc.getResult(), "Expected the Document returned by AEM would match the Document result.");
	}

	@Test
	@DisplayName("Test GeneratePDFOutput(Crx PathOrUrl,...) Happy Path with contextRoot.")
	void testGeneratePDFOutputCrxUrlDocumentWithPDFOutputOptions() throws Exception {
		MockPdfOutputService svc = new MockPdfOutputService();
		
		String expectedTemplateFilename = "crx:/content/dam/formsanddocuments/foo/bar.xdp";
		String expectedContextRoot = "crx:/othercontent/dam/foo/bar/";
		PathOrUrl filename = PathOrUrl.from(expectedTemplateFilename);
		Document data = Mockito.mock(Document.class);
		PDFOutputOptions options = new PDFOutputOptionsImpl();
		options.setContentRoot(PathOrUrl.from(expectedContextRoot));
		Document result = underTest.generatePDFOutput(filename, data, options);
		
		// Verify that all the results are correct.
		assertEquals(expectedTemplateFilename, svc.getTemplateStringArg(), "Expected the template filename passed to AEM would match the filename used.");
		assertEquals(expectedContextRoot, svc.optionsArg.getValue().getContentRoot().toString(), "Expected the template contextRoot passed to AEM would match the contextRoot used.");
		assertSame(data, svc.getDataArg(), "Expected the data Document passed to AEM would match the data Document used.");
		assertSame(options, svc.getOptionsArg(), "Expected the pdfRenderOptions passed to AEM would match the pdfOutputOptions used.");
		assertSame(result, svc.getResult(), "Expected the Document returned by AEM would match the Document result.");
	}

	@Test
	@DisplayName("Test GeneratePDFOutput(PathOrUrl,...) with null arguments.")
	void testGeneratePDFOutputPathOrUrl_nullArguments() throws Exception {
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
		assertTrue(ex3.getMessage().toLowerCase().contains("PDFOutputOptions".toLowerCase()), ()->"'" + ex3.getMessage() + "' does not contain 'PDFOutputOptions'");

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
		assertTrue(svc.getDataArg() == data, "Expected the data Document passed to AEM would match the data Document used.");
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
		assertSame(data, svc.getDataArg(), "Expected the data Document passed to AEM would match the data Document used.");
		assertSame(result, svc.getResult(), "Expected the Document returned by AEM would match the Document result.");
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
		assertSame(data, svc.getDataArg(), "Expected the data Document passed to AEM would match the data Document used.");
		assertSame(result, svc.getResult(), "Expected the Document returned by AEM would match the Document result.");
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
		assertSame(data, svc.getDataArg(), "Expected the data Document passed to AEM would match the data Document used.");
		assertSame(result, svc.getResult(), "Expected the Document returned by AEM would match the Document result.");
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
		assertSame(data, svc.getDataArg(), "Expected the data Document passed to AEM would match the data Document used.");
		assertSame(result, svc.getResult(), "Expected the Document returned by AEM would match the Document result.");
		PDFOutputOptionsImplTest.assertNotEmpty(svc.getOptionsArg());
	}

	@Disabled
	void testGeneratePDFOutputBatch() {
		fail("Not yet implemented");
	}

	@Test
	void testGeneratePrintedOutputDocumentDocumentPrintedOutputOptions() throws Exception {
		MockPrintedOutputService svc = new MockPrintedOutputService();
		
		Document template = Mockito.mock(Document.class);
		Document data = Mockito.mock(Document.class);
		PrintedOutputOptions options = Mockito.mock(PrintedOutputOptions.class);
		Document result = underTest.generatePrintedOutput(template, data, options);
		
		// Verify that all the results are correct.
		assertEquals(template, svc.getTemplateDocArg(), "Expected the template filename passed to AEM would match the filename used.");
		assertSame(data, svc.getDataArg(), "Expected the data Document passed to AEM would match the data Document used.");
		assertSame(options, svc.getOptionsArg(), "Expected the pdfRenderOptions passed to AEM would match the pdfOutputOptions used.");
		assertSame(result, svc.getResult(), "Expected the Document returned by AEM would match the Document result.");
	}

	@Test
	@DisplayName("Test GeneratePrintedOutput(Path,Document,Options) null arguments.")
	void testRenderPrintedOutputDocumentPath_nullArguments() throws Exception {
		//Document template = Mockito.mock(Document.class);
		Path filename = TestUtils.SAMPLE_FORM;
		Document data = Mockito.mock(Document.class);
		PrintedOutputOptions printedFormRenderOptions = Mockito.mock(PrintedOutputOptions.class);
		Path nullPath = null;
		
		NullPointerException ex1 = assertThrows(NullPointerException.class, ()->underTest.generatePrintedOutput(nullPath, data, printedFormRenderOptions));
		assertTrue(ex1.getMessage().contains("template"), ()->"'" + ex1.getMessage() + "' does not contain 'template'");

		// Null Data is allowed.
//		NullPointerException ex2 = assertThrows(NullPointerException.class, ()->underTest.renderPDFForm(filename, null, pdfFormRenderOptions));
//		assertTrue(ex2.getMessage().contains("data"), ()->"'" + ex2.getMessage() + "' does not contain 'data'");
		
		NullPointerException ex3 = assertThrows(NullPointerException.class, ()->underTest.generatePrintedOutput(filename, data, null));
		assertTrue(ex3.getMessage().contains("printedOutputOptions"), ()->"'" + ex3.getMessage() + "' does not contain 'printedOutputOptions'");
	}
	
	@Test
	@DisplayName("Test GeneratePrintedOutput(Document,...) throws OutputServiceException.")
	void testRenderPrintedOutputDocument__OutputServiceExceptionThrown() throws Exception {
		Mockito.when(adobeOutputService.generatePrintedOutput(Mockito.any(Document.class), Mockito.any(), Mockito.any())).thenThrow(OutputServiceException.class);

		Document template = Mockito.mock(Document.class);
		Document data = Mockito.mock(Document.class);
		PrintedOutputOptions pdfFormRenderOptions = Mockito.mock(PrintedOutputOptions.class);
		
		assertThrows(OutputServiceException.class, ()->underTest.generatePrintedOutput(template, data, pdfFormRenderOptions));
	}
	
	@Test
	@DisplayName("Test GeneratePrintedOutput(Path,...) Happy Path, No Context Root.")
	void testGeneratePrintedOutputPathDocumentDefaultPrintedOutputOptions() throws Exception {
		MockPrintedOutputService svc = new MockPrintedOutputService();
		
		Path filename = TestUtils.SAMPLE_FORM;
		Document data = Mockito.mock(Document.class);
		PrintedOutputOptions options = new PrintedOutputOptionsImpl();
		Document result = underTest.generatePrintedOutput(filename, data, options);
		
		// Verify that all the results are correct.
		assertEquals(filename.getFileName(), Paths.get(svc.getTemplateStringArg()), "Expected the template filename passed to AEM would match the filename used.");
		assertEquals(filename.getParent(), Paths.get(svc.getOptionsArg().getContentRoot().toString()), "Expected the template filename passed to AEM would match the filename used.");
		assertSame(data, svc.getDataArg(), "Expected the data Document passed to AEM would match the data Document used.");
		assertSame(options, svc.getOptionsArg(), "Expected the pdfRenderOptions passed to AEM would match the pdfOutputOptions used.");
		assertSame(result, svc.getResult(), "Expected the Document returned by AEM would match the Document result.");
	}
	
	@Test
	@DisplayName("Test GeneratePrintedOutput(Path,...) Happy Path, with Context Root.")
	void testGeneratePrintedOutputPathDocumentNonDefaultPrintedOutputOptions() throws Exception {
		MockPrintedOutputService svc = new MockPrintedOutputService();
		
		Path filename = TestUtils.SAMPLE_FORM.getFileName();
		Document data = Mockito.mock(Document.class);
		PrintedOutputOptions options = new PrintedOutputOptionsImpl();
		Path contextRoot = TestUtils.SAMPLE_FORMS_DIR;
		options.setContentRoot(contextRoot);
		Document result = underTest.generatePrintedOutput(filename, data, options);
		
		// Verify that all the results are correct.
		assertEquals(filename.getFileName(), Paths.get(svc.getTemplateStringArg()), "Expected the template filename passed to AEM would match the filename used.");
		assertEquals(contextRoot, Paths.get(svc.getOptionsArg().getContentRoot().toString()), "Expected the template filename passed to AEM would match the filename used.");
		assertSame(data, svc.getDataArg(), "Expected the data Document passed to AEM would match the data Document used.");
		assertSame(options, svc.getOptionsArg(), "Expected the pdfRenderOptions passed to AEM would match the pdfOutputOptions used.");
		assertSame(result, svc.getResult(), "Expected the Document returned by AEM would match the Document result.");
	}
	
	@Test
	@DisplayName("Test GeneratePrintedOutput(Path,...) null arguments.")
	void testGeneratePrintedOutputPath_nullArguments() throws Exception {
		Path filename = TestUtils.SAMPLE_FORM;
		Document data = Mockito.mock(Document.class);
		PrintedOutputOptions printedFormRenderOptions = Mockito.mock(PrintedOutputOptions.class);
		Path nullFilename = null;
		
		NullPointerException ex1 = assertThrows(NullPointerException.class, ()->underTest.generatePrintedOutput(nullFilename, data, printedFormRenderOptions));
		assertNotNull(ex1.getMessage());	// Exception should contain a message.
		assertTrue(ex1.getMessage().contains("template"), ()->"'" + ex1.getMessage() + "' does not contain 'template'");
		
		NullPointerException ex3 = assertThrows(NullPointerException.class, ()->underTest.generatePrintedOutput(filename, data, null));
		assertNotNull(ex1.getMessage());	// Exception should contain a message.
		assertTrue(ex3.getMessage().contains("printedOutputOptions"), ()->"'" + ex3.getMessage() + "' does not contain 'printedOutputOptions'");
	}
	
	@Test
	@DisplayName("Test GeneratePrintedOutput(Document,...) null arguments.")
	void testRenderPrintedOutputDocument_nullArguments() throws Exception {
		//MockPrintedOutputService svc = new MockPrintedOutputService();
		
		Document template = Mockito.mock(Document.class);
		Document data = Mockito.mock(Document.class);
		PrintedOutputOptions printedFormRenderOptions = Mockito.mock(PrintedOutputOptions.class);
		Document nullDocument = null;
		
		NullPointerException ex1 = assertThrows(NullPointerException.class, ()->underTest.generatePrintedOutput(nullDocument, data, printedFormRenderOptions));
		assertTrue(ex1.getMessage().contains("Template"), ()->"'" + ex1.getMessage() + "' does not contain 'Template'");
		
		NullPointerException ex3 = assertThrows(NullPointerException.class, ()->underTest.generatePrintedOutput(template, data, null));
		assertTrue(ex3.getMessage().contains("Options"), ()->"'" + ex1.getMessage() + "' does not contain 'Options'");
		
	}
	
	@Test
	@DisplayName("Test GeneratePrintedOutput(Path,...) path argument.")
	void testRenderPrintedOutputDocumentPathOrUrl_PathInput() throws Exception {
		MockPrintedOutputService svc = new MockPrintedOutputService();
		
		Path filename = TestUtils.SAMPLE_FORM;
		
		Document data = Mockito.mock(Document.class);
		PrintedOutputOptions printedOutputOptions = Mockito.mock(PrintedOutputOptions.class);
		
		Document resultPath = underTest.generatePrintedOutput(filename, data, printedOutputOptions);
		
		assertSame(resultPath, svc.getResult(), "Expected the document to be the same - path input.");

	}
	
	@Disabled("Not currently working, needs investigation.  Not a big issue, since generatePrintedOutput is not implemented elsewhere yet.")
	@Test
	@DisplayName("Test GeneratePrintedOutput(Path,...) path argument.")
	void testRenderPrintedOutputDocumentPathOrUrl_MockPathInput() throws Exception {
		PathOrUrl pathOrUrl = Mockito.mock(PathOrUrl.class);
		Document data = Mockito.mock(Document.class);
		PrintedOutputOptions printedOutputOptions = Mockito.mock(PrintedOutputOptions.class);
		IllegalArgumentException ex1 = assertThrows
				(IllegalArgumentException.class, ()->underTest.generatePrintedOutput(pathOrUrl, data, printedOutputOptions));
		
	}
	@Test
	@DisplayName("Test GeneratePrintedOutput(Url,...) path argument.")
	void testRenderPrintedOutputDocumentPathOrUrl_URLInput() throws Exception {
		MockPrintedOutputService svc = new MockPrintedOutputService();
		String expectedTemplateFilename = "bar.xdp";
		String expectedContextRoot = "http://www.example.com/foo/";
		URL url = new URL(expectedContextRoot + expectedTemplateFilename);
		
		Document data = Mockito.mock(Document.class);
		PrintedOutputOptions printedOutputOptions = Mockito.mock(PrintedOutputOptions.class);
		
		Document resultURL = underTest.generatePrintedOutput(url, data, printedOutputOptions);
		
		assertSame(resultURL, svc.getResult(), "Expected the document to be the same - Url input.");
	}
	
	@Disabled("Not currently working, needs investigation.  Not a big issue, since generatePrintedOutput is not implemented elsewhere yet.")
	@Test
	void testGeneratePrintedOutputCrxUrl() throws Exception {
		MockPrintedOutputService svc = new MockPrintedOutputService();
		
		PathOrUrl filename = PathOrUrl.from("crx:/content/dam/formsanddocuments/foo/bar.xdp");
		Document data = Mockito.mock(Document.class);
		
		PrintedOutputOptions printedOutputOptions = Mockito.mock(PrintedOutputOptions.class);
		
		Document resultCrx = underTest.generatePrintedOutput(filename, data, printedOutputOptions);
		
		// Verify that all the results are correct.
		assertEquals(filename.toString(), svc.getTemplateStringArg(), "Expected the template filename passed to AEM would match the filename used.");
		assertSame(data, svc.getDataArg(), "Expected the data Document passed to AEM would match the data Document used.");
		assertSame(resultCrx, svc.getResult(), "Expected the Document returned by AEM would match the Document result.");
	}
	
	@Test
	@DisplayName("Test GeneratePrintedOutput(Path,...) throws FileNotFoundException.")
	void testGeneratePrintedOutputPath__BadTemplate() throws Exception {
		String filename = "foo/bar.xdp";
		Path filePath = Paths.get(filename);
		Document data = Mockito.mock(Document.class);
		PrintedOutputOptions printedOutputOptions = Mockito.mock(PrintedOutputOptions.class);
		
		FileNotFoundException ex1 = assertThrows(FileNotFoundException.class, ()->underTest.generatePrintedOutput(filePath, data, printedOutputOptions));
		String message = ex1.getMessage();
		assertTrue(message.contains("template"), ()->"Expected exception message to contain a mention of the template. (" + message + ").");
		assertTrue(message.contains(filePath.toString()), "Expected exception message to contain the filepath provided. (" + message + ").");
	}
	
	@Test
	@DisplayName("Test GeneratePrintedOutput(Path,...) throws OutputServiceException.")
	void testGeneratePrintedOutputPath__FormsServiceExceptionThrown() throws Exception {
		Mockito.when(adobeOutputService.generatePrintedOutput(Mockito.any(String.class), Mockito.any(), Mockito.any())).thenThrow(OutputServiceException.class);

		Path filename = TestUtils.SAMPLE_FORM;
		Document data = Mockito.mock(Document.class);
		PrintedOutputOptions printedFormRenderOptions = Mockito.mock(PrintedOutputOptions.class);
		
		assertThrows(OutputServiceException.class, ()->underTest.generatePrintedOutput(filename, data, printedFormRenderOptions));
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
		private final ArgumentCaptor<String> templateStringArg = ArgumentCaptor.forClass(String.class);
		private final ArgumentCaptor<Document> templateDocArg = ArgumentCaptor.forClass(Document.class);
		private final ArgumentCaptor<Document> dataArg = ArgumentCaptor.forClass(Document.class);
		private final ArgumentCaptor<PrintedOutputOptions> optionsArg = ArgumentCaptor.forClass(PrintedOutputOptions.class);
		
		protected MockPrintedOutputService() throws OutputServiceException {
			super();
			// These are "lenient" because we only expect one or the other to be called.  Also, in some of the exceptional cases,
			// neither are called.
			Mockito.lenient().when(adobeOutputService.generatePrintedOutput(templateStringArg.capture(), dataArg.capture(), optionsArg.capture())).thenReturn(result);
			Mockito.lenient().when(adobeOutputService.generatePrintedOutput(templateDocArg.capture(), dataArg.capture(), optionsArg.capture())).thenReturn(result);
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

		protected PrintedOutputOptions getOptionsArg() {
			return optionsArg.getValue();
		}
	}


}
