package com.compomics.pladipus.client.gui.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.compomics.pladipus.client.gui.GuiControl;
import com.compomics.pladipus.client.gui.PopupControl;
import com.compomics.pladipus.client.gui.UserControl;
import com.compomics.pladipus.client.gui.UserWorkflowControl;
import com.compomics.pladipus.client.gui.model.WorkflowGui;
import com.compomics.pladipus.shared.PladipusReportableException;

import javafx.collections.ObservableList;
import javafx.stage.Stage;

public class GuiControlImpl implements GuiControl {
	@Autowired
	private PopupControl popupControl;
	@Autowired
	private UserControl userControl;
	@Autowired
	private UserWorkflowControl userWorkflowControl;
	
	@Override
	public void showError(String errorMsg, Stage stage) {
		popupControl.doError(errorMsg, stage);
	}
	
	@Override
	public boolean showAlert(String text, Stage stage) {
		return popupControl.doAlertYN(text, stage);
	}

	@Override
	public void login(String username, String password) throws PladipusReportableException {
		userControl.login(username, password);
	}
	
	@Override
	public void logout() {
		// TODO Stop any current messages.  send logout message?  Clear other lists...
		userControl.logout();
		userWorkflowControl.logout();
	}
	
	@Override
	public String getUserName() {
		return userControl.getLoggedInUsername();
	}
	
	@Override
	public ObservableList<WorkflowGui> getUserWorkflows() throws PladipusReportableException {
		return userWorkflowControl.getUserWorkflows();
	}
}
