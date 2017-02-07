package com.compomics.pladipus.repository.hibernate;

import java.util.List;

import com.compomics.pladipus.shared.PladipusReportableException;

public interface GenericRepository<T> {

	void persist(T t) throws PladipusReportableException;
	void merge(T t) throws PladipusReportableException;
	List<T> findAll() throws PladipusReportableException;
	T findById(final Long id) throws PladipusReportableException;
}
