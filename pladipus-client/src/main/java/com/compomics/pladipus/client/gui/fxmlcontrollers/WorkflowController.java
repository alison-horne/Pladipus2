package com.compomics.pladipus.client.gui.fxmlcontrollers;

import java.util.List;
import java.util.ResourceBundle;

import com.compomics.pladipus.client.gui.FxmlController;
import com.compomics.pladipus.client.gui.model.LegendItem;
import com.compomics.pladipus.client.gui.model.GlobalParameterGui;
import com.compomics.pladipus.client.gui.model.GuiSubstitutions;
import com.compomics.pladipus.client.gui.model.PladipusScene;
import com.compomics.pladipus.client.gui.model.StepIcon;
import com.compomics.pladipus.client.gui.model.StepLink;
import com.compomics.pladipus.client.gui.model.ToolColors;
import com.compomics.pladipus.client.gui.model.WorkflowGui;
import com.compomics.pladipus.client.gui.model.WorkflowGuiStep;
import com.compomics.pladipus.model.core.ToolInformation;
import com.compomics.pladipus.model.core.WorkflowOverview;
import com.compomics.pladipus.model.persist.Parameter;
import com.compomics.pladipus.model.persist.Step;
import com.compomics.pladipus.model.persist.Workflow;
import com.compomics.pladipus.shared.PladipusReportableException;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextFlow;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuItem;
import javafx.stage.WindowEvent;

public class WorkflowController extends FxmlController {
	
	private Workflow workflow;
	private WorkflowGui workflowGui;

	@FXML
	private StackPane canvasPane;
    @FXML
    private ResourceBundle resources;
    @FXML
    private TableView<LegendItem> legendTable;
    @FXML
    private TableColumn<LegendItem, Rectangle> colorColumn;
    @FXML
    private TableColumn<LegendItem, String> toolNameColumn;
    @FXML
    private TableView<GlobalParameterGui> globalsTable;
    @FXML
    private TableColumn<GlobalParameterGui, String> globalNameColumn;
    @FXML
    private TableColumn<GlobalParameterGui, TextFlow> globalValueColumn;
    @FXML
    private TableColumn<GlobalParameterGui, CheckBox> globalPerRunColumn;
    @FXML
    private Button deleteStepBtn;
    @FXML
    private Button editStepBtn;
    @FXML
    private TextField nameField;
    private double iconSizeScale = 1.0;
    private String REPLACE_BTN, CANCEL_BTN, LINK_NO_OUTPUT_BTN, LINK_OUTPUT_BTN;
    private boolean show = false;
	
