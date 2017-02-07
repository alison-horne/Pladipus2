package com.compomics.pladipus.repository.hibernate.impl;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.pladipus.model.hibernate.Default;
import com.compomics.pladipus.model.hibernate.User;
import com.compomics.pladipus.repository.hibernate.DefaultRepository;
import com.compomics.pladipus.shared.PladipusReportableException;

@Repository
@Transactional
public class DefaultRepositoryImpl extends GenericRepositoryImpl<Default> implements DefaultRepository {

	public DefaultRepositoryImpl() {
		super(Default.class);
	}

	@Override
	public List<Default> getDefaultsForUser(User user) throws PladipusReportableException {
		return getResultsList(getNamedQuery("Default.listByUser").setParameter("user", user));
	}
	
	@Override
	public Default getNamedDefault(User user, String name) throws PladipusReportableException {
		return getSingleResult(getNamedQuery("Default.namedForUser").setParameter("user", user).setParameter("name", name));
	}
	
	@Override
	public List<Default> getNamedDefaults(String name) throws PladipusReportableException {
		return getResultsList(getNamedQuery("Default.namedAnyUser").setParameter("name", name));
	}
}
