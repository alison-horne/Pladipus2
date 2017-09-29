package com.compomics.pladipus.base.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import com.compomics.pladipus.base.WorkflowControl;
import com.compomics.pladipus.base.helper.ValidationChecker;
import com.compomics.pladipus.model.persist.Workflow;
import com.compomics.pladipus.model.persist.User;
import com.compomics.pladipus.shared.PladipusReportableException;
import com.compomics.pladipus.shared.XMLHelper;
import com.compomics.pladipus.repository.service.WorkflowService;

public class WorkflowControlImpl implements WorkflowControl {
	
	@Autowired
	@Lazy
	private WorkflowService workflowService;
	
	@Autowired
	private ValidationChecker<Workflow> workflowValidator;
	
	@Autowired
	private XMLHelper<Workflow> workflowXMLHelper;

	@Override
	public void createWorkflow(String content, User user) throws PladipusReportableException {
		Workflow workflow = parseAndValidate(content, user);
		workflowService.insertWorkflow(workflow);
	}

	@Override
	public void replaceWorkflow(String content, User user) throws PladipusReportableException {
		Workflow workflow = parseAndValidate(content, user);
		workflowService.replaceWorkflow(workflow);
	}
	
	@Override
	public Workflow getNamedWorkflow(String name, User user) throws PladipusReportableException {
		return workflowService.getActiveWorkflowByName(name, user);
	}
	
	@Override
	public List<Workflow> getActiveWorkflows(User user) throws PladipusReportableException {
		return workflowService.getAllActiveWorkflowsForUser(user);
	}
	
	private Workflow parseAndValidate(String content, User user) throws PladipusReportableException {
		Workflow workflow = workflowXMLHelper.parseXml(content);
		workflow.setTemplateXml(content);
		workflow.setUser(user);
		workflowValidator.validate(workflow);
		return workflow;
	}
}
