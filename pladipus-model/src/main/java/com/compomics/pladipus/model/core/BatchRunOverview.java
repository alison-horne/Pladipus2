package com.compomics.pladipus.model.core;

import java.util.List;
import java.util.Map;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

public class BatchRunOverview {
	
	StringProperty name;
	long id;
	Map<String, List<String>> runParameters; //TODO get parameter map 
	ObservableList<RunOverview> runs;
	
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
