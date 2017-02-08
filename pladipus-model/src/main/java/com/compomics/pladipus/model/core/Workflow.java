package com.compomics.pladipus.model.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	private Map<String, String> substitutions = new HashMap<String, String>();
	
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
		if (values == null) {
			values = new HashSet<String>();
		}
		if (existingVals == null) {
			globalParams.put(paramName, values);
		} else {
			existingVals.addAll(values);
			globalParams.put(paramName, existingVals);
		}
	}
	public void addStep(Step step) {
		steps.put(step.getStepIdentifier(), step);
	}
	public Map<String, Step> getSteps() {
		return steps;
	}
	
	public void addSubstitution(String rawValue, String subValue) {
		substitutions.put(rawValue, subValue);
	}
	
	public Map<String, String> getSubstitutions() {
		return substitutions;
	}
	
	public void addStepSubstitutions() {
		for (Entry<String, Step> step: steps.entrySet()) {
			substitutions.put("{$" + step.getKey(), "{$" + step.getValue().getId());
		}
	}
	
	public void addGlobalSubstitution(String name, int id) {
		substitutions.put("{$GLOBAL." + name, "{$GLOBAL." + id);
	}
	
	public Parameter getParameter(int enclosingId, String paramName, Set<String> rawValues) {
		Parameter param = new Parameter(enclosingId);
		param.setParameterName(paramName);
		if (rawValues != null) {
			for (String rawVal: rawValues) {
				for (Entry<String, String> entry : substitutions.entrySet()) {
					rawVal = rawVal.replaceAll(Pattern.quote(entry.getKey()), Matcher.quoteReplacement(entry.getValue()));				
				}
				param.addValue(rawVal);
			}
		}
		return param;
	}
}
