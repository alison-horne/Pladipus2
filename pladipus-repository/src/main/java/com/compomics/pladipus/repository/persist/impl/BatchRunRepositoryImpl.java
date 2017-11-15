package com.compomics.pladipus.repository.persist.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.pladipus.model.persist.BatchRun;
import com.compomics.pladipus.repository.persist.BatchRunRepository;
import com.compomics.pladipus.shared.PladipusReportableException;

@Repository
@Transactional
public class BatchRunRepositoryImpl extends GenericRepositoryImpl<BatchRun> implements BatchRunRepository {

	public BatchRunRepositoryImpl() {
		super(BatchRun.class);
	}
	
	@Override
	public BatchRun findById(Long id) throws PladipusReportableException {
		return getSingleResult(getNamedQuery("BatchRun.findById").setParameter("id", id));
	}
}
