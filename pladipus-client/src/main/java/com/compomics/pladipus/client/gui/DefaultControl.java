package com.compomics.pladipus.client.gui;

import com.compomics.pladipus.client.gui.model.DefaultGui;
import com.compomics.pladipus.shared.PladipusReportableException;

import javafx.collections.ObservableList;

public interface DefaultControl {
	public void addDefault(DefaultGui defGui) throws PladipusReportableException;
	public ObservableList<DefaultGui> getUserDefaults();
	public ObservableList<String> getDefaultTypes();
	public void logout();
}