    @FXML
    public void initialize() {
    	REPLACE_BTN=resources.getString("workflow.replaceBtn");
    	CANCEL_BTN=resources.getString("workflow.cancelBtn");
    	LINK_NO_OUTPUT_BTN = resources.getString("workflow.linkNoOutputBtn");
    	LINK_OUTPUT_BTN = resources.getString("workflow.linkOutputBtn");
        toolNameColumn.setCellValueFactory(cellData -> cellData.getValue().toolNameProperty());
        colorColumn.setCellValueFactory(cellData -> cellData.getValue().colorProperty());
        globalNameColumn.setCellValueFactory(cellData -> cellData.getValue().globalNameProperty());
        globalValueColumn.setCellValueFactory(cellData -> cellData.getValue().displayValueProperty());
        globalPerRunColumn.setCellValueFactory(cellData -> cellData.getValue().checkBoxProperty());
        globalNameColumn.setCellFactory(cell -> new TableCell<GlobalParameterGui, String>() {
        	@Override
        	protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (item != null) {
					if (!getTableView().getItems().get(getIndex()).isValid()) {
						setTextFill(Color.RED);
						setTooltip(new Tooltip(resources.getString("workflow.invalidGlobal")));
					} else {
						setTextFill(ToolColors.getGlobalColor());
						setTooltip(null);
					}
					setText(item);
				} else {
					setText(null);
					setTooltip(null);
				}
        	}
        });
    }
    
    @FXML
    public void handleRename() {
    	String oldName = workflowGui.getWorkflowName();
		String header = resources.getString("workflow.newNameHeader");
		boolean finished = false;
		while (!finished) {
			String rename = guiControl.getText(stage, header, oldName);
			if (rename == null || rename.isEmpty() || rename.equalsIgnoreCase(oldName)) {
				finished = true;
			} else {
				if (guiControl.getWorkflow(rename) != null) {
		    		if (REPLACE_BTN.equals(customAlert("workflowExist", new String[]{REPLACE_BTN, CANCEL_BTN}))) {
		    			finished = true;
						workflowGui.setWorkflowName(rename);
						nameField.setText(rename);
		    		}
				} else {
					finished = true;
					workflowGui.setWorkflowName(rename);
					nameField.setText(rename);
				}
			}
		}
    }
    @FXML
    public void handleSave() { // TODO option to start run from save
    	if (workflowGui.changesMade()) {
    		if (guiControl.getWorkflow(workflowGui.getWorkflowName()) != null) {
    			// TODO update...what happens to existing runs when updating workflow in db? Do we need warning here?
    		}
    		if (workflowGui.isValid()) {
	    		Workflow wf = workflowGui.toWorkflow();
	    		try {
					WorkflowOverview overview = guiControl.saveWorkflow(wf); // TODO run as task
					workflowGui.clearChangedFlag();
					if (alert("wfBatchStart")) {
						loadBatch(overview);
					} else {
						close();
					}
				} catch (PladipusReportableException e) {
					error(resources.getString("workflow.saveError") + "\n" + e.getMessage());
				}
    		} else {
    			error(resources.getString("workflow.saveInvalid"));
    		}
    	} else if (alert("wfBatchStart")) {
    		loadBatch(guiControl.getWorkflowOverview(workflowGui.getWorkflowName()));
    	}
    	else {
    		close();
    	}
    }
    private void loadBatch(WorkflowOverview wo) {
    	if (wo.getHeaders() == null) {
    		error(resources.getString("workflow.batchNoHeaders"));
    		close();
    	} else if (wo.getHeaders().size() == 1) {
    		// TODO...does this even work?  Should allow it.  How are db tables set up to take it?
    	} else {
    		nextScene(PladipusScene.BATCH_LOAD, false, wo);
    	}
    }
    @FXML
    public void handleSaveXml() {
    	try {
			guiControl.saveWorkflowXml(stage, workflowGui.getWorkflowName() + ".xml", workflowGui.toWorkflow());
			infoAlert("workflowXml");
		} catch (PladipusReportableException e) {
			error(e.getMessage());
		}
    }
    @FXML
    public void handleCancel() {
    	if (!(workflowGui.changesMade() && !alert("workflowAbandon"))) {
    		close();
    	} 
    }
    
    @FXML
    public void handleAddStep() {// TODO Edit menu option to change size of icons
    	ToolInformation toolInfo = (ToolInformation) getFromScene(PladipusScene.TOOL_CHOICE);
    	if (toolInfo != null) {
    		WorkflowGuiStep newStep = workflowGui.addStep(toolInfo, null);
    		showStep(newStep);
    		workflowGui.setSelectedStep(newStep);
    	}
    }
    
    @FXML
    public void handleDeleteStep() {
    	if (alert("stepDelete")) workflowGui.deleteStep(workflowGui.getSelectedStep());
    }
    
    @FXML
    public void handleEditStep() {
    	getFromScene(PladipusScene.STEP_PARAM, workflowGui.getSelectedStep(), workflowGui);
    }
    
    @FXML
    public void handleAddGlobal() {
    	nextScene(PladipusScene.NEW_GLOBAL, true, workflowGui.getGlobals());
    }
    
    @FXML
    public void handleEditGlobal() {
    	GlobalParameterGui editGlobal = globalsTable.getSelectionModel().getSelectedItem();
    	if (editGlobal == null) {
    		error(resources.getString("workflow.alertNoGlobal"));
    	} else {
    		nextScene(PladipusScene.NEW_GLOBAL, true, workflowGui.getGlobals(), editGlobal);
    	}
    }
    
    @FXML
    public void handleRemoveGlobal() {
    	GlobalParameterGui removeGlobal = globalsTable.getSelectionModel().getSelectedItem();
    	if (removeGlobal == null) {
    		error(resources.getString("workflow.alertNoGlobal"));
    	} else {
    		if (!globalInUse(removeGlobal.getGlobalFullName()) || alert("globalDelete")) {
    			workflowGui.removeGlobal(removeGlobal);
    		}
    	}
    }
    
    private boolean globalInUse(String fullGlobalName) {
    	for (WorkflowGuiStep step: workflowGui.getGuiSteps()) {
    		if (step.paramsContainSub(fullGlobalName)) return true;
    	}
    	return false;
    }
    
    @Override
    public void setup(Object workflow) throws PladipusReportableException {
    	this.workflow = (Workflow) workflow;
    	initWorkflowGui();
    	nameField.setText(workflowGui.getWorkflowName());
    	addCanvasListeners();
    	bindButtons();
    	legendTable.setItems(workflowGui.getLegendData());
    	globalsTable.setItems(workflowGui.getGlobals());
    }
    
    private void bindButtons() {
        final BooleanBinding stepSelected = Bindings.isNull(workflowGui.selectedStepProperty());
        deleteStepBtn.disableProperty().bind(stepSelected);
        editStepBtn.disableProperty().bind(stepSelected);
    }
    
    @Override
    public void postShow() throws PladipusReportableException {
        displayWorkflow();
        if (!workflowGui.getGuiSteps().isEmpty()) {
        	arrangeIcons();
        }
        
    	stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				if (workflowGui.changesMade() && !alert("workflowAbandon")) {
					event.consume();
				}
			}
    	});
    }
 
    public void displayWorkflow() throws PladipusReportableException {
    	for (WorkflowGuiStep step: workflowGui.getGuiSteps()) {
    		showStep(step);
    	}
    	show = true;
    	workflowGui.checkLinksAndValid();
    }
    
    public void arrangeIcons() {
    	infoAlert("workflowEdit");
    	workflowGui.arrangeIcons(canvasPane.getBoundsInLocal().getWidth(), canvasPane.getBoundsInLocal().getHeight(), getIconSize());
    }
    
    private void initWorkflowGui() throws PladipusReportableException {
    	if (workflow != null) {
    		workflowGui = new WorkflowGui(workflow.getName());
    		if (workflow.getSteps() != null) {
    			for (Step step : workflow.getSteps().getStep()) {
    				workflowGui.addStep(step, guiControl.getToolInfo(step.getName()));
    			}
    		}
    		workflowGui.setDefaults(guiControl.getUserDefaults());
    		List<Parameter> globs = workflow.getGlobal().getParameters().getParameter();
    		for (Parameter globParam: globs) {
    			workflowGui.addOriginalGlobal(new GlobalParameterGui(globParam.getName(), globParam.getValue(), 
    					globParam.getValue().isEmpty(), new GuiSubstitutions(guiControl.getUserDefaults())));
    		}
    	} else {
    		throw new PladipusReportableException(resources.getString("workflow.noWorkflowError"));
    	}
    }
    
    private void addCanvasListeners() {
		canvasPane.setOnMouseDragOver(new EventHandler<MouseDragEvent>() {
			@Override
			public void handle(MouseDragEvent event) {
				if (workflowGui.getDrawingLink() != null) {
					event.consume();
					Point2D canvasBounds = canvasPane.localToScene(0, 0, true);
					double x = event.getSceneX() - canvasBounds.getX();
					double y = event.getSceneY() - canvasBounds.getY();				
					workflowGui.getDrawingLink().updateLink(x, y);
				}
			}		
		});
		canvasPane.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				workflowGui.setSelectedStep(null);
			}
		});
		workflowGui.getLinks().addListener(new ListChangeListener<StepLink>() {
			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends StepLink> c) {
				while (c.next()) {
					if (show) {
						for (StepLink removed: c.getRemoved()) {
							removeLink(removed);
						}
						for (StepLink added: c.getAddedSubList()) {
							if (!added.isDrawn()) {
								showLink(added);
							}
						}
					}
				}
			}			
		});
    }
    
    private void showStep(WorkflowGuiStep step) {
    	workflowGui.initStepIcon(step, getIconSize());
    	setupStepIcon(step);
    	canvasPane.getChildren().add(step.getIcon());
    }
    
    private void hideStep(WorkflowGuiStep step) {
    	canvasPane.getChildren().remove(step.getIcon());
    }
    
    private double getIconSize() {
    	return Math.min(canvasPane.getBoundsInParent().getHeight(), canvasPane.getBoundsInParent().getWidth()) * iconSizeScale / 10;
    }
    
    private void setupStepIcon(WorkflowGuiStep step) {
    	initContextMenu(step);
    	initBooleanListeners(step);
    }
    
    private void initContextMenu(WorkflowGuiStep step) {
    	StepIcon icon = step.getIcon();
    	if (icon != null) {
    		icon.initContextMenu(getEditMenuItem(step), getDeleteMenuItem(step));
    	}
    }
    
    private MenuItem getEditMenuItem(WorkflowGuiStep step) {
    	MenuItem edit = new MenuItem(resources.getString("workflow.editStepButton"));
		edit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				getFromScene(PladipusScene.STEP_PARAM, step, workflowGui);
			}
		});
		return edit;
    }
    
    private MenuItem getDeleteMenuItem(WorkflowGuiStep step) {
    	MenuItem delete = new MenuItem(resources.getString("workflow.deleteStepButton"));
		delete.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (alert("stepDelete")) workflowGui.deleteStep(step);
			}			
		});
		return delete;
    }
    
    private void initBooleanListeners(WorkflowGuiStep step) {
    	step.deletedProperty().addListener(deletedListener(step));
    	step.getIcon().selectedProperty().addListener(selectedListener(step));
    	step.getIcon().startLinkProperty().addListener(startListener(step));
    	step.getIcon().endLinkProperty().addListener(endListener(step));
    	step.getIcon().finishLinkProperty().addListener(finishListener(step));
    }
    
    private ChangeListener<Boolean> deletedListener(WorkflowGuiStep step) {
    	ChangeListener<Boolean> deleted = new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldVal, Boolean newVal) {
				if (newVal) hideStep(step);
			}	
    	};
    	return deleted;
    }

	private ChangeListener<Boolean> selectedListener(WorkflowGuiStep step) {
		ChangeListener<Boolean> selected = new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldVal, Boolean newVal) {
				if (newVal) workflowGui.setSelectedStep(step);
			}			
		};
		return selected;
	}
	
	private ChangeListener<Boolean> startListener(WorkflowGuiStep step) {
		ChangeListener<Boolean> start = new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldVal, Boolean newVal) {
				if (newVal) {
					workflowGui.startDrawingLink(step);
					showLink(workflowGui.getDrawingLink());
				} else {
					removeLink(workflowGui.getDrawingLink());
					workflowGui.clearDrawingLink();
				}
			}			
		};
		return start;
	}
	
	private ChangeListener<Boolean> endListener(WorkflowGuiStep step) {
		ChangeListener<Boolean> end = new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldVal, Boolean newVal) {
				if (newVal) {
					workflowGui.endDrawingLink(step);
				} else {
					workflowGui.endDrawingLink(null);
				}
			}			
		};
		return end;
	}
	
	private ChangeListener<Boolean> finishListener(WorkflowGuiStep step) {
		ChangeListener<Boolean> finished = new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldVal, Boolean newVal) {
				if (newVal) {
					StepLink link = workflowGui.getDrawingLink();
					if (link != null) {
						if (workflowGui.finaliseDrawingLink()) removeLink(link);
						String chosenButton;
						if (link.getStartStep().getOutputs().isEmpty()) {
							chosenButton = customAlert("stepLink", new String[]{LINK_NO_OUTPUT_BTN, CANCEL_BTN});
						} else {
							chosenButton = customAlert("stepLink", new String[]{LINK_NO_OUTPUT_BTN, LINK_OUTPUT_BTN, CANCEL_BTN});
						}
						if (chosenButton.equals(LINK_NO_OUTPUT_BTN)) {
							link.getEndStep().addStepLinkNoOutput(link.getStartStep().getStepId());
						} else if (chosenButton.equals(LINK_OUTPUT_BTN)) {
							getFromScene(PladipusScene.STEP_LINK, link, workflowGui);
						}	
						workflowGui.refresh();
					}
				}
			}			
		};
		return finished;
	}
	
    public void addLink(WorkflowGuiStep start, WorkflowGuiStep end) {
    	showLink(workflowGui.addLink(start, end));
    }
    
    private void showLink(StepLink link) {
    	if (link != null) {
    		canvasPane.getChildren().add(link.getLine());
    		canvasPane.getChildren().add(link.getArrow());
    	}
    }
    private void removeLink(StepLink link) {
    	if (link != null) {
    		canvasPane.getChildren().remove(link.getLine());
    		canvasPane.getChildren().remove(link.getArrow());
    	}
    }
}
