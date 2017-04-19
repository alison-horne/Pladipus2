package com.compomics.pladipus.worker;

import com.compomics.pladipus.model.queue.messages.worker.WorkerTaskMessage;

/**
 * Adds and removes tasks from the queue to be run, and sends them for processing.
 */
public interface QueueProcessor {

	/**
	 * Remove all tasks from the queue, and cancel any currently running task.
	 */
	public void cancelAll();

	/**
	 * Cancel the task with the specified run step identifier.  
	 * If it is currently running, this is cancelled mid-task.  
	 * If it is in the queue, it is removed from the queue.
	 * A message is sent to the controller to notify that the task is cancelled.
	 * 
	 * @param jobId - run step identifier of the task to be cancelled.
	 */
	public void cancelTask(Long jobId);

	/**
	 * Adds a task to the queue to be run, and begins processing if there are no other tasks waiting.
	 * @param taskMsg - WorkerTaskMessage received from the ActiveMQ worker queues.
	 */
	public void addTask(WorkerTaskMessage taskMsg);
	
}
