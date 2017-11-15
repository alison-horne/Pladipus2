package com.compomics.pladipus.model.core;

import java.util.ArrayList;
import java.util.List;

import com.compomics.pladipus.model.persist.RunStatus;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RunStepOverview {
	
	StringProperty name;
	StringProperty tool;
	RunStatus status;
	long id;
	List<WorkerRunOverview> workers;
	
	public RunStepOverview() {
		this(null, null, null, -1);
	}
	
	public RunStepOverview(String name, String tool, RunStatus status, long id) {
		this.name = new SimpleStringProperty(name);
		this.tool = new SimpleStringProperty(tool);
		this.status = status;
		this.id = id;
		workers = new ArrayList<WorkerRunOverview>();
	}
	
	public String getName() {
		return name.get();
	}
	public void setName(String name) {
		this.name.set(name);
	}
	public StringProperty nameProperty() {
		return name;
	}
	public String getTool() {
		return tool.get();
	}
	public void setTool(String tool) {
		this.tool.set(tool);
	}
	public StringProperty toolProperty() {
		return tool;
	}
	public RunStatus getStatus() {
		return status;
	}
	public void setStatus(RunStatus status) {
		this.status = status;
	}
	
	public long getid() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public List<WorkerRunOverview> getWorkers() {
		return workers;
	}
	public void setWorkers(List<WorkerRunOverview> workers) {
		this.workers.clear();
		if (workers != null) {
			this.workers = workers;
		}
	}
	public void addWorker(WorkerRunOverview wro) {
		workers.add(wro);
	}
}
