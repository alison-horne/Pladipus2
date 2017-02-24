package com.compomics.pladipus.worker;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.compomics.pladipus.worker.config.WorkerConfiguration;

public class WorkerMain {

	public static void main(String[] args) {
		//TODO handle shutdown properly
		// command line output to let user know it's running?
		@SuppressWarnings("resource")
		AbstractApplicationContext context = new AnnotationConfigApplicationContext(WorkerConfiguration.class);
		context.registerShutdownHook();
	}
}
