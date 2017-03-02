package com.compomics.pladipus.base.helper;

import com.compomics.pladipus.shared.PladipusReportableException;

/**
 * Helper class to convert XML to object
 *
 * @param <T> Type of object to parse XML document into
 */
public interface XMLHelper<T> {
	
	/**
	 * Parses out XML fields from document text into chosen object type, validating against schema
	 * @param content
	 * @return object
	 * @throws PladipusReportableException
	 */
	T parseXml(String content) throws PladipusReportableException;
}
