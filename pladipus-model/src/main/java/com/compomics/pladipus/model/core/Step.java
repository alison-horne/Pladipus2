package com.compomics.pladipus.model.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Workflow step from workflow template
 */
public class Step extends UpdateTracked {
	
	private int id = -1;
	private int workflowId = -1;
	private String stepIdentifier;
	private String toolType;
	private Map<String, Set<String>> stepParameters = new HashMap<String, Set<String>>();
	private Set<String> stepDependencies = new HashSet<String>();
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public void setWorkflowId(int workflowId) {
		this.workflowId = workflowId;
	}
	
	public int getWorkflowId() {
		return workflowId;
	}
	
	public void setStepIdentifier(String identifier) {
		this.stepIdentifier = identifier;
	}
	
	public String getStepIdentifier() {
		return stepIdentifier;
	}
	
	public void setToolType(String toolType) {
		this.toolType = toolType;
	}
	
	public String getToolType() {
		return toolType;
	}
	
	public void addParameterValues(String paramName, Set<String> values) {
		Set<String> existingVals = stepParameters.get(paramName);
		if (existingVals == null) {
			stepParameters.put(paramName, values);
		} else {
			existingVals.addAll(values);
			stepParameters.put(paramName, existingVals);
		}
	}
	
	public Map<String, Set<String>> getStepParameters() {
		return stepParameters;
	}
	
	public void addDependency(String step) {
		stepDependencies.add(step);
	}
	
	public Set<String> getDependencies() {
		return stepDependencies;
	}
}
