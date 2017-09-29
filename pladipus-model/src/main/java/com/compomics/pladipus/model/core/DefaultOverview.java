package com.compomics.pladipus.model.core;

import com.compomics.pladipus.model.parameters.Substitution;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DefaultOverview {
	StringProperty name;
	StringProperty value;
	StringProperty type;
	BooleanProperty global;
	
	public DefaultOverview() {
		this(null, null, null, null);
	}
	
	public DefaultOverview(String name, String value, String type, Boolean global) {
		if (type != null && type.isEmpty()) type = null;
		this.name = new SimpleStringProperty(name);
		this.value = new SimpleStringProperty(value);
		this.type = new SimpleStringProperty(type);
		this.global = new SimpleBooleanProperty(global);
	}
	
    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }
    
    public String getValue() {
        return value.get();
    }

    public void setValue(String value) {
        this.value.set(value);
    }

    public StringProperty valueProperty() {
        return value;
    }
    
    public String getType() {
        return type.get();
    }

    public void setType(String type) {
    	if (type != null && type.isEmpty()) type = null;
        this.type.set(type);
    }

    public StringProperty typeProperty() {
        return type;
    }
    
    public boolean getGlobal() {
        return global.get();
    }

    public void setGlobal(boolean global) {
        this.global.set(global);
    }

    public BooleanProperty globalProperty() {
        return global;
    }
    
	public String getFullDefaultName() {
		return Substitution.getPrefix() + Substitution.getDefault() + "." + name.get() + Substitution.getEnd();
	}
}
