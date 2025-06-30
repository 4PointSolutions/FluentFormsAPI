package com._4point.aem.docservices.rest_services.it_tests.client.output;

import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.*;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import com._4point.aem.docservices.rest_services.client.jersey.JerseyRestClient;
import com._4point.aem.docservices.rest_services.client.output.RestServicesOutputServiceAdapter;
import com._4point.aem.docservices.rest_services.it_tests.AemInstance;
import com._4point.aem.docservices.rest_services.it_tests.TestUtils;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.api.output.OutputService;
import com._4point.aem.fluentforms.api.output.PrintConfig;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;
import com._4point.aem.fluentforms.impl.UsageContext;
import com._4point.aem.fluentforms.impl.output.OutputServiceImpl;
import com.adobe.fd.output.api.PaginationOverride;

@Tag("client-tests")
class GeneratePrintedOutputTest {

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
	@DisplayName("Test generatePrintedOutput() Just Form and Data.")
	void testgeneratePrintedOutput_JustFormAndData() throws Exception {
		Document pdfResult =  underTest.generatePrintedOutput()
				                    .setPrintConfig(PrintConfig.HP_PCL_5e)
									.executeOn(REMOTE_SAMPLE_FORM_XDP, SimpleDocumentFactoryImpl.getFactory().create(LOCAL_SAMPLE_FORM_DATA_XML));
		
		TestUtils.validatePrintedResult(pdfResult.getInlineData(), "generatePrintedOutput_JustFormAndData.pcl");
	}

	@Test
	@DisplayName("Test generatePrintedOutput() Just Form Document and Data.")
	void testgeneratePrintedOutput_JustFormDocAndData() throws Exception {
		Document pdfResult =  underTest.generatePrintedOutput()
									   .setPrintConfig(PrintConfig.Generic_PS_L3)
									   .executeOn(SimpleDocumentFactoryImpl.getFactory().create(LOCAL_SAMPLE_FORM_XDP), SimpleDocumentFactoryImpl.getFactory().create(LOCAL_SAMPLE_FORM_DATA_XML));
		
		TestUtils.validatePrintedResult(pdfResult.getInlineData(), "testgeneratePrintedOutput_JustFormDocAndData.ps");
	}

	@Test
	@DisplayName("Test generatePrintedOutput() CRX Form and Data.")
	void testgeneratePrintedOutput_CRXFormAndData() throws Exception {
		Document pdfResult =  underTest.generatePrintedOutput()
									.setPrintConfig(PrintConfig.ZPL600)
									.setContentRoot(PathOrUrl.from(CRX_CONTENT_ROOT))
									.executeOn(REMOTE_SAMPLE_FORM_XDP.getFileName(), SimpleDocumentFactoryImpl.getFactory().create(LOCAL_SAMPLE_FORM_DATA_XML));
		
		TestUtils.validatePrintedResult(pdfResult.getInlineData(), "generatePrintedOutput_CRXFormAndData.zpl");		
	}

	@Test
	@DisplayName("Test generatePrintedOutput() All Arguments.")
	void testgeneratePrintedOutput_AllArgs() throws Exception {
		Path contentRoot = REMOTE_SAMPLE_FORM_XDP.getParent();
//		Path debugDir = null;
		
		Document pdfResult =  underTest.generatePrintedOutput()
									.setContentRoot(contentRoot)
									.setCopies(2)
//									.setDebugDir(debugDir)
									.setLocale(Locale.CANADA_FRENCH)
									.setPaginationOverride(PaginationOverride.duplexShortEdge)
									.setPrintConfig(PrintConfig.HP_PCL_5e)
									.xci()
									   .embedPclFonts(true)
									   .done()
									// TODO: Since contentRoot is set, the full path should not be necessary. Try changing to just the filename.
									.executeOn(REMOTE_SAMPLE_FORM_XDP, SimpleDocumentFactoryImpl.getFactory().create(LOCAL_SAMPLE_FORM_DATA_XML));
		
		TestUtils.validatePrintedResult(pdfResult.getInlineData(), "generatePrintedOutput_AllArgs.pcl");		
	}
	
