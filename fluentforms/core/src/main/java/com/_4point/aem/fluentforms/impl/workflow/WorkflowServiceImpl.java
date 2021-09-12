package com._4point.aem.fluentforms.impl.workflow;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com._4point.aem.fluentforms.api.workflow.Workflow;
import com._4point.aem.fluentforms.api.workflow.WorkflowService;
import com._4point.aem.fluentforms.api.workflow.WorkflowService.WorkflowServiceException;

public class WorkflowServiceImpl implements WorkflowService {

	private final WorkflowServiceAdapter workflowServiceAdapter;
	
	public WorkflowServiceImpl(WorkflowServiceAdapter workflowServiceAdapter) {
		this.workflowServiceAdapter = workflowServiceAdapter;
	}

	@Override
	public <T, R> Workflow<R> startWorkflow(String modelPath, String payloadType, T payload, Map<String, ?> metadata, Class<R> resultClass) throws WorkflowServiceException {
		Workflow<R> workflow = workflowServiceAdapter.startWorkflow(modelPath, payloadType, payload, metadata);
		return workflow;
	}

}
