package com.compomics.pladipus.client.gui.impl;

import java.util.List;

import com.compomics.pladipus.client.gui.UserWorkflowControl;
import com.compomics.pladipus.model.core.BatchOverview;
import com.compomics.pladipus.model.core.WorkflowOverview;
import com.compomics.pladipus.model.persist.Workflow;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class UserWorkflowControlImpl implements UserWorkflowControl {
	private ObservableList<WorkflowOverview> userWorkflows = FXCollections.observableArrayList();
	
	@Override
	public ObservableList<WorkflowOverview> getUserWorkflows() {
		return userWorkflows;
	}
	
	@Override
	public void clear() {
		userWorkflows.clear();
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
	public WorkflowOverview saveWorkflow(Workflow workflow, List<String> headers) {
		WorkflowOverview wo = getWorkflowOverview(workflow.getName());
		if (wo == null) {
			wo = new WorkflowOverview(workflow.getName(), workflow.getTemplateXml());
			userWorkflows.add(wo);
		} else {
			wo.setXml(workflow.getTemplateXml());
			wo.getBatches().clear();
		}
		wo.setHeaders(headers);
		return wo;
	}
	
	@Override
	public void addWorkflow(WorkflowOverview workflow) {
		userWorkflows.add(workflow);
	}

	@Override
	public void removeWorkflow(WorkflowOverview workflow) {
		userWorkflows.remove(workflow);
	}
	
	@Override
	public void removeBatch(BatchOverview batch) {
		for (WorkflowOverview workflow: userWorkflows) {
			if (workflow.removeBatch(batch)) return;
		}
	}
}
