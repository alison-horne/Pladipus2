package com.compomics.pladipus.client.gui.fxmlcontrollers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import com.compomics.pladipus.client.gui.FxmlController;
import com.compomics.pladipus.client.gui.model.EditDisplayParameter;
import com.compomics.pladipus.client.gui.model.GlobalParameterGui;
import com.compomics.pladipus.client.gui.model.GuiSubstitutions;
import com.compomics.pladipus.client.gui.model.PladipusScene;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class NewEditGlobalController extends FxmlController {
	
	@FXML
	private TextField globalName;
	@FXML
	private CheckBox perRunChk;
	@FXML
	private VBox valueList;
	@FXML
	private Button addValueBtn;
	@FXML
	private Button removeValueBtn;
	@FXML
	private Button cancelBtn;
	@FXML
	private ResourceBundle resources;
	GuiSubstitutions subs;
	
	ObservableList<GlobalParameterGui> existingGlobals;
	GlobalParameterGui editGlobal;
	private List<EditDisplayParameter> displayValues = new ArrayList<EditDisplayParameter>();
	
	@FXML
	public void initialize() {
		perRunChk.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				refreshView();
				activeButtons(!newValue);
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setup(Object... objs) {
		if (objs != null) {
			existingGlobals = (ObservableList<GlobalParameterGui>) objs[0];
			if (objs.length > 1) {
				editGlobal = (GlobalParameterGui) objs[1];
				globalName.setText(editGlobal.getGlobalName());
				globalName.setEditable(false);
				if (!editGlobal.isPerRun()) {
					for (String value: editGlobal.getValues()) {
						addNewDisplayValue(value);
					}
				}
				perRunChk.setSelected(editGlobal.isPerRun());
				subs = editGlobal.getSubs();
			}
		}
		refreshView();
		cancelBtn.requestFocus();
	}
	
	@FXML
	public void addValue() {
		addNewDisplayValue(null);
		refreshView();
	}
	@FXML
	public void removeValue() {
		if (displayValues.size() == 1) {
			displayValues.clear();
		} else {
			Iterator<EditDisplayParameter> iter = displayValues.iterator();
			boolean removed = false;
			while (iter.hasNext()) {
				EditDisplayParameter edp = iter.next();
				if (edp.isSelected()) {
					removed = true;
					iter.remove();
				}
			}
			if (!removed) {
				error(resources.getString("newglobal.alertNoSelected"));
			}
		}
		refreshView();
	}
	@FXML
	public void saveValues() {
		if (editGlobal != null) {
			saveEdit();
		} else {
			saveNew();
		}
	}
	@FXML
	public void doCancel() {
		close();
	}
	
	private void saveEdit() {
		if (perRunChk.isSelected()) {
			editGlobal.setPerRun(true);
			editGlobal.setValues(null);
			close();
		} else {
			List<String> updatedValues = getUpdatedValues();
			if (updatedValues.isEmpty()) {
				error(resources.getString("newglobal.alertNoValues"));
			} else {
				editGlobal.setPerRun(false);
				editGlobal.setValues(updatedValues);
				close();
			}
		}
	}
	
	private void saveNew() {
		String name = globalName.getText();
		if (name == null || name.isEmpty()) {
			error(resources.getString("newglobal.alertNoName"));
		} else if (!isUnique(name)) {
			error(resources.getString("newglobal.alertDuplicateName"));
		} else {
			if (perRunChk.isSelected()) {
				editGlobal = new GlobalParameterGui(name, null, true, subs);
				existingGlobals.add(editGlobal);
				close();
			} else {
				List<String> updatedValues = getUpdatedValues();
				if (updatedValues.isEmpty()) {
					error(resources.getString("newglobal.alertNoValues"));
				} else {
					editGlobal = new GlobalParameterGui(name, updatedValues, false, subs);
					existingGlobals.add(editGlobal);
					close();
				}
			}
		}
	}
	
	private List<String> getUpdatedValues() {
		List<String> updatedValues = new ArrayList<String>();
		for (EditDisplayParameter edp: displayValues) {
			String val = edp.getValue();
			if (val != null && !val.isEmpty()) updatedValues.add(val);
		}
		return updatedValues;
	}
	
	private boolean isUnique(String name) {
		for (GlobalParameterGui existing: existingGlobals) {
			if (existing.getGlobalName().equalsIgnoreCase(name)) return false;
		}
		return true;
	}
	
	private void refreshView() {
		valueList.getChildren().clear();
		if (perRunChk.isSelected()) {
			setUpPerRun();
		} else {
			setUpWithValues();
		}
	}
	
	private void setUpWithValues() {
		ensureOneField();
		for (EditDisplayParameter param: displayValues) {
			valueList.getChildren().add(param.getBox());
		}
	}
	
	private void setUpPerRun() {
		valueList.getChildren().add(getBlankLine().getBox());
	}
	
	private EditDisplayParameter getBlankLine() {
		if (subs == null) {
			subs = new GuiSubstitutions(guiControl.getUserDefaults());
		}
		EditDisplayParameter blank = new EditDisplayParameter("---", subs, 
				resources.getString("newglobal.btnTxt"), resources.getString("newglobal.browseBtnTxt"));
		blank.disableEdit(true);
		return blank;
	}
	
	private void activeButtons(boolean active) {
		addValueBtn.setDisable(!active);
		removeValueBtn.setDisable(!active);
	}
	
	private void addNewDisplayValue(String initValue) {
		if (subs == null) {
			subs = new GuiSubstitutions(guiControl.getUserDefaults());
		}
		EditDisplayParameter displayParam = new EditDisplayParameter(initValue, subs, 
				resources.getString("newglobal.btnTxt"), resources.getString("newglobal.browseBtnTxt"));
		displayParam.getInsertBtn().setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				String subValue = (String) getFromScene(PladipusScene.USER_DEFAULTS, true);
				if (subValue != null) displayParam.insertSub(subValue);
			}
			
		});
		displayParam.getBrowseBtn().setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				String fileDirPath = guiControl.getFileDirPath(stage);
				if (fileDirPath != null) displayParam.insertFileDir(fileDirPath);
			}
			
		});
		displayValues.add(displayParam);
	}
	
	private void ensureOneField() {
		if (displayValues.isEmpty()) addNewDisplayValue(null);
	}
	
	@Override
	public Object returnObject() {
		return editGlobal;
	}
}
