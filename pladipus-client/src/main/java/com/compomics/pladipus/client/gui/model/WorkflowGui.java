package com.compomics.pladipus.client.gui.model;

import java.util.HashSet;
import java.util.Set;

import com.compomics.pladipus.model.core.ToolInformation;
import com.compomics.pladipus.model.persist.Workflow;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseDragEvent;
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
	
	public WorkflowGui(Workflow workflow) {
		this.originalWorkflow = workflow;
		this.workflowName = new SimpleStringProperty(null);
		populateWorkflow();
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
	
	public void addStep(ToolInformation toolInfo, String stepId, boolean show) {
		if ((stepId == null) || stepId.isEmpty()) {
			stepId = "step" + stepIdDefault;
			stepIdDefault++;
		}
		WorkflowGuiStep step = new WorkflowGuiStep(this, toolInfo, stepId);
		guiSteps.add(step);
		if (show) {
			showStep(step);
		}
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
    
    private void populateWorkflow() {
    	if (originalWorkflow != null) {
    		setWorkflowName(originalWorkflow.getName());
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
    }
    public StepLink getDrawingLink() {
    	return drawingLink;
    }
    public void startDrawingLink(WorkflowGuiStep step) {
    	setDrawingLink(new StepLink(step));
    	canvas.getChildren().add(drawingLink.getLine());
    	canvas.getChildren().add(drawingLink.getArrow());
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
    }
}
