package com.compomics.pladipus.repository.persist;

import java.util.List;

import com.compomics.pladipus.model.persist.RunStatus;
import com.compomics.pladipus.model.persist.RunStep;
import com.compomics.pladipus.shared.PladipusReportableException;

public interface RunStepRepository extends GenericRepository<RunStep> {
	List<RunStep> findRunStepsByStatus(RunStatus status) throws PladipusReportableException;
}
