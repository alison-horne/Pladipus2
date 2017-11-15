package com.compomics.pladipus.base;

import java.util.List;

import com.compomics.pladipus.model.persist.User;
import com.compomics.pladipus.model.persist.Workflow;
import com.compomics.pladipus.shared.PladipusReportableException;

/**
 * Creation, validation, and retrieval of user workflows.
 */
public interface WorkflowControl {
	public void createWorkflow(String filecontent, User user) throws PladipusReportableException;
	public Workflow replaceWorkflow(String filecontent, User user) throws PladipusReportableException;
	public void deactivateWorkflow(String name, User user) throws PladipusReportableException;
	public Workflow getNamedWorkflow(String name, User user) throws PladipusReportableException;
	public List<Workflow> getActiveWorkflows(User user) throws PladipusReportableException;
}
