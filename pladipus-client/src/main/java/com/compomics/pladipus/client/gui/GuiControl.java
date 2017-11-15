package com.compomics.pladipus.client.gui;

import java.io.File;
import java.util.List;

import com.compomics.pladipus.model.core.BatchOverview;
import com.compomics.pladipus.model.core.DefaultOverview;
import com.compomics.pladipus.model.core.RunOverview;
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
	public void infoAlert(String text, Stage stage, boolean wait);
	public void infoAlert(String header, String content, Stage stage, boolean wait);
	public void login(String username, String password) throws PladipusReportableException;
	public void logout();
	public String getUserName();
	public ObservableList<WorkflowOverview> getUserWorkflows();
	public ObservableList<ToolInformation> getToolInfoList() throws PladipusReportableException;
	public ToolInformation getToolInfo(String name);
	public ObservableList<DefaultOverview> getUserDefaults();
	public ObservableList<String> getDefaultTypes();
	public void addDefault(DefaultOverview def) throws PladipusReportableException;
	public Workflow getWorkflow(String name);
	public WorkflowOverview getWorkflowOverview(String name);
	public Workflow getWorkflowFromFilePath(String path) throws PladipusReportableException;
	public Workflow getWorkflowFromXml(String xml) throws PladipusReportableException;
	public WorkflowOverview saveWorkflow(Workflow workflow) throws PladipusReportableException;
	public File getCsvFile(Stage stage);
	public File getXmlFile(Stage stage);
	public String saveCsvFile(Stage stage, String name, List<String> headers) throws PladipusReportableException;
	public String saveWorkflowXml(Stage stage, String name, Workflow workflow) throws PladipusReportableException;
	public String getFileDirPath(Stage stage);
	public String getText(Stage stage, String header, String original);
	public void loadBatchFromFile(WorkflowOverview wo, String batchName, String filename, boolean startRun) throws PladipusReportableException;
	public void loadBatchData(WorkflowOverview wo, String batchName, String content, boolean startRun) throws PladipusReportableException;
	public void createUser(String username, String email, String password) throws PladipusReportableException;
	public List<RunOverview> getBatchStatus(BatchOverview batch) throws PladipusReportableException;
	public WorkflowOverview choiceWorkflow(Stage stage);
	public BatchOverview choiceBatch(WorkflowOverview workflow, Stage stage);
	public void deleteWorkflow(WorkflowOverview workflow) throws PladipusReportableException;
	public void deleteBatch(BatchOverview batch) throws PladipusReportableException;
	public void runBatch(long id) throws PladipusReportableException;
	public void runBatchRun(long id, long batchId) throws PladipusReportableException;
	public void abortBatch(long id) throws PladipusReportableException;
	public void abortBatchRun(long id) throws PladipusReportableException;
}
