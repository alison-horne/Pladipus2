package com.compomics.pladipus.base.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import com.compomics.pladipus.base.BatchControl;
import com.compomics.pladipus.base.WorkflowControl;
import com.compomics.pladipus.base.helper.CsvParser;
import com.compomics.pladipus.model.core.BatchOverview;
import com.compomics.pladipus.model.persist.Batch;
import com.compomics.pladipus.model.persist.User;
import com.compomics.pladipus.model.persist.Workflow;
import com.compomics.pladipus.repository.service.BatchService;
import com.compomics.pladipus.repository.service.RunService;
import com.compomics.pladipus.shared.PladipusMessages;
import com.compomics.pladipus.shared.PladipusReportableException;

public class BatchControlImpl implements BatchControl {

	@Autowired
	@Lazy
	private WorkflowControl workflowControl;
	
	@Autowired
	private CsvParser<Batch, Workflow> batchCsvParser;
	
	@Autowired
	private PladipusMessages exceptionMessages;
	
	@Autowired
	@Lazy
	private BatchService batchService;
	
	@Autowired
	@Lazy
	private RunService runService;
	
	@Override
	public Batch createBatch(String csvString, String workflowName, String batchName, User user) throws PladipusReportableException {
		Batch batch = parseBatch(csvString, workflowName, batchName, user);
		batchService.insertBatch(batch);
		return batch;
	}

	@Override
	public Batch replaceBatch(String csvString, String workflowName, String batchName, User user) throws PladipusReportableException {
		Batch batch = parseBatch(csvString, workflowName, batchName, user);
		batchService.replaceBatch(batch);
		return batch;
	}

	@Override
	public List<String> generateHeaders(String workflowName, User user) throws PladipusReportableException {
		return generateHeadersFromWorkflow(getNamedWorkflow(workflowName, user));
	}
	
	@Override
	public List<String> generateHeadersFromWorkflow(Workflow workflow) throws PladipusReportableException {
		List<String> headers = batchCsvParser.getHeaders(workflow);
		if (headers.isEmpty()) {
			throw new PladipusReportableException(exceptionMessages.getMessage("batch.noHeaders"));
		}
		return headers;
	}
	
	private Workflow getNamedWorkflow(String name, User user) throws PladipusReportableException {
		Workflow workflow = workflowControl.getNamedWorkflow(name, user);
		if (workflow == null) {
			throw new PladipusReportableException(exceptionMessages.getMessage("batch.invalidWorkflow"));
		}
		return workflow;
	}
	
	private Batch parseBatch(String csvString, String workflowName, String batchName, User user) throws PladipusReportableException {
		Workflow workflow = getNamedWorkflow(workflowName, user);
		Batch batch = batchCsvParser.parseCSV(csvString, workflow);
		batch.setName(batchName);
		return batch;
	}

	@Override
	public List<BatchOverview> getBatchOverviewsForWorkflow(Workflow workflow) throws PladipusReportableException {
		List<BatchOverview> batchOverviews = new ArrayList<BatchOverview>();
		List<Batch> batches = batchService.getAllActiveBatchesForWorkflow(workflow);
		if (batches != null) {
			for (Batch batch: batches) {
				batchOverviews.add(batch.getBatchOverview());
			}
		}
		return batchOverviews;
	}
	
	@Override
	public void deactivateWorkflowBatches(Workflow workflow) throws PladipusReportableException {
		for (Batch batch: batchService.getAllActiveBatchesForWorkflow(workflow)) {
			deactivateBatch(batch);
		}
	}
	
	@Override
	public void deactivateBatch(Long id) throws PladipusReportableException {
		Batch batch = batchService.getBatchWithId(id);
		if (batch != null) {
			deactivateBatch(batch);
		}
	}
	
	private void deactivateBatch(Batch batch) throws PladipusReportableException {
		runService.abortBatchRuns(batch, true);
		batchService.deactivateBatch(batch);
	}
}
