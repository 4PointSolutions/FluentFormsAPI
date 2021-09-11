package com._4point.aem.fluentforms.impl.workflow;

import java.util.Map;

import javax.jcr.Session;

import com._4point.aem.fluentforms.api.workflow.WorkflowService.WorkflowServiceException;
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.Workflow;
import com.adobe.granite.workflow.exec.WorkflowData;

public class AdobeWorkflowServiceAdapter implements WorkflowServiceAdapter {
	
	private final WorkflowSession workflowSession;
	private final Session session;
	
	public AdobeWorkflowServiceAdapter(WorkflowSession workflowSession) {
		this.workflowSession = workflowSession;
		this.session = this.workflowSession.adaptTo(Session.class);
	}

	@Override
	public <T, R> com._4point.aem.fluentforms.api.workflow.Workflow<R> startWorkflow(String modelPath, String payloadType, T payload, Map<String, ?> metadata) throws WorkflowServiceException {
		try {
			WorkflowData wfData = workflowSession.newWorkflowData( "JCR_PATH", payload );
			@SuppressWarnings("unchecked")
			Workflow workflow = metadata.isEmpty()
									? workflowSession.startWorkflow( workflowSession.getModel( modelPath ), wfData )
									: workflowSession.startWorkflow( workflowSession.getModel( modelPath ), wfData, (Map<String, Object>) metadata );
			return new WorkflowImpl<R>(workflow.getId(), workflow.getTimeStarted().toInstant());
		} catch (WorkflowException e) { 	 
			throw new WorkflowServiceException("Error while attempting to start workflow (" + modelPath + ").", e);
		}
	}
	
}
