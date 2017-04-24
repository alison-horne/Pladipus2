package com.compomics.pladipus.queue;

import com.compomics.pladipus.shared.PladipusReportableException;

public interface QueueMessageController {
	public void removeMessagesByIdentifier(String identifier) throws PladipusReportableException;
}
