package com.compomics.pladipus.client.gui.controllers;

import java.util.ResourceBundle;

import org.apache.commons.cli.ParseException;

import com.compomics.pladipus.client.ClientTaskProcessor;
import com.compomics.pladipus.client.gui.MainGUI;
import com.compomics.pladipus.client.gui.model.BatchGui;
import com.compomics.pladipus.client.gui.model.WorkflowGui;
import com.compomics.pladipus.shared.PladipusReportableException;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;

public class DashboardController {
	
	private MainGUI main;
	private ClientTaskProcessor processor;

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
    	main.initWorkflowDialog(null, false);
    }
    
    @FXML
    public void logoutConfirm() { // TODO sort out button text
    	Alert alert = new Alert(AlertType.CONFIRMATION, resources.getString("dashboard.logoutConfirm"), ButtonType.OK, ButtonType.CANCEL);
    	alert.initOwner(main.getPrimaryStage());
    	alert.setTitle(main.getTitle());
    	alert.setHeaderText(resources.getString("dashboard.logoutHeader"));
    	alert.showAndWait();
    	if (alert.getResult() == ButtonType.OK) {
    	    main.initLogin();
    	}
    }
	
	public void setMain(MainGUI main) {
		this.main = main;
		this.processor = main.getProcessor();
		this.workflowTable.setItems(main.getUserWorkflows());
	}
	
	public void setUsername(String username) {
		userLabel.setText(username);
	}
	
	public void doAlert(String errorText) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.initOwner(main.getPrimaryStage());
        alert.setTitle(main.getTitle());
        alert.setHeaderText(resources.getString("dashboard.errorHeader"));
        alert.setContentText(errorText);
        alert.showAndWait();
	}
}
