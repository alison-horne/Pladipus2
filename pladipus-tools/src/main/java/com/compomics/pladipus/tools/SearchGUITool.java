package com.compomics.pladipus.tools;

import java.util.Map;

import com.compomics.pladipus.model.parameters.InputParameter;
import com.compomics.pladipus.model.parameters.InputType;
import com.compomics.pladipus.tools.annotations.PladipusTool;
import com.compomics.pladipus.tools.core.Tool;
import com.google.common.collect.ImmutableSet;

/**
 * SearchGUI Tool
 * 
 * TODO Full set of parameters, jar, etc.  Flesh out properly once Tool abstract class completed.
 */
@PladipusTool(displayName = "SearchGUI")
public class SearchGUITool extends Tool {
	
	public SearchGUITool() {
		super();
	}
	public SearchGUITool(Map<String, String> parameters) {
		super(parameters);
	}
	
	private static ImmutableSet<InputParameter> allParams = ImmutableSet.of(
			new InputParameter("spectrum_files", 
							   "Spectrum files (mgf format), comma separated list or an entire folder.",
							   true,
							   InputType.FILE_MGF_ZIP)
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
