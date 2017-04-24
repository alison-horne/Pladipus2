package com.compomics.pladipus.repository.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.pladipus.model.parameters.Substitution;
import com.compomics.pladipus.model.persist.Batch;
import com.compomics.pladipus.model.persist.Run;
import com.compomics.pladipus.model.persist.RunStatus;
import com.compomics.pladipus.model.persist.RunStep;
import com.compomics.pladipus.model.persist.RunStepOutput;
import com.compomics.pladipus.model.persist.RunStepWorker;
import com.compomics.pladipus.repository.persist.RunRepository;
import com.compomics.pladipus.repository.persist.RunStepRepository;
import com.compomics.pladipus.repository.service.RunService;
import com.compomics.pladipus.shared.PladipusMessages;
import com.compomics.pladipus.shared.PladipusReportableException;

public class RunServiceImpl implements RunService {
	
	@Autowired
	private RunRepository runRepo;
	
	@Autowired
	private RunStepRepository runStepRepo;
	
	@Autowired
	private PladipusMessages exceptionMessages;
	
	@Autowired
	private int taskRetries;

	@Transactional(rollbackFor={Exception.class})
	@Override
	public void insertRun(Run run) throws PladipusReportableException {
		runRepo.persist(run);
	}

	@Override
	public List<RunStep> getReadyRunSteps() throws PladipusReportableException {
		return runStepRepo.findRunStepsByStatus(RunStatus.READY);
	}
	
	@Override
	public List<Run> getAbortedRuns() throws PladipusReportableException {
		return runRepo.findRunsByStatus(RunStatus.ABORT);
	}

	@Transactional(rollbackFor={Exception.class})
	@Override
	public void updateStepStatus(RunStep step, RunStatus status) throws PladipusReportableException {
		if (step.getStatus().compareTo(status) < 0) {
			if (status.equals(RunStatus.ERROR) && canRetry(step)) {
				status = RunStatus.READY;
			}
			step.setStatus(status);
			runStepRepo.merge(step);
			Run run = runRepo.findById(step.getRun().getRunId());
			if (status.equals(RunStatus.ERROR)) {
				updateRunStatus(run, RunStatus.ABORT);
			} else if (status.equals(RunStatus.COMPLETE)) {
				boolean complete = true;
				for (RunStep runStep: run.getRunSteps()) {
					if (!runStep.getStatus().equals(RunStatus.COMPLETE)) {
						complete = false;
					}
				}
				if (complete) {
					updateRunStatus(run, RunStatus.COMPLETE);
				}
			} else if (run.getStatus().equals(RunStatus.READY) && (status.compareTo(RunStatus.READY) > 0)) {
				updateRunStatus(run, RunStatus.IN_PROGRESS);
			}
			
		}
	}
	
	@Transactional(rollbackFor={Exception.class})
	public void updateRunStatus(Run run, RunStatus status) throws PladipusReportableException {
		run.setStatus(status);
		if (status.equals(RunStatus.CANCELLED)) {
			for (RunStep step: run.getRunSteps()) {
				if (step.getStatus().compareTo(RunStatus.ON_WORKER) < 0) {
					step.setStatus(status);
				}
			}
		}
		runRepo.merge(run);
	}

	@Transactional(rollbackFor={Exception.class})
	@Override
	public void completeRunStep(Long runStepId, String output, String workerId) throws PladipusReportableException {
		RunStep rs = runStepRepo.findById(runStepId);
		rs.addOutput("out", output); // TODO multiple outputs with different names possible?
		rs.endWorker(workerId, null);
		updateStepStatus(rs, RunStatus.COMPLETE);
		unblockDependentSteps(rs);
		if (runRepo.findById(rs.getRun().getRunId()).getStatus().equals(RunStatus.COMPLETE)) {
			for (RunStepOutput rso: rs.getOutputs()) {
				rso.setFinalStep(true);
			}
			runStepRepo.merge(rs);
		}
	}
	
	private void unblockDependentSteps(RunStep rs) throws PladipusReportableException {
		for (RunStep step: rs.getDependents()) {
			boolean unblocked = true;
			for (RunStep prereq: step.getPrereqs()) {
				if (!prereq.getStatus().equals(RunStatus.COMPLETE)) {
					unblocked = false;
					break;
				}
			}
			if (unblocked) {
				step.setStatus(RunStatus.READY);
				runStepRepo.merge(step);
			}
		}
	}

	@Override
	public String doStepSubstitutions(String paramValue, Long runId) throws PladipusReportableException {
		if (paramValue.contains(Substitution.getPrefix())) {
			Map<String, String> substitutions = getStepSubstitutions(runId);
			for (String toSub: substitutions.keySet()) {
				paramValue = paramValue.replaceAll("(?i)"+Pattern.quote(toSub), Matcher.quoteReplacement(substitutions.get(toSub)));
			}
		}
		return paramValue;
	}
	
	private Map<String, String> getStepSubstitutions(Long runId) throws PladipusReportableException {
		Map<String, String> subMap = new HashMap<String, String>();
		Run run = runRepo.findById(runId);
		for (RunStep step: run.getRunSteps()) {
			for (RunStepOutput output: step.getOutputs()) {
				String toSub = Substitution.getPrefix() + step.getStepIdentifier() + "." + output.getOutputId() + Substitution.getEnd();
				subMap.put(toSub, output.getOutputValue());
			}
		}
		return subMap;
	}

	@Override
	public String getRunIdentifier(RunStep step) throws PladipusReportableException {
		return runRepo.findById(step.getRun().getRunId()).getRunIdentifier();
	}

	@Override
	public void abortBatchRuns(Batch batch) throws PladipusReportableException {
		for (Run run: runRepo.findRunsByBatch(batch)) {
			if (!isRunFinished(run)) {
				updateRunStatus(run, RunStatus.ABORT);
			}
		}
	}
	
	private boolean isRunFinished(Run run) {
		RunStatus status = run.getStatus();
		return status.equals(RunStatus.COMPLETE) || status.equals(RunStatus.ERROR) || status.equals(RunStatus.CANCELLED);
	}

	@Transactional(rollbackFor={Exception.class})
	@Override
	public void workerStepStatus(Long runStepId, RunStatus status, String workerId) throws PladipusReportableException {
		RunStep step = runStepRepo.findById(runStepId);
		if (step == null) throw new PladipusReportableException(exceptionMessages.getMessage("worker.invalidMessage", runStepId));
		RunStepWorker workerStep = step.findExistingWorker(workerId);
		if (workerStep == null) {
			step.addWorker(workerId);
		}
		if (status.equals(RunStatus.CANCELLED)) {
			step.endWorker(workerId, null);
		}
		updateStepStatus(step, status);
	}

	@Transactional(rollbackFor={Exception.class})
	@Override
	public void workerErrorStatus(Long runStepId, String workerId, String errorMsg) throws PladipusReportableException {
		RunStep step = runStepRepo.findById(runStepId);
		if (step == null) throw new PladipusReportableException(exceptionMessages.getMessage("worker.invalidMessage", runStepId));
		step.endWorker(workerId, errorMsg);
		updateStepStatus(step, RunStatus.ERROR);
	}
	
	private boolean canRetry(RunStep step) {
		if (step.getWorkers().size() <= taskRetries) {
			return true;
		}
		return false;
	}
}
