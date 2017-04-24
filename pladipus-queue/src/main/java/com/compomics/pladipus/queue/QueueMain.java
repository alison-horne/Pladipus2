package com.compomics.pladipus.queue;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.compomics.pladipus.queue.config.QueueConfiguration;

public class QueueMain {
	
	private AbstractApplicationContext context = new AnnotationConfigApplicationContext(QueueConfiguration.class);
	
	public static void main(String[] args) {
		// TODO command line output so user knows it's running...
		QueueMain qm = new QueueMain();
		qm.init();
	}
	
	public void init() {
		Runtime.getRuntime().addShutdownHook(new CleanShutdown());
	}
	
	class CleanShutdown extends Thread {
		public void run() {
			//TODO add other threads to shutdown nicely.
			ReadyTaskScheduler ts = context.getBean("readyTaskScheduler", ReadyTaskScheduler.class);
			ts.killScheduler();
			context.close();
		}
	}
}
