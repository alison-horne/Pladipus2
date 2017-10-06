package com.compomics.pladipus.client.gui.fxmlcontrollers;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.compomics.pladipus.client.gui.FxmlController;
import com.compomics.pladipus.client.gui.model.EditDisplayParameter;
import com.compomics.pladipus.client.gui.model.GlobalParameterGui;
import com.compomics.pladipus.client.gui.model.GuiSubstitutions;
import com.compomics.pladipus.client.gui.model.PladipusScene;
import com.compomics.pladipus.model.core.WorkflowOverview;
import com.compomics.pladipus.model.persist.Parameter;
import com.compomics.pladipus.shared.PladipusReportableException;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.WindowEvent;

public class BatchManualController extends FxmlController {
	
	@FXML
	private TableView<List<EditDisplayParameter>> tableView;
	@FXML
	private Button insertSubBtn;
	@FXML
	private Button browseBtn;
	@FXML
	private Button saveBtn, cancelBtn, addRowBtn, delRowBtn;
	@FXML
	private Label validatingLbl;
	@FXML
	private ResourceBundle resources;
	
	private WorkflowOverview wfo;
	private ObjectProperty<EditDisplayParameter> selectedCell;
	private ObservableList<GlobalParameterGui> globalSubs;
	private String csvString;
	private double colBuffer = 8.0; // TODO vary with style
	private List<String> idList = new ArrayList<String>();
	