	@Disabled("Sample artwork fails when sent as Document for some reason, but works when sent as a reference.")
	@Test
	@DisplayName("Test generatePrintedOutput() Just Form Doc.  FluentFormsAPI Issue #15")
	void testgeneratePrintedOutput_JustFormDocIssue15() throws Exception {
		Document pdfResult =  underTest.generatePrintedOutput()
									   .setPrintConfig(PrintConfig.HP_PCL_5e)
									   .executeOn(SimpleDocumentFactoryImpl.getFactory().create(LOCAL_SAMPLE_ARTWORK_PDF));
		
		TestUtils.validatePrintedResult(pdfResult.getInlineData(), "generatePrintedOutput_JustFormDocIssue15.pcl");
	}

	@Test
	@DisplayName("Test generatePrintedOutput() Just Form.  FluentFormsAPI Issue #15")
	void testgeneratePrintedOutput_JustFormIssue15() throws Exception {
		Document pdfResult =  underTest.generatePrintedOutput()
				   					   .setPrintConfig(PrintConfig.Generic_PS_L3)
									   .executeOn(REMOTE_SAMPLE_ARTWORK_PDF);
		
		TestUtils.validatePrintedResult(pdfResult.getInlineData(), "testgeneratePrintedOutput_JustFormIssue15.ps");
	}

	@Test
	@DisplayName("Test generatePrintedOutput() for issue #32.")
	void testgeneratePrintedOutput_MixedContentRoot_Issue32() throws Exception {
		Path contentRoot = REMOTE_SAMPLE_FORM_XDP.getParent().getParent(); // Get a content root two directories up
		Path template = REMOTE_SAMPLE_FORM_XDP.getParent().resolve(REMOTE_SAMPLE_FORM_XDP.getFileName()); // Get a template that contains a parent directory

		Document pdfResult =  underTest.generatePrintedOutput()
									   .setPrintConfig(PrintConfig.HP_PCL_5e)
				                       .setContentRoot(contentRoot)
                                       .executeOn(template, SimpleDocumentFactoryImpl.getFactory().create(LOCAL_SAMPLE_FORM_DATA_XML));
		
		TestUtils.validatePrintedResult(pdfResult.getInlineData(), "testgeneratePrintedOutput_MixedContentRoot_Issue32.pcl");
	}


	@Test
	@EnabledOnOs(OS.WINDOWS)
	@EnabledIf("targetIsLinux")
	@DisplayName("Test generatePrintedOutput() Just Form with Linux Path.  From Windows Machine")
	void testgeneratePrintedOutput_JustForm() throws Exception {
		URL formPath = new URL("file:/opt/adobe/ff_it_files/SampleForm.xdp");
		Document pdfResult =  underTest.generatePrintedOutput()
				   					   .setPrintConfig(PrintConfig.HP_PCL_5e)
									   .executeOn(formPath);
		
		TestUtils.validatePrintedResult(pdfResult.getInlineData(), "testgeneratePrintedOutput_JustForm.pcl");
	}

	@Test
	@EnabledOnOs(OS.WINDOWS)
	@EnabledIf("targetIsLinux")
	@DisplayName("Test generatePrintedOutput() Just Form with Linux ContentRoot.  From Windows Machine")
	void testgeneratePrintedOutput_JustFormContentRoot() throws Exception {
		Path contentPath = Paths.get("\\","opt", "adobe", "ff_it_files");
		Path formPath = Paths.get("SampleForm.xdp");
		Document pdfResult =  underTest.generatePrintedOutput()
				   					   .setPrintConfig(PrintConfig.Generic_PS_L3)
									   .setContentRoot(contentPath)
									   .executeOn(formPath);
		
		TestUtils.validatePrintedResult(pdfResult.getInlineData(), "testgeneratePrintedOutput_JustFormContentRoot.ps");
	}
	
	boolean targetIsLinux() {
		return AemInstance.AEM_1.isLinux();
	}
}
