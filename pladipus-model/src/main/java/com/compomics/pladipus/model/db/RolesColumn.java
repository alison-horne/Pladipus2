package com.compomics.pladipus.model.db;

/**
 * Columns in the ROLES table
 */
public enum RolesColumn {
	ROLE_ID,
	ROLE_NAME;
	
	private static final String TABLE = DbTable.ROLES.name();
	
	@Override
	public String toString() {
		return TABLE + "." + name();
	}
}
