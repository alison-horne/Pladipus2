package com.compomics.pladipus.client.gui.impl;

import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;

import com.compomics.pladipus.client.BatchCsvIO;
import com.compomics.pladipus.client.gui.DefaultControl;
import com.compomics.pladipus.client.gui.GuiControl;
import com.compomics.pladipus.client.gui.PopupControl;
import com.compomics.pladipus.client.gui.ToolControl;
import com.compomics.pladipus.client.gui.UserControl;
import com.compomics.pladipus.client.gui.UserWorkflowControl;
import com.compomics.pladipus.client.gui.model.DefaultGui;
import com.compomics.pladipus.client.gui.model.WorkflowOverview;
import com.compomics.pladipus.model.core.ToolInformation;
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
	private ToolControl toolControl;
	@Autowired
	private DefaultControl defaultControl;
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
	public String getFileDirPath(Stage stage) {
		File file = popupControl.fileOrDirectoryBrowse(stage);
		if (file != null) {
			try {
				return file.getCanonicalPath();
			} catch (IOException e) {}
		}
		return null;
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
	public void infoAlert(String text, Stage stage) {
		popupControl.showInfo(text, stage);
	}

	@Override
	public void login(String username, String password) throws PladipusReportableException {
		userControl.login(username, password);
		initialize();
	}
	
	@Override
	public void logout() {
		userControl.logout();
	}
	
	@Override
	public String getUserName() {
		return userControl.getLoggedInUsername();
	}
	
	@Override
	public ObservableList<WorkflowOverview> getUserWorkflows() throws PladipusReportableException {
		return userWorkflowControl.getUserWorkflows();
	}
	
	@Override
	public ObservableList<ToolInformation> getToolInfoList() throws PladipusReportableException {
		return toolControl.getToolInfoList();
	}
	
	@Override
	public ToolInformation getToolInfo(String name) {
		return toolControl.getToolInfo(name);
	}

	@Override
	public Workflow getWorkflow(String name) {
		WorkflowOverview wfo= userWorkflowControl.getWorkflowOverview(name);
		if (wfo != null) {
			try {
				return getWorkflowFromXml(wfo.getXml());
			} catch (PladipusReportableException e) {
				return null;
			}
		}
		return null;
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
	public void saveWorkflow(Workflow workflow) throws PladipusReportableException {
		workflow.setTemplateXml(workflowXMLHelper.objectToXML(workflow));
		userWorkflowControl.saveWorkflow(workflow);
	}

	@Override
	public String getText(Stage stage, String header, String original) {
		if (original == null) original = "";
		return popupControl.getText(stage, header, original);
	}

	@Override
	public ObservableList<DefaultGui> getUserDefaults() {
		return defaultControl.getUserDefaults();
	}

	@Override
	public ObservableList<String> getDefaultTypes() {
		return defaultControl.getDefaultTypes();
	}
	
	@Override
	public void addDefault(DefaultGui def) throws PladipusReportableException {
		defaultControl.addDefault(def);
	}
	
	private void initialize() {
		// TODO
	}
}
