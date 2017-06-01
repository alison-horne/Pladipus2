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
import com.compomics.pladipus.shared.PladipusReportableException;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
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
	
	private static final String PLADIPUS_NAME = "Pladipus 2.0";
	private static final String PLADIPUS_ICON = "images/pladipus_icon.gif";
	
	private Stage primaryStage;
	private Stage workflowStage;
	private Stage toolChoiceStage;
	
	@Override
	public void openScene(PladipusScene scene, Object object) {
		Stage stage = getStage(scene);
		try {
        	FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(scene.getFxml()));
        	loader.setResources(ResourceBundle.getBundle(scene.getTexts()));
			Region layout = loader.load();
            FxmlController controller = loader.getController();
            controller.setGuiControl(guiControl);
            controller.setSceneControl(this);
            controller.setStage(stage);
            controller.setup(object);
	        Scene newScene = new Scene(layout);
	        if (scene.getCss() != null) {
	        	newScene.getStylesheets().add(getClass().getClassLoader().getResource(scene.getCss()).toExternalForm());
	        }
	        stage.setScene(newScene);
	        stage.centerOnScreen();
	        stage.setResizable(scene.canResize());
	        stage.show();
	        controller.postShow();
	    } catch (IOException e) {
	    	// TODO how to handle scene loading errors
			e.printStackTrace();
	    } catch (PladipusReportableException e) {
	    	popupControl.doError(e.getMessage(), stage);
	    }
	}
	
	@Override
	public void openFirstScene() {
		// TODO check setup...
		boolean setup = true;
		if (!setup) {
			openScene(PladipusScene.SETUP, null);
		} else {
			openScene(PladipusScene.LOGIN, null);
		}
	}

	@Override
	public void initPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
		initStage(primaryStage);
		primaryStage.setOnCloseRequest(sureCloseHandler()); 
	}
	
	private void initStage(Stage stage) {
		stage.setTitle(PLADIPUS_NAME);
		stage.getIcons().add(new Image(PLADIPUS_ICON));
	}

	private Stage getStage(PladipusScene scene) {
		switch (scene.getStage()) {
			case WORKFLOW:
				return getWorkflowStage();
			case TOOL:
				return getToolChoiceStage();
			case PRIMARY:
				return primaryStage;
			default:
				return primaryStage;
		}
	}
	
	private Stage getWorkflowStage() {
		if (workflowStage == null) {
			workflowStage = new Stage();
			initStage(workflowStage);
			workflowStage.initModality(Modality.NONE);
		}
		return workflowStage;
	}
	
	private Stage getToolChoiceStage() {
		if (toolChoiceStage == null) {
			toolChoiceStage = new Stage();
			initStage(toolChoiceStage);
			toolChoiceStage.initModality(Modality.WINDOW_MODAL);
			toolChoiceStage.initOwner(workflowStage);
		}
		return toolChoiceStage;
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
}
