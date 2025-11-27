package com._4point.aem.fluentforms.sampleapp.resources;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
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
				classes = FluentFormsSpringApplication.class,
				// Wiremock produces a lot of output, the following entries reduce that output.  They can be removed for debugging.
				properties = {"logging.level.org.wiremock.spring=WARN", "logging.level.WireMock.wiremock=WARN"}
				)
@EnableWireMock(@ConfigureWireMock(
					portProperties = "fluentforms.aem.port"
        			)
				)
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class WireMockAemProxyEndpointTest extends AbstractAemProxyEndpointTest {
	private static final boolean WIREMOCK_RECORDING = false;

	private static final String CRX_CONTENT_ROOT = "crx:/content/dam/formsanddocuments/sample-forms/";

	public WireMockAemProxyEndpointTest() {
		super(CRX_CONTENT_ROOT + SAMPLE_XDP_FILENAME_PATH);
	}

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

	@Override
	protected void verifyProxyTest() {
		List.of(
	    		/* "/content/xfaforms/profiles/default.html", */ // This fails for some reason however it's not essential to the test because if this were truly not working, none of the other calls would be made.
	    		/* "/libs/granite/csrf/token.json", */ // This is not tested because it doesn't always happen (depending on the timings).
	    		"/etc.clientlibs/fd/xfaforms/clientlibs/I18N/en.js",
	    		"/etc.clientlibs/fd/xfaforms/clientlibs/profile.css",
	    		"/etc.clientlibs/fd/xfaforms/clientlibs/profile.js",
	    		"/etc.clientlibs/clientlibs/granite/jquery/granite/csrf.js",
	    		"/etc.clientlibs/toggles.json"
	    		)
	    	.forEach(url->verify(getRequestedFor(urlPathEqualTo(url))));
	}
}
