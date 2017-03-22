package com.compomics.pladipus.queue;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ControlClientListener implements MessageListener {
	
	@Autowired
	private BeanFactory beanFactory;
	
	@Override
	public void onMessage(Message msg) {
		if (msg instanceof TextMessage) {
			beanFactory.getBean(ClientTaskProcessor.class, (TextMessage)msg).start();
		} else {
			// TODO handle bad message
		}
	}
}
