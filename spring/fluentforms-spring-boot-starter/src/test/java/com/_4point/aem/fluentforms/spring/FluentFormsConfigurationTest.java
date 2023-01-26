package com._4point.aem.fluentforms.spring;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

import com._4point.aem.docservices.rest_services.client.af.AdaptiveFormsService;
import com._4point.aem.docservices.rest_services.client.html5.Html5FormsService;
import com._4point.aem.fluentforms.api.assembler.AssemblerService;
import com._4point.aem.fluentforms.api.docassurance.DocAssuranceService;
import com._4point.aem.fluentforms.api.forms.FormsService;
import com._4point.aem.fluentforms.api.generatePDF.GeneratePDFService;
import com._4point.aem.fluentforms.api.output.OutputService;
import com._4point.aem.fluentforms.api.pdfUtility.PdfUtilityService;
import com._4point.aem.fluentforms.spring.AemConfigurationTest.TestApplication;

@SpringBootTest(classes = {com._4point.aem.fluentforms.spring.FluentFormsConfigurationTest.TestApplication.class, FluentFormsConfiguration.class}, 
				properties = {
						"fluentforms.aem.servername=localhos", 
						"fluentforms.aem.port=4502", 
						"fluentforms.aem.user=admin",		 
						"fluentforms.aem.password=admin)",
						})
class FluentFormsConfigurationTest {
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testAdaptiveFormsService(@Autowired AdaptiveFormsService service) {
		assertNotNull(service);
	}

	@Test
	void testAssemblerService(@Autowired AssemblerService service) {
		assertNotNull(service);
	}

	@Test
	void testDocAssuranceService(@Autowired DocAssuranceService service) {
		assertNotNull(service);
	}

	@Test
	void testFormsService(@Autowired FormsService service) {
		assertNotNull(service);
	}

	@Test
	void testGeneratePDFService(@Autowired GeneratePDFService service) {
		assertNotNull(service);
	}

	@Test
	void testHtml5FormsService(@Autowired Html5FormsService service) {
		assertNotNull(service);
	}

	@Test
	void testOutputService(@Autowired OutputService service) {
		assertNotNull(service);
	}

	@Test
	void testPdfUtilityService(@Autowired PdfUtilityService service) {
		assertNotNull(service);
	}

	@SpringBootApplication
	@EnableConfigurationProperties({AemConfiguration.class})
	public static class TestApplication {
		public static void main(String[] args) {
			SpringApplication.run(TestApplication.class, args);
		}

	}
}
