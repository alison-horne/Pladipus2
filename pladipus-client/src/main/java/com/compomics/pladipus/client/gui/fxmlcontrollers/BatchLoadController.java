package com.compomics.pladipus.client.gui.fxmlcontrollers;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import com.compomics.pladipus.client.gui.FxmlController;
import com.compomics.pladipus.client.gui.model.PladipusScene;
import com.compomics.pladipus.model.core.WorkflowOverview;
import com.compomics.pladipus.shared.PladipusReportableException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
	private String content;
	
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
    	content = null;
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
    	String fromTable = (String) getFromScene(PladipusScene.BATCH_MANUAL, wfo, content);
    	if (fromTable != null) {
    		content = fromTable;
    		setFromTable();
    	}
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
    	boolean loadOnly = chkBox.isSelected();
		String batchName = batchNameField.getText();
		if (batchName == null || batchName.isEmpty()) batchName = getDefaultBatchName();
		batchNameField.setText(batchName);
		if (wfo.batchExists(batchName) && !alert("replaceBatch")) {
			return;
		}
		LoadingTask<Void> batchTask = new LoadingTask<Void>(resources.getString("batchload.loadingLbl"), null) {
			@Override
			public void onSuccess() {
				close();
			}
			@Override
			public Void doTask() throws Exception {
				if (filename != null) {
					guiControl.loadBatchFromFile(wfo, batchNameField.getText(), filename, !loadOnly);
				} else if (content != null) {
					guiControl.loadBatchData(wfo, batchNameField.getText(), content, !loadOnly);
				} else {
					throw new PladipusReportableException(resources.getString("batchload.noSelected"));
				}
				return null;
			}
		};
		batchTask.run();
    }
}
