package com.compomics.pladipus.repository.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.pladipus.model.persist.Run;
import com.compomics.pladipus.model.persist.RunStatus;
import com.compomics.pladipus.model.persist.RunStep;
import com.compomics.pladipus.repository.persist.RunRepository;
import com.compomics.pladipus.repository.persist.RunStepRepository;
import com.compomics.pladipus.repository.service.RunService;
import com.compomics.pladipus.shared.PladipusReportableException;

public class RunServiceImpl implements RunService {
	
	@Autowired
	private RunRepository runRepo;
	
	@Autowired
	private RunStepRepository runStepRepo;

	@Transactional(rollbackFor={Exception.class})
	@Override
	public void insertRun(Run run) throws PladipusReportableException {
		runRepo.persist(run);
	}

	@Override
	public List<RunStep> getReadyRunSteps() throws PladipusReportableException {
		return runStepRepo.findRunStepsByStatus(RunStatus.READY);
	}

	@Transactional(rollbackFor={Exception.class})
	@Override
	public void updateStepStatus(RunStep step, RunStatus status) throws PladipusReportableException {
		step.setStatus(status);
		Run run = step.getRun();
		// TODO update run status to reflect step status.  If error on step, use cancelled status and kill/pull from queue other steps in run
		runRepo.merge(run);
	}


}
