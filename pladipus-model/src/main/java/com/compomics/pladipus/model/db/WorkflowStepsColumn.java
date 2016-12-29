package com.compomics.pladipus.model.db;

/**
 * Columns in the WORKFLOW_STEPS table
 */
public enum WorkflowStepsColumn {
	WORKFLOW_STEP_ID,
	WORKFLOW_ID,
	STEP_IDENTIFIER,
	TOOL_TYPE;
	
	private static final String TABLE = DbTable.WORKFLOW_STEPS.name();
	
	@Override
	public String toString() {
		return TABLE + "." + name();
	}
}
