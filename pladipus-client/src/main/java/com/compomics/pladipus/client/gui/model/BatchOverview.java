package com.compomics.pladipus.client.gui.model;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class BatchGui {
	StringProperty batchName;
	SimpleListProperty<RunGui> runs = new SimpleListProperty<RunGui>(FXCollections.observableArrayList());
	IntegerProperty runSize;
	
	public BatchGui() {
		this(null);
	}
	
	public BatchGui(String batchName) {
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
	
	public ObservableList<RunGui> getRuns() {
		return runs;
	}
	public void addRun(RunGui run) {
		runs.add(run);
	}
	public IntegerProperty runSizeProperty() {
		return runSize;
	}
}
