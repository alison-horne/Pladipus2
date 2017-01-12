package com.compomics.pladipus.repository.dao.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.compomics.pladipus.model.core.Parameter;
import com.compomics.pladipus.model.db.DbTable;
import com.compomics.pladipus.model.db.WorkflowStepParamsColumn;
import com.compomics.pladipus.model.db.WorkflowStepValuesColumn;
import com.compomics.pladipus.repository.dao.BaseDAOImpl;
import com.compomics.pladipus.repository.dao.Query;
import com.compomics.pladipus.repository.mappers.WorkflowStepParamMapper;

/**
 * Access workflow_step_params database table
 */
public class WorkflowStepParamDAOImpl extends BaseDAOImpl<Parameter> {
	private static final String TYPE = "workflow step parameter";
	
	public WorkflowStepParamDAOImpl(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	protected Map<String, Object> mapDbColumns(Parameter param) {
		Map<String, Object> columnMap = new HashMap<String, Object>();
		columnMap.put(WorkflowStepParamsColumn.WORKFLOW_STEP_ID.name(), (param.getEnclosingId() < 0) ? null : param.getEnclosingId());
		columnMap.put(WorkflowStepParamsColumn.WKF_STEP_PARAM_ID.name(), (param.getId() < 0) ? null : param.getId());
		columnMap.put(WorkflowStepParamsColumn.PARAMETER_NAME.name(), param.getParameterName());
		return columnMap;
	}

	@Override
	protected String getTableName() {
		return DbTable.WORKFLOW_STEP_PARAMS.name();
	}

	@Override
	protected String getType() {
		return TYPE;
	}

	@Override
	protected RowMapper<Parameter> getRowMapper() {
		return new WorkflowStepParamMapper();
	}

	@Override
	protected boolean isInsertValid(List<String> insertColumns) {
		if (insertColumns.contains(WorkflowStepParamsColumn.WORKFLOW_STEP_ID.name()) &&
			insertColumns.contains(WorkflowStepParamsColumn.PARAMETER_NAME.name())) {
			return true;
		}
		return false;
	}

	@Override
	protected List<String> getUniqueIdentifier() {
		return Arrays.asList(WorkflowStepParamsColumn.WKF_STEP_PARAM_ID.name());
	}
	
	@Override
	protected Query getBatchInsertQuery(Parameter parameter) {
		Set<String> values = parameter.getValues();
		if ((values == null) || (values.isEmpty())) {
			return null;
		}
		Query query = new Query();
		query.setSql(VALUES_INSERT_SQL);
		for (String value: values) {
			query.addBatchParameter(new MapSqlParameterSource().addValue("id", parameter.getId()).addValue("value", value));
		}
		return query;
	}
	
	private static final String VALUES_INSERT_SQL = 
			"INSERT INTO " + DbTable.WORKFLOW_STEP_VALUES.name() + " (" +
			WorkflowStepValuesColumn.WKF_STEP_PARAM_ID.name() + ", " +
			WorkflowStepValuesColumn.PARAMETER_VALUE.name() + ") VALUES (:id, :value)";
	
}
