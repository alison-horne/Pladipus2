package com.compomics.pladipus.repository.dao.impl;

import javax.sql.DataSource;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.compomics.pladipus.model.core.User;
import com.compomics.pladipus.model.db.DbTable;
import com.compomics.pladipus.model.db.RolesColumn;
import com.compomics.pladipus.model.db.UserRolesColumn;
import com.compomics.pladipus.shared.PladipusReportableException;
import com.compomics.pladipus.repository.dao.Query;

/**
 * Queries for updating user_roles table
 */
public class UserRoleDAOImpl extends UserDAOImpl {
	
	public UserRoleDAOImpl(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	protected Query constructInsertQuery(User user) throws PladipusReportableException {
		int userId = user.getId();
		if (userId < 0) {
			throw new PladipusReportableException(getMessage("db.invalidInsert", "user role"));		
		}
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO ");
		sql.append(DbTable.USER_ROLES);
		sql.append(" (");
		sql.append(UserRolesColumn.USER_ID.name());
		sql.append(", ");
		sql.append(UserRolesColumn.ROLE_ID.name());
		sql.append(") SELECT :userid, ");
		sql.append(RolesColumn.ROLE_ID.toString());
		sql.append(" FROM ");
		sql.append(DbTable.ROLES);
		sql.append(" WHERE UPPER(");
		sql.append(RolesColumn.ROLE_NAME.toString());
		sql.append(") = :rolename");

		MapSqlParameterSource namedParameters = new MapSqlParameterSource()
													.addValue("rolename", user.getUserRole().getRole().name())
													.addValue("userid", userId);
		Query query = new Query();
		query.setSql(sql.toString());
		query.setParameters(namedParameters);
		return query;
	}

	@Override
	protected Query constructUpdateQuery(User user) throws PladipusReportableException {
		int userId = user.getId();
		if (userId < 0) {
			throw new PladipusReportableException(getMessage("db.invalidUpdate", "user role"));		
		}
		
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE ");
		sql.append(DbTable.USER_ROLES);
		sql.append(" SET ");
		sql.append(UserRolesColumn.ROLE_ID.name());
		sql.append(" = (SELECT ");
		sql.append(RolesColumn.ROLE_ID.name());
		sql.append(" FROM ");
		sql.append(DbTable.ROLES);
		sql.append(" WHERE UPPER(");
		sql.append(RolesColumn.ROLE_NAME.name());
		sql.append(") = :rolename) WHERE ");
		sql.append(UserRolesColumn.USER_ID.name());
		sql.append(" = :userid");
		
		MapSqlParameterSource namedParameters = new MapSqlParameterSource()
													.addValue("rolename", user.getUserRole().getRole().name())
													.addValue("userid", userId);
		
		Query query = new Query();
		query.setSql(sql.toString());
		query.setParameters(namedParameters);
		return query;
	}
	
	@Override
	protected void clearTrackedColumns(User user) {
		// Do nothing here, not tracking user roles.
	}
}
