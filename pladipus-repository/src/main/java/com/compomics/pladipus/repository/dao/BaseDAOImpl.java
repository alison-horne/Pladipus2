package com.compomics.pladipus.repository.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.compomics.pladipus.model.core.UpdateTracked;
import com.compomics.pladipus.model.exceptions.PladipusMessages;
import com.compomics.pladipus.model.exceptions.PladipusReportableException;

public abstract class BaseDAOImpl<T extends UpdateTracked> extends NamedParameterJdbcTemplate implements BaseDAO<T> {
	
	@Autowired
	private PladipusMessages exceptionMessages;
	
	@Autowired
	public BaseDAOImpl(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	public int insert(T t) throws PladipusReportableException {
		Query query = constructInsertQuery(t);
		int inserted = insert(query);
		clearTrackedColumns(t);
		return inserted;
	}
	
	@Override
	public int insert(Query query) throws PladipusReportableException {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		int inserted;
		try {
			if ((inserted = update(query.getSql(), query.getParameters(), keyHolder)) != 1) {
			    throw new PladipusReportableException(getMessage("db.nonUniqueInsert", getType(), inserted));
			}
			return keyHolder.getKey() == null? 0 : keyHolder.getKey().intValue();
		} catch (DataAccessException e) {
			throw new PladipusReportableException(getMessage("db.insertError", getType(), getExceptionCause(e)));
		}
	}
	
	@Override
	public int batchInsert(T t) throws PladipusReportableException {
		Query query = getBatchInsertQuery(t);
		if (query == null) return 0;
		return batchQueryInsert(query);
	}

	protected int batchQueryInsert(Query query) throws PladipusReportableException {
		try {
			int[] inserts = batchUpdate(query.getSql(), query.getBatchNamedParameters());
			return inserts.length;
		} catch (Exception e) {
			throw new PladipusReportableException(getMessage("db.insertError", getType(), getExceptionCause(e)));
		}
	}
	
	@Override
	public T get(Query query) throws PladipusReportableException {
		constructGetQuery(query);
		try {
			return queryForObject(query.getSql(), query.getParameters(), getRowMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (IncorrectResultSizeDataAccessException e) {
			throw new PladipusReportableException(getMessage("db.nonUniqueGet", getType()));
		} catch (DataAccessException e) {
			throw new PladipusReportableException(getMessage("db.invalidGetQuery", getExceptionCause(e)));
		}
	}

	@Override
	public List<T> getList(Query query) throws PladipusReportableException {
		constructGetQuery(query);
		try {
			return query(query.getSql(), query.getParameters(), getRowMapper());
		} catch (DataAccessException e) {
			throw new PladipusReportableException(getMessage("db.invalidGetQuery", getExceptionCause(e)));
		}
	}
	
	@Override
	public void update(T t) throws PladipusReportableException {
		Query query = constructUpdateQuery(t);
		int updated;
		if ((updated = update(query)) != 1) {
		    throw new PladipusReportableException(getMessage("db.nonUniqueUpdate", getType(), updated));
		}
		clearTrackedColumns(t);
	}
	
	@Override
	public int update(Query query) throws PladipusReportableException {
		try {
			return update(query.getSql(), query.getParameters());
		} catch (DataAccessException e) {
			throw new PladipusReportableException(getMessage("db.updateError", getExceptionCause(e)));
		}
	}
	
	protected void constructGetQuery(Query query) {
		if ((query.getSql() == null) || query.getSql().isEmpty()) {
			query.setSql(getSelect() + " " + query.getWhereClause());
		}
	}
	
	protected String getSelect() {
		return "SELECT * FROM " + getTableName();
	}

	protected Query constructInsertQuery(T t) throws PladipusReportableException {
		Map<String, Object> columnMap = mapDbColumns(t);
		List<String> insertColumns = new ArrayList<String>();
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		
		for (String column: columnMap.keySet()) {
			if (columnMap.get(column) != null) {
				insertColumns.add(column);
				namedParameters.addValue(column, columnMap.get(column));
			}
		}
		
		if (insertColumns.isEmpty() || !isInsertValid(insertColumns)) {
			throw new PladipusReportableException(getMessage("db.invalidInsert", getType()));
		}
		
		Query query = new Query();
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO ");
		sql.append(getTableName());
		sql.append(" (");
		sql.append(StringUtils.join(insertColumns, ", "));
		sql.append(") VALUES (:");
		sql.append(StringUtils.join(insertColumns, ", :"));
		sql.append(")");
				
		query.setSql(sql.toString());
		query.setParameters(namedParameters);
		return query;
	}

	protected Query constructUpdateQuery(T t) throws PladipusReportableException {
		Set<String> updatedColumns = t.getUpdateColumns();
		if (updatedColumns == null || updatedColumns.isEmpty()) {
			throw new PladipusReportableException(getMessage("db.invalidUpdate", getType()));
		}
		Map<String, Object> columnMap = mapDbColumns(t);
		Query query = constructUpdateWhereClause(columnMap);
		columnMap.keySet().retainAll(updatedColumns);
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE ");
		sql.append(getTableName());
		sql.append(" SET ");
        Iterator<Entry<String, Object>> iter = columnMap.entrySet().iterator();
        while (iter.hasNext()) {
        	Entry<String, Object> updated = iter.next();
        	sql.append(updated.getKey());
        	sql.append(" = :");
        	sql.append(updated.getKey());
        	query.getParameters().addValue(updated.getKey(), updated.getValue());
        	if (iter.hasNext()) {
        		sql.append(", ");
        	}
        }
        query.setSql(sql.toString() + " " + query.getWhereClause());
		return query;
	}
	
	private Query constructUpdateWhereClause(Map<String, Object> columnMap) throws PladipusReportableException {
		
		List<String> keys = getUniqueIdentifier();
		Map<String, Object> identifiers = new HashMap<String, Object>();
		for (String key: keys) {
			identifiers.put(key, columnMap.get(key));
		}

		if (identifiers.isEmpty()) {
			throw new PladipusReportableException(getMessage("db.invalidUpdate", getType()));
		}
		
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		StringBuilder sql = new StringBuilder();
		sql.append("WHERE ");
        Iterator<Entry<String, Object>> iter = identifiers.entrySet().iterator();
        while (iter.hasNext()) {
        	Entry<String, Object> identifier = iter.next();
        	if (identifier.getValue() == null) {
        		throw new PladipusReportableException(getMessage("db.invalidUpdate", getType()));
        	}
        	sql.append(identifier.getKey());
        	sql.append(" = :");
        	sql.append(identifier.getKey());
        	namedParameters.addValue(identifier.getKey(), identifier.getValue());
        	if (iter.hasNext()) {
        		sql.append(" AND ");
        	}
        }
		
		Query query = new Query();
		query.setWhereClause(sql.toString());
		query.setParameters(namedParameters);
		return query;
	}
	
	protected void clearTrackedColumns(T t) {
		t.clearTrackedChanges();
	}
	
	protected String getMessage(String msg) {
		return exceptionMessages.getMessage(msg);
	}
	
	protected String getMessage(String msg, Object...params) {
		return exceptionMessages.getMessage(msg, params);
	}
	
	protected String getExceptionCause(Exception e) {
		return (e.getCause() != null) ? e.getCause().getMessage() : e.getMessage();
	}
	
	protected abstract Map<String, Object> mapDbColumns(T t);
	protected abstract String getTableName();
	protected abstract String getType();
	protected abstract RowMapper<T> getRowMapper();
	protected abstract boolean isInsertValid(List<String> insertColumns);
	protected abstract List<String> getUniqueIdentifier();
	protected Query getBatchInsertQuery(T t) {
		return null;
	}
}
