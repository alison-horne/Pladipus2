package com.compomics.pladipus.queue;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class ControlClientProducer {
	@Autowired
	private JmsTemplate toClientsTemplate;
	
	@Autowired
	private String clientIdProperty;

	public void sendMessage(final String txt, final String correlationId, final String clientId) {
		toClientsTemplate.send(new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				TextMessage msg = session.createTextMessage(txt);
				msg.setJMSCorrelationID(correlationId);
				msg.setStringProperty(clientIdProperty, clientId);
				return msg;
			}			
		});
	}
}
