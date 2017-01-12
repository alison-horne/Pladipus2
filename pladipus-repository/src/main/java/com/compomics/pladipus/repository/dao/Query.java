package com.compomics.pladipus.repository.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

/**
 * Database query to be executed by NamedParameterJdbcTemplate.
 * Consists of - sql string to be executed; and map of query parameters
 */
public class Query {
	private String sql;
	private MapSqlParameterSource namedParameters;
	private String whereClause;
	private List<MapSqlParameterSource> batchNamedParameters = new ArrayList<MapSqlParameterSource>();
	
	public void setSql(String sql) {
		this.sql = sql;
	}
	
	public String getSql() {
		return sql;
	}
	
	public void setParameters(MapSqlParameterSource namedParameters) {
		this.namedParameters = namedParameters;
	}
	
	public MapSqlParameterSource getParameters() {
		return (namedParameters == null) ? new MapSqlParameterSource() : namedParameters;
	}
	
	/**
	 * For cases when it is useful to keep the "WHERE" clause separate when building a sql 
	 * query.  If the rest of the query string is not set, a suitable default will be used.
	 * @param whereClause (include WHERE keyword)
	 */
	public void setWhereClause(String whereClause) {
		this.whereClause = whereClause;
	}
	
	public String getWhereClause() {
		return (whereClause == null) ? "" : whereClause;
	}
	
	public MapSqlParameterSource[] getBatchNamedParameters() {
		return batchNamedParameters.toArray(new MapSqlParameterSource[0]);
	}
	
	public void addBatchParameter(MapSqlParameterSource source) {
		batchNamedParameters.add(source);
	}
}
