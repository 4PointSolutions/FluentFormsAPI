package com._4point.aem.fluentforms.sampleapp.resources;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import com._4point.aem.fluentforms.sampleapp.FluentFormsSpringApplication;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.recording.SnapshotRecordResult;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;

@WireMockTest(httpPort = 5502)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, 
				classes = FluentFormsSpringApplication.class,
				properties = {"fluentforms.aem.port=5502"}
				)
class FluentFormsResourcesTest {
	private static final boolean SAVE_RESULTS = false;
	private static final boolean WIREMOCK_RECORDING = false;

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
	void testOutputServiceGeneratePdf() {
		Response response = ClientBuilder.newClient()
										 .target(getBaseUri(port))
										 .path("/FluentForms/OutputServiceGeneratePdf")
										 .queryParam("formName", "formName")
										 .request()
										 .get();
		
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}

	private static String getBaseUriString(int port) {
		return getBaseUri(port).toString();
	}

	private static URI getBaseUri(int port) {
		return URI.create("http://localhost:" + port);
	}

    @Test
    public void whenSpringContextIsBootstrapped_thenNoExceptions() {
    }
}
