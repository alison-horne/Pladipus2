package com.compomics.pladipus.repository.persist;

import java.util.List;

import com.compomics.pladipus.model.persist.Batch;
import com.compomics.pladipus.model.persist.Run;
import com.compomics.pladipus.model.persist.RunStatus;
import com.compomics.pladipus.shared.PladipusReportableException;

public interface RunRepository extends GenericRepository<Run> {
	List<Run> findRunsByStatus(RunStatus status) throws PladipusReportableException;
	List<Run> findRunsByBatch(Batch batch) throws PladipusReportableException;
}
