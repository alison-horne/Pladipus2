package com.compomics.pladipus.repository.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.pladipus.model.hibernate.Default;
import com.compomics.pladipus.model.hibernate.User;
import com.compomics.pladipus.shared.PladipusMessages;
import com.compomics.pladipus.shared.PladipusReportableException;
import com.compomics.pladipus.repository.hibernate.DefaultRepository;
import com.compomics.pladipus.repository.service.DefaultService;

public class DefaultServiceImpl implements DefaultService {

	@Autowired
	private PladipusMessages exceptionMessages;
	
	@Autowired
	DefaultRepository defaultRepo;
	
	@Transactional(rollbackFor={Exception.class})
	@Override
	public void insertDefault(Default def) throws PladipusReportableException {
		if (isExistingDefault(def.getUser(), def.getName())) {
			throw new PladipusReportableException(exceptionMessages.getMessage("db.defaultExists", def.getName()));
		}
		defaultRepo.persist(def);
	}

	@Transactional(rollbackFor={Exception.class})
	@Override
	public void addType(Default def, String type) throws PladipusReportableException {
		def.setType(type);
		defaultRepo.merge(def);
	}

	@Override
	public List<Default> getDefaultsForUser(User user) throws PladipusReportableException {
		return defaultRepo.getDefaultsForUser(user);
	}
	
	@Override
	public Default getDefaultById(Long id) throws PladipusReportableException {
		return defaultRepo.findById(id);
	}

	private Default getDefault(String name, User user) throws PladipusReportableException {
		return defaultRepo.getNamedDefault(user, name);
	}
	
	private List<Default> getNamedDefaults(String name) throws PladipusReportableException {
		return defaultRepo.getNamedDefaults(name);
	}
	
	private boolean isExistingDefault(User user, String name) throws PladipusReportableException {
		if (user != null) {
			return (getDefault(name, user) != null);
		} else {
			return (!getNamedDefaults(name).isEmpty());
		}
	}
}
