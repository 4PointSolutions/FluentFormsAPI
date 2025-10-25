package com._4point.aem.fluentforms.sampleapp.resources;

import static com._4point.aem.fluentforms.sampleapp.resources.ResponseEntityMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.net.URI;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

// This test does not call AEM, so does not require WireMock
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class AfSubmissionHandlerTest {

	public static final String AF_TEMPLATE_NAME = "sample00002test";
	private static final String SUBMIT_ADAPTIVE_FORM_SERVICE_PATH = "/aem/content/forms/af/" + AF_TEMPLATE_NAME + "/jcr:content/guideContainer.af.submit.jsp";

	@LocalServerPort
	private int port;

	private static final RestClient REST_CLIENT = RestClient.create();

	@Test
	void testAdaptiveFormSubmitHandler() {
		var mockData = mockFormData("http://localhost:8080/redirect", "{ \"foo\" : \"bar\"}");

		URI uri = UriComponentsBuilder.fromUri(getBaseUri(port))
							.path(SUBMIT_ADAPTIVE_FORM_SERVICE_PATH)
							.queryParam("form", "sample_template.xdp")
							.build()
							.toUri(); 
		ResponseEntity<String> response = REST_CLIENT.post()
				.uri(uri)
				.body(mockData)
				.contentType(MediaType.MULTIPART_FORM_DATA)	// Need to set this explicitly since we're sending all strings.
				.retrieve()
				.toEntity(String.class);
		
		assertThat(response, allOf(isStatus(HttpStatus.OK), isMediaType(MediaType.TEXT_PLAIN), hasStringEntityMatching(equalTo("Successful Submit"))));
	}
	
	private static MultiValueMap<String, Object> mockFormData(String redirect, String data) {
		final MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
		parts.add("guideContainerPath", "/aem/content/forms/af/" + AF_TEMPLATE_NAME + "/jcr:content/guideContainer");
		parts.add("aemFormComponentPath", "");
		parts.add("_asyncSubmit", "false");
		parts.add("_charset_", "UTF-8");
		parts.add("runtimeLocale", "en");
		parts.add("fileAttachmentMap", "{}");
		parts.add("afSubmissionInfo", "{\"computedMetaInfo\":{},\"stateOverrides\":{},\"signers\":{}}");
		parts.add("TextField1", "TextField1 Contents");
		parts.add("TextField2", "TextField2 Contents");
		parts.add("jcr:data", data);
		parts.add(":redirect", redirect);
		parts.add(":selfUrl", "/aem/content/forms/af/" + AF_TEMPLATE_NAME);
		parts.add("_guideValueMap", "yes");
		parts.add("_guideValuesMap", "{\"textdraw1555538078737\":\"<p style=\\\"text-align: center;\\\"><b>Sample Form</b></p>\\n\",\"TextField1\":\"DFGDFG\",\"TextField2\":\"DFGDG 233\",\"submit\":null}");
		parts.add("_guideAttachments", "");
		parts.add(":cq_csrf_token", "eyJleHAiOjE1NjU2MzUzNzcsImlhdCI6MTU2NTYzNDc3N30.9KB9yPr_mvIfyiwzn5S8mMh-yUzD0-BF99cJR7vW49M");
		return parts;
	}

	protected static String getBaseUriString(int port) {
		return getBaseUri(port).toString();
	}

	private static URI getBaseUri(int port) {
		return URI.create("http://localhost:" + port);
	}
}
