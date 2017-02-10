package com.compomics.pladipus.base.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;

import com.compomics.pladipus.base.WorkflowControl;
import com.compomics.pladipus.base.helper.ValidationChecker;
import com.compomics.pladipus.base.helper.XMLHelper;
import com.compomics.pladipus.model.persist.Workflow;
import com.compomics.pladipus.model.persist.User;
import com.compomics.pladipus.shared.PladipusReportableException;
import com.compomics.pladipus.repository.service.WorkflowService;

public class WorkflowControlImpl implements WorkflowControl {
	
	@Autowired
	private WorkflowService workflowService;
	
	@Autowired
	private ValidationChecker<Workflow> workflowValidator;
	
	@Autowired
	private XMLHelper<Workflow> workflowXMLHelper;

	@Override
	public void createWorkflow(String filepath, User user) throws PladipusReportableException {
		createWorkflow(workflowXMLHelper.filepathToDocument(filepath), user);
	}
	
	@Override
	public void replaceWorkflow(String filepath, User user) throws PladipusReportableException {
		replaceWorkflow(workflowXMLHelper.filepathToDocument(filepath), user);
	}

	@Override
	public void createWorkflow(Document document, User user) throws PladipusReportableException {
		Workflow workflow = parseAndValidate(document, user);
		workflowService.insertWorkflow(workflow);
	}

	@Override
	public void replaceWorkflow(Document document, User user) throws PladipusReportableException {
		Workflow workflow = parseAndValidate(document, user);
		workflowService.replaceWorkflow(workflow);
	}
	
	private Workflow parseAndValidate(Document document, User user) throws PladipusReportableException {
		Workflow workflow = workflowXMLHelper.parseDocument(document);
		workflow.setTemplateXml(workflowXMLHelper.documentToString(document));
		workflow.setUser(user);
		workflowValidator.validate(workflow);
		return workflow;
	}
}
