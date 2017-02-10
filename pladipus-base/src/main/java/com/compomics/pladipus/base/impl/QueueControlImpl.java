package com.compomics.pladipus.base.impl;

import com.compomics.pladipus.base.QueueControl;
import com.compomics.pladipus.model.core.TaskStatus;
import com.compomics.pladipus.model.persist.User;
import com.compomics.pladipus.shared.PladipusReportableException;

public class QueueControlImpl implements QueueControl {

	@Override
	public void process(String batchName, User user) throws PladipusReportableException {
		// TODO
		// if batchName null/empty, start all jobs.  If job already done/partially started, make sure to warn about overwriting output.
	}
	
	@Override
	public void restart(String batchName, User user) throws PladipusReportableException {
		//TODO if batchName null/empty, throw exception
	}

	@Override
	public TaskStatus status(String batchName, User user) throws PladipusReportableException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void abort(String batchName, User user) throws PladipusReportableException {
		// TODO Auto-generated method stub
		// If batchName null/empty, abort all
	}

}
