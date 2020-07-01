package com._4point.aem.docservices.rest_services.it_tests.client.forms;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import javax.swing.text.html.parser.DocumentParser;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com._4point.aem.docservices.rest_services.client.forms.RestServicesFormsServiceAdapter;
import com._4point.aem.docservices.rest_services.it_tests.ByteArrayString;
import com._4point.aem.docservices.rest_services.it_tests.TestUtils;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.forms.FormsService;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;
import com._4point.aem.fluentforms.impl.UsageContext;
import com._4point.aem.fluentforms.impl.forms.FormsServiceImpl;
import com.adobe.fd.forms.api.DataFormat;

class ExportDataTest {

	private static final boolean SAVE_RESULTS = true;
	private static final String APPLICATION_XML = "application/xml";
	private FormsService underTest; 
	Document document;

	@BeforeEach
	void setUp() throws Exception {
		RestServicesFormsServiceAdapter adapter = RestServicesFormsServiceAdapter.builder()
														.machineName(TEST_MACHINE_NAME)
														.port(TEST_MACHINE_PORT)
														.basicAuthentication(TEST_USER, TEST_USER_PASSWORD)
														.useSsl(false)
														.build();

		underTest = new FormsServiceImpl(adapter, UsageContext.CLIENT_SIDE);
	}

	@Test
	@DisplayName("Test exportData() Happy Path.")
	void testExportData() throws Exception {
	
		Document pdforxdp = SimpleDocumentFactoryImpl.INSTANCE.create(SAMPLE_FORM_PDF.toFile());
		DataFormat dataformat = com.adobe.fd.forms.api.DataFormat.XmlData;
		Document pdfResult = underTest.exportData(pdforxdp, dataformat);
		
		 org.w3c.dom.Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(pdfResult.getInlineData())); 
		 XPathExpression xpath = XPathFactory.newInstance().newXPath().compile("/form1");
		 Node output1 = (Node)xpath.evaluate(document, XPathConstants.NODE);
		 output1.normalize();
		 String content = output1.getTextContent();
		 output1.normalize();
		 
		 System.out.println(content.contains("abcd"));
		 
		 assertEquals("form1",output1.getNodeName());
		 
		System.out.println(content.getBytes());
		
		assertEquals(APPLICATION_XML,pdfResult.getContentType());
		
		 
		if (SAVE_RESULTS) {
			IOUtils.write(pdfResult.getInlineData(), Files.newOutputStream(ACTUAL_RESULTS_DIR.resolve("ExportDataClient_BytesResult.xml")));
		}
     
		}
}
