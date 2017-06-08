package com.compomics.pladipus.client.gui.fxmlcontrollers;

import java.util.ResourceBundle;

import com.compomics.pladipus.client.gui.FxmlController;
import com.compomics.pladipus.client.gui.model.WorkflowGuiStep;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.stage.WindowEvent;

public class StepParametersController extends FxmlController {

	@FXML
	private Label stepIdLabel;
	@FXML
	private Label toolLabel;
	@FXML
	private Button applyBtn;
    @FXML
    private ResourceBundle resources;
    @FXML
    private TreeTableView paramTable;
    @FXML
    private TreeTableColumn mandatoryColumn;
    @FXML
    private TreeTableColumn perRunColumn;
    @FXML
    private TreeTableColumn paramNameColumn;
    @FXML
    private TreeTableColumn paramValueColumn;
    @FXML
    private TreeTableColumn editColumn;
    
	private WorkflowGuiStep step;
	
	@FXML
	public void initialize() {
		
	}
	
	@FXML
	public void handleApply() {
		
	}
	
	@FXML
	public void handleCancel() {
		if (cancelChanges()) close();
	}
	
	@FXML
	public void handleDelete() {
		
	}
	
	@Override
	public void setup(Object obj) {
		step = (WorkflowGuiStep) obj;
		stepIdLabel.setText(resources.getString("stepparam.titleLabel") + ": " + step.getStepId());
		toolLabel.setText(resources.getString("stepparam.toolLabel") + ": " + step.getToolName());
	}
	
	@Override
	public void postShow() {
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				if (!cancelChanges()) {
					event.consume();
				}
			}
    	});
	}
	
	private boolean cancelChanges() {
		if (!step.isParamChange()) return true;
		if (alert("paramDiscard")) {
			step.clearParamChanges();
			return true;
		} else {
			return false;
		}
	}
}
