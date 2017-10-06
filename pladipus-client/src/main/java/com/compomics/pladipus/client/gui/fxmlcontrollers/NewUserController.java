package com.compomics.pladipus.client.gui.fxmlcontrollers;

import java.util.ResourceBundle;

import com.compomics.pladipus.client.gui.FxmlController;
import com.compomics.pladipus.shared.PladipusReportableException;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

public class NewUserController extends FxmlController {

	@FXML
	private Button saveBtn;
	@FXML
	private Button cancelBtn;
	@FXML
	private Label savingLbl;
	@FXML
	private TextField nameField;
	@FXML
	private TextField emailField;
	@FXML
	private PasswordField passwordField;
	@FXML
	private PasswordField confirmField;
	@FXML
	private ResourceBundle resources;
	
	@FXML
	public void initialize(){}
	
	@FXML
	public void handleSave() {
		String username = nameField.getText();
		String email = emailField.getText();
		String password = passwordField.getText();
		String confirm = confirmField.getText();
		if (emptyStr(username) || emptyStr(email) || emptyStr(password) || emptyStr(confirm)) {
			error(resources.getString("newuser.emptyField"));
			return;
		}
		if (!password.equals(confirm)) {
			error(resources.getString("newuser.passwordMismatch"));
			return;
		}
		savingLook(true);
		Task<Void> createTask = new Task<Void>() {
            @Override protected Void call() throws PladipusReportableException {
            	guiControl.createUser(username, email, password);
                return null;
            }
        };
	    createTask.setOnSucceeded((WorkerStateEvent event) -> {
	    	infoAlert("newuser", true);
	        close();
	    });
	    createTask.setOnFailed((WorkerStateEvent event) -> {
	    	savingLook(false);
	    	error(createTask.getException().getMessage());
	    });
	    new Thread(createTask).start();
	}
	@FXML
	public void handleCancel() {
		close();
	}
	
	private void savingLook(boolean saving) {
		if (saving) {
			savingLbl.setTextFill(Color.RED);
			savingLbl.setText(resources.getString("newuser.savingLbl"));
		} else {
			savingLbl.setText("");
		}
		saveBtn.setDisable(saving);
		cancelBtn.setDisable(saving);
	}
	
	private boolean emptyStr(String str) {
		return (str == null) || str.trim().isEmpty();
	}
}
