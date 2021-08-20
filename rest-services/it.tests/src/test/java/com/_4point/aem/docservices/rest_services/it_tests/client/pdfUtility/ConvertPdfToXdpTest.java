package com._4point.aem.docservices.rest_services.it_tests.client.pdfUtility;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com._4point.aem.docservices.rest_services.client.pdfUtility.RestServicesPdfUtilityServiceAdapter;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.pdfUtility.PdfUtilityService;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;
import com._4point.aem.fluentforms.impl.pdfUtility.PdfUtilityServiceImpl;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;

import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.*;

class ConvertPdfToXdpTest {

	private PdfUtilityService underTest;
	
	@BeforeEach
	void setUp() throws Exception {
		RestServicesPdfUtilityServiceAdapter adapter = RestServicesPdfUtilityServiceAdapter.builder()
				.machineName(TEST_MACHINE_NAME)
				.port(TEST_MACHINE_PORT)
				.basicAuthentication(TEST_USER, TEST_USER_PASSWORD)
				.useSsl(false)
				.aemServerType(TEST_MACHINE_AEM_TYPE)
				.build();
		
		this.underTest = new PdfUtilityServiceImpl(adapter);
	}
	
	@Test
	void testConvertPdfToXdp() throws Exception {
		Document result = underTest.convertPDFtoXDP(SimpleDocumentFactoryImpl.getFactory().create(SAMPLE_FORM_PDF));
		
		assertNotNull(result);
		assertEquals("application/vnd.adobe.xdp+xml", result.getContentType());
		XML xml = new XMLDocument(result.getInputStream());	// parse it to make sure it's valid XML
	}

}
