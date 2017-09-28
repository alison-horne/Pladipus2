package com.compomics.pladipus.client.queue;

import com.compomics.pladipus.model.queue.messages.client.ClientToControlMessage;
import com.compomics.pladipus.model.queue.messages.client.ControlToClientMessage;
import com.compomics.pladipus.shared.PladipusReportableException;

public interface MessageSender {
	public ControlToClientMessage makeRequest(ClientToControlMessage message) throws PladipusReportableException;
}
