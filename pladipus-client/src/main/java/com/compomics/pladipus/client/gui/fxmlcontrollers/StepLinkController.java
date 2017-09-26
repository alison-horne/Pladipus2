package com.compomics.pladipus.client.gui.fxmlcontrollers;

import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import com.compomics.pladipus.client.gui.FxmlController;
import com.compomics.pladipus.client.gui.model.GuiSubstitutions;
import com.compomics.pladipus.client.gui.model.PladipusScene;
import com.compomics.pladipus.client.gui.model.StepLink;
import com.compomics.pladipus.client.gui.model.StepParameterGui;
import com.compomics.pladipus.client.gui.model.WorkflowGui;
import com.compomics.pladipus.client.gui.model.WorkflowGuiStep;
import com.compomics.pladipus.model.parameters.InputParameter;
import com.compomics.pladipus.model.parameters.Substitution;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.WindowEvent;
import javafx.util.StringConverter;

public class StepLinkController extends FxmlController {

	@FXML
	private ChoiceBox<String> stepOutChoice;
	@FXML
	private ChoiceBox<InputParameter> stepInChoice;
	@FXML
	private Label stepOutLabel;
	@FXML
	private Label stepInLabel;
	@FXML
	private TableView<TableContent> linksTable;
	@FXML
	private TableColumn<TableContent, String> outColumn;
	@FXML
	private TableColumn<TableContent, String> inColumn;
	@FXML
	private TableColumn<TableContent, Button> editBtnColumn;
	@FXML
	private Button saveBtn;
	@FXML
	private ResourceBundle resources;
	private StepLink link;
	private WorkflowGui wfGui;
	private GuiSubstitutions subs;
	private Set<StepParameterGui> paramGuis = new HashSet<StepParameterGui>();
	private boolean changed = false;
	private ObservableList<TableContent> contentList = FXCollections.observableArrayList();
	
	@FXML
	public void initialize() {
    	stepInChoice.setConverter(new StringConverter<InputParameter>() {
			@Override
			public InputParameter fromString(String string) {
				return null;
			}
			@Override
			public String toString(InputParameter ip) {
				return ip.getParamName();
			}   		
    	});
    	outColumn.setCellValueFactory(cellData -> cellData.getValue().outputProperty());
    	inColumn.setCellValueFactory(cellData -> cellData.getValue().inParamProperty());
    	editBtnColumn.setCellValueFactory(cellData -> cellData.getValue().editBtnProperty());
    	linksTable.setItems(contentList);
	}
	
	@Override
	public void setup(Object...objs) {
		link = (StepLink) objs[0];
		wfGui = (WorkflowGui) objs[1];
		stepOutChoice.getItems().addAll(link.getStartStep().getToolInfo().getOutputs());
		stepInChoice.getItems().addAll(link.getEndStep().getToolInfo().getParameters());
		stepOutLabel.setText("Output from step " + link.getStartStep().getStepId());
		stepInLabel.setText("Step " + link.getEndStep().getStepId() + " input parameter");
		initExistingLinks();
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				handleCancel();
			}
    	});
	}
	
	@FXML
	public void handleCreateLink() {
		String out = stepOutChoice.getSelectionModel().getSelectedItem();
		InputParameter in = stepInChoice.getSelectionModel().getSelectedItem();
		if (out == null || in == null) {
			error(resources.getString("steplink.alertNoSelection"));
		} else {
			StepParameterGui param = getParamGui(in);
			if ((boolean) getFromScene(PladipusScene.PARAM_EDIT, param, getFullOut(out))) {
				changed = true;
				paramGuis.add(param); 
				refreshTable();
			}
		}
	}
	@FXML
	public void handleSave() {
		if (changed) applyChanges();
		close();
	}
	@FXML
	public void handleCancel() {
    	if (!(changed && !alert("workflowAbandon"))) {
    		close();
    	} 
	}
	
	private void applyChanges() {
		for (StepParameterGui param: paramGuis) {
			if (param.valueChanged()) {
				if (param.getValues().isEmpty()) {
					link.getEndStep().removeParameter(param.getInputParam().getParamName());
				} else {
					link.getEndStep().updateParameter(param.getInputParam().getParamName(), param.getValues());
				}
			}
		}
	}
	
	private void initExistingLinks() {
		for (InputParameter param: link.getEndStep().getParamsContainingSub(Substitution.getPrefix() + link.getStartStep().getStepId())) {
			paramGuis.add(getParamGui(param));
		}
		refreshTable();
	}
	
	private void refreshTable() {
		contentList.clear();
		for (StepParameterGui spg: paramGuis) {
			for (String output: link.getStartStep().getToolInfo().getOutputs()) {
				if (String.join(",", spg.getValues()).contains(getFullOut(output))) {
					contentList.add(new TableContent(output, spg));
				}
			}
		}
	}
	private StepParameterGui getParamGui(InputParameter ip) {
		for (StepParameterGui param: paramGuis) {
			if (param.getInputParam().equals(ip)) {
				return param;
			}
		}
		return initParameter(ip);
	}
	
	private StepParameterGui initParameter(InputParameter ip) {		
		List<String> values = link.getEndStep().getParamValues(ip.getParamName());	
		return new StepParameterGui(ip, values, false, getSubstitutions());
	}
	private GuiSubstitutions getSubstitutions() {
		if (subs == null) {
			subs = new GuiSubstitutions(guiControl.getUserDefaults(), wfGui.getGlobals());
			for (WorkflowGuiStep wfStep : wfGui.getGuiSteps()) {
				if (wfStep != link.getEndStep()) {
					subs.addStep(wfStep);
				}
			}
		}
		return subs;
	}
	private String getFullOut(String out) {
		return Substitution.getPrefix() + link.getStartStep().getStepId() + "." + out + Substitution.getEnd();
	}
	
	class TableContent {
		private StringProperty output;
		private StringProperty inParam;
		private ObjectProperty<Button> editBtn;
		private StepParameterGui paramGui;

		public TableContent(String output, StepParameterGui paramGui) {
			this.output = new SimpleStringProperty(output);
			this.paramGui = paramGui;
			editBtn = new SimpleObjectProperty<Button>(initEditBtn());
			this.inParam = new SimpleStringProperty(paramGui.getInputParam().getParamName());
		}
		
		public StringProperty outputProperty() {
			return output;
		}
		public String getOutput() {
			return output.get();
		}
		public StringProperty inParamProperty() {
			return inParam;
		}
		public String getInParam() {
			return inParam.get();
		}
		public StepParameterGui getParamGui() {
			return paramGui;
		}
		public ObjectProperty<Button> editBtnProperty() {
			return editBtn;
		}
		public Button initEditBtn() {
			Button btn = new Button(resources.getString("steplink.editBtn"));
			btn.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					if ((boolean) getFromScene(PladipusScene.PARAM_EDIT, paramGui)) {
						changed = true;
						refreshTable();
					}
				}				
			});
			return btn;
		}
	}
}
