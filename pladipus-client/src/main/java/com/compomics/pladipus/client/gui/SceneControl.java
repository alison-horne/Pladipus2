package com.compomics.pladipus.client.gui;

import com.compomics.pladipus.client.gui.model.PladipusScene;

import javafx.stage.Stage;

public interface SceneControl {
	public void openScene(PladipusScene scene, Object object);
	public void initPrimaryStage(Stage primaryStage);
	public void openFirstScene();
}
