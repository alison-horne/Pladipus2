package com.compomics.pladipus.client.gui;

import com.compomics.pladipus.client.gui.model.WorkflowGui;
import com.compomics.pladipus.shared.PladipusReportableException;

import javafx.collections.ObservableList;
import javafx.stage.Stage;

public interface GuiControl {
	public void showError(String errorMsg, Stage stage);
	public boolean showAlert(String text, Stage stage);
	public void login(String username, String password) throws PladipusReportableException;
	public void logout();
	public String getUserName();
	public ObservableList<WorkflowGui> getUserWorkflows() throws PladipusReportableException;
}
