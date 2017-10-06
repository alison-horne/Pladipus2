package com.compomics.pladipus.model.core;

import java.util.List;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class BatchOverview {
	StringProperty batchName;
	long id;
	SimpleListProperty<BatchRunOverview> runs;
	IntegerProperty runSize;
	
	public BatchOverview() {
		this(null, -1);
	}
	
	public BatchOverview(String batchName, long id) {
		this.batchName = new SimpleStringProperty(batchName);
		this.id = id;
		this.runs = new SimpleListProperty<BatchRunOverview>(FXCollections.observableArrayList());
		this.runSize = new SimpleIntegerProperty();
		runSize.bind(runs.sizeProperty());
	}
	
	public String getBatchName() {
		return batchName.get();
	}
	public void setBatchName(String batchName) {
		this.batchName.set(batchName);
	}
	public StringProperty batchNameProperty() {
		return batchName;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public ObservableList<BatchRunOverview> getRuns() {
		return runs.get();
	}
	public void setRuns(List<BatchRunOverview> runs) {
		this.runs.clear();
		if (runs != null) this.runs.addAll(runs);
	}
	public void addRun(BatchRunOverview run) {
		runs.add(run);
	}
	public IntegerProperty runSizeProperty() {
		return runSize;
	}
}
