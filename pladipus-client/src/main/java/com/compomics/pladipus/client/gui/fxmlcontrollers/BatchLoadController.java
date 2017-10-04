package com.compomics.pladipus.client.gui.fxmlcontrollers;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import com.compomics.pladipus.client.gui.FxmlController;
import com.compomics.pladipus.model.core.WorkflowOverview;
import com.compomics.pladipus.shared.PladipusReportableException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class BatchLoadController extends FxmlController {
	
	@FXML
	private Label workflowNameLabel;
	@FXML
	private TextField batchNameField;
	@FXML
	private CheckBox chkBox;
	@FXML
	private TextArea loadingText;
	@FXML
	private Button startBtn;
	@FXML
	private ResourceBundle resources;
	private WorkflowOverview wfo;
	private String START_BTN, LOAD_BTN;
	private String filename;
	
    @FXML
    public void initialize() {
    	START_BTN = resources.getString("batchload.startBtn");
    	LOAD_BTN = resources.getString("batchload.loadBtn");
    	setButtonText();
    	setLoadText(null);
    	batchNameField.setText(getDefaultBatchName());
    	chkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
    	    @Override
    	    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
    	        setButtonText();
    	    }
    	});
    }
    
    private String getDefaultBatchName() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		return dtf.format(LocalDateTime.now());
    }
    
    private void setFileName(String filename) {
    	this.filename = filename;
    	setLoadText(filename);
    }
    
    private void setLoadText(String load) {
    	if (load == null || load.isEmpty()) {
    		startBtn.setDisable(true);
    	} else {
    		startBtn.setDisable(false);
    	}
    	loadingText.setText(load);
    }
    
    private void setButtonText() {
    	if (chkBox.isSelected()) {
    		startBtn.setText(LOAD_BTN);
    	} else {
    		startBtn.setText(START_BTN);
    	}
    }
       
    @Override
    public void setup(Object workflowOverview) throws PladipusReportableException {
    	wfo = (WorkflowOverview) workflowOverview;
    	if (wfo.getHeaders() == null || wfo.getHeaders().isEmpty()) {
    		throw new PladipusReportableException(resources.getString("batchload.invalidWorkflow"));
    	}
    }
    
    private void setFromTable() {
    	setLoadText(resources.getString("batchload.fromTableText"));
    	filename = null;
    }
    
    @FXML
    public void handleFromTable() {
    	// TODO create table, and return content as CSV String for sending to base. Maybe list of comma-separated...
    	setFromTable();
    }
    
    @FXML
    public void handleCreateHeaders() {
    	String name = batchNameField.getText();
    	if (name == null || name.isEmpty()) name = getDefaultBatchName();
    	try {
			String filename = guiControl.saveCsvFile(stage, name + ".csv", wfo.getHeaders());
			if (filename != null) {
				setFileName(filename);
			}
		} catch (PladipusReportableException e) {
			error(e.getMessage());
		}
    }
    
    @FXML
    public void handleLoadFile() {
    	File file = guiControl.getCsvFile(stage);
    	if (file != null) {
    		setFileName(file.getAbsolutePath());
    	}
    }
    
    @FXML
    public void handleCancel() {
    	close();
    }
    
    @FXML
    public void handleStart() {
    	boolean loadOnly = chkBox.isSelected(); //TODO show to user it is sending...
    	Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				if (filename != null) {
					guiControl.loadBatchFromFile(wfo, batchNameField.getText(), filename, !loadOnly);
				}
				return null;
			}
    	};
	    task.setOnSucceeded((WorkerStateEvent event) -> {
	        close();
	    });
	    task.setOnFailed((WorkerStateEvent event) -> {
	    	error(task.getException().getMessage());
	    });
    	new Thread(task).start();
    }
}
