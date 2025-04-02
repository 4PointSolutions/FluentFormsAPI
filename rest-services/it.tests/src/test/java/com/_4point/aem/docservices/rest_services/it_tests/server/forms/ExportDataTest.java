package com._4point.aem.docservices.rest_services.it_tests.server.forms;

import static com._4point.aem.docservices.rest_services.it_tests.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Node;

import com._4point.aem.docservices.rest_services.it_tests.AemInstance;
import com._4point.aem.docservices.rest_services.it_tests.TestUtils;
import com.adobe.fd.forms.api.DataFormat;

@Tag("server-tests")
public class ExportDataTest {


	private static final String EXPORT_DATA_URL = "http://" + AemInstance.AEM_1.aemHost() + ":" + AemInstance.AEM_1.aemPort() + TEST_MACHINE_AEM_TYPE.pathPrefix() + "/services/FormsService/ExportData";
	private static final MediaType APPLICATION_PDF = new MediaType("application", "pdf");
	protected static final MediaType APPLICATION_XDP = new MediaType("application", "vnd.adobe.xdp+xml");
	private static final MediaType APPLICATION_XML = new MediaType("application", "xml");
	
	private static final boolean SAVE_RESULTS = false;
	
	private WebTarget target;

	@BeforeAll
	static void setUpAll() throws Exception {
		AemInstance.AEM_1.prepareForTests();
	}

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
												.field("pdforxdp", TestUtils.SAMPLE_FORM_WITH_DATA_PDF.toFile() ,APPLICATION_PDF)
												.field("dataformat", DataFormat.XmlData.name())) {
			
			Response result = target.request()
									  .accept(MediaType.APPLICATION_XML_TYPE)
									  .post(Entity.entity(multipart, multipart.getMediaType()));
			
			assertTrue(result.hasEntity(), "Expected the response to have an entity. Status=" + result.getStatus());
			
			assertEquals(Response.Status.OK.getStatusCode(), result.getStatus(), () -> "Expected response to be 'OK', entity='" + TestUtils.readEntityToString(result) + "'.");
			
		
			byte[] resultBytes = IOUtils.toByteArray((InputStream)result.getEntity());
			
			if (SAVE_RESULTS) {
				IOUtils.write(resultBytes, Files.newOutputStream(TestUtils.ACTUAL_RESULTS_DIR.resolve("testExportData_Bytes_result.xml")));
			}

			org.w3c.dom.Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(resultBytes)); 
			 XPathExpression xpath = XPathFactory.newInstance().newXPath().compile("/form1");
			 Node output1 = (Node)xpath.evaluate(document, XPathConstants.NODE);
			
			 assertEquals("form1",output1.getNodeName());
			 
			 assertEquals("abcd", output1.getTextContent().trim());
			
			 assertEquals(APPLICATION_XML, MediaType.valueOf(result.getHeaderString(HttpHeaders.CONTENT_TYPE)));
		}
		}
		
	@Test
	void testExportDataXdp_Bytes() throws Exception {
		try (@SuppressWarnings("resource")
			final FormDataMultiPart multipart = new FormDataMultiPart()
														.field("pdforxdp", TestUtils.SAMPLE_FORM_WITH_DATA_PDF.toFile() ,APPLICATION_PDF)
														.field("dataformat", DataFormat.XDP.name())) {
			
			Response result = target.request()
									  .accept(MediaType.APPLICATION_XML_TYPE)
									  .post(Entity.entity(multipart, multipart.getMediaType()));
			
			assertTrue(result.hasEntity(), "Expected the response to have an entity.");
			
			assertEquals(Response.Status.OK.getStatusCode(), result.getStatus(), ()->"Expected response to be 'OK', entity='" + TestUtils.readEntityToString(result) + "'.");
			
			byte[] resultBytes = IOUtils.toByteArray((InputStream)result.getEntity());
			
			if (SAVE_RESULTS) {
				IOUtils.write(resultBytes, Files.newOutputStream(TestUtils.ACTUAL_RESULTS_DIR.resolve("testExportDataXdp_Bytes_result.xml")));
			}

			org.w3c.dom.Document document = DocumentBuilderFactory.newInstance()
																  .newDocumentBuilder()
																  .parse(new ByteArrayInputStream(resultBytes));
			XPathExpression xpath = XPathFactory.newInstance().newXPath().compile("//form1");
			Node output1 = (Node) xpath.evaluate(document, XPathConstants.NODE);

			assertEquals("form1", output1.getNodeName());

			assertEquals("abcd", output1.getTextContent().trim());

			assertEquals(MediaType.APPLICATION_XML_TYPE, MediaType.valueOf(result.getHeaderString(HttpHeaders.CONTENT_TYPE)));
		}
	}
		
	@Test
	void testExportDataAuto_Bytes() throws Exception {
		
		try (@SuppressWarnings("resource")
		final FormDataMultiPart multipart = new FormDataMultiPart()
												.field("pdforxdp", TestUtils.SAMPLE_FORM_WITH_DATA_PDF.toFile() ,APPLICATION_PDF)
												.field("dataformat", DataFormat.Auto.name())) {
			
			Response result = target.request()
									  .accept(MediaType.APPLICATION_XML_TYPE)
									  .post(Entity.entity(multipart, multipart.getMediaType()));
			
			assertTrue(result.hasEntity(), "Expected the response to have an entity.");
			
			assertEquals(Response.Status.OK.getStatusCode(), result.getStatus(), ()->"Expected response to be 'OK', entity='" + TestUtils.readEntityToString(result) + "'.");
			
			byte[] resultBytes = IOUtils.toByteArray((InputStream)result.getEntity());
			
			if (SAVE_RESULTS) {
				IOUtils.write(resultBytes, Files.newOutputStream(TestUtils.ACTUAL_RESULTS_DIR.resolve("testExportDataAuto_Bytes_result.xml")));
			}
	
			org.w3c.dom.Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(resultBytes)); 
			XPathExpression xpath = XPathFactory.newInstance().newXPath().compile("//form1");
			 Node output1 = (Node)xpath.evaluate(document, XPathConstants.NODE);
			 			 
			 assertEquals("form1",output1.getNodeName());
			 
			 assertEquals("abcd", output1.getTextContent().trim());
			
			
			assertEquals(MediaType.APPLICATION_XML_TYPE, MediaType.valueOf(result.getHeaderString(HttpHeaders.CONTENT_TYPE)));
		}
	}

	@Test
	void testExportData_NoContent() throws Exception {
		
		try (@SuppressWarnings("resource")
		final FormDataMultiPart multipart = new FormDataMultiPart()
												.field("pdforxdp", TestUtils.SAMPLE_FORM_PDF.toFile() ,APPLICATION_PDF)
												.field("dataformat", DataFormat.XmlData.name())) {
			
			Response result = target.request()
									  .accept(MediaType.APPLICATION_XML_TYPE)
									  .post(Entity.entity(multipart, multipart.getMediaType()));
			
			assertFalse(result.hasEntity(), "Expected the response to have no entity.");
			
			assertEquals(Response.Status.NO_CONTENT.getStatusCode(), result.getStatus(), ()->"Expected response to be 'No Content'.");
			
			byte[] resultBytes = IOUtils.toByteArray((InputStream)result.getEntity());
			
			if (SAVE_RESULTS) {
				IOUtils.write(resultBytes, Files.newOutputStream(TestUtils.ACTUAL_RESULTS_DIR.resolve("testExportData_NoContent_result.xml")));
			}
		}
	}

}
