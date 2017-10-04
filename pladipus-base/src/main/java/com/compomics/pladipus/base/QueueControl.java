package com.compomics.pladipus.base;

import com.compomics.pladipus.model.core.TaskStatus;
import com.compomics.pladipus.model.persist.Batch;
import com.compomics.pladipus.model.persist.User;
import com.compomics.pladipus.shared.PladipusReportableException;

public interface QueueControl {
	public void process(String batchName, User user) throws PladipusReportableException;
	public void restart(String batchName, User user) throws PladipusReportableException;
	public void abort(String batchName, User user) throws PladipusReportableException;
	public TaskStatus status(String batchName, User user) throws PladipusReportableException;
	public void processBatch(Batch batch, User user) throws PladipusReportableException;
}
