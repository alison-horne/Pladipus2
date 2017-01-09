package com.compomics.pladipus.model.core;

import java.util.HashMap;
import java.util.Map;
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
	private Map<String, Set<String>> globalParams = new HashMap<String, Set<String>>();
	private Map<String, Step> steps = new HashMap<String, Step>();
	private Map<String, Integer> defaultMappings = new HashMap<String, Integer>();
	
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
	public Map<String, Set<String>> getGlobalParameters() {
		return globalParams;
	}
	public void addParameterValues(String paramName, Set<String> values) {
		Set<String> existingVals = globalParams.get(paramName);
		if (existingVals == null) {
			globalParams.put(paramName, values);
		} else {
			if (values != null) {
				existingVals.addAll(values);
				globalParams.put(paramName, existingVals);
			}
		}
	}
	public void addStep(Step step) {
		steps.put(step.getStepIdentifier(), step);
	}
	public Map<String, Step> getSteps() {
		return steps;
	}
	
	public void addDefaultSub(String defName, int defId) {
		defaultMappings.put(defName, defId);
	}
}
