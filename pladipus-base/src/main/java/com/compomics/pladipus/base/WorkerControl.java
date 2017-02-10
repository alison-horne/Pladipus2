package com.compomics.pladipus.base;

import com.compomics.pladipus.model.persist.User;
import com.compomics.pladipus.shared.PladipusReportableException;

public interface WorkerControl {
	public void startWorker(User user) throws PladipusReportableException;
}
