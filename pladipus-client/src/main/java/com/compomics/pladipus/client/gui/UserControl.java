package com.compomics.pladipus.client.gui;

import com.compomics.pladipus.shared.PladipusReportableException;

public interface UserControl {
	public boolean isLoggedIn();
	public String getLoggedInUsername();
	public void login(String username, String password) throws PladipusReportableException;
	public void logout();
}
