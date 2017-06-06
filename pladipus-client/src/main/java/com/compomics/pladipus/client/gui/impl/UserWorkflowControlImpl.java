package com.compomics.pladipus.client.gui.impl;

import com.compomics.pladipus.client.gui.TestData;
import com.compomics.pladipus.client.gui.UserWorkflowControl;
import com.compomics.pladipus.client.gui.model.WorkflowGui;
import com.compomics.pladipus.shared.PladipusReportableException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class UserWorkflowControlImpl implements UserWorkflowControl {
	private ObservableList<WorkflowGui> userWorkflows = FXCollections.observableArrayList();
	private boolean loaded = false;
	
	@Override
	public ObservableList<WorkflowGui> getUserWorkflows() throws PladipusReportableException {
		if (!loaded) {
			loadWorkflows();
		}
		return userWorkflows;
	}
	
	@Override
	public WorkflowGui getWorkflowGui(String name) {
    	WorkflowGui existing = null;
    	for (WorkflowGui wf : userWorkflows) {
    		if (wf.getWorkflowName().equalsIgnoreCase(name)) {
    			existing = wf;
    			break;
    		}
    	}
    	return existing;
    }
	
	@Override
	public void logout() {
		userWorkflows.clear();
		loaded = false;
	}
	
	private void loadWorkflows() throws PladipusReportableException {
		// TODO - get workflows from control
		userWorkflows.addAll(TestData.getWorkflows("test1"));
		loaded = true;
	}
}
