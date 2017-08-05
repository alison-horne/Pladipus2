package com.compomics.pladipus.client.gui;

import com.compomics.pladipus.client.gui.model.WorkflowOverview;
import com.compomics.pladipus.shared.PladipusReportableException;

import javafx.collections.ObservableList;

public interface UserWorkflowControl {

	public ObservableList<WorkflowOverview> getUserWorkflows() throws PladipusReportableException;
	public WorkflowOverview getWorkflowOverview(String name);
	public void logout();
}
