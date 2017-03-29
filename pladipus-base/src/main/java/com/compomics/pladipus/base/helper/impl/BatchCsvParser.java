package com.compomics.pladipus.base.helper.impl;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;

import com.compomics.pladipus.base.helper.CsvParser;
import com.compomics.pladipus.model.persist.Batch;
import com.compomics.pladipus.model.persist.BatchRun;
import com.compomics.pladipus.model.persist.Step;
import com.compomics.pladipus.model.persist.Workflow;
import com.compomics.pladipus.model.persist.WorkflowGlobalParameter;
import com.compomics.pladipus.model.persist.WorkflowStepParameter;
import com.compomics.pladipus.shared.PladipusMessages;
import com.compomics.pladipus.shared.PladipusReportableException;

public class BatchCsvParser implements CsvParser<Batch, Workflow> {

	@Autowired
	private PladipusMessages exceptionMessages;
	
	private static final String RUN_ID_HEADER = "pladipus_run_id"; 

	@Override
	public Batch parseCSV(String csvString, Workflow workflow) throws PladipusReportableException {
		Reader reader = null;
		CSVParser parser = null;
		Batch batch = new Batch();
		batch.setWorkflow(workflow);
		try {
			reader = new StringReader(csvString);
			parser = new CSVParser(reader, CSVFormat.EXCEL.withHeader());
		    for (final CSVRecord record : parser) {
		    	BatchRun run = new BatchRun();
		    	run.setName(record.get(RUN_ID_HEADER));
		    	for (Entry<String, Long> global: getBatchGlobalParameters(workflow).entrySet()) {
		    		for (String value : record.get(global.getKey()).split(",")) {
		    			run.addGlobalValue(global.getValue(), value);
		    		}
		    	}
		    	for (Entry<String, Long> step: getAllBatchStepParameters(workflow).entrySet()) {
		    		for (String value: record.get(step.getKey()).split(",")) {
		    			run.addStepValue(step.getValue(), value);	
		    		}
		    	}
		    	batch.addRun(run);
		    }
		} catch (IOException | IllegalArgumentException e) {
			throw new PladipusReportableException(exceptionMessages.getMessage("batch.parseError", e.getMessage()));
		} finally {
			try {
				if (parser != null) parser.close();
				if (reader != null) reader.close();
			} catch (IOException e) {
				// Ignore
			}
		}
		return batch;
	}

	@Override
	public List<String> getHeaders(Workflow workflow) {
		List<String> headerList = new ArrayList<String>();
		headerList.add(RUN_ID_HEADER);
		headerList.addAll(getBatchGlobalParameters(workflow).keySet());
		headerList.addAll(getAllBatchStepParameters(workflow).keySet());
		return headerList;
	}
	
	private Map<String, Long> getBatchGlobalParameters(Workflow workflow) {
		Map<String, Long> paramMap = new TreeMap<String, Long>();
		Set<WorkflowGlobalParameter> globals = workflow.getGlobalParams();
		Predicate<WorkflowGlobalParameter> pred = gp -> gp.getValues().isEmpty();
		for (WorkflowGlobalParameter param: globals) {
			if (pred.test(param)) {
				paramMap.put(param.getName(), param.getGlobalId());
			}
		}
		return paramMap;
	}
	
	private Map<String, Long> getAllBatchStepParameters(Workflow workflow) {
		Map<String, Long> allStepParams = new TreeMap<String, Long>();
		for (Step step: workflow.getTemplateSteps()) {
			allStepParams.putAll(getBatchStepParameters(step));
		}
		return allStepParams;
	}
	
	private Map<String, Long> getBatchStepParameters(Step step) {
		Map<String, Long> paramMap = new HashMap<String, Long>();
		Set<WorkflowStepParameter> stepParams = step.getStepParams();
		Predicate<WorkflowStepParameter> pred = sp -> sp.getValues().isEmpty();
		for (WorkflowStepParameter param: stepParams) {
			if (pred.test(param)) {
				paramMap.put(step.getId() + "." + param.getName(), param.getStepParamId());
			}
		}
		return paramMap;
	}
}
