package com.compomics.pladipus.base.helper;

import java.util.List;

import com.compomics.pladipus.shared.PladipusReportableException;

public interface CsvParser<T, V> {
	T parseCSV(String csvString, V v) throws PladipusReportableException;
	List<String> getHeaders(V v);
}
