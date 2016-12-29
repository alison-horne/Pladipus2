package com.compomics.pladipus.model.db;

/**
 * Columns in the COMMON_DEFAULTS table
 */
public enum DefaultsColumn {

	DEFAULT_ID,
	USER_ID,
	DEFAULT_TYPE,
	DEFAULT_VALUE,
	NAME;
	
	private static final String TABLE = DbTable.COMMON_DEFAULTS.name();
	
	@Override
	public String toString() {
		return TABLE + "." + name();
	}
}
