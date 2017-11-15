package com.compomics.pladipus.model.core;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class WorkflowOverview {
	private StringProperty name;
	private String xml;
	private List<String> headers;
	private ObservableList<BatchOverview> batches;
	
	public WorkflowOverview() {
		this(null, null);
	}
	
	public WorkflowOverview(String name, String xml) {
		this.name = new SimpleStringProperty(name);
		this.xml = xml;
		headers = new ArrayList<String>();
		this.batches = FXCollections.observableArrayList();
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
	public void setHeaders(List<String> headers) {
		this.headers = headers;
	}
	public List<String> getHeaders() {
		return headers;
	}
	public ObservableList<BatchOverview> getBatches() {
		return batches;
	}
	public void setBatches(List<BatchOverview> batches) {
		this.batches.clear();
		if (batches != null) this.batches.addAll(batches);
	}
	public void addBatch(BatchOverview batch) {
		batches.add(batch);
	}
    public boolean batchExists(String batchName) {
    	for (BatchOverview batch: batches) {
    		if (batch.getBatchName().equalsIgnoreCase(batchName)) return true;
    	}
    	return false;
    }

	public void addReplaceBatch(BatchOverview bo) {
		for (BatchOverview batch: batches) {
			if (batch.getBatchName().equalsIgnoreCase(bo.getBatchName())) {
				batches.remove(batch);
				break;
			}
		}
		batches.add(bo);
	}
	
	public boolean removeBatch(BatchOverview batch) {
		for (BatchOverview bo: batches) {
			if (bo.getId() == batch.getId()) {
				batches.remove(batch);
				return true;
			}
		}
		return false;
	}
}
