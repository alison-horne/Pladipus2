package com.compomics.pladipus.base.helper.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.compomics.pladipus.base.DefaultsControl;
import com.compomics.pladipus.base.ToolControl;
import com.compomics.pladipus.base.helper.ValidationChecker;
import com.compomics.pladipus.model.persist.Parameter;
import com.compomics.pladipus.model.persist.Step;
import com.compomics.pladipus.model.persist.Workflow;
import com.compomics.pladipus.shared.PladipusMessages;
import com.compomics.pladipus.shared.PladipusReportableException;
import com.compomics.pladipus.model.parameters.InputParameter;
import com.compomics.pladipus.model.parameters.Substitution;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class WorkflowValidator implements ValidationChecker<Workflow> {

	@Autowired
	private ToolControl toolControl;
	
	@Autowired
	private DefaultsControl defaultsControl;
	
	@Autowired
	private PladipusMessages exceptionMessages;
	
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
		List<Step> visited = new ArrayList<Step>();
		class StepCheckNode {
			int level;
			Step step;
			List<Step> tree;
			
			StepCheckNode(int level, Step step, List<Step> tree) {
				this.level = level;
				this.step = step;
				this.tree = tree;
			}
			
			public void process() throws PladipusReportableException {
				tree = tree.subList(0, level);
				if (tree.contains(step)) {
					throw new PladipusReportableException(exceptionMessages.getMessage("template.stepDependency", step.getId()));
				} 
				tree.add(step);
				visited.add(step);
				for (Step nextStep: step.getPrereqs()) {
					new StepCheckNode(level + 1, nextStep, tree).process();
				}
			}
		}
		
		List<Step> tree = new ArrayList<Step>();
		for (Step step: workflow.getSteps().getStep()) {
			if (!visited.contains(step)) {
				new StepCheckNode(0, step, tree).process();
			}
		}
	}
	
	private void checkGlobalParameters(Workflow workflow) throws PladipusReportableException {
		// TODO check value types, standard mappings?  Tidy this up, no duplication etc.
		List<String> defaults = defaultsControl.getDefaultNamesForUser(workflow.getUser());
		for (Parameter param : workflow.getGlobal().getParameters().getParameter()) {
			Iterator<String> iter = param.getValue().iterator();
			while (iter.hasNext()) {
				String value = iter.next();
				if (isSubstitution(value)) {
					int start = value.indexOf(Substitution.getPrefix());
					while (start > -1) {
						int end = value.indexOf(Substitution.getEnd(), start);
						String subValue = value.substring(start + Substitution.getPrefix().length(), end);
						start = value.indexOf(Substitution.getPrefix(), end);
						String[] split = subValue.split("\\.", 2);
						if (split.length != 2) {
							throw new PladipusReportableException(exceptionMessages.getMessage("template.subFormat", value));
						}
						if (split[0].equalsIgnoreCase(Substitution.getDefault())) {
							if (!defaults.contains(split[1].toUpperCase())) {
								throw new PladipusReportableException(exceptionMessages.getMessage("template.invalidDefault", subValue));
							}
						} else {
							throw new PladipusReportableException(exceptionMessages.getMessage("template.invalidGlobalSubstitution", subValue));
						}
					}
				}
			}
		}
	}
	
	private void checkStepParameters(Workflow workflow) throws PladipusReportableException {
		for (Step step: workflow.getSteps().getStep()) {
			checkStep(step);
		}
	}
	
	private void checkStep(Step step) throws PladipusReportableException {
		checkParameterNames(step);
		checkMandatoryParameters(step);
		validateValueTypes(step);
	}
	
	private void checkMandatoryParameters(Step step) throws PladipusReportableException {
		ImmutableSet<InputParameter> mandatory = toolControl.getMandatoryParameters(step.getName());
		Set<String> stepParamNames = new HashSet<String>();
		for (Parameter param: step.getParameters().getParameter()) {
			stepParamNames.add(param.getName());
		}
		for (InputParameter mand: mandatory) {
			if (!stepParamNames.contains(mand.getParamName())) {
				String defaultValue = mand.getDefaultValue();
				if (defaultValue != null && !defaultValue.isEmpty()) {
					Parameter p = new Parameter();
					p.setName(mand.getParamName());
					p.getValue().add(defaultValue);
					step.getParameters().getParameter().add(p);
					// TODO log adding default
				} else {
					throw new PladipusReportableException(exceptionMessages.getMessage("template.missingMandatoryParameter", 
																						step.getId(), 
																						mand.getParamName()));
				}
			}
		}
	}
	
	private void checkParameterNames(Step step) throws PladipusReportableException {
		ImmutableSet<String> toolParams = toolControl.getParameterMap(step.getName()).keySet();
		Iterator<Parameter> iter = step.getParameters().getParameter().iterator();
		while (iter.hasNext()) {
			String param = iter.next().getName();
			if (!toolParams.contains(param)) {
				throw new PladipusReportableException(exceptionMessages.getMessage("template.invalidParameterName", step.getId(), param));
			}
		}
	}
	
	private void validateValueTypes(Step step) throws PladipusReportableException {
		//TODO validate types when substitution value - check default/global
		ImmutableMap<String, InputParameter> inputParams = toolControl.getParameterMap(step.getName());
		Iterator<Parameter> iter = step.getParameters().getParameter().iterator();
		while (iter.hasNext()) {
			Parameter parameter = iter.next();
			for (String value: parameter.getValue()) {
				if (!inputParams.get(parameter.getName()).isTypeValid(value)) {
					throw new PladipusReportableException(exceptionMessages.getMessage("template.invalidParameterValue", step.getId(), parameter.getName(), value));
				}
			}
		}
	}
	
	private void checkSubstitutions(Workflow workflow) throws PladipusReportableException {
		List<String> defaults = defaultsControl.getDefaultNamesForUser(workflow.getUser());
		for (Step step: workflow.getSteps().getStep()) {
			for (Parameter param: step.getParameters().getParameter()) {
				Iterator<String> iter = param.getValue().iterator();
				while (iter.hasNext()) {
					String value = iter.next();
					if (isSubstitution(value)) {
						int start = value.indexOf(Substitution.getPrefix());
						while (start > -1) {
							int end = value.indexOf(Substitution.getEnd(), start);
							String subValue = value.substring(start + Substitution.getPrefix().length(), end);
							start = value.indexOf(Substitution.getPrefix(), end);
							String[] split = subValue.split("\\.", 2);
							if (split.length != 2) {
								throw new PladipusReportableException(exceptionMessages.getMessage("template.subFormat", value));
							}
							if (split[0].equalsIgnoreCase(Substitution.getDefault())) {
								if (!defaults.contains(split[1].toUpperCase())) {
									throw new PladipusReportableException(exceptionMessages.getMessage("template.invalidDefault", subValue));
								}
							} else if (split[0].equalsIgnoreCase(Substitution.getGlobal())) {
								Iterator<Parameter> glParams = workflow.getGlobal().getParameters().getParameter().iterator();
								boolean found = false;
								while (glParams.hasNext()) {
									if (glParams.next().getName().equals(split[1])) {
										found = true;
										break;
									}
								}
								if (!found) {
									Parameter p = new Parameter();
									p.setName(split[1]);
									workflow.getGlobal().getParameters().getParameter().add(p);
								}
							} else {
								if (workflow.getSteps().getStepMap().keySet().contains(split[0])) {
									step.addPrereq(workflow.getSteps().getStepMap().get(split[0]));
								} else {
									throw new PladipusReportableException(exceptionMessages.getMessage("template.stepMissing", step.getId(), split[0]));
								}
							}
						}
					}
				}
			}
		}
	}
	
	private boolean isSubstitution(String value) throws PladipusReportableException {
		if (!value.contains(Substitution.getPrefix())) {
			return false;
		}
		if (!isValid(value)) {
			throw new PladipusReportableException(exceptionMessages.getMessage("template.subFormat", value));
		}
		return true;
	}
	
	private boolean isValid(String valueString) {
		int startCount = StringUtils.countOccurrencesOf(valueString, Substitution.getPrefix());
		int endCount = StringUtils.countOccurrencesOf(valueString, Substitution.getEnd());
		if (startCount != endCount) return false;
		int subStart = 0;
		int subEnd = 0;
        for (int i = 0; i < startCount; i++) {
        	subStart = valueString.indexOf(Substitution.getPrefix(), subEnd);
        	subEnd = valueString.indexOf(Substitution.getEnd(), subStart);
        	if (subStart < 0 || subEnd < 0) return false;
        }
        return true;
	}

	private void checkToolNamesValid(Workflow workflow) throws PladipusReportableException {
		ImmutableSet<String> toolNames = toolControl.getToolNames();
		Collection<Step> steps = workflow.getSteps().getStep();
		for (Step step: steps) {
			if (!toolNames.contains(step.getName())) {
				throw new PladipusReportableException(exceptionMessages.getMessage("template.invalidXmlTool", step.getName()));
			}
		}
	}
}
