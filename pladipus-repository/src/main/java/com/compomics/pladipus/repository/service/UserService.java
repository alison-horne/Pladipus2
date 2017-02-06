package com.compomics.pladipus.repository.service;

import java.util.List;

import com.compomics.pladipus.model.hibernate.User;
import com.compomics.pladipus.shared.PladipusReportableException;

/**
 * Service to be used by the front end for dealing with users.  
 */
public interface UserService {
	
	/**
	 * Create a new user
	 * 
	 * @param user, a User object with all fields set as necessary except for ID and password
	 * @param password - unencrypted, will be encrypted before committing to database
	 * @throws PladipusReportableException if user is invalid, or there is an error inserting data in database
	 */
	public void createUser(User user, String password) throws PladipusReportableException;
		
	/**
	 * Look up database for user with given username
	 * @param username
	 * @return User object, or null if not found
	 * @throws PladipusReportableException
	 */
	public User getUserByName(String username) throws PladipusReportableException;
	
	/**
	 * Get all users in the database
	 * @return List of all users 
	 * @throws PladipusReportableException
	 */
	public List<User> getAllUsers() throws PladipusReportableException;
	
	/**
	 * Login - gets user from database for the given username, and checks the password
	 * @param username
	 * @param password
	 * @return User object
	 * @throws PladipusReportableException if username/password wrong, or if there is a problem with database access
	 */
	public User login(String username, String password) throws PladipusReportableException;
	
	/**
	 * Set user to be active/inactive (allow access to use tool, or not)
	 * @param user object
	 * @param active - boolean.  True to set user as active, false for inactive
	 * @throws PladipusReportableException
	 */
	public void setActive(User user, boolean active) throws PladipusReportableException;
		
	/**
	 * Set user to be an administrator, or regular user
	 * @param user object
	 * @param admin - boolean.  True to set user as administrator, false for regular user
	 * @throws PladipusReportableException
	 */
	public void setAdmin(User user, boolean admin) throws PladipusReportableException;
	
	/**
	 * Change user's password.  Encrypts new password and inserts in database
	 * @param user object
	 * @param newPassword, unencrypted
	 * @throws PladipusReportableException
	 */
	public void changePassword(User user, String newPassword) throws PladipusReportableException;
}
