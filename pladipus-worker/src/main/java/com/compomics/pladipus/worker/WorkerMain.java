package com.compomics.pladipus.worker;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.compomics.pladipus.worker.config.WorkerConfiguration;

public class WorkerMain {

	private AbstractApplicationContext context = new AnnotationConfigApplicationContext(WorkerConfiguration.class);
	
	public static void main(String[] args) {
		new WorkerMain().init();
	}
	
	public void init() {
		Runtime.getRuntime().addShutdownHook(new CleanShutdown());
		// TODO command line output to let the user know it's running
	}
	
	class CleanShutdown extends Thread {
		public void run() {
			// Try to cancel all tasks neatly, and send cancel messages to the controller
			QueueProcessor qp = context.getBean("workerTaskQueueProcessor", QueueProcessor.class);
			qp.cancelAll();
			context.close();
		}
	}
}
