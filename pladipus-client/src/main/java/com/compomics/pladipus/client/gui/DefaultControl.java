package com.compomics.pladipus.client.gui;

import com.compomics.pladipus.model.core.DefaultOverview;
import com.compomics.pladipus.shared.PladipusReportableException;

import javafx.collections.ObservableList;

public interface DefaultControl {
	public void addDefault(DefaultOverview defGui) throws PladipusReportableException;
	public ObservableList<DefaultOverview> getUserDefaults();
	public ObservableList<String> getDefaultTypes();
	public void logout();
}
