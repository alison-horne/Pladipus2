package com.compomics.pladipus.client.gui.fxmlcontrollers;

import java.util.ResourceBundle;

import com.compomics.pladipus.client.gui.FxmlController;
import com.compomics.pladipus.client.gui.model.PladipusScene;
import com.compomics.pladipus.client.gui.model.WorkflowOverview;
import com.compomics.pladipus.shared.PladipusReportableException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.util.StringConverter;

public class EditWorkflowController extends FxmlController {

	@FXML
	private Label instructionLabel;
	@FXML
	private ChoiceBox<WorkflowOverview> choiceBox;
	@FXML
	private Button editBtn;
    @FXML
    private ResourceBundle resources;
	
	@FXML
	public void initialize() {
    	choiceBox.setConverter(new StringConverter<WorkflowOverview>() {
			@Override
			public WorkflowOverview fromString(String string) {
				return null;
			}

			@Override
			public String toString(WorkflowOverview wf) {
				return wf.getName();
			}   		
    	});
    	editBtn.disableProperty().bind(choiceBox.valueProperty().isNull());
	}
	
	@Override
	protected void setupController() throws PladipusReportableException {
		choiceBox.setItems(guiControl.getUserWorkflows());
		if (choiceBox.getItems().size() == 0) instructionLabel.setText(resources.getString("editworkflow.noneExist"));
	}
	
	@FXML
	public void handleEdit() {
		nextScene(PladipusScene.WORKFLOW, guiControl.getWorkflow(choiceBox.getValue().getName()), false);
	}
	
	@FXML
	public void handleNew() {
		nextScene(PladipusScene.NEW_WORKFLOW, false);
	}
	
	@FXML
	public void handleCancel() {
		close();
	}
}
