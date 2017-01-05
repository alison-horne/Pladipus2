package com.compomics.pladipus.base.impl;

import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;

import com.compomics.pladipus.base.ToolControl;
import com.compomics.pladipus.model.exceptions.PladipusMessages;
import com.compomics.pladipus.model.exceptions.PladipusReportableException;
import com.compomics.pladipus.model.parameters.InputParameter;
import com.compomics.pladipus.tools.core.ToolInfo;
import com.compomics.pladipus.tools.core.ToolInfoProvider;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

public class PladipusToolControl implements ToolControl {
	
	@Autowired
	private ToolInfoProvider pladipusToolInfoProvider;

	private ImmutableMap<String, ToolInfo> allToolInfo;
	
	@Autowired
	private PladipusMessages exceptionMessages;
	
	@Override
	public ImmutableMap<String, ToolInfo> getAllToolInfo() throws PladipusReportableException {
		if (allToolInfo == null || allToolInfo.isEmpty()) {
			allToolInfo = pladipusToolInfoProvider.getAllToolInfo();
		}
		return allToolInfo;
	}
	
	@Override
	public ImmutableSet<String> getToolNames() throws PladipusReportableException {
		return getAllToolInfo().keySet();
	}

	@Override
	public ToolInfo getInfoForTool(String toolName) throws PladipusReportableException {
		ToolInfo toolInfo = getAllToolInfo().get(toolName);
		if (toolInfo == null) {
			throw new PladipusReportableException(exceptionMessages.getMessage("base.badTool", toolName));
		}
		return toolInfo;
	}

	@Override
	public ImmutableSet<String> getMandatoryParameters(String toolName) throws PladipusReportableException {
		Predicate<InputParameter> pred = ip -> ip.isMandatory();
		return filterParameters(getInfoForTool(toolName).getInputParams(), pred);
	}

	@Override
	public ImmutableSet<String> getOptionalParameters(String toolName) throws PladipusReportableException {
		Predicate<InputParameter> pred = ip -> !ip.isMandatory();
		return filterParameters(getInfoForTool(toolName).getInputParams(), pred);
	}
	
	private ImmutableSet<String> filterParameters(ImmutableSet<InputParameter> allParameters, Predicate<InputParameter> pred) {
		Builder<String> builder = ImmutableSet.builder();
		if (allParameters != null) {
			for (InputParameter param: allParameters) {
				if (pred.test(param)) {
					builder.add(param.getParamName());
				}
			}
		}
		return builder.build();
	}
}
