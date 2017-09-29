package com.compomics.pladipus.client.gui.impl;

import java.util.List;

import com.compomics.pladipus.client.gui.UserWorkflowControl;
import com.compomics.pladipus.model.core.WorkflowOverview;
import com.compomics.pladipus.model.persist.Workflow;
import com.compomics.pladipus.shared.PladipusReportableException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class UserWorkflowControlImpl implements UserWorkflowControl {
	private ObservableList<WorkflowOverview> userWorkflows = FXCollections.observableArrayList();
	
	@Override
	public ObservableList<WorkflowOverview> getUserWorkflows() throws PladipusReportableException {
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

	@Override
	public void saveWorkflow(Workflow workflow, List<String> headers) {
		WorkflowOverview wo = getWorkflowOverview(workflow.getName());
		if (wo == null) {
			wo = new WorkflowOverview(workflow.getName(), workflow.getTemplateXml());
			userWorkflows.add(wo);
		} else {
			wo.setXml(workflow.getTemplateXml());
		}
		wo.setHeaders(headers);
	}
	
	@Override
	public void addWorkflow(WorkflowOverview workflow) {
		userWorkflows.add(workflow);
	}
}
