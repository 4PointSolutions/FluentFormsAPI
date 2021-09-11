package com._4point.aem.fluentforms.api.workflow;

import java.util.Collections;
import java.util.Map;

public interface WorkflowService {
	
	public <T, R> Workflow<R> startWorkflow(String modelPath, String payloadType, T payload, Map<String, ?> metadata, Class<R> resultClass) throws WorkflowServiceException;

	@SuppressWarnings("unchecked")
	public default <T> Workflow<T> startWorkflow(String modelPath, String payloadType, T payload, Map<String, ?> metadata) throws WorkflowServiceException {
		return startWorkflow(modelPath, payloadType, payload, Collections.emptyMap(), (Class<T>)payload.getClass());
	}
	
	public default <T> Workflow<T> startWorkflow(String modelPath, String payloadType, T payload) throws WorkflowServiceException {
		return startWorkflow(modelPath, payloadType, payload, Collections.emptyMap());
	}
	
	@SuppressWarnings("serial")
	public static class WorkflowServiceException extends Exception {

		public WorkflowServiceException() {
			super();
		}

		public WorkflowServiceException(String message, Throwable cause) {
			super(message, cause);
		}

		public WorkflowServiceException(String message) {
			super(message);
		}

		public WorkflowServiceException(Throwable cause) {
			super(cause);
		}
	}
}
