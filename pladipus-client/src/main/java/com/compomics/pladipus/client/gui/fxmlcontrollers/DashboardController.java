package com.compomics.pladipus.client.gui.fxmlcontrollers;

import java.util.ResourceBundle;

import com.compomics.pladipus.client.gui.FxmlController;
import com.compomics.pladipus.model.core.BatchOverview;
import com.compomics.pladipus.client.gui.model.PladipusScene;
import com.compomics.pladipus.model.core.WorkflowOverview;
import com.compomics.pladipus.shared.PladipusReportableException;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class DashboardController extends FxmlController {

    @FXML
    private TableView<WorkflowOverview> workflowTable;
    @FXML
    private TableColumn<WorkflowOverview, String> workflowColumn;
    @FXML
    private TableView<BatchOverview> batchTable;
    @FXML
    private TableColumn<BatchOverview, String> batchNameColumn;
    @FXML
    private TableColumn<BatchOverview, Number> batchRunsColumn;
    
    @FXML
    private ResourceBundle resources;
    @FXML
    private Label userLabel;
	
    @FXML
    public void initialize() {
    	workflowColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
    	batchNameColumn.setCellValueFactory(cellData -> cellData.getValue().batchNameProperty());
    	batchRunsColumn.setCellValueFactory(cellData -> cellData.getValue().runSizeProperty());
        showBatchDetails(null);
        workflowTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        workflowTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldWorkflow, newWorkflow) -> showBatchDetails(newWorkflow));
    }
    
    private void showBatchDetails(WorkflowOverview workflow) {
    	if (workflow == null) {
    		batchTable.setPlaceholder(new Label(resources.getString("dashboard.batchPlaceholder")));
    		batchTable.setItems(FXCollections.emptyObservableList());
    	} else {
    		batchTable.setPlaceholder(new Label(resources.getString("dashboard.batchWorkflowPlaceholder")));
    		batchTable.setItems(workflow.getBatches());
    	} 
    }
    
    @FXML
    public void handleNewWorkflow() {
    	nextScene(PladipusScene.NEW_WORKFLOW, true);
    }
    
    @FXML
    public void handleEditWorkflow() {
    	WorkflowOverview selected = workflowTable.getSelectionModel().getSelectedItem();
    	if (selected == null) {
    		nextScene(PladipusScene.EDIT_WORKFLOW, true);
    	} else {
    		nextScene(PladipusScene.WORKFLOW, true, guiControl.getWorkflow(selected.getName()));
    	}
    }
    
    @FXML
    public void logoutConfirm() {
    	if (alert("logout")) {
    		sceneControl.logout();
    		guiControl.logout();
    	    nextScene(PladipusScene.LOGIN, false);
    	}
    }
	
    @Override
	public void setupController() throws PladipusReportableException {
		workflowTable.setItems(guiControl.getUserWorkflows());
		userLabel.setText(guiControl.getUserName());
	}
    
    @FXML
    public void handleViewDefaults() {
    	nextScene(PladipusScene.USER_DEFAULTS, true, false);
    }
}
