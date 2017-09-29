package com.compomics.pladipus.model.core;

import java.util.HashSet;
import java.util.Set;

public class GuiSetup {
	private Set<ToolInformation> tools;
	private Set<DefaultOverview> userDefaults;
	private Set<WorkflowOverview> workflows;
	
	public GuiSetup(){}
	public GuiSetup(Set<ToolInformation> tools, Set<DefaultOverview> userDefaults, Set<WorkflowOverview> workflows) {
		this.tools = tools;
		this.userDefaults = userDefaults;
		this.workflows = workflows;
	}
	public void setTools(Set<ToolInformation> tools) {
		this.tools = tools;
	}
	public Set<ToolInformation> getTools() {
		initSets();
		return tools;
	}
	public void addTool(ToolInformation tool) {
		initSets();
		tools.add(tool);
	}
	public void setUserDefaults(Set<DefaultOverview> userDefaults) {
		this.userDefaults = userDefaults;
	}
	public Set<DefaultOverview> getUserDefaults() {
		initSets();
		return userDefaults;
	}
	public void addDefault(DefaultOverview def) {
		initSets();
		userDefaults.add(def);
	}
	public void setWorkflows(Set<WorkflowOverview> workflows) {
		this.workflows = workflows;
	}
	public Set<WorkflowOverview> getWorkflows() {
		initSets();
		return workflows;
	}
	public void addWorkflow(WorkflowOverview wf) {
		initSets();
		workflows.add(wf);
	}
	
	private void initSets() {
		if (tools == null) {
			tools = new HashSet<ToolInformation>();
		}
		if (userDefaults == null) {
			userDefaults = new HashSet<DefaultOverview>();
		}
		if (workflows == null) {
			workflows = new HashSet<WorkflowOverview>();
		}
	}
}
