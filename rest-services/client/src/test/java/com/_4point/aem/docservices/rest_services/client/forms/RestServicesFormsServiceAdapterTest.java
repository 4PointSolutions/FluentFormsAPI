package com._4point.aem.docservices.rest_services.client.forms;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.forms.FormsService.FormsServiceException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestServicesFormsServiceAdapterTest {

	private static final String TEST_MACHINE_NAME = "testmachinename";
	private static final int TEST_MACHINE_PORT = 8080;

	private static final MediaType APPLICATION_PDF = new MediaType("application", "pdf");
	
	@Mock Client client;
	@Mock WebTarget target;
	@Mock Response response;
	@Mock Builder builder;
	
	@Captor ArgumentCaptor<String> machineName;
	@Captor ArgumentCaptor<String> path;
	
	RestServicesFormsServiceAdapter underTest;
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@Disabled
	void testExportData() {
		fail("Not yet implemented");
	}

	@Test
	void testImportData_noSsl() throws Exception {

		when(client.target(machineName.capture())).thenReturn(target);
		when(target.path(path.capture())).thenReturn(target);
		when(target.request()).thenReturn(builder);
		when(builder.accept(APPLICATION_PDF)).thenReturn(builder);
		when(builder.post(any())).thenReturn(response);
		
		underTest = new RestServicesFormsServiceAdapter(TEST_MACHINE_NAME, TEST_MACHINE_PORT, false, ()->client);
		
		Document pdf = Mockito.mock(Document.class);
		Document data = Mockito.mock(Document.class);

		Document pdfResult = underTest.importData(pdf, data);
		
		// Make sure the correct URL is called.
		assertThat("Expected target url contains 'http://'", machineName.getValue(), containsString("http://"));
		assertThat("Expected target url contains TEST_MACHINE_NAME", machineName.getValue(), containsString(TEST_MACHINE_NAME));
		assertThat("Expected target url contains TEST_MACHINE_PORT", machineName.getValue(), containsString(Integer.toString(TEST_MACHINE_PORT)));
		assertThat("Expected target url contains 'ImportData'", path.getValue(), containsString("ImportData"));

		// TODO: Make sure that the arguments we passed in are transmitted correctly.
		
		// TODO: Make sure the response is handled correctly.
	}
	
	@Disabled
	void testRenderPDFForm() {
		fail("Not yet implemented");
	}

	@Disabled
	void testValidate() {
		fail("Not yet implemented");
	}

}
