package com._4point.aem.fluentforms.sampleapp.resources;

import static com._4point.testing.matchers.jaxrs.ResponseMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat; 
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import com._4point.aem.fluentforms.sampleapp.FluentFormsSpringApplication;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.recording.SnapshotRecordResult;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;

@WireMockTest(httpPort = FluentFormsResourcesTest.WIREMOCK_HTTP_PORT)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, 
				classes = FluentFormsSpringApplication.class,
				properties = {"fluentforms.aem.port=" + FluentFormsResourcesTest.WIREMOCK_HTTP_PORT }
				)
class FluentFormsResourcesTest {
	private static final boolean SAVE_RESULTS = false;
	private static final boolean WIREMOCK_RECORDING = false;
	/* package */ static final int WIREMOCK_HTTP_PORT = 5502;

	private static final String APPLICATION_PDF = "application/pdf";
	private static final MediaType APPLICATION_PDF_TYPE = MediaType.valueOf(APPLICATION_PDF);
	
	private static final Path RESOURCES_DIR = Path.of("src", "test", "resources");
	private static final Path SAMPLE_FILES_DIR = RESOURCES_DIR.resolve("SampleFiles");

	@LocalServerPort
	private int port;

	@BeforeEach
	void setUp(WireMockRuntimeInfo wmRuntimeInfo) throws Exception {
		if (WIREMOCK_RECORDING) {
			WireMock.startRecording(getBaseUriString(4502));
		}
	}

	@AfterEach
	void tearDown() throws Exception {
		if (WIREMOCK_RECORDING) {
			SnapshotRecordResult recordings = WireMock.stopRecording();
			List<StubMapping> mappings = recordings.getStubMappings();
			System.out.println("Found " + mappings.size() + " recordings.");
			for (StubMapping mapping : mappings) {
				ResponseDefinition response = mapping.getResponse();
				var jsonBody = response.getJsonBody();
				System.out.println(jsonBody == null ? "JsonBody is null" : jsonBody.toPrettyString());
			}
		}
	}
	
	@Test
	void testOutputServiceGeneratePdf_NoData() {
		Response response = ClientBuilder.newClient()
										 .target(getBaseUri(port))
										 .path("/FluentForms/OutputServiceGeneratePdf")
										 .queryParam("form", "sample_template.xdp")
										 .request()
										 .get();
		
		assertThat(response, allOf(isStatus(Status.OK), hasMediaType(APPLICATION_PDF_TYPE)));
	}

	@Test
	void testOutputServiceGeneratePdf_WithData() {
		Client client = ClientBuilder.newClient();
		String dataKeyValue = "testOutputServiceGeneratePdf_WithData.xml";
		Response dataResponse = client.target(getBaseUri(port))
								  .path("/FluentForms/SaveData")
								  .queryParam("dataKey", dataKeyValue)
								  .request()
								  .post(Entity.xml(readXmlData()));
		assertThat(dataResponse, allOf(isStatus(Status.NO_CONTENT)));

		Response response = ClientBuilder.newClient()
				 .target(getBaseUri(port))
				 .path("/FluentForms/OutputServiceGeneratePdf")
				 .queryParam("form", "sample_template.xdp")
				 .queryParam("dataKey", dataKeyValue)
				 .request()
				 .get();

		assertThat(response, allOf(isStatus(Status.OK), hasMediaType(APPLICATION_PDF_TYPE)));
	}

	@Test
	void testOutputServiceGeneratePdf_WithPostedXmlData() {
		Response response = ClientBuilder.newClient()
				 .target(getBaseUri(port))
				 .path("/FluentForms/OutputServiceGeneratePdf")
				 .queryParam("form", "sample_template.xdp")
				 .request()
				 .post(Entity.xml(readXmlData()));

		assertThat(response, allOf(isStatus(Status.OK), hasMediaType(APPLICATION_PDF_TYPE)));
	}

	@Test
	void testOutputServiceGeneratePdf_WithPostedJsonData() {
		Response response = ClientBuilder.newClient()
				 .target(getBaseUri(port))
				 .path("/FluentForms/OutputServiceGeneratePdf")
				 .queryParam("form", "sample_template.xdp")
				 .request()
				 .post(Entity.json(readJsonData()));

		assertThat(response, allOf(isStatus(Status.OK), hasMediaType(APPLICATION_PDF_TYPE)));
	}

