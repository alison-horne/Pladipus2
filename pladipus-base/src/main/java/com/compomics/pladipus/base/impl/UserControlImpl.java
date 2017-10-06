package com.compomics.pladipus.base.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import com.compomics.pladipus.base.UserControl;
import com.compomics.pladipus.model.persist.User;
import com.compomics.pladipus.model.queue.LoginUser;
import com.compomics.pladipus.repository.service.UserService;
import com.compomics.pladipus.shared.PladipusReportableException;

public class UserControlImpl implements UserControl {

	@Autowired
	@Lazy
	private UserService userService;
	
	@Override
	public LoginUser getLoginUser(String name, String password) throws PladipusReportableException {
		return new LoginUser(userService.login(name, password));
	}
	
	@Override
	public void createUser(String name, String email, String password) throws PladipusReportableException {
		User user = new User();
		user.setActive(true);
		user.setAdmin(false);
		user.setEmail(email);
		user.setUserName(name);
		userService.createUser(user, password);
	}
}
