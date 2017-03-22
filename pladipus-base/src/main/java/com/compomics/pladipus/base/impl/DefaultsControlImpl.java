package com.compomics.pladipus.base.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import com.compomics.pladipus.base.DefaultsControl;
import com.compomics.pladipus.model.persist.Default;
import com.compomics.pladipus.model.persist.User;
import com.compomics.pladipus.shared.PladipusReportableException;
import com.compomics.pladipus.repository.service.DefaultService;

public class DefaultsControlImpl implements DefaultsControl {

	@Autowired
	@Lazy
	private DefaultService defaultService;
	
	@Override
	public void addDefault(String name, String value, String type, User user) throws PladipusReportableException {
		Default def = new Default();
		def.setName(name);
		def.setValue(value);
		def.setType(type);
		def.setUser(user);
		defaultService.insertDefault(def);
	}

	@Override
	public List<Default> getDefaults(User user) throws PladipusReportableException {
		return defaultService.getDefaultsForUser(user);
	}
	
	@Override
	public List<String> getDefaultNamesForUser(User user) throws PladipusReportableException {
		List<Default> defaults = getDefaults(user);
		List<String> names = new ArrayList<String>();
		for (Default def: defaults) {
			names.add(def.getName().toUpperCase());
		}
		return names;
	}
}
