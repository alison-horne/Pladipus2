package com.compomics.pladipus.base;

import org.w3c.dom.Document;

import com.compomics.pladipus.model.persist.User;
import com.compomics.pladipus.shared.PladipusReportableException;

/**
 * Creation, validation, and retrieval of user workflows.
 */
public interface WorkflowControl {
	public void createWorkflow(String filepath, User user) throws PladipusReportableException;
	public void createWorkflow(Document document, User user) throws PladipusReportableException;
	public void replaceWorkflow(String filepath, User user) throws PladipusReportableException;
	public void replaceWorkflow(Document document, User user) throws PladipusReportableException;
}
