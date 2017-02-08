package com.compomics.pladipus.repository.service;

import java.util.List;

import com.compomics.pladipus.model.persist.Default;
import com.compomics.pladipus.model.persist.User;
import com.compomics.pladipus.shared.PladipusReportableException;

/**
 * Service to be used by the front end for dealing with defaults in the database.  
 */
public interface DefaultService {
	
	/**
	 * Insert a new default into the database
	 * 
	 * @param def - Default object to insert into the database
	 * @throws PladipusReportableException if default already exists, or if database insert fails.
	 */
	public void insertDefault(Default def) throws PladipusReportableException;
	
	/**
	 * Add or change a type on an existing default
	 * 
	 * @param def - Default object to update
	 * @param type - String type to add
	 * @throws PladipusReportableException
	 */
	public void addType(Default def, String type) throws PladipusReportableException;
	
	/**
	 * Get defaults for given user ID, as well as general defaults with no user ID set
	 * 
	 * @param user (null to get general defaults only)
	 * @return List of Default objects
	 * @throws PladipusReportableException 
	 */
	public List<Default> getDefaultsForUser(User user) throws PladipusReportableException;

	/**
	 * Find default by ID.
	 * 
	 * @param id
	 * @return Default object
	 * @throws PladipusReportableException 
	 */
	public Default getDefaultById(Long id) throws PladipusReportableException;
}
