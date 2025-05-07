package com._4point.aem.docservices.rest_services.it_tests.client.forms;

import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Node;

import com._4point.aem.docservices.rest_services.client.forms.RestServicesFormsServiceAdapter;
import com._4point.aem.docservices.rest_services.client.jersey.JerseyRestClient;
import com._4point.aem.docservices.rest_services.it_tests.AemInstance;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.forms.FormsService;
import com._4point.aem.fluentforms.api.forms.FormsService.FormsServiceException;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;
import com._4point.aem.fluentforms.impl.UsageContext;
import com._4point.aem.fluentforms.impl.forms.FormsServiceImpl;
import com._4point.testing.matchers.javalang.ExceptionMatchers;
import com.adobe.fd.forms.api.DataFormat;

@Tag("client-tests")
class ExportDataTest {

	private static final String APPLICATION_XML = "application/xml";
	private FormsService underTest;
	private static String path = "src" + File.separator + "test" + File.separator + "resources" + File.separator
			+ "ActualResults";
	Document document;
	private static final boolean SAVE_RESULTS = false;

	@BeforeAll
	static void setUpAll() throws Exception {
		AemInstance.AEM_1.prepareForTests();
	}

	@BeforeEach
	void setUp() throws Exception {
		RestServicesFormsServiceAdapter adapter = RestServicesFormsServiceAdapter.builder(JerseyRestClient.factory())
				.machineName(AemInstance.AEM_1.aemHost())
				.port(AemInstance.AEM_1.aemPort())
				.basicAuthentication(TEST_USER, TEST_USER_PASSWORD)
				.useSsl(false)
				.aemServerType(TEST_MACHINE_AEM_TYPE)
				.build();

		underTest = new FormsServiceImpl(adapter, UsageContext.CLIENT_SIDE);
	}

	@Test
	@DisplayName("Test exportData() Happy Path.")
	void testExportData() throws Exception {

		Document pdforxdp = SimpleDocumentFactoryImpl.INSTANCE.create(SAMPLE_FORM_WITH_DATA_PDF.toFile());
		DataFormat dataformat = com.adobe.fd.forms.api.DataFormat.XmlData;
		Optional<Document> pdfResult = underTest.exportData(pdforxdp, dataformat);

		org.w3c.dom.Document document1 = DocumentBuilderFactory.newInstance().newDocumentBuilder()
															   .parse(new ByteArrayInputStream(pdfResult.get().getInlineData()));
		XPathExpression xpath = XPathFactory.newInstance().newXPath().compile("/form1");
		Node output1 = (Node) xpath.evaluate(document1, XPathConstants.NODE);

		assertEquals("form1", output1.getNodeName());
		assertEquals("abcd", output1.getTextContent().trim());

		if (SAVE_RESULTS) {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource((Node) document1);

			StreamResult result = new StreamResult(new File(path + "/testExportData_result.xml"));
			transformer.transform(source, result);
		}

		assertEquals(APPLICATION_XML, pdfResult.get().getContentType());

	}

	@Test
	@DisplayName("Test exportData() Non-interactive PDF.")
	void testExportDataNonInteractive() throws Exception {

		Document pdforxdp = SimpleDocumentFactoryImpl.INSTANCE.create(SAMPLE_FORM_WITHOUT_DATA_PDF);
		DataFormat dataformat = com.adobe.fd.forms.api.DataFormat.XmlData;
		FormsServiceException ex = assertThrows(FormsServiceException.class, ()->underTest.exportData(pdforxdp, dataformat));
		
		assertThat(ex, ExceptionMatchers.exceptionMsgContainsAll("Error while POSTing to server", "/services/FormsService/ExportData"));
		ex.printStackTrace();
	}

	@Test
	@DisplayName("Test exportData() NoData PDF.")
	void testExportDataNoData() throws Exception {

		Document pdforxdp = SimpleDocumentFactoryImpl.INSTANCE.create(SAMPLE_FORM_PDF);
		DataFormat dataformat = com.adobe.fd.forms.api.DataFormat.XmlData;
		Optional<Document> pdfResult = underTest.exportData(pdforxdp, dataformat);

		assertFalse(pdfResult.isPresent(), "Expected no Document to be returned.");
	}

}
