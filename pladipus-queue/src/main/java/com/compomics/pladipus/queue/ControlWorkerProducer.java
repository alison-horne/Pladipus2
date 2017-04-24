package com.compomics.pladipus.queue;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.compomics.pladipus.model.queue.MessageSelector;
import com.compomics.pladipus.model.queue.Prerequisite;

public class ControlWorkerProducer {
	@Autowired
	private JmsTemplate toWorkersTemplate;

	public void sendMessage(final String txt, final String failedWorkers, final String runIdentifier) {
		toWorkersTemplate.send(new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				TextMessage msg = session.createTextMessage(txt);
				for (Prerequisite p: Prerequisite.values()) {
					// TODO actually filter on OS where necessary
					msg.setBooleanProperty(p.name(), true);
				}
				msg.setStringProperty(MessageSelector.FAILED_WORKERS.name(), (failedWorkers != null) ? failedWorkers : "");
				msg.setStringProperty(MessageSelector.JMX_IDENTIFIER.name(), runIdentifier);
				return msg;
			}	
		});
	}
}
