package com.compomics.pladipus.client.gui.fxmlcontrollers;

import java.util.ResourceBundle;

import com.compomics.pladipus.client.gui.FxmlController;
import com.compomics.pladipus.client.gui.model.PladipusScene;
import com.compomics.pladipus.shared.PladipusReportableException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class LoginController extends FxmlController {
	
	// TODO issues with login screen...disabling of buttons while logging in.  Alert disappearing instantly if click done before alert appears
	// Allow cancel after login button hit but before return from control

	@FXML
	private TextField usernameField;
	@FXML
	private PasswordField passwordField;
	@FXML
	private Button loginButton;
	@FXML
	private Button cancelButton;
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
    		try {
	    		guiControl.login(username, password);
	    		nextScene(PladipusScene.DASHBOARD);
    		} catch (PladipusReportableException e) {
    			error(e.getMessage());
    			close();
    		}
    	}
    }
    
    @FXML
    public void handleCancel() {
    	close();
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
