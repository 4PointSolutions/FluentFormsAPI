package com._4point.aem.docservices.rest_services.it_tests.client.convertPdf;

import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.TEST_MACHINE_AEM_TYPE;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.TEST_USER;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.TEST_USER_PASSWORD;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com._4point.aem.docservices.rest_services.client.convertPdf.RestServicesConvertPdfServiceAdapter;
import com._4point.aem.docservices.rest_services.client.jersey.JerseyRestClient;
import com._4point.aem.docservices.rest_services.it_tests.AemInstance;
import com._4point.aem.docservices.rest_services.it_tests.ByteArrayString;
import com._4point.aem.docservices.rest_services.it_tests.TestUtils;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.impl.convertPdf.ConvertPdfServiceImpl;

@Tag("client-tests")
public class ToPSTest {

	private static final boolean SAVE_RESULTS = true;

	private ConvertPdfServiceImpl underTest;

	@BeforeAll
	static void setUpAll() throws Exception {
		AemInstance.AEM_1.prepareForTests();
	}

	@BeforeEach
	void setUp() throws Exception {
		RestServicesConvertPdfServiceAdapter adapter = RestServicesConvertPdfServiceAdapter.builder(JerseyRestClient.factory())
				.machineName(AemInstance.AEM_1.aemHost())
				.port(AemInstance.AEM_1.aemPort())
				.basicAuthentication(TEST_USER, TEST_USER_PASSWORD)
				.useSsl(false)
				.aemServerType(TEST_MACHINE_AEM_TYPE)
				.build();

		underTest = new ConvertPdfServiceImpl(adapter);
	}

	@Test
	@DisplayName("Test toPS() Happy Path.")
	void testToPS() throws Exception {

		Document result = underTest.toPS()
				 				   .executeOn(Files.readAllBytes(TestUtils.LOCAL_SAMPLE_FORM_PDF));
		
		byte[] resultBytes = result.getInputStream().readAllBytes();
		
		if (SAVE_RESULTS) {
			Path outputPath = TestUtils.ACTUAL_RESULTS_DIR.resolve("ToPSResult.ps");
			Files.write(outputPath, resultBytes);
			System.out.println("Saved result to " + outputPath.toAbsolutePath());
		}
		
		assertThat("Expected PostScript to be returned.", ByteArrayString.toString(resultBytes, 10), containsString("%, !, P, S, -, A, d, o, b, e"));
	}
}
