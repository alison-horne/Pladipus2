package com.compomics.pladipus.base.helper.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.compomics.pladipus.base.DefaultsControl;
import com.compomics.pladipus.base.ToolControl;
import com.compomics.pladipus.base.helper.ValidationChecker;
import com.compomics.pladipus.model.core.Step;
import com.compomics.pladipus.model.core.Workflow;
import com.compomics.pladipus.model.exceptions.PladipusMessages;
import com.compomics.pladipus.model.exceptions.PladipusReportableException;
import com.compomics.pladipus.model.parameters.InputParameter;
import com.google.common.collect.ImmutableSet;

public class WorkflowValidator implements ValidationChecker<Workflow> {

	@Autowired
	private ToolControl toolControl;
	
	@Autowired
	private DefaultsControl defaultsControl;
	
	@Autowired
	private PladipusMessages exceptionMessages;
	
	private static final String SUBSTITUTE_PREFIX = "{$";
	private static final String SUBSTITUTE_END = "}";
	private static final String DEFAULT_PREFIX = SUBSTITUTE_PREFIX + "DEFAULT";
	private static final String GLOBAL_PREFIX = SUBSTITUTE_PREFIX + "GLOBAL";
	
	@Override
	public void validate(Workflow workflow) throws PladipusReportableException {
		checkToolNamesValid(workflow);
		checkParameters(workflow);
		checkStepDependencies(workflow);
	}

	private void checkStepDependencies(Workflow workflow) {
		// TODO Auto-generated method stub
	}

	private void checkParameters(Workflow workflow) throws PladipusReportableException {
		checkGlobalParameters(workflow);
		checkStepParameters(workflow);
		// change to {$DEFAULT.id...}? {$GLOBAL.id} etc. Replace method on gets for db insert.
	}
	
	private void checkGlobalParameters(Workflow workflow) {
		// TODO
	}
	
	private void checkStepParameters(Workflow workflow) throws PladipusReportableException {
		for (Step step: workflow.getSteps().values()) {
			ImmutableSet<String> mandatory = toolControl.getMandatoryParameters(step.getToolType());
			ImmutableSet<String> optional = toolControl.getOptionalParameters(step.getToolType());
		}
	}

	private void checkToolNamesValid(Workflow workflow) throws PladipusReportableException {
//		ImmutableSet<String> toolNames = toolControl.getToolNames();
//		List<Step> steps = workflow.getSteps();
//		for (Step step: steps) {
//			if (!toolNames.contains(step.getToolType())) {
//				throw new PladipusReportableException(exceptionMessages.getMessage("template.invalidXmlTool", step.getToolType()));
//			}
//		}
	}
}
