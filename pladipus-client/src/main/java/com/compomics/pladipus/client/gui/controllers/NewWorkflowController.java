package com.compomics.pladipus.client.gui.controllers;

import java.io.File;
import java.util.Optional;
import java.util.ResourceBundle;

import com.compomics.pladipus.client.gui.GuiTaskProcessor;
import com.compomics.pladipus.client.gui.MainGUI;
import com.compomics.pladipus.client.gui.model.WorkflowGui;
import com.compomics.pladipus.shared.PladipusReportableException;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
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
    private enum Existing {EDIT, RENAME, CANCEL};
    
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
    	workflowIdentifier.setText("");
    	enableBrowseBtn(false);
    }
    
    private void setupFile() {
    	instructionLbl.setText(fileLabel);
    	workflowIdentifier.setText("");
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
    	try {
	    	WorkflowGui wfGui = null;
	    	WorkflowGui existing = null;
	    	if (toggle.getSelectedToggle().equals(radioNew)) {
	    		String wfName = workflowIdentifier.getText();
	    		wfGui = new WorkflowGui(null);
	    		wfGui.setWorkflowName(wfName);
	    		if ((existing = workflowExists(wfName)) != null) {
	    			Existing handleExist = handleExisting();
	    			if (handleExist == Existing.CANCEL) {
	    				return;
	    			} else if (handleExist == Existing.EDIT) {
	    				wfGui = existing;
	    			} else {
	    				String newName = getNewName(wfName);
	    				if (newName != null) {
	    	    			wfGui.setWorkflowName(newName);
	    				} else {
	    					return;
	    				}
	    			}
	    		} 
	    	} else if (toggle.getSelectedToggle().equals(radioFile)) {
	    		wfGui = new WorkflowGui(processor.getWorkflowFromFilePath(workflowIdentifier.getText()));
	    		if ((existing = workflowExists(wfGui.getWorkflowName())) != null) {
	    			Existing handleExist = handleExisting();
	    			if (handleExist == Existing.CANCEL) {
	    				return;
	    			} else if (handleExist == Existing.RENAME) {
	    				String newName = getNewName(wfGui.getWorkflowName());
	    				if (newName != null) {
	    	    			wfGui.setWorkflowName(newName);
	    	    			wfGui.getWorkflow().setName(newName);
	    				} else {
	    					return;
	    				}
	    			}
	    		}
	    	}
	    	main.initWorkflowDialog(wfGui, stage);
    	} catch (PladipusReportableException e) {
    		fileReadAlert(e.getMessage());
    	}
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
    
    private WorkflowGui workflowExists(String name) {
    	WorkflowGui existing = null;
    	for (WorkflowGui wf : main.getUserWorkflows()) {
    		if (wf.getWorkflowName().equalsIgnoreCase(name)) {
    			existing = wf;
    			break;
    		}
    	}
    	return existing;
    }
    
    private Existing handleExisting() {
    	ButtonType editBtn = new ButtonType(resources.getString("newworkflow.editBtn"));
    	ButtonType renameBtn = new ButtonType(resources.getString("newworkflow.renameBtn"));
    	ButtonType cancelBtn = new ButtonType(resources.getString("newworkflow.cancelBtn"));
    	Alert alert = new Alert(AlertType.CONFIRMATION, resources.getString("newworkflow.editExisting"), editBtn, renameBtn, cancelBtn);
    	alert.initOwner(stage);
    	alert.setTitle(stage.getTitle());
    	alert.setHeaderText(resources.getString("newworkflow.editHeader"));
    	alert.showAndWait();
    	if (alert.getResult() == editBtn) {
    	    return Existing.EDIT;
    	} else if (alert.getResult() == renameBtn) {
    		return Existing.RENAME;
    	}
    	return Existing.CANCEL;
    }
    
    private String getNewName(String oldName) {
		TextInputDialog dialog = new TextInputDialog(oldName);
		dialog.setHeaderText(resources.getString("newworkflow.enterNewName"));
		dialog.initOwner(stage);
		dialog.setTitle(stage.getTitle());
		boolean unique = false;
		Optional<String> result = null;
		
		while (!unique) {
			result = dialog.showAndWait();
			if (result.isPresent()) {
				unique = (workflowExists(result.get()) == null);
				dialog.setHeaderText(resources.getString("newworkflow.existsWarning"));
			} else {
				return null;
			}
		}
		return result.get();
    }
    
    private void fileReadAlert(String errorMsg) {
    	Alert alert = new Alert(AlertType.ERROR, errorMsg);
    	alert.initOwner(stage);
    	alert.setTitle(stage.getTitle());
    	alert.setHeaderText(resources.getString("newworkflow.fileReadError"));
    	alert.show();
    }
}
