package com.compomics.pladipus.repository.persist.impl;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.pladipus.model.persist.Batch;
import com.compomics.pladipus.model.persist.Run;
import com.compomics.pladipus.model.persist.RunStatus;
import com.compomics.pladipus.repository.persist.RunRepository;
import com.compomics.pladipus.shared.PladipusReportableException;

@Repository
@Transactional
public class RunRepositoryImpl extends GenericRepositoryImpl<Run> implements RunRepository {

	public RunRepositoryImpl() {
		super(Run.class);
	}

	@Override
	public List<Run> findRunsByStatus(RunStatus status) throws PladipusReportableException {
		return getResultsList(getNamedQuery("Run.findByStatus").setParameter("status", status));
	}

	@Override
	public List<Run> findRunsByBatch(Batch batch) throws PladipusReportableException {
		return getResultsList(getNamedQuery("Run.findByBatch").setParameter("batch", batch.getRuns()));
	}
	
}
