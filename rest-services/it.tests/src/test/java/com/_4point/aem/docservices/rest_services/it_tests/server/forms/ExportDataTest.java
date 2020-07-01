
package com._4point.aem.docservices.rest_services.it_tests.server.forms;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;
import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com._4point.aem.docservices.rest_services.it_tests.ByteArrayString;
import com._4point.aem.docservices.rest_services.it_tests.TestUtils;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.impl.SimpleDocumentFactoryImpl;
import com.adobe.fd.forms.api.DataFormat;

public class ExportDataTest{


	private static final String EXPORT_DATA_URL = "http://" + TEST_MACHINE_NAME + ":" + TEST_MACHINE_PORT_STR + "/services/FormsService/ExportData";
	private static final MediaType APPLICATION_PDF = new MediaType("application", "pdf");
	protected static final MediaType APPLICATION_XDP = new MediaType("application", "vnd.adobe.xdp+xml");
	private static final MediaType APPLICATION_XML = new MediaType("application", "xml");
	private static final boolean SAVE_RESULTS = true;
	
	private WebTarget target;

	@BeforeEach
	void setUp() throws Exception {
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(TEST_USER, TEST_USER_PASSWORD);	// default AEM passwords
		target = ClientBuilder.newClient()
							  .register(feature)
							  .register(MultiPartFeature.class)
							  .target(EXPORT_DATA_URL);
	}
	 
	@Test
	void testExportData_Bytes() throws Exception {
	
		try (@SuppressWarnings("resource")
		final FormDataMultiPart multipart = new FormDataMultiPart()
        .field("pdforxdp", TestUtils.SAMPLE_FORM_PDF.toFile() ,APPLICATION_PDF)
        .field("dataformat", DataFormat.XmlData.name())) {
			
			Response result = target.request()
									  .accept(MediaType.APPLICATION_XML_TYPE)
									  .post(Entity.entity(multipart, multipart.getMediaType()));
			
			assertTrue(result.hasEntity(), "Expected the response to have an entity.");
			
			assertEquals(Response.Status.OK.getStatusCode(), result.getStatus(), () -> "Expected response to be 'OK', entity='" + TestUtils.readEntityToString(result) + "'.");
			
		
			byte[] resultBytes = IOUtils.toByteArray((InputStream)result.getEntity());
			
			org.w3c.dom.Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(resultBytes)); 
			 XPathExpression xpath = XPathFactory.newInstance().newXPath().compile("/form1");
			 Node output1 = (Node)xpath.evaluate(document, XPathConstants.NODE);
			 output1.normalize();
			 String content = output1.getTextContent();
			
			 System.out.println(content.contains("abcd"));
			 
			 assertEquals("form1",output1.getNodeName());
		 
		 
			if (SAVE_RESULTS) {
				IOUtils.write(resultBytes, Files.newOutputStream(TestUtils.ACTUAL_RESULTS_DIR.resolve("ExportedData1.xml")));
			}
			assertEquals(APPLICATION_XML, MediaType.valueOf(result.getHeaderString(HttpHeaders.CONTENT_TYPE)));
		
		}
		try (@SuppressWarnings("resource")
		final FormDataMultiPart multipart = new FormDataMultiPart()
        .field("pdforxdp", TestUtils.SAMPLE_FORM_XDP.toFile() ,APPLICATION_PDF)
        .field("dataformat", DataFormat.XDP.name())) {
			
			Response result = target.request()
									  .accept(MediaType.APPLICATION_XML_TYPE)
									  .post(Entity.entity(multipart, multipart.getMediaType()));
			
			assertTrue(result.hasEntity(), "Expected the response to have an entity.");
			assertEquals(Response.Status.OK.getStatusCode(), result.getStatus(), ()->"Expected response to be 'OK', entity='" + TestUtils.readEntityToString(result) + "'.");
			byte[] resultBytes = IOUtils.toByteArray((InputStream)result.getEntity());
			String s=resultBytes.toString();
			
			
			if (SAVE_RESULTS) {
				IOUtils.write(resultBytes, Files.newOutputStream(TestUtils.ACTUAL_RESULTS_DIR.resolve("ExportedData2.xml")));
			}
			assertEquals(MediaType.APPLICATION_XML_TYPE, MediaType.valueOf(result.getHeaderString(HttpHeaders.CONTENT_TYPE)));
		}
		
		try (@SuppressWarnings("resource")
		final FormDataMultiPart multipart = new FormDataMultiPart()
        .field("pdforxdp", TestUtils.SAMPLE_FORM_XDP.toFile() ,APPLICATION_PDF)
        .field("dataformat", DataFormat.Auto.name())) {
			
			Response result = target.request()
									  .accept(MediaType.APPLICATION_XML_TYPE)
									  .post(Entity.entity(multipart, multipart.getMediaType()));
			
			assertTrue(result.hasEntity(), "Expected the response to have an entity.");
			assertEquals(Response.Status.OK.getStatusCode(), result.getStatus(), ()->"Expected response to be 'OK', entity='" + TestUtils.readEntityToString(result) + "'.");
			byte[] resultBytes = IOUtils.toByteArray((InputStream)result.getEntity());
		
			if (SAVE_RESULTS) {
				IOUtils.write(resultBytes, Files.newOutputStream(TestUtils.ACTUAL_RESULTS_DIR.resolve("ExportedData3.xml")));
			}
			assertEquals(MediaType.APPLICATION_XML_TYPE, MediaType.valueOf(result.getHeaderString(HttpHeaders.CONTENT_TYPE)));
		}
	}

	}
