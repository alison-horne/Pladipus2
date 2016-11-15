package com.compomics.pladipus.model.db;

/**
 * Columns in the USER_ROLES table
 */
public enum UserRolesColumn {
	USER_ID,
	ROLE_ID;
	
	private static final String TABLE = DbTable.USER_ROLES.name();
	
	@Override
	public String toString() {
		return TABLE + "." + name();
	}
}
