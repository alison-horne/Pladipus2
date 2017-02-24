package com.compomics.pladipus.queue;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class ControlWorkerProducer {
	@Autowired
	private JmsTemplate toWorkersTemplate;

	public void sendMessage(final String txt) {
		toWorkersTemplate.send(new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				TextMessage msg = session.createTextMessage(txt);
				//TODO set prereq message properties, e.g. ok to run on windows, etc.
				return msg;
			}			
		});
	}
}
