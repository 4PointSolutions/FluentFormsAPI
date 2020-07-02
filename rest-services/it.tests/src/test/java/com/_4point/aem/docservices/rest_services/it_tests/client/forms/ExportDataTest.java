package com._4point.aem.docservices.rest_services.it_tests.client.forms;

import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.SAMPLE_FORM_PDF;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.TEST_MACHINE_NAME;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.TEST_MACHINE_PORT;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.TEST_USER;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.TEST_USER_PASSWORD;
import static org.junit.jupiter.api.Assertions.assertEquals;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Node;

import com._4point.aem.docservices.rest_services.client.forms.RestServicesFormsServiceAdapter;
import com._4point.aem.docservices.rest_services.it_tests.TestUtils;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.forms.FormsService;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;
import com._4point.aem.fluentforms.impl.UsageContext;
import com._4point.aem.fluentforms.impl.forms.FormsServiceImpl;
import com.adobe.fd.forms.api.DataFormat;

class ExportDataTest {

	
	private static final String APPLICATION_XML = "application/xml";
	private FormsService underTest; 
	private static String path = "src" +File.separator+ "test"+File.separator + "resources"+File.separator+ "ActualResults" ;
	Document document;
	private static final boolean SAVE_RESULTS = true;

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
		
		
		 org.w3c.dom.Document document1 = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(pdfResult.getInlineData())); 
		 XPathExpression xpath = XPathFactory.newInstance().newXPath().compile("/form1");
		 Node output1 = (Node)xpath.evaluate(document1, XPathConstants.NODE);
	     String content = output1.getTextContent();
	
	     assertEquals("form1",output1.getNodeName());
	  		
	  	 assertEquals("abcd", output1.getTextContent().trim());
		 
		
         if (SAVE_RESULTS) {
        	 TransformerFactory transformerFactory = TransformerFactory.newInstance();
             Transformer transformer = transformerFactory.newTransformer();
             DOMSource source = new DOMSource((Node) document1);
            
             StreamResult result = new StreamResult(new File(path +"/result.xml"));
             transformer.transform(source, result);
			}
		
         assertEquals(APPLICATION_XML,pdfResult.getContentType());
		
		}
}
