package com.compomics.pladipus.client.gui.fxmlcontrollers;

import java.util.ResourceBundle;

import com.compomics.pladipus.client.gui.FxmlController;
import com.compomics.pladipus.model.core.DefaultOverview;
import com.compomics.pladipus.client.gui.model.GuiSubstitutions;
import com.compomics.pladipus.client.gui.model.GlobalParameterGui;
import com.compomics.pladipus.client.gui.model.PladipusScene;
import com.compomics.pladipus.client.gui.model.StepOutput;
import com.compomics.pladipus.shared.PladipusReportableException;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.WindowEvent;

public class SubChoiceController extends FxmlController {

	private GuiSubstitutions subs;
	private String subValue;
    @FXML
    private ResourceBundle resources;
	@FXML
	private Button addSubBtn;
	@FXML
	private TableView<TableContent> valueTable;
	@FXML
	private TableColumn<TableContent, String> col1;
	@FXML
	private TableColumn<TableContent, TextFlow> col2;
	@FXML
	private RadioButton radioGlobal;
	@FXML
	private RadioButton radioStep;
	@FXML
	private RadioButton radioDefault;
	private ObservableList<TableContent> contents = FXCollections.observableArrayList();
	private ToggleGroup toggle;
	private String ADD_DEFAULT_BTN, ADD_GLOBAL_BTN, NAME_COL, VAL_COL, STEPNAME_COL, STEPVAL_COL;
	
	@FXML
	public void initialize() {
		ADD_DEFAULT_BTN = resources.getString("subchoice.buttonDefault");
		ADD_GLOBAL_BTN = resources.getString("subchoice.buttonGlobal");
		NAME_COL = resources.getString("subchoice.tableName");
		VAL_COL = resources.getString("subchoice.tableValue");
		STEPNAME_COL = resources.getString("subchoice.stepName");
		STEPVAL_COL = resources.getString("subchoice.stepValue");
		
		addSubBtnActive(false);
		
		col1.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		col2.setCellValueFactory(cellData -> cellData.getValue().valueProperty());
		valueTable.setItems(contents);
		
    	toggle = new ToggleGroup();
    	radioGlobal.setToggleGroup(toggle);
    	radioStep.setToggleGroup(toggle);
    	radioDefault.setToggleGroup(toggle);
        toggle.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
				if (newValue.getToggleGroup().getSelectedToggle().equals(radioGlobal)) {
					setupGlobal();
				} else if (newValue.getToggleGroup().getSelectedToggle().equals(radioStep)) {
					setupStep();
				} else {
					setupDefault();
				}
			}
		});
	}
	
	private void addSubBtnActive(boolean active) {
		addSubBtn.setVisible(active);
		addSubBtn.setDisable(!active);
	}
	
	private void setupGlobal() {
		addSubBtn.setText(ADD_GLOBAL_BTN);
		addSubBtnActive(true);
		col1.setText(NAME_COL);
		col2.setText(VAL_COL);
		contents.clear();
		for (GlobalParameterGui global: subs.getGlobals()) {
			contents.add(new TableContent(global));
		}
	}
	
	private void setupStep() {
		addSubBtnActive(false);
		col1.setText(STEPNAME_COL);
		col2.setText(STEPVAL_COL);
		contents.clear();
		for (StepOutput step: subs.getStepOutputs()) {
			contents.add(new TableContent(step));
		}
	}
	
	private void setupDefault() {
		addSubBtn.setText(ADD_DEFAULT_BTN);
		addSubBtnActive(true);
		col1.setText(NAME_COL);
		col2.setText(VAL_COL);
		contents.clear();
		for (DefaultOverview def: subs.getDefaults()) {
			contents.add(new TableContent(def));
		}
	}
	
	@Override
	public void setup(Object obj) {
		subs = (GuiSubstitutions) obj;
	}
	
	@FXML
	public void handleAccept() {
		TableContent selected = valueTable.getSelectionModel().getSelectedItem();
		if (selected != null) {
			subValue = selected.getFullName();
			stage.close();
		} else {
			error(resources.getString("subchoice.noSelection"));
		}
	}
	
	@FXML
	public void handleCancel() {
		subValue = null;
		stage.close();
	}
	
	@FXML
	public void handleAddSub() {
		if (toggle.getSelectedToggle().equals(radioGlobal)) {
			GlobalParameterGui glob = (GlobalParameterGui) getFromScene(PladipusScene.NEW_GLOBAL, subs.getGlobals());
			if (glob != null) {
				TableContent newGlob = new TableContent(glob);
				contents.add(newGlob);
				valueTable.getSelectionModel().select(newGlob);
			}
		} else {
			DefaultOverview def = (DefaultOverview) getFromScene(PladipusScene.NEW_DEFAULT);
			if (def != null) {
				TableContent newDef = new TableContent(def);
				contents.add(newDef);
				valueTable.getSelectionModel().select(newDef);
			}
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
	public Object returnObject() {
		return subValue;
	}
	
	class TableContent {
		StringProperty name;
		ObjectProperty<TextFlow> value;
		Object obj;
		
		public TableContent(Object obj) {
			this.obj = obj;
			TextFlow tf;
			if (obj instanceof GlobalParameterGui) {
				name = ((GlobalParameterGui)obj).globalNameProperty();
				tf = ((GlobalParameterGui)obj).getDisplayValue();
			} else if (obj instanceof DefaultOverview) {
				name = ((DefaultOverview)obj).nameProperty();
				tf = new TextFlow(new Text(((DefaultOverview)obj).valueProperty().get()));
			} else {
				name = ((StepOutput)obj).stepIdProperty();
				tf = new TextFlow(new Text(((StepOutput)obj).valueProperty().get()));
			}
			value = new SimpleObjectProperty<TextFlow>(tf);
		}
		
		public StringProperty nameProperty() {
			return name;
		}
		public ObjectProperty<TextFlow> valueProperty() {
			return value;
		}
		public String getFullName() {
			if (obj instanceof GlobalParameterGui) {
				return ((GlobalParameterGui)obj).getGlobalFullName();
			} else if (obj instanceof DefaultOverview) {
				return ((DefaultOverview)obj).getFullDefaultName();
			} 
			return ((StepOutput)obj).toString();
		}
	}
}
