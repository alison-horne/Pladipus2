package com.compomics.pladipus.repository.dao.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.RowMapper;

import com.compomics.pladipus.model.core.Workflow;
import com.compomics.pladipus.model.db.DbTable;
import com.compomics.pladipus.model.db.WorkflowsColumn;
import com.compomics.pladipus.repository.dao.BaseDAOImpl;
import com.compomics.pladipus.repository.mappers.WorkflowMapper;

/**
 * Access workflows database table
 */
public class WorkflowDAOImpl extends BaseDAOImpl<Workflow>{
	private static final String TYPE = "workflow";
	
	public WorkflowDAOImpl(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	protected Map<String, Object> mapDbColumns(Workflow workflow) {
		Map<String, Object> columnMap = new HashMap<String, Object>();
		columnMap.put(WorkflowsColumn.ACTIVE.name(), workflow.isActive());
		columnMap.put(WorkflowsColumn.TEMPLATE.name(), workflow.getTemplate());
		columnMap.put(WorkflowsColumn.USER_ID.name(), (workflow.getUserId() < 0) ? null : workflow.getUserId());
		columnMap.put(WorkflowsColumn.WORKFLOW_ID.name(), (workflow.getId() < 0) ? null : workflow.getId());
		columnMap.put(WorkflowsColumn.WORKFLOW_NAME.name(), workflow.getWorkflowName());
		return columnMap;
	}

	@Override
	protected String getTableName() {
		return DbTable.WORKFLOWS.name();
	}

	@Override
	protected String getType() {
		return TYPE;
	}

	@Override
	protected RowMapper<Workflow> getRowMapper() {
		return new WorkflowMapper();
	}

	@Override
	protected boolean isInsertValid(List<String> insertColumns) {
		if (insertColumns.contains(WorkflowsColumn.WORKFLOW_NAME.name()) &&
			insertColumns.contains(WorkflowsColumn.TEMPLATE.name())) {
			return true;
		}
		return false;
	}

	@Override
	protected List<String> getUniqueIdentifier() {
		return Arrays.asList(WorkflowsColumn.WORKFLOW_ID.name());
	}
	
}
