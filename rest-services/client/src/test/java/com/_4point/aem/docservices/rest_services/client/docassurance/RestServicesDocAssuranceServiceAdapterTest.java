package com._4point.aem.docservices.rest_services.client.docassurance;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com._4point.aem.docservices.rest_services.client.helpers.AemServerType;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.docassurance.EncryptionOptions;
import com._4point.aem.fluentforms.api.docassurance.ReaderExtensionOptions;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;
import com.adobe.fd.docassurance.client.api.SignatureOptions;
import com.adobe.fd.signatures.pdf.inputs.UnlockOptions;

@ExtendWith(MockitoExtension.class)
public class RestServicesDocAssuranceServiceAdapterTest {

	private static final String CORRELATION_ID_HTTP_HDR = "X-Correlation-ID";
	private static final String CORRELATION_ID = "correlationId";
	private static final String TEST_MACHINE_NAME = "testmachinename";
	private static final int TEST_MACHINE_PORT = 8080;

	private static final MediaType APPLICATION_PDF = new MediaType("application", "pdf");
	
	@Mock(answer = Answers.RETURNS_SELF) Client client;	// answers used to mock Client's fluent interface. 
	@Mock WebTarget target;
	@Mock Response response;
	@Mock Builder builder;
	@Mock StatusType statusType;
	
	@Captor ArgumentCaptor<String> machineName;
	@Captor ArgumentCaptor<String> path;
	@SuppressWarnings("rawtypes")
	@Captor ArgumentCaptor<Entity> entity;
	@Captor ArgumentCaptor<String> correlationId;

	RestServicesDocAssuranceServiceAdapter underTest;
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@Disabled
	void testExportData() {
		fail("Not yet implemented");
	}

	private enum HappyPaths { SSL, NO_SSL };
	
	@ParameterizedTest
	@EnumSource(HappyPaths.class)
	void testSecureDocument_noSsl(HappyPaths codePath) throws Exception {
		Document responseData = MockDocumentFactory.GLOBAL_INSTANCE.create("response Document Data".getBytes());

		// TODO: Change this based on https://maciejwalkowiak.com/mocking-fluent-interfaces/
		when(client.target(machineName.capture())).thenReturn(target);
		when(target.path(path.capture())).thenReturn(target);
		when(target.request()).thenReturn(builder);
		when(builder.accept(APPLICATION_PDF)).thenReturn(builder);
		when(builder.post(entity.capture())).thenReturn(response);
		when(response.getStatusInfo()).thenReturn(statusType);
		when(statusType.getFamily()).thenReturn(Response.Status.Family.SUCCESSFUL);	// return Successful
		when(response.hasEntity()).thenReturn(true);
		when(response.getEntity()).thenReturn(new ByteArrayInputStream(responseData.getInlineData()));
		when(response.getHeaderString(HttpHeaders.CONTENT_TYPE)).thenReturn("application/pdf");
		
		boolean useSSL = false;
		boolean useCorrelationId = false;
		switch (codePath) {
		case SSL:
			useSSL = true;
			useCorrelationId = true;
			when(builder.header(eq(CORRELATION_ID_HTTP_HDR), correlationId.capture())).thenReturn(builder);
			break;
		case NO_SSL:
			useSSL = false;
			useCorrelationId = false;
			break;
		default:
			throw new IllegalStateException("Found unexpected HappyPaths value (" + codePath.toString() + ").");
		}
		
		com._4point.aem.docservices.rest_services.client.docassurance.RestServicesDocAssuranceServiceAdapter.DocAssuranceServiceBuilder adapterBuilder = RestServicesDocAssuranceServiceAdapter.builder()
						.machineName(TEST_MACHINE_NAME)
						.port(TEST_MACHINE_PORT)
						.basicAuthentication("username", "password")
						.useSsl(useSSL)
						.aemServerType(AemServerType.StandardType.JEE)
						.clientFactory(()->client);
		if (useCorrelationId) {
			adapterBuilder.correlationId(()->CORRELATION_ID);
		}
		
		underTest = adapterBuilder
						.build();
				
		Document pdf = MockDocumentFactory.GLOBAL_INSTANCE.create("pdf Document Data".getBytes());
		EncryptionOptions eOptions = null;
		SignatureOptions sOptions = null;
		UnlockOptions uOptions = null;
		ReaderExtensionOptions reOptions = Mockito.mock(ReaderExtensionOptions.class);
		Document pdfResult = underTest.secureDocument(pdf, eOptions, sOptions, reOptions, uOptions);
		
		// Make sure the correct URL is called.
		final String expectedPrefix = useSSL ? "https://" : "http://";
		assertThat("Expected target url contains '" + expectedPrefix + "'", machineName.getValue(), containsString(expectedPrefix));
		assertThat("Expected target url contains TEST_MACHINE_NAME", machineName.getValue(), containsString(TEST_MACHINE_NAME));
		assertThat("Expected target url contains TEST_MACHINE_PORT", machineName.getValue(), containsString(Integer.toString(TEST_MACHINE_PORT)));
		assertThat("Expected target url contains 'SecureDocument'", path.getValue(), containsString("SecureDocument"));

		// Make sure that the arguments we passed in are transmitted correctly.
		@SuppressWarnings("unchecked")
		Entity<FormDataMultiPart> postedEntity = (Entity<FormDataMultiPart>)entity.getValue();
		FormDataMultiPart postedData = postedEntity.getEntity();
		
		assertEquals(MediaType.MULTIPART_FORM_DATA_TYPE, postedEntity.getMediaType());
		validateDocumentFormField(postedData, "inDoc", new MediaType("application", "pdf"), pdf.getInlineData());
		
		if (useCorrelationId) {
			assertEquals(CORRELATION_ID, correlationId.getValue());
		}
		
		// Make sure the response is correct.
		assertArrayEquals(responseData.getInlineData(), pdfResult.getInlineData());
		assertEquals(APPLICATION_PDF, MediaType.valueOf(pdfResult.getContentType()));
	}
	
