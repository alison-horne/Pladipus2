package com.compomics.pladipus.worker;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;

public class WorkerListener implements MessageListener {
	
	@Autowired
	private WorkerProcessor workerProcessor;
	
	@Override
	public void onMessage(Message msg) {
		if (msg instanceof TextMessage) {
			workerProcessor.processMessage((TextMessage) msg);
		} else {
			// TODO deal with bad message
		}
	}
}
