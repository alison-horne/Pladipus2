package com.compomics.pladipus.client.gui.model;

import java.util.HashSet;
import java.util.Set;

import com.compomics.pladipus.model.core.DefaultOverview;
import com.compomics.pladipus.model.parameters.Substitution;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

public class GuiSubstitutions {
	private ObservableList<GlobalParameterGui> globals;
	private ObservableList<DefaultOverview> defaults;
	private ObservableList<StepOutput> stepOuts;
	private Set<WorkflowGuiStep> steps;
	
	public GuiSubstitutions(ObservableList<DefaultOverview> defaults) {
		this.defaults = defaults;
	}
	
	public GuiSubstitutions(ObservableList<DefaultOverview> defaults, ObservableList<GlobalParameterGui> globals) {
		this.defaults = defaults;
		this.globals = globals;
		steps = new HashSet<WorkflowGuiStep>();
		stepOuts = FXCollections.observableArrayList();
	}
	
	public void addStep(WorkflowGuiStep step) {
		steps.add(step);
		for (String output: step.getToolInfo().getOutputs()) {
			stepOuts.add(new StepOutput(step.getStepId(), output));
		}
	}
	public ObservableList<StepOutput> getStepOutputs() {
		return stepOuts;
	}
	public ObservableList<DefaultOverview> getDefaults() {
		return defaults;
	}
	public ObservableList<GlobalParameterGui> getGlobals() {
		return globals;
	}
	
	public boolean validDefault(String defaultName) {
		for (DefaultOverview def: defaults) {
			if (def.getName().equalsIgnoreCase(defaultName)) return true;
		}
		return false;
	}
	public boolean validGlobal(String globalName) {
		if (globals != null) {
			for (GlobalParameterGui glob: globals) {
				if (glob.getGlobalName().equalsIgnoreCase(globalName)) return true;
			}
		}
		return false;
	}
	public Color getValidStepColor(String stepId, String outId) {
		if (steps != null) {
			for (WorkflowGuiStep step: steps) {
				if (step.getStepId().equalsIgnoreCase(stepId)) {
					if (step.getToolInfo().getOutputs().contains(outId) && // TODO case sensitive...
							step.getIcon() != null) return step.getIcon().getColor();
				}
			}
		}
		return null;
	}
	 
	class DisplaySubstitution {
		private Color color;
		private String fullText;
		private String displayText;
		private boolean validSub = true;
		
		DisplaySubstitution(String subText) {
			if (subText.startsWith(Substitution.getPrefix())) subText = subText.substring(Substitution.getPrefix().length());
			if (subText.endsWith(Substitution.getEnd())) subText = subText.substring(0, subText.length() - Substitution.getEnd().length());
			fullText = Substitution.getPrefix() + subText + Substitution.getEnd();
			String[] split = subText.split("\\.");
			if (split.length != 2) {
				invalidSub();
			} else {
				if (split[0].equalsIgnoreCase(Substitution.getGlobal())) {
					if (validGlobal(split[1])) {
						displayText = split[1];
						color = ToolColors.getGlobalColor();
					} else {
						invalidSub();
					}
				} else if (split[0].equalsIgnoreCase(Substitution.getDefault())) {
					if (validDefault(split[1])) {
						displayText = split[1];
						color = ToolColors.getDefaultColor();
					} else {
						invalidSub();
					}
				} else {
					color = getValidStepColor(split[0], split[1]);
					if (color != null) {
						displayText = subText;
					} else {
						invalidSub();
					}
				}
			}
		}
		
		void invalidSub() {
			validSub = false;
			displayText = fullText;
			color = Color.GRAY;
		}
		
		public Color getColor() {
			return color;
		}
		public String getDisplayText() {
			return " " + displayText + " ";
		}
		public String getFullText() {
			return fullText;
		}
		public boolean isValidSub() {
			return validSub;
		}
	}
}
