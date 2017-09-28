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
 * Takes messages from the 'general' ActiveMQ queue, and processes them.
 * The general queue is for the controller to send tasks which may be picked up and run by any worker.
 * 
 * This listener will wait for a task to complete before picking up the next one from the queue, allowing other
 * free workers to process them.
 * 
 * If tasks arrive on the 'direct' ActiveMQ queue, they will be given priority and this listener will be stopped 
 * until they have been completed.
 */
public class WorkerTaskListener implements MessageListener {
	
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
				synchronized (taskMsg) {
					workerTaskQueueProcessor.addTask(taskMsg);
					taskMsg.wait();
				}
			} catch (IOException | JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO
			}
		} else {
			// TODO deal with bad message - will implement error queue for returning bad messages to control
		}
	}
}
