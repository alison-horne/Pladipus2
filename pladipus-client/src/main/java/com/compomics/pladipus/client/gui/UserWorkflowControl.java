package com.compomics.pladipus.client.gui;

import com.compomics.pladipus.client.gui.model.WorkflowGui;
import com.compomics.pladipus.shared.PladipusReportableException;

import javafx.collections.ObservableList;

public interface UserWorkflowControl {

	public ObservableList<WorkflowGui> getUserWorkflows() throws PladipusReportableException;
	public void logout();
}
