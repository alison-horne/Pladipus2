package com.compomics.pladipus.base;

import com.compomics.pladipus.model.persist.User;
import com.compomics.pladipus.shared.PladipusReportableException;

public interface BatchControl {
	public void createBatch(String filepath, String workflowName, String batchName, User user) throws PladipusReportableException;
	public void replaceBatch(String filepath, String workflowName, String batchName, User user) throws PladipusReportableException;
	public void generateHeaders(String filepath, String workflowName, User user, boolean force) throws PladipusReportableException;
}
