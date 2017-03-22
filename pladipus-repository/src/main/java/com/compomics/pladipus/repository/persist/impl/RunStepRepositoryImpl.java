package com.compomics.pladipus.repository.persist.impl;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.pladipus.model.persist.RunStatus;
import com.compomics.pladipus.model.persist.RunStep;
import com.compomics.pladipus.repository.persist.RunStepRepository;
import com.compomics.pladipus.shared.PladipusReportableException;

@Repository
@Transactional
public class RunStepRepositoryImpl extends GenericRepositoryImpl<RunStep> implements RunStepRepository {

	public RunStepRepositoryImpl() {
		super(RunStep.class);
	}

	@Override
	public List<RunStep> findRunStepsByStatus(RunStatus status) throws PladipusReportableException {
		return getResultsList(getNamedQuery("RunStep.findByStatus").setParameter("status", status));
	}
}
