package com.compomics.pladipus.client.gui.model;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.compomics.pladipus.client.gui.GuiControl;
import com.compomics.pladipus.model.core.ToolInformation;
import com.compomics.pladipus.model.persist.Step;
import com.compomics.pladipus.model.persist.Workflow;
import com.compomics.pladipus.shared.PladipusReportableException;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

public class WorkflowGui {
	private Workflow originalWorkflow;
	private StringProperty workflowName;
	private StackPane canvas;
	private ToolLegend toolLegend;
	private boolean changed = false;
	private double iconSizeScale = 1.0;
	private int stepIdDefault = 1;
	private Set<WorkflowGuiStep> guiSteps = new HashSet<WorkflowGuiStep>();
	private ObservableList<BatchGui> batches = FXCollections.observableArrayList();
	private Set<StepLink> links = new HashSet<StepLink>();
	private StepLink drawingLink;
	private ObjectProperty<WorkflowGuiStep> selectedStep;
	private GuiControl guiControl;
	
	// TODO - thoughts on links...want to be able to draw twice, for ease of user adding another out->in param link, but only want one actual arrow on screen
	public WorkflowGui(Workflow workflow) {
		this.originalWorkflow = workflow;
		this.workflowName = new SimpleStringProperty(null);
		if (workflow != null) setWorkflowName(workflow.getName());
		selectedStep = new SimpleObjectProperty<WorkflowGuiStep>(null);
	} 
	
	public void setGuiController(GuiControl guiControl) {
		this.guiControl = guiControl;
	}
	
	public Workflow getWorkflow() {
		return originalWorkflow;
	}
	
	public void setCanvas(StackPane canvas) {
		this.canvas = canvas;
		addCanvasListeners();
		this.toolLegend = new ToolLegend();
	}
	
	public WorkflowGui(Workflow workflow, StackPane canvas) {
		this(workflow);
		setCanvas(canvas);
	}
	
    public String getWorkflowName() {
        return workflowName.get();
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName.set(workflowName);
    }

    public StringProperty workflowNameProperty() {
        return workflowName;
    }

	public ObservableList<LegendItem> getLegendData() {
		return toolLegend.getLegendData();
	}
	
	public void addStep(ToolInformation toolInfo, String stepId) {
		if ((stepId == null) || stepId.isEmpty()) {
			stepId = "step" + stepIdDefault;
			stepIdDefault++;
		}
		WorkflowGuiStep step = new WorkflowGuiStep(this, toolInfo, stepId);
		guiSteps.add(step);
		showStep(step);
		setSelectedStep(step);
	}
	
	private void addStep(Step step) throws PladipusReportableException {
		WorkflowGuiStep guiStep = new WorkflowGuiStep(this, step);
		guiStep.validate();
		guiSteps.add(guiStep);
	}
	
	public void showStep(WorkflowGuiStep step) {
		step.initIcon(getIconSize(), ToolColors.getColor(toolLegend.addTool(step.getToolName())));
		canvas.getChildren().add(step.getIcon());
	}
	
	public boolean changesMade() {
		return changed;
	}
	
    private double getIconSize() {
    	return Math.min(canvas.getBoundsInParent().getHeight(), canvas.getBoundsInParent().getWidth()) * iconSizeScale / 10;
    }
    
    private void populateWorkflow() throws PladipusReportableException {
    	if (originalWorkflow != null) {
    		setWorkflowName(originalWorkflow.getName());
    		for (Step step: originalWorkflow.getSteps().getStep()) {
    			addStep(step);
    		}
    	}
    }
    
    public ObservableList<BatchGui> getBatches() {
    	return batches;
    }
    public void addBatch(BatchGui batch) {
    	batches.add(batch);
    }
    
    public void setDrawingLink(StepLink drawingLink) {
    	this.drawingLink = drawingLink;
    	if (drawingLink == null) {
    		for (WorkflowGuiStep step: guiSteps) {
    			step.getIcon().highlightInCircle(false);
    		}
    	}
    }
    public StepLink getDrawingLink() {
    	return drawingLink;
    }
    public void startDrawingLink(WorkflowGuiStep step) {
    	setDrawingLink(new StepLink(step));
    	canvas.getChildren().add(drawingLink.getLine());
    	canvas.getChildren().add(drawingLink.getArrow());
    	for (WorkflowGuiStep otherStep: guiSteps) {
    		if (!otherStep.equals(step)) {
    			otherStep.getIcon().highlightInCircle(true);
    		}
    	}
    }
    public void endDrawingLink(WorkflowGuiStep step) {
    	if (drawingLink != null) {
    		drawingLink.setEndStep(step);
    	}
    }
    public void clearDrawingLink() {
    	if (drawingLink != null) {
    		canvas.getChildren().remove(drawingLink.getLine());
    		canvas.getChildren().remove(drawingLink.getArrow());
    		setDrawingLink(null);
    	}
    }
    public void finaliseDrawingLink() {
    	if (drawingLink != null) {
	    	links.add(drawingLink);
	    	setDrawingLink(null);
    	}
    }
    
    public Set<StepLink> getLinksToStep(WorkflowGuiStep endStep) {
    	return links.stream().filter( l -> l.getEndStep().equals(endStep) ).collect(Collectors.<StepLink>toSet());
    }
    public Set<StepLink> getLinksFromStep(WorkflowGuiStep startStep) {
    	return links.stream().filter( l -> l.getStartStep().equals(startStep) ).collect(Collectors.<StepLink>toSet());
    }

    public void addLink(WorkflowGuiStep start, WorkflowGuiStep end) {
    	StepLink link = new StepLink(start, end);
    	links.add(link);
    	canvas.getChildren().add(link.getLine());
    	canvas.getChildren().add(link.getArrow());
    }
    
    public void linksToFront(WorkflowGuiStep step) {
    	for (StepLink link: getLinksToStep(step)) link.toFront();
    	for (StepLink link: getLinksFromStep(step)) link.toFront();
    }
    
    public void setSelectedStep(WorkflowGuiStep step) {
	    if (step != null) {
	    	step.getIcon().toFront();
	    	linksToFront(step);
	    	step.getIcon().highlightIcon(true);
	    }
	    if (getSelectedStep() != null && (step == null || !step.equals(getSelectedStep()))) {
	    	getSelectedStep().getIcon().highlightIcon(false);
	    }
	    selectedStep.set(step);
    }
    public WorkflowGuiStep getSelectedStep() {
    	return selectedStep.get();
    }
    public ObjectProperty<WorkflowGuiStep> selectedStepProperty() {
    	return selectedStep;
    }
    
    private void addCanvasListeners() {
		canvas.setOnMouseDragOver(new EventHandler<MouseDragEvent>() {
			@Override
			public void handle(MouseDragEvent event) {
				if (getDrawingLink() != null) {
					event.consume();
					Point2D canvasBounds = canvas.localToScene(0, 0, true);
					double x = event.getSceneX() - canvasBounds.getX();
					double y = event.getSceneY() - canvasBounds.getY();				
					getDrawingLink().updateLink(x, y);
				}
			}		
		});
		canvas.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				setSelectedStep(null);
			}
		});
    }
    
    public void displayWorkflow() throws PladipusReportableException {
    	populateWorkflow();
    	for (WorkflowGuiStep step: guiSteps) {
    		showStep(step);
    	}
    }
    
    public void arrangeIcons() {
    	// TODO
    }
    
    public ToolInformation getTool(String toolName) throws PladipusReportableException {
    	return guiControl.getToolInfo(toolName);
    }
}
