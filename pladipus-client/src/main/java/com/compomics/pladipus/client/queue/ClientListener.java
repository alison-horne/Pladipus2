package com.compomics.pladipus.client.queue;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;

public class ClientListener implements MessageListener {

	@Autowired
	private	MessageMap messageMap;
	
	@Override
	public void onMessage(Message msg) {
		if (msg instanceof TextMessage) {
			TextMessage txt = (TextMessage) msg;
			try {
				messageMap.messageReceived(txt.getJMSCorrelationID(), txt.getText());
			} catch (JMSException e) {
				// Just ignore invalid message and allow timeout to deal with caller.
			}
		}
	}

}
