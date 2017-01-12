package com.compomics.pladipus.repository.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.compomics.pladipus.model.core.Parameter;
import com.compomics.pladipus.model.db.WorkflowStepParamsColumn;

/**
 * Map result of workflow_step_params SELECT query into Parameter object
 */
public class WorkflowStepParamMapper implements RowMapper<Parameter> {
	 
	public Parameter mapRow(ResultSet rs, int rowNum) throws SQLException {
		Parameter param = new Parameter(rs.getInt(WorkflowStepParamsColumn.WORKFLOW_STEP_ID.toString()));
		param.setId(rs.getInt(WorkflowStepParamsColumn.WKF_STEP_PARAM_ID.toString()));
		param.setParameterName(rs.getString(WorkflowStepParamsColumn.PARAMETER_NAME.toString()));
		param.clearTrackedChanges();
		return param;
	}
}
