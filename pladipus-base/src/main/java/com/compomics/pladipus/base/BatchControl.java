package com.compomics.pladipus.base;

import java.util.List;

import com.compomics.pladipus.model.persist.User;
import com.compomics.pladipus.shared.PladipusReportableException;

public interface BatchControl {
	public void createBatch(String csvString, String workflowName, String batchName, User user) throws PladipusReportableException;
	public void replaceBatch(String csvString, String workflowName, String batchName, User user) throws PladipusReportableException;
	public List<String> generateHeaders(String workflowName, User user) throws PladipusReportableException;
}
