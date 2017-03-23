package com.compomics.pladipus.repository.persist;

import java.util.Collection;
import java.util.List;

import com.compomics.pladipus.model.persist.Batch;
import com.compomics.pladipus.model.persist.Workflow;
import com.compomics.pladipus.shared.PladipusReportableException;

public interface BatchRepository extends GenericRepository<Batch> {
	public Batch findActiveBatchByName(String batchName, Workflow workflow) throws PladipusReportableException;
	public List<Batch> findActiveBatchesForWorkflows(Collection<Workflow> workflows) throws PladipusReportableException;
	public Batch findNamedActiveBatchWithinWorkflows(String batchName, Collection<Workflow> workflows) throws PladipusReportableException;
}
