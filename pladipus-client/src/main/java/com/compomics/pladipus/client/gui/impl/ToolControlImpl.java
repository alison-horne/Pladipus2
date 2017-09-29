package com.compomics.pladipus.client.gui.impl;

import com.compomics.pladipus.client.gui.ToolControl;
import com.compomics.pladipus.model.core.ToolInformation;
import com.compomics.pladipus.shared.PladipusReportableException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ToolControlImpl implements ToolControl {
	
	private ObservableList<ToolInformation> toolInfoList = FXCollections.observableArrayList();

	@Override
	public ObservableList<ToolInformation> getToolInfoList() throws PladipusReportableException {
		return toolInfoList;
	}
	
	@Override
	public ToolInformation getToolInfo(String name) {
		try {
			for (ToolInformation tool: getToolInfoList()) {
				if (tool.getToolName().equals(name)) return tool;
			}
		} catch (PladipusReportableException e) {
			// TODO what if no tools?
		}
		return null;
	}
	
	@Override
	public void addTool(ToolInformation tool) {
		toolInfoList.add(tool);
	}
}
