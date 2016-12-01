package com.compomics.pladipus.base;

import com.compomics.pladipus.model.exceptions.PladipusReportableException;

/**
 * Logs in user, and holds user information.
 */
public interface UserControl {
	
	/**
	 * Log in user in order to access Pladipus services.
	 * @param username
	 * @param password
	 * @throws PladipusReportableException if user not found or inactive, password wrong, or database access problems.
	 */
	public void login(String username, String password) throws PladipusReportableException;
	public void logout();
	public int getUserId() throws PladipusReportableException;
}
