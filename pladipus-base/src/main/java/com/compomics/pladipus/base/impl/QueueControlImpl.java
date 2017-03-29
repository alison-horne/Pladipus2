package com.compomics.pladipus.base.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.pladipus.base.QueueControl;
import com.compomics.pladipus.model.core.TaskStatus;
import com.compomics.pladipus.model.parameters.Substitution;
import com.compomics.pladipus.model.persist.Batch;
import com.compomics.pladipus.model.persist.BatchRun;
import com.compomics.pladipus.model.persist.Default;
import com.compomics.pladipus.model.persist.Run;
import com.compomics.pladipus.model.persist.RunStatus;
import com.compomics.pladipus.model.persist.RunStep;
import com.compomics.pladipus.model.persist.Step;
import com.compomics.pladipus.model.persist.User;
import com.compomics.pladipus.model.persist.WorkflowGlobalParameter;
import com.compomics.pladipus.model.persist.WorkflowGlobalValue;
import com.compomics.pladipus.model.persist.WorkflowStepParameter;
import com.compomics.pladipus.model.persist.WorkflowStepValue;
import com.compomics.pladipus.repository.service.BatchService;
import com.compomics.pladipus.repository.service.DefaultService;
import com.compomics.pladipus.repository.service.RunService;
import com.compomics.pladipus.shared.PladipusMessages;
import com.compomics.pladipus.shared.PladipusReportableException;

public class QueueControlImpl implements QueueControl {

	@Autowired
	@Lazy
	private RunService runService;
	
	@Autowired
	@Lazy
	private BatchService batchService;
	
	@Autowired
	@Lazy
	private DefaultService defaultService;
	
	@Autowired
	private PladipusMessages exceptionMessages;
	
	@Transactional(rollbackFor={Exception.class})
	@Override
	public void process(String batchName, User user) throws PladipusReportableException {
		// TODO What to do with batches already in progress/completed?  Stop/cancel?  Run again?  Ignore?
		List<Batch> batches = getActiveBatches(batchName, user);
		if (batches.isEmpty()) {
			throw new PladipusReportableException(exceptionMessages.getMessage("batch.noBatch"));
		}
		
		Map<String, String> defaultsMap = getUserDefaultsMap(user);
		
		for (Batch batch: batches) {
			for (BatchRun batchRun: batch.getRuns()) {
				new CreateRun(batchRun, defaultsMap).insertRun();
			}
		}
	}
	
	@Override
	public void restart(String batchName, User user) throws PladipusReportableException {
		// TODO if batchName null/empty, throw exception
		// Create new run.  End any currently running old ones?
	}

	@Override
	public TaskStatus status(String batchName, User user) throws PladipusReportableException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void abort(String batchName, User user) throws PladipusReportableException {
		// TODO Auto-generated method stub
		// If batchName null/empty, abort all...set run status to cancel/abort, then have another polling job on controller pick it up and pull from queue/end currently running tasks
	}
	
	private List<Batch> getActiveBatches(String batchName, User user) throws PladipusReportableException {
		if (batchName == null) {
			return batchService.getAllActiveBatchesForUser(user);
		} else {
			Batch batch = batchService.getActiveNamedBatch(batchName, user);
			List<Batch> list = new ArrayList<Batch>();
			if (batch != null) list.add(batch);
			return list;
		}
	}
	
	private Map<String, String> getUserDefaultsMap(User user) throws PladipusReportableException {
		List<Default> userDefaults = defaultService.getDefaultsForUser(user);
		Map<String, String> defaultMap = new HashMap<String, String>();
		for (Default userDefault: userDefaults) {
			defaultMap.put(userDefault.getName(), userDefault.getValue());
		}
		return defaultMap;
	}
	
	class CreateRun {
		private BatchRun batchRun;
		private Map<String, String> defaultMap;
		private Run run = new Run();
		private Map<Long, Set<String>> batchStepParamMap;
		private Map<Long, Set<String>> batchGlobals;
		private Map<Step, RunStep> stepMap = new HashMap<Step, RunStep>();
		private Map<String, RunStep> runStepIdMap = new HashMap<String, RunStep>();
		private Map<Step, Set<String>> batchPrereqs = new HashMap<Step, Set<String>>();
		private Map<String, Set<String>> globalMap = new HashMap<String, Set<String>>();
		
		CreateRun(BatchRun batchRun, Map<String, String> defaultMap) {
			this.batchRun = batchRun;
			this.defaultMap = defaultMap;
			batchStepParamMap = batchRun.getStepParamMap();
			batchGlobals = batchRun.getGlobalParamMap();
			setupRun();
		}
		
		private void setupRun() {
			run.setStatus(RunStatus.READY);
			run.setBatchRun(batchRun);
			setupGlobalParameterMap();
			setupRunSteps();
			setupRunStepPrereqs();
		}
		
