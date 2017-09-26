package com.compomics.pladipus.client.gui.impl;

import java.io.IOException;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;

import com.compomics.pladipus.client.gui.FxmlController;
import com.compomics.pladipus.client.gui.GuiControl;
import com.compomics.pladipus.client.gui.PopupControl;
import com.compomics.pladipus.client.gui.SceneControl;
import com.compomics.pladipus.client.gui.UserControl;
import com.compomics.pladipus.client.gui.model.PladipusScene;
import com.compomics.pladipus.shared.PladipusMessages;
import com.compomics.pladipus.shared.PladipusReportableException;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class SceneControlImpl implements SceneControl {

	@Autowired
	private GuiControl guiControl;
	@Autowired
	private UserControl userControl;
	@Autowired
	private PopupControl popupControl;
	@Autowired
	private PladipusMessages exceptionMessages;
	
	private static final String PLADIPUS_NAME = "Pladipus 2.0";
	private static final String PLADIPUS_ICON = "images/pladipus_icon.gif";
	
	private Stage primaryStage;
	
	@Override
	public void openScene(PladipusScene scene, Stage stage, Object... initObjects) {
		openOwnedScene(scene, stage, null, initObjects);
	}
	
	@Override
	public void openOwnedScene(PladipusScene scene, Stage stage, Stage owner, Object... initObjects) {
		if (owner == null) owner = primaryStage;
		if (stage == null) {
			if (scene.isPrimary()) {
				stage = primaryStage;
			} else {
				stage = getNewStage();
				stage.initOwner(owner);
			}
		}
		try {
			FxmlController controller = setupScene(stage, scene); 
			controller.setup(initObjects);
			stage.show();
			controller.postShow();
		} catch (PladipusReportableException e) {
			sceneLoadError(owner, e.getMessage());
		}
	}
	
	@Override
	public Object getOwnedSceneContent(PladipusScene scene, Stage owner, Object... initObjects) {
		if (owner == null) owner = primaryStage;
		Stage stage = getNewStage();
		stage.initOwner(owner);
    	stage.initModality(Modality.WINDOW_MODAL);
		try {
			FxmlController controller = setupScene(stage, scene);
			controller.setup(initObjects);
			stage.showAndWait();
			return controller.returnObject();
		} catch (PladipusReportableException e) {
			sceneLoadError(owner, e.getMessage());
			return null;
		}
	}
	
	private Stage getNewStage() {
		Stage stage = new Stage();
		initStage(stage);
		return stage;
	}

	private void openFirstScene() {
		// TODO check setup...
		boolean setup = true;
		if (!setup) {
			openScene(PladipusScene.SETUP, primaryStage);
		} else {
			openScene(PladipusScene.LOGIN, primaryStage);
		}
	}

	@Override
	public void initPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
		initStage(primaryStage);
		primaryStage.setOnCloseRequest(sureCloseHandler()); 
		openFirstScene();
	}
	
	private void initStage(Stage stage) {
		stage.setTitle(PLADIPUS_NAME);
		stage.getIcons().add(new Image(PLADIPUS_ICON));
	}
	
    private EventHandler<WindowEvent> sureCloseHandler() {
    	return new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				if (userControl.isLoggedIn()) {
					if (!popupControl.doAlertYN("confirmExit", primaryStage)) {
			    		event.consume();
			    	} else {
			    		Platform.exit();
			    	}
				}
			}
        };
    }
    
    private FxmlController setupScene(Stage stage, PladipusScene scene) throws PladipusReportableException {
    	try {
	    	FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(scene.getFxml()));
	    	loader.setResources(ResourceBundle.getBundle(scene.getTexts()));
			Region layout = loader.load();
	        FxmlController controller = loader.getController();
	        controller.setGuiControl(guiControl);
	        controller.setSceneControl(this);
	        controller.setStage(stage);
	        
	        Scene newScene = new Scene(layout);
	        if (scene.getCss() != null) {
	        	newScene.getStylesheets().add(getClass().getClassLoader().getResource(scene.getCss()).toExternalForm());
	        }
	        stage.setScene(newScene);
	        stage.centerOnScreen();
	        stage.setResizable(scene.canResize());
	        return controller;
    	} catch (IOException e) {
    		throw new PladipusReportableException(exceptionMessages.getMessage("client.sceneLoadError", scene.name(), e.getMessage()));
    	}
    }
    
    private void sceneLoadError(Stage stage, String errorMsg) {
    	popupControl.doError(errorMsg, stage);
    }
}
