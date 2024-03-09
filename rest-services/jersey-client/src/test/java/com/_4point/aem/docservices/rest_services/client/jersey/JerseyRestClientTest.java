package com._4point.aem.docservices.rest_services.client.jersey;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.io.ByteArrayInputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com._4point.aem.docservices.rest_services.client.RestClient;
import com._4point.aem.docservices.rest_services.client.RestClient.ContentType;
import com._4point.aem.docservices.rest_services.client.RestClient.GetRequest;
import com._4point.aem.docservices.rest_services.client.RestClient.MultipartPayload;
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
	private static final String ENDPOINT = "/services/OutputService/GeneratePdfOutput";
	private static final String ERROR_BODY_TEXT = "Error Body";

	private AemConfig aemConfig;
	private RestClient underTest;
	
	@BeforeEach
	void setup(WireMockRuntimeInfo wmRuntimeInfo) {
		aemConfig = AemConfig.builder()
				   .port(wmRuntimeInfo.getHttpPort())
				   .build();
		
		underTest = new JerseyRestClient(aemConfig, ENDPOINT);
	}

	/*
	 * 
	 * PostToServer Tests
	 * 
	 * 
	 */
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
		stubFor(post(ENDPOINT).willReturn(okForContentType(ContentType.APPLICATION_PDF.contentType(), MOCK_PDF_BYTES)));
		
		// When
		Response response = performPostToServer(FIELD1_NAME, FIELD1_DATA, FIELD2_NAME, FIELD2_DATA, "foo", "BAR").orElseThrow();

		// Then
		assertEquals(ContentType.APPLICATION_PDF, response.contentType());
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
		stubFor(post(ENDPOINT).willReturn(okForContentType(ContentType.APPLICATION_PDF.contentType(), MOCK_PDF_BYTES)
											.withHeader(SAMPLE_HEADER, SAMPLE_HEADER_VALUE)
										  ));
	
		// When
		Response response = performPostToServer(FIELD1_NAME, FIELD1_DATA).orElseThrow();

		// Then
		assertEquals(ContentType.APPLICATION_PDF, response.contentType());
		assertEquals(MOCK_PDF_BYTES, new String(response.data().readAllBytes()));
		assertEquals(SAMPLE_HEADER_VALUE, response.retrieveHeader(SAMPLE_HEADER).orElseThrow());
		verify(postRequestedFor(urlEqualTo(ENDPOINT))
				.withAllRequestBodyParts(aMultipart(FIELD1_NAME).withBody(equalTo(FIELD1_DATA)))
				);
	}

	@DisplayName("PostToServer with 1 part using byte array data")
	@Test
	void testPostToServer_DocumentResponseFromByteArray() throws Exception {
		// Given
		stubFor(post(ENDPOINT).willReturn(okForContentType(ContentType.APPLICATION_PDF.contentType(), MOCK_PDF_BYTES)
											.withHeader(SAMPLE_HEADER, SAMPLE_HEADER_VALUE)
										  ));
	
		// When
		Response response = performPostToServer(FIELD1_NAME, FIELD1_DATA.getBytes(), ContentType.APPLICATION_PDF).orElseThrow();

		// Then
		assertEquals(ContentType.APPLICATION_PDF, response.contentType());
		assertEquals(MOCK_PDF_BYTES, new String(response.data().readAllBytes()));
		assertEquals(SAMPLE_HEADER_VALUE, response.retrieveHeader(SAMPLE_HEADER).orElseThrow());
		verify(postRequestedFor(urlEqualTo(ENDPOINT))
				.withAllRequestBodyParts(aMultipart(FIELD1_NAME).withBody(equalTo(FIELD1_DATA))
																.withHeader("content-type", equalTo(ContentType.APPLICATION_PDF.contentType()))
										)
				);
	}

	@DisplayName("PostToServer with 1 part using InputStream data")
	@Test
	void testPostToServer_DocumentResponseFromInputStream() throws Exception {
		// Given
		stubFor(post(ENDPOINT).willReturn(okForContentType(ContentType.APPLICATION_PDF.contentType(), MOCK_PDF_BYTES)
											.withHeader(SAMPLE_HEADER, SAMPLE_HEADER_VALUE)
										  ));
	
		// When
		Response response = performPostToServer(FIELD1_NAME, new ByteArrayInputStream(FIELD1_DATA.getBytes()), ContentType.TEXT_HTML).orElseThrow();

		// Then
		assertEquals(ContentType.APPLICATION_PDF, response.contentType());
		assertEquals(MOCK_PDF_BYTES, new String(response.data().readAllBytes()));
		assertEquals(SAMPLE_HEADER_VALUE, response.retrieveHeader(SAMPLE_HEADER).orElseThrow());
		verify(postRequestedFor(urlEqualTo(ENDPOINT))
				.withAllRequestBodyParts(aMultipart(FIELD1_NAME).withBody(equalTo(FIELD1_DATA))
																.withHeader("content-type", equalTo(ContentType.TEXT_HTML.contentType()))
										)
				);
	}

	@DisplayName("When AEM returns 500 Internal Server error with no body, postToServer should throw RestClientException.")
	@Test
	void testPostToServer_AemReturns500NoBody() throws Exception {
		// Given
		stubFor(post(ENDPOINT).willReturn(serverError()));

		// When
		RestClientException ex = assertThrows(RestClientException.class,()->performPostToServer(FIELD1_NAME, FIELD1_DATA));
		String msg = ex.getMessage();
		assertNotNull(msg);

		// Then
		assertThat(msg, allOf(
				containsString("Call to server failed"),
				containsString("500"),
				containsString("Server Error"),
				not(containsString(ERROR_BODY_TEXT))
				));
	}

	@DisplayName("When AEM returns 500 Internal Server error with body, postToServer should throw RestClientException containing body.")
	@Test
	void testPostToServer_AemReturns500WithBody() throws Exception {
		// Given
		stubFor(post(ENDPOINT).willReturn(serverError().withBody(ERROR_BODY_TEXT.getBytes())));

		// When
		RestClientException ex = assertThrows(RestClientException.class,()->performPostToServer(FIELD1_NAME, FIELD1_DATA));
		String msg = ex.getMessage();
		assertNotNull(msg);

		// Then
		assertThat(msg, allOf(
				containsString("Call to server failed"),
				containsString("500"),
				containsString("Server Error"),
				containsString(ERROR_BODY_TEXT)
				));
	}

	@DisplayName("When AEM returns 200 with no entity, postToServer should throw RestClientException.")
	@Test
	void testPostToServer_AemReturnsNoEntity() throws Exception {
		// Given
		stubFor(post(ENDPOINT).willReturn(ok()));

		// When
		RestClientException ex = assertThrows(RestClientException.class,()->performPostToServer(FIELD1_NAME, FIELD1_DATA));
		String msg = ex.getMessage();
		assertNotNull(msg);
		
		// Then
		assertThat(msg, allOf(
				containsString("Call to server succeeded"),
				containsString("server failed to return content")
				));

	}

	@DisplayName("When AEM returns incompatible content type, postToServer should throw RestClientException.")
	@Test
	void testPostToServer_AemReturnsWrongContent() throws Exception {
		// Given
		stubFor(post(ENDPOINT).willReturn(okForContentType(ContentType.TEXT_HTML.contentType(), MOCK_PDF_BYTES)));

		// When
		RestClientException ex = assertThrows(RestClientException.class,()->performPostToServer(FIELD1_NAME, FIELD1_DATA));
		String msg = ex.getMessage();
		assertNotNull(msg);
		
		// Then
		assertThat(msg, allOf(
				containsString("Response from AEM server was not of expected type"),
				containsString(ContentType.APPLICATION_PDF.contentType()),
				containsString(ContentType.TEXT_HTML.contentType())
				));

	}
	
	@DisplayName("When AEM returns no content type, postToServer should throw RestClientException.")
	@Test
	void testPostToServer_AemReturnsNoContent() throws Exception {
		// Given
		stubFor(post(ENDPOINT).willReturn(ok(MOCK_PDF_BYTES)));

		// When
		RestClientException ex = assertThrows(RestClientException.class,()->performPostToServer(FIELD1_NAME, FIELD1_DATA));
		String msg = ex.getMessage();
		assertNotNull(msg);
		
		// Then
		assertThat(msg, allOf(
				containsString("Response from AEM server was not of expected type"),
				containsString(ContentType.APPLICATION_PDF.contentType()),
				containsString("null")
				));

	}
	
	@DisplayName("RestClient must be able to handle multile endpoints.")
	@Test
	void testMultipleEndpoints() throws Exception {
		String endPoint1 = "/services/AssemberService/GenerateHtml";
		String mockHtmlBytes = "Mock HTML Content";
		String endPoint2 = "/services/FormsService/GeneratePdfForm";
		String mockPdfBytes = "Mock PDF Content";
		// Given
		stubFor(post(endPoint1).willReturn(okForContentType(ContentType.TEXT_HTML.contentType(), mockHtmlBytes)));
		stubFor(post(endPoint2).willReturn(okForContentType(ContentType.APPLICATION_PDF.contentType(), mockPdfBytes)));
		var client1 = new JerseyRestClient(aemConfig, endPoint1);
		var client2 = new JerseyRestClient(aemConfig, endPoint2);

		// When
		MultipartPayload.Builder builder1 = client1.multipartPayloadBuilder()
												   .add(FIELD1_NAME, FIELD1_DATA);
		MultipartPayload.Builder builder2 = client2.multipartPayloadBuilder()
				   				 				   .add(FIELD2_NAME, FIELD2_DATA);

		try (MultipartPayload payload1 = builder1.build(); MultipartPayload payload2 = builder2.build()) {
			var response1 = payload1.postToServer(ContentType.TEXT_HTML).orElseThrow();
			var response2 = payload2.postToServer(ContentType.APPLICATION_PDF).orElseThrow();
		
			// Then
			assertEquals(ContentType.TEXT_HTML, response1.contentType());
			assertEquals(mockHtmlBytes, new String(response1.data().readAllBytes()));
			verify(postRequestedFor(urlEqualTo(endPoint1))
					.withAllRequestBodyParts(aMultipart(FIELD1_NAME).withBody(equalTo(FIELD1_DATA)))
					);

			assertEquals(ContentType.APPLICATION_PDF, response2.contentType());
			assertEquals(mockPdfBytes, new String(response2.data().readAllBytes()));
			verify(postRequestedFor(urlEqualTo(endPoint2))
					.withAllRequestBodyParts(aMultipart(FIELD2_NAME).withBody(equalTo(FIELD2_DATA)))
					);
		}

	}
	
	
	
	// TODO:  Test that instantiates multiple JerseyRestClients and calls each to make sure that:
	// a) Lazy initialization of client works
	// b) Calling configureClient on the same client multiple times is OK.
	
	
	private Optional<Response> performPostToServer(String...strings) throws RestClientException, Exception {
		if (strings.length % 2 != 0) { 
			throw new IllegalArgumentException("Odd number of Strings passed in, must be even. (" + strings.length + ").");
		}
		
		MultipartPayload.Builder builder = underTest.multipartPayloadBuilder();
		for(int i = 0; i < strings.length; i+=2) {
			builder.add(strings[i], strings[i+1]);
		}
		
		try (MultipartPayload payload = builder.build()) {
			return payload.postToServer(ContentType.APPLICATION_PDF);
		}
	}

	private Optional<Response> performPostToServer(String fieldName, byte[] data, ContentType contentType) throws RestClientException, Exception {
		MultipartPayload.Builder builder = underTest.multipartPayloadBuilder()
								   .add(fieldName, data, contentType);
		
		try (MultipartPayload payload = builder.build()) {
			return payload.postToServer(ContentType.APPLICATION_PDF);
		}
	}
	
	private Optional<Response> performPostToServer(String fieldName, InputStream data, ContentType contentType) throws RestClientException, Exception {
		MultipartPayload.Builder builder = underTest.multipartPayloadBuilder()
								   .add(fieldName, data, contentType);
		
		try (MultipartPayload payload = builder.build()) {
			return payload.postToServer(ContentType.APPLICATION_PDF);
		}
	}
	
	/*
	 * 
	 * GetFromServer Tests
	 * 
	 * 
	 */
	
	@DisplayName("GetFromServer with 1 query parameter and return no content in response")
	@Test
	void testGetFromServer_NoContentResponse() throws Exception {
		// Given
		stubFor(get(urlPathEqualTo(ENDPOINT)).withQueryParam(FIELD1_NAME, equalTo(FIELD1_DATA)).willReturn(noContent()));
		
		// When
		Optional<Response> result = performGetFromServer(FIELD1_NAME, FIELD1_DATA);

		// Then
		assertTrue(result.isEmpty());

		verify(getRequestedFor(urlPathEqualTo(ENDPOINT))
				.withQueryParam(FIELD1_NAME, equalTo(FIELD1_DATA))
				);
	}

	@DisplayName("GetFromServer with 2 query parameters and return no headers in response")
	@Test
	void testGetFromServer_DocumentResponseNoHeader() throws Exception {
		// Given
		stubFor(get(urlPathEqualTo(ENDPOINT))
				    .withQueryParams(Map.of(FIELD1_NAME, equalTo(FIELD1_DATA), FIELD2_NAME, equalTo(FIELD2_DATA)))
				    .willReturn(okForContentType(ContentType.TEXT_HTML.contentType(), MOCK_PDF_BYTES)));
		
		// When
		Response response = performGetFromServer(FIELD1_NAME, FIELD1_DATA, FIELD2_NAME, FIELD2_DATA).orElseThrow();

		// Then
		assertEquals(ContentType.TEXT_HTML, response.contentType());
		assertEquals(MOCK_PDF_BYTES, new String(response.data().readAllBytes()));
		assertTrue(response.retrieveHeader(SAMPLE_HEADER).isEmpty());
		verify(getRequestedFor(urlPathEqualTo(ENDPOINT))
				.withQueryParam(FIELD1_NAME, equalTo(FIELD1_DATA))
				.withQueryParam(FIELD2_NAME, equalTo(FIELD2_DATA))
				);
	}

	@DisplayName("GetFromServer with no query parameters and return 1 header in response")
	@Test
	void testGetFromServer_DocumentResponseWithHeader() throws Exception {
		// Given
		stubFor(get(ENDPOINT).willReturn(okForContentType(ContentType.TEXT_HTML.contentType(), MOCK_PDF_BYTES)
											.withHeader(SAMPLE_HEADER, SAMPLE_HEADER_VALUE)
										  ));
	
		// When
		Response response = performGetFromServer().orElseThrow();

		// Then
		assertEquals(ContentType.TEXT_HTML, response.contentType());
		assertEquals(MOCK_PDF_BYTES, new String(response.data().readAllBytes()));
		assertEquals(SAMPLE_HEADER_VALUE, response.retrieveHeader(SAMPLE_HEADER).orElseThrow());
		verify(getRequestedFor(urlEqualTo(ENDPOINT))
				.withoutQueryParam(FIELD1_NAME)
				);
	}

	@DisplayName("When AEM returns 500 Internal Server error with no body, postToServer should throw RestClientException.")
	@Test
	void testGetFromServer_AemReturns500NoBody() throws Exception {
		// Given
		stubFor(get(urlPathEqualTo(ENDPOINT)).withQueryParam(FIELD1_NAME, equalTo(FIELD1_DATA)).willReturn(serverError()));

		// When
		RestClientException ex = assertThrows(RestClientException.class,()->performGetFromServer(FIELD1_NAME, FIELD1_DATA));
		String msg = ex.getMessage();
		assertNotNull(msg);

		// Then
		assertThat(msg, allOf(
				containsString("Call to server failed"),
				containsString("500"),
				containsString("Server Error"),
				not(containsString(ERROR_BODY_TEXT))
				));
	}

	@DisplayName("When AEM returns 500 Internal Server error with body, postToServer should throw RestClientException containing body.")
	@Test
	void testGetFromServer_AemReturns500WithBody() throws Exception {
		// Given
		stubFor(get(urlPathEqualTo(ENDPOINT)).withQueryParam(FIELD1_NAME, equalTo(FIELD1_DATA)).willReturn(serverError().withBody(ERROR_BODY_TEXT.getBytes())));

		// When
		RestClientException ex = assertThrows(RestClientException.class,()->performGetFromServer(FIELD1_NAME, FIELD1_DATA));
		String msg = ex.getMessage();
		assertNotNull(msg);

		// Then
		assertThat(msg, allOf(
				containsString("Call to server failed"),
				containsString("500"),
				containsString("Server Error"),
				containsString(ERROR_BODY_TEXT)
				));
	}

	@DisplayName("When AEM returns 200 with no entity, postToServer should throw RestClientException.")
	@Test
	void testGetFromServer_AemReturnsNoEntity() throws Exception {
		// Given
		stubFor(get(urlPathEqualTo(ENDPOINT)).withQueryParam(FIELD1_NAME, equalTo(FIELD1_DATA)).willReturn(ok()));

		// When
		RestClientException ex = assertThrows(RestClientException.class,()->performGetFromServer(FIELD1_NAME, FIELD1_DATA));
		String msg = ex.getMessage();
		assertNotNull(msg);
		
		// Then
		assertThat(msg, allOf(
				containsString("Call to server succeeded"),
				containsString("server failed to return content")
				));

	}

	@DisplayName("When AEM returns incompatible content type, postToServer should throw RestClientException.")
	@Test
	void testGetFromServer_AemReturnsWrongContent() throws Exception {
		// Given
		stubFor(get(urlPathEqualTo(ENDPOINT)).withQueryParam(FIELD1_NAME, equalTo(FIELD1_DATA)).willReturn(okForContentType(ContentType.APPLICATION_PDF.contentType(), MOCK_PDF_BYTES)));

		// When
		RestClientException ex = assertThrows(RestClientException.class,()->performGetFromServer(FIELD1_NAME, FIELD1_DATA));
		String msg = ex.getMessage();
		assertNotNull(msg);
		
		// Then
		assertThat(msg, allOf(
				containsString("Response from AEM server was not of expected type"),
				containsString(ContentType.APPLICATION_PDF.contentType()),
				containsString(ContentType.TEXT_HTML.contentType())
				));

	}
	
	@DisplayName("When AEM returns no content type, postToServer should throw RestClientException.")
	@Test
	void testGetFromServer_AemReturnsNoContent() throws Exception {
		// Given
		stubFor(get(urlPathEqualTo(ENDPOINT)).withQueryParam(FIELD1_NAME, equalTo(FIELD1_DATA)).willReturn(ok(MOCK_PDF_BYTES)));

		// When
		RestClientException ex = assertThrows(RestClientException.class,()->performGetFromServer(FIELD1_NAME, FIELD1_DATA));
		String msg = ex.getMessage();
		assertNotNull(msg);
		
		// Then
		assertThat(msg, allOf(
				containsString("Response from AEM server was not of expected type"),
				containsString(ContentType.TEXT_HTML.contentType()),
				containsString("null")
				));

	}
	
	private Optional<Response> performGetFromServer(String...strings) throws RestClientException, Exception {
		if (strings.length % 2 != 0) { 
			throw new IllegalArgumentException("Odd number of Strings passed in, must be even. (" + strings.length + ").");
		}
		
		GetRequest.Builder builder = underTest.getRequestBuilder();
		for(int i = 0; i < strings.length; i+=2) {
			builder.queryParam(strings[i], strings[i+1]);
		}
		
		GetRequest payload = builder.build();

		return payload.getFromServer(ContentType.TEXT_HTML);
	}
}
