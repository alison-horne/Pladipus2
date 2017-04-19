package com.compomics.pladipus.worker.impl;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.compomics.pladipus.model.queue.MessageSelector;
import com.compomics.pladipus.model.queue.messages.worker.WorkerStatus;
import com.compomics.pladipus.model.queue.messages.worker.WorkerToControlMessage;
import com.compomics.pladipus.worker.MessageProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WorkerProducer implements MessageProducer {
	
	@Autowired
	private JmsTemplate jmsTemplate;
	
	@Autowired
	private ObjectMapper jsonMapper;
	
	@Autowired
	private String workerId;

	private void sendMessage(final String txt) {
		jmsTemplate.send(new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				TextMessage msg = session.createTextMessage(txt);
				// Set worker ID property so the controller knows which worker is running which task
				msg.setStringProperty(MessageSelector.WORKER_ID.name(), workerId);
				return msg;
			}			
		});
	}
	
	@Override
	public void sendMessage(Long jobId, WorkerStatus status, String message) {
		WorkerToControlMessage msg = new WorkerToControlMessage();
		msg.setJobId(jobId);
		msg.setStatus(status);
		msg.setMessage(message);
		try {
			sendMessage(jsonMapper.writeValueAsString(msg));
		} catch (JsonProcessingException e) {
			// TODO Retry? Ignore?
			e.printStackTrace();
		}
	}
}