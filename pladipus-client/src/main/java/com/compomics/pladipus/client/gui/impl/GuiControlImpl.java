package com.compomics.pladipus.client.gui.impl;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;

import com.compomics.pladipus.client.BatchCsvIO;
import com.compomics.pladipus.client.gui.DefaultControl;
import com.compomics.pladipus.client.gui.GuiControl;
import com.compomics.pladipus.client.gui.PopupControl;
import com.compomics.pladipus.client.gui.ToolControl;
import com.compomics.pladipus.client.gui.UserControl;
import com.compomics.pladipus.client.gui.UserWorkflowControl;
import com.compomics.pladipus.client.queue.MessageSender;
import com.compomics.pladipus.model.core.DefaultOverview;
import com.compomics.pladipus.model.core.GuiSetup;
import com.compomics.pladipus.model.core.WorkflowOverview;
import com.compomics.pladipus.model.core.ToolInformation;
import com.compomics.pladipus.model.persist.Workflow;
import com.compomics.pladipus.model.queue.messages.client.ClientTask;
import com.compomics.pladipus.model.queue.messages.client.ClientToControlMessage;
import com.compomics.pladipus.model.queue.messages.client.ControlToClientMessage;
import com.compomics.pladipus.shared.PladipusReportableException;
import com.compomics.pladipus.shared.XMLHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

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
	@Autowired
	private MessageSender messageSender;
	@Autowired
	private ObjectMapper jsonMapper;
	
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
	public String saveCsvFile(Stage stage, String name, List<String> headers) throws PladipusReportableException {
		File file = popupControl.fileSaveBrowse(stage, name, getCsvFilters());
		if (file != null) {
			batchCsvIO.writeHeaderFile(file.getAbsolutePath(), headers);
			if (Desktop.isDesktopSupported()) {
				try {
					Desktop.getDesktop().open(file);
				} catch (IOException e) {
					// Ignore
				}
			}
			return file.getAbsolutePath();
		}
		return null;
	}
	
	@Override
	public String saveWorkflowXml(Stage stage, String name, Workflow workflow) throws PladipusReportableException {
		File file = popupControl.fileSaveBrowse(stage, name, getXmlFilters());
		if (file != null) {
			batchCsvIO.stringToFile(file.getAbsolutePath(), workflowXMLHelper.objectToXML(workflow));
			return file.getAbsolutePath();
		}
		return null;
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
	public void infoAlert(String text, Stage stage, boolean wait) {
		popupControl.showInfo(text, stage, wait);
	}

	@Override
	public void login(String username, String password) throws PladipusReportableException {
		userControl.login(username, password);
		initialize();
	}
	
	@Override
	public void logout() {
		userControl.logout();
		defaultControl.clear();
		toolControl.clear();
		userWorkflowControl.clear();
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
	public WorkflowOverview getWorkflowOverview(String name) {
		return userWorkflowControl.getWorkflowOverview(name);
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
	public WorkflowOverview saveWorkflow(Workflow workflow) throws PladipusReportableException {
		workflow.setTemplateXml(workflowXMLHelper.objectToXML(workflow));
		ClientToControlMessage msg = new ClientToControlMessage(ClientTask.REPLACE_WORKFLOW);
		msg.setFileContent(workflow.getTemplateXml());
		String headerList = sendMessage(msg, 3);
		ObjectReader reader = jsonMapper.readerFor(List.class);
		try {
			return userWorkflowControl.saveWorkflow(workflow, reader.readValue(headerList));
		} catch (IOException e) {
			throw new PladipusReportableException(resources.getString("popup.noLogin") + "\n" + e.getMessage());
		}
	}

	@Override
	public String getText(Stage stage, String header, String original) {
		if (original == null) original = "";
		return popupControl.getText(stage, header, original);
	}

	@Override
	public ObservableList<DefaultOverview> getUserDefaults() {
		return defaultControl.getUserDefaults();
	}

	@Override
	public ObservableList<String> getDefaultTypes() {
		return defaultControl.getDefaultTypes();
	}
	
	@Override
	public void addDefault(DefaultOverview def) throws PladipusReportableException {
		ClientToControlMessage msg = new ClientToControlMessage(ClientTask.ADD_DEFAULT);
		msg.setDefaultName(def.getName());
		msg.setDefaultValue(def.getValue());
		msg.setDefaultType(def.getType());
		msg.setDefaultGlobal(def.getGlobal());
		sendMessage(msg, 3);
		defaultControl.addDefault(def);
	}
	
	@Override
	public void loadBatchFromFile(WorkflowOverview wo, String batchName, String filename, boolean startRun) throws PladipusReportableException {
		if (batchName == null || batchName.isEmpty()) batchName = batchCsvIO.getFileName(filename);
		loadBatchData(wo, batchName, batchCsvIO.fileToString(filename), startRun);
	}
	
	@Override
	public void loadBatchData(WorkflowOverview wo, String batchName, String content, boolean startRun) throws PladipusReportableException {
		ClientToControlMessage msg = new ClientToControlMessage(ClientTask.REPLACE_BATCH);
		msg.setBatchName(batchName);
		msg.setBatchRun(startRun);
		msg.setWorkflowName(wo.getName());
		msg.setFileContent(content);
		sendMessage(msg, 3); // TODO get batch info returned, update wo
	}

	private void initialize() throws PladipusReportableException {
		ObjectReader reader = jsonMapper.readerFor(GuiSetup.class);
		try {
			GuiSetup setup = reader.readValue(sendMessage(new ClientToControlMessage(ClientTask.GUI_SETUP), 3));
			for (DefaultOverview def: setup.getUserDefaults()) {
				defaultControl.addDefault(def);
			}
			for (ToolInformation tool: setup.getTools()) {
				toolControl.addTool(tool);
			}
			for (WorkflowOverview workflow: setup.getWorkflows()) {
				userWorkflowControl.addWorkflow(workflow);
			}
		} catch (IOException e) {
			throw new PladipusReportableException(resources.getString("popup.noLogin") + "\n" + e.getMessage());
		}
	}
	
	private String sendMessage(ClientToControlMessage msg, int retries) throws PladipusReportableException {
		msg.setUsername(getUserName());
		while (retries > 0) {
			ControlToClientMessage response = messageSender.makeRequest(msg);
			switch (response.getStatus()) {
				case ERROR:
					throw new PladipusReportableException(response.getErrorMsg());
				case OK:
					return response.getContent();
				case TIMEOUT:
					retries--;
				case NO_LOGIN:
					throw new PladipusReportableException(resources.getString("popup.noLogin"));
				default:
					break;
			}
		}
		throw new PladipusReportableException(resources.getString("popup.requestTimeout"));
	}
}
