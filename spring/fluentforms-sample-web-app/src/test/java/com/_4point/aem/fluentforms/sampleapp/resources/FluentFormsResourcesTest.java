package com._4point.aem.fluentforms.sampleapp.resources;

import static com._4point.aem.fluentforms.sampleapp.resources.ResponseMatcher.hasMediaType;
import static com._4point.aem.fluentforms.sampleapp.resources.ResponseMatcher.isStatus;
import static org.hamcrest.MatcherAssert.assertThat; 
import static org.hamcrest.Matchers.*;
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

@WireMockTest(httpPort = FluentFormsResourcesTest.WIREMOCK_PORT)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, 
				classes = FluentFormsSpringApplication.class,
				properties = {"fluentforms.aem.port=" + FluentFormsResourcesTest.WIREMOCK_PORT }
				)
class FluentFormsResourcesTest {
	private static final boolean SAVE_RESULTS = false;
	private static final boolean WIREMOCK_RECORDING = false;
	private static final int WIREMOCK_PORT = 5502;

	private static final String APPLICATION_PDF = "application/pdf";
	private static final MediaType APPLICATION_PDF_TYPE = MediaType.valueOf(APPLICATION_PDF);

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
										 .queryParam("form", "sample_template.xdp")
										 .request()
										 .get();
		
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

	private static String getBaseUriString(int port) {
		return getBaseUri(port).toString();
	}

	private static URI getBaseUri(int port) {
		return URI.create("http://localhost:" + port);
	}
}
