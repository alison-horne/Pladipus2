package com.compomics.pladipus.repository.hibernate;

import com.compomics.pladipus.model.hibernate.User;
import com.compomics.pladipus.shared.PladipusReportableException;

public interface UserRepository extends GenericRepository<User> {
	User findUserByName(final String name) throws PladipusReportableException;
}
