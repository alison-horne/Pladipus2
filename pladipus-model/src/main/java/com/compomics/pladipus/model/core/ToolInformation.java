package com.compomics.pladipus.model.core;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import com.compomics.pladipus.model.parameters.InputParameter;

public class ToolInformation {
	private String toolName;
	private Set<InputParameter> parameters = new HashSet<InputParameter>();
	private Set<String> outputs = new HashSet<String>();
	
	public ToolInformation(){}
	public ToolInformation(String toolName, Set<InputParameter> parameters, Set<String> outputs) {
		this.toolName = toolName;
		this.parameters = parameters;
		this.outputs = outputs;
	}
	
	public String getToolName() {
		return toolName;
	}
	public void setToolName(String toolName) {
		this.toolName = toolName;
	}
	public Set<InputParameter> getParameters() {
		return parameters;
	}
	public void setParameters(Set<InputParameter> parameters) {
		this.parameters = parameters;
	}
	public Set<String> getOutputs() {
		return outputs;
	}
	public void setOutputs(Set<String> outputs) {
		this.outputs = outputs;
	}
	public Set<InputParameter> getMandatoryParametersWithDefault() {
		return filterParameters(ip -> (ip.isMandatory() && ip.hasDefaultValue()));
	}
	public Set<InputParameter> getMandatoryParametersNoDefault() {
		return filterParameters(ip -> (ip.isMandatory() && !ip.hasDefaultValue()));
	}
	public Set<InputParameter> getOptionalParametersWithDefault() {
		return filterParameters(ip -> (!ip.isMandatory() && ip.hasDefaultValue()));
	}
	public Set<InputParameter> getOptionalParametersNoDefault() {
		return filterParameters(ip -> (!ip.isMandatory() && !ip.hasDefaultValue()));
	}
	
	private Set<InputParameter> filterParameters(Predicate<InputParameter> pred) {
		Set<InputParameter> filterParams = new HashSet<InputParameter>();
		if (parameters != null) {
			for (InputParameter param: parameters) {
				if (pred.test(param)) {
					filterParams.add(param);
				}
			}
		}
		return filterParams;
	}
	public InputParameter getParameterByName(String name) {
		for (InputParameter param: parameters) {
			if (param.getParamName().equalsIgnoreCase(name)) return param;
		}
		return null;
	}
}
