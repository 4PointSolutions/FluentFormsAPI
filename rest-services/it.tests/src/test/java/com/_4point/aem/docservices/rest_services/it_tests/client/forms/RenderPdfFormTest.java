package com._4point.aem.docservices.rest_services.it_tests.client.forms;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.*;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.*;

import com._4point.aem.docservices.rest_services.client.forms.RestServicesFormsServiceAdapter;
import com._4point.aem.docservices.rest_services.it_tests.ByteArrayString;
import com._4point.aem.docservices.rest_services.it_tests.TestUtils;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.forms.FormsService;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;
import com._4point.aem.fluentforms.impl.forms.FormsServiceImpl;
import com.adobe.fd.forms.api.AcrobatVersion;
import com.adobe.fd.forms.api.CacheStrategy;

class RenderPdfFormTest {

	private FormsService underTest; 

	@BeforeEach
	void setUp() throws Exception {
		RestServicesFormsServiceAdapter adapter = RestServicesFormsServiceAdapter.builder()
				.machineName(TEST_MACHINE_NAME)
				.port(TEST_MACHINE_PORT)
				.basicAuthentication(TEST_USER, TEST_USER_PASSWORD)
				.useSsl(false)
				.build();

		underTest = new FormsServiceImpl(adapter);
	}

	@Test
	@DisplayName("Test renderPdfForm() Just Form and Data.")
	void testRenderPdfForm_JustFormAndData() throws Exception {
		Document pdfResult =  underTest.renderPDFForm()
									.executeOn(SAMPLE_FORM_XDP, SimpleDocumentFactoryImpl.getFactory().create(SAMPLE_FORM_DATA_XML.toFile()));
		
		assertThat("Expected a PDF to be returned.", ByteArrayString.toString(pdfResult.getInlineData(), 8), containsString("%, P, D, F, -, 1, ., 7"));
		IOUtils.write(pdfResult.getInlineData(), Files.newOutputStream(ACTUAL_RESULTS_DIR.resolve("RenderPdfFormClient_JustFormAndData.pdf")));
		
	}

	@Test
	@DisplayName("Test renderPdfForm() All Arguments.")
	void testRenderPdfForm_AllArgs() throws Exception {
		AcrobatVersion acrobatVersion = AcrobatVersion.Acrobat_10_1;
		CacheStrategy strategy = CacheStrategy.NONE;
		Path contentRoot = SAMPLE_FORM_XDP.getParent();
//		Path debugDir = null;
		Locale locale = Locale.CANADA_FRENCH;
//		String submitUrl = "";
		Document xci = SimpleDocumentFactoryImpl.INSTANCE.create(RESOURCES_DIR.resolve("pa.xci"));
		
		Document pdfResult =  underTest.renderPDFForm()
									.setAcrobatVersion(acrobatVersion)
									.setCacheStrategy(strategy)
									.setContentRoot(contentRoot)
//									.setDebugDir(debugDir)
									.setLocale(locale)
//									.setSubmitUrlString(submitUrl)
									.setTaggedPDF(true)
									.setXci(xci)
									.executeOn(SAMPLE_FORM_XDP, SimpleDocumentFactoryImpl.getFactory().create(SAMPLE_FORM_DATA_XML.toFile()));
		
		assertThat("Expected a PDF to be returned.", ByteArrayString.toString(pdfResult.getInlineData(), 8), containsString("%, P, D, F, -, 1, ., 7"));
		IOUtils.write(pdfResult.getInlineData(), Files.newOutputStream(ACTUAL_RESULTS_DIR.resolve("RenderPdfFormClient_AllArgs.pdf")));
		
	}


}
