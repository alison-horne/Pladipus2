package com.compomics.pladipus.repository.dao.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.RowMapper;

import com.compomics.pladipus.model.core.Step;
import com.compomics.pladipus.model.db.DbTable;
import com.compomics.pladipus.model.db.WorkflowStepsColumn;
import com.compomics.pladipus.repository.dao.BaseDAOImpl;
import com.compomics.pladipus.repository.mappers.WorkflowStepMapper;

/**
 * Access workflow_steps database table
 */
public class WorkflowStepDAOImpl extends BaseDAOImpl<Step>{
	private static final String TYPE = "step";
	
	public WorkflowStepDAOImpl(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	protected Map<String, Object> mapDbColumns(Step step) {
		Map<String, Object> columnMap = new HashMap<String, Object>();
		columnMap.put(WorkflowStepsColumn.STEP_IDENTIFIER.name(), step.getStepIdentifier());
		columnMap.put(WorkflowStepsColumn.WORKFLOW_ID.name(), (step.getWorkflowId() < 0) ? null : step.getWorkflowId());
		columnMap.put(WorkflowStepsColumn.WORKFLOW_STEP_ID.name(), (step.getId() < 0) ? null : step.getId());
		columnMap.put(WorkflowStepsColumn.TOOL_TYPE.name(), step.getToolType());
		return columnMap;
	}

	@Override
	protected String getTableName() {
		return DbTable.WORKFLOW_STEPS.name();
	}

	@Override
	protected String getType() {
		return TYPE;
	}

	@Override
	protected RowMapper<Step> getRowMapper() {
		return new WorkflowStepMapper();
	}

	@Override
	protected boolean isInsertValid(List<String> insertColumns) {
		if (insertColumns.contains(WorkflowStepsColumn.STEP_IDENTIFIER.name()) &&
			insertColumns.contains(WorkflowStepsColumn.WORKFLOW_ID.name()) &&
			insertColumns.contains(WorkflowStepsColumn.TOOL_TYPE.name())) {
			return true;
		}
		return false;
	}

	@Override
	protected List<String> getUniqueIdentifier() {
		return Arrays.asList(WorkflowStepsColumn.WORKFLOW_STEP_ID.name());
	}
	
}
