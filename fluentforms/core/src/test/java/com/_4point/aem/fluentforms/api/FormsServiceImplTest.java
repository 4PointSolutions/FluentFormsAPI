package com._4point.aem.fluentforms.api;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com._4point.aem.fluentforms.api.forms.FormsService;
import com._4point.aem.fluentforms.api.forms.FormsService.FormsServiceException;
import com._4point.aem.fluentforms.api.forms.ValidationOptions;
import com._4point.aem.fluentforms.impl.forms.FormsServiceImpl;
import com._4point.aem.fluentforms.impl.forms.TraditionalFormsService;
import com.adobe.fd.forms.api.DataFormat;
import com.adobe.fd.forms.api.PDFFormRenderOptions;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
class FormsServiceImplTest {

	private final AemContext context = new AemContext();

	@Mock
	private TraditionalFormsService adobeFormsService;

	private FormsService underTest; 

	@BeforeEach
	void setUp() throws Exception {
		underTest = new FormsServiceImpl(adobeFormsService);
	}

	@Test
	@DisplayName("Test exportData() Happy Path.")
	void testExportData() throws com.adobe.fd.forms.api.FormsServiceException, FormsServiceException {
		Document result = Mockito.mock(Document.class);
		ArgumentCaptor<com.adobe.fd.forms.api.DataFormat> dataFormatArg = ArgumentCaptor.forClass(com.adobe.fd.forms.api.DataFormat.class);
		ArgumentCaptor<Document> documenttArg = ArgumentCaptor.forClass(Document.class);
		Mockito.when(adobeFormsService.exportData(documenttArg.capture(), dataFormatArg.capture())).thenReturn(result);

		DataFormat dataFormat = DataFormat.XmlData;
		Document pdfOrXdp = Mockito.mock(Document.class);

		Document exportedData = underTest.exportData(pdfOrXdp, dataFormat);

		// Verify that all the results are correct.
		assertTrue(documenttArg.getValue() == pdfOrXdp, "Expected the Document passed to AEM would match the Document object argument.");
		assertTrue(dataFormatArg.getValue() == dataFormat, "Expected the DataFormat passed to AEM would match the DataFormat object argument.");
		assertTrue(exportedData == result, "Expected the Document returned by AEM would match the Document result.");
	}

	@Test
	@DisplayName("Test exportData() with null arguments throws exception and the exception contains the argument name.")
	void testExportData_nullArguments() throws FormsServiceException {
		DataFormat dataFormat = DataFormat.XmlData;
		NullPointerException ex1 = assertThrows(NullPointerException.class, ()->underTest.exportData(null, dataFormat));
		assertTrue(ex1.getMessage().contains("pdfOrXdp"));
		
		Document pdfOrXdp = Mockito.mock(Document.class);
		NullPointerException ex2 = assertThrows(NullPointerException.class, ()->underTest.exportData(pdfOrXdp, null));
		assertTrue(ex2.getMessage().contains("dataFormat"));
	}

	@Test
	@DisplayName("Test exportData() when exception is thrown.")
	void testExportData_FormsServiceExceptionThrown() throws FormsServiceException {
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
		assertTrue(pdfArg.getValue() == pdf, "Expected the pdf Document passed to AEM would match the pdf Document used.");
		assertTrue(dataArg.getValue() == data, "Expected the data Document passed to AEM would match the data Docyment used.");
		assertTrue(pdfResult == result, "Expected the Document returned by AEM would match the Document result.");
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
	@DisplayName("Test RenderPDFForm(Path,...) Happy Path.")
	void testRenderPDFFormPathDocumentPDFFormRenderOptions() throws FormsServiceException {
		Document result = Mockito.mock(Document.class);
		ArgumentCaptor<String> templateArg = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Document> dataArg = ArgumentCaptor.forClass(Document.class);
		ArgumentCaptor<PDFFormRenderOptions> optionsArg = ArgumentCaptor.forClass(PDFFormRenderOptions.class);
		Mockito.when(adobeFormsService.renderPDFForm(templateArg.capture(), dataArg.capture(), optionsArg.capture())).thenReturn(result);
		
		String filename = "foo/bar.xdp";
		Path filePath = Paths.get(filename);
		Document data = Mockito.mock(Document.class);
		PDFFormRenderOptions pdfFormRenderOptions = Mockito.mock(PDFFormRenderOptions.class);
		Document pdfResult = underTest.renderPDFForm(filePath, data, pdfFormRenderOptions);
		
		// Verify that all the results are correct.
		assertEquals(Paths.get(templateArg.getValue()), Paths.get(filename), "Expected the template filename passed to AEM would match the filename used.");
		assertTrue(dataArg.getValue() == data, "Expected the data Document passed to AEM would match the data Docyment used.");
		assertTrue(optionsArg.getValue() == pdfFormRenderOptions, "Expected the pdfRenderOptions passed to AEM would match the pdfRenderOptions used.");
		assertTrue(pdfResult == result, "Expected the Document returned by AEM would match the Document result.");
	}

	@Test
	@DisplayName("Test RenderPDFForm(Path,...) null arguments.")
	void testRenderPDFFormPath_nullArguments() throws FormsServiceException {
		Path filename = Mockito.mock(Path.class);
		Document data = Mockito.mock(Document.class);
		PDFFormRenderOptions pdfFormRenderOptions = Mockito.mock(PDFFormRenderOptions.class);
		
		NullPointerException ex1 = assertThrows(NullPointerException.class, ()->underTest.renderPDFForm(filename, data, pdfFormRenderOptions));
		assertTrue(ex1.getMessage().contains("filename"), ()->"'" + ex1.getMessage() + "' does not contain 'filename'");
		
		NullPointerException ex2 = assertThrows(NullPointerException.class, ()->underTest.renderPDFForm(filename, data, pdfFormRenderOptions));
		assertTrue(ex2.getMessage().contains("data"), ()->"'" + ex2.getMessage() + "' does not contain 'data'");
		
		NullPointerException ex3 = assertThrows(NullPointerException.class, ()->underTest.renderPDFForm(filename, data, pdfFormRenderOptions));
		assertTrue(ex3.getMessage().contains("pdfFormRenderOptions"), ()->"'" + ex3.getMessage() + "' does not contain 'pdfFormRenderOptions'");
	}

	@Test
	@DisplayName("Test RenderPDFForm(Path,...) throws FormsServiceException.")
	void testRenderPDFFormPath__FormsServiceExceptionThrown() throws FormsServiceException {
		Mockito.when(adobeFormsService.renderPDFForm(Mockito.any(), Mockito.any(), Mockito.any())).thenThrow(FormsServiceException.class);

		Path filename = Mockito.mock(Path.class);
		Document data = Mockito.mock(Document.class);
		PDFFormRenderOptions pdfFormRenderOptions = Mockito.mock(PDFFormRenderOptions.class);
		
		assertThrows(FormsServiceException.class, ()->underTest.renderPDFForm(filename, data, pdfFormRenderOptions));
	}

	@Test
	void testRenderPDFFormURLDocumentPDFFormRenderOptions() {
		fail("Not yet implemented");
	}

	@Test
	void testRenderPDFForm() {
		fail("Not yet implemented");
	}

	@Test
	void testValidatePathDocumentValidationOptions() {
		fail("Not yet implemented");
	}

	@Test
	void testValidate() {
		fail("Not yet implemented");
	}

}
