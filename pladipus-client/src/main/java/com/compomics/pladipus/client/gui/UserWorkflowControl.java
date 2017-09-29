package com.compomics.pladipus.client.gui;

import java.util.List;

import com.compomics.pladipus.model.core.WorkflowOverview;
import com.compomics.pladipus.model.persist.Workflow;
import com.compomics.pladipus.shared.PladipusReportableException;

import javafx.collections.ObservableList;

public interface UserWorkflowControl {

	public ObservableList<WorkflowOverview> getUserWorkflows() throws PladipusReportableException;
	public WorkflowOverview getWorkflowOverview(String name);
	public void saveWorkflow(Workflow workflow, List<String> headers) throws PladipusReportableException;
	public void addWorkflow(WorkflowOverview workflow);
}
