package com._4point.aem.fluentforms.api.workflow;

import java.util.Collections;
import java.util.Map;

public interface WorkflowService {
	
	public enum WorkflowPayloadType {
		JAVA_OBJECT,
		JCR_PATH,
		JCR_UUID,
		URL,
		BINARY,
		;
	}

	// Any type of payload and any type of result
	public <T, R> Workflow<R> startWorkflow(String modelPath, WorkflowPayloadType payloadType, T payload, Map<String, ?> metadata, Class<R> resultClass) throws WorkflowServiceException;

	// Assumes payload and result are of the same type (so this is a little more succinct) 
	@SuppressWarnings("unchecked")
	public default <T> Workflow<T> startWorkflow(String modelPath, WorkflowPayloadType payloadType, T payload, Map<String, ?> metadata) throws WorkflowServiceException {
		return startWorkflow(modelPath, payloadType, payload, Collections.emptyMap(), (Class<T>)payload.getClass());
	}
	
	// Assumes payload and result are the same type with no metadata to be added.
	public default <T> Workflow<T> startWorkflow(String modelPath, WorkflowPayloadType payloadType, T payload) throws WorkflowServiceException {
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
