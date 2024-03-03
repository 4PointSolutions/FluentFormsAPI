package com._4point.aem.docservices.rest_services.client.jersey;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com._4point.aem.docservices.rest_services.client.RestClient;
import com._4point.aem.docservices.rest_services.client.RestClient.MultipartPayload;
import com._4point.aem.docservices.rest_services.client.RestClient.MultipartPayload.Builder;
import com._4point.aem.docservices.rest_services.client.RestClient.Response;
import com._4point.aem.docservices.rest_services.client.RestClient.RestClientException;
import com._4point.aem.docservices.rest_services.client.helpers.AemConfig;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

@WireMockTest
class JerseyRestClientTest {
	private static final String FIELD1_NAME = "field1";
	private static final String FIELD1_DATA = "field1 data";
	private static final String FIELD2_NAME = "field2";
	private static final String FIELD2_DATA = "field2 data";
	private static final String SAMPLE_HEADER_VALUE = "sample_header_value";
	private static final String SAMPLE_HEADER = "sample_header";
	private static final String MOCK_PDF_BYTES = "Mock PDF Bytes";
	private static final String APPLICATION_PDF = "application/pdf";
	private static final String ENDPOINT = "/services/OutputService/GeneratePdfOutput";

	private RestClient underTest;
	
	@BeforeEach
	void setup(WireMockRuntimeInfo wmRuntimeInfo) {
		AemConfig aemConfig = AemConfig.builder()
				   .port(wmRuntimeInfo.getHttpPort())
				   .build();
		
		underTest = new JerseyRestClient(aemConfig, ENDPOINT);
	}

	@DisplayName("PostToServer with 1 part and return no content in response")
	@Test
	void testPostToServer_NoContentResponse() throws Exception {
		// Given
		stubFor(post(ENDPOINT).willReturn(noContent()));
		
		// When
		Optional<Response> result = performPostToServer(FIELD1_NAME, FIELD1_DATA);

		// Then
		assertTrue(result.isEmpty());

		verify(postRequestedFor(urlEqualTo(ENDPOINT))
				.withAllRequestBodyParts(aMultipart(FIELD1_NAME).withBody(equalTo(FIELD1_DATA)))
				);
	}

	@DisplayName("PostToServer with 2 parts and return no headers in response")
	@Test
	void testPostToServer_DocumentResponseNoHeader() throws Exception {
		// Given
		stubFor(post(ENDPOINT).willReturn(okForContentType(APPLICATION_PDF, MOCK_PDF_BYTES)));
		
		// When
		Response response = performPostToServer(FIELD1_NAME, FIELD1_DATA, FIELD2_NAME, FIELD2_DATA, "foo", "BAR").orElseThrow();

		// Then
		assertEquals(APPLICATION_PDF, response.contentType());
		assertEquals(MOCK_PDF_BYTES, new String(response.data().readAllBytes()));
		assertTrue(response.retrieveHeader(SAMPLE_HEADER).isEmpty());
		verify(postRequestedFor(urlEqualTo(ENDPOINT))
				.withRequestBodyPart(aMultipart().withName(FIELD1_NAME).withBody(equalTo(FIELD1_DATA)).build())
				.withRequestBodyPart(aMultipart().withName(FIELD2_NAME).withBody(equalTo(FIELD2_DATA)).build())
				);
	}

	@DisplayName("PostToServer with 1 part and return 1 header in response")
	@Test
	void testPostToServer_DocumentResponseWithHeader() throws Exception {
		// Given
		stubFor(post(ENDPOINT).willReturn(okForContentType(APPLICATION_PDF, MOCK_PDF_BYTES)
											.withHeader(SAMPLE_HEADER, SAMPLE_HEADER_VALUE)
										  ));
	
		// When
		Response response = performPostToServer(FIELD1_NAME, FIELD1_DATA).orElseThrow();

		// Then
		assertEquals(APPLICATION_PDF, response.contentType());
		assertEquals(MOCK_PDF_BYTES, new String(response.data().readAllBytes()));
		assertEquals(SAMPLE_HEADER_VALUE, response.retrieveHeader(SAMPLE_HEADER).orElseThrow());
		verify(postRequestedFor(urlEqualTo(ENDPOINT))
				.withAllRequestBodyParts(aMultipart(FIELD1_NAME).withBody(equalTo(FIELD1_DATA)))
				);
	}

	// TODO:  Test that instantiates multiple JerseyRestClients and calls each to make sure that:
	// a) Lazy initialization of client works
	// b) Calling configureClient on the same client multiple times is OK.
	
	private Optional<Response> performPostToServer(String...strings) throws RestClientException, Exception {
		if (strings.length % 2 != 0) { 
			throw new IllegalArgumentException("Odd number of Strings passed in, must be even. (" + strings.length + ").");
		}
		
		Builder builder = underTest.multipartPayloadBuilder();
		for(int i = 0; i < strings.length; i+=2) {
			builder.add(strings[i], strings[i+1]);
		}
		
		try (MultipartPayload payload = builder.build()) {
			return payload.postToServer(APPLICATION_PDF);
		}
	}
}
