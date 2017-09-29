package com.compomics.pladipus.model.core;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class BatchOverview {
	StringProperty batchName;
	SimpleListProperty<RunOverview> runs = new SimpleListProperty<RunOverview>(FXCollections.observableArrayList());
	IntegerProperty runSize;
	
	public BatchOverview() {
		this(null);
	}
	
	public BatchOverview(String batchName) {
		this.batchName = new SimpleStringProperty(batchName);
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
	
	public ObservableList<RunOverview> getRuns() {
		return runs;
	}
	public void addRun(RunOverview run) {
		runs.add(run);
	}
	public IntegerProperty runSizeProperty() {
		return runSize;
	}
}
