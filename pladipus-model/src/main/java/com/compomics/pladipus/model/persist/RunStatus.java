package com.compomics.pladipus.model.persist;

public enum RunStatus {
	BLOCKED,
	READY,
	QUEUED,
	ON_WORKER,
	IN_PROGRESS,
	COMPLETE,
	ABORT,
	CANCELLED,
	ERROR
}
