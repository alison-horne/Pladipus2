package com.compomics.pladipus.model.core;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class BatchRunOverview {
	
	StringProperty name;
	long id;
	Map<String, String> parameters;
	
	public BatchRunOverview() {
		this(null, -1);
	}
	
	public BatchRunOverview(String name, long id) {
		this.name = new SimpleStringProperty(name);
		this.id = id;
		parameters = new HashMap<String, String>();
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
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Map<String, String> getParameters() {
		return parameters;
	}
	public void setParameters(Map<String, String> parameters) {
		this.parameters.clear();
		if (parameters != null) {
			this.parameters.putAll(parameters);
		}
	}
	public void addParameter(String key, String value) {
		parameters.put(key, value);
	}
}
