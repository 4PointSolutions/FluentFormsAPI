package com._4point.aem.fluentforms.impl.workflow;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

import java.util.HashMap;
import java.util.Map;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.event.WorkflowEvent;
import com.adobe.granite.workflow.exec.Workflow;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.adobe.granite.workflow.model.WorkflowModel;

@Component(service = org.osgi.service.event.EventHandler.class, immediate = true, property = {
		Constants.SERVICE_DESCRIPTION + "=FluentForms Workflow Completed Event Listener",
		EventConstants.EVENT_TOPIC + "=" + WorkflowEvent.EVENT_TOPIC // WORKFLOW_COMPLETED_EVENT
		})
public class WorkflowMonitor implements EventHandler {
	
	Map<String, WorkflowImpl<?>> registry = new HashMap<>();

	@Override
	public void handleEvent(Event event) {
		WorkflowEvent wfevent = (WorkflowEvent) event;
		String workflowInstanceId = wfevent.getWorkflowInstanceId();
		String eventType = wfevent.getEventType();
		String workflowName = (String) event.getProperty("WorkflowName");
		if (eventType.equals(WorkflowEvent.WORKFLOW_COMPLETED_EVENT)) {
			synchronized (registry) {
				WorkflowImpl<?> workflowImpl = registry.get(workflowInstanceId);
				if (workflowImpl != null) {
					// Complete the workflow
					workflowImpl.complete(null); // get the payload and complete the Future
				}
			}
//			if ("Smartforms_GenerateForm".equals(workflowName)) {				
//				if (workflowInstanceId.equals(_workflow.getId())) {
//					notify();
//				}
//			}
		}
	}

}
