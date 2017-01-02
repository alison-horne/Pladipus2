package com.compomics.pladipus.client;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.compomics.pladipus.client.config.ClientConfiguration;

public class PladipusMain {
	public static void main(String[] args) {
		new PladipusMain().init(args);
	}
	
	public void init(String[] args) {
		@SuppressWarnings("resource")
		AbstractApplicationContext context = new AnnotationConfigApplicationContext(ClientConfiguration.class);
		context.registerShutdownHook();
		if (args.length == 0) {
			((MainGUI)context.getBean("gui")).guiMain();
		}
		else {
			((MainCLI)context.getBean("cli")).cliMain(args);
		}
	}
}
