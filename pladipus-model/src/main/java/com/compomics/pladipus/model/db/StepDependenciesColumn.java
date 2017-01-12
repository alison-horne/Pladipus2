package com.compomics.pladipus.model.db;

/**
 * Columns in the step_dependencies table
 */
public enum StepDependenciesColumn {

	WORKFLOW_STEP_ID,
	PREREQUISITE_STEP_ID;
	
	private static final String TABLE = DbTable.STEP_DEPENDENCIES.name();
	
	@Override
	public String toString() {
		return TABLE + "." + name();
	}
}