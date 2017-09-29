package com.compomics.pladipus.client.gui;

import com.compomics.pladipus.model.core.ToolInformation;
import com.compomics.pladipus.shared.PladipusReportableException;

import javafx.collections.ObservableList;

public interface ToolControl {
	public ObservableList<ToolInformation> getToolInfoList() throws PladipusReportableException;
	public ToolInformation getToolInfo(String name);
	public void addTool(ToolInformation tool);
}
