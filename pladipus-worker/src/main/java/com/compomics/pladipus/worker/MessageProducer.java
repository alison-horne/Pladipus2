package com.compomics.pladipus.worker;

import java.util.Map;

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
	 * @param outputs - the outputs of a successful completed task (name -> output), or null
	 */
	public void sendMessage(Long jobId, WorkerStatus status, Map<String, String> outputs);
	
	/**
	 * Send an error message to the controller
	 * 
	 * @param jobId - run step identifier
	 * @param errorMsg - description of the error
	 */
	public void sendErrorMessage(Long jobId, String errorMsg);
}
