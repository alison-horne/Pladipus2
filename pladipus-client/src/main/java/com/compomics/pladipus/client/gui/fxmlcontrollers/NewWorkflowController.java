package com.compomics.pladipus.client.gui.fxmlcontrollers;

import java.io.File;
import java.util.ResourceBundle;

import com.compomics.pladipus.client.gui.FxmlController;
import com.compomics.pladipus.client.gui.model.PladipusScene;
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

public class NewWorkflowController extends FxmlController {

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
    private String EDIT_BTN, RENAME_BTN, CANCEL_BTN;
    
    @FXML
    public void initialize() {
    	EDIT_BTN = resources.getString("newworkflow.editBtn");
    	RENAME_BTN = resources.getString("newworkflow.renameBtn");
    	CANCEL_BTN = resources.getString("newworkflow.cancelBtn");
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
    	close();
    }
    
    @FXML
    public void handleOpen() {
    	try {
	    	Workflow wf = null;
	    	Workflow existing = null;
	    	if (toggle.getSelectedToggle().equals(radioNew)) {
	    		String wfName = workflowIdentifier.getText();
	    		wf = new Workflow();
	    		wf.setName(wfName);
	    		if ((existing = guiControl.getWorkflow(wfName)) != null) {
	    			String exist = customAlert("editExisting", new String[]{EDIT_BTN, RENAME_BTN, CANCEL_BTN});
	    			if (exist.equals(CANCEL_BTN)) {
	    				return;
	    			} else if (exist.equals(EDIT_BTN)) {
	    				wf = existing;
	    			} else {
	    				String newName = getNewName(wfName);
	    				if (newName != null) {
	    					wf.setName(newName);
	    				} else {
	    					return;
	    				}
	    			}
	    		} 
	    	} else if (toggle.getSelectedToggle().equals(radioFile)) {
	    		wf = guiControl.getWorkflowFromFilePath(workflowIdentifier.getText());
	    		if ((existing = guiControl.getWorkflow(wf.getName())) != null) {
	    			String exist = customAlert("editExisting", new String[]{EDIT_BTN, RENAME_BTN, CANCEL_BTN});
	    			if (exist.equals(CANCEL_BTN)) {
	    				return;
	    			} else if (exist.equals(RENAME_BTN)) {
	    				String newName = getNewName(wf.getName());
	    				if (newName != null) {
	    					wf.setName(newName);
	    				} else {
	    					return;
	    				}
	    			}
	    		}
	    	}
	    	nextScene(PladipusScene.WORKFLOW, wf, false);
    	} catch (PladipusReportableException e) {
    		error(resources.getString("newworkflow.fileReadError") + "\n" + e.getMessage());
    	}
    }
    
    @FXML
    public void handleBrowse() {
    	File xmlFile = guiControl.getXmlFile(stage);
    	workflowIdentifier.setText((xmlFile != null) ? xmlFile.getPath() : "");
    }
    
    private String getNewName(String oldName) {
    	String newName = null; 
		boolean unique = false;
		String header = resources.getString("newworkflow.enterNewName");
		
		while (!unique) {
			newName = guiControl.getText(stage, header, oldName);
			if (newName != null) {
				unique = (guiControl.getWorkflow(newName) == null);
				header = resources.getString("newworkflow.existsWarning");
			} else {
				unique = true;
			}
		}
		return newName;
    }
}
