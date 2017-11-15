package com.compomics.pladipus.client.gui.fxmlcontrollers;

import java.util.ResourceBundle;

import com.compomics.pladipus.client.gui.FxmlController;
import com.compomics.pladipus.client.gui.model.PladipusScene;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class LoginController extends FxmlController {

	@FXML
	private TextField usernameField;
	@FXML
	private PasswordField passwordField;
    @FXML
    private ResourceBundle resources;
	
    @FXML
    public void initialize() {}
    
    @FXML
    public void handleLogin() {
    	String username = usernameField.getText();
    	String password = passwordField.getText();
    	
    	if ((username == null) || username.isEmpty()) {
    		error(resources.getString("login.noUsername"));
    	} else if ((password == null) || password.isEmpty()) {
    		error(resources.getString("login.noPassword"));
    	} else {
    		LoadingTask<Void> loginTask = new LoadingTask<Void>(resources.getString("login.wait"), null) {
				@Override
				public Void doTask() throws Exception {
					guiControl.login(username, password);
					return null;
				}
				@Override
				public void onSuccess() {
					nextScene(PladipusScene.DASHBOARD, false);
				}
    		};
    		loginTask.run();
    	}
    }
    
    @FXML
    public void handleCreate() {
    	nextScene(PladipusScene.NEW_USER, true);
    }
    
    @FXML
    public void handleForgot() {
    	// TODO
    }

    @FXML
    public void enterListener(KeyEvent event) {
    	if(event.getCode() == KeyCode.ENTER) {
    		handleLogin();
    	}
    }
}
