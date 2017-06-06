package com.compomics.pladipus.client.gui.impl;

import java.io.File;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;

import com.compomics.pladipus.client.BatchCsvIO;
import com.compomics.pladipus.client.gui.GuiControl;
import com.compomics.pladipus.client.gui.PopupControl;
import com.compomics.pladipus.client.gui.UserControl;
import com.compomics.pladipus.client.gui.UserWorkflowControl;
import com.compomics.pladipus.client.gui.model.WorkflowGui;
import com.compomics.pladipus.model.persist.Workflow;
import com.compomics.pladipus.shared.PladipusReportableException;
import com.compomics.pladipus.shared.XMLHelper;

import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

public class GuiControlImpl implements GuiControl {
	@Autowired
	private PopupControl popupControl;
	@Autowired
	private UserControl userControl;
	@Autowired
	private UserWorkflowControl userWorkflowControl;
	@Autowired
	private BatchCsvIO batchCsvIO;	
	@Autowired
	private XMLHelper<Workflow> workflowXMLHelper;
	
	private ResourceBundle resources = ResourceBundle.getBundle("guiTexts/popup");
	
	private ExtensionFilter[] getXmlFilters() {
		ExtensionFilter xmlExt = new ExtensionFilter(resources.getString("popup.xmlFiles"), "*.xml");
		ExtensionFilter allExt = new ExtensionFilter(resources.getString("popup.allFiles"), "*");
		return new ExtensionFilter[]{xmlExt, allExt};
	}
	
	private ExtensionFilter[] getCsvFilters() {
		ExtensionFilter csvExt = new ExtensionFilter(resources.getString("popup.csvFiles"), "*.csv");
		ExtensionFilter allExt = new ExtensionFilter(resources.getString("popup.allFiles"), "*");
		return new ExtensionFilter[]{csvExt, allExt};
	}
	
	@Override
	public File getCsvFile(Stage stage) {
		return popupControl.fileBrowse(stage, getCsvFilters());
	}
	
	@Override
	public File getXmlFile(Stage stage) {
		return popupControl.fileBrowse(stage, getXmlFilters());
	}
	
	@Override
	public void showError(String errorMsg, Stage stage) {
		popupControl.doError(errorMsg, stage);
	}
	
	@Override
	public boolean showAlert(String text, Stage stage) {
		return popupControl.doAlertYN(text, stage);
	}
	
	@Override
	public String customAlert(String text, Stage stage, String[] buttons) {
		return popupControl.doAlertCustom(text, stage, buttons);
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

	@Override
	public WorkflowGui getWorkflowGui(String name) {
		return userWorkflowControl.getWorkflowGui(name);
	}
	
	@Override
	public Workflow getWorkflowFromFilePath(String path) throws PladipusReportableException {
		return workflowXMLHelper.parseXml(batchCsvIO.fileToString(path));
	}
	
	@Override
	public Workflow getWorkflowFromXml(String xml) throws PladipusReportableException {
		return workflowXMLHelper.parseXml(xml);
	}

	@Override
	public String getText(Stage stage, String header, String original) {
		if (original == null) original = "";
		return popupControl.getText(stage, header, original);
	}
}
