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
import com.compomics.pladipus.model.db.WorkflowGlobalParamsColumn;
import com.compomics.pladipus.model.db.WorkflowGlobalValuesColumn;
import com.compomics.pladipus.repository.dao.BaseDAOImpl;
import com.compomics.pladipus.repository.dao.Query;
import com.compomics.pladipus.repository.mappers.WorkflowGlobalParamMapper;

/**
 * Access workflow_global_params database table
 */
public class WorkflowGlobalParamDAOImpl extends BaseDAOImpl<Parameter>{
	private static final String TYPE = "workflow global parameter";
	
	public WorkflowGlobalParamDAOImpl(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	protected Map<String, Object> mapDbColumns(Parameter param) {
		Map<String, Object> columnMap = new HashMap<String, Object>();
		columnMap.put(WorkflowGlobalParamsColumn.WORKFLOW_ID.name(), (param.getEnclosingId() < 0) ? null : param.getEnclosingId());
		columnMap.put(WorkflowGlobalParamsColumn.WORKFLOW_GLOBAL_ID.name(), (param.getId() < 0) ? null : param.getId());
		columnMap.put(WorkflowGlobalParamsColumn.PARAMETER_NAME.name(), param.getParameterName());
		return columnMap;
	}

	@Override
	protected String getTableName() {
		return DbTable.WORKFLOW_GLOBAL_PARAMS.name();
	}

	@Override
	protected String getType() {
		return TYPE;
	}

	@Override
	protected RowMapper<Parameter> getRowMapper() {
		return new WorkflowGlobalParamMapper();
	}

	@Override
	protected boolean isInsertValid(List<String> insertColumns) {
		if (insertColumns.contains(WorkflowGlobalParamsColumn.WORKFLOW_ID.name()) &&
			insertColumns.contains(WorkflowGlobalParamsColumn.PARAMETER_NAME.name())) {
			return true;
		}
		return false;
	}

	@Override
	protected List<String> getUniqueIdentifier() {
		return Arrays.asList(WorkflowGlobalParamsColumn.WORKFLOW_GLOBAL_ID.name());
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
			"INSERT INTO " + DbTable.WORKFLOW_GLOBAL_VALUES.name() + " (" +
			WorkflowGlobalValuesColumn.WORKFLOW_GLOBAL_ID.name() + ", " +
			WorkflowGlobalValuesColumn.PARAMETER_VALUE.name() + ") VALUES (:id, :value)";
	
}
