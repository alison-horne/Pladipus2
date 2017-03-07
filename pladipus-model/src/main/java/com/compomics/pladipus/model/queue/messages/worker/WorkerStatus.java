package com.compomics.pladipus.model.queue.messages.worker;

public enum WorkerStatus {
	ACK,
	PROCESSING,
	ERROR,
	COMPLETED,
	TIMEOUT
}
