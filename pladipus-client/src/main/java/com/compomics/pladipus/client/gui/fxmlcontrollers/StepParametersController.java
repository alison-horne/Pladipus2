package com.compomics.pladipus.client.gui.fxmlcontrollers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import com.compomics.pladipus.client.gui.FxmlController;
import com.compomics.pladipus.client.gui.model.GuiSubstitutions;
import com.compomics.pladipus.client.gui.model.StepParameterGui;
import com.compomics.pladipus.client.gui.model.PladipusScene;
import com.compomics.pladipus.client.gui.model.WorkflowGui;
import com.compomics.pladipus.client.gui.model.WorkflowGuiStep;
import com.compomics.pladipus.model.parameters.InputParameter;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.paint.Color;
import javafx.scene.text.TextFlow;
import javafx.stage.WindowEvent;

public class StepParametersController extends FxmlController {

	@FXML
	private Label stepIdLabel;
	@FXML
	private Label toolLabel;
	@FXML
	private Button applyBtn;
	@FXML
	private Button removeLinkBtn;
    @FXML
    private ResourceBundle resources;
    @FXML
    private TreeTableView<StepParameterGui> paramTable;
    @FXML
    private TreeTableColumn<StepParameterGui, CheckBox> perRunColumn;
    @FXML
    private TreeTableColumn<StepParameterGui, InputParameter> paramNameColumn;
    @FXML
    private TreeTableColumn<StepParameterGui, TextFlow> paramValueColumn;
    @FXML
    private TreeTableColumn<StepParameterGui, Button> editButtonColumn;
    private List<StepParameterGui> allParams = new ArrayList<StepParameterGui>();
    private String newStepName;
    private final String ID_REGEX = "[a-zA-Z_][a-zA-Z0-9_-]*";
	private WorkflowGuiStep step;
	private WorkflowGui workflowGui;
	private boolean delete = false;
	
