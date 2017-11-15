package com.compomics.pladipus.client.gui.fxmlcontrollers;

import java.util.Map;
import java.util.ResourceBundle;

import com.compomics.pladipus.client.gui.FxmlController;
import com.compomics.pladipus.model.core.BatchRunOverview;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class BatchParamController extends FxmlController {

	@FXML
	private Label runLabel;
	@FXML
	private TableView<Map.Entry<String, String>> paramTable;
	@FXML
	private TableColumn<Map.Entry<String, String>, String> nameColumn;
	@FXML
	private TableColumn<Map.Entry<String, String>, String> valueColumn;
	@FXML
	private ResourceBundle resources;
	
	@FXML
	public void initialize() {
		nameColumn.setCellValueFactory((p)->{
	        return new SimpleStringProperty(p.getValue().getKey());
		});
		valueColumn.setCellValueFactory((p)->{
	        return new SimpleStringProperty(p.getValue().getValue());
		});
	}
	
	@FXML
	public void handleOK() {
		close();
	}
	
	@Override
	public void setup(Object obj) {
		BatchRunOverview bro = (BatchRunOverview) obj;
		runLabel.setText(resources.getString("batchparam.subheader") + " " + bro.getName());
		paramTable.getItems().addAll(bro.getParameters().entrySet());
	}
}
