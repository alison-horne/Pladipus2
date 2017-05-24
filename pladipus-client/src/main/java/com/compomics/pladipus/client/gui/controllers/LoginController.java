package com.compomics.pladipus.client.gui.controllers;

import java.util.ResourceBundle;

import org.apache.commons.cli.ParseException;

import com.compomics.pladipus.client.ClientTaskProcessor;
import com.compomics.pladipus.client.gui.MainGUI;
import com.compomics.pladipus.shared.PladipusReportableException;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;

public class LoginController {
	
	// TODO issues with login screen...disabling of buttons while logging in.  Alert disappearing instantly if click done before alert appears
	// Allow cancel after login button hit but before return from control
	
	private MainGUI main;
	private ClientTaskProcessor processor;
	
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
    		doAlert(resources.getString("login.noUsername"));
    	} else if ((password == null) || password.isEmpty()) {
    		doAlert(resources.getString("login.noPassword"));
    	} else {
    		try {
    			processor.login(username, password);
    			main.initDashboard(username);
    		} catch (PladipusReportableException | ParseException e) {
    			doAlert(e.getMessage()); 			
    		}
    	}
    }
    
    @FXML
    public void handleCancel() {
    	main.shutdown();
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
	
	public void setMain(MainGUI main) {
		this.main = main;
		this.processor = main.getProcessor();
	}
	
	public void doAlert(String errorText) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.initOwner(main.getPrimaryStage());
        alert.setTitle(main.getTitle());
        alert.setHeaderText(resources.getString("login.errorHeader"));
        alert.setContentText(errorText);
        alert.showAndWait();
	}
}
