package com.compomics.pladipus.model.core;

import java.util.HashSet;
import java.util.Set;

import com.compomics.pladipus.model.db.WorkflowsColumn;

/**
 * Workflow template
 */
public class Workflow extends UpdateTracked {

	private int id = -1;
	private String workflowName;
	private String template;
	private int userId = -1;
	private boolean active = true;
	private Set<Parameter> globalParams = new HashSet<Parameter>();
	private Set<Step> steps = new HashSet<Step>();
	
	public String getWorkflowName() {
		return workflowName;
	}
	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
		trackColumn(WorkflowsColumn.ACTIVE.name());
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Set<Parameter> getGlobalParameters() {
		return globalParams;
	}
	public void addGlobalParameters(Set<Parameter> params) {
		globalParams.addAll(params);
	}
	public void addStep(Step step) {
		steps.add(step);
	}
}
