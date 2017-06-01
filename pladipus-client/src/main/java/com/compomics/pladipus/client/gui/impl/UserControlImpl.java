package com.compomics.pladipus.client.gui.impl;

import com.compomics.pladipus.client.gui.UserControl;
import com.compomics.pladipus.shared.PladipusReportableException;

public class UserControlImpl implements UserControl {

	// TODO add admin options
	private String username = null;
	
	@Override
	public boolean isLoggedIn() {
		return (username != null);
	}

	@Override
	public String getLoggedInUsername() {
		return username;
	}

	@Override
	public void login(String username, String password) throws PladipusReportableException {
		// TODO Auto-generated method stub
		if (username.equals("test1")) {
		this.username = username;
		} else { throw new PladipusReportableException("Bad login");}
	}
	
	@Override
	public void logout() {
		this.username = null;
	}
	
}
