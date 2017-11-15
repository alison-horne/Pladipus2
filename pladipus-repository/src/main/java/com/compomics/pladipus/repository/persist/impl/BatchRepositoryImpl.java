package com.compomics.pladipus.repository.persist.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
	public Batch findById(Long id) throws PladipusReportableException {
		return getSingleResult(getNamedQuery("Batch.findById").setParameter("id", id));
	}

	@Override
	public Batch findActiveBatchByName(String batchName, Workflow workflow) throws PladipusReportableException {
		return getSingleResult(getNamedQuery("Batch.findNamedActive").setParameter("name", batchName).setParameter("workflow", workflow));
	}

	@Override
	public List<Batch> findActiveBatchesForWorkflows(Collection<Workflow> workflows) throws PladipusReportableException {
		if (workflows != null && !workflows.isEmpty()) {
			return getResultsList(getNamedQuery("Batch.findActiveForWorkflows").setParameter("workflows", workflows));
		}
		return Collections.emptyList();
	}

	@Override
	public Batch findNamedActiveBatchWithinWorkflows(String batchName, Collection<Workflow> workflows) throws PladipusReportableException {
		if (workflows != null && !workflows.isEmpty()) {
			return getSingleResult(getNamedQuery("Batch.findNamedActiveForWorkflows").setParameter("name", batchName).setParameter("workflows", workflows));
		}
		return null;
	}
}
