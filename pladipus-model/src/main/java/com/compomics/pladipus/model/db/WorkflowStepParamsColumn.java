package com.compomics.pladipus.model.db;

/**
 * Columns in the workflow_step_params table
 */
public enum WorkflowStepParamsColumn {
	
	WKF_STEP_PARAM_ID,
	WORKFLOW_STEP_ID,
	PARAMETER_NAME;
	
	
	private static final String TABLE = DbTable.WORKFLOW_STEP_PARAMS.name();
	
	@Override
	public String toString() {
		return TABLE + "." + name();
	}
}