package com.compomics.pladipus.repository.hibernate.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.pladipus.model.hibernate.User;
import com.compomics.pladipus.repository.hibernate.UserRepository;
import com.compomics.pladipus.shared.PladipusReportableException;

@Repository
@Transactional
public class UserRepositoryImpl extends GenericRepositoryImpl<User> implements UserRepository {

	public UserRepositoryImpl() {
		super(User.class);
	}

	@Override
	public User findUserByName(final String name) throws PladipusReportableException {
		return getSingleResult(getNamedQuery("User.findByName").setParameter("name", name));
	}
	
}
