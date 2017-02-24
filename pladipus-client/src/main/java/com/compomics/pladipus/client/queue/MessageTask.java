package com.compomics.pladipus.client.queue;

import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;

public class MessageTask implements Callable<String> {

	@Autowired
	private MessageMap messageMap;
	
	@Autowired
	private UuidGenerator uuidGen;
	
	@Autowired
	private long clientTimeout;
	
	@Autowired
	private ClientMessageProducer clientMessageProducer;
	
	private String messageTxt;
	
	public MessageTask(String messageTxt) {
		this.messageTxt = messageTxt;
	}
	
	public String call() {
		String correlationId = uuidGen.getUUID();
		synchronized(this) {
			try {			
				messageMap.initMessage(correlationId, this);
				clientMessageProducer.sendMessage(messageTxt, correlationId);
				this.wait(clientTimeout);
			} catch (InterruptedException e) {
				// Ignore
			}
		}
		String responseTxt = messageMap.getMessageText(correlationId);
		if (responseTxt == null) {
			messageMap.messageTimeout(correlationId);
		}
		return responseTxt;
	}
}
