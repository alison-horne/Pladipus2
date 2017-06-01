package com.compomics.pladipus.client.gui;

import javafx.stage.Stage;

public interface PopupControl {
	
	public boolean doAlertYN(String text, Stage stage);
	
	// The last button must do the same as clicking the x to close the window
	public String doAlertCustom(String text, Stage stage, String[] buttons);
	public void doError(String errorMsg, Stage stage);
}
