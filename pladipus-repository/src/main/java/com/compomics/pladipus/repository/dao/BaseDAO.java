package com.compomics.pladipus.repository.dao;

import java.util.List;

import com.compomics.pladipus.model.core.UpdateTracked;
import com.compomics.pladipus.model.exceptions.PladipusReportableException;

/**
 * Database Access methods
 * Up to the calling service to add @Transactional notation to implement rollback in case of error.
 * @param <T> the type of model object, e.g. User
 */
public interface BaseDAO<T extends UpdateTracked> {
	/**
	 * Insert object in database
	 * @param Object to be inserted
	 * @return integer ID generated if auto-incremented ID field exists for the object type; or 0 if not
	 * @throws PladipusReportableException
	 */
	int insert(T t) throws PladipusReportableException;
	
	/**
	 * Get a single object from the database.  Returns null if not found.
	 * @param query - should populate the where clause, and named parameter map
	 * @return single object, or null if none found
	 * @throws PladipusReportableException
	 */
	T get(Query query) throws PladipusReportableException;
	
	/**
	 * Get multiple objects from the database.
	 * @param query - populate the where clause and parameter map, or leave as blank
	 * 				  query to return all objects in the database
	 * @return list of returned objects
	 * @throws PladipusReportableException
	 */
	List<T> getList(Query query) throws PladipusReportableException;
	
	/**
	 * Update an object in the database.  Will commit fields changed under tracked changes list.
	 * @param object to update
	 * @throws PladipusReportableException
	 */
	void update(T t) throws PladipusReportableException;
	int update(Query query) throws PladipusReportableException;
	int insert(Query query) throws PladipusReportableException;
	int batchInsert(T t) throws PladipusReportableException;
}
