package com.compomics.pladipus.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainGUI extends Application implements Alert{

	protected Parent root;
	
	private static final String SETUP_SCENE = "Setup";
	private static final String SETUP_FXML = "fxml/Setup.fxml";
	private static final String LOGIN_SCENE = "Login";
	private static final String LOGIN_FXML = "fxml/Login.fxml";
	private static final String DASHBOARD_SCENE = "Dashboard";
	private static final String DASHBOARD_FXML = "fxml/Dashboard.fxml";
	private static final String WORKFLOW_SCENE = "Workflow";
	private static final String WORKFLOW_FXML = "fxml/Workflow.fxml";
	
    public void guiMain() {	
        launch();
    }
    
	@Override
	public void init() throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/Login.fxml"));
//		loader.setController(context.getBean("LoginController"));    
		root = loader.load();
	}
    
	@Override
	public void start(Stage stage) throws Exception {

	    Scene scene = new Scene(root, 300, 275);
	    
	    stage.setTitle("Pladipus");
	    stage.setScene(scene);
	    stage.show();
	}

	@Override
	public void alert(String msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void alertAndDie(String msg) {
		// TODO Auto-generated method stub
		
	}
}
