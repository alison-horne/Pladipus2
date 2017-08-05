package com.compomics.pladipus.client.gui.fxmlcontrollers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import com.compomics.pladipus.client.gui.FxmlController;
import com.compomics.pladipus.client.gui.model.GuiParameter;
import com.compomics.pladipus.client.gui.model.PladipusScene;
import com.compomics.pladipus.client.gui.model.WorkflowGuiStep;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
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
    private ResourceBundle resources;
    @FXML
    private TreeTableView<GuiParameter> paramTable;
    @FXML
    private TreeTableColumn<GuiParameter, CheckBox> perRunColumn;
    @FXML
    private TreeTableColumn<GuiParameter, String> paramNameColumn;
    @FXML
    private TreeTableColumn<GuiParameter, TextFlow> paramValueColumn;
    @FXML
    private TreeTableColumn<GuiParameter, Button> editButtonColumn;
    
	private WorkflowGuiStep step;
	
	@FXML
	public void initialize() {
		paramNameColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().paramNameProperty());
		perRunColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().checkBoxProperty());
		paramValueColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().valueProperty());
		editButtonColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().editButtonProperty());
	}
	
	@FXML
	public void handleApply() {
		
	}
	
	@FXML
	public void handleCancel() {
		if (cancelChanges()) close();
	}
	
	@FXML
	public void handleDelete() {
		
	}
	
	@Override
	public void setup(Object obj) {
		step = (WorkflowGuiStep) obj;
		stepIdLabel.setText(resources.getString("stepparam.titleLabel") + ": " + step.getStepId());
		toolLabel.setText(resources.getString("stepparam.toolLabel") + ": " + step.getToolName());
		setData();
	}
	
	@Override
	public void postShow() {
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				if (!cancelChanges()) {
					event.consume();
				}
			}
    	});
	}
	
	private boolean cancelChanges() {
		if (!step.isParamChange()) return true;
		if (alert("paramDiscard")) {
			step.clearParamChanges();
			return true;
		} else {
			return false;
		}
	}
	
	public void setData() {
		GuiParameter mand = new GuiParameter("MANDATORY", null, false, false, null);
		GuiParameter opt = new GuiParameter("OPTIONAL", null, false, false, null);
		GuiParameter mand1 = new GuiParameter("first mand", Collections.singletonList("val{$GLOBAL.Mand}1with{$step2.out}too"), false, true, step);
		setButtonListener(mand1);
		GuiParameter mand2 = new GuiParameter("second mand", Collections.singletonList("val{$DEFAULT.Mand}2"), true, true, step);
		GuiParameter opt1 = new GuiParameter("first optional", Collections.singletonList("valOpt1"), false, true, step);
		TreeItem<GuiParameter> mandItem = new TreeItem<GuiParameter>(mand);
		TreeItem<GuiParameter> optItem = new TreeItem<GuiParameter>(opt);
		TreeItem<GuiParameter> mand1Item = new TreeItem<GuiParameter>(mand1);
		TreeItem<GuiParameter> mand2Item = new TreeItem<GuiParameter>(mand2);
		TreeItem<GuiParameter> opt1Item = new TreeItem<GuiParameter>(opt1);
		mandItem.getChildren().addAll(mand1Item, mand2Item);
		optItem.getChildren().add(opt1Item);
		TreeItem<GuiParameter> root = new TreeItem<GuiParameter>();
		root.getChildren().addAll(mandItem, optItem);
		paramTable.setRoot(root);
		paramTable.setShowRoot(false);
	}
	
	private void setButtonListener(GuiParameter param) {
		if (param.getEditButton() != null) {
			param.getEditButton().setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent arg0) {
					nextScene(PladipusScene.PARAM_EDIT, param, true);
				}
			});
		}
	}
}
