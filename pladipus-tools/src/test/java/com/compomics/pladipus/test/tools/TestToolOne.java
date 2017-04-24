package com.compomics.pladipus.test.tools;

import java.util.Map;

import com.compomics.pladipus.model.parameters.InputParameter;
import com.compomics.pladipus.model.parameters.InputType;
import com.compomics.pladipus.tools.annotations.PladipusTool;
import com.compomics.pladipus.tools.core.Tool;
import com.google.common.collect.ImmutableSet;

@PladipusTool(displayName = "One")
public class TestToolOne extends Tool {
	
	public TestToolOne() {
		super();
	}
	public TestToolOne(Map<String, String> parameters) {
		super(parameters);
	}

	private static ImmutableSet<InputParameter> allParams = ImmutableSet.of(
			new InputParameter("input_one", "First input param", true, InputType.FILE_MGF, "inputone.mgf"),
			new InputParameter("input_two", "Second input param", false, InputType.FILE_MGF_ZIP, "inputtwo.zip"),
			new InputParameter("input_no_default", "No default input param", false, InputType.FILE_MGF_ZIP),
			new InputParameter("input_no_default_mandatory", "No default, mandatory param", true, InputType.FILE_ZIP),
			new InputParameter("input_no_type", "No input type", true, "default_value"),
			new InputParameter("input_no_default_or_type", "No input type, no default param", false) 
			);

	@Override
	public ImmutableSet<InputParameter> getAllToolInputParameters() {
		return allParams;
	}

	@Override
	public String getJar() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getOutput() {
		// TODO Auto-generated method stub
		return null;
	}

}
