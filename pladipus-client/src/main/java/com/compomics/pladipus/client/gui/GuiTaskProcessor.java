package com.compomics.pladipus.client.gui;

import com.compomics.pladipus.model.persist.Workflow;
import com.compomics.pladipus.shared.PladipusReportableException;

public interface GuiTaskProcessor {

	void login(String username, String password) throws PladipusReportableException;

	Workflow getWorkflowFromXml(String xml) throws PladipusReportableException;

	Workflow getWorkflowFromFilePath(String path) throws PladipusReportableException;

}
