package com.compomics.pladipus.repository.service;

import java.util.List;

import com.compomics.pladipus.model.persist.Workflow;
import com.compomics.pladipus.model.persist.User;
import com.compomics.pladipus.shared.PladipusReportableException;

/**
 * Service to be used by the front end for dealing with workflows in the database.  
 */
public interface WorkflowService {
	
	/**
	 * Inserts a workflow template into the database, populating step and parameter tables.
	 * 
	 * @param workflow - Workflow object to insert into the database
	 * @throws PladipusReportableException if workflow already exists in the database for the user, or there is a problem with the database inserts.
	 */
	public void insertWorkflow(Workflow workflow) throws PladipusReportableException;
	
	/**
	 * Inserts a workflow template into the database, marking inactive any with the same name which already exists for the user.
	 * 
	 * @param workflow - Workflow object to insert into the database.
	 * @throws PladipusReportableException
	 */
	public void replaceWorkflow(Workflow workflow) throws PladipusReportableException;
	
	/**
	 * Returns a list of active workflows from the workflows table for the user.  
	 * @param user
	 * @return List of active workflows, or an empty list if none exist.
	 * @throws PladipusReportableException
	 */
	public List<Workflow> getAllActiveWorkflowsForUser(User user) throws PladipusReportableException;
	
	/**
	 * Returns data from the workflows table for an active workflow for the given user with specified name.
	 * @param workflowName
	 * @param user
	 * @return workflow data, or null if none found.
	 * @throws PladipusReportableException
	 */
	public Workflow getActiveWorkflowByName(String workflowName, User user) throws PladipusReportableException;
	
	/**
	 * Marks a workflow as inactive so it is no longer seen in the GUI and cannot be run.
	 * @param workflow - Workflow object to be marked as inactive.
	 * @throws PladipusReportableException
	 */
	public void deactivateWorkflow(Workflow workflow) throws PladipusReportableException;
}
