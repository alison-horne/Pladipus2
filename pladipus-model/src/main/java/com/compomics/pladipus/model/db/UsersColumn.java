package com.compomics.pladipus.model.db;

/**
 * Columns in the USERS table
 */
public enum UsersColumn {
	  USER_ID,
	  USER_NAME,
	  EMAIL,
	  PASSWORD,
	  ACTIVE;
	
	private static final String TABLE = DbTable.USERS.name();
	
	@Override
	public String toString() {
		return TABLE + "." + name();
	}
}
