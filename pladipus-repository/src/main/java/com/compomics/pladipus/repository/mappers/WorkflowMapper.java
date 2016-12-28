package com.compomics.pladipus.repository.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.compomics.pladipus.model.core.Workflow;
import com.compomics.pladipus.model.db.WorkflowsColumn;

/**
 * Map result of workflow SELECT query into Workflow object
 */
public class WorkflowMapper implements RowMapper<Workflow> {
	 
	public Workflow mapRow(ResultSet rs, int rowNum) throws SQLException {
		Workflow workflow = new Workflow();
		workflow.setId(rs.getInt(WorkflowsColumn.WORKFLOW_ID.toString()));
		workflow.setWorkflowName(rs.getString(WorkflowsColumn.WORKFLOW_NAME.toString()));
		workflow.setTemplate(rs.getString(WorkflowsColumn.TEMPLATE.toString()));
		workflow.setUserId(rs.getInt(WorkflowsColumn.USER_ID.toString()));
		workflow.setActive(rs.getBoolean(WorkflowsColumn.ACTIVE.toString()));
		workflow.clearTrackedChanges();
		return workflow;
	}
}
