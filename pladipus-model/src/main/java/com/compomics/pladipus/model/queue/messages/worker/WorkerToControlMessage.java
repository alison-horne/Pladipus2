package com.compomics.pladipus.model.queue.messages.worker;

public class WorkerToControlMessage {
	private WorkerStatus status;
	private Long jobId;
	private String message;
	
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
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
