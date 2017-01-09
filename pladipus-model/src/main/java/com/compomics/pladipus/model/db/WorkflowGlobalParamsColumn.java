package com.compomics.pladipus.model.db;

/**
 * Columns in the workflow_global_params table
 */
public enum WorkflowGlobalParamsColumn {

	WORKFLOW_GLOBAL_ID,
	WORKFLOW_ID,
	PARAMETER_NAME;
	
	private static final String TABLE = DbTable.WORKFLOW_GLOBAL_PARAMS.name();
	
	@Override
	public String toString() {
		return TABLE + "." + name();
	}
}