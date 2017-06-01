package com.compomics.pladipus.client.gui.controllers;

import java.util.ResourceBundle;

import com.compomics.pladipus.client.gui.MainGUI;
import com.compomics.pladipus.client.gui.model.LegendItem;
import com.compomics.pladipus.client.gui.model.WorkflowGui;
import com.compomics.pladipus.model.core.ToolInformation;
import com.compomics.pladipus.shared.PladipusReportableException;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class WorkflowController {
	
	private WorkflowGui workflowGui;
	
	private Stage stage;
	private MainGUI main;

	@FXML
	private StackPane canvasPane;
    @FXML
    private ResourceBundle resources;
    @FXML
    private TableView<LegendItem> legendTable;
    @FXML
    private TableColumn<LegendItem, Rectangle> colorColumn;
    @FXML
    private TableColumn<LegendItem, String> toolNameColumn;
    @FXML
    private Button deleteStepBtn;
    @FXML
    private Button editStepBtn;
	
    // TODO warnings on edit/delete about existing runs
    @FXML
    public void initialize() {
        toolNameColumn.setCellValueFactory(cellData -> cellData.getValue().toolNameProperty());
        colorColumn.setCellValueFactory(cellData -> cellData.getValue().colorProperty());
    }
    
    @FXML
    public void handleAddStep() {// TODO Edit menu option to change size of icons
    	ToolInformation toolInfo = main.getToolChoice(stage);
    	if (toolInfo != null) {
    		workflowGui.addStep(toolInfo, null);
    	}
    }
    
    @FXML
    public void handleDeleteStep() {
    	
    }
    
    @FXML
    public void handleEditStep() {
    	
    }

    public void setWorkflowGui(WorkflowGui workflow) {
    	this.workflowGui = workflow;
    	workflowGui.setCanvas(canvasPane);
    	workflowGui.setMain(main);
    	bindButtons();
    	legendTable.setItems(workflowGui.getLegendData());
    }
    
    private void bindButtons() {
        final BooleanBinding stepSelected = Bindings.isNull(workflowGui.selectedStepProperty());
        deleteStepBtn.disableProperty().bind(stepSelected);
        editStepBtn.disableProperty().bind(stepSelected);
    }
 
    public void displayWorkflow() throws PladipusReportableException {
    	workflowGui.displayWorkflow();
    }
    
    public void arrangeIcons() {
    	Alert loading = new Alert(AlertType.INFORMATION);
    	loading.initOwner(stage);
    	loading.show(); // TODO make nice "your workflow has loaded" info message
    	workflowGui.arrangeIcons();
    }
    
    public void setStage(Stage stage) {
    	this.stage = stage;
    	stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				if (!abandonChangesAlert()) {
					event.consume();
				}
			}
    	});
    }
    public void setMain(MainGUI main) {
    	this.main = main;
    } 
    private boolean abandonChangesAlert() {
    	boolean abandon = true;
    	if (workflowGui.changesMade()) {
        	Alert alert = new Alert(AlertType.CONFIRMATION, resources.getString("workflow.abandonConfirm"), ButtonType.OK, ButtonType.CANCEL);
        	alert.initOwner(stage);
        	alert.setTitle(stage.getTitle());
        	alert.setHeaderText(resources.getString("workflow.abandonHeader"));
        	alert.showAndWait();
        	if (alert.getResult() != ButtonType.OK) {
        	    abandon = false;
        	}
    	}
    	return abandon;
    }
}
