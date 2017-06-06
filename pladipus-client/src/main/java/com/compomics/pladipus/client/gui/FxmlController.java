package com.compomics.pladipus.client.gui;

import com.compomics.pladipus.client.gui.model.PladipusScene;
import com.compomics.pladipus.shared.PladipusReportableException;

import javafx.stage.Stage;

public abstract class FxmlController {
	protected Stage stage;
	protected GuiControl guiControl;
	protected SceneControl sceneControl;
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	public void setGuiControl(GuiControl guiControl) {
		this.guiControl = guiControl;
	}
	public void setSceneControl(SceneControl sceneControl) {
		this.sceneControl = sceneControl;
	}
	protected void nextScene(PladipusScene scene, boolean newStage) {
		sceneControl.openScene(scene, null, newStage ? null : stage);
	}
	protected void nextScene(PladipusScene scene, Object object, boolean newStage) {
		sceneControl.openScene(scene, object, newStage ? null : stage);
	}
	protected Object getFromScene(PladipusScene scene) {
		return sceneControl.getOwnedSceneContent(scene, null, stage);
	}
	protected Object getFromScene(PladipusScene scene, Object initObject) {
		return sceneControl.getOwnedSceneContent(scene, initObject, stage);
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
	protected String customAlert(String text, String[] buttons) {
		return guiControl.customAlert(text, stage, buttons);
	}
	public Object returnObject() {return null;}
	public void setup(Object object) throws PladipusReportableException {setupController();}
	protected void setupController() throws PladipusReportableException {}
	public void postShow() throws PladipusReportableException {}
}
