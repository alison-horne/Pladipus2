package com.compomics.pladipus.client.gui.impl;

import com.compomics.pladipus.client.gui.DefaultControl;
import com.compomics.pladipus.model.core.DefaultOverview;
import com.compomics.pladipus.shared.PladipusReportableException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DefaultControlImpl implements DefaultControl {
	private ObservableList<DefaultOverview> userDefaults = FXCollections.observableArrayList();
	private ObservableList<String> defaultTypes = FXCollections.observableArrayList();
	
	@Override
	public void addDefault(DefaultOverview defGui) throws PladipusReportableException {
		userDefaults.add(defGui);
		String type = defGui.getType();
		if (type != null && !type.isEmpty() && !defaultTypes.contains(type)) {
			defaultTypes.add(type);
		}
	}
	
	@Override
	public void clear() {
		userDefaults.clear();
		defaultTypes.clear();
	}

	@Override
	public ObservableList<DefaultOverview> getUserDefaults() {
		return userDefaults;
	}

	@Override
	public ObservableList<String> getDefaultTypes() {
		return defaultTypes;
	}
}
