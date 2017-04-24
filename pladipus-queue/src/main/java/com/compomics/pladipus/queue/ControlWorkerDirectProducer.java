package com.compomics.pladipus.queue;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.compomics.pladipus.model.queue.MessageSelector;

public class ControlWorkerDirectProducer {
	@Autowired
	private JmsTemplate toWorkerDirectTemplate;

	public void sendMessage(final String txt, final String workerId) {
		toWorkerDirectTemplate.send(new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				TextMessage msg = session.createTextMessage(txt);
				msg.setStringProperty(MessageSelector.WORKER_ID.name(), workerId);
				return msg;
			}	
		});
	}
}