	@FXML
	public void initialize() {
		paramNameColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().inputParamProperty());
		perRunColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().checkBoxProperty());
		paramValueColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().displayValueProperty());
		editButtonColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().editButtonProperty());
		paramNameColumn.setCellFactory(cell -> new TreeTableCell<StepParameterGui, InputParameter>() {
			@Override
			protected void updateItem(InputParameter item, boolean empty) {
				super.updateItem(item, empty);
				if (item != null) {
					setText(item.getParamName());
					setTooltip(new Tooltip(item.getDescription()));
				} else {
					setText(null);
					setTooltip(null);
				}
			}
		});
	}
	
	@FXML
	public void handleApply() {
		if (changesMade()) applyChanges();
		close();
	}
	
	@FXML
	public void handleCancel() {
		if (cancelChanges()) close();
	}
	
	@FXML
	public void handleRename() {
		String oldName = (newStepName != null)? newStepName : step.getStepId();
		String header = resources.getString("stepparam.newNameHeader");
		boolean finished = false;
		while (!finished) {
			String rename = guiControl.getText(stage, header, oldName);
			if (rename == null || rename.isEmpty() || rename.equalsIgnoreCase(oldName)) {
				finished = true;
			} else {
				if (!Pattern.matches(ID_REGEX, rename)) {
					error("stepparam.nameFormat");
				} else if (workflowGui.stepIdExists(rename)) {
					error("stepparam.nameNotUnique");
				} else {
					finished = true;
					newStepName = rename;
					stepIdLabel.setText(resources.getString("stepparam.titleLabel") + ": " + newStepName);
				}
			}
		}
	}
	
	@FXML
	public void handleDelete() {
		if (alert("stepDelete")) {
			delete = true;
			close();
			workflowGui.deleteStep(step);
		}
	}
	
	@FXML
	public void handleRemoveLinks() {
		if (step.getStepLinkNoOutputs().size() > 1) {
			if ((boolean) getFromScene(PladipusScene.REMOVE_LINKS, step)) workflowGui.refresh();
		} else {
			step.clearStepLinkNoOutput();
		}
		workflowGui.refresh();
		removeBtnEnabled();
	}
	
	@Override
	public void setup(Object... objs) {
		step = (WorkflowGuiStep) objs[0];
		workflowGui = (WorkflowGui) objs[1];
		stepIdLabel.setText(resources.getString("stepparam.titleLabel") + ": " + step.getStepId());
		toolLabel.setText(resources.getString("stepparam.toolLabel") + ": " + step.getToolName());
		setData();
		removeBtnEnabled();
	}
	
	private void removeBtnEnabled() {
		if (step.getStepLinkNoOutputs().isEmpty()) {
			removeLinkBtn.setDisable(true);
			removeLinkBtn.setVisible(false);
		}
	}
	
	@Override
	public void postShow() {
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				if (!delete) {
					if (!cancelChanges()) {
						event.consume();
					}
				}
			}
    	});
	}
	
	private boolean changesMade() {
		for (StepParameterGui param: allParams) {
			if (param.valueChanged()) return true;
		}
		if (newStepName != null) return true;
		return false;
	}
	
	private boolean cancelChanges() {
		if (!changesMade()) return true;
		if (alert("paramDiscard")) return true;
		return false;
	}
	
	private void applyChanges() {
		if (newStepName != null) {
			workflowGui.updateIdLinks(step.getStepId(), newStepName);
			step.setStepId(newStepName);
		}
		for (StepParameterGui param: allParams) {
			if (param.valueChanged()) {
				if (param.isPerRun()) {
					step.perRunParameter(param.getInputParam().getParamName());
				} else if (param.getValues().isEmpty()) {
					step.removeParameter(param.getInputParam().getParamName());
				} else {
					step.updateParameter(param.getInputParam().getParamName(), param.getValues());
				}
			}
		}
		workflowGui.refresh();
	}
	
	private void setData() {
		GuiSubstitutions subs = getSubstitutions();
		StepParameterGui mandHeader = new StepParameterGui(resources.getString("stepparam.mandatory"), resources.getString("stepparam.descMand"));
		StepParameterGui optHeader = new StepParameterGui(resources.getString("stepparam.optional"), resources.getString("stepparam.descOpt"));
		TreeItem<StepParameterGui> mandItem = new TreeItem<StepParameterGui>(mandHeader);
		TreeItem<StepParameterGui> optItem = new TreeItem<StepParameterGui>(optHeader);
		for (InputParameter ip : step.getToolInfo().getMandatoryParametersNoDefault()) {
			mandItem.getChildren().add(new TreeItem<StepParameterGui>(initParameter(ip, subs)));
		}
		for (InputParameter ip: step.getToolInfo().getMandatoryParametersWithDefault()) {
			mandItem.getChildren().add(new TreeItem<StepParameterGui>(initParameter(ip, subs)));
		}
		for (InputParameter ip : step.getToolInfo().getOptionalParametersNoDefault()) {
			optItem.getChildren().add(new TreeItem<StepParameterGui>(initParameter(ip, subs)));
		}
		for (InputParameter ip: step.getToolInfo().getOptionalParametersWithDefault()) {
			optItem.getChildren().add(new TreeItem<StepParameterGui>(initParameter(ip, subs)));
		}

		TreeItem<StepParameterGui> root = new TreeItem<StepParameterGui>();
		root.getChildren().add(mandItem);
		root.getChildren().add(optItem);
		paramTable.setRoot(root);
		paramTable.setShowRoot(false);
	}
	
	private StepParameterGui initParameter(InputParameter inputParam, GuiSubstitutions subs) {
		List<String> values = null;
		boolean perRun = false;
		if (step.getParamValues(inputParam.getParamName()) != null) {
			values = step.getParamValues(inputParam.getParamName());
			if (values.isEmpty()) perRun = true;
		} else {
			if (inputParam.hasDefaultValue()) {
				values = Arrays.asList(inputParam.getDefaultValue().split(","));
			}
		}
		StepParameterGui paramGui = new StepParameterGui(inputParam, values, perRun, subs);
		setupButton(paramGui);
		allParams.add(paramGui);
		return paramGui;
	}
	
	private void setupButton(StepParameterGui param) {
		if (param.getEditButton() != null) {
			Button btn = param.getEditButton();
			btn.setText(resources.getString("stepparam.editParamBtn"));
	    	buttonLook(btn, param.invalidValueProperty().get());
	    	param.invalidValueProperty().addListener((observable, oldValue, newValue) -> {
	    		buttonLook(btn, newValue);
	    	});
			btn.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent arg0) {
					getFromScene(PladipusScene.PARAM_EDIT, param);
				}
			});
		}
	}
	
    private void buttonLook(Button btn, boolean invalid) {
        if(invalid) {
    		btn.setTextFill(Color.RED);
			btn.setTooltip(new Tooltip(resources.getString("stepparam.invalidSub")));
        }
        else {
    		btn.setTextFill(Color.BLACK);
    		btn.setTooltip(null);
        }
    }
	
	private GuiSubstitutions getSubstitutions() {
		GuiSubstitutions subs = new GuiSubstitutions(guiControl.getUserDefaults(), workflowGui.getGlobals());
		for (WorkflowGuiStep wfStep : workflowGui.getGuiSteps()) {
			if (wfStep != step) {
				subs.addStep(wfStep);
			}
		}
		return subs;
	}
}
