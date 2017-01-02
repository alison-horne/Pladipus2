package com.compomics.pladipus.base;

import com.compomics.pladipus.model.exceptions.PladipusReportableException;

public interface WorkerControl {
	public void startWorker(int userId) throws PladipusReportableException;
}