package com.compomics.pladipus.base.helper;

import com.compomics.pladipus.shared.PladipusReportableException;

public interface ValidationChecker<T> {
	public void validate(T t) throws PladipusReportableException;
}
