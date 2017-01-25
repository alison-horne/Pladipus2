package com.compomics.pladipus.base;

import com.compomics.pladipus.model.core.TaskStatus;
import com.compomics.pladipus.shared.PladipusReportableException;

public interface QueueControl {
	public void process(String batchName, int userId) throws PladipusReportableException;
	public void restart(String batchName, int userId) throws PladipusReportableException;
	public void abort(String batchName, int userId) throws PladipusReportableException;
	public TaskStatus status(String batchName, int userId) throws PladipusReportableException;
}
