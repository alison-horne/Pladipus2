package com.compomics.pladipus.model.queue.messages.worker;

import java.util.HashMap;
import java.util.Map;

public class WorkerTaskMessage {
	int timeout = -1;
	WorkerDirectTask task;
	String toolName;
	Map<String, String> parameters = new HashMap<String, String>();
	Long jobId;
	
	public void setTimeout(int millis) {
		this.timeout = millis;
	}
	public int getTimeout() {
		return timeout;
	}
	
	public void setTask(WorkerDirectTask task) {
		this.task = task;
	}
	public WorkerDirectTask getTask() {
		return task;
	}
	
	public void setToolname(String toolname) {
		this.toolName = toolname;
	}
	public String getToolname() {
		return toolName;
	}
	
	public void addParameter(String name, String value) {
		parameters.put(name, value);
	}
	public void addParameters(Map<String, String> params) {
		parameters.putAll(params);
	}
	public Map<String, String> getParameters() {
		return parameters;
	}
	
	public void setJobId(Long id) {
		this.jobId = id;
	}
	public Long getJobId() {
		return jobId;
	}
}
