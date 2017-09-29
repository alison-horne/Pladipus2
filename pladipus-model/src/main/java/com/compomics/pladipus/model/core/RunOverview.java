package com.compomics.pladipus.model.core;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RunOverview {
	// TODO status from db - to readable string from properties file...
	
	StringProperty name;
	StringProperty status;
	
	public RunOverview() {
		this(null, null);
	}
	
	public RunOverview(String name, String status) {
		this.name = new SimpleStringProperty(name);
		this.status = new SimpleStringProperty(status);
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
	
	public String getStatus() {
		return status.get();
	}
	public void setStatus(String status) {
		this.status.set(status);
	}
	public StringProperty statusProperty() {
		return status;
	}
}
