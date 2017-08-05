package com.compomics.pladipus.client.gui.model;

import com.compomics.pladipus.model.core.ToolInformation;
import com.compomics.pladipus.model.persist.Step;
import com.compomics.pladipus.shared.PladipusReportableException;

import javafx.scene.paint.Color;

public class WorkflowGuiStep {
	private StepIcon icon;
	private ToolInformation toolInfo;
	private String stepId;
	private Step step;
	
	public WorkflowGuiStep(ToolInformation toolInfo, String stepId) {
		this.toolInfo = toolInfo;
		this.stepId = stepId;
	}
	
	public WorkflowGuiStep(ToolInformation toolInfo, Step step) {
		this.toolInfo = toolInfo;
		this.step = step;
		stepId = step.getId();
	}

	public void validate() throws PladipusReportableException {
		if (step != null) {
			// Get parameters sorted
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
			icon = new StepIcon(size, color, stepId);
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
	
	public boolean isParamChange() { //TODO
		return true;
	}
	
	public void clearParamChanges() {
		//TODO
	}
}
