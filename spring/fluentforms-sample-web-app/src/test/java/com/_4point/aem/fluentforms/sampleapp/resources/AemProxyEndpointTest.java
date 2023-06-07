package com._4point.aem.fluentforms.sampleapp.resources;

import static com._4point.testing.matchers.jaxrs.ResponseMatchers.hasMediaType;
import static com._4point.testing.matchers.jaxrs.ResponseMatchers.isStatus;
import static com._4point.testing.matchers.jaxrs.ResponseMatchers.hasEntity;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.util.List;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;

import com._4point.aem.fluentforms.sampleapp.FluentFormsSpringApplication;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.recording.SnapshotRecordResult;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;

@WireMockTest(httpPort = AemProxyEndpointTest.WIREMOCK_PORT)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, 
				classes = FluentFormsSpringApplication.class,
				properties = {"fluentforms.aem.port=" + AemProxyEndpointTest.WIREMOCK_PORT }
				)
class AemProxyEndpointTest {
	private static final boolean SAVE_RESULTS = false;
	private static final boolean WIREMOCK_RECORDING = false;
	static final int WIREMOCK_PORT = 5502;

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
	void testProxyCsrfToken() throws Exception {
		String csrf_token_path =  "/aem/libs/granite/csrf/token.json";
		Response response = ClientBuilder.newClient()
				 .target(getBaseUri(port))
				 .path(csrf_token_path)
				 .request()
				 .get();
		
		assertThat(response, allOf(isStatus(Status.OK), hasMediaType(MediaType.TEXT_HTML_TYPE), hasEntity()));

//		if (USE_WIREMOCK) {	// For some reason that I can't determine, wiremock returns text/html.  I would like to fix this, but for now, I work around it.
//			assertTrue(MediaType.TEXT_HTML_TYPE.isCompatible(mediaType), "Expected response media type (" + response.getMediaType().toString() + ") to be compatible with 'text/html'.");
//		} else {
//			assertTrue(MediaType.APPLICATION_JSON_TYPE.isCompatible(mediaType), "Expected response media type (" + response.getMediaType().toString() + ") to be compatible with 'application/json'.");
//		}
//		assertTrue(response.hasEntity(), "Expected response to have entity");
//		byte[] resultBytes = ((InputStream)response.getEntity()).readAllBytes();
//		if (SAVE_RESULTS /* && USE_AEM */) {
//			try (var os = Files.newOutputStream(ACTUAL_RESULTS_DIR.resolve("testProxyCsrfToken_" + (urlPrefix.isBlank() ? "osgi" : "jee") + "_result.json"))) {
//				os.write(resultBytes);;
//			}
//		}
	}

	private static String getBaseUriString(int port) {
		return getBaseUri(port).toString();
	}

	private static URI getBaseUri(int port) {
		return URI.create("http://localhost:" + port);
	}
}
