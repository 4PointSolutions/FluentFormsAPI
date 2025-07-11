package com._4point.aem.docservices.rest_services.it_tests.client.output;

import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.*;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import com._4point.aem.docservices.rest_services.client.jersey.JerseyRestClient;
import com._4point.aem.docservices.rest_services.client.output.RestServicesOutputServiceAdapter;
import com._4point.aem.docservices.rest_services.it_tests.AemInstance;
import com._4point.aem.docservices.rest_services.it_tests.Pdf;
import com._4point.aem.docservices.rest_services.it_tests.TestUtils;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.api.output.OutputService;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;
import com._4point.aem.fluentforms.impl.UsageContext;
import com._4point.aem.fluentforms.impl.output.OutputServiceImpl;
import com.adobe.fd.output.api.AcrobatVersion;

@Tag("client-tests")
class GeneratePdfOutputTest {

	private static final String CRX_CONTENT_ROOT = "crx:/content/dam/formsanddocuments/sample-forms";

	private OutputService underTest;
	
	@BeforeAll
	static void setUpAll() throws Exception {
		AemInstance.AEM_1.prepareForTests();
	}

	@BeforeEach
	void setUp() throws Exception {
		RestServicesOutputServiceAdapter adapter = RestServicesOutputServiceAdapter.builder(JerseyRestClient.factory())
				.machineName(AemInstance.AEM_1.aemHost())
				.port(AemInstance.AEM_1.aemPort())
				.basicAuthentication(TEST_USER, TEST_USER_PASSWORD)
				.useSsl(false)
				.aemServerType(TEST_MACHINE_AEM_TYPE)
				.build();

		underTest = new OutputServiceImpl(adapter, UsageContext.CLIENT_SIDE);
	}

	@Test
	@DisplayName("Test generatePdfOutput() Just Form and Data.")
	void testGeneratePdfOutput_JustFormAndData() throws Exception {
		Document pdfResult =  underTest.generatePDFOutput()
									.executeOn(REMOTE_SAMPLE_FORM_XDP, SimpleDocumentFactoryImpl.getFactory().create(LOCAL_SAMPLE_FORM_DATA_XML));
		
		TestUtils.validatePdfResult(pdfResult.getInlineData(), "GeneratePdfOutput_JustFormAndData.pdf", false, false, false);
	}

	@Test
	@DisplayName("Test generatePdfOutput() Just Form Document and Data.")
	void testGeneratePdfOutput_JustFormDocAndData() throws Exception {
		Document pdfResult =  underTest.generatePDFOutput()
									.executeOn(SimpleDocumentFactoryImpl.getFactory().create(LOCAL_SAMPLE_FORM_XDP), SimpleDocumentFactoryImpl.getFactory().create(LOCAL_SAMPLE_FORM_DATA_XML));
		
		TestUtils.validatePdfResult(pdfResult.getInlineData(), "testGeneratePdfOutput_JustFormDocAndData.pdf", false, false, false);
	}

	@Test
	@DisplayName("Test generatePdfOutput() CRX Form and Data.")
	void testGeneratePdfOutput_CRXFormAndData() throws Exception {
		Document pdfResult =  underTest.generatePDFOutput()
									.setContentRoot(PathOrUrl.from(CRX_CONTENT_ROOT))
									.executeOn(REMOTE_SAMPLE_FORM_XDP.getFileName(), SimpleDocumentFactoryImpl.getFactory().create(LOCAL_SAMPLE_FORM_DATA_XML));
		
		TestUtils.validatePdfResult(pdfResult.getInlineData(), "GeneratePdfOutput_CRXFormAndData.pdf", false, false, false);		
	}

	@Test
	@DisplayName("Test generatePdfOutput() All Arguments.")
	void testGeneratePdfOutput_AllArgs() throws Exception {
		AcrobatVersion acrobatVersion = AcrobatVersion.Acrobat_10_1;
		Path contentRoot = REMOTE_SAMPLE_FORM_XDP.getParent();
//		Path debugDir = null;
		
		Document pdfResult =  underTest.generatePDFOutput()
									.setAcrobatVersion(acrobatVersion)
									.setContentRoot(contentRoot)
//									.setDebugDir(debugDir)
									.setEmbedFonts(true)
									.setLinearizedPDF(true)
									.setLocale(Locale.CANADA_FRENCH)
									.setRetainPDFFormState(true)
									.setRetainUnsignedSignatureFields(true)
									.setTaggedPDF(true)
									// TODO: Since contentRoot is set, the full path should not be necessary. Try changing to just the filename.
									.executeOn(REMOTE_SAMPLE_FORM_XDP, SimpleDocumentFactoryImpl.getFactory().create(LOCAL_SAMPLE_FORM_DATA_XML));
		
		TestUtils.validatePdfResult(pdfResult.getInlineData(), "GeneratePdfOutput_AllArgs.pdf", false, false, false);		
	}
	
