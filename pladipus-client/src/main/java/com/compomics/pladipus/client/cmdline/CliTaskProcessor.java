package com.compomics.pladipus.client.cmdline;

import org.apache.commons.cli.ParseException;

import com.compomics.pladipus.shared.PladipusReportableException;

public interface CliTaskProcessor {
	public void login(String username, String password) throws ParseException, PladipusReportableException;
	public void doTemplateTask(String filepath, boolean force) throws PladipusReportableException;
	public void doBatchTask(String filepath, String workflowName, String batchName, boolean force) throws PladipusReportableException;
	public void doProcessTask(String batchName, boolean force) throws PladipusReportableException;
	public void doStatusTask(String batchName) throws PladipusReportableException;
	public void doGenerateTask(String filepath, String workflowName, boolean force) throws PladipusReportableException;
	public void doDefaultTask(String defaultName, String defaultValue, String defaultType) throws PladipusReportableException;
	public void doAbortTask(String batchName) throws PladipusReportableException;
}
