package com.compomics.pladipus.repository.service;

import com.compomics.pladipus.model.persist.Batch;
import com.compomics.pladipus.shared.PladipusReportableException;

public interface BatchService {
	public void insertBatch(Batch batch) throws PladipusReportableException;
	public void replaceBatch(Batch batch) throws PladipusReportableException;
}
