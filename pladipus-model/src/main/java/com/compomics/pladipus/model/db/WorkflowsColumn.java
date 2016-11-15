package com.compomics.pladipus.model.db;

/**
 * Columns in the WORKFLOWS table
 */
public enum WorkflowsColumn {
	  WORKFLOW_ID,
	  WORKFLOW_NAME,
	  TEMPLATE,
	  USER_ID,
	  ACTIVE;
	
	private static final String TABLE = DbTable.WORKFLOWS.name();
	
	@Override
	public String toString() {
		return TABLE + "." + name();
	}
}
