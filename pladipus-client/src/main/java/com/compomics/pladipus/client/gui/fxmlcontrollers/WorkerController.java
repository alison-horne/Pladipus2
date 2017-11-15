package com.compomics.pladipus.client.gui.fxmlcontrollers;

import java.util.Map.Entry;
import java.util.ResourceBundle;

import com.compomics.pladipus.client.gui.FxmlController;
import com.compomics.pladipus.model.core.RunStepOverview;
import com.compomics.pladipus.model.core.WorkerRunOverview;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;

public class WorkerController extends FxmlController {

	@FXML
	private Label stepLabel;
	@FXML
	private TreeTableView<TableRow> workerTable;
	@FXML
	private TreeTableColumn<TableRow, String> firstColumn;
	@FXML
	private TreeTableColumn<TableRow, String> secondColumn;
	@FXML
	private ResourceBundle resources;
	private String PROG, ERR, COMPLETE, ERR_MSG;
	
	@FXML
	public void initialize() {
		PROG = resources.getString("worker.inProgress");
		ERR = resources.getString("worker.error");
		COMPLETE = resources.getString("worker.complete");
		ERR_MSG = resources.getString("worker.errorMsg");
		firstColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().keyProperty());
		secondColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().valueProperty());
	}
	
	@FXML
	public void handleOK() {
		close();
	}
	
	@Override
	public void setup(Object obj) {
		RunStepOverview rso = (RunStepOverview) obj;
		stepLabel.setText(rso.getName() + ": " + rso.getTool());
		if (!rso.getWorkers().isEmpty()) {
			TreeItem<TableRow> root = new TreeItem<TableRow>();
			for (WorkerRunOverview wro: rso.getWorkers()) {
				String status = COMPLETE;
				if (wro.isInProgress()) status = PROG;
				if (wro.getErrorMsg() != null && !wro.getErrorMsg().isEmpty()) status = ERR;
				TreeItem<TableRow> workerItem = new TreeItem<TableRow>(new TableRow(wro.getWorkerId(), status));
				workerItem.setExpanded(true);
				for (Entry<String, String> output : wro.getOutputs().entrySet()) {
					workerItem.getChildren().add(new TreeItem<TableRow>(new TableRow(output.getKey(), output.getValue())));
				}
				if (status.equals(ERR)) {
					workerItem.getChildren().add(new TreeItem<TableRow>(new TableRow(ERR_MSG, wro.getErrorMsg())));
				}
				root.getChildren().add(workerItem);
			}
			workerTable.setRoot(root);
			workerTable.setShowRoot(false);
		}
	}
	
	class TableRow {
		StringProperty key;
		StringProperty value;
		public TableRow(String key, String value) {
			this.key = new SimpleStringProperty(key);
			this.value = new SimpleStringProperty(value);
		}
		public StringProperty keyProperty() {
			return key;
		}
		public StringProperty valueProperty() {
			return value;
		}
	}
}
