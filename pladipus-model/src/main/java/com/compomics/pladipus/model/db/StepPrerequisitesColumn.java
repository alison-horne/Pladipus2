package com.compomics.pladipus.model.db;

/**
 * Columns in the step_prerequisites table
 */
public enum StepPrerequisitesColumn {

	WORKFLOW_STEP_ID,
	PREREQUISITE_STEP_ID;
	
	private static final String TABLE = DbTable.STEP_PREREQUISITES.name();
	
	@Override
	public String toString() {
		return TABLE + "." + name();
	}
}