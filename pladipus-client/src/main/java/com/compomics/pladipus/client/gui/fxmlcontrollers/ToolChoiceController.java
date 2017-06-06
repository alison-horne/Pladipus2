package com.compomics.pladipus.client.gui.fxmlcontrollers;

import com.compomics.pladipus.client.gui.FxmlController;
import com.compomics.pladipus.model.core.ToolInformation;
import com.compomics.pladipus.shared.PladipusReportableException;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.util.StringConverter;

public class ToolChoiceController extends FxmlController {

	private ToolInformation selectedTool = null;
	
	@FXML
	private ChoiceBox<ToolInformation> toolChoices;
	
    @FXML
    public void initialize() {
    	toolChoices.setConverter(new StringConverter<ToolInformation>() {
			@Override
			public ToolInformation fromString(String toolName) {
				return null;
			}
			@Override
			public String toString(ToolInformation toolInfo) {
				return toolInfo.getToolName();
			}   		
    	});
    }

	protected void setupController() throws PladipusReportableException {
		toolChoices.setItems(guiControl.getToolInfoList());
	}
	
	@FXML
	public void handleOK() {
		selectedTool = toolChoices.getValue();
		close();
	}
	
	@FXML
	public void handleCancel() {
		selectedTool = null;
		close();
	}
	
	@Override
	public ToolInformation returnObject() {
		return selectedTool;
	}
}
