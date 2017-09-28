package com.compomics.pladipus.client.gui.fxmlcontrollers;

import java.util.ResourceBundle;

import com.compomics.pladipus.client.gui.FxmlController;
import com.compomics.pladipus.client.gui.model.PladipusScene;
import com.compomics.pladipus.shared.PladipusReportableException;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
	private Button loginButton;
	@FXML
	private Label loginWaitLabel;
    @FXML
    private ResourceBundle resources;
	
    @FXML
    public void initialize() {}
    
    @FXML
    public void handleLogin() {
    	activeBtn(false);
    	String username = usernameField.getText();
    	String password = passwordField.getText();
    	
    	if ((username == null) || username.isEmpty()) {
    		loginError(resources.getString("login.noUsername"));
    	} else if ((password == null) || password.isEmpty()) {
    		loginError(resources.getString("login.noPassword"));
    	} else {
			Task<String> loginTask = new Task<String>() {
	            @Override protected String call() throws Exception {
	            	try {
	            		guiControl.login(username, password);
	            	} catch (PladipusReportableException e) {
	            		return e.getMessage();
	            	}
	                return null;
	            }
	        };
		    loginTask.setOnSucceeded((WorkerStateEvent event) -> {
		        if (loginTask.getValue() != null) {
		        	loginError(loginTask.getValue());
		        } else {
		        	nextScene(PladipusScene.DASHBOARD, false);
		        }
		    });
		    new Thread(loginTask).start();
    	}
    }
    
    private void loginError(String errorMsg) {
    	error(errorMsg);
    	activeBtn(true);
    }
    
    private void activeBtn(boolean active) {
    	loginWaitLabel.setText(active ? "" : resources.getString("login.wait"));
    	loginButton.setDisable(!active);
    }
    
    @FXML
    public void handleCreate() {
    	// TODO
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
