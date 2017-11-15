package com.compomics.pladipus.base;

import java.util.List;

import com.compomics.pladipus.model.core.RunOverview;
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
	public void abortBatch(long batchId) throws PladipusReportableException;
	public void restartBatch(long batchId, User user) throws PladipusReportableException;
	public void abortBatchRun(long batchRunId) throws PladipusReportableException;
	public void restartBatchRun(long batchRunId, long batchId, User user) throws PladipusReportableException;
	public List<RunOverview> batchStatus(long batchId) throws PladipusReportableException;
}
