package com.compomics.pladipus.base.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.compomics.pladipus.base.DefaultsControl;
import com.compomics.pladipus.model.core.Default;
import com.compomics.pladipus.model.exceptions.PladipusReportableException;
import com.compomics.pladipus.repository.service.DefaultService;

public class DefaultsControlImpl implements DefaultsControl {

	@Autowired
	private DefaultService defaultService;
	
	@Override
	public void addDefault(String name, String value, String type, int userId) throws PladipusReportableException {
		Default def = new Default();
		def.setName(name);
		def.setValue(value);
		def.setType(type);
		def.setUserId(userId);
		defaultService.insertDefault(def);
	}

	@Override
	public List<Default> getDefaults(int userId) throws PladipusReportableException {
		return defaultService.getDefaultsForUser(userId);
	}
}
