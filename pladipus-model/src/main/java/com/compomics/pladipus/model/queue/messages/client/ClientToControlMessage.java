package com.compomics.pladipus.model.queue.messages.client;

public class ClientToControlMessage {
	private ClientTask task;
	private String username;
	private String password;
	private String fileContent;
	private String batchName;
	
	public ClientTask getTask() {
		return task;
	}
	public void setTask(ClientTask task) {
		this.task = task;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
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
}
