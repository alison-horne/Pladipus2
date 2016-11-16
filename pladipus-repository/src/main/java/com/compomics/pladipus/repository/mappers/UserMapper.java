package com.compomics.pladipus.repository.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.compomics.pladipus.model.core.User;
import com.compomics.pladipus.model.db.RolesColumn;
import com.compomics.pladipus.model.db.UsersColumn;

/**
 * Map result of user SELECT query into User object
 */
public class UserMapper implements RowMapper<User> {
	 
	public User mapRow(ResultSet rs, int rowNum) throws SQLException {
		User user = new User();
		user.setId(rs.getInt(UsersColumn.USER_ID.toString()));
		user.setUsername(rs.getString(UsersColumn.USER_NAME.toString()));
		user.setEmail(rs.getString(UsersColumn.EMAIL.toString()));
		user.setPasswordEncrypted(rs.getString(UsersColumn.PASSWORD.toString()));
		user.setActive(rs.getBoolean(UsersColumn.ACTIVE.toString()));
		user.setUserRole(rs.getString(RolesColumn.ROLE_NAME.toString()));
		user.clearTrackedChanges();
		return user;
	}  
}
