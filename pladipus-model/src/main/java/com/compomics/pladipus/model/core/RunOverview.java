package com.compomics.pladipus.model.core;

import java.util.ArrayList;
import java.util.List;

import com.compomics.pladipus.model.persist.RunStatus;

public class RunOverview {
	
	long id;
	long batchRunId;
	RunStatus status;
	List<RunStepOverview> steps;
	
	public RunOverview() {
		this(-1, -1, null);
	}
	
	public RunOverview(long id, long batchRunId, RunStatus status) {
		this.id = id;
		this.batchRunId = batchRunId;
		this.status = status;
		steps = new ArrayList<RunStepOverview>();
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getBatchRunId() {
		return batchRunId;
	}
	public void setBatchRunId(long batchRunId) {
		this.batchRunId = batchRunId;
	}
	public RunStatus getStatus() {
		return status;
	}
	public void setStatus(RunStatus status) {
		this.status = status;
	}
	public List<RunStepOverview> getSteps() {
		return steps;
	}
	public void setSteps(List<RunStepOverview> steps) {
		this.steps.clear();
		if (steps != null) {
			this.steps = steps;
		}
	}
	public void addStep(RunStepOverview rso) {
		steps.add(rso);
	}
}
