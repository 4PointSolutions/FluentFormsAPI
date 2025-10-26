package com._4point.aem.fluentforms.sampleapp.resources;

import static com._4point.aem.fluentforms.sampleapp.resources.ResponseEntityMatchers.*;
import static com._4point.testing.matchers.jaxrs.ResponseMatchers.hasMediaType;
import static com._4point.testing.matchers.jaxrs.ResponseMatchers.hasStringEntityMatching;
import static com._4point.testing.matchers.jaxrs.ResponseMatchers.isStatus;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import java.nio.file.Path;

import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlPage;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

abstract class AbstractAemProxyEndpointTest {
	protected static final Path SAMPLE_XDP_FILENAME_PATH = Path.of("SampleForm.xdp");

	@LocalServerPort
	private int port;
	private final String sampleFileLocation;

	protected AbstractAemProxyEndpointTest(String sampleFileLocation) {
		this.sampleFileLocation = sampleFileLocation;
	}

	@Test
	void testProxyCsrfToken() throws Exception {
		String csrf_token_path =  "/aem/libs/granite/csrf/token.json";
		Response response = ClientBuilder.newClient()
				 .target(getBaseUri(port))
				 .path(csrf_token_path)
				 .request()
				 .get();
		
		assertThat(response, allOf(isStatus(Status.OK), 
								   // For some reason that I can't determine, wiremock returns text/html.  I would like to fix this, but for now, I accept both.
								   anyOf(hasMediaType(MediaType.APPLICATION_JSON_TYPE), hasMediaType(MediaType.TEXT_HTML_TYPE)), 
								   hasStringEntityMatching(matchesRegex("\\s*\\{\"token\":\".*\"\\}\\s*"))
								   ));
	}

	@Test
	void proxyTest() throws Exception {
	    try (final WebClient webClient = new WebClient()) {
	        String baseUri = getBaseUriString(port) + "/FluentForms/Html5FormsServiceRenderHtml5Form" + "?form=" + sampleFileLocation;
			final HtmlPage page = webClient.getPage(baseUri);
	        assertEquals("LC Forms", page.getTitleText());
	    }
	    verifyProxyTest();
	}

	protected abstract void verifyProxyTest();

	protected static String getBaseUriString(int port) {
		return getBaseUri(port).toString();
	}

	private static URI getBaseUri(int port) {
		return URI.create("http://localhost:" + port);
	}
	
}
