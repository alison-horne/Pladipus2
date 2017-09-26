package com.compomics.pladipus.client.gui.impl;

import com.compomics.pladipus.client.gui.DefaultControl;
import com.compomics.pladipus.client.gui.TestData;
import com.compomics.pladipus.client.gui.model.DefaultGui;
import com.compomics.pladipus.shared.PladipusReportableException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DefaultControlImpl implements DefaultControl {
	private ObservableList<DefaultGui> userDefaults = FXCollections.observableArrayList();
	private ObservableList<String> defaultTypes = FXCollections.observableArrayList();
	private boolean loaded = false;
	
	@Override
	public void addDefault(DefaultGui defGui) throws PladipusReportableException {
		// TODO Save to control...
		addDefaultToLists(defGui);
	}

	@Override
	public ObservableList<DefaultGui> getUserDefaults() {
		if (!loaded) {
			loadDefaults();
		}
		return userDefaults;
	}

	@Override
	public ObservableList<String> getDefaultTypes() {
		if (!loaded) {
			loadDefaults();
		}
		return defaultTypes;
	}

	@Override
	public void logout() {
		userDefaults.clear();
		loaded = false;
	}
	
	private void loadDefaults() {
		// TODO get from control
		for (DefaultGui def: TestData.getDefaults("test1")) {
			addDefaultToLists(def);
		}
		loaded = true;
	}
	
	private void addDefaultToLists(DefaultGui def) {
		userDefaults.add(def);
		String type = def.getType();
		if (type != null && !type.isEmpty() && !defaultTypes.contains(type)) {
			defaultTypes.add(type);
		}
	}

}
