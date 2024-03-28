package com._4point.aem.docservices.rest_services.client.pdfUtility;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.MatcherAssert.assertThat; 
import static org.hamcrest.Matchers.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com._4point.aem.docservices.rest_services.client.RestClient;
import com._4point.aem.docservices.rest_services.client.RestClient.ContentType;
import com._4point.aem.docservices.rest_services.client.RestClient.MultipartPayload;
import com._4point.aem.docservices.rest_services.client.RestClient.Response;
import com._4point.aem.docservices.rest_services.client.helpers.AemConfig;
import com._4point.aem.docservices.rest_services.client.helpers.BuilderImpl.TriFunction;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;
import com.adobe.fd.pdfutility.services.client.PDFPropertiesOptionSpec;
import com.adobe.fd.pdfutility.services.client.RedactionOptionSpec;

@ExtendWith(MockitoExtension.class)
class RestServicesPdfUtilityServiceAdapterTest {
	private final static Document DUMMY_DOC = MockDocumentFactory.GLOBAL_DUMMY_DOCUMENT;
	private static final String CORRELATION_ID = "correlationId";


	@Mock(stubOnly = true) TriFunction<AemConfig, String, Supplier<String>, RestClient> mockClientFactory;
	@Mock(stubOnly = true) RestClient mockClient;
	@Mock(stubOnly = true) MultipartPayload mockPayload;
	@Mock(stubOnly = true) MultipartPayload.Builder mockPayloadBuilder;
	@Mock(stubOnly = true) Response mockResponse;

	@Captor ArgumentCaptor<AemConfig> aemConfig;
	@Captor ArgumentCaptor<String> servicePath;
	@Captor ArgumentCaptor<InputStream> postBodyBytes;
	@Captor ArgumentCaptor<ContentType> acceptableCntentType;
	@Captor ArgumentCaptor<Supplier<String>> correlationIdFn;

	@BeforeEach
	void setup() {
		when(mockClientFactory.apply(aemConfig.capture(), servicePath.capture(), correlationIdFn.capture())).thenReturn(mockClient);
	}

	@Test
	void testConvertPDFtoXDP() throws Exception {
		// Given
		byte[] responseData = "response Document Data".getBytes();
		ContentType expectedContentType = ContentType.APPLICATION_XDP;
		setUpMocks(responseData, expectedContentType);
		
		// When
		RestServicesPdfUtilityServiceAdapter underTest = RestServicesPdfUtilityServiceAdapter.builder(mockClientFactory)
				.correlationId(()->CORRELATION_ID)
				.build();
		Document result = underTest.convertPDFtoXDP(DUMMY_DOC);

		// Then
		// Make sure the correct URL is called.
		assertThat("Expected target url contains 'PdfUtility' and 'ConvertPdfToXdp'", servicePath.getValue(), allOf(containsString("PdfUtility"), containsString("ConvertPdfToXdp")));

		// Make sure the correct data was posted.
		assertArrayEquals(DUMMY_DOC.getInputStream().readAllBytes(), postBodyBytes.getValue().readAllBytes());
		assertEquals(expectedContentType, acceptableCntentType.getValue());
		
		// Make sure the response is returned transparently in the returned Document.
		assertArrayEquals(responseData, result.getInputStream().readAllBytes());
		assertEquals(expectedContentType.contentType(), result.getContentType());

		// Make sure correlation ID fn was passed correctly.
		assertEquals(CORRELATION_ID, correlationIdFn.getValue().get());

	}

	@Test
	void testCloneDocument() {
		RestServicesPdfUtilityServiceAdapter underTest = RestServicesPdfUtilityServiceAdapter.builder(mockClientFactory).build();
		UnsupportedOperationException ex = assertThrows(UnsupportedOperationException.class, ()->underTest.clone(DUMMY_DOC));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, allOf(containsString("clone"), containsString("is not supported as a remote operation")));
	}

	@Test
	void testConvertPDFtoXDP_nullArguments() {
		when(mockClient.multipartPayloadBuilder()).thenReturn(mockPayloadBuilder);
		RestServicesPdfUtilityServiceAdapter underTest = RestServicesPdfUtilityServiceAdapter.builder(mockClientFactory).build();
		
		NullPointerException ex = assertThrows(NullPointerException.class, ()->underTest.convertPDFtoXDP(null));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, allOf(containsString("document"),containsString("parameter cannot be null")));
	}

	@Test
	void testGetPDFProperties() {
		PDFPropertiesOptionSpec pdfPropertiesOptionSpec = Mockito.mock(PDFPropertiesOptionSpec.class);
		RestServicesPdfUtilityServiceAdapter underTest = RestServicesPdfUtilityServiceAdapter.builder(mockClientFactory).build();
		UnsupportedOperationException ex = assertThrows(UnsupportedOperationException.class, ()->underTest.getPDFProperties(DUMMY_DOC, pdfPropertiesOptionSpec ));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, allOf(containsString("getPDFProperties"), containsString("is not implemented yet")));
	}

	@Test
	void testMulticlone() {
		RestServicesPdfUtilityServiceAdapter underTest = RestServicesPdfUtilityServiceAdapter.builder(mockClientFactory).build();
		UnsupportedOperationException ex = assertThrows(UnsupportedOperationException.class, ()->underTest.multiclone(DUMMY_DOC, 2));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, allOf(containsString("multiclone"), containsString("is not supported as a remote operation")));
	}

	@Test
	void testRedact() {
		RedactionOptionSpec redactOptSpec = Mockito.mock(RedactionOptionSpec.class);
		RestServicesPdfUtilityServiceAdapter underTest = RestServicesPdfUtilityServiceAdapter.builder(mockClientFactory).build();
		UnsupportedOperationException ex = assertThrows(UnsupportedOperationException.class, ()->underTest.redact(DUMMY_DOC, redactOptSpec));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, allOf(containsString("redact"), containsString("is not implemented yet")));
	}

	@Test
	void testSanitize() {
		RestServicesPdfUtilityServiceAdapter underTest = RestServicesPdfUtilityServiceAdapter.builder(mockClientFactory).build();
		UnsupportedOperationException ex = assertThrows(UnsupportedOperationException.class, ()->underTest.sanitize(DUMMY_DOC));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, allOf(containsString("sanitize"), containsString("is not implemented yet")));
	}

	private void setUpMocks(byte[] responseData, ContentType expectedContentType) throws Exception {
		when(mockClient.multipartPayloadBuilder()).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.add(eq("document"), postBodyBytes.capture(), eq(ContentType.APPLICATION_PDF))).thenReturn(mockPayloadBuilder);
		when(mockPayloadBuilder.build()).thenReturn(mockPayload);
		when(mockPayload.postToServer(acceptableCntentType.capture())).thenReturn(Optional.of(mockResponse));
		when(mockResponse.contentType()).thenReturn(expectedContentType);
		when(mockResponse.data()).thenReturn(new ByteArrayInputStream(responseData));
	}
}
