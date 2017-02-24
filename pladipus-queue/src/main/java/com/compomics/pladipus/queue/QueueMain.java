package com.compomics.pladipus.queue;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.compomics.pladipus.queue.config.QueueConfiguration;

public class QueueMain {

	public static void main(String[] args) {
		//TODO handle shutdown properly
		@SuppressWarnings("resource")
		AbstractApplicationContext context = new AnnotationConfigApplicationContext(QueueConfiguration.class);
		context.registerShutdownHook();
	}

}
