package com.compomics.pladipus.client.gui.controllers;

import java.util.ResourceBundle;

import com.compomics.pladipus.client.gui.MainGUI;
import com.compomics.pladipus.client.gui.model.WorkflowGui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class EditWorkflowController {

	private MainGUI main;
	private Stage stage;
	@FXML
	private Label instructionLabel;
	@FXML
	private ChoiceBox<WorkflowGui> choiceBox;
	@FXML
	private Button editBtn;
    @FXML
    private ResourceBundle resources;
	
	@FXML
	public void initialize() {
    	choiceBox.setConverter(new StringConverter<WorkflowGui>() {
			@Override
			public WorkflowGui fromString(String string) {
				return null;
			}

			@Override
			public String toString(WorkflowGui wfGui) {
				return wfGui.getWorkflowName();
			}   		
    	});
    	editBtn.disableProperty().bind(choiceBox.valueProperty().isNull());
	}
	
	public void setMain(MainGUI main) {
		this.main = main;
		choiceBox.setItems(main.getUserWorkflows());
		if (choiceBox.getItems().size() == 0) instructionLabel.setText(resources.getString("editworkflow.noneExist"));
	}
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	@FXML
	public void handleEdit() {
		main.initWorkflowDialog(choiceBox.getValue(), stage);
	}
	
	@FXML
	public void handleNew() {
		main.initNewWorkflow(stage);
	}
	
	@FXML
	public void handleCancel() {
		stage.close();
	}
}
