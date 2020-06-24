package com._4point.aem.docservices.rest_services.it_tests.client.forms;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.*;

import java.nio.file.Files;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com._4point.aem.docservices.rest_services.client.forms.RestServicesFormsServiceAdapter;
import com._4point.aem.docservices.rest_services.it_tests.ByteArrayString;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.forms.FormsService;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;
import com._4point.aem.fluentforms.impl.UsageContext;
import com._4point.aem.fluentforms.impl.forms.FormsServiceImpl;
import com.adobe.fd.forms.api.DataFormat;

class ExportDataTest {

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
	@DisplayName("Test exportData() Happy Path.")
	void testExportData() throws Exception {

		Document pdforxdp = SimpleDocumentFactoryImpl.INSTANCE.create(SAMPLE_FORM_PDF.toFile());
		DataFormat dataformat = com.adobe.fd.forms.api.DataFormat.XmlData;
		Document pdfResult = underTest.exportData(pdforxdp, dataformat);

		// Verify that all the results are correct.
		assertThat("Expected a xml to be returned.", ByteArrayString.toString(pdfResult.getInlineData(), 8), containsString("%, X, M, L, -, 1, ., 7"));
		if (SAVE_RESULTS) {
			IOUtils.write(pdfResult.getInlineData(), Files.newOutputStream(ACTUAL_RESULTS_DIR.resolve("ImportDataClient_BytesResult.xml")));
		}
	}

}
