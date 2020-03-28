package com._4point.aem.docservices.rest_services.it_tests.client.forms;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.*;

import java.nio.file.Files;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com._4point.aem.docservices.rest_services.it_tests.TestUtils;
import com._4point.aem.docservices.rest_services.client.forms.RestServicesFormsServiceAdapter;
import com._4point.aem.docservices.rest_services.it_tests.ByteArrayString;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.forms.FormsService;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;
import com._4point.aem.fluentforms.impl.UsageContext;
import com._4point.aem.fluentforms.impl.forms.FormsServiceImpl;

class ImportDataTest {

	private static final boolean SAVE_RESULTS = false;
	
	private FormsService underTest; 

	@BeforeEach
	void setUp() throws Exception {
		RestServicesFormsServiceAdapter adapter = RestServicesFormsServiceAdapter.builder()
														.machineName(TEST_MACHINE_NAME)
														.port(TEST_MACHINE_PORT)
														.basicAuthentication(TEST_USER, TEST_USER_PASSWORD)
														.useSsl(false)
														.build();

		underTest = new FormsServiceImpl(adapter, UsageContext.CLIENT_SIDE);
	}

	@Test
	@DisplayName("Test importData() Happy Path.")
	void testImportData() throws Exception {

		Document data = SimpleDocumentFactoryImpl.INSTANCE.create(SAMPLE_FORM_DATA_XML.toFile());
		Document pdf = SimpleDocumentFactoryImpl.INSTANCE.create(SAMPLE_FORM_PDF.toFile());;
		Document pdfResult = underTest.importData(pdf, data);

		// Verify that all the results are correct.
		assertThat("Expected a PDF to be returned.", ByteArrayString.toString(pdfResult.getInlineData(), 8), containsString("%, P, D, F, -, 1, ., 7"));
		if (SAVE_RESULTS) {
			IOUtils.write(pdfResult.getInlineData(), Files.newOutputStream(ACTUAL_RESULTS_DIR.resolve("ImportDataClient_BytesResult.pdf")));
		}
	}

}
