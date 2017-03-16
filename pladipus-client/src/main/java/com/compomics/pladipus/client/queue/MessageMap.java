package com.compomics.pladipus.client.queue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;

public class MessageMap {
	private ConcurrentMap<String, String> messageMap = new ConcurrentHashMap<String, String>();
	private ConcurrentMap<String, Object> lockMap = new ConcurrentHashMap<String, Object>();
	private List<Future<String>> futureList = new ArrayList<Future<String>>();
	
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
	
	public void addFuture(Future<String> future) {
		futureList.add(future);
	}
	
	public void removeFuture(Future<String> future) {
		futureList.remove(future);
	}
	
	public void terminateFutures() {
		synchronized(futureList) {
			for (Future<String> future: futureList) {
				future.cancel(true);
			}
		}
	}
}
