package com._4point.aem.docservices.rest_services.it_tests.client.generatePDF;

import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com._4point.aem.docservices.rest_services.client.generatePDF.RestServicesGeneratePDFServiceAdapter;
import com._4point.aem.fluentforms.api.generatePDF.CreatePDFResult;
import com._4point.aem.fluentforms.api.generatePDF.GeneratePDFService;
import com._4point.aem.fluentforms.api.generatePDF.GeneratePDFService.GeneratePDFServiceException;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;
import com._4point.aem.fluentforms.impl.generatePDF.GeneratePDFServiceImpl;
import com._4point.aem.fluentforms.impl.generatePDF.PDFSettings;
import com._4point.aem.fluentforms.impl.generatePDF.SecuritySettings;

public class GeneratePDFTest {
	private static final MediaType APPLICATION_PDF = new MediaType("application", "pdf");
	private GeneratePDFService underTest;

	@BeforeEach
	void setUp() throws Exception {
		RestServicesGeneratePDFServiceAdapter adapter = RestServicesGeneratePDFServiceAdapter.builder()
				.machineName(TEST_MACHINE_NAME)
				.port(TEST_MACHINE_PORT)
				.basicAuthentication(TEST_USER, TEST_USER_PASSWORD)
				.useSsl(false)
				.aemServerType(TEST_MACHINE_AEM_TYPE)
				.build();

		underTest = new GeneratePDFServiceImpl(adapter);
	}

	@Test
	@DisplayName("Test testGeneratePdf() Happy Path.")
	void testGeneratePdf() throws Exception {
		CreatePDFResult createPDFResult = underTest.createPDF()
				.executeOn(SimpleDocumentFactoryImpl.getFactory().create(SAMPLE_FORM_DOCX.toFile()), "docx");
	    assertNotNull(createPDFResult.getCreatedDocument().getInlineData());
	    assertEquals(APPLICATION_PDF.toString(), createPDFResult.getCreatedDocument().getContentType());
	}

	@Test
	@DisplayName("Test testGeneratePdf() All Arguments.")
	void testGeneratePdf_AllArgs() throws Exception {
		CreatePDFResult createPDFResult = underTest.createPDF().setPdfSetting(PDFSettings.High_Quality_Print)
				.setSecuritySetting(SecuritySettings.No_Security).setFileTypeSettings("").setSettingDoc(null)
				.setxmpDoc(null)
				.executeOn(SimpleDocumentFactoryImpl.getFactory().create(SAMPLE_FORM_DOCX.toFile()), "docx");
		   assertNotNull(createPDFResult.getCreatedDocument().getInlineData());
		   assertEquals(APPLICATION_PDF.toString(), createPDFResult.getCreatedDocument().getContentType());
	}

	@Test
	@DisplayName("Test testGeneratePdf with bad data.")
	void testGeneratePdf_BadData() throws Exception {
		GeneratePDFServiceException ex = assertThrows(GeneratePDFServiceException.class,
				() -> underTest.createPDF2(SimpleDocumentFactoryImpl.getFactory().create(SAMPLE_FORM_DOCX.toFile()),
						" ", null, null, null, null, null));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertTrue(msg.contains("Call to server failed"));
	}
}
