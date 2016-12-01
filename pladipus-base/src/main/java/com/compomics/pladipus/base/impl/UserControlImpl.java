package com.compomics.pladipus.base.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.compomics.pladipus.base.UserControl;
import com.compomics.pladipus.model.core.User;
import com.compomics.pladipus.model.exceptions.PladipusMessages;
import com.compomics.pladipus.model.exceptions.PladipusReportableException;
import com.compomics.pladipus.repository.service.UserService;

public class UserControlImpl implements UserControl {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PladipusMessages exceptionMessages;
	
	private User loggedInUser;

	@Override
	public void login(String username, String password) throws PladipusReportableException {
		setLoggedInUser(userService.login(username, password));
	}
	
	@Override
	public void logout() {
		setLoggedInUser(null);
	}

	@Override
	public int getUserId() throws PladipusReportableException {
		return getLoggedInUser().getId();
	}

	private User getLoggedInUser() throws PladipusReportableException {
		if (loggedInUser == null) {
			throw new PladipusReportableException(exceptionMessages.getMessage("base.noLogin"));
		}
		return loggedInUser;
	}

	private void setLoggedInUser(User loggedInUser) {
		this.loggedInUser = loggedInUser;
	}
}
