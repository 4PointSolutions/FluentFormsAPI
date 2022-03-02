package com._4point.aem.docservices.rest_services.cli.restservices.cli;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.shell.Shell;
import org.springframework.shell.result.DefaultResultHandler;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.recording.SnapshotRecordResult;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;

@Tag("Integration")
@SpringBootTest(properties = { "spring.shell.interactive.enabled=false", "spring.shell.script.enabled=false" })
@WireMockTest
class ApplicationTests {

	private static final boolean WIREMOCK_RECORDING = false;

	@Autowired
	private Shell shell;

	@Autowired
	private DefaultResultHandler resultHandler;

	private static int wiremockPort;
	
	private String runShellCommand(String command) {
		Object cmd = shell.evaluate(()->command);
		assertNotNull(cmd);
		resultHandler.handleResult(cmd);
		return cmd.toString();
	}
	
	@DynamicPropertySource
	static void setAemProperties(DynamicPropertyRegistry registry) {
		registry.add(AemConfigProperties.AEM_PORT_ENV_PARAM, ()->Integer.toString(wiremockPort));
	}
	
	@BeforeAll
	 static void setupAll(WireMockRuntimeInfo wmRuntimeInfo) {
		wiremockPort = wmRuntimeInfo.getHttpPort();
	}
	
	@BeforeEach
	void setUp(WireMockRuntimeInfo wmRuntimeInfo) throws Exception {
		// Need to move this into @TestConfig
//		underTest = new PdfFormService(createAemConfig("localhost", wmRuntimeInfo.getHttpPort()));
		if (WIREMOCK_RECORDING) {
			String realServiceBaseUri = new URI("http://localhost:4502").toString();
			WireMock.startRecording(realServiceBaseUri);
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
	void testContextLoads() {
		assertThat(runShellCommand("help"), allOf(
				containsString("generate-pdfoutput"),
				containsString("generate-pdfoutput-remote")
				));
	}

	@Test
	void testGeneratePdfOutput() {
		assertThat(runShellCommand("generate-pdfoutput --template src/test/resources/sampleFiles/SampleForm.xdp --data src/test/resources/sampleFiles/SampleForm_data.xml"), containsStringIgnoringCase("Document generated"));
	}

	@Test
	void testGeneratePdfOutputRemote() {
		assertThat(runShellCommand("generate-pdfoutput-remote"), containsStringIgnoringCase("Document generated"));
	}
}
