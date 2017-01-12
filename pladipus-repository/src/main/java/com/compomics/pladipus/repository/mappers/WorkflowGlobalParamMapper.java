package com.compomics.pladipus.repository.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.compomics.pladipus.model.core.Parameter;
import com.compomics.pladipus.model.db.WorkflowGlobalParamsColumn;

/**
 * Map result of workflow_global_params SELECT query into Parameter object
 */
public class WorkflowGlobalParamMapper implements RowMapper<Parameter> {
	 
	public Parameter mapRow(ResultSet rs, int rowNum) throws SQLException {
		Parameter param = new Parameter(rs.getInt(WorkflowGlobalParamsColumn.WORKFLOW_ID.toString()));
		param.setId(rs.getInt(WorkflowGlobalParamsColumn.WORKFLOW_GLOBAL_ID.toString()));
		param.setParameterName(rs.getString(WorkflowGlobalParamsColumn.PARAMETER_NAME.toString()));
		param.clearTrackedChanges();
		return param;
	}
}
