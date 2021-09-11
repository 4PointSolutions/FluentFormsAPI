package com._4point.aem.fluentforms.impl.workflow;

import java.time.Instant;
import java.util.Optional;

import com._4point.aem.fluentforms.api.workflow.Workflow;

public class WorkflowImpl<T> extends Workflow<T> {
	private final String id;
	private final Instant timeStarted;
	private Optional<Instant> timeEnded;	// Only available if workflow has completed or been aborted.

	public WorkflowImpl(String id, Instant timeStarted) {
		this.id = id;
		this.timeStarted = timeStarted;
		this.timeEnded = Optional.empty();
	}

	public String getId() {
		return id;
	}

	public Instant getTimeStarted() {
		return timeStarted;
	}

	public Optional<Instant> getTimeEnded() {
		return timeEnded;
	}
}
