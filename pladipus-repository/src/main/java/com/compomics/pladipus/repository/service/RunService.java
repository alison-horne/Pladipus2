package com.compomics.pladipus.repository.service;

import java.util.List;
import java.util.Map;

import com.compomics.pladipus.model.persist.Batch;
import com.compomics.pladipus.model.persist.Run;
import com.compomics.pladipus.model.persist.RunStatus;
import com.compomics.pladipus.model.persist.RunStep;
import com.compomics.pladipus.shared.PladipusReportableException;

public interface RunService {
	public void insertRun(Run run) throws PladipusReportableException;
	public List<RunStep> getReadyRunSteps() throws PladipusReportableException;
	public void abortBatchRuns(Batch batch) throws PladipusReportableException;
	public void completeRunStep(Long runStepId, Map<String, String> outputs, String workerId) throws PladipusReportableException;
	public void updateStepStatus(RunStep step, RunStatus status) throws PladipusReportableException;
	public void workerStepStatus(Long runStepId, RunStatus status, String workerId) throws PladipusReportableException;
	public void workerErrorStatus(Long runStepId, String workerId, String errorMsg) throws PladipusReportableException;
	public String doStepSubstitutions(String paramValue, Long runId) throws PladipusReportableException;
	public String getRunIdentifier(RunStep step) throws PladipusReportableException;
	public List<Run> getAbortedRuns() throws PladipusReportableException;
	public void updateRunStatus(Run run, RunStatus status) throws PladipusReportableException;
}
