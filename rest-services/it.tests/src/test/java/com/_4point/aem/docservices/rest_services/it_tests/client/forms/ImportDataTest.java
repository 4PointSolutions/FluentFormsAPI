package com._4point.aem.docservices.rest_services.it_tests.client.forms;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.*;

import java.nio.file.Files;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com._4point.aem.docservices.rest_services.client.forms.RestServicesFormsServiceAdapter;
import com._4point.aem.docservices.rest_services.client.jersey.JerseyRestClient;
import com._4point.aem.docservices.rest_services.it_tests.AemInstance;
import com._4point.aem.docservices.rest_services.it_tests.ByteArrayString;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.forms.FormsService;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;
import com._4point.aem.fluentforms.impl.UsageContext;
import com._4point.aem.fluentforms.impl.forms.FormsServiceImpl;

@Tag("client-tests")
class ImportDataTest {

	private static final boolean SAVE_RESULTS = false;
	
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
	@DisplayName("Test importData() Happy Path.")
	void testImportData() throws Exception {

		Document data = SimpleDocumentFactoryImpl.INSTANCE.create(LOCAL_SAMPLE_FORM_DATA_XML);
		Document pdf = SimpleDocumentFactoryImpl.INSTANCE.create(LOCAL_SAMPLE_FORM_PDF);;
		Document pdfResult = underTest.importData(pdf, data);

		// Verify that all the results are correct.
		assertThat("Expected a PDF to be returned.", ByteArrayString.toString(pdfResult.getInlineData(), 8), containsString("%, P, D, F, -, 1, ., 7"));
		if (SAVE_RESULTS) {
			IOUtils.write(pdfResult.getInlineData(), Files.newOutputStream(ACTUAL_RESULTS_DIR.resolve("ImportDataClient_BytesResult.pdf")));
		}
	}

}
