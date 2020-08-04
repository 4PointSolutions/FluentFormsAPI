package com._4point.aem.fluentforms.testing.generatePDF;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.generatePDF.CreatePDFResult;
import com._4point.aem.fluentforms.impl.generatePDF.CreatePDFResultImpl;
import com._4point.aem.fluentforms.impl.generatePDF.PDFSettings;
import com._4point.aem.fluentforms.impl.generatePDF.SecuritySettings;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;
import com._4point.aem.fluentforms.testing.generatePDF.MockTraditionalGeneratePDFService.GeneratePDFResultArgs;

public class MockGeneratePDFServiceTest {

	@Test
	void testGeneratePdf() throws Exception {
		String expectedResultString = "Test Result";
		MockDocumentFactory mockDocumentFactory = new MockDocumentFactory();
		Document inputDoc = mockDocumentFactory.create(expectedResultString.getBytes());
		CreatePDFResult createPDFResult = Mockito.mock(CreatePDFResultImpl.class);
		MockGeneratePDFService underTest = MockGeneratePDFService.createGeneratePDFServiceMock(createPDFResult);
		CreatePDFResult result = underTest.createPDF().executeOn(inputDoc, "pptx");
		GeneratePDFResultArgs capturedArgs = underTest.getGeneratePDFResultArgs();
		assertEquals(result, createPDFResult);
		assertEquals(inputDoc, capturedArgs.getInputDoc());
		assertEquals("pptx", capturedArgs.getInputFileExtension());
		assertNull(capturedArgs.getFileTypeSettings());
		assertNull(capturedArgs.getPdfSettings());
		assertNull(capturedArgs.getSecuritySettings());
		assertNull(capturedArgs.getSettingsDoc());
		assertNull(capturedArgs.getXmpDoc());
	}

	@Test
	void testGeneratePdfWithOptionalParameters() throws Exception {
		String expectedResultString = "Test Result";
		MockDocumentFactory mockDocumentFactory = new MockDocumentFactory();
		Document inputDoc = mockDocumentFactory.create(expectedResultString.getBytes());
		CreatePDFResult createPDFResult = Mockito.mock(CreatePDFResultImpl.class);
		MockGeneratePDFService underTest = MockGeneratePDFService.createGeneratePDFServiceMock(createPDFResult);
		CreatePDFResult result = underTest.createPDF().setFileTypeSettings("Filetype Settings")
				.setPdfSetting(PDFSettings.High_Quality_Print).setSecuritySetting(SecuritySettings.Password_Security)
				.setSettingDoc(Mockito.mock(Document.class)).setxmpDoc(Mockito.mock(Document.class))
				.executeOn(inputDoc, "pptx");
		GeneratePDFResultArgs capturedArgs = underTest.getGeneratePDFResultArgs();
		assertEquals(result, createPDFResult);
		assertEquals(inputDoc, capturedArgs.getInputDoc());
		assertEquals("pptx", capturedArgs.getInputFileExtension());
		assertEquals("Filetype Settings", capturedArgs.getFileTypeSettings());
		assertEquals("High_Quality_Print", capturedArgs.getPdfSettings().toString());
		assertEquals("Password_Security", capturedArgs.getSecuritySettings().toString());
		assertNotNull(capturedArgs.getSettingsDoc());
		assertNotNull(capturedArgs.getXmpDoc());
	}
}
