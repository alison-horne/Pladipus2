package com.compomics.pladipus.repository.service.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.pladipus.model.persist.Batch;
import com.compomics.pladipus.model.persist.BatchRun;
import com.compomics.pladipus.model.persist.User;
import com.compomics.pladipus.model.persist.Workflow;
import com.compomics.pladipus.repository.persist.BatchRepository;
import com.compomics.pladipus.repository.persist.BatchRunRepository;
import com.compomics.pladipus.repository.persist.WorkflowRepository;
import com.compomics.pladipus.repository.service.BatchService;
import com.compomics.pladipus.shared.PladipusMessages;
import com.compomics.pladipus.shared.PladipusReportableException;

public class BatchServiceImpl implements BatchService {

	@Autowired
	private BatchRepository batchRepo;
	
	@Autowired
	private BatchRunRepository batchRunRepo;
	
	@Autowired
	private WorkflowRepository workflowRepo;
	
	@Autowired
	private PladipusMessages exceptionMessages;
	
	@Override
	public void insertBatch(Batch batch) throws PladipusReportableException {
		if (batchRepo.findActiveBatchByName(batch.getName(), batch.getWorkflow()) != null) {
			throw new PladipusReportableException(exceptionMessages.getMessage("db.batchExists", batch.getName()));
		}
		batchRepo.persist(batch);
	}
	
	@Transactional(rollbackFor={Exception.class})
	@Override
	public void replaceBatch(Batch batch) throws PladipusReportableException {
		endOldBatch(batch);
		batchRepo.persist(batch);
	}

	private void endOldBatch(Batch batch) throws PladipusReportableException {
		Batch oldBatch = batchRepo.findActiveBatchByName(batch.getName(), batch.getWorkflow());
		if (oldBatch != null) {
			deactivateBatch(oldBatch);
		}
	}

	@Override
	public Batch getActiveNamedBatch(String batchName, User user) throws PladipusReportableException {
		return batchRepo.findNamedActiveBatchWithinWorkflows(batchName, workflowRepo.findAllActiveWorkflowsForUser(user));
	}

	@Override
	public List<Batch> getAllActiveBatchesForUser(User user) throws PladipusReportableException {
		return batchRepo.findActiveBatchesForWorkflows(workflowRepo.findAllActiveWorkflowsForUser(user));
	}
	
	@Override
	public List<Batch> getAllActiveBatchesForWorkflow(Workflow workflow) throws PladipusReportableException {
		return batchRepo.findActiveBatchesForWorkflows(Collections.singleton(workflow));
	}
	
	@Override
	public Batch getBatchWithId(long id) throws PladipusReportableException {
		return batchRepo.findById(id);
	}
	
	@Override
	public BatchRun getBatchRunWithId(long id) throws PladipusReportableException {
		return batchRunRepo.findById(id);
	}

	@Override
	public void deactivateBatch(Batch batch) throws PladipusReportableException {
		batch.setActive(false);
		batchRepo.merge(batch);
	}
}
