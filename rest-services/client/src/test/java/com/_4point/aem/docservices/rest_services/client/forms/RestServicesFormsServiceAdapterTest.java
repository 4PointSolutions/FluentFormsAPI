package com._4point.aem.docservices.rest_services.client.forms;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.forms.FormsService.FormsServiceException;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;

import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class RestServicesFormsServiceAdapterTest {

	private static final String TEST_MACHINE_NAME = "testmachinename";
	private static final int TEST_MACHINE_PORT = 8080;

	private static final MediaType APPLICATION_PDF = new MediaType("application", "pdf");
	
	@Mock Client client;
	@Mock WebTarget target;
	@Mock Response response;
	@Mock Builder builder;
	@Mock StatusType statusType;
	
	@Captor ArgumentCaptor<String> machineName;
	@Captor ArgumentCaptor<String> path;
	@Captor ArgumentCaptor<Entity> entity;

	RestServicesFormsServiceAdapter underTest;
	
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
	void testImportData_noSsl(HappyPaths codePath) throws Exception {

		Document responseData = MockDocumentFactory.GLOBAL_INSTANCE.create("response Document Data".getBytes());
		
		when(client.target(machineName.capture())).thenReturn(target);
		when(target.path(path.capture())).thenReturn(target);
		when(target.request()).thenReturn(builder);
		when(builder.accept(APPLICATION_PDF)).thenReturn(builder);
		when(builder.post(entity.capture())).thenReturn(response);
		when(response.getStatusInfo()).thenReturn(statusType);
		when(statusType.getFamily()).thenReturn(Response.Status.Family.SUCCESSFUL);	// return Successful
		when(response.hasEntity()).thenReturn(true);
		when(response.getEntity()).thenReturn(new ByteArrayInputStream(responseData.getInlineData()));
		
		boolean useSSL = false;
		switch (codePath) {
		case SSL:
			useSSL = true;
			break;
		case NO_SSL:
			useSSL = false;
			break;
		default:
			throw new IllegalStateException("Found unexpected HappyPaths value (" + codePath.toString() + ").");
		}
		
		underTest = new RestServicesFormsServiceAdapter(TEST_MACHINE_NAME, TEST_MACHINE_PORT, useSSL, ()->client);
		
		Document pdf = MockDocumentFactory.GLOBAL_INSTANCE.create("pdf Document Data".getBytes());
		Document data = MockDocumentFactory.GLOBAL_INSTANCE.create("data Document Data".getBytes());

		Document pdfResult = underTest.importData(pdf, data);
		
		// Make sure the correct URL is called.
		final String expectedPrefix = useSSL ? "https://" : "http://";
		assertThat("Expected target url contains '" + expectedPrefix + "'", machineName.getValue(), containsString(expectedPrefix));
		assertThat("Expected target url contains TEST_MACHINE_NAME", machineName.getValue(), containsString(TEST_MACHINE_NAME));
		assertThat("Expected target url contains TEST_MACHINE_PORT", machineName.getValue(), containsString(Integer.toString(TEST_MACHINE_PORT)));
		assertThat("Expected target url contains 'ImportData'", path.getValue(), containsString("ImportData"));

		// Make sure that the arguments we passed in are transmitted correctly.
		@SuppressWarnings("unchecked")
		Entity<FormDataMultiPart> postedEntity = (Entity<FormDataMultiPart>)entity.getValue();
		FormDataMultiPart postedData = postedEntity.getEntity();
		
		assertEquals(MediaType.MULTIPART_FORM_DATA_TYPE, postedEntity.getMediaType());
		validateDocumentFormField(postedData, "pdf", new MediaType("application", "pdf"), pdf.getInlineData());
		validateDocumentFormField(postedData, "data", MediaType.APPLICATION_XML_TYPE, data.getInlineData());
		
		// Make sure the response is correct.
		assertArrayEquals(responseData.getInlineData(), pdfResult.getInlineData());
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
	void testImportData_SuccessButNoEntity() throws Exception {

		when(client.target(machineName.capture())).thenReturn(target);
		when(target.path(path.capture())).thenReturn(target);
		when(target.request()).thenReturn(builder);
		when(builder.accept(APPLICATION_PDF)).thenReturn(builder);
		when(builder.post(entity.capture())).thenReturn(response);
		when(response.getStatusInfo()).thenReturn(statusType);
		when(statusType.getFamily()).thenReturn(Response.Status.Family.SUCCESSFUL);	// return Successful
		
		underTest = new RestServicesFormsServiceAdapter(TEST_MACHINE_NAME, TEST_MACHINE_PORT, false, ()->client);
		
		Document pdf = MockDocumentFactory.GLOBAL_INSTANCE.create("pdf Document Data".getBytes());
		Document data = MockDocumentFactory.GLOBAL_INSTANCE.create("data Document Data".getBytes());

		FormsServiceException ex = assertThrows(FormsServiceException.class, ()->underTest.importData(pdf, data));
		assertThat(ex.getMessage(), containsString("should never happen"));
	}
	
	// TODO:  Add more importData tests for exceptional case (i.e. those cases where exceptions are thrown.
	
	@Disabled
	void testRenderPDFForm() {
		fail("Not yet implemented");
	}

	@Disabled
	void testValidate() {
		fail("Not yet implemented");
	}

}
