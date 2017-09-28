package com.compomics.pladipus.worker.impl;

import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import com.compomics.pladipus.model.queue.messages.worker.WorkerTaskMessage;
import com.compomics.pladipus.worker.QueueProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

/**
 * Takes messages from the 'direct' ActiveMQ queue, and processes them.
 * The direct queue is for the controller to send messages to this one worker in particular.
 * This is used for cancelling jobs, checking on the worker status, and sending tasks to a specific worker, 
 * e.g. if a run step was processed on this worker and the next step should be too as there is data on this machine.
 * 
 * As all messages are specifically for this worker, they are all consumed immediately, and tasks will be queued locally.
 */
public class WorkerDirectListener implements MessageListener {
	
	@Autowired
	private ObjectMapper jsonMapper;
	
	@Autowired
	@Lazy
	private QueueProcessor workerTaskQueueProcessor;
	
	@Override
	public void onMessage(Message msg) {
		if (msg instanceof TextMessage) {
			try {
				ObjectReader reader = jsonMapper.readerFor(WorkerTaskMessage.class);
				WorkerTaskMessage taskMsg = reader.readValue(((TextMessage) msg).getText());
				switch (taskMsg.getTask()) {
					case ABORT_ALL:
						workerTaskQueueProcessor.cancelAll();
						break;
					case ABORT_JOB:
						workerTaskQueueProcessor.cancelTask(taskMsg.getJobId());
						break;
					case ALIVE_CHECK:
						// TODO
						break;
					case PRIORITY_TASK:
						workerTaskQueueProcessor.addTask(taskMsg);
						break;
					default: //TODO
						break;
				}
			} catch (IOException | JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			// TODO deal with bad message - will implement error queue for returning bad messages to control
		}
	}
}
