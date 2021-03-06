package com.compomics.pladipus.repository.service;

import java.util.List;

import com.compomics.pladipus.model.persist.Batch;
import com.compomics.pladipus.model.persist.BatchRun;
import com.compomics.pladipus.model.persist.User;
import com.compomics.pladipus.model.persist.Workflow;
import com.compomics.pladipus.shared.PladipusReportableException;

public interface BatchService {
	public void insertBatch(Batch batch) throws PladipusReportableException;
	public void replaceBatch(Batch batch) throws PladipusReportableException;
	public Batch getActiveNamedBatch(String batchName, User user) throws PladipusReportableException;
	public List<Batch> getAllActiveBatchesForUser(User user) throws PladipusReportableException;
	public List<Batch> getAllActiveBatchesForWorkflow(Workflow workflow) throws PladipusReportableException;
	public Batch getBatchWithId(long id) throws PladipusReportableException;
	public BatchRun getBatchRunWithId(long id) throws PladipusReportableException;
	public void deactivateBatch(Batch batch) throws PladipusReportableException;
}
