package com.compomics.pladipus.repository.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.pladipus.shared.PladipusMessages;
import com.compomics.pladipus.shared.PladipusReportableException;
import com.compomics.pladipus.model.persist.User;
import com.compomics.pladipus.model.persist.Workflow;
import com.compomics.pladipus.repository.persist.WorkflowRepository;
import com.compomics.pladipus.repository.service.WorkflowService;

public class WorkflowServiceImpl implements WorkflowService {
	
	@Autowired
	private PladipusMessages exceptionMessages;
	
	@Autowired
	private WorkflowRepository workflowRepo;

	@Transactional(rollbackFor={Exception.class})
	@Override
	public void insertWorkflow(Workflow workflow) throws PladipusReportableException {
		if (getActiveWorkflowByName(workflow.getName(), workflow.getUser()) != null) {
			throw new PladipusReportableException(exceptionMessages.getMessage("db.workflowExists", workflow.getName()));
		}
		workflowRepo.persist(workflow);
	}
	
	@Transactional(rollbackFor={Exception.class})
	@Override
	public void replaceWorkflow(Workflow workflow) throws PladipusReportableException {
		endOldWorkflow(workflow);
		workflowRepo.persist(workflow);
	}

	@Override
	public List<Workflow> getAllActiveWorkflowsForUser(User user) throws PladipusReportableException {
		return workflowRepo.findAllActiveWorkflowsForUser(user);
	}
	
	@Override
	public Workflow getActiveWorkflowByName(String workflowName, User user) throws PladipusReportableException {
		return workflowRepo.findUserActiveWorkflowByName(user, workflowName);
	}

	private void endOldWorkflow(Workflow workflow) throws PladipusReportableException {
		Workflow oldWorkflow = getActiveWorkflowByName(workflow.getName(), workflow.getUser());
		if (oldWorkflow != null) {
			deactivateWorkflow(oldWorkflow);
		}
	}
	
	@Override
	public void deactivateWorkflow(Workflow workflow) throws PladipusReportableException {
		workflow.setActive(false);
		workflowRepo.merge(workflow);
	}
}
