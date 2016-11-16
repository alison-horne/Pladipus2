package com.compomics.pladipus.repository.dao.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.RowMapper;

import com.compomics.pladipus.model.core.User;
import com.compomics.pladipus.model.db.DbTable;
import com.compomics.pladipus.model.db.RolesColumn;
import com.compomics.pladipus.model.db.UserRolesColumn;
import com.compomics.pladipus.model.db.UsersColumn;
import com.compomics.pladipus.repository.dao.BaseDAOImpl;
import com.compomics.pladipus.repository.mappers.UserMapper;

/**
 * Access users in the database
 */
public class UserDAOImpl extends BaseDAOImpl<User> {
	private static final String TYPE = "user";
	
	public UserDAOImpl(DataSource dataSource) {
		super(dataSource);
	}
	
	@Override
	protected String getTableName() {
		return DbTable.USERS.name();
	}
	
	public Map<String, Object> mapDbColumns (User user) {
		Map<String, Object> columnMap = new HashMap<String, Object>();
		columnMap.put(UsersColumn.ACTIVE.name(), user.isActive());
		columnMap.put(UsersColumn.EMAIL.name(), user.getEmail());
		columnMap.put(UsersColumn.PASSWORD.name(), user.getPasswordEncrypted());
		columnMap.put(UsersColumn.USER_ID.name(), (user.getId() < 0) ? null : user.getId());
		columnMap.put(UsersColumn.USER_NAME.name(), user.getUsername());
		return columnMap;
	}
	
	@Override
	protected boolean isInsertValid(List<String> insertColumns) {
		if (!insertColumns.contains(UsersColumn.USER_NAME.name())) {
			return false;
		} 
		return true;
	}
	
	@Override
	protected String getSelect() {
		return GET_SELECT;
	}

	@Override
	protected RowMapper<User> getRowMapper() {
		return new UserMapper();
	}

	private static final String GET_SELECT =
			"SELECT " + DbTable.USERS + ".*, " + RolesColumn.ROLE_NAME + " " +
			"FROM " + DbTable.USERS + " " +
		    "LEFT JOIN " + DbTable.USER_ROLES + " ON " + UsersColumn.USER_ID.toString() + " = " + UserRolesColumn.USER_ID.toString() + " " +
		    "LEFT JOIN " + DbTable.ROLES + " ON " + UserRolesColumn.ROLE_ID.toString() + " = " + RolesColumn.ROLE_ID.toString();

	@Override
	protected String getType() {
		return TYPE;
	}

	@Override
	protected List<String> getUniqueIdentifier() {
		return Arrays.asList(UsersColumn.USER_ID.name());
	}
}