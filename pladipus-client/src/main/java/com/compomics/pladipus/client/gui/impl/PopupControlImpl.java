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
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.stage.FileChooser.ExtensionFilter;
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
    	alert.show();
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
	public File fileBrowse(Stage stage, ExtensionFilter[] filters) {
    	FileChooser browser = new FileChooser();
    	browser.getExtensionFilters().addAll(filters);
    	return browser.showOpenDialog(stage);
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
}
