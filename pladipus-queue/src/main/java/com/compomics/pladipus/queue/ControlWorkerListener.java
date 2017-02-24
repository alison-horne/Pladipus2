package com.compomics.pladipus.queue;

import javax.jms.Message;
import javax.jms.MessageListener;

public class ControlWorkerListener implements MessageListener {
	
	@Override
	public void onMessage(Message msg) {
		new WorkerTaskProcessor(msg).start();
	}
}
