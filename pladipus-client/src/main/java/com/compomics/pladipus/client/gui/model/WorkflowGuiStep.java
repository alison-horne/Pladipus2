package com.compomics.pladipus.client.gui.model;

import java.util.NoSuchElementException;

import com.compomics.pladipus.model.core.ToolInformation;
import com.compomics.pladipus.model.persist.Step;
import com.compomics.pladipus.shared.PladipusReportableException;

import javafx.scene.paint.Color;

public class WorkflowGuiStep {
	private StepIcon icon;
	private WorkflowGui workflowGui;
	private ToolInformation toolInfo;
	private String stepId;
	private Step step;
	
	public WorkflowGuiStep(WorkflowGui workflowGui, ToolInformation toolInfo, String stepId) {
		this.workflowGui = workflowGui;
		this.toolInfo = toolInfo;
		this.stepId = stepId;
	}
	
	public WorkflowGuiStep(WorkflowGui workflowGui, Step step) {
		this.workflowGui = workflowGui;
		this.step = step;
		stepId = step.getId();
	}
	
	public void validate() throws PladipusReportableException {
		if (step != null) {
			toolInfo = workflowGui.getTool(step.getName());
		}
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
	
	public void setStepId(String stepId) { // TODO check for unique / valid type?
		this.stepId = stepId;
		if (icon != null) {
			icon.updateLabel(stepId);
		}
	}
	public String getStepId() {
		return stepId;
	}
	
	public WorkflowGui getWorkflowGui() {
		return workflowGui;
	}
	
	public boolean isParamChange() { //TODO
		return true;
	}
	
	public void clearParamChanges() {
		//TODO
	}
}
