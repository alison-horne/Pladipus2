package com.compomics.pladipus.model.db;

/**
 * Columns in the workflow_step_values table
 */
public enum WorkflowStepValuesColumn {

	WKF_STEP_PARAM_ID,
	PARAMETER_VALUE;
	
	private static final String TABLE = DbTable.WORKFLOW_STEP_VALUES.name();
	
	@Override
	public String toString() {
		return TABLE + "." + name();
	}
}