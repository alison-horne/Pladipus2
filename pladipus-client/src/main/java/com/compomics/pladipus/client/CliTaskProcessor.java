package com.compomics.pladipus.client;

public interface CliTaskProcessor {
	public void login(String username, String password);
	public void doTemplateTask(String filepath, boolean force);
	public void doBatchTask(String filepath, String workflowName, String batchName, boolean force);
	public void doProcessTask(String batchName, boolean force);
	public void doStatusTask(String batchName);
	public void doGenerateTask(String filepath, String workflowName, boolean force);
	public void doDefaultTask(String defaultName, String defaultValue, String defaultType);
	public void doAbortTask(String batchName);
}
