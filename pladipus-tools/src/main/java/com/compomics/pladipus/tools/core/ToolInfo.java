package com.compomics.pladipus.tools.core;

import com.compomics.pladipus.model.parameters.InputParameter;
import com.google.common.collect.ImmutableSet;

/**
 * Information about a tool to be provided to the GUI.
 */
public interface ToolInfo {
	public ImmutableSet<InputParameter> getInputParams();
	public void setInputParams(ImmutableSet<InputParameter> inputParams);
}
