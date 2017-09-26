package com.compomics.pladipus.client.gui.model;

import java.util.List;

import com.compomics.pladipus.model.parameters.InputParameter;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class StepParameterGui extends ParameterGui {
	
	private boolean header = false;
	private ObjectProperty<InputParameter> inputParam;
	
	public StepParameterGui(String headerName, String description) {
		super();
		header = true;
		inputParam = new SimpleObjectProperty<InputParameter>(new InputParameter(headerName, description, false));
	}
	
	public StepParameterGui(InputParameter inputParam, List<String> values, boolean perRun, GuiSubstitutions subs) {
		super(values, perRun, subs);
		this.inputParam = new SimpleObjectProperty<InputParameter>(inputParam);
	}

	public void setInputParam(InputParameter inputParam) {
		this.inputParam.set(inputParam);
	}
	public InputParameter getInputParam() {
		return inputParam.get();
	}
	public ObjectProperty<InputParameter> inputParamProperty() {
		return inputParam;
	}
	
	public boolean isHeader() {
		return header;
	}
}
