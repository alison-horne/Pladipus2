package com.compomics.pladipus.client.gui.fxmlcontrollers;

import java.util.ResourceBundle;

import com.compomics.pladipus.client.gui.FxmlController;
import com.compomics.pladipus.client.gui.model.WorkflowGuiStep;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class RemoveLinksController extends FxmlController {
	
	private WorkflowGuiStep step;
	private boolean removed = false;
	
	@FXML
	private ResourceBundle resources;
	@FXML
	private VBox linksBox;
	@FXML
	private Label stepIdLbl;
	
	@FXML
	public void initialize() {}
	
	@Override
	public void setup(Object obj) {
		step = (WorkflowGuiStep) obj;
		stepIdLbl.setText(step.getStepId());
		for (String stepId: step.getStepLinkNoOutputs()) {
			linksBox.getChildren().add(new CheckBox(stepId));
		}
	}

	@FXML
	public void handleRemove() {
		for (Node child: linksBox.getChildren()) {
			if (child instanceof CheckBox) {
				if (((CheckBox) child).isSelected()) {
					removed = true;
					step.removeStepLinkNoOutput(((CheckBox) child).getText());
				}
			}
		}
		if (removed) {
			close();
		} else {
			error(resources.getString("removelinks.alertNoSelected"));
		}
	}
	
	@FXML
	public void handleCancel() {
		close();
	}
	
	@Override
	public Object returnObject() {
		return removed;
	}
}
