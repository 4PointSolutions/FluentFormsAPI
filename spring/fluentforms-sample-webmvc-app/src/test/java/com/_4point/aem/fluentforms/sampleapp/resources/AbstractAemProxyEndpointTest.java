package com._4point.aem.fluentforms.sampleapp.resources;

import static com._4point.aem.fluentforms.sampleapp.resources.ResponseEntityMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import java.nio.file.Path;

import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlPage;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

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
		
		// RestTestClient requires Spring Boot 4.x, which we can't use yet because Fluent Forms requires Spring Boot 3.x.
//		RestTestClient restTestClient = RestTestClient.bindToServer()
//													  .baseUrl(getBaseUri(port).toASCIIString())
//													  .build();
//		restTestClient.get().uri(csrf_token_path).exchange()
//					  .expectStatus().isOk()
//					  .expectHeader().value(HttpHeaders.CONTENT_TYPE, 
//							  org.hamcrest.Matchers.anyOf(org.hamcrest.Matchers.equalTo(MediaType.APPLICATION_JSON_VALUE), org.hamcrest.Matchers.equalTo(MediaType.TEXT_HTML_VALUE)))
//					  .expectBody(String.class)
//					  .value(body -> assertThat(body, matchesRegex("\\s*\\{\"token\":\".*\"\\}\\s*")));

		RestClient restClient = RestClient.create();
		ResponseEntity<String> response = restClient.get()
				.uri(getBaseUri(port) + csrf_token_path)
				.retrieve()
				.toEntity(String.class);
		
		assertThat(response, allOf(isStatus(HttpStatus.OK), 
		   // For some reason that I can't determine, wiremock returns text/html.  I would like to fix this, but for now, I accept both.
		   anyOf(isMediaType(MediaType.APPLICATION_JSON), isMediaType(MediaType.TEXT_HTML)), 
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
