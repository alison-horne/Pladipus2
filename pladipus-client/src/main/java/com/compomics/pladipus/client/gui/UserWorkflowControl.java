package com.compomics.pladipus.client.gui;

import java.util.List;

import com.compomics.pladipus.model.core.BatchOverview;
import com.compomics.pladipus.model.core.WorkflowOverview;
import com.compomics.pladipus.model.persist.Workflow;

import javafx.collections.ObservableList;

public interface UserWorkflowControl {

	public ObservableList<WorkflowOverview> getUserWorkflows();
	public WorkflowOverview getWorkflowOverview(String name);
	public WorkflowOverview saveWorkflow(Workflow workflow, List<String> headers);
	public void addWorkflow(WorkflowOverview workflow);
	public void clear();
	public void removeWorkflow(WorkflowOverview workflow);
	public void removeBatch(BatchOverview batch);
}
