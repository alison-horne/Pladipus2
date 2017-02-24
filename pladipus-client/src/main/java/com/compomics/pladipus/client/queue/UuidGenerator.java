package com.compomics.pladipus.client.queue;

import java.util.UUID;

public class UuidGenerator {
	private static String clientID;
	
	public final String getClientID() {
		if (clientID == null) {
			clientID = getUUID();
		}
		return clientID;
	}
	
	public final String getUUID() {
		return UUID.randomUUID().toString();
	}
}
