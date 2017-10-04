package com.compomics.pladipus.client.gui.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.compomics.pladipus.model.core.DefaultOverview;
import com.compomics.pladipus.model.core.ToolInformation;
import com.compomics.pladipus.model.persist.Parameter;
import com.compomics.pladipus.model.persist.Step;
import com.compomics.pladipus.model.persist.Steps;
import com.compomics.pladipus.model.persist.Workflow;
import com.compomics.pladipus.shared.PladipusReportableException;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class WorkflowGui {
	private String workflowName;
	private ToolLegend toolLegend;
	private boolean changed = false;
	private int stepIdDefault = 1;
	private final static String STEP_NAME = "s";
	private Set<WorkflowGuiStep> guiSteps = new HashSet<WorkflowGuiStep>();
	private ObservableList<StepLink> links = FXCollections.observableArrayList();
	private StepLink drawingLink;
	private ObjectProperty<WorkflowGuiStep> selectedStep;
	private ObservableList<GlobalParameterGui> globals = FXCollections.observableArrayList();
	private List<GlobalParameterGui> originalGlobals = new ArrayList<GlobalParameterGui>();
	private ObservableList<DefaultOverview> userDefaults;
	
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
	
	public ObservableList<GlobalParameterGui> getGlobals() {
		return globals;
	}
	public void addGlobal(GlobalParameterGui glob) {
		globals.add(glob);
	}
	public void removeGlobal(GlobalParameterGui glob) {
		globals.remove(glob);
	}
	public void addOriginalGlobal(GlobalParameterGui glob) {
		originalGlobals.add(glob);
		globals.add(glob);
	}
	
	public void deleteStep(WorkflowGuiStep step) {
		guiSteps.remove(step);
		refresh();
		step.deleteStep();
		toolLegend.removeTool(step.getToolName());
	}
	
	public WorkflowGuiStep addStep(ToolInformation toolInfo, String stepId) {
		if ((stepId == null) || stepId.isEmpty()) {
			stepId = getNextUniqueStepId();
		}
		WorkflowGuiStep step = new WorkflowGuiStep(toolInfo, stepId);
		guiSteps.add(step);
		refresh();
		return step;
	}
	
	public void addStep(Step step, ToolInformation toolInfo) throws PladipusReportableException {
		WorkflowGuiStep guiStep = new WorkflowGuiStep(toolInfo, step);
		guiSteps.add(guiStep);
	}
	
	public void clearChangedFlag() {
		changed = false;
	}
	
	public void checkLinksAndValid() {
		for (WorkflowGuiStep step: guiSteps) {
			step.checkSubs();
			checkGlobals(step);
			checkDefaults(step);
			checkStepLinks(step);
		}
		removeDeadLinks();
		validIcons();
	}
	
	public void arrangeIcons(double width, double height, double iconSize) {
		Map<Integer, Set<WorkflowGuiStep>> rankMap = getRanks();
		if (rankMap.isEmpty()) {
			setupCircle(width, height, iconSize);
		} else {
			setupRanks(width, height, iconSize, rankMap);
		}
	}
	
	private void setupCircle(double width, double height, double iconSize) {
		double xcenter = (width - iconSize) / 2;
		double xradius = (width - (3 * iconSize)) / 2;
		double ycenter = (height - iconSize) / 2;
		double yradius = (height - (3 * iconSize)) / 2;
		double angleInc = Math.PI * 2 / guiSteps.size();
		double angle = 0;
		for (WorkflowGuiStep step: guiSteps) {
			double xpos = xcenter - (xradius * Math.cos(angle));
			double ypos = ycenter - (yradius * Math.sin(angle));
			step.getIcon().setInitPosition(xpos, ypos);
			angle += angleInc;
		}
	}
	
	private void setupRanks(double width, double height, double iconSize, Map<Integer, Set<WorkflowGuiStep>> rankMap) {
		double xmin = iconSize;
		double xmax = width - (2 * iconSize);
		double ymin = iconSize;
		double ymax = height - (2 * iconSize);
		double xdiff = xmax - xmin;
		if (rankMap.size() > 1) xdiff /= (rankMap.size() - 1);
		for (int rank: rankMap.keySet()) {
			double xpos = xmin + (rank * xdiff);
			Set<WorkflowGuiStep> steps = rankMap.get(rank);
			if (steps != null && !steps.isEmpty()) {
				double ypos = ymin;
				double ydiff = 0;
				if (steps.size() == 1) {
					ypos = (ymax - ymin) / 2;
				} else {
					ydiff = (ymax - ymin) / (steps.size() - 1);
				}
				for (WorkflowGuiStep step: steps) {
					step.getIcon().setInitPosition(xpos, ypos);
					ypos += ydiff;
				}
			}
		}
	}
	
	public Set<WorkflowGuiStep> getGuiSteps() {
		return guiSteps;
	}
	
	public void initStepIcon(WorkflowGuiStep step, double size) {
		step.initIcon(size, ToolColors.getColor(toolLegend.addTool(step.getToolName())));
	}
	
	public boolean changesMade() {
		return changed || globalsChanged();
	}
	public void wfChanged() {
		changed = true;
	}
	public boolean globalsChanged() {
		for (GlobalParameterGui gpg: globals) {
			if (gpg.valueChanged()) return true;
		}
		if (globals.size() != originalGlobals.size()) return true;
		if (!globals.containsAll(originalGlobals)) return true;
		return false;
	}
	public void refresh() {
		wfChanged();
		checkLinksAndValid();
	}
	
	private void checkStepLinks(WorkflowGuiStep step) {
		for (String stepId: step.getStepLinkNoOutputs()) {
			WorkflowGuiStep outStep = getStepById(stepId);
			if (outStep == null) {
				step.removeStepLinkNoOutput(stepId);
			} else if (!linkExists(outStep, step)){
				links.add(new StepLink(outStep, step));
			}
		}
		for (String stepId: step.getSubStepOutputs().keySet()) {
			WorkflowGuiStep outStep = getStepById(stepId);
			if (outStep == null) {
				step.setInvalidSub(true);
			} else {
				boolean validLink = false;
				for (String output: step.getSubStepOutputs().get(stepId)) {
					if (!outStep.getToolInfo().getOutputs().contains(output)) {
						step.setInvalidSub(true);
					} else {
						validLink = true;
					}
				}
				if (validLink) {
					if (!linkExists(outStep, step)) {
						links.add(new StepLink(outStep, step));
					}
				}
			}
		}
	}
	private void checkGlobals(WorkflowGuiStep step) {
		if (!step.isInvalidSub()) {
			for (String subGlob: step.getSubGlobals()) {
				boolean valid = false;
				for (GlobalParameterGui global: globals) {
					if (global.getGlobalName().equalsIgnoreCase(subGlob)) {
						valid = true;
						break;
					}
				}
				if (!valid) {
					step.setInvalidSub(true);
					return;
				}
			}
		}
	}
	private void checkDefaults(WorkflowGuiStep step) {
		if (!step.isInvalidSub()) {
			for (String def: step.getSubDefaults()) {
				boolean valid = false;
				for (DefaultOverview userDef: userDefaults) {
					if (userDef.getName().equalsIgnoreCase(def)) {
						valid = true;
						break;
					}
				}
				if (!valid) {
					step.setInvalidSub(true);
					return;
				}
			}
		}
	}
	private void removeDeadLinks() {
		Iterator<StepLink> iter = links.iterator();
		while (iter.hasNext()) {
			StepLink link = iter.next();
			if (!(guiSteps.contains(link.getStartStep()) && guiSteps.contains(link.getEndStep()) 
					&& (link.getEndStep().getSubStepOutputs().keySet().contains(link.getStartStep().getStepId()) 
						|| link.getEndStep().getStepLinkNoOutputs().contains(link.getStartStep().getStepId())))) {
				iter.remove();
			}
		}
	}
	private void validIcons() {
		for (WorkflowGuiStep step: guiSteps) {
			step.setIconComplete();
		}
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
    		drawingLink.getStartStep().getIcon().highlightOutCircle(false);
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
    private boolean linkExists(WorkflowGuiStep start, WorkflowGuiStep end) {
    	for (StepLink exist: links) {
    		if (exist.getStartStep() == start && exist.getEndStep() == end) return true;
    	}
    	return false;
    }
    
    public boolean stepIdExists(String id) {
    	for (WorkflowGuiStep step: guiSteps) {
    		if (step.getStepId().equalsIgnoreCase(id)) return true;
    	}
    	return false;
    }
    private WorkflowGuiStep getStepById(String id) {
    	for (WorkflowGuiStep step: guiSteps) {
    		if (step.getStepId().equalsIgnoreCase(id)) return step;
    	}
    	return null;
    }
    
    private String getNextUniqueStepId() {
    	String id;
    	do {
    		id = STEP_NAME + stepIdDefault;
    		stepIdDefault++;
    	} while (stepIdExists(id));
    	return id;
    }
    
    public ObservableList<StepLink> getLinks() {
    	return links;
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
    public Workflow toWorkflow() {
    	refresh();
    	Workflow wf = new Workflow();
    	wf.setName(workflowName);
    	for (GlobalParameterGui global: globals) {
    		Parameter globalParam = new Parameter();
    		globalParam.setName(global.getGlobalName());
    		if (!global.isPerRun()) {
    			for (String value: global.getValues()) {
    				globalParam.getValue().add(value);
    			}
    		}
    		wf.getGlobal().getParameters().getParameter().add(globalParam);
    	}
    	for (WorkflowGuiStep wgStep: guiSteps) {
    		if (wf.getSteps() == null) {
    			wf.setSteps(new Steps());
    		}
    		wf.getSteps().getStep().add(wgStep.toStep());
    	}
    	for (WorkflowGuiStep wgStep: guiSteps) {
    		if (!wgStep.getStepLinkNoOutputs().isEmpty()) {
    			Step depStep = wf.getSteps().getStepMap().get(wgStep.getStepId());
    			Set<Step> prereqs = new HashSet<Step>();
    			for (String preStepId: wgStep.getStepLinkNoOutputs()) {
    				prereqs.add(wf.getSteps().getStepMap().get(preStepId));
    			}
    			depStep.setRunAfter(prereqs);
    		}
    	}
    	return wf;
    }

	public void setDefaults(ObservableList<DefaultOverview> userDefaults) {
		this.userDefaults = userDefaults;
	}
	
	public boolean isValid() {
		refresh();
		if (workflowName == null || workflowName.isEmpty()) return false;
		for (WorkflowGuiStep step: guiSteps) {
			if (!step.isComplete()) return false;
		}
		for (GlobalParameterGui global: globals) {
			if (!global.isValid()) return false;
		}
		return true;
	}
	
	private Map<Integer, Set<WorkflowGuiStep>> getRanks() {
		Set<WorkflowGuiStep> checked = new HashSet<WorkflowGuiStep>();
		checked.addAll(guiSteps);
		int loops = checked.size();
		boolean invalid = false;
		while (!checked.isEmpty() && loops > 0) {
			loops--;
			invalid = false;
			Iterator<WorkflowGuiStep> iter = checked.iterator();
			while (iter.hasNext()) {
				WorkflowGuiStep step = iter.next();
				if (step.allPrereqStepIds().isEmpty()) {
					step.setRank(0);
					iter.remove();
				} else {
					int max = -1;
					for (String id: step.allPrereqStepIds()) {
						WorkflowGuiStep prereq = getStepById(id);
						if (prereq != null) {
							int prRank = prereq.getRank();
							if (prRank < 0) {
								invalid = true;
								break;
							}
							if (prRank > max) max = prRank;
						}
					}
					if (max > -1) {
						step.setRank(max + 1);
						iter.remove();
					}
				}
			}
		}
		Map<Integer, Set<WorkflowGuiStep>> rankMap = new HashMap<Integer, Set<WorkflowGuiStep>>();
		if (!invalid) {
			for (WorkflowGuiStep step: guiSteps) {
				int rank = step.getRank();
				Set<WorkflowGuiStep> stepsWithRank = rankMap.get(rank);
				if (stepsWithRank == null) stepsWithRank = new HashSet<WorkflowGuiStep>();
				stepsWithRank.add(step);
				rankMap.put(rank, stepsWithRank);
			}
		}
		return rankMap;
	}
}
