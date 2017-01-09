package com.compomics.pladipus.model.db;

/**
 * Columns in the workflow_global_values table
 */
public enum WorkflowGlobalValuesColumn {

	WORKFLOW_GLOBAL_ID,
	PARAMETER_VALUE;
	
	private static final String TABLE = DbTable.WORKFLOW_GLOBAL_VALUES.name();
	
	@Override
	public String toString() {
		return TABLE + "." + name();
	}
}