package com.compomics.pladipus.base;

import com.compomics.pladipus.model.queue.LoginUser;
import com.compomics.pladipus.shared.PladipusReportableException;

public interface UserControl {
	public LoginUser getLoginUser(String name, String password) throws PladipusReportableException;
}
