package com.compomics.pladipus.tools.core.impl;

import com.compomics.pladipus.model.parameters.InputParameter;
import com.compomics.pladipus.tools.core.ToolInfo;
import com.google.common.collect.ImmutableSet;

public class PladipusToolInfo implements ToolInfo {

	private ImmutableSet<InputParameter> inputParams;
	
	@Override
	public ImmutableSet<InputParameter> getInputParams() {
		return inputParams;
	}
	
	@Override
	public void setInputParams(ImmutableSet<InputParameter> inputParams) {
		this.inputParams = inputParams;
	}
}
