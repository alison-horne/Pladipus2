package com.compomics.pladipus.model.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Workflow step from workflow template
 */
public class Step extends UpdateTracked {
	
	private int id = -1;
	private int workflowId = -1;
	private String stepIdentifier;
	private String toolType;
	private List<Parameter> stepParameters = new ArrayList<Parameter>();
	
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
	
	public void addParameters(List<Parameter> params) {
		stepParameters.addAll(params);
	}
	
	public List<Parameter> getStepParameters() {
		return stepParameters;
	}
}
