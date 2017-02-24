package com.compomics.pladipus.queue;

import javax.jms.Message;

public class WorkerTaskProcessor extends Thread {
	
	private Message msg;
	
	public WorkerTaskProcessor(Message msg) {
		this.msg = msg;
	}
	
	public void run() {
		// TODO process received message from worker
	}

}
