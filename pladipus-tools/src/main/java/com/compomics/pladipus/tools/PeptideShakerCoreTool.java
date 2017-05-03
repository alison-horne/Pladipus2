package com.compomics.pladipus.tools;

import java.util.Map;

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
	
	public PeptideShakerCoreTool() {
		super();
	}
	public PeptideShakerCoreTool(Map<String, String> parameters) {
		super(parameters);
	}
	
	private static ImmutableSet<InputParameter> allParams = ImmutableSet.of(
			);
	
	@Override
	public ImmutableSet<InputParameter> getAllToolInputParameters() {
		return allParams;
	}

	@Override
	public int getDefaultTimeoutSeconds() {
		// TODO if needed, override timeout for tool
		return super.getDefaultTimeoutSeconds();
	}

	@Override
	public String getJar() {
		// TODO actual jar location
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
