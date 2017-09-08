package com.compomics.pladipus.shared;

import com.compomics.pladipus.shared.PladipusReportableException;

/**
 * Helper class to convert XML to object
 *
 * @param <T> Type of object to parse XML document into
 */
/**
 * @author alhor
 *
 * @param <T>
 */
public interface XMLHelper<T> {
	
	/**
	 * Parses out XML fields from document text into chosen object type, validating against schema
	 * @param content
	 * @return object
	 * @throws PladipusReportableException
	 */
	T parseXml(String content) throws PladipusReportableException;
	
	/**
	 * Converts Object to XML string
	 * @param Object
	 * @return XML string
	 * @throws PladipusReportableException
	 */
	String objectToXML(T t) throws PladipusReportableException;
}