	@Test
	void testAdaptiveFormsServiceRenderAdaptiveForm_NoData() {
		Response response = ClientBuilder.newClient()
										 .target(getBaseUri(port))
										 .path("/FluentForms/AdaptiveFormsServiceRenderAdaptiveForm")
										 .queryParam("form", "sample00002test")
										 .request()
										 .get();
		
		assertThat(response, allOf(isStatus(Status.OK), hasMediaType(MediaType.TEXT_HTML_TYPE)));
	}

	@Test
	void testAdaptiveFormsServiceRenderAdaptiveForm_WithData() {
		Client client = ClientBuilder.newClient();
		String dataKeyValue = "testAdaptiveFormsServiceRenderAdaptiveForm_WithData";
		Response dataResponse = client.target(getBaseUri(port))
								  .path("/FluentForms/SaveData")
								  .queryParam("dataKey", dataKeyValue)
								  .request()
								  .post(Entity.xml(readXmlData()));
		assertThat(dataResponse, allOf(isStatus(Status.NO_CONTENT)));

		Response response = client.target(getBaseUri(port))
								  .path("/FluentForms/AdaptiveFormsServiceRenderAdaptiveForm")
								  .queryParam("form", "sample00002test")
								  .queryParam("dataKey", dataKeyValue)
								  .request()
								  .get();
		
		assertThat(response, allOf(isStatus(Status.OK), hasMediaType(MediaType.TEXT_HTML_TYPE)));
	}

	@Test
	void testHtml5FormsServiceRenderHtml5Form_NoData() {
		Response response = ClientBuilder.newClient()
										 .target(getBaseUri(port))
										 .path("/FluentForms/Html5FormsServiceRenderHtml5Form")
										 .queryParam("form", SAMPLE_FILES_DIR.resolve("SampleForm.xdp").toAbsolutePath())
										 .request()
										 .get();
		
		assertThat(response, allOf(isStatus(Status.OK), hasMediaType(MediaType.TEXT_HTML_TYPE)));
	}

	@Test
	void testHtml5FormsServiceRenderHtmlForm_WithData() {
		Client client = ClientBuilder.newClient();
		String dataKeyValue = "testHtml5FormsServiceRenderHtmlForm_WithData";
		Response dataResponse = client.target(getBaseUri(port))
								  .path("/FluentForms/SaveData")
								  .queryParam("dataKey", dataKeyValue)
								  .request()
								  .post(Entity.xml(readXmlData()));
		assertThat(dataResponse, allOf(isStatus(Status.NO_CONTENT)));

		Response response = client.target(getBaseUri(port))
								  .path("/FluentForms/Html5FormsServiceRenderHtml5Form")
								  .queryParam("form", SAMPLE_FILES_DIR.resolve("SampleForm.xdp").toAbsolutePath())
								  .queryParam("dataKey", dataKeyValue)
								  .request()
								  .get();
		
		assertThat(response, allOf(isStatus(Status.OK), hasMediaType(MediaType.TEXT_HTML_TYPE)));
	}

	private byte[] readXmlData() {
		Path sampleData = SAMPLE_FILES_DIR.resolve("SampleForm_data.xml");
		try {
			return Files.readAllBytes(sampleData);
		} catch (IOException e) {
			throw new IllegalStateException("Error while reading sample data (%s)".formatted(sampleData.toAbsolutePath()), e);
		}
	}
	
	private byte[] readJsonData() {
		Path sampleData = SAMPLE_FILES_DIR.resolve("SampleForm_data.json");
		try {
			return Files.readAllBytes(sampleData);
		} catch (IOException e) {
			throw new IllegalStateException("Error while reading sample data (%s)".formatted(sampleData.toAbsolutePath()), e);
		}
	}

	@Test
	void testSaveData() {
		Response response = ClientBuilder.newClient()
										 .target(getBaseUri(port))
										 .path("/FluentForms/SaveData")
										 .queryParam("dataKey", "saveDataKey_testSaveData")
										 .request()
										 .post(Entity.xml("<root/>"));
		
		assertThat(response, allOf(isStatus(Status.NO_CONTENT)));
	}

	private static String getBaseUriString(int port) {
		return getBaseUri(port).toString();
	}

	private static URI getBaseUri(int port) {
		return URI.create("http://localhost:" + port);
	}

