package com._4point.aem.docservices.rest_services.it_tests.client.html5;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.net.URI;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com._4point.aem.docservices.rest_services.client.html5.Html5FormsService;
import com._4point.aem.docservices.rest_services.client.jersey.JerseyRestClient;
import com._4point.aem.docservices.rest_services.it_tests.AemInstance;
import com._4point.aem.docservices.rest_services.it_tests.HtmlFormDocument;
import com._4point.aem.docservices.rest_services.it_tests.TestUtils;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;

@Tag("client-tests")
class RenderHtml5FormTest {
	private static final String CRX_CONTENT_ROOT = "crx:/content/dam/formsanddocuments/sample-forms/";
	
	
	private Html5FormsService underTest; 

	@BeforeAll
	static void setUpAll() throws Exception {
		AemInstance.AEM_1.prepareForTests();
	}

	@BeforeEach
	void setUp() throws Exception {
		underTest = Html5FormsService.builder(JerseyRestClient.factory())
				.machineName(AemInstance.AEM_1.aemHost())
				.port(AemInstance.AEM_1.aemPort())
				.basicAuthentication(TestUtils.TEST_USER, TestUtils.TEST_USER_PASSWORD)
				.useSsl(false)
				.aemServerType(TestUtils.TEST_MACHINE_AEM_TYPE)
				.build();

	}


	@Test
	void testRenderHtml5FormPathOrUrl() throws Exception {
		PathOrUrl template = PathOrUrl.from(CRX_CONTENT_ROOT + TestUtils.REMOTE_SAMPLE_FORM_XDP.getFileName().toString());
		
		Document result = underTest.renderHtml5Form(template);
		
		// Verify the result;
		byte[] resultBytes = IOUtils.toByteArray(result.getInputStream());
		HtmlFormDocument htmlDoc = HtmlFormDocument.create(resultBytes, new URI("http://" + TestUtils.TEST_MACHINE_NAME + ":" + TestUtils.TEST_MACHINE_PORT));
		assertEquals("LC Forms", htmlDoc.getTitle());

		// Make sure the data wasn't populated.
		String html = new String(resultBytes, StandardCharsets.UTF_8);
		// Does not contain field data.
		assertThat(html, not(anyOf(containsString("Text Field1 Data"), containsString("Text Field2 Data"))));
	
	}

	@Test
	void testRenderHtml5FormPathOrUrlDocument() throws Exception {
		PathOrUrl template = PathOrUrl.from(CRX_CONTENT_ROOT + TestUtils.REMOTE_SAMPLE_FORM_XDP.getFileName().toString());
		Document data = SimpleDocumentFactoryImpl.INSTANCE.create(TestUtils.LOCAL_SAMPLE_FORM_DATA_XML);
		data.setContentTypeIfEmpty("application/xml");
		
		Document result = underTest.renderHtml5Form(template, data);

		// Verify the result;
		byte[] resultBytes = IOUtils.toByteArray(result.getInputStream());
		HtmlFormDocument htmlDoc = HtmlFormDocument.create(resultBytes, new URI("http://" + TestUtils.TEST_MACHINE_NAME + ":" + TestUtils.TEST_MACHINE_PORT));
		assertEquals("LC Forms", htmlDoc.getTitle());

		// Make sure the data was populated.
		String html = new String(resultBytes, StandardCharsets.UTF_8);
		// Contains field data.
		assertThat(html, allOf(containsString("Text Field1 Data"), containsString("Text Field2 Data")));
	}

}
