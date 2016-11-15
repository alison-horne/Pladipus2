package com.compomics.pladipus.model.core;

import java.util.HashSet;
import java.util.Set;

/**
 * Keep track of changes to objects which we allow to be updated in the database.
 */
public class UpdateTracked {
	private Set<String> updateColumns = new HashSet<String>();
	
	public Set<String> getUpdateColumns() {
		return updateColumns;
	}
	
	public void clearTrackedChanges() {
		updateColumns.clear();
	}
	
	public void trackColumn(String column) {
		updateColumns.add(column);
	}
}
