package com._4point.aem.fluentforms.impl.workflow;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com._4point.aem.fluentforms.api.workflow.Workflow;
import com._4point.aem.fluentforms.api.workflow.WorkflowService;

import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
class WorkflowServiceImplTest {

	@Mock WorkflowServiceAdapter workflowServiceAdapter;
	@Mock Workflow<Object> workflow;
	
	private WorkflowService underTest;
	
	@BeforeEach
	void setUp() throws Exception {
		underTest = new WorkflowServiceImpl(workflowServiceAdapter);
		when(workflowServiceAdapter.startWorkflow(anyString(), anyString(), anyString(), anyMap())).thenReturn(workflow);
	}


	@Test
	void testStartWorkflow() throws Exception {
		String modelPath = "ModelPath";
		String payloadType = "String";
		String payload = "Payload";
		CompletableFuture<String> result = underTest.startWorkflow(modelPath, payloadType, payload);
		assertNotNull(result);
	}

}
