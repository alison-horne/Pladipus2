package com.compomics.pladipus.model.queue.messages.worker;

import java.util.HashMap;
import java.util.Map;

public class WorkerToControlMessage {
	private WorkerStatus status;
	private Long jobId;
	private Map<String, String> outputs = new HashMap<String, String>();
	public String errorMessage;
	
	public void setStatus(WorkerStatus status) {
		this.status = status;
	}
	public WorkerStatus getStatus() {
		return status;
	}
	
	public void setJobId(Long id) {
		this.jobId = id;
	}
	public Long getJobId() {
		return jobId;
	}
	
	public Map<String, String> getOutputs() {
		return outputs;
	}
	public void setOutputs(Map<String, String> outputs) {
		this.outputs = outputs;
	}
	public void addOutput(String name, String value) {
		outputs.put(name, value);
	}

	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
