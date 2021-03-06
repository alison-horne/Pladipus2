package com.compomics.pladipus.base.impl;

import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;

import com.compomics.pladipus.base.ToolControl;
import com.compomics.pladipus.shared.PladipusMessages;
import com.compomics.pladipus.shared.PladipusReportableException;
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
	public ImmutableMap<String, InputParameter> getParameterMap(String toolName) throws PladipusReportableException {
		ImmutableSet<InputParameter> params = getInfoForTool(toolName).getInputParams();
		ImmutableMap.Builder<String, InputParameter> builder = ImmutableMap.builder();
		if (params != null) {
			for (InputParameter param: params) {
				builder.put(param.getParamName(), param);
			}
		}
		return builder.build();
	}

	@Override
	public ImmutableSet<InputParameter> getMandatoryParameters(String toolName) throws PladipusReportableException {
		Predicate<InputParameter> pred = ip -> ip.isMandatory();
		return filterParameters(getInfoForTool(toolName).getInputParams(), pred);
	}

	@Override
	public ImmutableSet<InputParameter> getOptionalParameters(String toolName) throws PladipusReportableException {
		Predicate<InputParameter> pred = ip -> !ip.isMandatory();
		return filterParameters(getInfoForTool(toolName).getInputParams(), pred);
	}
	
	private ImmutableSet<InputParameter> filterParameters(ImmutableSet<InputParameter> allParameters, Predicate<InputParameter> pred) {
		Builder<InputParameter> builder = ImmutableSet.builder();
		if (allParameters != null) {
			for (InputParameter param: allParameters) {
				if (pred.test(param)) {
					builder.add(param);
				}
			}
		}
		return builder.build();
	}
}