	@Test
	@DisplayName("Test generatePdfOutput() Just Form Doc.  FluentFormsAPI Issue #15")
	void testGeneratePdfOutput_JustFormDocIssue15() throws Exception {
		Document pdfResult =  underTest.generatePDFOutput()
									   .executeOn(SimpleDocumentFactoryImpl.getFactory().create(LOCAL_SAMPLE_ARTWORK_PDF));
		
		TestUtils.validatePdfResult(pdfResult.getInlineData(), "GeneratePdfOutput_JustFormDocIssue15.pdf", false, false, false);
	}

	@Test
	@DisplayName("Test generatePdfOutput() Just Form.  FluentFormsAPI Issue #15")
	void testGeneratePdfOutput_JustFormIssue15() throws Exception {
		Document pdfResult =  underTest.generatePDFOutput()
									   .executeOn(REMOTE_SAMPLE_ARTWORK_PDF);
		
		TestUtils.validatePdfResult(pdfResult.getInlineData(), "testGeneratePdfOutput_JustFormIssue15.pdf", false, false, false);
	}

	@Test
	@DisplayName("Test generatePdfOutput() for issue #32.")
	void testGeneratePdfOutput_MixedContentRoot_Issue32() throws Exception {
		Path contentRoot = REMOTE_SAMPLE_FORM_XDP.getParent().getParent(); // Get a content root two directories up
		Path template = REMOTE_SAMPLE_FORM_XDP.getParent().resolve(REMOTE_SAMPLE_FORM_XDP.getFileName()); // Get a template that contains a parent directory

		Document pdfResult =  underTest.generatePDFOutput()
				                       .setContentRoot(contentRoot)
                                       .executeOn(template, SimpleDocumentFactoryImpl.getFactory().create(LOCAL_SAMPLE_FORM_DATA_XML));
		
		TestUtils.validatePdfResult(pdfResult.getInlineData(), "testGeneratePdfOutput_MixedContentRoot_Issue32.pdf", false, false, false);
	}


	@Test
	@EnabledOnOs(OS.WINDOWS)
	@EnabledIf("targetIsLinux")
	@DisplayName("Test generatePdfOutput() Just Form with Linux Path.  From Windows Machine")
	void testGeneratePdfOutput_JustForm() throws Exception {
		URL formPath = new URL("file:/opt/adobe/ff_it_files/SampleForm.xdp");
		Document pdfResult =  underTest.generatePDFOutput()
									   .executeOn(formPath);
		
		Pdf resultPdf = Pdf.from(pdfResult.getInputStream());
		
//		TestUtils.validatePdfResult(pdfResult.getInlineData(), "testGeneratePdfOutput_JustForm.pdf", false, false, false);
	}

	@Test
	@EnabledOnOs(OS.WINDOWS)
	@EnabledIf("targetIsLinux")
	@DisplayName("Test generatePdfOutput() Just Form with Linux ContentRoot.  From Windows Machine")
	void testGeneratePdfOutput_JustFormContentRoot() throws Exception {
		Path contentPath = Paths.get("\\","opt", "adobe", "ff_it_files");
		Path formPath = Paths.get("SampleForm.xdp");
		Document pdfResult =  underTest.generatePDFOutput()
									   .setContentRoot(contentPath)
									   .executeOn(formPath);
		
		Pdf resultPdf = Pdf.from(pdfResult.getInputStream());
		
//		TestUtils.validatePdfResult(pdfResult.getInlineData(), "testGeneratePdfOutput_JustFormContentRoot.pdf", false, false, false);
	}
	
	boolean targetIsLinux() {
		return AemInstance.AEM_1.isLinux();
	}
}
