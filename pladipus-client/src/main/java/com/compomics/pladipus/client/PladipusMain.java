package com.compomics.pladipus.client;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.compomics.pladipus.client.cmdline.MainCLI;
import com.compomics.pladipus.client.config.ClientConfiguration;
import com.compomics.pladipus.client.gui.GuiMain;
import com.compomics.pladipus.client.gui.MainGUI;
import com.compomics.pladipus.client.queue.MessageMap;

public class PladipusMain {
	private AbstractApplicationContext context = new AnnotationConfigApplicationContext(ClientConfiguration.class);
	
	public static void main(String[] args) {
		new PladipusMain().init(args);
	}
	
	public void init(String[] args) {
		Runtime.getRuntime().addShutdownHook(new Logout());
		if (args.length == 0) {
			context.getBean("gui", GuiMain.class).guiMain();
		}
		else {
			context.getBean("cli", MainCLI.class).cliMain(args);
		}
		System.exit(0);
	}
	
	class Logout extends Thread {
		public void run() {
			// TODO send logout message?
			MessageMap map = context.getBean("messageMap", MessageMap.class);
			map.terminateFutures();
			context.close();
		}
	}
}
