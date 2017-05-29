package com.compomics.pladipus.client.gui;

import java.io.IOException;
import java.util.ResourceBundle;

import com.compomics.pladipus.client.gui.controllers.DashboardController;
import com.compomics.pladipus.client.gui.controllers.LoginController;
import com.compomics.pladipus.client.gui.controllers.NewWorkflowController;
import com.compomics.pladipus.client.gui.controllers.ToolChoiceController;
import com.compomics.pladipus.client.gui.controllers.WorkflowController;
import com.compomics.pladipus.client.gui.model.DefaultGui;
import com.compomics.pladipus.client.gui.model.StepIcon;
import com.compomics.pladipus.client.gui.model.WorkflowGui;
import com.compomics.pladipus.model.core.ToolInformation;
import com.compomics.pladipus.model.persist.Default;
import com.compomics.pladipus.model.persist.Workflow;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MainGUI extends Application {

	private static GuiTaskProcessor guiTaskProcessor;
	// TODO sort out ToolInfo getting into model (as Properties, not String, etc?)  Need most methods in Base ToolControl?
	private ObservableList<WorkflowGui> userWorkflows = FXCollections.observableArrayList();
	private ObservableList<DefaultGui> userDefaults = FXCollections.observableArrayList();
	private ObservableList<ToolInformation> toolInfo = FXCollections.observableArrayList();
	
	private boolean loggedIn = false;
	private Stage primaryStage;
	private static final String PLADIPUS_NAME = "Pladipus 2.0";
	private static final String PLADIPUS_ICON = "images/pladipus_icon.gif";
	private static final String LOGIN_FXML = "fxml/Login.fxml";
	private static final String LOGIN_TEXTS = "guiTexts/login";
	private static final String DASHBOARD_FXML = "fxml/Dashboard.fxml";
	private static final String DASHBOARD_TEXTS = "guiTexts/dashboard";
	private static final String WORKFLOW_FXML = "fxml/Workflow.fxml";
	private static final String WORKFLOW_TEXTS = "guiTexts/workflow";
	private static final String WORKFLOW_CSS = "css/workflow.css";
	private static final String TOOLCHOICE_FXML = "fxml/ToolChoice.fxml";
	private static final String TOOLCHOICE_TEXTS = "guiTexts/toolchoice";
	private static final String NEWWORKFLOW_FXML = "fxml/NewWorkflow.fxml";
	private static final String NEWWORKFLOW_TEXTS = "guiTexts/newworkflow";

	public MainGUI(){}
	public MainGUI(GuiTaskProcessor proc) {
		super();
		MainGUI.guiTaskProcessor = proc;
	}
	
    public void guiMain() {
        launch();
    }
    
	@Override
	public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        setDefaultLook(primaryStage);
        primaryStage.setOnCloseRequest(sureCloseHandler()); 
        if (isSetup()) {
        	initLogin();
        } else {
        	initSetup();
        }
	}
	
	private void setDefaultLook(Stage stage) {
		stage.setTitle(PLADIPUS_NAME);
		addStageIcon(stage);
	}
	private void addStageIcon(Stage stage) {
		stage.getIcons().add(new Image(PLADIPUS_ICON));
	}
	
	public String getTitle() {
		return PLADIPUS_NAME;
	}
	
	public Stage getPrimaryStage() {
		return primaryStage;
	}
	
	public GuiTaskProcessor getProcessor() {
		return guiTaskProcessor;
	}
	
	public ObservableList<ToolInformation> getToolInfo() {
		// TODO - rig up to populate this list...give error if no tool names found
		return toolInfo;
	}
	
	public ObservableList<WorkflowGui> getUserWorkflows() {
		// TODO - rig up to populate this list
		return userWorkflows;
	}
	
	public ObservableList<DefaultGui> getUserDefaults() {
		// TODO - rig up to populate this list
		return userDefaults;
	}
	
    public void initLogin() {
        try {
        	loggedIn = false;
        	userWorkflows.clear();
        	userDefaults.clear();
        	FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(LOGIN_FXML));
        	loader.setResources(ResourceBundle.getBundle(LOGIN_TEXTS));
			AnchorPane loginLayout = (AnchorPane) loader.load();
            LoginController controller = loader.getController();
            controller.setMain(this);
	        Scene scene = new Scene(loginLayout);
	        primaryStage.setScene(scene);
	        primaryStage.centerOnScreen();
	        primaryStage.show();
	    } catch (IOException e) {
	    	// TODO how to handle scene loading errors
			e.printStackTrace();
		}
    }
    
    public void initDashboard(String username) {
        try {
        	loggedIn = true;
        	initTestData(username); // TODO plumb in properly...
        	FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(DASHBOARD_FXML));
        	loader.setResources(ResourceBundle.getBundle(DASHBOARD_TEXTS));
			AnchorPane dashboardLayout = (AnchorPane) loader.load();
            DashboardController controller = loader.getController();
            controller.setMain(this);
            controller.setUsername(username);
	        Scene scene = new Scene(dashboardLayout);
	        primaryStage.setScene(scene);
	        primaryStage.centerOnScreen();
	    } catch (IOException e) {
	    	// TODO how to handle scene loading errors
			e.printStackTrace();
		}
    }
    
    public void initNewWorkflow(Stage wfStage) {
    	if (wfStage == null) {
	        wfStage = new Stage();
	        setDefaultLook(wfStage);
	        wfStage.initModality(Modality.NONE);
    	}
    	
    	try {
	    	FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(NEWWORKFLOW_FXML));
	    	loader.setResources(ResourceBundle.getBundle(NEWWORKFLOW_TEXTS));
			AnchorPane newWorkflowLayout = (AnchorPane) loader.load();
	        NewWorkflowController controller = loader.getController();
	        controller.setMain(this);
	        controller.setStage(wfStage);

	        Scene scene = new Scene(newWorkflowLayout);
	        wfStage.setScene(scene);
	        wfStage.centerOnScreen();
	        wfStage.show();
    	} catch (IOException e) {
	    	// TODO how to handle scene loading errors
			e.printStackTrace();
    	}
    }
    
    public void initEditWorkflow(Workflow editWorkflow) {
    	if (editWorkflow == null) {
    		// TODO select workflow from drop-down list pop-up (or choice to open 'new' dialog - only choice if existing workflows list empty)
    	}
 //   	initWorkflowDialog(editWorkflow, editWorkflow.getName());
    }
    
    public void initWorkflowDialog(Workflow workflow, String name, Stage wfStage) {
    	// TODO make this WorkflowGUI passed in, so can go straight to edit? Check the step dependency getting method already in Workflow...
    	try {
	    	FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(WORKFLOW_FXML));
	    	loader.setResources(ResourceBundle.getBundle(WORKFLOW_TEXTS));
			BorderPane workflowLayout = (BorderPane) loader.load();
	        WorkflowController controller = loader.getController();
	        controller.setWorkflow(workflow);

	        Stage workflowStage = new Stage();
	        setDefaultLook(workflowStage);
	        workflowStage.initModality(Modality.NONE);
	        Scene scene = new Scene(workflowLayout);
	        scene.getStylesheets().add(getClass().getClassLoader().getResource(WORKFLOW_CSS).toExternalForm());
	        workflowStage.setScene(scene);
	        workflowStage.setResizable(false);
	        controller.setStage(workflowStage);
	        controller.setMain(this);
	        workflowStage.show();
//	        if (workflow != null) {
//	        	controller.displayWorkflow();
//	        	controller.arrangeIcons();
//	        }
    	} catch (IOException e) {
	    	// TODO how to handle scene loading errors
			e.printStackTrace();
    	}
    }
    
    public ToolInformation getToolChoice(Stage wfStage) {
    	try {
			FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(TOOLCHOICE_FXML));
			loader.setResources(ResourceBundle.getBundle(TOOLCHOICE_TEXTS));
			AnchorPane toolChoiceLayout = (AnchorPane) loader.load();
			
			Stage toolChoiceStage = new Stage();
			setDefaultLook(toolChoiceStage);
			toolChoiceStage.initModality(Modality.WINDOW_MODAL);
			toolChoiceStage.initOwner(wfStage);
			
			Scene scene = new Scene(toolChoiceLayout);
			toolChoiceStage.setScene(scene);
			
			ToolChoiceController controller = loader.getController();
			controller.setStage(toolChoiceStage);
			controller.setToolChoices(getToolInfo());
			toolChoiceStage.showAndWait();

			return controller.getSelectedTool();
    	} catch (IOException e) {
	    	// TODO how to handle scene loading errors
			e.printStackTrace();
			return null;
    	}
    }
    
    private boolean isSetup() {
    	// TODO check if ActiveMQ settings set in properties
    	return true;
    }
    
    public void initSetup() {
    	// TODO - allow user to setup ActiveMQ via GUI screen
    }
    
    public void shutdown() {
    	primaryStage.close();
    }
    
    private EventHandler<WindowEvent> sureCloseHandler() {
    	return new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) { // TODO sort out text.
				if (loggedIn) {
			    	Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to exit?", ButtonType.YES, ButtonType.NO);
			    	alert.initOwner(primaryStage);
			    	alert.setTitle(getTitle());
			    	alert.setHeaderText("Exit header...");
			    	alert.showAndWait();
			    	if (alert.getResult() != ButtonType.YES) {
			    		event.consume();
			    	} else {
			    		Platform.exit();
			    	}
				}
			}
        };
    }
    
    private void initTestData(String username) {
    	toolInfo.addAll(TestData.getTools());
    	userWorkflows.addAll(TestData.getWorkflows(username));
    	userDefaults.addAll(TestData.getDefaults(username));
    }
}
