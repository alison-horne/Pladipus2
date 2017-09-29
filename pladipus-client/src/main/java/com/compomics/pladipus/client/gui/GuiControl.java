package com.compomics.pladipus.client.gui;

import java.io.File;

import com.compomics.pladipus.model.core.DefaultOverview;
import com.compomics.pladipus.model.core.WorkflowOverview;
import com.compomics.pladipus.model.core.ToolInformation;
import com.compomics.pladipus.model.persist.Workflow;
import com.compomics.pladipus.shared.PladipusReportableException;

import javafx.collections.ObservableList;
import javafx.stage.Stage;

public interface GuiControl {
	public void showError(String errorMsg, Stage stage);
	public boolean showAlert(String text, Stage stage);
	public String customAlert(String text, Stage stage, String[] buttons);
	public void infoAlert(String text, Stage stage);
	public void login(String username, String password) throws PladipusReportableException;
	public void logout();
	public String getUserName();
	public ObservableList<WorkflowOverview> getUserWorkflows() throws PladipusReportableException;
	public ObservableList<ToolInformation> getToolInfoList() throws PladipusReportableException;
	public ToolInformation getToolInfo(String name);
	public ObservableList<DefaultOverview> getUserDefaults();
	public ObservableList<String> getDefaultTypes();
	public void addDefault(DefaultOverview def) throws PladipusReportableException;
	public Workflow getWorkflow(String name);
	public Workflow getWorkflowFromFilePath(String path) throws PladipusReportableException;
	public Workflow getWorkflowFromXml(String xml) throws PladipusReportableException;
	public void saveWorkflow(Workflow workflow) throws PladipusReportableException;
	public File getCsvFile(Stage stage);
	public File getXmlFile(Stage stage);
	public String getFileDirPath(Stage stage);
	public String getText(Stage stage, String header, String original);
}
