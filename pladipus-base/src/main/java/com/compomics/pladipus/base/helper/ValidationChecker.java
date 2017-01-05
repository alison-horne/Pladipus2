package com.compomics.pladipus.base.helper;

import com.compomics.pladipus.model.exceptions.PladipusReportableException;

public interface ValidationChecker<T> {
	void validate(T t) throws PladipusReportableException;
}
