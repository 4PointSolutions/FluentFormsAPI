package com._4point.aem.fluentforms.impl.generatePDF;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.generatePDF.CreatePDFResult;
import com._4point.aem.fluentforms.api.generatePDF.GeneratePDFService;
import com._4point.aem.fluentforms.api.generatePDF.GeneratePDFService.GeneratePDFServiceException;

import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
public class GeneratePDFServiceImplTest {

	@Mock
	private TraditionalGeneratePDFService adobeGeneratePDFService;

	private GeneratePDFService underTest;

	@BeforeEach
	void setUp() throws Exception {
		underTest = new GeneratePDFServiceImpl(adobeGeneratePDFService);
	}

	@Test
	@DisplayName("Test testCreatePDF2(Document,...) Happy Path.")
	void testCreatePDF2() throws Exception {
		GeneratePdfServiceMock svc = new GeneratePdfServiceMock();
		Document inputDoc = Mockito.mock(Document.class);
		String inputFileExtension = "docx";
		String fileTypeSettings = null;
		PDFSettings pdfSettings = null;
		SecuritySettings securitySettings = null;
		Document settingsDoc = Mockito.mock(Document.class);
		Document xmpDoc = Mockito.mock(Document.class);
		CreatePDFResult pdfResult = underTest.createPDF2(inputDoc, inputFileExtension, fileTypeSettings, pdfSettings,
				securitySettings, settingsDoc, xmpDoc);
		// Verify that all the results are correct.
		assertEquals(inputDoc, svc.getInputDoc(), "Expected the inputDoc passed to AEM would match the inputDoc used.");
		assertTrue(pdfResult == svc.getCreatePDFResult(),
				"Expected the CreatePDFResult returned by AEM would match the CreatePDFResult.");
		assertTrue(inputFileExtension == svc.getInpuFileextension(),
				"Expected the inputFileExtension returned by AEM would match the inputFileExtension.");
		assertTrue(fileTypeSettings == svc.getFileTypeSettings(),
				"Expected the fileTypeSettings returned by AEM would match the fileTypeSettings.");
		assertTrue(securitySettings == svc.getSecuritySettings(),
				"Expected the securitySettings returned by AEM would match the securitySettings.");
		assertEquals(pdfSettings, svc.getPdfSettings(),
				"Expected the pdfSettings returned by AEM would match the pdfSettings.");
		assertEquals(settingsDoc, svc.getSettingsDoc(),
				"Expected the settingsDoc returned by AEM would match the settingsDoc.");
		assertEquals(xmpDoc, svc.getXmpDoc(), "Expected the xmpDoc returned by AEM would match the xmpDoc.");
	}

	@Test
	@DisplayName("Test testGeneratePDF(Document,...) null arguments.")
	void testGeneratePDF_nullArguments() throws Exception {
		Document nullDocument = null;
		Document inputDoc = Mockito.mock(Document.class);
		NullPointerException ex1 = assertThrows(NullPointerException.class,
				() -> underTest.createPDF2(nullDocument, ".docx", null, null, null, null, null));
		assertTrue(ex1.getMessage().contains("inputDoc"),() -> "'" + ex1.getMessage() + "' does not contain 'inputDoc'");

		NullPointerException ex2 = assertThrows(NullPointerException.class,() -> underTest.createPDF2(inputDoc, null, null, null, null, null, null));
		assertTrue(ex2.getMessage().contains("inputFileExtension"),() -> "'" + ex2.getMessage() + "' does not contain 'inputFileExtension'");
	}

	@Test  
	@DisplayName("Test testGeneratePDF(Document,...) Happy Path.") void
	testGeneratePDF() throws Exception {
		GeneratePdfServiceMock svc = new GeneratePdfServiceMock();
		Document inputDoc = Mockito.mock(Document.class);
		CreatePDFResult pdfResult =  underTest.createPDF()
				                               .setFileTypeSettings("Filetype Settings")
				                          	   .setPdfSetting(PDFSettings.High_Quality_Print)
				                          	   .setSecuritySetting(SecuritySettings.Certificate_Security)
				                          	   .setSettingDoc(Mockito.mock(Document.class))
				                          	   .setxmpDoc(Mockito.mock(Document.class))
				                          	   .executeOn(inputDoc, "docx");
		assertEquals(inputDoc, svc.getInputDoc(), "Expected the inputDoc passed to AEM would match the inputDoc used.");
		assertTrue(pdfResult == svc.getCreatePDFResult(),
				"Expected the CreatePDFResult returned by AEM would match the CreatePDFResult.");
		assertTrue("docx" == svc.getInpuFileextension());
	    assertNotNull(svc.getFileTypeSettings());
	    assertNotNull(svc.getInpuFileextension());		 			                              
	    assertNotNull(svc.getInputDoc());	
	    assertNotNull(svc.getPdfSettings());
	    assertNotNull(svc.getSettingsDoc());
	    assertNotNull(svc.getXmpDoc());
	}

	private class GeneratePdfServiceMock {
		private final CreatePDFResult createPDFResult = Mockito.mock(CreatePDFResult.class);
		private final ArgumentCaptor<Document> inputDoc = ArgumentCaptor.forClass(Document.class);
		private final ArgumentCaptor<String> inpuFileextension = ArgumentCaptor.forClass(String.class);
		private final ArgumentCaptor<String> fileTypeSettings = ArgumentCaptor.forClass(String.class);
		private final ArgumentCaptor<PDFSettings> pdfSettings = ArgumentCaptor.forClass(PDFSettings.class);
		private final ArgumentCaptor<SecuritySettings> securitySettings = ArgumentCaptor.forClass(SecuritySettings.class);
		private final ArgumentCaptor<Document> settingsDoc = ArgumentCaptor.forClass(Document.class);
		private final ArgumentCaptor<Document> xmpDoc = ArgumentCaptor.forClass(Document.class);

		protected GeneratePdfServiceMock() throws GeneratePDFServiceException {
			super();
			// These are "lenient" because we only expect one or the other to be called.
			// Also, in some of the exceptional cases,
			// neither are called.
			Mockito.lenient()
			.when(adobeGeneratePDFService.createPDF2(inputDoc.capture(), inpuFileextension.capture(),
					fileTypeSettings.capture(), pdfSettings.capture(), securitySettings.capture(),
					settingsDoc.capture(), xmpDoc.capture()))
			.thenReturn(createPDFResult);
		}

		public CreatePDFResult getCreatePDFResult() {
			return createPDFResult;
		}

		public Document getInputDoc() {
			return inputDoc.getValue();
		}

		public String getInpuFileextension() {
			return inpuFileextension.getValue();
		}

		public String getFileTypeSettings() {
			return fileTypeSettings.getValue();
		}

		public PDFSettings getPdfSettings() {
			return pdfSettings.getValue();
		}

		public SecuritySettings getSecuritySettings() {
			return securitySettings.getValue();
		}

		public Document getSettingsDoc() {
			return settingsDoc.getValue();
		}

		public Document getXmpDoc() {
			return xmpDoc.getValue();
		}
	}

}
