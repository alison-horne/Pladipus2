package com.compomics.pladipus.repository.persist;

import java.util.List;

import com.compomics.pladipus.model.persist.User;
import com.compomics.pladipus.model.persist.Workflow;
import com.compomics.pladipus.shared.PladipusReportableException;

public interface WorkflowRepository extends GenericRepository<Workflow> {
	Workflow findUserActiveWorkflowByName(User user, String name) throws PladipusReportableException;
	List<Workflow> findAllActiveWorkflowsForUser(User user) throws PladipusReportableException;
}
