package com.compomics.pladipus.client.gui.controllers;

import java.io.File;
import java.util.ResourceBundle;

import com.compomics.pladipus.client.gui.GuiTaskProcessor;
import com.compomics.pladipus.client.gui.MainGUI;
import com.compomics.pladipus.model.persist.Workflow;
import com.compomics.pladipus.shared.PladipusReportableException;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class NewWorkflowController {
	
	private Stage stage;
	private MainGUI main;
	private GuiTaskProcessor processor;

	@FXML
	private RadioButton radioNew;
	@FXML
	private RadioButton radioFile;
	@FXML
	private Button browseBtn;
	@FXML
	private Button openBtn;
	@FXML
	private Label instructionLbl;
	@FXML
	private TextField workflowIdentifier;
    @FXML
    private ResourceBundle resources;
    private ToggleGroup toggle;
    private String newLabel;
    private String fileLabel;
    
    @FXML
    public void initialize() {
    	enableBrowseBtn(false);
    	openBtn.disableProperty().bind(Bindings.isEmpty(workflowIdentifier.textProperty()));
    	toggle = new ToggleGroup();
    	radioNew.setToggleGroup(toggle);
    	radioFile.setToggleGroup(toggle);
    	newLabel = resources.getString("newworkflow.workflowNameLabel");
    	fileLabel = resources.getString("newworkflow.workflowFileLabel");
        toggle.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
				if (newValue.getToggleGroup().getSelectedToggle().equals(radioNew)) {
					setupNew();
				} else {
					setupFile();
				}
			}
		});
    }
    
    public void setStage(Stage stage) {
    	this.stage = stage;
    }
    
    public void setMain(MainGUI main) {
    	this.main = main;
    	this.processor = main.getProcessor();
    }
    
    private void setupNew() {
    	instructionLbl.setText(newLabel);
    	enableBrowseBtn(false);
    }
    
    private void setupFile() {
    	instructionLbl.setText(fileLabel);
    	enableBrowseBtn(true);
    }
    
    private void enableBrowseBtn(boolean enable) {
    	browseBtn.setDisable(!enable);
    	browseBtn.setVisible(enable);
    }

    @FXML
    public void handleCancel() {
    	stage.close();
    }
    
    @FXML
    public void handleOpen() {
//    	try {
//	    	if (toggle.getSelectedToggle().equals(radioNew)) {
//	    		
//	    	} else if (toggle.getSelectedToggle().equals(radioFile)) {
//	    		processor.getWorkflowFromFilePath();
//	    	}
//    	} catch (PladipusReportableException e) {
//    		//TODO Do alert
//    	}
//    	main.initWorkflowDialog(getWorkflow(), getName(), stage);
    }
    
    @FXML
    public void handleBrowse() {
    	FileChooser browser = new FileChooser();
    	ExtensionFilter xmlExt = new ExtensionFilter(resources.getString("newworkflow.xmlFiles"), "*.xml");
    	ExtensionFilter allExt = new ExtensionFilter(resources.getString("newworkflow.allFiles"), "*");
    	browser.getExtensionFilters().addAll(xmlExt, allExt);
    	File xmlFile = browser.showOpenDialog(stage);
    	workflowIdentifier.setText((xmlFile != null) ? xmlFile.getPath() : "");
    }
    
    private Workflow getWorkflow() {
    	return null;
    }
    
    private String getName() {
    	return null;
    }
}
