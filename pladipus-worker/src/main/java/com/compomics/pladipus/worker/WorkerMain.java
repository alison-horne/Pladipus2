package com.compomics.pladipus.worker;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.compomics.pladipus.worker.config.WorkerConfiguration;

public class WorkerMain {

	private AbstractApplicationContext context = new AnnotationConfigApplicationContext(WorkerConfiguration.class);
	
	public static void main(String[] args) {
		new WorkerMain().init(args);
	}
	
	public void init(String[] args) {
		Runtime.getRuntime().addShutdownHook(new Logout());
		// TODO setup prerequisites (i.e. is this running on Windows?  Add message property to listen for Windows, etc)  
	}
	
	class Logout extends Thread {
		public void run() {
			// TODO cancel running jobs, alert controller if possible
			context.close();
		}
	}
}
