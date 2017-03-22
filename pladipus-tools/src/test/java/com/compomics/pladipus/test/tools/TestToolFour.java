package com.compomics.pladipus.test.tools;

import java.util.Map;

import com.compomics.pladipus.model.parameters.InputParameter;
import com.compomics.pladipus.tools.annotations.PladipusTool;
import com.compomics.pladipus.tools.core.Tool;
import com.google.common.collect.ImmutableSet;

@PladipusTool(displayName = "Four")
public class TestToolFour extends Tool {

	public TestToolFour() {
		super();
	}
	public TestToolFour(Map<String, String> parameters) {
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

}
