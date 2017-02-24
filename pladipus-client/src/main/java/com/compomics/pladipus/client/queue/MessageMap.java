package com.compomics.pladipus.client.queue;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MessageMap {
	private ConcurrentMap<String, String> messageMap = new ConcurrentHashMap<String, String>();
	private ConcurrentMap<String, Object> lockMap = new ConcurrentHashMap<String, Object>();
	
	public void initMessage(String id, Object obj) {
		lockMap.put(id, obj);
	}
	
	public void messageTimeout(String id) {
		lockMap.remove(id);
	}
	
	public String getMessageText(String id) {
		return messageMap.remove(id);
	}
	
	public void messageReceived(String id, String text) {
		Object obj = lockMap.remove(id);
		if (obj != null) {
			synchronized(obj) {
				messageMap.put(id, text);
				obj.notify();
			}
		}
	}
}
