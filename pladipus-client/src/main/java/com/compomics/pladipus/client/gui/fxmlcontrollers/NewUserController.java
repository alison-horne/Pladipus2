package com.compomics.pladipus.client.gui.fxmlcontrollers;

import java.util.ResourceBundle;

import com.compomics.pladipus.client.gui.FxmlController;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class NewUserController extends FxmlController {

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
		} else if (!password.equals(confirm)) {
			error(resources.getString("newuser.passwordMismatch"));
			return;
		} else {
			LoadingTask<Void> createTask = new LoadingTask<Void>(resources.getString("newuser.savingLbl"), null) {
				@Override
				public Void doTask() throws Exception {
	            	guiControl.createUser(username, email, password);
	                return null;
				}
				@Override
				public void onSuccess() {
			    	infoAlert("newuser", true);
			        close();
				}
			};
			createTask.run();
		}
	}
	@FXML
	public void handleCancel() {
		close();
	}
	
	private boolean emptyStr(String str) {
		return (str == null) || str.trim().isEmpty();
	}
}
