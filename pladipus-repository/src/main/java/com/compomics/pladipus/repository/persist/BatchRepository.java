package com.compomics.pladipus.repository.persist;

import com.compomics.pladipus.model.persist.Batch;
import com.compomics.pladipus.model.persist.Workflow;
import com.compomics.pladipus.shared.PladipusReportableException;

public interface BatchRepository extends GenericRepository<Batch> {
	public Batch findActiveBatchByName(String batchName, Workflow workflow) throws PladipusReportableException;
}
