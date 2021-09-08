package com._4point.aem.fluentforms.api.workflow;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface WorkflowService {
	<T> CompletableFuture<T> startWorkflow(String payloadType, T payload, Map<String, ?> metadata);
	
	default <T> CompletableFuture<T> startWorkflow(String payloadType, T payload) {
		return startWorkflow(payloadType, payload, Collections.emptyMap());
	}
}