		private void setupGlobalParameterMap() {
			for (WorkflowGlobalParameter workflowGlobal : batchRun.getBatch().getWorkflow().getGlobalParams()) {
				Set<String> values = new HashSet<String>();
				for (WorkflowGlobalValue wkValue : workflowGlobal.getValues()) {
					values.add(wkValue.getValue());
				}
				if (batchGlobals.containsKey(workflowGlobal.getGlobalId())) {
					values.addAll(batchGlobals.get(workflowGlobal.getGlobalId()));
				}
				globalMap.put(workflowGlobal.getName(), values);
			}
		}
		
		private void setupRunSteps() {
			Set<Step> steps = batchRun.getBatch().getWorkflow().getTemplateSteps();
			for (Step step: steps) {
				RunStep runStep = new RunStep();
				run.addRunStep(runStep);
				stepMap.put(step, runStep);
				runStepIdMap.put(step.getId(), runStep);
				runStep.setStepIdentifier(step.getId());
				runStep.setToolName(step.getName());
				for (WorkflowStepParameter param: step.getStepParams()) {
					String paramName = param.getName();
					runStep.addParameter(paramName, getParameterValue(param, step));
				}
			}
		}
		
		private void setupRunStepPrereqs() {
			for (Step step: stepMap.keySet()) {
				RunStep runStep = stepMap.get(step);
				if (step.getPrereqs().isEmpty() && !batchPrereqs.containsKey(step)) {
					runStep.setStatus(RunStatus.READY);
				} else {
					runStep.setStatus(RunStatus.BLOCKED);
					for (Step prereq: step.getPrereqs()) {
						runStep.addPrereq(stepMap.get(prereq));
					}
					if (batchPrereqs.containsKey(step)) {
						for (String stepId: batchPrereqs.get(step)) {
							runStep.addPrereq(runStepIdMap.get(stepId));
						}
					}
				}
			}
		}
		
		private String getParameterValue(WorkflowStepParameter param, Step step) {
			Set<String> values = new HashSet<String>();
			for (WorkflowStepValue stepValue : param.getValues()) {
				values.addAll(doSubstitutions(stepValue.getValue()));
			}
			if (batchStepParamMap.containsKey(param.getStepParamId())) {
				for (String val: batchStepParamMap.get(param.getStepParamId())) {
					List<String> batchVals = doSubstitutions(val);
					mapBatchStepDeps(batchVals, step);
					values.addAll(batchVals);
				}
			}
			
			return String.join(",", values);
		}
		
		private List<String> doSubstitutions(String rawValue) {

			List<String> values = new ArrayList<String>();
			values.add(rawValue);
			if (isSubstitution(rawValue)) {
				for (String global : globalMap.keySet()) {
					String sub = Substitution.getPrefix() + Substitution.getGlobal() + "." + global + Substitution.getEnd();
					ListIterator<String> iter = values.listIterator();
					while (iter.hasNext()) {
						String value = iter.next();
						if (value.toUpperCase().contains(sub.toUpperCase())) {
							iter.remove();
							for (String globalVal: globalMap.get(global)) {
								iter.add(value.replaceAll("(?i)"+Pattern.quote(sub), Matcher.quoteReplacement(globalVal)));
							}
						}
					}
				}
				for (String def : defaultMap.keySet()) {
					String sub = Substitution.getPrefix() + Substitution.getDefault() + "." + def + Substitution.getEnd();
					ListIterator<String> iter = values.listIterator();
					while (iter.hasNext()) {
						String value = iter.next();
						if (value.toUpperCase().contains(sub.toUpperCase())) {
							iter.remove();
							iter.add(value.replaceAll("(?i)"+Pattern.quote(sub), Matcher.quoteReplacement(defaultMap.get(def))));
						}
					}
				}
			}

			return values;
		}
		
		private boolean isSubstitution(String value) {
			return value.contains(Substitution.getPrefix());
		}
		
		private void mapBatchStepDeps(List<String> batchVals, Step step) {
			for (String value: batchVals) {
				if (isSubstitution(value)) {
					int start = value.indexOf(Substitution.getPrefix());
					while (start > -1) {
						int end = value.indexOf(Substitution.getEnd(), start);
						String subValue = value.substring(start + Substitution.getPrefix().length(), end);
						start = value.indexOf(Substitution.getPrefix(), end);
						String[] split = subValue.split("\\.", 2);
			    		if (batchPrereqs.get(step) == null) {
			    			batchPrereqs.put(step, new HashSet<String>());
			    		}
			    		batchPrereqs.get(step).add(split[0]);
					}
				}
			}
		}
		
		public void insertRun() throws PladipusReportableException {
			runService.insertRun(run);
		}
	}
}
