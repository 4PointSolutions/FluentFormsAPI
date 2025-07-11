package com._4point.aem.docservices.rest_services.it_tests.client.af;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com._4point.aem.docservices.rest_services.client.af.AdaptiveFormsService;
import com._4point.aem.docservices.rest_services.client.jersey.JerseyRestClient;
import com._4point.aem.docservices.rest_services.it_tests.AemInstance;
import com._4point.aem.docservices.rest_services.it_tests.TestUtils;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;

@Tag("client-tests")
class RenderAdaptiveFormTest {

	private static final String SAMPLE_AF_NAME = "sample00002test";
	private static final String SAMPLE_JSON_AF_NAME = "sample-json-adaptive-form--1";	// TODO: Locate this adaptive form (or generate one) and then enable testRenderAdaptiveFormStringJsonDocument().

	private AdaptiveFormsService underTest;
	
	private static final boolean SAVE_RESULTS = false;
	
	@BeforeAll
	static void setUpAll() throws Exception {
		AemInstance.AEM_1.prepareForTests();
	}

	@BeforeEach
	void setUp() throws Exception {
		underTest = AdaptiveFormsService.builder(JerseyRestClient.factory())
				.machineName(AemInstance.AEM_1.aemHost())
				.port(AemInstance.AEM_1.aemPort())
				.basicAuthentication(TestUtils.TEST_USER, TestUtils.TEST_USER_PASSWORD)
				.useSsl(false)
				.aemServerType(TestUtils.TEST_MACHINE_AEM_TYPE)
				.build();
	}


	@Test
	void testRenderAdaptiveFormPathOrUrl() throws Exception {
		PathOrUrl template = PathOrUrl.from(SAMPLE_AF_NAME);
		
		Document result = underTest.renderAdaptiveForm(template);

		// Verify the result;
		byte[] resultBytes = IOUtils.toByteArray(result.getInputStream());

		if (SAVE_RESULTS) {
			IOUtils.write(resultBytes, Files.newOutputStream(TestUtils.ACTUAL_RESULTS_DIR.resolve("testRenderAdaptiveFormPathOrUrl_client_result.html")));
		}
		assertThat(new String(resultBytes, StandardCharsets.UTF_8), not(anyOf(containsString("Text Field1 Data"), containsString("Text Field2 Data"))));
	}

	@Test
	void testRenderAdaptiveFormString() throws Exception {
		String template = SAMPLE_AF_NAME;
		
		Document result = underTest.renderAdaptiveForm(template);

		// Verify the result;
		byte[] resultBytes = IOUtils.toByteArray(result.getInputStream());

		if (SAVE_RESULTS) {
			IOUtils.write(resultBytes, Files.newOutputStream(TestUtils.ACTUAL_RESULTS_DIR.resolve("testRenderAdaptiveFormString_client_result.html")));
		}
		assertThat(new String(resultBytes, StandardCharsets.UTF_8), not(anyOf(containsString("Text Field1 Data"), containsString("Text Field2 Data"))));
	}

	@Test
	void testRenderAdaptiveFormPath() throws Exception {
		Path template = Paths.get(SAMPLE_AF_NAME);
		
		Document result = underTest.renderAdaptiveForm(template);

		// Verify the result;
		byte[] resultBytes = IOUtils.toByteArray(result.getInputStream());

		if (SAVE_RESULTS) {
			IOUtils.write(resultBytes, Files.newOutputStream(TestUtils.ACTUAL_RESULTS_DIR.resolve("testRenderAdaptiveFormPath_client_result.html")));
		}
		assertThat(new String(resultBytes, StandardCharsets.UTF_8), not(anyOf(containsString("Text Field1 Data"), containsString("Text Field2 Data"))));
	}

	@Test
	void testRenderAdaptiveFormPathOrUrlDocument() throws Exception {
		PathOrUrl template = PathOrUrl.from(SAMPLE_AF_NAME);
		Document data = SimpleDocumentFactoryImpl.INSTANCE.create(TestUtils.LOCAL_SAMPLE_FORM_DATA_XML);
		data.setContentTypeIfEmpty("application/xml");
		
		Document result = underTest.renderAdaptiveForm(template, data);

		// Verify the result;
		byte[] resultBytes = IOUtils.toByteArray(result.getInputStream());

		if (SAVE_RESULTS) {
			IOUtils.write(resultBytes, Files.newOutputStream(TestUtils.ACTUAL_RESULTS_DIR.resolve("testRenderAdaptiveForm_FormRefAndData_result.html")));
		}
		assertThat(new String(resultBytes, StandardCharsets.UTF_8), allOf(containsString("Text Field1 Data"), containsString("Text Field2 Data")));
	}

	@Test
	void testRenderAdaptiveFormStringDocument() throws Exception {
		String template = SAMPLE_AF_NAME;
		Document data = SimpleDocumentFactoryImpl.INSTANCE.create(TestUtils.LOCAL_SAMPLE_FORM_DATA_XML);
		data.setContentTypeIfEmpty("application/xml");
		
		Document result = underTest.renderAdaptiveForm(template, data);

		// Verify the result;
		byte[] resultBytes = IOUtils.toByteArray(result.getInputStream());

		if (SAVE_RESULTS) {
			IOUtils.write(resultBytes, Files.newOutputStream(TestUtils.ACTUAL_RESULTS_DIR.resolve("testRenderAdaptiveForm_FormRefAndData_result.html")));
		}
		assertThat(new String(resultBytes, StandardCharsets.UTF_8), allOf(containsString("Text Field1 Data"), containsString("Text Field2 Data")));
	}

	@Test
	void testRenderAdaptiveFormPathDocument() throws Exception {
		Path template = Paths.get(SAMPLE_AF_NAME);
		Document data = SimpleDocumentFactoryImpl.INSTANCE.create(TestUtils.LOCAL_SAMPLE_FORM_DATA_XML);
		data.setContentTypeIfEmpty("application/xml");
		
		Document result = underTest.renderAdaptiveForm(template, data);

		// Verify the result;
		byte[] resultBytes = IOUtils.toByteArray(result.getInputStream());

		if (SAVE_RESULTS) {
			IOUtils.write(resultBytes, Files.newOutputStream(TestUtils.ACTUAL_RESULTS_DIR.resolve("testRenderAdaptiveForm_FormRefAndData_result.html")));
		}
		assertThat(new String(resultBytes, StandardCharsets.UTF_8), allOf(containsString("Text Field1 Data"), containsString("Text Field2 Data")));
	}
	
	@Disabled("This test is disabled because the sample JSON Adaptive Form is not available in the test environment.  It can be enabled when the sample JSON Adaptive Form is available.")
	@Test
	void testRenderAdaptiveFormStringJsonDocument() throws Exception {
		String template = SAMPLE_JSON_AF_NAME;
		Document data = SimpleDocumentFactoryImpl.INSTANCE.create(TestUtils.LOCAL_SAMPLE_FORM_DATA_JSON);
		data.setContentTypeIfEmpty("application/json");
		
		Document result = underTest.renderAdaptiveForm(template, data);

		// Verify the result;
		byte[] resultBytes = IOUtils.toByteArray(result.getInputStream());

		if (SAVE_RESULTS) {
			IOUtils.write(resultBytes, Files.newOutputStream(TestUtils.ACTUAL_RESULTS_DIR.resolve("testRenderAdaptiveForm_FormRefAndData_result.html")));
		}
		assertThat(new String(resultBytes, StandardCharsets.UTF_8), allOf(containsString("Text Field1 Data"), containsString("Text Field2 Data")));
	}
}
