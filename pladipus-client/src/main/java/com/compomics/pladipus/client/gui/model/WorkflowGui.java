package com.compomics.pladipus.client.gui.model;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.compomics.pladipus.model.core.ToolInformation;
import com.compomics.pladipus.model.persist.Step;
import com.compomics.pladipus.shared.PladipusReportableException;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class WorkflowGui {
	private String workflowName;
	private ToolLegend toolLegend;
	private boolean changed = false;
	private int stepIdDefault = 1;
	private final static String STEP_NAME = "step";
	private Set<WorkflowGuiStep> guiSteps = new HashSet<WorkflowGuiStep>();
	private Set<StepLink> links = new HashSet<StepLink>();
	private StepLink drawingLink;
	private ObjectProperty<WorkflowGuiStep> selectedStep;
	
	// TODO - thoughts on links...want to be able to draw twice, for ease of user adding another out->in param link, but only want one actual arrow on screen
	public WorkflowGui(String name) {
		this.workflowName = name;
		this.toolLegend = new ToolLegend();
		selectedStep = new SimpleObjectProperty<WorkflowGuiStep>(null);
	}
	
    public String getWorkflowName() {
        return workflowName;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

	public ObservableList<LegendItem> getLegendData() {
		return toolLegend.getLegendData();
	}
	
	public WorkflowGuiStep addStep(ToolInformation toolInfo, String stepId) {
		if ((stepId == null) || stepId.isEmpty()) {
			stepId = getNextUniqueStepId();
		}
		WorkflowGuiStep step = new WorkflowGuiStep(toolInfo, stepId);
		guiSteps.add(step);
		return step;
	}
	
	public void addStep(Step step, ToolInformation toolInfo) throws PladipusReportableException {
		WorkflowGuiStep guiStep = new WorkflowGuiStep(toolInfo, step);
		guiStep.validate();
		guiSteps.add(guiStep);
	}
	
	public Set<WorkflowGuiStep> getGuiSteps() {
		return guiSteps;
	}
	
	public void initStepIcon(WorkflowGuiStep step, double size) {
		step.initIcon(size, ToolColors.getColor(toolLegend.addTool(step.getToolName())));
	}
	
	public Color getStepColor(String stepId) {
		for (WorkflowGuiStep guiStep: guiSteps) {
			if (guiStep.getStepId().equals(stepId)) {
				int colorId = toolLegend.getToolColorId(guiStep.getToolName());
				if (colorId > -1) return ToolColors.getColor(colorId);
			}
		}
		return null;
	}
	
	public boolean changesMade() {
		return changed;
	}
    
    public void setDrawingLink(StepLink drawingLink) {
    	this.drawingLink = drawingLink;
    	if (drawingLink == null) {
    		for (WorkflowGuiStep step: guiSteps) {
    			step.getIcon().highlightInCircle(false);
        		step.getIcon().dropLink();
    		}
    	}
    }
    public StepLink getDrawingLink() {
    	return drawingLink;
    }
    public void startDrawingLink(WorkflowGuiStep step) {
    	setDrawingLink(new StepLink(step));
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
    		setDrawingLink(null);
    	}
    }
    public boolean finaliseDrawingLink() {
    	boolean exists = true;
    	if (drawingLink != null) {
    		exists = linkExists(drawingLink);
    		if (!exists) {
    			links.add(drawingLink);
    		}
	    	setDrawingLink(null);
    	}
    	return exists;
    }
    private boolean linkExists(StepLink link) {
    	for (StepLink exist: links) {
    		if (exist.getStartStep() == link.getStartStep() &&
    			exist.getEndStep() == link.getEndStep()) {
    			return true;
    		}
    	}
    	return false;
    }
    
    public boolean stepIdExists(String id) {
    	for (WorkflowGuiStep step: guiSteps) {
    		if (step.getStepId().equals(id)) return true;
    	}
    	return false;
    }
    
    private String getNextUniqueStepId() {
    	String id;
    	do {
    		id = STEP_NAME + stepIdDefault;
    		stepIdDefault++;
    	} while (stepIdExists(id));
    	return id;
    }
    
    public Set<StepLink> getLinksToStep(WorkflowGuiStep endStep) {
    	return links.stream().filter( l -> l.getEndStep().equals(endStep) ).collect(Collectors.<StepLink>toSet());
    }
    public Set<StepLink> getLinksFromStep(WorkflowGuiStep startStep) {
    	return links.stream().filter( l -> l.getStartStep().equals(startStep) ).collect(Collectors.<StepLink>toSet());
    }

    public StepLink addLink(WorkflowGuiStep start, WorkflowGuiStep end) {
    	StepLink link = new StepLink(start, end);
    	if (!linkExists(link)){
    		links.add(link);
    		return link;
    	}
    	return null;
    }
    
    public void linksToFront(WorkflowGuiStep step) {
    	for (StepLink link: getLinksToStep(step)) link.toFront();
    	for (StepLink link: getLinksFromStep(step)) link.toFront();
    }
    
    public void setSelectedStep(WorkflowGuiStep step) {
	    if (step != null) {
	    	step.getIcon().toFront();
	    	linksToFront(step);
	    	step.getIcon().setSelected(true);
	    }
	    if (getSelectedStep() != null && (step == null || !step.equals(getSelectedStep()))) {
	    	getSelectedStep().getIcon().setSelected(false);
	    }
	    selectedStep.set(step);
    }
    public WorkflowGuiStep getSelectedStep() {
    	return selectedStep.get();
    }
    public ObjectProperty<WorkflowGuiStep> selectedStepProperty() {
    	return selectedStep;
    }
}
