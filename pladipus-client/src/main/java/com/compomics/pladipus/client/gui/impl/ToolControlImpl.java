package com.compomics.pladipus.client.gui.impl;

import com.compomics.pladipus.client.gui.TestData;
import com.compomics.pladipus.client.gui.ToolControl;
import com.compomics.pladipus.model.core.ToolInformation;
import com.compomics.pladipus.shared.PladipusReportableException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ToolControlImpl implements ToolControl {
	
	private ObservableList<ToolInformation> toolInfoList = FXCollections.observableArrayList();
	private boolean loaded = false;

	@Override
	public ObservableList<ToolInformation> getToolInfoList() throws PladipusReportableException {
		if (!loaded) {
			loadToolList();
		}
		return toolInfoList;
	}

	private void loadToolList() throws PladipusReportableException {
		// TODO get tools from controller
		toolInfoList.addAll(TestData.getTools());
		loaded = true;
	}
	
	@Override
	public ToolInformation getToolInfo(String name) {
		for (ToolInformation tool: toolInfoList) {
			if (tool.getToolName().equals(name)) return tool;
		}
		return null;
	}
}
