package com.compomics.pladipus.model.queue.messages.client;

public class ControlToClientMessage {
	private ClientTaskStatus status;
	private String errorMsg;

	public ClientTaskStatus getStatus() {
		return status;
	}
	public void setStatus(ClientTaskStatus status) {
		this.status = status;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
}
