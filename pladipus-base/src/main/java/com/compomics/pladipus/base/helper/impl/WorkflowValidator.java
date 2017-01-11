package com.compomics.pladipus.base.helper.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.compomics.pladipus.base.DefaultsControl;
import com.compomics.pladipus.base.ToolControl;
import com.compomics.pladipus.base.helper.ValidationChecker;
import com.compomics.pladipus.model.core.Step;
import com.compomics.pladipus.model.core.Workflow;
import com.compomics.pladipus.model.exceptions.PladipusMessages;
import com.compomics.pladipus.model.exceptions.PladipusReportableException;
import com.compomics.pladipus.model.parameters.InputParameter;
import com.google.common.collect.ImmutableMap;
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
	private static final String DEFAULT_PREFIX = "DEFAULT";
	private static final String GLOBAL_PREFIX = "GLOBAL";
	
	@Override
	public void validate(Workflow workflow) throws PladipusReportableException {
		checkToolNamesValid(workflow);
		checkStepParameters(workflow);
		checkSubstitutions(workflow);
		checkStepDependencies(workflow);
		checkGlobalParameters(workflow);
	}

	/**
	 * Checks to find any circular dependencies between steps.  i.e. step1 needs output of step2...which needs output of step1.
	 * @param workflow
	 * @throws PladipusReportableException
	 */
	private void checkStepDependencies(Workflow workflow) throws PladipusReportableException {
		
		class StepCheckNode {
			int level;
			Step step;
			List<String> tree;
			
			StepCheckNode(int level, Step step, List<String> tree) {
				this.level = level;
				this.step = step;
				this.tree = tree;
			}
			
			public void process() throws PladipusReportableException {
				tree = tree.subList(0, level);
				if (tree.contains(step.getStepIdentifier())) {
					throw new PladipusReportableException(exceptionMessages.getMessage("template.stepDependency", step.getStepIdentifier()));
				} 
				tree.add(step.getStepIdentifier());
				step.setVisited();
				for (String stepId: step.getDependencies()) {
					new StepCheckNode(level + 1, workflow.getSteps().get(stepId), tree).process();
				}
			}
		}
		
		List<String> tree = new ArrayList<String>();
		for (Step step: workflow.getSteps().values()) {
			if (!step.isVisited()) {
				new StepCheckNode(0, step, tree).process();
			}
		}
	}
	
	private void checkGlobalParameters(Workflow workflow) {
		// TODO
	}
	
	private void checkStepParameters(Workflow workflow) throws PladipusReportableException {
		for (Step step: workflow.getSteps().values()) {
			checkStep(step);
		}
	}
	
	private void checkStep(Step step) throws PladipusReportableException {
		checkParameterNames(step);
		checkMandatoryParameters(step);
		validateValueTypes(step);
	}
	
	private void checkMandatoryParameters(Step step) throws PladipusReportableException {
		ImmutableSet<InputParameter> mandatory = toolControl.getMandatoryParameters(step.getToolType());
		Set<String> stepParamNames = step.getStepParameters().keySet();
		for (InputParameter mand: mandatory) {
			if (!stepParamNames.contains(mand.getParamName())) {
				String defaultValue = mand.getDefault();
				if (defaultValue != null && !defaultValue.isEmpty()) {
					// TODO log adding default
					step.addParameterValues(mand.getParamName(), Collections.singleton(defaultValue));
				} else {
					throw new PladipusReportableException(exceptionMessages.getMessage("template.missingMandatoryParameter", 
																						step.getStepIdentifier(), 
																						mand.getParamName()));
				}
			}
		}
	}
	
	private void checkParameterNames(Step step) throws PladipusReportableException {
		ImmutableSet<String> toolParams = toolControl.getParameterMap(step.getToolType()).keySet();
		Set<String> stepParamNames = step.getStepParameters().keySet();
		for (String param: stepParamNames) {
			if (!toolParams.contains(param)) {
				throw new PladipusReportableException(exceptionMessages.getMessage("template.invalidParameterName", step.getStepIdentifier(), param));
			}
		}
	}
	
	private void validateValueTypes(Step step) throws PladipusReportableException {
		ImmutableMap<String, InputParameter> inputParams = toolControl.getParameterMap(step.getToolType());
		Iterator<Entry<String, Set<String>>> iter = step.getStepParameters().entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, Set<String>> parameter = iter.next();
			for (String value: parameter.getValue()) {
				if (!inputParams.get(parameter.getKey()).isTypeValid(value)) {
					throw new PladipusReportableException(exceptionMessages.getMessage("template.invalidParameterValue", step.getStepIdentifier(), parameter.getKey(), value));
				}
			}
		}
	}
	
	private void checkSubstitutions(Workflow workflow) throws PladipusReportableException {
		Map<String, Integer> defaults = defaultsControl.getDefaultMap(workflow.getUserId());
		for (Step step: workflow.getSteps().values()) {
			for (Entry<String, Set<String>> param: step.getStepParameters().entrySet()) {
				Iterator<String> iter = param.getValue().iterator();
				while (iter.hasNext()) {
					String value = iter.next();
					if (isSubstitution(value)) {
						int start = value.indexOf(SUBSTITUTE_PREFIX);
						while (start > -1) {
							int end = value.indexOf("}", start);
							String subValue = value.substring(start + SUBSTITUTE_PREFIX.length(), end);
							start = value.indexOf(SUBSTITUTE_PREFIX, end);
							String[] split = subValue.split("\\.", 2);
							if (split.length != 2) {
								throw new PladipusReportableException(exceptionMessages.getMessage("template.subFormat", value));
							}
							if (split[0].equalsIgnoreCase(DEFAULT_PREFIX)) {
								if (defaults.get(split[1].toUpperCase()) != null) {
									workflow.addDefaultSub(split[1], defaults.get(split[1].toUpperCase()));
								} else {
									throw new PladipusReportableException(exceptionMessages.getMessage("template.invalidDefault", subValue));
								}
							} else if (split[0].equalsIgnoreCase(GLOBAL_PREFIX)) {
								workflow.addParameterValues(split[1], null);
							} else {
								step.addDependency(split[0]);
							}
						}
					}
				}
			}
		}
	}
	
	private boolean isSubstitution(String value) throws PladipusReportableException {
		if (!value.contains(SUBSTITUTE_PREFIX)) {
			return false;
		}
		if (!isValid(value)) {
			throw new PladipusReportableException(exceptionMessages.getMessage("template.subFormat", value));
		}
		return true;
	}
	
	private boolean isValid(String valueString) {
		int startCount = StringUtils.countOccurrencesOf(valueString, SUBSTITUTE_PREFIX);
		int endCount = StringUtils.countOccurrencesOf(valueString, SUBSTITUTE_END);
		return ((startCount == endCount) && 
				(valueString.indexOf(SUBSTITUTE_PREFIX) < valueString.indexOf(SUBSTITUTE_END)));
	}

	private void checkToolNamesValid(Workflow workflow) throws PladipusReportableException {
		ImmutableSet<String> toolNames = toolControl.getToolNames();
		Collection<Step> steps = workflow.getSteps().values();
		for (Step step: steps) {
			if (!toolNames.contains(step.getToolType())) {
				throw new PladipusReportableException(exceptionMessages.getMessage("template.invalidXmlTool", step.getToolType()));
			}
		}
	}
}