	// The following test is commented out because it is not working.  The intention of the test is to verify that the
	// client can connect to the server using SSL.  The test is not working because of various issuesL
	//   1. We can't use WireMock's internal self-sign SSL certificate because we don't have access to it.  Using a separate
	//      SSL cert we generate ourselves would require a lot of additional configuration.
	//   2. Configuring the Spring Boot SSL properties is problematic because the annotations must be constants.
	//
	//  For the time being I will leave this in to remind future developers that this is a problem.  Maybe it will become easier as
	//  new versions become available.
	
//	@WireMockTest(httpsPort = FluentFormsResourcesHttpsTest.WIREMOCK_HTTPS_PORT, httpsEnabled = true, httpPort = FluentFormsResourcesTest.WIREMOCK_HTTP_PORT)
//	@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, 
//					classes = FluentFormsSpringApplication.class,
//					properties = {
//							"fluentforms.aem.port=" + FluentFormsResourcesHttpsTest.WIREMOCK_HTTPS_PORT,
//							"fluentforms.aem.useSsl=true",
////							"spring.ssl.bundle.jks.aem.truststore.location=file:" + FluentFormsResourcesHttpsTest.TRUST_STORE_PATH_STR,
////							"spring.ssl.bundle.jks.aem.truststore.password=" + FluentFormsResourcesHttpsTest.TRUST_STORE_PASSWORD_STR,
////							"spring.ssl.bundle.jks.aem.truststore.type=PKCS12"
//
//							}
//					)
//	static class FluentFormsResourcesHttpsTest {
////	    private static final Path TRUST_STORE_PATH = Path.of(System.getProperty("java.io.tmpdir"), "FluentFormsResourcesHttpsTest.keystore");
////	    /* package */ static final String TRUST_STORE_PATH_STR = TRUST_STORE_PATH.toUri().toString();
//	    /* package */ static final String TRUST_STORE_PATH_STR = "src/test/resources/FluentFormsResourcesHttpsTest.keystore";
//	    /* package */ static final String TRUST_STORE_PASSWORD_STR = "some_password";
//		/* package */ static final int WIREMOCK_HTTPS_PORT = 5504;
//
//		@LocalServerPort
//		private int port;
//
//		@Test
//		void testAdaptiveFormsServiceRenderAdaptiveForm_NoData() {
//			
//			Response response = ClientBuilder.newClient()
//											 .target(getBaseUri(port))
//											 .path("/FluentForms/AdaptiveFormsServiceRenderAdaptiveForm")
//											 .queryParam("form", "sample00002test")
//											 .request()
//											 .get();
//			
//			assertThat(response, allOf(isStatus(Status.OK), hasMediaType(MediaType.TEXT_HTML_TYPE)));
//		}
//
//		@Test
//		void testGetWireMockCertificate() throws Exception {
//			// http://localhost:8080. 
//			Response response = ClientBuilder.newClient()
//					 .target(getBaseUri(FluentFormsResourcesTest.WIREMOCK_HTTP_PORT))
//					 .path("/__admin/certs/wiremock-ca.crt")
//					 .queryParam("form", "sample00002test")
//					 .request()
//					 .get();
//			
//			var cert = response.readEntity(byte[].class);
//			assertNotNull(cert);
//			System.out.println("Certificate: " + new String(cert));
//			assertThat(response, allOf(isStatus(Status.OK), hasMediaType(MediaType.APPLICATION_OCTET_STREAM_TYPE)));
//
////			KeyStore keyStore = KeyStore.getInstance("JKS");
////	        keyStore.load(null, null);
////	        try (InputStream fileInputStream = new ByteArrayInputStream(cert);
////	             InputStream bufferedInputStream = new BufferedInputStream(fileInputStream)
////	        ) {
////	            while (bufferedInputStream.available() > 0) {
////	                Certificate certificate = CertificateFactory.getInstance("X.509").generateCertificate(bufferedInputStream);
////	                keyStore.setCertificateEntry("wiremock_root", certificate);
////	            }
////	        }
////	        try(OutputStream outputStream = Files.newOutputStream(Path.of(FluentFormsResourcesHttpsTest.TRUST_STORE_PATH_STR))) {
////	        	keyStore.store(outputStream, FluentFormsResourcesHttpsTest.TRUST_STORE_PASSWORD_STR.toCharArray());
////	        }
//		}
//	}
}