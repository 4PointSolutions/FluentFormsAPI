package com._4point.aem.fluentforms.impl.workflow;

import java.util.Map;

import com._4point.aem.fluentforms.api.workflow.Workflow;
import com._4point.aem.fluentforms.api.workflow.WorkflowService.WorkflowServiceException;

public interface WorkflowServiceAdapter {

	<T, R> Workflow<R> startWorkflow(String modelPath, String payloadType, T payload, Map<String, ?> metadata) throws WorkflowServiceException;

	
}
