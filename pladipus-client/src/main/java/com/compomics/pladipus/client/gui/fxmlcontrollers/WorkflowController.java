package com.compomics.pladipus.client.gui.fxmlcontrollers;

import java.util.ResourceBundle;

import com.compomics.pladipus.client.gui.FxmlController;
import com.compomics.pladipus.client.gui.model.LegendItem;
import com.compomics.pladipus.client.gui.model.PladipusScene;
import com.compomics.pladipus.client.gui.model.WorkflowGui;
import com.compomics.pladipus.model.core.ToolInformation;
import com.compomics.pladipus.shared.PladipusReportableException;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.stage.WindowEvent;

public class WorkflowController extends FxmlController {
	
	private WorkflowGui workflowGui;

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
    	ToolInformation toolInfo = (ToolInformation) getFromScene(PladipusScene.TOOL_CHOICE);
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
    
    @Override
    public void setup(Object workflow) {
    	this.workflowGui = (WorkflowGui) workflow;
    	workflowGui.setCanvas(canvasPane);
    	workflowGui.setGuiController(guiControl);
    	bindButtons();
    	legendTable.setItems(workflowGui.getLegendData());
    }
    
    private void bindButtons() {
        final BooleanBinding stepSelected = Bindings.isNull(workflowGui.selectedStepProperty());
        deleteStepBtn.disableProperty().bind(stepSelected);
        editStepBtn.disableProperty().bind(stepSelected);
    }
    
    @Override
    public void postShow() throws PladipusReportableException {
        if (workflowGui.getWorkflow() != null) {
        	displayWorkflow();
        	arrangeIcons();
        }
        
    	stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				if (workflowGui.changesMade() && alert("workflowAbandon")) {
					event.consume();
				}
			}
    	});
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
}
