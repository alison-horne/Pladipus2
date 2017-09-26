package com.compomics.pladipus.client.gui.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class WorkflowOverview {
	private StringProperty name;
	private String xml;
	private ObservableList<BatchOverview> batches = FXCollections.observableArrayList();
	
	public WorkflowOverview() {
		this(null, null);
	}
	
	public WorkflowOverview(String name, String xml) {
		this.name = new SimpleStringProperty(name);
		this.xml = xml;
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
	public void setXml(String xml) {
		this.xml = xml;
	}
	public String getXml() {
		return xml;
	}
    public ObservableList<BatchOverview> getBatches() {
    	return batches;
    }
    public void addBatch(BatchOverview batch) {
    	batches.add(batch);
    }
}
