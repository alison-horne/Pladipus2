package com.compomics.pladipus.model.core;

import java.util.HashSet;
import java.util.Set;

public class GuiSetup {
	private Set<ToolInformation> tools;
	private Set<DefaultOverview> userDefaults;
	private Set<WorkflowOverview> workflows;
	
	public GuiSetup(){
		this(null, null, null);
	}
	public GuiSetup(Set<ToolInformation> tools, Set<DefaultOverview> userDefaults, Set<WorkflowOverview> workflows) {
		this.tools = new HashSet<ToolInformation>();
		this.userDefaults = new HashSet<DefaultOverview>();
		this.workflows = new HashSet<WorkflowOverview>();
		setTools(tools);
		setUserDefaults(userDefaults);
		setWorkflows(workflows);
	}
	public void setTools(Set<ToolInformation> tools) {
		this.tools.clear();
		if (tools != null) {
			this.tools.addAll(tools);
		}
	}
	public Set<ToolInformation> getTools() {
		return tools;
	}
	public void addTool(ToolInformation tool) {
		tools.add(tool);
	}
	public void setUserDefaults(Set<DefaultOverview> userDefaults) {
		this.userDefaults.clear();
		if (userDefaults != null) {
			this.userDefaults.addAll(userDefaults);
		}
	}
	public Set<DefaultOverview> getUserDefaults() {
		return userDefaults;
	}
	public void addDefault(DefaultOverview def) {
		userDefaults.add(def);
	}
	public void setWorkflows(Set<WorkflowOverview> workflows) {
		this.workflows.clear();
		if (workflows != null) {
			this.workflows.addAll(workflows);
		}
	}
	public Set<WorkflowOverview> getWorkflows() {
		return workflows;
	}
	public void addWorkflow(WorkflowOverview wf) {
		workflows.add(wf);
	}
}
