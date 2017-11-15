package com.compomics.pladipus.client.gui;

import com.compomics.pladipus.client.gui.model.PladipusScene;
import com.compomics.pladipus.shared.PladipusReportableException;

import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public abstract class FxmlController {
	protected Stage stage;
	public GuiControl guiControl;
	protected SceneControl sceneControl;
	private Stage loadingDialog;
	private StringProperty loadingDialogContent;
	private String icon;
	public void setStage(Stage stage, String icon) {
		this.stage = stage;
		this.icon = icon;
        loadingDialogContent = new SimpleStringProperty(null);
        loadingDialogContent.addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue != null) {
					showLoadingDialog(true);
				} else {
					showLoadingDialog(false);
				}
			}       	
        });
	}
	public void setGuiControl(GuiControl guiControl) {
		this.guiControl = guiControl;
	}
	public void setSceneControl(SceneControl sceneControl) {
		this.sceneControl = sceneControl;
	}
	protected void nextScene(PladipusScene scene, boolean newStage) {
		sceneControl.openScene(scene, newStage ? null : stage);
	}
	protected void nextScene(PladipusScene scene, boolean newStage, Object...objects) {
		sceneControl.openOwnedScene(scene, newStage ? null : stage, newStage ? stage : null, objects);
	}
	protected Object getFromScene(PladipusScene scene) {
		return sceneControl.getOwnedSceneContent(scene, stage);
	}
	protected Object getFromScene(PladipusScene scene, Object... initObjects) {
		return sceneControl.getOwnedSceneContent(scene, stage, initObjects);
	}
	protected void close() {
		stage.close();
	}
	protected void error(String errorMsg) {
		guiControl.showError(errorMsg, stage);
	}
	protected boolean alert(String text) {
		return guiControl.showAlert(text, stage);
	}
	protected void infoAlert(String text, boolean wait) {
		guiControl.infoAlert(text, stage, wait);
	}
	protected void infoAlert(String header, String content, boolean wait) {
		guiControl.infoAlert(header, content, stage, wait);
	}
	protected String customAlert(String text, String[] buttons) {
		return guiControl.customAlert(text, stage, buttons);
	}
	public Object returnObject() {return null;}
	public void setup(Object...objects) throws PladipusReportableException {
		if (objects != null && objects.length == 1) {
			setup(objects[0]);
		} else {
			setupController();
		}
	}
	public void setup(Object object) throws PladipusReportableException {setupController();}
	protected void setupController() throws PladipusReportableException {}
	public void postShow() throws PladipusReportableException {}
	
	private void showLoadingDialog(boolean show) {
		if (show) {
			GridPane gp = new GridPane();
			gp.setStyle("-fx-border-color:black;-fx-background-color:white;-fx-background-radius:20;-fx-border-radius:20");
			Label loading = new Label(loadingDialogContent.get());
			loading.setStyle("-fx-font-weight:bold");
			loading.setPrefHeight(50);
			gp.add(loading, 0, 0);
			GridPane.setHalignment(loading, HPos.CENTER);
			StackPane sp = new StackPane();
			sp.setStyle("-fx-background-color:transparent");
			Image image = new Image(icon);
			ImageView view = new ImageView(image);
			view.prefHeight(200);
			view.prefWidth(200);
			sp.getChildren().add(view);
			StackPane.setAlignment(view, Pos.CENTER);
			sp.setPadding(new Insets(40,80,80,80));
			gp.add(sp, 0, 1);
			
		    RotateTransition rotateTransition = new RotateTransition(); 
		    rotateTransition.setDuration(Duration.millis(1000)); 
		    rotateTransition.setNode(view);       
		    rotateTransition.setByAngle(-360);
		    rotateTransition.setCycleCount(Timeline.INDEFINITE);
		    rotateTransition.setAutoReverse(false);
		    rotateTransition.play(); 
	       
		    Scene scene = new Scene(gp);
		    scene.setFill(Color.TRANSPARENT);
		    loadingDialog = new Stage();
		    loadingDialog.initStyle(StageStyle.TRANSPARENT);
		    loadingDialog.initModality(Modality.APPLICATION_MODAL);
		    loadingDialog.setScene(scene);
			loadingDialog.initOwner(stage);
		    loadingDialog.show();
		} else {
			loadingDialog.close();
		}
	}
	
	public abstract class LoadingTask<T> {
		Task<T> task;
		protected T returned;
		String loadingText;
		String errorString;
		public LoadingTask (String loadingText, String errorString) {
			this.loadingText = loadingText;
			if (errorString != null && !errorString.isEmpty()) {
				this.errorString = errorString + "\n";
			} else {
				this.errorString = "";
			}
			task = new Task<T>() {
				@Override
				protected T call() throws Exception {
					return doTask();
				}				
			};
		    task.setOnSucceeded((WorkerStateEvent event) -> {
		    	returned = task.getValue();
		    	loadingDialogContent.set(null);
		    	onSuccess();
		    });
		    task.setOnFailed((WorkerStateEvent event) -> {
		    	loadingDialogContent.set(null);
		    	error(this.errorString + task.getException().getMessage());
		    	onFail();
		    });
		    task.setOnCancelled((WorkerStateEvent event) -> {
		    	loadingDialogContent.set(null);
		    	onCancel();
		    });
		}
		
		public abstract T doTask() throws Exception;
		public void onSuccess(){};
		public void onFail(){};
		public void onCancel(){};
		public void run() {
			loadingDialogContent.set(loadingText);
			new Thread(task).start();
		}
	}
}
