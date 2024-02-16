package com._4point.aem.fluentforms.spring;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

import com._4point.aem.docservices.rest_services.client.af.AdaptiveFormsService;
import com._4point.aem.docservices.rest_services.client.html5.Html5FormsService;
import com._4point.aem.fluentforms.api.DocumentFactory;
import com._4point.aem.fluentforms.api.assembler.AssemblerService;
import com._4point.aem.fluentforms.api.convertPdf.ConvertPdfService;
import com._4point.aem.fluentforms.api.docassurance.DocAssuranceService;
import com._4point.aem.fluentforms.api.forms.FormsService;
import com._4point.aem.fluentforms.api.generatePDF.GeneratePDFService;
import com._4point.aem.fluentforms.api.output.OutputService;
import com._4point.aem.fluentforms.api.pdfUtility.PdfUtilityService;

@SpringBootTest(classes = {com._4point.aem.fluentforms.spring.FluentFormsAutoConfigurationTest.TestApplication.class, FluentFormsAutoConfiguration.class}, 
				properties = {
						"fluentforms.aem.servername=localhost", 
						"fluentforms.aem.port=4502", 
						"fluentforms.aem.user=admin",		 
						"fluentforms.aem.password=admin)",
						})
class FluentFormsAutoConfigurationTest {
	
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

	@Test
	void testConvertPdfService(@Autowired ConvertPdfService service) {
		assertNotNull(service);
	}

	@Test
	void testDocumentFactory(@Autowired DocumentFactory factory) {
		assertNotNull(factory);
		assertNotNull(factory.create(new byte[6]));
	}
	
	@Test
	void testAfInputStreamFilterFactory(@Autowired Function<InputStream, InputStream> afInputStreamFilter) throws Exception {
		final String INPUT_STRING = "/etc.clientlibs/foobar";
		final String EXPECTED_RESULT_STRING = "/aem/etc.clientlibs/foobar";
		
		assertNotNull(afInputStreamFilter);
		assertEquals(EXPECTED_RESULT_STRING, applyStreamFilter(INPUT_STRING, afInputStreamFilter)); 
	}
	
	@SpringBootApplication
	@EnableConfigurationProperties({AemConfiguration.class})
	public static class TestApplication {
		public static void main(String[] args) {
			SpringApplication.run(TestApplication.class, args);
		}
	}
	
	
	@SpringBootTest(classes = {com._4point.aem.fluentforms.spring.FluentFormsAutoConfigurationTest.TestApplication.class, FluentFormsAutoConfiguration.class}, 
			properties = {
					"fluentforms.aem.servername=localhost", 
					"fluentforms.aem.port=4502", 
					"fluentforms.aem.user=admin",		 
					"fluentforms.aem.password=admin)",
					"fluentforms.rproxy.aemPrefix=/app_prefix",
					"fluentforms.rproxy.clientPrefix=/client_prefix",
					})
	public static class AfStreamFilterTest {
		
		@Test
		void testAfInputStreamFilterFactory(@Autowired Function<InputStream, InputStream> afInputStreamFilter) throws Exception {
			final String INPUT_STRING = "/app_prefix/etc.clientlibs/foobar";
			final String EXPECTED_RESULT_STRING = "/client_prefix/aem/app_prefix/etc.clientlibs/foobar";
			
			assertNotNull(afInputStreamFilter);
			assertEquals(EXPECTED_RESULT_STRING, applyStreamFilter(INPUT_STRING, afInputStreamFilter)); 
		}
		
	}
	
	
	private static String applyStreamFilter(String inputString, Function<InputStream, InputStream> afInputStreamFilter) {
		try (InputStream is = afInputStreamFilter.apply(stringToInputStream(inputString))) {
			return inputStreamToString(is);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private static InputStream stringToInputStream(String inputString) {
		return new ByteArrayInputStream(inputString.getBytes(StandardCharsets.UTF_8));
	}
	
	private static String inputStreamToString(InputStream inputStream) throws IOException {
		String result = new BufferedReader(
			      new InputStreamReader(inputStream, StandardCharsets.UTF_8))
			        .lines()
			        .collect(Collectors.joining("\n"));
		return result;
	}
}