	private void validateDocumentFormField(FormDataMultiPart postedData, String fieldName, MediaType expectedMediaType, byte[] expectedData) throws IOException {
		List<FormDataBodyPart> pdfFields = postedData.getFields(fieldName);
		assertEquals(1, pdfFields.size());
		
		FormDataBodyPart pdfPart = pdfFields.get(0);
		assertEquals(expectedMediaType, pdfPart.getMediaType());
		byte[] pdfBytes = IOUtils.toByteArray((InputStream) pdfPart.getEntity());
		assertArrayEquals(expectedData, pdfBytes);  // TODO: Need to figure out how to test for entity.
	}

	@Test
	void testSecureDocument_SuccessButNoEntity() throws Exception {

		when(client.target(machineName.capture())).thenReturn(target);
		when(target.path(path.capture())).thenReturn(target);
		when(target.request()).thenReturn(builder);
		when(builder.accept(APPLICATION_PDF)).thenReturn(builder);
		when(builder.post(entity.capture())).thenReturn(response);
		when(response.getStatusInfo()).thenReturn(statusType);
		when(statusType.getFamily()).thenReturn(Response.Status.Family.SUCCESSFUL);	// return Successful
		
		underTest = RestServicesDocAssuranceServiceAdapter.builder()
				.machineName(TEST_MACHINE_NAME)
				.port(TEST_MACHINE_PORT)
				.basicAuthentication("username", "password")
				.useSsl(false)
				.clientFactory(()->client)
				.build();
		
		
		Document pdf = MockDocumentFactory.GLOBAL_INSTANCE.create("pdf Document Data".getBytes());

		Exception ex = assertThrows(Exception.class, ()->underTest.secureDocument(pdf, null, null, null, null));
		assertThat(ex.getMessage(), containsString("should never happen"));
	}

	@Test
	void testImportData_FailureWithHTMLResponse() throws Exception {
		final String SAMPLE_HTML_RESPONSE = "<html><head><title>Content modified /services/DocAssuranceService/SecureDocument</title></head><body><h1>Content modified /services/DocAssuranceService/SecureDocument</h1></body></html>";
		final String HTML_CONTENT_TYPE = "text/html;charset=utf-8";

		when(client.target(machineName.capture())).thenReturn(target);
		when(target.path(path.capture())).thenReturn(target);
		when(target.request()).thenReturn(builder);
		when(builder.accept(APPLICATION_PDF)).thenReturn(builder);
		when(builder.post(entity.capture())).thenReturn(response);
		when(response.getStatusInfo()).thenReturn(statusType);
		when(statusType.getFamily()).thenReturn(Response.Status.Family.SUCCESSFUL);	// return Successful
		when(response.hasEntity()).thenReturn(true);
		when(response.getEntity()).thenReturn(new ByteArrayInputStream(SAMPLE_HTML_RESPONSE.getBytes()));
		when(response.getHeaderString(HttpHeaders.CONTENT_TYPE)).thenReturn(HTML_CONTENT_TYPE);
		
		underTest = RestServicesDocAssuranceServiceAdapter.builder()
				.machineName(TEST_MACHINE_NAME)
				.port(TEST_MACHINE_PORT)
				.basicAuthentication("username", "password")
				.useSsl(false)
				.clientFactory(()->client)
				.build();
		
		
		Document pdf = MockDocumentFactory.GLOBAL_INSTANCE.create("pdf Document Data".getBytes());

		Exception ex = assertThrows(Exception.class, ()->underTest.secureDocument(pdf, null, null, null, null));
		assertThat(ex.getMessage(), containsString("was not a PDF"));
		assertThat(ex.getMessage(), containsString(HTML_CONTENT_TYPE));
	}


}
