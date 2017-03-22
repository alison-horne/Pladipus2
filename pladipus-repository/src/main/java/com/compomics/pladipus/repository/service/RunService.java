package com.compomics.pladipus.repository.service;

import java.util.List;

import com.compomics.pladipus.model.persist.Run;
import com.compomics.pladipus.model.persist.RunStatus;
import com.compomics.pladipus.model.persist.RunStep;
import com.compomics.pladipus.shared.PladipusReportableException;

public interface RunService {
	public void insertRun(Run run) throws PladipusReportableException;
	public List<RunStep> getReadyRunSteps() throws PladipusReportableException;
	public void updateStepStatus(RunStep step, RunStatus status) throws PladipusReportableException;
}
