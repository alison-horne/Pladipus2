package com.compomics.pladipus.queue;

import javax.jms.Message;
import javax.jms.MessageListener;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ControlClientListener implements MessageListener {
	
	@Autowired
	private BeanFactory beanFactory;
	
	@Override
	public void onMessage(Message msg) {
		beanFactory.getBean(ClientTaskProcessor.class, msg).start();
	}
}
