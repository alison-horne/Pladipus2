package com.compomics.pladipus.client.gui.model;

import com.compomics.pladipus.model.parameters.Substitution;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class StepOutput {
	private StringProperty stepId;
	private StringProperty value;
	
	public StepOutput(String stepId, String value) {
		this.stepId = new SimpleStringProperty(stepId);
		this.value = new SimpleStringProperty(value);
	}
	
	public StringProperty stepIdProperty() {
		return stepId;
	}
	public StringProperty valueProperty() {
		return value;
	}
	
	@Override
	public String toString() {
		return Substitution.getPrefix() + stepId.get() + "." + value.get() + Substitution.getEnd();
	}
}
