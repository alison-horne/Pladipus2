package com.compomics.pladipus.repository.persist;

import com.compomics.pladipus.model.persist.User;
import com.compomics.pladipus.shared.PladipusReportableException;

public interface UserRepository extends GenericRepository<User> {
	User findUserByName(final String name) throws PladipusReportableException;
}
