package com.compomics.pladipus.client.config;

import com.compomics.pladipus.client.queue.MessageTask;

public class MockMessageTask implements MessageTask {
	
	private String messageTxt;

	public MockMessageTask(String messageTxt) {
		this.messageTxt = messageTxt;
	}
	
	public String call() {
		System.out.println("Calling mock " + messageTxt);
		return null;
	}
}
