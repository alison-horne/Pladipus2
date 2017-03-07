package com.compomics.pladipus.repository.persist.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.pladipus.model.persist.Batch;
import com.compomics.pladipus.model.persist.Workflow;
import com.compomics.pladipus.repository.persist.BatchRepository;
import com.compomics.pladipus.shared.PladipusReportableException;

@Repository
@Transactional
public class BatchRepositoryImpl extends GenericRepositoryImpl<Batch> implements BatchRepository {

	public BatchRepositoryImpl() {
		super(Batch.class);
	}

	@Override
	public Batch findActiveBatchByName(String batchName, Workflow workflow) throws PladipusReportableException {
		return getSingleResult(getNamedQuery("Batch.findNamedActive").setParameter("name", batchName).setParameter("workflow", workflow));
	}
}
