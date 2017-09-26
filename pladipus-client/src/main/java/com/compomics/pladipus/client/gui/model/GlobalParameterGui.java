package com.compomics.pladipus.client.gui.model;

import java.util.List;

import com.compomics.pladipus.model.parameters.Substitution;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class GlobalParameterGui extends ParameterGui {
	
	private StringProperty globalName;
	
	public GlobalParameterGui(String name, List<String> values, boolean perRun, GuiSubstitutions subs) {
		super(values, perRun, subs);
		globalName = new SimpleStringProperty(name);
	}
	
	public void setGlobalName(String globalName) {
		this.globalName.set(globalName);
	}
	public String getGlobalName() {
		return globalName.get();
	}
	public StringProperty globalNameProperty() {
		return globalName;
	}
	
    public String getGlobalFullName() {
    	if (globalName != null && !getGlobalName().isEmpty()) {
    		return Substitution.getPrefix() + Substitution.getGlobal() + "." + getGlobalName() + Substitution.getEnd();
    	}
    	return null;
    }
    
    @Override
    public void setValues(List<String> values) {
    	super.setValues(values);
    	// Refresh string property so that if value is invalid, table tooltip is shown
    	String name = getGlobalName();
    	setGlobalName(null);
    	setGlobalName(name);
    }
}
