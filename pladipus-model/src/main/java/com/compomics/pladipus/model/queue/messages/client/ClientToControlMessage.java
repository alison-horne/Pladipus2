package com.compomics.pladipus.model.queue.messages.client;

public class ClientToControlMessage {
	private ClientTask task;
	private String username;
	private String email;
	private String password;
	private String fileContent;
	private String batchName;
	private String workflowName;
	private String defaultName;
	private String defaultValue;
	private String defaultType;
	private Boolean defaultGlobal;
	private Boolean batchRun;
	private Long batchId;
	private Long batchRunId;
	
	public ClientToControlMessage() {}
	
	public ClientToControlMessage(ClientTask task) {
		this.task = task;
	}
	
	public ClientTask getTask() {
		return task;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getFileContent() {
		return fileContent;
	}
	public void setFileContent(String fileContent) {
		this.fileContent = fileContent;
	}
	public String getBatchName() {
		return batchName;
	}
	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}
	public String getWorkflowName() {
		return workflowName;
	}
	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}
	public String getDefaultName() {
		return defaultName;
	}
	public void setDefaultName(String defaultName) {
		this.defaultName = defaultName;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public String getDefaultType() {
		return defaultType;
	}
	public void setDefaultType(String defaultType) {
		this.defaultType = defaultType;
	}
	public Boolean getDefaultGlobal() {
		return defaultGlobal;
	}
	public void setDefaultGlobal(boolean defaultGlobal) {
		this.defaultGlobal = defaultGlobal;
	}
	public Boolean getBatchRun() {
		return batchRun;
	}
	public void setBatchRun(boolean batchRun) {
		this.batchRun = batchRun;
	}
	public Long getBatchId() {
		return batchId;
	}
	public void setBatchId(long batchId) {
		this.batchId = batchId;
	}
	public Long getBatchRunId() {
		return batchRunId;
	}
	public void setBatchRunId(long batchRunId) {
		this.batchRunId = batchRunId;
	}
}
