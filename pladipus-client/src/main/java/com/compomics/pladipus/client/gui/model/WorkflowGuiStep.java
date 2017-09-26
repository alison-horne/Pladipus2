package com.compomics.pladipus.client.gui.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.compomics.pladipus.model.core.ToolInformation;
import com.compomics.pladipus.model.parameters.InputParameter;
import com.compomics.pladipus.model.parameters.Substitution;
import com.compomics.pladipus.model.persist.Parameter;
import com.compomics.pladipus.model.persist.Step;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.paint.Color;

public class WorkflowGuiStep {
	private StepIcon icon;
	private ToolInformation toolInfo;
	private String stepId;
	private Map<String, List<String>> parameters = new HashMap<String, List<String>>();
	private Set<String> subGlobals = new HashSet<String>();
	private Set<String> subDefaults = new HashSet<String>();
	private Map<String, Set<String>> subStepOutputs = new HashMap<String, Set<String>>();
	private Set<String> stepLinkNoOutputs = new HashSet<String>();
	private boolean invalidSub = false;
	private BooleanProperty deleted = new SimpleBooleanProperty(false);
	private int startRank = -1;
	
	public WorkflowGuiStep(ToolInformation toolInfo, String stepId) {
		this.toolInfo = toolInfo;
		this.stepId = stepId;
	}
	
	public WorkflowGuiStep(ToolInformation toolInfo, Step step) {
		this.toolInfo = toolInfo;
		stepId = step.getId();
		for (Parameter param : step.getParameters().getParameter()) {
			parameters.put(param.getName(), param.getValue());
		}
		if (step.getRunAfter() != null) {
			for (Step raStep: step.getRunAfter()) {
				stepLinkNoOutputs.add(raStep.getId());
			}
		}
		checkSubs();
	}
	
	public void deleteStep() {
		deleted.set(true);
	}
	public BooleanProperty deletedProperty() {
		return deleted;
	}
	
	public String getToolName() {
		return toolInfo.getToolName();
	}
	
	public ToolInformation getToolInfo() {
		return toolInfo;
	}
	public Set<String> getSubDefaults() {
		return subDefaults;
	}
	public Set<String> getSubGlobals() {
		return subGlobals;
	}
	public Map<String, Set<String>> getSubStepOutputs() {
		return subStepOutputs;
	}
	public Set<String> getStepLinkNoOutputs() {
		return stepLinkNoOutputs;
	}
	public void addStepLinkNoOutput(String id) {
		stepLinkNoOutputs.add(id);
	}
	public void removeStepLinkNoOutput(String id) {
		stepLinkNoOutputs.remove(id);
	}
	public void clearStepLinkNoOutput() {
		stepLinkNoOutputs.clear();
	}
	public Set<String> allPrereqStepIds() {
		Set<String> allIds = new HashSet<String>();
		allIds.addAll(stepLinkNoOutputs);
		allIds.addAll(subStepOutputs.keySet());
		return allIds;
	}
	
	public void checkSubs() {
		invalidSub = false;
		subDefaults.clear();
		subGlobals.clear();
		subStepOutputs.clear();
		for (List<String> vals: parameters.values()) {
			for (String val: vals) {
				stripSubs(val);
			}
		}
	}
	
	private void stripSubs(String value) {
		if (value.contains(Substitution.getPrefix())) {
	        int subStart = value.indexOf(Substitution.getPrefix());
	        int subEnd = -Substitution.getEnd().length();
	        int subMid = subStart;
	        while (subStart > -1) {
	        	subMid = value.indexOf(Substitution.getPrefix(), subStart  + 1);
	        	subEnd = value.indexOf(Substitution.getEnd(), subStart);
	        	if (subEnd < 0 || (subMid > 0 && subMid < subEnd)) {
	        		invalidSub = true;
	        		subEnd = subStart + 1;
	        	} else {
	            	sortSub(value.substring(subStart + Substitution.getPrefix().length(), subEnd));
	        	}
	        	subStart = value.indexOf(Substitution.getPrefix(), subEnd);
	        }
		}
	}
	
	private void sortSub(String subStr) {
		String[] split = subStr.split("\\.");
		if (split.length != 2) {
			invalidSub = true;
		} else {
			if (split[0].equalsIgnoreCase(Substitution.getDefault())) {
				subDefaults.add(split[1]);
			} else if (split[0].equalsIgnoreCase(Substitution.getGlobal())) {
				subGlobals.add(split[1]);
			} else {
				Set<String> vals = subStepOutputs.get(split[0]);
				if (vals == null) vals = new HashSet<String>();
				vals.add(split[1]);
				subStepOutputs.put(split[0], vals);
			}
		}
	}
	
	public Set<String> getOutputs() {
		Set<String> outputs = new HashSet<String>();
		for (String out: toolInfo.getOutputs()) {
			outputs.add(stepId + "." + out);
		}
		return outputs;
	}
	
	public StepIcon getIcon() {
		return icon;
	}
	
	public void initIcon(double size, Color color) {
		if (icon == null) {
			icon = new StepIcon(size, color, stepId);
			setIconComplete();
		}
	}
	
	public void setStepId(String stepId) {
		this.stepId = stepId;
		if (icon != null) {
			icon.updateLabel(stepId);
		}
	}
	public String getStepId() {
		return stepId;
	}
	
	public void removeParameter(String name) {
		parameters.remove(name);
	}
	public void perRunParameter(String name) {
		parameters.put(name, new ArrayList<String>());
	}
	public void updateParameter(String name, List<String> values) {
		parameters.put(name, values);
	}
	public List<String> getParamValues(String paramName) {
		return parameters.get(paramName);
	}
	public boolean paramsContainSub(String subName) {
		for (String param : parameters.keySet()) {
			for (String value: parameters.get(param)) {
				if (value.toUpperCase().contains(subName.toUpperCase())) return true;
			}
		}
		return false;
	}
	public Set<InputParameter> getParamsContainingSub(String subName) {
		Set<InputParameter> params = new HashSet<InputParameter>();
		for (String param: parameters.keySet()) {
			for (String value: parameters.get(param)) {
				if (value.toUpperCase().contains(subName.toUpperCase())) {
					InputParameter ip = toolInfo.getParameterByName(param);
					if (ip != null) {
						params.add(ip);
						continue;
					}
				}
			}
		}
		return params;
	}
	
	public void setIconComplete() {
		if (icon != null) {
			icon.setComplete(isComplete());
		}
	}
	
	public boolean allMandatoryParamsSet() {
		for (InputParameter mand : toolInfo.getMandatoryParametersNoDefault()) {
			if (!parameters.containsKey(mand.getParamName())) {
				return false;
			}
		}
		return true;
	}
	
	public void setInvalidSub(boolean invalid) {
		invalidSub = invalid;
	}
	public boolean isInvalidSub() {
		return invalidSub;
	}

	public boolean isComplete() {
		return (!invalidSub && allMandatoryParamsSet());
	}
	
	public Step toStep() {
		Step s = new Step();
		s.setId(stepId);
		s.setName(toolInfo.getToolName());
		for (String param: parameters.keySet()) {
			Parameter p = new Parameter();
			p.setName(param);
			for (String value: parameters.get(param)) {
				p.getValue().add(value);
			}
			s.getParameters().getParameter().add(p);
		}
		return s;
	}
	
	public void setRank(int rank) {
		this.startRank = rank;
	}
	public int getRank() {
		return startRank;
	}
}
