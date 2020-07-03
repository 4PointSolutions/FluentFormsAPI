package com._4point.aem.docservices.rest_services.it_tests.client.html5;

import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.SAMPLE_FORM_DATA_XML;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.SAMPLE_FORM_PDF;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com._4point.aem.docservices.rest_services.client.html5.Html5FormsService;
import com._4point.aem.docservices.rest_services.client.html5.Html5FormsService.Html5FormsServiceException;
import com._4point.aem.docservices.rest_services.it_tests.TestUtils;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.PathOrUrl;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;

class RenderHtml5FormTest {
	private static final String CRX_CONTENT_ROOT = "crx:/content/dam/formsanddocuments/sample-forms";
	
	
	private Html5FormsService underTest; 

	@BeforeEach
	void setUp() throws Exception {
		underTest = Html5FormsService.builder()
				.machineName(TestUtils.TEST_MACHINE_NAME)
				.port(TestUtils.TEST_MACHINE_PORT)
				.basicAuthentication(TestUtils.TEST_USER, TestUtils.TEST_USER_PASSWORD)
				.useSsl(false)
				.build();

	}


	@Test
	void testRenderHtml5FormPathOrUrl() throws Exception {
		PathOrUrl template = PathOrUrl.from(CRX_CONTENT_ROOT + TestUtils.SAMPLE_FORM_XDP.getFileName().toString());
		
		Document result = underTest.renderHtml5Form(template);
		
		// Do something to verify the result;
	}

	@Test
	void testRenderHtml5FormPathOrUrlDocument() throws Exception {
		PathOrUrl template = PathOrUrl.from(TestUtils.SAMPLE_FORM_XDP.getFileName());
		Document data = SimpleDocumentFactoryImpl.INSTANCE.create(SAMPLE_FORM_DATA_XML.toFile());
		
		Document result = underTest.renderHtml5Form(template, data);
		
		// Do something to verify the result;
	}

}
