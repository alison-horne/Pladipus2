package com.compomics.pladipus.client.gui.fxmlcontrollers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import com.compomics.pladipus.client.gui.FxmlController;
import com.compomics.pladipus.client.gui.model.EditDisplayParameter;
import com.compomics.pladipus.client.gui.model.StepParameterGui;
import com.compomics.pladipus.shared.PladipusReportableException;
import com.compomics.pladipus.client.gui.model.PladipusScene;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;

public class ParamEditController extends FxmlController {

	@FXML
	private VBox valueList;
	@FXML
	private Label paramName;
	@FXML
	private TextArea paramDescription;
	@FXML
	private Button cancelBtn;
	@FXML
	private ResourceBundle resources;
	private boolean changed = false;
	private StepParameterGui param;
	private List<EditDisplayParameter> displayValues = new ArrayList<EditDisplayParameter>();
	
	@FXML
	public void initialize() {}
	
	@Override
	public void setup(Object... obj) {
		param = (StepParameterGui) obj[0];
		paramName.setText(param.getInputParam().getParamName());
		paramDescription.setText(param.getInputParam().getDescription());
		for (String value: param.getValues()) {
			addNewDisplayValue(value);
		}
		if (obj.length > 1) {
			for (int i = 1; i < obj.length; i++) {
				addNewDisplayValue((String) obj[i]);
			}
		}
		ensureOneField();
		cancelBtn.requestFocus();
	}
	
	@FXML
	public void addValue() {
		addNewDisplayValue(null);
	}
	@FXML
	public void removeValue() {
		if (displayValues.size() == 1) {
			displayValues.clear();
			valueList.getChildren().clear();
		} else {
			Iterator<EditDisplayParameter> iter = displayValues.iterator();
			boolean removed = false;
			while (iter.hasNext()) {
				EditDisplayParameter edp = iter.next();
				if (edp.isSelected()) {
					removed = true;
					iter.remove();
					valueList.getChildren().remove(edp.getBox());
				}
			}
			if (!removed) {
				error(resources.getString("paramEdit.alertNoSelected"));
			}
		}
		ensureOneField();
	}
	@FXML
	public void saveValues() {
		List<String> updatedValues = new ArrayList<String>();
		for (EditDisplayParameter edp: displayValues) {
			String val = edp.getValue();
			if (val != null && !val.isEmpty()) updatedValues.add(val);
		}
		param.setValues(updatedValues);
		changed = true;
		close();
	}
	@FXML
	public void doCancel() {
		changed = false;
		close();
	}
	private void addNewDisplayValue(String initValue) {
		EditDisplayParameter displayParam = new EditDisplayParameter(initValue, 
				param.getSubs(), resources.getString("paramedit.btnTxt"), resources.getString("paramedit.browseBtnTxt"));
		displayParam.getInsertBtn().setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				String subValue = (String) getFromScene(PladipusScene.SUB_CHOICE, param.getSubs());
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
		valueList.getChildren().add(displayParam.getBox());
	}
	private void ensureOneField() {
		if (displayValues.isEmpty()) addNewDisplayValue(null);
	}
	
	@Override
	public Object returnObject() {
		return changed;
	}
    @Override
    public void postShow() throws PladipusReportableException {
    	stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				doCancel();
			}
    	});
    }
}
