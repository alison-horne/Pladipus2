package com.compomics.pladipus.base;

import org.w3c.dom.Document;

import com.compomics.pladipus.model.core.Workflow;
import com.compomics.pladipus.model.exceptions.PladipusReportableException;

/**
 * Creation, validation, and retrieval of user workflows.
 */
public interface WorkflowControl {
	public Workflow createWorkflow(String filepath, int userId) throws PladipusReportableException;
	public Workflow createWorkflow(Document document, int userId) throws PladipusReportableException;
	public Workflow replaceWorkflow(String filepath, int userId) throws PladipusReportableException;
	public Workflow replaceWorkflow(Document document, int userId) throws PladipusReportableException;
}