    @FXML
    public void initialize() {
    	disableSubButtons(true);
		selectedCell = new SimpleObjectProperty<EditDisplayParameter>(null);
		selectedCell.addListener(new ChangeListener<EditDisplayParameter>() {
			@Override
			public void changed(ObservableValue<? extends EditDisplayParameter> observable,
					EditDisplayParameter oldValue, EditDisplayParameter newValue) {
				disableSubButtons(newValue == null);
			}
		});
        tableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				selectedCell.set(null);
			}       	
        });
    }
    
    private void disableSubButtons(boolean disable) {
    	insertSubBtn.setDisable(disable);
    	browseBtn.setDisable(disable);
    }
    private void disableAllButtons(boolean disable) {
    	saveBtn.setDisable(disable);
    	cancelBtn.setDisable(disable);
    	addRowBtn.setDisable(disable);
    	delRowBtn.setDisable(disable);
    	if (disable || (selectedCell.get() != null)) {
    		disableSubButtons(disable);
    	}
    }
    
    private void addRow(String[] initValues) {
		List<EditDisplayParameter> newRow = new ArrayList<EditDisplayParameter>();
		for (int i = 0; i < wfo.getHeaders().size(); i++) {
			String initVal = null;
			if (initValues != null && initValues.length > i) initVal = initValues[i];
			if (i == 0) {
				newRow.add(new EditDisplayParameter(initVal, new GuiSubstitutions(FXCollections.observableArrayList()), selectedCell, true));
			} else {
				if (wfo.getHeaders().get(i).contains(".")) {
					newRow.add(new EditDisplayParameter(initVal, new GuiSubstitutions(guiControl.getUserDefaults(), globalSubs), selectedCell));
				} else {
					newRow.add(new EditDisplayParameter(initVal, new GuiSubstitutions(guiControl.getUserDefaults()), selectedCell));
				}
			}
		}
		tableView.getItems().add(newRow);
    }
    
    private void initTable() {
		for (int i = 0; i < wfo.getHeaders().size(); i++) {
		    final int finalIdx = i;
		    TableColumn<List<EditDisplayParameter>, HBox> column = new TableColumn<>(wfo.getHeaders().get(finalIdx));
		    column.setMinWidth(500.0);
		    column.setSortable(false);
		    column.setCellValueFactory(param -> param.getValue().get(finalIdx).textStripProperty());
		    column.setEditable(false);
		    column.setResizable(false);
		    column.setCellFactory(cell -> new TableCell<List<EditDisplayParameter>, HBox>() {
				@Override
				protected void updateItem(HBox box, boolean empty) {
					super.updateItem(box, empty);
					if (box != null) {
						setGraphic(box);
						setText(null);
						box.widthProperty().addListener(new ChangeListener<Number>() {
							@Override
							public void changed(ObservableValue<? extends Number> observable, Number oldValue,
									Number newValue) {
								if (newValue.doubleValue() + colBuffer > column.getWidth()) {
									column.setPrefWidth(newValue.doubleValue() + colBuffer);
								}
							}
							
						});
					}
					else {
						setText(null);
						setGraphic(null);
					}
				}
		    });
		    tableView.getColumns().add(column);
		}
    }
    
    @FXML
    public void handleAddRow() {
    	addRow(null);
    }
    
    @FXML
    public void handleDeleteRow() {
		if (tableView.getSelectionModel().getSelectedItem() != null) {
			tableView.getItems().remove(tableView.getSelectionModel().getSelectedItem());
		}
    }
    
    @FXML
    public void handleInsertSub() {
    	EditDisplayParameter param = selectedCell.get();
    	if (param != null) {
    		String subValue = (String) getFromScene(PladipusScene.SUB_CHOICE, param.getSubs(), false);
    		if (subValue != null) param.insertSub(subValue);
    	}
    }
    
    @FXML
    public void handleBrowse() {
    	String fileDirPath = guiControl.getFileDirPath(stage);
    	if (fileDirPath != null && selectedCell.get() != null) selectedCell.get().insertFileDir(fileDirPath);
    }
    
    @FXML
    public void handleTableToString() {
    	if (!tableView.getItems().isEmpty()) {
	    	validatingLbl.setText(resources.getString("batchmanual.validating"));
	    	disableAllButtons(true);
	    	new Thread(validateTask()).start();
    	}
    }
    
    @FXML
    public void handleCancel() {
    	if (alert("batchmanualCancel")) {
	    	csvString = null;
	    	close();
    	}
    }
    
    @Override
    public void postShow() throws PladipusReportableException {
    	stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				handleCancel();
			}
    	});
    }
    
    @Override
    public void setup(Object...objs) throws PladipusReportableException {
    	wfo = (WorkflowOverview) objs[0];
    	getGlobalSubs();
    	initTable();
    	if (objs.length > 0 && objs[1] != null) {
    		String initValues = (String) objs[1];
    		String[] rows = initValues.split("\n");
    		if (rows.length > 1) {
    			for (int i = 1; i < rows.length; i++) {
    				String row = rows[i];
    				if (row.startsWith("\"") && row.endsWith("\"")){
    					row = row.substring(1, row.length() - 1);
    				}
    				addRow(row.split("\",\""));
    			}
    		}
    	}
    	if (tableView.getItems().isEmpty()) {
    		addRow(null);
    	}
    	tableView.getSelectionModel().selectFirst();
    }
    
    private void getGlobalSubs() {
    	globalSubs = FXCollections.observableArrayList();
    	try {
			List<Parameter> globs = guiControl.getWorkflowFromXml(wfo.getXml()).getGlobal().getParameters().getParameter();
    		for (Parameter globParam: globs) {
    			globalSubs.add(new GlobalParameterGui(globParam.getName(), globParam.getValue(), 
    					globParam.getValue().isEmpty(), new GuiSubstitutions(guiControl.getUserDefaults())));
    		}
		} catch (PladipusReportableException e) {
			// Ignore - just don't allow global subs
		}
    }
    
	@Override
	public Object returnObject() {
		return csvString;
	}
	
	private Task<Void> validateTask() {
		Task<Void> validateTask = new Task<Void>() {
			@Override
			protected Void call() throws PladipusReportableException {
				idList.clear();
				StringBuilder builder = new StringBuilder();
		    	builder.append(getHeaderString());
		    	for (List<EditDisplayParameter> row: tableView.getItems()) {
		    		builder.append("\n");
		    		builder.append(validateRow(row));
		    	}
		    	csvString = builder.toString();
				return null;
			}
		};
	    validateTask.setOnSucceeded((WorkerStateEvent event) -> {
	        close();
	    });
	    validateTask.setOnFailed((WorkerStateEvent event) -> {
	    	disableAllButtons(false);
	    	validatingLbl.setText("");
	    	csvString = null;
	    	error(validateTask.getException().getMessage());
	    });
	    return validateTask;
	}
	
	private String getHeaderString() {
		StringBuilder builder = new StringBuilder();
		for (String header: wfo.getHeaders()) {
			builder.append(quoteString(header));
			builder.append(",");
		}
		builder.deleteCharAt(builder.length() - 1);
		return builder.toString();
	}
	private String validateRow(List<EditDisplayParameter> row) throws PladipusReportableException {
		if (row.size() != wfo.getHeaders().size()) throw new PladipusReportableException(resources.getString("batchmanual.validationError"));
		StringBuilder builder = new StringBuilder();
		String rowId = null;
		for (int i = 0; i < row.size(); i++) {
			if (!row.get(i).isValid()) {
				String errorStr = resources.getString("batchmanual.invalidSub") + " " + row.get(i).getValue() +
						"\n" + resources.getString("batchmanual.forValue") + " " + wfo.getHeaders().get(i);
				if (rowId != null) errorStr += "\n" + resources.getString("batchmanual.forRowId") + " " + rowId;
				throw new PladipusReportableException(errorStr);
			}
			if (i == 0) {
				rowId = row.get(i).getValue().toUpperCase();
				if (idList.contains(rowId)) throw new PladipusReportableException(resources.getString("batchmanual.duplicateId") + " " + rowId);
				idList.add(rowId);
			}
			if (row.get(i).getValue().trim().isEmpty()) {
				String errorString = resources.getString("batchmanual.missingValue") + " " + wfo.getHeaders().get(i);
				if (rowId != null) errorString += "\n" + resources.getString("batchmanual.forRowId") + " " + rowId;
				throw new PladipusReportableException(errorString);
			}
			if (row.get(i).getValue().contains("\n") || row.get(i).getValue().contains("\"")) {
				String errorString = resources.getString("batchmanual.newlineValue") + " " + wfo.getHeaders().get(i);
				if (rowId != null) errorString += "\n" + resources.getString("batchmanual.forRowId") + " " + rowId;
				throw new PladipusReportableException(errorString);
			}
			builder.append(quoteString(row.get(i).getValue()));
			builder.append(",");
		}
		builder.deleteCharAt(builder.length() - 1);
		return builder.toString();
	}
	private String quoteString(String str) {
		return "\"" + str.trim() + "\"";
	}
}
