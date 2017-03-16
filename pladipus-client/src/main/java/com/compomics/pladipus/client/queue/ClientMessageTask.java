package com.compomics.pladipus.client.queue;

import org.springframework.beans.factory.annotation.Autowired;

public class ClientMessageTask implements MessageTask {

	@Autowired
	private MessageMap messageMap;
	
	@Autowired
	private UuidGenerator uuidGen;
	
	@Autowired
	private long clientTimeout;
	
	@Autowired
	private ClientMessageProducer clientMessageProducer;
	
	private String messageTxt;
	
	public ClientMessageTask(String messageTxt) {
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
				Thread.currentThread().interrupt();
			}
		}
		String responseTxt = messageMap.getMessageText(correlationId);
		if (responseTxt == null) {
			messageMap.messageTimeout(correlationId);
		}
		return responseTxt;
	}
}
