package com.compomics.pladipus.client.queue;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class ClientMessageProducer {
	@Autowired
	private JmsTemplate jmsTemplate;
	
	@Autowired
	private String clientIdProperty;
	
	@Autowired
	private UuidGenerator uuidGen;

	public void sendMessage(final String txt, final String correlationId) {
		jmsTemplate.send(new MessageCreator() {

			@Override
			public Message createMessage(Session session) throws JMSException {
				TextMessage msg = session.createTextMessage(txt);
				msg.setStringProperty(clientIdProperty, uuidGen.getClientID());
				msg.setJMSCorrelationID(correlationId);
				return msg;
			}
			
		});

	}
}
