package com.compomics.pladipus.base;

import com.compomics.pladipus.model.exceptions.PladipusReportableException;

public interface DefaultsControl {
	public void addDefault(String name, String value, String type, int userId) throws PladipusReportableException;
}
