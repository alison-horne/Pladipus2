package com.compomics.pladipus.client.gui.fxmlcontrollers;

import java.util.ResourceBundle;

import com.compomics.pladipus.client.gui.FxmlController;
import com.compomics.pladipus.model.core.DefaultOverview;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class NewDefaultController extends FxmlController {
	
	@FXML
	private TextField nameField;
	@FXML
	private TextField valueField;
	@FXML
	private ComboBox<String> typeBox;
	@FXML
	private CheckBox userCheck;
	@FXML
	private ResourceBundle resources;
	private DefaultOverview added;
	
	@FXML
	public void initialize() {
		userCheck.setSelected(false);
	}
	
	@Override
	public void setupController() {
		typeBox.setItems(guiControl.getDefaultTypes());
	}
	
	@FXML
	public void handleAdd() {
		String name = nameField.getText();
		String value = valueField.getText();
		if (name == null || name.isEmpty() || value == null || value.isEmpty()) {
			error(resources.getString("newdefault.missing"));
		} else {
			DefaultOverview def = new DefaultOverview(name, value, typeBox.getValue(), userCheck.isSelected());
			LoadingTask<Void> task = new LoadingTask<Void>(resources.getString("newdefault.sendingLbl"), null) {
				@Override
				public Void doTask() throws Exception {
					guiControl.addDefault(def);
					return null;
				}
				
				@Override
			    public void onSuccess() {
			        added = def;
			        close();
			    }
			};
			task.run();
		}
	}
	
	@FXML
	public void handleCancel() {
		added = null;
		stage.close();
	}
	
	@Override
	public Object returnObject() {
		return added;
	}
}
