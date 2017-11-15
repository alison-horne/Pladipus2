package com.compomics.pladipus.client.gui;

import java.io.File;
import java.util.List;

import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public interface PopupControl {
	
	public boolean doAlertYN(String text, Stage stage);
	
	// The last button must do the same as clicking the x to close the window
	public String doAlertCustom(String text, Stage stage, String[] buttons);
	public void doError(String errorMsg, Stage stage);
	public File fileBrowse(Stage stage, ExtensionFilter[] filters);
	public File directoryBrowse(Stage stage);
	public File fileOrDirectoryBrowse(Stage stage);
	public String getText(Stage stage, String header, String original);
	public void showInfo(String text, Stage stage, boolean wait);
	public void showInfo(String header, String content, Stage stage, boolean wait);
	public File fileSaveBrowse(Stage stage, String name, ExtensionFilter[] filters);
	public <T>T choiceDialog(Stage stage, String text, List<T> list);
}
