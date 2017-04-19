package com.compomics.pladipus.model.queue.messages.worker;

public enum WorkerStatus {
	ACK,
	PROCESSING,
	CANCELLED,
	ERROR,
	COMPLETED,
	TIMEOUT
}
