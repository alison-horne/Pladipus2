package com.compomics.pladipus.repository.service.impl;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.pladipus.model.core.Step;
import com.compomics.pladipus.model.core.Workflow;
import com.compomics.pladipus.model.db.WorkflowsColumn;
import com.compomics.pladipus.model.exceptions.PladipusMessages;
import com.compomics.pladipus.model.exceptions.PladipusReportableException;
import com.compomics.pladipus.repository.dao.BaseDAO;
import com.compomics.pladipus.repository.dao.Query;
import com.compomics.pladipus.repository.service.WorkflowService;

public class WorkflowServiceImpl implements WorkflowService {
	
	@Autowired
	private PladipusMessages exceptionMessages;
	
	@Autowired
	private BaseDAO<Workflow> workflowDAO;
	
	@Autowired
	private BaseDAO<Step> workflowStepDAO;

	@Transactional(rollbackFor={Exception.class})
	@Override
	public Workflow insertWorkflow(Workflow workflow) throws PladipusReportableException {
		if (getActiveWorkflowByName(workflow.getWorkflowName(), workflow.getUserId()) != null) {
			throw new PladipusReportableException(exceptionMessages.getMessage("db.workflowExists", workflow.getWorkflowName()));
		}
		insertWholeWorkflow(workflow);
		return workflow;
	}
	
	@Transactional(rollbackFor={Exception.class})
	@Override
	public Workflow replaceWorkflow(Workflow workflow) throws PladipusReportableException {
		endOldWorkflow(workflow);
		insertWholeWorkflow(workflow);
		return workflow;
	}

	@Override
	public List<Workflow> getAllActiveWorkflowsForUser(int userId) throws PladipusReportableException {
		Query query = new Query();
		query.setWhereClause("WHERE " + WorkflowsColumn.USER_ID.name() + " = :userid AND " + WorkflowsColumn.ACTIVE.name() + " = true");
		query.setParameters(new MapSqlParameterSource().addValue("userid", userId));
		return workflowDAO.getList(query);
	}
	
	@Override
	public Workflow getActiveWorkflowByName(String workflowName, int userId) throws PladipusReportableException {
		Query query = new Query();
		query.setWhereClause("WHERE " + WorkflowsColumn.USER_ID.name() + " = :userid AND " 
									  + WorkflowsColumn.ACTIVE.name() + " = true AND "
									  + WorkflowsColumn.WORKFLOW_NAME.name() + " = :workflowname");
		query.setParameters(new MapSqlParameterSource().addValue("userid", userId).addValue("workflowname", workflowName));
		return workflowDAO.get(query);
	}

	private void endOldWorkflow(Workflow workflow) throws PladipusReportableException {
		Workflow oldWorkflow = getActiveWorkflowByName(workflow.getWorkflowName(), workflow.getUserId());
		if (oldWorkflow != null) {
			deactivateWorkflow(oldWorkflow);
		}
	}
	
	@Override
	public void deactivateWorkflow(Workflow workflow) throws PladipusReportableException {
		workflow.setActive(false);
		workflowDAO.update(workflow);
	}
	
	private void insertWholeWorkflow(Workflow workflow) throws PladipusReportableException {
		workflow.setId(workflowDAO.insert(workflow));
		insertSteps(workflow);
		//TODO insert into other tables - params etc.
	}
	
	private void insertSteps(Workflow workflow) throws PladipusReportableException {
		List<Step> steps = workflow.getSteps();
		Iterator<Step> iter = steps.iterator();
		while (iter.hasNext()) {
			Step step = iter.next();
			step.setWorkflowId(workflow.getId()); 
			step.setId(workflowStepDAO.insert(step));
		}
	}
}
