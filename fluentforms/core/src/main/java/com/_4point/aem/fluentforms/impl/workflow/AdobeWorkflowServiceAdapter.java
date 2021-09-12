package com._4point.aem.fluentforms.impl.workflow;

import java.util.Map;

import javax.jcr.Session;

import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com._4point.aem.fluentforms.api.workflow.WorkflowService.WorkflowServiceException;
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.Workflow;
import com.adobe.granite.workflow.exec.WorkflowData;

public class AdobeWorkflowServiceAdapter implements WorkflowServiceAdapter {
	private static final Logger log = LoggerFactory.getLogger(AdobeWorkflowServiceAdapter.class);
	
	private final WorkflowSession workflowSession;
	private final Session session;
	
	public AdobeWorkflowServiceAdapter(WorkflowSession workflowSession) {
		this.workflowSession = workflowSession;
		this.session = this.workflowSession.adaptTo(Session.class);
	}

	@Override
	public <T, R> com._4point.aem.fluentforms.api.workflow.Workflow<R> startWorkflow(String modelPath, String payloadType, T payload, Map<String, ?> metadata) throws WorkflowServiceException {
		// TODO: Fill this in to do more...
		// Find the WorkflowMonitor
		// Lock the registry
		//    Notes: Locking the registry while we start the workflow may slow things down if starting the
		//           workflow takes a long time, however the alternative (locking after starting it) runs the 
		//           risk of having the workflow complete before we've registered it with the registry.
		com._4point.aem.fluentforms.api.workflow.Workflow<R> workflowInstance = createWorkflowInstance(modelPath, payloadType, payload, metadata);	// payloadType  = "JCR_PATH"
		// Add the workflow to the regisrty
		// Unlock the registry
		return workflowInstance;
	}

	private <T,R> com._4point.aem.fluentforms.api.workflow.Workflow<R> createWorkflowInstance(String modelPath, String payloadType, T payload, Map<String, ?> metadata) throws WorkflowServiceException {
		try {
			WorkflowData wfData = workflowSession.newWorkflowData( payloadType, payload ); 
			@SuppressWarnings("unchecked")
			Workflow workflow = metadata.isEmpty()
									? workflowSession.startWorkflow( workflowSession.getModel( modelPath ), wfData )
									: workflowSession.startWorkflow( workflowSession.getModel( modelPath ), wfData, (Map<String, Object>) metadata );
//			log.info("workflow started (" + workflowName + "/" + workflowInstanceId + ").");
			return new WorkflowImpl<R>(workflow.getId(), workflow.getTimeStarted().toInstant());
		} catch (WorkflowException e) { 	 
			throw new WorkflowServiceException("Error while attempting to start workflow (" + modelPath + ").", e);
		}
	}

	public <R> com._4point.aem.fluentforms.api.workflow.Workflow<R> getWorkflow(String id) {
		return null;	
	}
	
	public static AdobeWorkflowServiceAdapter from(ResourceResolver resolver) {
		return new AdobeWorkflowServiceAdapter(resolver.adaptTo(WorkflowSession.class));
	}
}
