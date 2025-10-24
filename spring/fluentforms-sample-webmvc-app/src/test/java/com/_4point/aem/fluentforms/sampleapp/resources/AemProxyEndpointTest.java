package com._4point.aem.fluentforms.sampleapp.resources;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.htmlunit.DefaultCredentialsProvider;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
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
class AemProxyEndpointTest extends AbstractAemProxyEndpointTest {
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
	
	// In order to re-record the AEM interactions for Wiremock emulation, you need to:
	//  1) run a local AEM server
	//  2) set the WIREMOCK_RECORDING variable to true
	//  3) then run this test.
	// 
	// It will record the interactions with the AEM server.
	// Don't forget to set the WIREMOCK_RECORDING variable back to false after you are done.
	//
	// Note: THe recordings may require modification.  As of AEM 6.5 LTS, the required changes are:
	//  * Two calls to /content/xfaforms/profiles/default.html - One returns a 401, the other returns the form.
	//    This is because this HTMLUnit emulates a browser.  The 401 recording can be deleted since the FluentForms code
	//    sends a pre-emptive authentication header.
	//  * the /etc.clientlibs/fd/xfaforms/clientlibs/I18N/en_US recording must be modified to make the _US optional.
	//    It appears that the FluentForms call does not include the _US suffix.
	@Disabled("This test is not really a test but it is used to record interactions with the AEM server.")
	@Test
	void aemTest(WireMockRuntimeInfo wmRuntimeInfo) throws Exception {
	    DefaultCredentialsProvider userCredentials = new DefaultCredentialsProvider();
	    userCredentials.addCredentials("admin", "admin".toCharArray());
	    try (final WebClient webClient = new WebClient()) {
	    	webClient.setCredentialsProvider(userCredentials);
	        String baseUri = "http://localhost:" + wmRuntimeInfo.getHttpPort() + "/content/xfaforms/profiles/default.html?contentRoot=crx:///content/dam/formsanddocuments/sample-forms&template=SampleForm.xdp";
			final HtmlPage page = webClient.getPage(baseUri);
	        assertEquals("LC Forms", page.getTitleText());

//	        final String pageAsXml = page.asXml();
//	        assertTrue(pageAsXml.contains("<body class=\"topBarDisabled\">"), "Does not contain topBarDisabled");
//
//	        final String pageAsText = page.asNormalizedText();
//	        assertTrue(pageAsText.contains("Support for the HTTP and HTTPS protocols"));
	    }
	    List.of("/content/xfaforms/profiles/default.html", 
	    		"/etc.clientlibs/toggles.json", 
	    		"/libs/granite/csrf/token.json",
	    		"/etc.clientlibs/fd/xfaforms/clientlibs/I18N/en_US.js",
	    		"/etc.clientlibs/fd/xfaforms/clientlibs/profile.css",
	    		"/etc.clientlibs/fd/xfaforms/clientlibs/profile.js"
	    		)
	    	.forEach(url->verify(getRequestedFor(urlPathEqualTo(url))));
	}
}
