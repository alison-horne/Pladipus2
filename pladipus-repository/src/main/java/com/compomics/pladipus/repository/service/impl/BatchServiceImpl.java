package com.compomics.pladipus.repository.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.pladipus.model.persist.Batch;
import com.compomics.pladipus.repository.persist.BatchRepository;
import com.compomics.pladipus.repository.service.BatchService;
import com.compomics.pladipus.shared.PladipusMessages;
import com.compomics.pladipus.shared.PladipusReportableException;

public class BatchServiceImpl implements BatchService {

	@Autowired
	private BatchRepository batchRepo;
	
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
			oldBatch.setActive(false);
			batchRepo.merge(oldBatch);
		}
	}
}
