package com.compomics.pladipus.client.gui.fxmlcontrollers;

import java.util.ResourceBundle;

import com.compomics.pladipus.client.gui.FxmlController;
import com.compomics.pladipus.client.gui.model.LegendItem;
import com.compomics.pladipus.client.gui.model.PladipusScene;
import com.compomics.pladipus.client.gui.model.StepIcon;
import com.compomics.pladipus.client.gui.model.StepLink;
import com.compomics.pladipus.client.gui.model.WorkflowGui;
import com.compomics.pladipus.client.gui.model.WorkflowGuiStep;
import com.compomics.pladipus.model.core.ToolInformation;
import com.compomics.pladipus.model.persist.Step;
import com.compomics.pladipus.model.persist.Workflow;
import com.compomics.pladipus.shared.PladipusReportableException;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Button;
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
    private Button deleteStepBtn;
    @FXML
    private Button editStepBtn;
    private double iconSizeScale = 1.0;
	
    // TODO warnings on edit/delete about existing runs
    @FXML
    public void initialize() {
        toolNameColumn.setCellValueFactory(cellData -> cellData.getValue().toolNameProperty());
        colorColumn.setCellValueFactory(cellData -> cellData.getValue().colorProperty());
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
    	
    }
    
    @FXML
    public void handleEditStep() {
    	nextScene(PladipusScene.STEP_PARAM, workflowGui.getSelectedStep(), true);
    }
    
    @Override
    public void setup(Object workflow) throws PladipusReportableException {
    	this.workflow = (Workflow) workflow;
    	initWorkflowGui();
    	addCanvasListeners();
    	bindButtons();
    	legendTable.setItems(workflowGui.getLegendData());
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
				if (workflowGui.changesMade() && alert("workflowAbandon")) {
					event.consume();
				}
			}
    	});
    }
 
    public void displayWorkflow() throws PladipusReportableException {
//    	workflowGui.displayWorkflow(); // TODO setupStepIcons
    }
    
    public void arrangeIcons() {
    	Alert loading = new Alert(AlertType.INFORMATION);
    	loading.initOwner(stage);
    	loading.show(); // TODO make nice "your workflow has loaded" info message
//    	workflowGui.arrangeIcons();
    }
    
    private void initWorkflowGui() throws PladipusReportableException {
    	if (workflow != null) {
    		workflowGui = new WorkflowGui(workflow.getName());
    		if (workflow.getSteps() != null) {
    			for (Step step : workflow.getSteps().getStep()) {
    				workflowGui.addStep(step, guiControl.getToolInfo(step.getName()));
    			}
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
    }
    
    private void showStep(WorkflowGuiStep step) {
    	workflowGui.initStepIcon(step, getIconSize());
    	setupStepIcon(step);
    	canvasPane.getChildren().add(step.getIcon());
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
				nextScene(PladipusScene.STEP_PARAM, step, true);
			}			
		});
		return edit;
    }
    
    private MenuItem getDeleteMenuItem(WorkflowGuiStep step) {
    	MenuItem delete = new MenuItem(resources.getString("workflow.deleteStepButton"));
		delete.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// TODO 
			}			
		});
		return delete;
    }
    
    private void initBooleanListeners(WorkflowGuiStep step) {
    	step.getIcon().selectedProperty().addListener(selectedListener(step));
    	step.getIcon().startLinkProperty().addListener(startListener(step));
    	step.getIcon().endLinkProperty().addListener(endListener(step));
    	step.getIcon().finishLinkProperty().addListener(finishListener(step));
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
						nextScene(PladipusScene.STEP_LINK, true, link, workflowGui);
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
