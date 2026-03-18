package com._4point.aem.docservices.rest_services.it_tests.client.convertPdf;

import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.TEST_MACHINE_AEM_TYPE;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.TEST_USER;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.TEST_USER_PASSWORD;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com._4point.aem.docservices.rest_services.client.convertPdf.RestServicesConvertPdfServiceAdapter;
import com._4point.aem.docservices.rest_services.client.jersey.JerseyRestClient;
import com._4point.aem.docservices.rest_services.it_tests.AemInstance;
import com._4point.aem.docservices.rest_services.it_tests.TestUtils;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.impl.convertPdf.ConvertPdfServiceImpl;
import com.adobe.fd.cpdf.api.enumeration.ImageConvertFormat;

@Disabled("This test is currently disabled because the ToImage service is not working correctly. It is work in progress.")
@Tag("client-tests")
public class ToImageTest {

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
	@DisplayName("Test toImage() Happy Path.")
	void testToImage() throws Exception {
		List<Document> result = underTest.toImage()
										 .setImageConvertFormat(ImageConvertFormat.PNG)
										 .executeOn(Files.readAllBytes(TestUtils.LOCAL_SAMPLE_FORM_PDF));
		
		if (SAVE_RESULTS) {
			for (int i = 0; i < result.size(); i++) {
				Document imageDoc = result.get(i);
				byte[] imageBytes = imageDoc.getInputStream().readAllBytes();
				String outputFileName = String.format("ToImageResult_Page%d.png", i + 1);
				Path outputPath = TestUtils.ACTUAL_RESULTS_DIR.resolve(outputFileName);
				Files.write(outputPath, imageBytes);
				System.out.println("Saved result to " + outputPath.toAbsolutePath());
			}
		}
	}
}
