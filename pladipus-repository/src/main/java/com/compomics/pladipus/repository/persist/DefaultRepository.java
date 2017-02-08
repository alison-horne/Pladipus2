package com.compomics.pladipus.repository.persist;

import java.util.List;

import com.compomics.pladipus.model.persist.Default;
import com.compomics.pladipus.model.persist.User;
import com.compomics.pladipus.shared.PladipusReportableException;

public interface DefaultRepository extends GenericRepository<Default> {
	List<Default> getDefaultsForUser(User user) throws PladipusReportableException;
	Default getNamedDefault(User user, String name) throws PladipusReportableException;
	List<Default> getNamedDefaults(String name) throws PladipusReportableException; 
}
