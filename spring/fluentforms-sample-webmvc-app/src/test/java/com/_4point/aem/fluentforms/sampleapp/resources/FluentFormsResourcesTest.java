package com._4point.aem.fluentforms.sampleapp.resources;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;

import com._4point.aem.fluentforms.sampleapp.FluentFormsSpringApplication;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.recording.SnapshotRecordResult;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;

@EnabledIf("com._4point.aem.fluentforms.sampleapp.resources.TestConstants#runWiremockTests")
@WireMockTest()
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, 
				classes = FluentFormsSpringApplication.class
				)
@EnableWireMock(@ConfigureWireMock(
					portProperties = "fluentforms.aem.port"
        			)
				)
class FluentFormsResourcesTest extends AbstractFluentFormsResourcesTest {
	private static final boolean WIREMOCK_RECORDING = false;



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