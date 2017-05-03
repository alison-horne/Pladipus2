package com.compomics.pladipus.test.tools;

import java.util.Map;

import com.compomics.pladipus.model.parameters.InputParameter;
import com.compomics.pladipus.tools.annotations.PladipusTool;
import com.compomics.pladipus.tools.core.Tool;
import com.google.common.collect.ImmutableSet;

@PladipusTool(displayName = "Two")
public class TestToolTwo extends Tool {

	public TestToolTwo() {
		super();
	}
	public TestToolTwo(Map<String, String> parameters) {
		super(parameters);
	}
	
	@Override
	public ImmutableSet<InputParameter> getAllToolInputParameters() {
		return null;
	}

	@Override
	public String getJar() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Map<String, String> getOutputs() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public ImmutableSet<String> getErrorStrings() {
		// TODO Auto-generated method stub
		return null;
	}
}
