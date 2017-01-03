package com.compomics.pladipus.repository.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.compomics.pladipus.model.core.Step;
import com.compomics.pladipus.model.db.WorkflowStepsColumn;

/**
 * Map result of workflow_steps SELECT query into Step object
 */
public class WorkflowStepMapper implements RowMapper<Step> {
	 
	public Step mapRow(ResultSet rs, int rowNum) throws SQLException {
		Step step = new Step();
		step.setId(rs.getInt(WorkflowStepsColumn.WORKFLOW_STEP_ID.toString()));
		step.setStepIdentifier(rs.getString(WorkflowStepsColumn.STEP_IDENTIFIER.toString()));
		step.setToolType(WorkflowStepsColumn.TOOL_TYPE.toString());
		step.setWorkflowId(rs.getInt(WorkflowStepsColumn.WORKFLOW_ID.toString()));
		step.clearTrackedChanges();
		return step;
	}
}
