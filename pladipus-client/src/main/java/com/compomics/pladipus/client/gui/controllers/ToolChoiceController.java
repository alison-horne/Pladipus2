package com.compomics.pladipus.client.gui.controllers;

import com.compomics.pladipus.model.core.ToolInformation;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class ToolChoiceController {

	private ToolInformation selectedTool = null;
	private Stage stage;
	
	@FXML
	private ChoiceBox<ToolInformation> toolChoices;
	
    @FXML
    public void initialize() {
    	toolChoices.setConverter(new StringConverter<ToolInformation>() {
			@Override
			public ToolInformation fromString(String toolName) {
				return new ToolInformation(toolName);
			}
			@Override
			public String toString(ToolInformation toolInfo) {
				return toolInfo.getToolName();
			}   		
    	});
    }
    
    public void setStage(Stage stage) {
    	this.stage = stage;
    }

	public void setToolChoices(ObservableList<ToolInformation> toolInfo) {
		toolChoices.setItems(toolInfo);
	}
	
	@FXML
	public void handleOK() {
		selectedTool = toolChoices.getValue();
		stage.close();
	}
	
	@FXML
	public void handleCancel() {
		selectedTool = null;
		stage.close();
	}

	public ToolInformation getSelectedTool() {
		return selectedTool;
	}
}
