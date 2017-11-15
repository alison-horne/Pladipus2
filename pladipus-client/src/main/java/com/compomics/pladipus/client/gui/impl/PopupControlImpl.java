package com.compomics.pladipus.client.gui.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.ResourceBundle;

import com.compomics.pladipus.client.gui.PopupControl;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class PopupControlImpl implements PopupControl {
	
	private ResourceBundle resources = ResourceBundle.getBundle("guiTexts/popup");

	@Override
	public boolean doAlertYN(String text, Stage stage) {
		if (doAlert(text, stage, ButtonType.YES, ButtonType.NO) != ButtonType.YES) {
			return false;
		}
		return true;
	}

	@Override
	public String doAlertCustom(String text, Stage stage, String[] buttons) {
		List<ButtonType> buttonTypes = new ArrayList<ButtonType>();
		for (int i = 0; i < buttons.length ; i++) {
			if (i == (buttons.length - 1)) {
				buttonTypes.add(new ButtonType(buttons[i], ButtonData.CANCEL_CLOSE));
			} else {
				buttonTypes.add(new ButtonType(buttons[i]));
			}
		}
		
    	return doAlert(text, stage, buttonTypes.toArray(new ButtonType[]{})).getText();
	}
	
	@Override
	public void doError(String errorMsg, Stage stage) {
		Alert alert = new Alert(AlertType.ERROR, errorMsg);
    	alert.initOwner(stage);
    	alert.setTitle(stage.getTitle());
    	alert.showAndWait();
	}

    private String getContent(String text) {
    	try {
    		return resources.getString("popup." + text + ".content");
    	} catch (MissingResourceException e) {
    		return "";
    	}
    }
    
    private String getHeader(String text) {
    	try {
    		return resources.getString("popup." + text + ".header");
    	} catch (MissingResourceException e) {
    		return "";
    	}
    }
    
    private ButtonType doAlert(String text, Stage stage, ButtonType... buttons) {
    	Alert alert = new Alert(AlertType.CONFIRMATION, getContent(text), buttons);
    	alert.initOwner(stage);
    	alert.setTitle(stage.getTitle());
    	alert.setHeaderText(getHeader(text));
    	alert.showAndWait();
    	return alert.getResult();
    }
    
    @Override
    public void showInfo(String text, Stage stage, boolean wait) {
    	showInfo(getHeader(text), getContent(text), stage, wait);
    }
    
    @Override
    public void showInfo(String header, String content, Stage stage, boolean wait) {
    	Alert alert = new Alert(AlertType.INFORMATION);
    	alert.initOwner(stage);
    	alert.setContentText(content);
    	alert.setTitle(stage.getTitle());
    	alert.setHeaderText(header);
    	if (wait) {
    		alert.showAndWait();
    	} else {
    		alert.show();
    	}
    }

	@Override
	public File fileBrowse(Stage stage, ExtensionFilter[] filters) {
    	FileChooser browser = new FileChooser();
    	if (filters != null) {
    		browser.getExtensionFilters().addAll(filters);
    	}
    	return browser.showOpenDialog(stage);
	}
	
	@Override
	public File directoryBrowse(Stage stage) {
		DirectoryChooser browser = new DirectoryChooser();
		return browser.showDialog(stage);
	}
	
	@Override
	public File fileOrDirectoryBrowse(Stage stage) {
		String fileBtn = resources.getString("popup.fileBtn");
		String dirBtn = resources.getString("popup.dirBtn");
		String cancelBtn = resources.getString("popup.cancelBtn");
		String result = doAlertCustom("fileOrDir", stage, new String[]{fileBtn, dirBtn, cancelBtn});
		if (result.equals(fileBtn)) {
			return fileBrowse(stage, null);
		} else if (result.equals(dirBtn)) {
			return directoryBrowse(stage);
		}
		return null;
	}
	
	@Override
	public File fileSaveBrowse(Stage stage, String name, ExtensionFilter[] filters) {
		FileChooser chooser = new FileChooser();
		if (filters != null) {
			chooser.getExtensionFilters().addAll(filters);
		}
		chooser.setInitialFileName(name);
		return chooser.showSaveDialog(stage);
	}

	@Override
	public String getText(Stage stage, String header, String original) {
		TextInputDialog dialog = new TextInputDialog(original);
		dialog.setHeaderText(header);
		dialog.initOwner(stage);
		dialog.setTitle(stage.getTitle());
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			return result.get();
		}
		return null;
	}
	
	@Override
	public <T>T choiceDialog(Stage stage, String text, List<T> list) {
		ChoiceDialog<T> dialog = new ChoiceDialog<>();
		dialog.getItems().addAll(list);
		dialog.initOwner(stage);
		dialog.setTitle(stage.getTitle());
		dialog.setHeaderText(getHeader(text));
		dialog.setContentText(getContent(text));

		Optional<T> result = dialog.showAndWait();
		if (result.isPresent()){
		   	return result.get();
		}
		return null;
	}
}
