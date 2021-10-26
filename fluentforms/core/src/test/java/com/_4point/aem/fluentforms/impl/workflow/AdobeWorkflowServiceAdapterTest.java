package com._4point.aem.fluentforms.impl.workflow;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import javax.jcr.Session;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com._4point.aem.fluentforms.api.workflow.WorkflowService.WorkflowPayloadType;
import com._4point.aem.fluentforms.api.workflow.WorkflowService.WorkflowServiceException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.Workflow;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.model.WorkflowModel;

@ExtendWith(MockitoExtension.class)
class AdobeWorkflowServiceAdapterTest {
	
	@Mock WorkflowSession workflowSession;
	@Mock Session session;
	@Mock Workflow adobeWorkflow;

	@Captor ArgumentCaptor<WorkflowModel> model;
	@Captor ArgumentCaptor<WorkflowData> payload;
	
	private AdobeWorkflowServiceAdapter underTest;
	
	@BeforeEach
	void setUp() throws Exception {
		when(workflowSession.adaptTo(any())).thenReturn(session);
		this.underTest = new AdobeWorkflowServiceAdapter(workflowSession);
	}

	@Test
	void testStartWorkflow() throws Exception {
		when(workflowSession.startWorkflow(model.capture(), payload.capture())).thenReturn(adobeWorkflow);
		String expectedId = "Expected Id";
		Instant expectedTimeStarted = new Date().toInstant();
		setupAdobeWorkflowMock(expectedId, expectedTimeStarted);
		
		String expectedModelPath = "Expected Model Path";
		WorkflowPayloadType expectedPayloadType = WorkflowPayloadType.BINARY;
		Object expectedPayload = "Expected Payload";
		Map<String, String> expectedMetadata = Collections.emptyMap();
		com._4point.aem.fluentforms.api.workflow.Workflow<Object> result = underTest.startWorkflow(expectedModelPath, expectedPayloadType, expectedPayload, expectedMetadata);
		assertNotNull(result);
		// Check the result
		assertAll(
				()->assertEquals(expectedId, result.getId()),
				()->assertEquals(expectedTimeStarted, result.getTimeStarted()),
				()->assertFalse(result.getTimeEnded().isPresent())
				);
	}

	void setupAdobeWorkflowMock(String id, Instant timeStarted) {
		when(adobeWorkflow.getId()).thenReturn(id);
		when(adobeWorkflow.getTimeStarted()).thenReturn(Date.from(timeStarted));
	}
}
