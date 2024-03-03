package com._4point.aem.docservices.rest_services.client.jersey;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com._4point.aem.docservices.rest_services.client.RestClient;
import com._4point.aem.docservices.rest_services.client.RestClient.MultipartPayload;
import com._4point.aem.docservices.rest_services.client.RestClient.Response;
import com._4point.aem.docservices.rest_services.client.RestClient.RestClientException;
import com._4point.aem.docservices.rest_services.client.helpers.AemConfig;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

@WireMockTest
class JerseyRestClientTest {
	private static final String FIELD1_DATA = "field1 data";
	private static final String FIELD1_NAME = "field1";
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

	@Test
	void testPostToServer_NoContentResponse() throws Exception {
		// Given
		stubFor(post(ENDPOINT).willReturn(noContent()));
		
		// When
		Optional<Response> result = performPostToServer();

		// Then
		assertTrue(result.isEmpty());

		verify(postRequestedFor(urlEqualTo(ENDPOINT))
				.withAllRequestBodyParts(aMultipart(FIELD1_NAME).withBody(equalTo(FIELD1_DATA)))
				);
	}

	@Test
	void testPostToServer_DocumentResponseNoHeader() throws Exception {
		// Given
		stubFor(post(ENDPOINT).willReturn(okForContentType(APPLICATION_PDF, MOCK_PDF_BYTES)));
		
		// When
		Response response = performPostToServer().orElseThrow();

		// Then
		assertEquals(APPLICATION_PDF, response.contentType());
		assertEquals(MOCK_PDF_BYTES, new String(response.data().readAllBytes()));
		assertTrue(response.retrieveHeader(SAMPLE_HEADER).isEmpty());
		verify(postRequestedFor(urlEqualTo(ENDPOINT))
				.withAllRequestBodyParts(aMultipart(FIELD1_NAME).withBody(equalTo(FIELD1_DATA)))
				);
	}

	@Test
	void testPostToServer_DocumentResponseWithHeader() throws Exception {
		// Given
		stubFor(post(ENDPOINT).willReturn(okForContentType(APPLICATION_PDF, MOCK_PDF_BYTES)
											.withHeader(SAMPLE_HEADER, SAMPLE_HEADER_VALUE)
										  ));
	
		// When
		Response response = performPostToServer().orElseThrow();

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
	
	private Optional<Response> performPostToServer() throws RestClientException, Exception {
		try (MultipartPayload payload = underTest.multipartPayloadBuilder()
										.add(FIELD1_NAME, FIELD1_DATA)
										.build()) {
			
			return payload.postToServer(APPLICATION_PDF);
		}
	}
}
