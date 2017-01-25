package com.compomics.pladipus.base.helper;

import org.w3c.dom.Document;

import com.compomics.pladipus.shared.PladipusReportableException;

/**
 * Helper class to convert XML to object
 *
 * @param <T> Type of object to parse XML document into
 */
public interface XMLHelper<T> {

	/**
	 * Validates XML Document against XSD schema.  Throws exception if validation fails.
	 * 
	 * @param document - Document to be validated
	 * @throws PladipusReportableException if validation fails
	 */
	void validateDocument(Document document) throws PladipusReportableException;
	
	/**
	 * Parses out XML field from document into chosen object type
	 * @param document
	 * @return object
	 * @throws PladipusReportableException
	 */
	T parseDocument(Document document) throws PladipusReportableException;
	
	/**
	 * Helper method to convert a file to an XML Document
	 * @param filePath
	 * @return Document
	 * @throws PladipusReportableException if file not found, cannot be read, or if there is a problem with the conversion
	 */
	Document fileToDocument(String filePath) throws PladipusReportableException;
	
	/**
	 * Helper method to convert a string to an XML Document
	 * @param string
	 * @return Document
	 * @throws PladipusReportableException
	 */
	Document stringToDocument(String string) throws PladipusReportableException;
	
	/**
	 * Helper method to convert an XML document to a string
	 * @param document
	 * @return String
	 * @throws PladipusReportableException
	 */
	String documentToString(Document document) throws PladipusReportableException;
}
