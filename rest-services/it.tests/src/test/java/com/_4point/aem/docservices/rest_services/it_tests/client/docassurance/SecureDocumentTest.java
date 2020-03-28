package com._4point.aem.docservices.rest_services.it_tests.client.docassurance;

import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.ACTUAL_RESULTS_DIR;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.SAMPLE_FORM_DATA_XML;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.SAMPLE_FORM_PDF;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.TEST_MACHINE_NAME;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.TEST_MACHINE_PORT;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.TEST_USER;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.TEST_USER_PASSWORD;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.containsStringIgnoringCase;
import static org.hamcrest.MatcherAssert.assertThat;

import java.nio.file.Files;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com._4point.aem.docservices.rest_services.client.docassurance.RestServicesDocAssuranceServiceAdapter;
import com._4point.aem.docservices.rest_services.it_tests.ByteArrayString;
import com._4point.aem.docservices.rest_services.it_tests.TestUtils;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.docassurance.DocAssuranceService;
import com._4point.aem.fluentforms.api.docassurance.DocAssuranceService.DocAssuranceServiceException;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;
import com._4point.aem.fluentforms.impl.docassurance.DocAssuranceServiceImpl;

public class SecureDocumentTest {

	private DocAssuranceService underTest; 

	@BeforeEach
	void setUp() throws Exception {
		RestServicesDocAssuranceServiceAdapter adapter = RestServicesDocAssuranceServiceAdapter.builder()
		                                                     .machineName(TEST_MACHINE_NAME)
		                                                     .port(TEST_MACHINE_PORT)
		                                                     .basicAuthentication(TEST_USER, TEST_USER_PASSWORD)
		                                                     .useSsl(false)
		                                                     .build();

		underTest = new DocAssuranceServiceImpl(adapter);
	}

	@Test
	@DisplayName("Test readerExtendPDF() All Arguments.")
	void testReaderExtendPDF_AllArgs() throws Exception {
		
		Document pdfResult =  underTest.secureDocument()
		                          .readerExtensionsOptions("recred")
		                              .setReOptions()
		                                  .setUsageRights()
		                                      .setEnabledBarcodeDecoding(true)
		                                      .setEnabledComments(true)
		                                      .setEnabledCommentsOnline(true)
		                                      .setEnabledDigitalSignatures(true)
		                                      .setEnabledDynamicFormFields(true)
		                                      .setEnabledDynamicFormPages(true)
		                                      .setEnabledEmbeddedFiles(true)
		                                      .setEnabledFormDataImportExport(true)
		                                      .setEnabledFormFillIn(true)
		                                      .setEnabledOnlineForms(true)
		                                      .setEnabledSubmitStandalone(true)
		                                  .done()
		                              .done()
		                          .done()
		                          .executeOn(SimpleDocumentFactoryImpl.getFactory().create(SAMPLE_FORM_PDF.toFile()));
		
		TestUtils.validatePdfResult(pdfResult.getInlineData(), "testReaderExtendPDF_AllArgs_Client.pdf", true, true, true);
	}

	@Test
	@DisplayName("Test readerExtendPDF() Just PDF and Credential.")
	void testReaderExtendPDF_JustInDocAndCred() throws Exception {
		try {
			Document pdfResult =  underTest.secureDocument()
			                          .readerExtensionsOptions("recred")
			                          .done()
			                          .executeOn(SimpleDocumentFactoryImpl.getFactory().create(SAMPLE_FORM_PDF.toFile()));
			assertThat("Expected an error to be thrown.", pdfResult.getInlineData().length > 0);
		} catch (DocAssuranceServiceException e) {
			assertThat(e.getMessage(), containsStringIgnoringCase("Internal Error while reader extending a PDF."));
			// At least one non-null usage right must be specified or will get a com.adobe.internal.pdftoolkit.core.exceptions.PDFInvalidParameterException, which will cause...
			assertThat(e.getMessage(), containsStringIgnoringCase("AEM-REX-001-008: Unable to apply the requested usage rights to the given document."));
		}
	}

	@Test
	@DisplayName("Test readerExtendPDF() Bad Credential Alias.")
	void testReaderExtendPDF_BadCredentialAlias() throws Exception {
		try {
			Document pdfResult =  underTest.secureDocument()
			                          .readerExtensionsOptions("bad")
			                          .done()
			                          .executeOn(SimpleDocumentFactoryImpl.getFactory().create(SAMPLE_FORM_PDF.toFile()));
			assertThat("Expected an error to be thrown.", pdfResult.getInlineData().length > 0);
		} catch (DocAssuranceServiceException e) {
			assertThat(e.getMessage(), containsStringIgnoringCase("Internal Error while reader extending a PDF."));
			assertThat(e.getMessage(), containsStringIgnoringCase("No credential found with alias [bad]."));
		}
	}

	@Test
	@DisplayName("Test readerExtendPDF() Null Credential Alias.")
	void testReaderExtendPDF_NullCredentialAlias() throws Exception {
		try {
			Document pdfResult =  underTest.secureDocument()
			                          .readerExtensionsOptions(null)
			                          .done()
			                          .executeOn(SimpleDocumentFactoryImpl.getFactory().create(SAMPLE_FORM_PDF.toFile()));
			assertThat("Expected an error to be thrown.", pdfResult.getInlineData().length > 0);
		} catch (NullPointerException e) {
			assertThat(e.getMessage(), containsStringIgnoringCase("Credential Alias provided in Reader Extension options cannot be null."));
		}
	}

	@Test
	@DisplayName("Test readerExtendPDF() Credential Alias is Empty.")
	void testReaderExtendPDF_NoCredentialAlias() throws Exception {
		try {
			Document pdfResult =  underTest.secureDocument()
			                          .readerExtensionsOptions("")
			                          .done()
			                          .executeOn(SimpleDocumentFactoryImpl.getFactory().create(SAMPLE_FORM_PDF.toFile()));
			assertThat("Expected an error to be thrown.", pdfResult.getInlineData().length > 0);
		} catch (DocAssuranceServiceException e) {
			assertThat(e.getMessage(), containsStringIgnoringCase("Internal Error while reader extending a PDF."));
			assertThat(e.getMessage(), containsStringIgnoringCase("AEM-REX-001-023: The credential alias is not specified."));
		}
	}

	@Test
	@DisplayName("Test readerExtendPDF() InDoc is not a PDF.")
	void testReaderExtendPDF_InDocNotPdf() throws Exception {
		try {
			Document pdfResult =  underTest.secureDocument()
			                          .readerExtensionsOptions("recred")
			                          .done()
			                          .executeOn(SimpleDocumentFactoryImpl.getFactory().create(SAMPLE_FORM_DATA_XML.toFile()));
			assertThat("Expected an error to be thrown.", pdfResult.getInlineData().length > 0);
		} catch (DocAssuranceServiceException e) {
			assertThat(e.getMessage(), containsStringIgnoringCase("Internal Error while reader extending a PDF."));
			assertThat(e.getMessage(), containsStringIgnoringCase("Exception while converting Doc to PDF in DocAssuranceService"));
			assertThat(e.getMessage(), containsStringIgnoringCase("Stream does not represent a PDF document."));
		}
	}

}
