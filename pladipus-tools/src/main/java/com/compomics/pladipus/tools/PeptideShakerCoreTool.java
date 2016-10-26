package com.compomics.pladipus.tools;

import com.compomics.pladipus.model.parameters.InputParameter;
import com.compomics.pladipus.tools.annotations.PladipusTool;
import com.compomics.pladipus.tools.core.Tool;
import com.google.common.collect.ImmutableSet;

/**
 * Peptide Shaker tool base.
 *  
 * TODO Full set of parameters, jar, etc.  Flesh out properly once Tool abstract class completed.
 * Different forms of Peptide Shaker tool - reports...
 */
@PladipusTool(displayName = "Peptide Shaker")
public class PeptideShakerCoreTool extends Tool {
	
	private static ImmutableSet<InputParameter> allParams = ImmutableSet.of(
			);
	
	@Override
	public ImmutableSet<InputParameter> getAllToolInputParameters() {
		return allParams;
	}
}
