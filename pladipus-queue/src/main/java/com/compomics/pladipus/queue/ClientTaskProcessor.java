package com.compomics.pladipus.queue;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;

public class ClientTaskProcessor extends Thread {
	
	@Autowired
	private ControlClientProducer clientProducer;
	
	@Autowired
	private String clientIdProperty;
	
	private Message msg;
	private String clientId;
	private String corrId;
	
	public ClientTaskProcessor(Message msg) {
		this.msg = msg;
	}
	
	public void run() {
		try {
			clientId = msg.getStringProperty(clientIdProperty);
			corrId = msg.getJMSCorrelationID(); // TODO if either of these is null, do not process, just log error.
			if (msg instanceof TextMessage) {
				((TextMessage) msg).getText();
				String txt = "response"; // TODO Process message
				clientProducer.sendMessage(txt, corrId, clientId);
			}
		} catch (JMSException e) {
			// TODO log error, leave client to timeout
		}
	}

}
