package com.compomics.pladipus.client.gui;

import java.io.File;

import com.compomics.pladipus.client.gui.model.WorkflowGui;
import com.compomics.pladipus.model.persist.Workflow;
import com.compomics.pladipus.shared.PladipusReportableException;

import javafx.collections.ObservableList;
import javafx.stage.Stage;

public interface GuiControl {
	public void showError(String errorMsg, Stage stage);
	public boolean showAlert(String text, Stage stage);
	public String customAlert(String text, Stage stage, String[] buttons);
	public void login(String username, String password) throws PladipusReportableException;
	public void logout();
	public String getUserName();
	public ObservableList<WorkflowGui> getUserWorkflows() throws PladipusReportableException;
	public WorkflowGui getWorkflowGui(String name);
	public Workflow getWorkflowFromFilePath(String path) throws PladipusReportableException;
	public Workflow getWorkflowFromXml(String xml) throws PladipusReportableException;
	public File getCsvFile(Stage stage);
	public File getXmlFile(Stage stage);
	public String getText(Stage stage, String header, String original);
}
