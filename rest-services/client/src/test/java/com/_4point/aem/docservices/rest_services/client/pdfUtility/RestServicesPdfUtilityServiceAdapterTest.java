package com._4point.aem.docservices.rest_services.client.pdfUtility;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat; 
import static org.hamcrest.Matchers.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;
import com.adobe.fd.pdfutility.services.client.PDFPropertiesOptionSpec;
import com.adobe.fd.pdfutility.services.client.RedactionOptionSpec;

@ExtendWith(MockitoExtension.class)
class RestServicesPdfUtilityServiceAdapterTest {
	private final static Document DUMMY_DOC = MockDocumentFactory.GLOBAL_DUMMY_DOCUMENT;
	private static final MediaType APPLICATION_XDP = new MediaType("application", "vnd.adobe.xdp+xml");

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

	@Test
	void testConvertPDFtoXDP() throws Exception {
		Document resultDoc = MockDocumentFactory.GLOBAL_INSTANCE.create("response Document Data".getBytes());
		setUpMocks(resultDoc);
		RestServicesPdfUtilityServiceAdapter underTest = RestServicesPdfUtilityServiceAdapter.builder().clientFactory(()->client).build();
		Document result = underTest.convertPDFtoXDP(DUMMY_DOC);

		// Make sure the correct URL is called.
		assertThat("Expected target url contains 'PdfUtility' and 'ConvertPdfToXdp'", path.getValue(), allOf(containsString("PdfUtility"), containsString("ConvertPdfToXdp")));

		// Make sure that the arguments we passed in are transmitted correctly.
		@SuppressWarnings("unchecked")
		Entity<FormDataMultiPart> postedEntity = (Entity<FormDataMultiPart>)entity.getValue();
		FormDataMultiPart postedData = postedEntity.getEntity();
		
		assertEquals(MediaType.MULTIPART_FORM_DATA_TYPE, postedEntity.getMediaType());
		validateDocumentFormField(postedData, "document", new MediaType("application", "pdf"), DUMMY_DOC.getInlineData());

		// Make sure the response is correct.
		assertArrayEquals(resultDoc.getInlineData(), result.getInlineData());
		assertEquals(APPLICATION_XDP, MediaType.valueOf(result.getContentType()));
	}

	@Test
	void testCloneDocument() {
		RestServicesPdfUtilityServiceAdapter underTest = RestServicesPdfUtilityServiceAdapter.builder().build();
		UnsupportedOperationException ex = assertThrows(UnsupportedOperationException.class, ()->underTest.clone(DUMMY_DOC));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, allOf(containsString("clone"), containsString("is not supported as a remote operation")));
	}

	@Test
	void testConvertPDFtoXDP_nullArguments() {
		RestServicesPdfUtilityServiceAdapter underTest = RestServicesPdfUtilityServiceAdapter.builder().build();
		
		NullPointerException ex = assertThrows(NullPointerException.class, ()->underTest.convertPDFtoXDP(null));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, allOf(containsString("document"),containsString("parameter cannot be null")));
	}

	@Test
	void testGetPDFProperties() {
		PDFPropertiesOptionSpec pdfPropertiesOptionSpec = Mockito.mock(PDFPropertiesOptionSpec.class);
		RestServicesPdfUtilityServiceAdapter underTest = RestServicesPdfUtilityServiceAdapter.builder().build();
		UnsupportedOperationException ex = assertThrows(UnsupportedOperationException.class, ()->underTest.getPDFProperties(DUMMY_DOC, pdfPropertiesOptionSpec ));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, allOf(containsString("getPDFProperties"), containsString("is not implemented yet")));
	}

	@Test
	void testMulticlone() {
		RestServicesPdfUtilityServiceAdapter underTest = RestServicesPdfUtilityServiceAdapter.builder().build();
		UnsupportedOperationException ex = assertThrows(UnsupportedOperationException.class, ()->underTest.multiclone(DUMMY_DOC, 2));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, allOf(containsString("multiclone"), containsString("is not supported as a remote operation")));
	}

	@Test
	void testRedact() {
		RedactionOptionSpec redactOptSpec = Mockito.mock(RedactionOptionSpec.class);
		RestServicesPdfUtilityServiceAdapter underTest = RestServicesPdfUtilityServiceAdapter.builder().build();
		UnsupportedOperationException ex = assertThrows(UnsupportedOperationException.class, ()->underTest.redact(DUMMY_DOC, redactOptSpec));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, allOf(containsString("redact"), containsString("is not implemented yet")));
	}

	@Test
	void testSanitize() {
		RestServicesPdfUtilityServiceAdapter underTest = RestServicesPdfUtilityServiceAdapter.builder().build();
		UnsupportedOperationException ex = assertThrows(UnsupportedOperationException.class, ()->underTest.sanitize(DUMMY_DOC));
		String msg = ex.getMessage();
		assertNotNull(msg);
		assertThat(msg, allOf(containsString("sanitize"), containsString("is not implemented yet")));
	}

	private void setUpMocks(Document responseData) throws IOException {
		// TODO: Change this based on https://maciejwalkowiak.com/mocking-fluent-interfaces/
		when(client.target(machineName.capture())).thenReturn(target);
		when(target.path(path.capture())).thenReturn(target);
		when(target.request()).thenReturn(builder);
		when(builder.accept(APPLICATION_XDP)).thenReturn(builder);
		when(builder.post(entity.capture())).thenReturn(response);
		when(response.getStatusInfo()).thenReturn(statusType);
		when(statusType.getFamily()).thenReturn(Response.Status.Family.SUCCESSFUL);	// return Successful
		when(response.hasEntity()).thenReturn(true);
		when(response.getEntity()).thenReturn(new ByteArrayInputStream(responseData.getInlineData()));
		when(response.getHeaderString(HttpHeaders.CONTENT_TYPE)).thenReturn("application/vnd.adobe.xdp+xml");
	}
	
	private void validateDocumentFormField(FormDataMultiPart postedData, String fieldName, MediaType expectedMediaType, byte[] expectedData) throws IOException {
		List<FormDataBodyPart> pdfFields = postedData.getFields(fieldName);
		assertEquals(1, pdfFields.size());
		
		FormDataBodyPart pdfPart = pdfFields.get(0);
		assertEquals(expectedMediaType, pdfPart.getMediaType());
		byte[] pdfBytes = IOUtils.toByteArray((InputStream) pdfPart.getEntity());
		assertArrayEquals(expectedData, pdfBytes);  // TODO: Need to figure out how to test for entity.
	}

}
