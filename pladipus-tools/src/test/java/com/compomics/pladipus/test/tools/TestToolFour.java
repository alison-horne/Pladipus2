package com.compomics.pladipus.test.tools;

import com.compomics.pladipus.model.parameters.InputParameter;
import com.compomics.pladipus.tools.annotations.PladipusTool;
import com.compomics.pladipus.tools.core.Tool;
import com.google.common.collect.ImmutableSet;

@PladipusTool(displayName = "Four")
public class TestToolFour extends Tool {

	@Override
	public ImmutableSet<InputParameter> getAllToolInputParameters() {
		return null;
	}

}
