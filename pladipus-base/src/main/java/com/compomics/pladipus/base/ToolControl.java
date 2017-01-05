package com.compomics.pladipus.base;

import com.compomics.pladipus.model.exceptions.PladipusReportableException;
import com.compomics.pladipus.tools.core.ToolInfo;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * Gets information about available tools, for use in the GUI
 */
public interface ToolControl {

	public ImmutableMap<String, ToolInfo> getAllToolInfo() throws PladipusReportableException;
	public ImmutableSet<String> getToolNames() throws PladipusReportableException;
	public ToolInfo getInfoForTool(String toolName) throws PladipusReportableException;
	public ImmutableSet<String> getMandatoryParameters(String toolName) throws PladipusReportableException;
	public ImmutableSet<String> getOptionalParameters(String toolName) throws PladipusReportableException;
}
