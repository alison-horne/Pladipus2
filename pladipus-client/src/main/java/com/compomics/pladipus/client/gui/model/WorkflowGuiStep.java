package com.compomics.pladipus.client.gui.model;

import com.compomics.pladipus.model.core.ToolInformation;

import javafx.scene.paint.Color;

public class WorkflowGuiStep {
	private StepIcon icon;
	private WorkflowGui workflowGui;
	private ToolInformation toolInfo;
	private String stepId;
	
	public WorkflowGuiStep(WorkflowGui workflowGui, ToolInformation toolInfo, String stepId) {
		this.workflowGui = workflowGui;
		this.toolInfo = toolInfo;
		this.stepId = stepId;
	}
	
	public String getToolName() {
		return toolInfo.getToolName();
	}
	
	public StepIcon getIcon() {
		return icon;
	}
	
	public void initIcon(double size, Color color) {
		if (icon == null) {
			icon = new StepIcon(this, size, color, stepId);
		}
	}
	
	public void setStepId(String stepId) {
		this.stepId = stepId; // TODO if icon not null, update label
	}
	
	public WorkflowGui getWorkflowGui() {
		return workflowGui;
	}
}
