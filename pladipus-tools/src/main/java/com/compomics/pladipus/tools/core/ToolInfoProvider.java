package com.compomics.pladipus.tools.core;

import com.compomics.pladipus.shared.PladipusReportableException;
import com.google.common.collect.ImmutableMap;

/**
 * Provides information about all available tools, for use in the GUI.
 */
public interface ToolInfoProvider {
	
	public ImmutableMap<String, ToolInfo> getAllToolInfo() throws PladipusReportableException;
}
