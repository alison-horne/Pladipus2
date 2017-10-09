package com.compomics.pladipus.model.core;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class BatchRunOverview {
	
	StringProperty name;
	long id;
	
	public BatchRunOverview() {
		this(null, -1);
	}
	
	public BatchRunOverview(String name, long id) {
		this.name = new SimpleStringProperty(name);
		this.id = id;
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
	
	public long getid() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
}
