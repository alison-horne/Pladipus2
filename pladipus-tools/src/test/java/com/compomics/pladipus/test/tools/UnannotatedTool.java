package com.compomics.pladipus.test.tools;

import com.compomics.pladipus.model.parameters.InputParameter;
import com.compomics.pladipus.tools.core.Tool;
import com.google.common.collect.ImmutableSet;

/**
 * Blank test class used to check that Tool subclasses NOT annotated @PladipusTool are not registered
 *
 */
public class UnannotatedTool extends Tool {

	@Override
	public ImmutableSet<InputParameter> getAllToolInputParameters() {
		return null;
	}

}
