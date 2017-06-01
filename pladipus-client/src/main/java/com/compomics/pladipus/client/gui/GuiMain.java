package com.compomics.pladipus.client.gui;

import javafx.application.Application;
import javafx.stage.Stage;

public class GuiMain extends Application {
	
	private static SceneControl control;

	public GuiMain(){}
	public GuiMain(SceneControl control) {
		super();
		GuiMain.control = control;
	}
	
    public void guiMain() {
        launch();
    }
    
	@Override
	public void start(Stage primaryStage) {
		control.initPrimaryStage(primaryStage);
	}
}
