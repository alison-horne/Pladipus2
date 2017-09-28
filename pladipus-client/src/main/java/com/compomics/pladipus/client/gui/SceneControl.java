package com.compomics.pladipus.client.gui;

import com.compomics.pladipus.client.gui.model.PladipusScene;

import javafx.stage.Stage;

public interface SceneControl {
	public void openScene(PladipusScene scene, Stage stage, Object... initObjects);
	public void openOwnedScene(PladipusScene scene, Stage stage, Stage owner, Object... initObjects);
	public void initPrimaryStage(Stage primaryStage);
	public Object getOwnedSceneContent(PladipusScene scene, Stage owner, Object... initObjects);
	public void logout();
}
