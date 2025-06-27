package com._4point.aem.docservices.rest_services.it_tests.client.pdfUtility;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com._4point.aem.docservices.rest_services.client.jersey.JerseyRestClient;
import com._4point.aem.docservices.rest_services.client.pdfUtility.RestServicesPdfUtilityServiceAdapter;
import com._4point.aem.docservices.rest_services.it_tests.AemInstance;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.pdfUtility.PdfUtilityService;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;
import com._4point.aem.fluentforms.impl.pdfUtility.PdfUtilityServiceImpl;
import com.adobe.fd.pdfutility.services.client.PDFPropertiesOptionSpec;
import com.adobe.fd.pdfutility.services.client.RedactionOptionSpec;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;

import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.*;

@Tag("client-tests")
class ConvertPdfToXdpTest {

	private final Document DUMMY_DOC = SimpleDocumentFactoryImpl.getFactory().create(LOCAL_SAMPLE_FORM_PDF);
	private PdfUtilityService underTest;
	
	@BeforeAll
	static void setUpAll() throws Exception {
		AemInstance.AEM_1.prepareForTests();
	}

	@BeforeEach
	void setUp() throws Exception {
		RestServicesPdfUtilityServiceAdapter adapter = RestServicesPdfUtilityServiceAdapter.builder(JerseyRestClient.factory())
				.machineName(AemInstance.AEM_1.aemHost())
				.port(AemInstance.AEM_1.aemPort())
				.basicAuthentication(TEST_USER, TEST_USER_PASSWORD)
				.useSsl(false)
				.aemServerType(TEST_MACHINE_AEM_TYPE)
				.build();
		
		this.underTest = new PdfUtilityServiceImpl(adapter);
	}
	
	@Test
	void testConvertPdfToXdp() throws Exception {
		Document result = underTest.convertPDFtoXDP(SimpleDocumentFactoryImpl.getFactory().create(LOCAL_SAMPLE_FORM_PDF));
		
		assertNotNull(result);
		assertEquals("application/vnd.adobe.xdp+xml", result.getContentType());
		XML xml = new XMLDocument(result.getInputStream());	// parse it to make sure it's valid XML
	}

	@Test
	void testCloneDocument() {
		UnsupportedOperationException ex = assertThrows(UnsupportedOperationException.class, ()->underTest.clone(DUMMY_DOC));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, allOf(containsString("clone"), containsString("is not supported as a remote operation")));
	}
	
	@Test
	void testGetPDFProperties() {
		UnsupportedOperationException ex = assertThrows(UnsupportedOperationException.class, ()->underTest.getPDFProperties(DUMMY_DOC, new PDFPropertiesOptionSpec() ));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, allOf(containsString("getPDFProperties"), containsString("is not implemented yet")));
	}
	
	@Test
	void testMulticlone() {
		UnsupportedOperationException ex = assertThrows(UnsupportedOperationException.class, ()->underTest.multiclone(DUMMY_DOC, 2));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, allOf(containsString("multiclone"), containsString("is not supported as a remote operation")));
	}
	
	@Test
	void testRedact() {
		UnsupportedOperationException ex = assertThrows(UnsupportedOperationException.class, ()->underTest.redact(DUMMY_DOC, new RedactionOptionSpec()));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, allOf(containsString("redact"), containsString("is not implemented yet")));
	}
	
	@Test
	void testSanitize() {
		UnsupportedOperationException ex = assertThrows(UnsupportedOperationException.class, ()->underTest.sanitize(DUMMY_DOC));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, allOf(containsString("sanitize"), containsString("is not implemented yet")));
	}
}
