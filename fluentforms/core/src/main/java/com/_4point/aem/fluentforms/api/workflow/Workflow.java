package com._4point.aem.fluentforms.api.workflow;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public abstract class Workflow<T> extends CompletableFuture<T> {

	public abstract String getId();

	public abstract Instant getTimeStarted();

	public abstract Optional<Instant> getTimeEnded();

	// Things we might need from the workflow that are not provided by the Completable Future
	//	 - String initiator;
	//	 - String state;
	//	 - boolean isActive;
	// WorkflowData
	//	 - Object payload
	//	 - String pauloadType
	// WorkflowModel - Things we might need from the model
	//   - String title
	//   - String description
	//	 - String version
	// WorkItems
	//  I don't see anything useful here, I don't intend to track the workflow

}