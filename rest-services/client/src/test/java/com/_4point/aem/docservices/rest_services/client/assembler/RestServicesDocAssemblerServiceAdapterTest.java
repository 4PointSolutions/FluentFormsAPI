package com._4point.aem.docservices.rest_services.client.assembler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com._4point.aem.docservices.rest_services.client.assembler.RestServicesDocAssemblerServiceAdapter.AssemblerServiceBuilder;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.assembler.AssemblerOptionsSpec;
import com._4point.aem.fluentforms.api.assembler.AssemblerResult;
import com._4point.aem.fluentforms.impl.assembler.AssemblerOptionsSpecImpl;
import com._4point.aem.fluentforms.testing.MockDocumentFactory;
import com.adobe.fd.assembler.client.OperationException;

@ExtendWith(MockitoExtension.class)
class RestServicesDocAssemblerServiceAdapterTest {
	private static final String CORRELATION_ID_HTTP_HDR = "X-Correlation-ID";
	private static final String CORRELATION_ID = "correlationId";
	private static final String TEST_MACHINE_NAME = "testmachinename";
	private static final int TEST_MACHINE_PORT = 8080;

	private static final MediaType APPLICATION_PDF = new MediaType("application", "pdf");
	private static final MediaType APPLICATION_XML = new MediaType("application", "xml");
	
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

	RestServicesDocAssemblerServiceAdapter underTest;
	private static final String POPULATED_ASSEMBLER_RESULT_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><assemblerResult><resultDocument documentName=\"concatenatedPDF.pdf\"><mergedDoc>dGVzdERvUG9zdCBIYXBweSBQYXRoIFJlc3VsdA==</mergedDoc></resultDocument><failedBlockNames><failedBlockName>failedBlock1</failedBlockName><failedBlockName>failedBlock2</failedBlockName></failedBlockNames><successfulDocumentNames><successfulDocumentName>successDocument1</successfulDocumentName><successfulDocumentName>successDocument2</successfulDocumentName></successfulDocumentNames><successfulBlockNames><successfulBlockName>successBlock1</successfulBlockName><successfulBlockName>successBlock2</successfulBlockName></successfulBlockNames><latestBatesNumber value=\"2\"/><numRequestedBlocks value=\"3\"/><multipleResultBlocks name=\"document\"><documentNames><documentName>test1</documentName><documentName>test2</documentName></documentNames></multipleResultBlocks><jobLog joblogValue=\"SU5GTw==\"/></assemblerResult>";
	private static final String EMPTY_ASSEMBLER_RESULT_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><assemblerResult><failedBlockNames/><successfulDocumentNames/><successfulBlockNames/><latestBatesNumber value=\"0\"/><numRequestedBlocks value=\"0\"/><jobLog/></assemblerResult>";

	@Test
	void testInvoke() throws Exception {
		Document responseData = MockDocumentFactory.GLOBAL_INSTANCE.create(POPULATED_ASSEMBLER_RESULT_XML.getBytes());

		setupRestClientMocks(true, responseData, APPLICATION_XML);

		AssemblerServiceBuilder assemblerServiceBuilder = RestServicesDocAssemblerServiceAdapter.builder()
								.machineName(TEST_MACHINE_NAME)
								.port(TEST_MACHINE_PORT)
								.basicAuthentication("username", "password")
								.useSsl(false)
								.correlationId(()->CORRELATION_ID)
								.clientFactory(()->client);
		
		underTest = assemblerServiceBuilder.build();
		
		AssemblerOptionsSpec adobeAssemblerOptionSpec = new AssemblerOptionsSpecImpl();
		Map<String, Object> sourceDocuments = 
				Collections.singletonMap("SourceDocument1", MockDocumentFactory.GLOBAL_INSTANCE.create("source Document Data".getBytes()));
		Document ddx = MockDocumentFactory.GLOBAL_INSTANCE.create("ddx Document Data".getBytes());
		
		AssemblerResult result = underTest.invoke(ddx, sourceDocuments, adobeAssemblerOptionSpec);
		
		validatePopulatedResult(result);
	}

