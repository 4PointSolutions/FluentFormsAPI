package com._4point.aem.docservices.rest_services.it_tests.client.forms;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;

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
import com._4point.aem.fluentforms.impl.forms.FormsServiceImpl;

class ImportDataTest {

	private static final String TEST_MACHINE_NAME = "localhost";
	private static final int TEST_MACHINE_PORT = 4502;
	
	private FormsService underTest; 

	@BeforeEach
	void setUp() throws Exception {
		RestServicesFormsServiceAdapter adapter = RestServicesFormsServiceAdapter.builder()
														.machineName(TEST_MACHINE_NAME)
														.port(TEST_MACHINE_PORT)
														.basicAuthentication("admin", "admin")
														.useSsl(false)
														.build();

		underTest = new FormsServiceImpl(adapter);
	}

	@Test
	@DisplayName("Test importData() Happy Path.")
	void testImportData() throws Exception {

		Document data = SimpleDocumentFactoryImpl.INSTANCE.create(TestUtils.SAMPLE_FORM_DATA_XML.toFile());
		Document pdf = SimpleDocumentFactoryImpl.INSTANCE.create(TestUtils.SAMPLE_FORM_PDF.toFile());;
		Document pdfResult = underTest.importData(pdf, data);

		// Verify that all the results are correct.
		assertThat("Expected a PDF to be returned.", ByteArrayString.toString(pdfResult.getInlineData(), 8), containsString("%, P, D, F, -, 1, ., 7"));
		IOUtils.write(pdfResult.getInlineData(), Files.newOutputStream(TestUtils.ACTUAL_RESULTS_DIR.resolve("ImportDataClient_BytesResult.pdf")));
	}

}
