package com.compomics.pladipus.client.gui.fxmlcontrollers;

import java.util.ResourceBundle;

import com.compomics.pladipus.client.gui.FxmlController;
import com.compomics.pladipus.client.gui.model.BatchGui;
import com.compomics.pladipus.client.gui.model.PladipusScene;
import com.compomics.pladipus.client.gui.model.WorkflowGui;
import com.compomics.pladipus.shared.PladipusReportableException;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class DashboardController extends FxmlController {

    @FXML
    private TableView<WorkflowGui> workflowTable;
    @FXML
    private TableColumn<WorkflowGui, String> workflowColumn;
    @FXML
    private TableView<BatchGui> batchTable;
    @FXML
    private TableColumn<BatchGui, String> batchNameColumn;
    @FXML
    private TableColumn<BatchGui, Number> batchRunsColumn;
    
    @FXML
    private ResourceBundle resources;
    @FXML
    private Label userLabel;
	
    @FXML
    public void initialize() {
    	workflowColumn.setCellValueFactory(cellData -> cellData.getValue().workflowNameProperty());
    	batchNameColumn.setCellValueFactory(cellData -> cellData.getValue().batchNameProperty());
    	batchRunsColumn.setCellValueFactory(cellData -> cellData.getValue().runSizeProperty());
        showBatchDetails(null);
        workflowTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        workflowTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldWorkflow, newWorkflow) -> showBatchDetails(newWorkflow));
    }
    
    private void showBatchDetails(WorkflowGui workflow) {
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
    	WorkflowGui selected = workflowTable.getSelectionModel().getSelectedItem();
    	if (selected == null) {
    		nextScene(PladipusScene.EDIT_WORKFLOW, true);
    	} else {
    		nextScene(PladipusScene.WORKFLOW, selected, true);
    	}
    }
    
    @FXML
    public void logoutConfirm() {
    	if (alert("logout")) {
    		guiControl.logout();
    	    nextScene(PladipusScene.LOGIN, false);
    	}
    }
	
    @Override
	public void setupController() throws PladipusReportableException {
		workflowTable.setItems(guiControl.getUserWorkflows());
		userLabel.setText(guiControl.getUserName());
	}
}
