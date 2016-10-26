package com.compomics.pladipus.tools.core;

import com.compomics.pladipus.model.parameters.InputParameter;
import com.google.common.collect.ImmutableSet;

/**
 * All tools must extend this class.
 * Performs setup and running of a tool.
 *
 * TODO - Do we need to specify output types for a tool, for use as input parameters in other tools?
 * 		- Are we going to perform validation here?
 * 		- Pre-run setup, e.g. unzip files if necessary.  Tool-specific setup can override.
 * 		- Do actual running of tool from here, i.e. construct command line arguments
 * 		- Progress reporting from here?
 */
public abstract class Tool {
	public abstract ImmutableSet<InputParameter> getAllToolInputParameters();
}
