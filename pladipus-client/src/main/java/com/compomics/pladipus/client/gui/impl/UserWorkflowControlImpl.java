package com.compomics.pladipus.client.gui.impl;

import com.compomics.pladipus.client.gui.UserWorkflowControl;
import com.compomics.pladipus.model.core.WorkflowOverview;
import com.compomics.pladipus.model.persist.Workflow;
import com.compomics.pladipus.shared.PladipusReportableException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class UserWorkflowControlImpl implements UserWorkflowControl {
	private ObservableList<WorkflowOverview> userWorkflows = FXCollections.observableArrayList();
	private boolean loaded = false;
	
	@Override
	public ObservableList<WorkflowOverview> getUserWorkflows() throws PladipusReportableException {
		if (!loaded) {
			loadWorkflows();
		}
		return userWorkflows;
	}
	
	@Override
	public WorkflowOverview getWorkflowOverview(String name) {
    	WorkflowOverview existing = null;
    	for (WorkflowOverview wf : userWorkflows) {
    		if (wf.getName().equalsIgnoreCase(name)) {
    			existing = wf;
    			break;
    		}
    	}
    	return existing;
    }
	
	private void loadWorkflows() throws PladipusReportableException {
		// TODO - get workflows from control
//		userWorkflows.addAll(TestData.getWorkflows("test1"));
		loaded = true;
	}

	@Override
	public void saveWorkflow(Workflow workflow) {
		// TODO save workflow to db...force flag if wo exists
		WorkflowOverview wo = getWorkflowOverview(workflow.getName());
		if (wo == null) {
			wo = new WorkflowOverview(workflow.getName(), workflow.getTemplateXml());
			userWorkflows.add(wo);
		} else {
			wo.setXml(workflow.getTemplateXml());
		}
	}
}
