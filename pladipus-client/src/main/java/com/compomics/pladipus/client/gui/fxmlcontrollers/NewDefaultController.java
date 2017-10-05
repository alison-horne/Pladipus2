package com.compomics.pladipus.client.gui.fxmlcontrollers;

import java.util.ResourceBundle;

import com.compomics.pladipus.client.gui.FxmlController;
import com.compomics.pladipus.model.core.DefaultOverview;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class NewDefaultController extends FxmlController {
	
	@FXML
	private TextField nameField;
	@FXML
	private TextField valueField;
	@FXML
	private ComboBox<String> typeBox;
	@FXML
	private CheckBox userCheck;
	@FXML
	private Button addBtn, cancelBtn;
	@FXML
	private Label addLbl;
	@FXML
	private ResourceBundle resources;
	private DefaultOverview added;
	
	@FXML
	public void initialize() {
		userCheck.setSelected(false);
	}
	
	@Override
	public void setupController() {
		typeBox.setItems(guiControl.getDefaultTypes());
	}
	
	@FXML
	public void handleAdd() {
		String name = nameField.getText();
		String value = valueField.getText();
		if (name == null || name.isEmpty() || value == null || value.isEmpty()) {
			error(resources.getString("newdefault.missing"));
		} else {
			DefaultOverview def = new DefaultOverview(name, value, typeBox.getValue(), userCheck.isSelected());
			sendingLook(true);
			Task<Void> task = new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					guiControl.addDefault(def);
					return null;
				}
			};
		    task.setOnSucceeded((WorkerStateEvent event) -> {
		        added = def;
		        close();
		    });
		    task.setOnFailed((WorkerStateEvent event) -> {
		    	sendingLook(false);
		    	error(task.getException().getMessage());
		    });
			new Thread(task).start();
		}
	}
	
	@FXML
	public void handleCancel() {
		added = null;
		stage.close();
	}
	
	@Override
	public Object returnObject() {
		return added;
	}
	
	private void sendingLook(boolean sending) {
		if (sending) {
			addLbl.setText(resources.getString("newdefault.sendingLbl"));
		} else {
			addLbl.setText("");
		}
		addBtn.setDisable(sending);
		cancelBtn.setDisable(sending);
	}
}