	private void setupRestClientMocks(boolean setupCorrelationId, Document responseData, MediaType produces) throws IOException {
		// TODO: Change this based on https://maciejwalkowiak.com/mocking-fluent-interfaces/
		when(client.target(machineName.capture())).thenReturn(target);
		when(target.path(path.capture())).thenReturn(target);
		when(target.request()).thenReturn(builder);
		when(builder.accept(produces)).thenReturn(builder);
		when(builder.post(entity.capture())).thenReturn(response);
		when(response.getStatusInfo()).thenReturn(statusType);
		when(statusType.getFamily()).thenReturn(Response.Status.Family.SUCCESSFUL);	// return Successful
		when(response.hasEntity()).thenReturn(true);
		when(response.getEntity()).thenReturn(new ByteArrayInputStream(responseData.getInlineData()));
		when(response.getMediaType()).thenReturn(produces);

		if (setupCorrelationId) {
			when(builder.header(eq(CORRELATION_ID_HTTP_HDR), correlationId.capture())).thenReturn(builder);
		}
	}
	

	@Test
	void testConvertXmlToAssemblerResult() throws Exception {
		AssemblerResult result = RestServicesDocAssemblerServiceAdapter.convertXmlToAssemblerResult(new ByteArrayInputStream(POPULATED_ASSEMBLER_RESULT_XML.getBytes()));
		
		validatePopulatedResult(result);
	}

	private void validatePopulatedResult(AssemblerResult result) {
		Map<String, Document> documents = result.getDocuments();
		assertEquals(1, documents.size());
		assertTrue(documents.containsKey("concatenatedPDF.pdf"));
		
		List<String> failedBlockNames = result.getFailedBlockNames();
		assertEquals(2, failedBlockNames.size());
		assertEquals("failedBlock1", failedBlockNames.get(0));
		assertEquals("failedBlock2", failedBlockNames.get(1));
		
		Document jobLog = result.getJobLog();
		assertTrue(jobLog.isEmpty());
		
		int lastBatesNumber = result.getLastBatesNumber();
		assertEquals(2, lastBatesNumber);
		
		Map<String, List<String>> multipleResultsBlocks = result.getMultipleResultsBlocks();
		assertEquals(1, multipleResultsBlocks.size());
		List<String> multipleResultBlocksList = multipleResultsBlocks.get("document");
		assertEquals(2, multipleResultBlocksList.size());
		assertEquals("test1", multipleResultBlocksList.get(0));
		assertEquals("test2", multipleResultBlocksList.get(1));
		
		int numRequestedBlocks = result.getNumRequestedBlocks();
		assertEquals(3, numRequestedBlocks);

		List<String> successfulBlockNames = result.getSuccessfulBlockNames();
		assertEquals(2, successfulBlockNames.size());
		assertEquals("successBlock1", successfulBlockNames.get(0));
		assertEquals("successBlock2", successfulBlockNames.get(1));
		
		List<String> successfulDocumentNames = result.getSuccessfulDocumentNames();
		assertEquals(2, successfulDocumentNames.size());
		assertEquals("successDocument1", successfulDocumentNames.get(0));
		assertEquals("successDocument2", successfulDocumentNames.get(1));
		
		Map<String, OperationException> throwables = result.getThrowables();
		assertEquals(0, throwables.size());
	}

	@Test
	void testConvertXmlToAssemblerEmptyResult() throws Exception {
		AssemblerResult result = RestServicesDocAssemblerServiceAdapter.convertXmlToAssemblerResult(new ByteArrayInputStream(EMPTY_ASSEMBLER_RESULT_XML.getBytes()));
		
		validateEmptyResult(result);
	}

	private void validateEmptyResult(AssemblerResult result) {
		Map<String, Document> documents = result.getDocuments();
		assertEquals(0, documents.size());
		
		List<String> failedBlockNames = result.getFailedBlockNames();
		assertEquals(0, failedBlockNames.size());
		
		Document jobLog = result.getJobLog();
		assertTrue(jobLog.isEmpty());
		
		int lastBatesNumber = result.getLastBatesNumber();
		assertEquals(0, lastBatesNumber);
		
		Map<String, List<String>> multipleResultsBlocks = result.getMultipleResultsBlocks();
		assertEquals(0, multipleResultsBlocks.size());

		int numRequestedBlocks = result.getNumRequestedBlocks();
		assertEquals(0, numRequestedBlocks);

		List<String> successfulBlockNames = result.getSuccessfulBlockNames();
		assertEquals(0, successfulBlockNames.size());
		
		List<String> successfulDocumentNames = result.getSuccessfulDocumentNames();
		assertEquals(0, successfulDocumentNames.size());
		
		Map<String, OperationException> throwables = result.getThrowables();
		assertEquals(0, throwables.size());
	}

}
