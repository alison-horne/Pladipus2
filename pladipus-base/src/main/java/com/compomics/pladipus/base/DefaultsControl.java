package com.compomics.pladipus.base;

import java.util.List;
import java.util.Map;

import com.compomics.pladipus.model.core.Default;
import com.compomics.pladipus.model.exceptions.PladipusReportableException;

public interface DefaultsControl {
	public void addDefault(String name, String value, String type, int userId) throws PladipusReportableException;
	public List<Default> getDefaults(int userId) throws PladipusReportableException;
	public Map<String, Integer> getDefaultMap(int userId) throws PladipusReportableException;
}
