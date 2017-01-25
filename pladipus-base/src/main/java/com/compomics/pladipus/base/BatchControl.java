package com.compomics.pladipus.base;

import com.compomics.pladipus.shared.PladipusReportableException;

public interface BatchControl {
	public void createBatch(String filepath, String workflowName, String batchName, int userId) throws PladipusReportableException;
	public void replaceBatch(String filepath, String workflowName, String batchName, int userId) throws PladipusReportableException;
	public void generateHeaders(String filepath, String workflowName, int userId, boolean force) throws PladipusReportableException;
}
