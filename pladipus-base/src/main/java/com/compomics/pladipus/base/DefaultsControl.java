package com.compomics.pladipus.base;

import java.util.List;

import com.compomics.pladipus.model.persist.Default;
import com.compomics.pladipus.model.persist.User;
import com.compomics.pladipus.shared.PladipusReportableException;

public interface DefaultsControl {
	public void addDefault(String name, String value, String type, User user) throws PladipusReportableException;
	public List<Default> getDefaults(User user) throws PladipusReportableException;
	public List<String> getDefaultNamesForUser(User user) throws PladipusReportableException;
}
