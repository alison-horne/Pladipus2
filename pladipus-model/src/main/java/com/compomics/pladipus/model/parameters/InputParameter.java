package com.compomics.pladipus.model.parameters;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Input parameter - a command line argument passed through when running a tool.
 * 
 * TODO (here or elsewhere)
 * 		- Prerequisites (e.g. if parameter present, or with certain value = Windows only)
 * 		- Validation - regex, or allowed values (e.g. 0 or 1)
 *		- Flesh out input types.  Allow multiple potential types (file, directory, zip...)
 *		- Allow multi-value, e.g. comma-separated list of files
 *		- Mapping to global parameters, e.g. have one output folder parameter
 *		- Parameters which take no arguments
 *		- Document this better!
 */
public class InputParameter {
	
	/**
	 * The command line argument name.
	 */
	private String paramName;
	
	/**
	 * Friendly description to show to the user when creating their workflows.
	 */
	private String description;
	
	/**
	 * Type of value expected, for validation.
	 */
	private InputType inputType;
	
	/**
	 * True if presence of the parameter is mandatory for the tool to run.
	 */
	private boolean mandatory;
	
	/**
	 * Default value to run tool with, if no value provided.
	 */
	private String defaultValue;
	
	public InputParameter(){}
	public InputParameter(String paramName, String description, boolean mandatory, InputType inputType, String defaultValue) {
		this.paramName = paramName;
		this.description = description;
		this.mandatory = mandatory;
		this.inputType = inputType;
		this.defaultValue = defaultValue;
	}
	
	public InputParameter(String paramName, String description, boolean mandatory, InputType inputType) {
		this(paramName, description, mandatory,  inputType, "");
	}
	
	public InputParameter(String paramName, String description, boolean mandatory, String defaultValue) {
		this(paramName, description, mandatory, InputType.STRING, defaultValue);
	}
	
	public InputParameter(String paramName, String description, boolean mandatory) {
		this(paramName, description, mandatory, InputType.STRING, "");
	}
	
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	public String getParamName() {
		return paramName;
	}
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@JsonIgnore
	public InputType getInputType() {
		return inputType;
	}
	
	public boolean isMandatory() {
		return mandatory;
	}
	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}
	
	public boolean isTypeValid(String value) {
		//TODO - validation of supplied value against InputType
		return true;
	}
	public boolean hasDefaultValue() {
		return ((defaultValue != null) && !defaultValue.isEmpty()); 
	}
}
