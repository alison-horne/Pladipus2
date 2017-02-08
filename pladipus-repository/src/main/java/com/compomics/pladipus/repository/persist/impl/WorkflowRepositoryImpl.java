package com.compomics.pladipus.repository.persist.impl;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.pladipus.model.persist.User;
import com.compomics.pladipus.model.persist.Workflow;
import com.compomics.pladipus.repository.persist.WorkflowRepository;
import com.compomics.pladipus.shared.PladipusReportableException;

@Repository
@Transactional
public class WorkflowRepositoryImpl extends GenericRepositoryImpl<Workflow> implements WorkflowRepository {

	public WorkflowRepositoryImpl() {
		super(Workflow.class);
	}

	@Override
	public Workflow findUserActiveWorkflowByName(User user, String name) throws PladipusReportableException {
		return getSingleResult(getNamedQuery("Workflow.namedActiveForUser").setParameter("name", name).setParameter("user", user));
	}

	@Override
	public List<Workflow> findAllActiveWorkflowsForUser(User user) throws PladipusReportableException {
		return getResultsList(getNamedQuery("Workflow.activeForUser").setParameter("user", user));
	}
	
}
