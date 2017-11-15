package com.compomics.pladipus.model.core;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class WorkerRunOverview {
	
	StringProperty workerId;
	BooleanProperty inProgress;
	StringProperty errorMsg;
	Map<String, String> outputs;
	
	public WorkerRunOverview() {
		this(null, false, null);
	}
	
	public WorkerRunOverview(String workerId, boolean inProgress, String errorMsg) {
		this.workerId = new SimpleStringProperty(workerId);
		this.inProgress = new SimpleBooleanProperty(inProgress);
		this.errorMsg = new SimpleStringProperty(errorMsg);
		outputs = new HashMap<String, String>();
	}
	
	public String getWorkerId() {
		return workerId.get();
	}
	public void setWorkerId(String workerId) {
		this.workerId.set(workerId);
	}
	public StringProperty workerIdProperty() {
		return workerId;
	}
	public boolean isInProgress() {
		return inProgress.get();
	}
	public void setInProgress(boolean inProgress) {
		this.inProgress.set(inProgress);
	}
	public BooleanProperty inProgressProperty() {
		return inProgress;
	}
	public String getErrorMsg() {
		return errorMsg.get();
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg.set(errorMsg);
	}
	public StringProperty errorMsgProperty() {
		return errorMsg;
	}
	public Map<String, String> getOutputs() {
		return outputs;
	}
	public void setOutputs(Map<String, String> outputs) {
		this.outputs.clear();
		if (outputs != null) {
			this.outputs = outputs;
		}
	}
	public void addOutput(String name, String value) {
		this.outputs.put(name, value);
	}
}
