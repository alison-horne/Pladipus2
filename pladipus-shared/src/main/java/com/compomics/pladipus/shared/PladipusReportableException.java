package com.compomics.pladipus.shared;

/**
 * Exceptions which should be reported back to the user.
 * Provide a meaningful error message, so the problem may be resolved.
 */
public class PladipusReportableException extends Exception {

	private static final long serialVersionUID = 8275194575269353052L;

	public PladipusReportableException(String message) { super(message); }
}
