package com.compomics.pladipus.base.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;

import com.compomics.pladipus.base.WorkflowControl;
import com.compomics.pladipus.base.helper.XMLHelper;
import com.compomics.pladipus.model.core.Workflow;
import com.compomics.pladipus.model.exceptions.PladipusReportableException;
import com.compomics.pladipus.repository.service.WorkflowService;

public class WorkflowControlImpl implements WorkflowControl {
	
	@Autowired
	private WorkflowService workflowService;
	
	@Autowired
	private XMLHelper<Workflow> workflowXMLHelper;

	@Override
	public Workflow createWorkflow(String filepath, int userId) throws PladipusReportableException {
		return createWorkflow(workflowXMLHelper.fileToDocument(filepath), userId);
	}
	
	@Override
	public Workflow replaceWorkflow(String filepath, int userId) throws PladipusReportableException {
		return replaceWorkflow(workflowXMLHelper.fileToDocument(filepath), userId);
	}

	@Override
	public Workflow createWorkflow(Document document, int userId) throws PladipusReportableException {
		Workflow workflow = parseAndValidate(document, userId);
		return workflowService.insertWorkflow(workflow);
	}

	@Override
	public Workflow replaceWorkflow(Document document, int userId) throws PladipusReportableException {
		Workflow workflow = parseAndValidate(document, userId);
		return workflowService.replaceWorkflow(workflow);
	}
	
	private Workflow parseAndValidate(Document document, int userId) throws PladipusReportableException {
		workflowXMLHelper.validateDocument(document);
		Workflow workflow = workflowXMLHelper.parseDocument(document);
		workflow.setUserId(userId);
		//TODO Extra validation - check tool names valid, and parameters are as in tools
		// Workflow check for valid parameters - no circular step dependencies, defaults exist, etc.
		// Add mandatory params that haven't already been mentioned, with defaults if they exist.
		return workflow;
	}
}