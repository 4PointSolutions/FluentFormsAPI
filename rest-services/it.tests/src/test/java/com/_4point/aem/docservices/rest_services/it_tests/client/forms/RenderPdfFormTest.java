package com._4point.aem.docservices.rest_services.it_tests.client.forms;

import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.*;

import java.nio.file.Path;
import java.util.Locale;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com._4point.aem.docservices.rest_services.client.forms.RestServicesFormsServiceAdapter;
import com._4point.aem.docservices.rest_services.client.jersey.JerseyRestClient;
import com._4point.aem.docservices.rest_services.it_tests.AemInstance;
import com._4point.aem.docservices.rest_services.it_tests.TestUtils;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.api.forms.FormsService;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;
import com._4point.aem.fluentforms.impl.UsageContext;
import com._4point.aem.fluentforms.impl.forms.FormsServiceImpl;
import com.adobe.fd.forms.api.AcrobatVersion;
import com.adobe.fd.forms.api.CacheStrategy;

@Tag("client-tests")
class RenderPdfFormTest {

	private static final String CRX_CONTENT_ROOT = "crx:/content/dam/formsanddocuments/sample-forms";

	private FormsService underTest; 

	@BeforeAll
	static void setUpAll() throws Exception {
		AemInstance.AEM_1.prepareForTests();
	}

	@BeforeEach
	void setUp() throws Exception {
		RestServicesFormsServiceAdapter adapter = RestServicesFormsServiceAdapter.builder(JerseyRestClient.factory())
				.machineName(AemInstance.AEM_1.aemHost())
				.port(AemInstance.AEM_1.aemPort())
				.basicAuthentication(TEST_USER, TEST_USER_PASSWORD)
				.useSsl(false)
				.aemServerType(TEST_MACHINE_AEM_TYPE)
				.build();

		underTest = new FormsServiceImpl(adapter, UsageContext.CLIENT_SIDE);
	}

	@Test
	@DisplayName("Test renderPdfForm() Just Form and Data.")
	void testRenderPdfForm_JustFormAndData() throws Exception {
		Document pdfResult =  underTest.renderPDFForm()
									.executeOn(REMOTE_SAMPLE_FORM_XDP, SimpleDocumentFactoryImpl.getFactory().create(LOCAL_SAMPLE_FORM_DATA_XML));
		
		TestUtils.validatePdfResult(pdfResult.getInlineData(), "RenderPdfFormClient_JustFormAndData.pdf", true, true, false);
	}

	@Test
	@DisplayName("Test renderPdfForm() Just Form Document and Data.")
	void testRenderPdfForm_JustFormDocAndData() throws Exception {
		Document pdfResult =  underTest.renderPDFForm()
									.executeOn(SimpleDocumentFactoryImpl.getFactory().create(LOCAL_SAMPLE_FORM_XDP), SimpleDocumentFactoryImpl.getFactory().create(LOCAL_SAMPLE_FORM_DATA_XML));
		
		TestUtils.validatePdfResult(pdfResult.getInlineData(), "RenderPdfFormClient_JustFormAndData.pdf", true, true, false);
	}

	@Test
	@DisplayName("Test renderPdfForm() CRX Form and Data.")
	void testRenderPdfForm_CRXFormAndData() throws Exception {
		Document pdfResult =  underTest.renderPDFForm()
									.setContentRoot(PathOrUrl.from(CRX_CONTENT_ROOT))
									.executeOn(REMOTE_SAMPLE_FORM_XDP.getFileName(), SimpleDocumentFactoryImpl.getFactory().create(LOCAL_SAMPLE_FORM_DATA_XML));
		
		TestUtils.validatePdfResult(pdfResult.getInlineData(), "RenderPdfFormClient_CRXFormAndData.pdf", true, true, false);
	}

	@Test
	@DisplayName("Test renderPdfForm() All Arguments.")
	void testRenderPdfForm_AllArgs() throws Exception {
		AcrobatVersion acrobatVersion = AcrobatVersion.Acrobat_10_1;
		CacheStrategy strategy = CacheStrategy.NONE;
		Path contentRoot = REMOTE_SAMPLE_FORM_XDP.getParent();
//		Path debugDir = null;
		Locale locale = Locale.CANADA_FRENCH;
		String submitUrl = "http://example.com/";
		Document xci = SimpleDocumentFactoryImpl.INSTANCE.create(RESOURCES_DIR.resolve("pa.xci"));
		
		Document pdfResult =  underTest.renderPDFForm()
									.setAcrobatVersion(acrobatVersion)
									.setCacheStrategy(strategy)
									.setContentRoot(contentRoot)
//									.setDebugDir(debugDir)
									.setEmbedFonts(true)
									.setLocale(locale)
									.setSubmitUrlString(submitUrl)
									.setTaggedPDF(true)
									.setXci(xci)
									.executeOn(REMOTE_SAMPLE_FORM_XDP, SimpleDocumentFactoryImpl.getFactory().create(LOCAL_SAMPLE_FORM_DATA_XML));
		
		TestUtils.validatePdfResult(pdfResult.getInlineData(), "RenderPdfFormClient_AllArgs.pdf", false, true, false);
	}


}
