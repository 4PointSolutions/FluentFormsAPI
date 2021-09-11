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
		// TODO: Fill this in to do more...
		// Find the WorkflowMonitor
		// Lock the registry
		// Start the workflow
		//    Notes: Locking the registry while we start the workflow may slow things down if starting the
		//           workflow takes a long time, however the alternative (locking after starting it) runs the 
		//           risk of having the workflow complete before we've registered it with the registry.
		Workflow<R> workflow = workflowServiceAdapter.startWorkflow(modelPath, payloadType, payload, metadata);
		// Add the workflow to the regisrty
		// Unlock the registry
		return workflow;
	}

}
