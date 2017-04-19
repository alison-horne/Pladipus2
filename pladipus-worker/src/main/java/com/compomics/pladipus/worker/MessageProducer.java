package com.compomics.pladipus.worker;

import com.compomics.pladipus.model.queue.messages.worker.WorkerStatus;

/**
 * Used to send JMS messages to the controller to update with progress on tasks
 */
public interface MessageProducer {

	/**
	 * Send a message to the controller with all necessary information to be processed
	 * 
	 * @param jobId - run step identifier
	 * @param status - WorkerStatus
	 * @param message - error message, or the output of a successful completed task
	 */
	public void sendMessage(Long jobId, WorkerStatus status, String message);
}
