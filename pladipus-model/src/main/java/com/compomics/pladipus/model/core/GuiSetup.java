package com.compomics.pladipus.model.core;

import java.util.HashSet;
import java.util.Set;

public class GuiSetup {
	private Set<ToolInformation> tools;
	private Set<DefaultOverview> userDefaults;
	private Set<WorkflowOverview> workflows;
	
	public GuiSetup(){}
	public void setTools(Set<ToolInformation> tools) {
		this.tools = tools;
	}
	public Set<ToolInformation> getTools() {
		return tools;
	}
	public void addTool(ToolInformation tool) {
		if (tools == null) {
			tools = new HashSet<ToolInformation>();
		}
		tools.add(tool);
	}
	public void setUserDefaults(Set<DefaultOverview> userDefaults) {
		this.userDefaults = userDefaults;
	}
	public Set<DefaultOverview> getUserDefaults() {
		return userDefaults;
	}
	public void addDefault(DefaultOverview def) {
		if (userDefaults == null) {
			userDefaults = new HashSet<DefaultOverview>();
		}
		userDefaults.add(def);
	}
	public void setWorkflows(Set<WorkflowOverview> workflows) {
		this.workflows = workflows;
	}
	public Set<WorkflowOverview> getWorkflows() {
		return workflows;
	}
	public void addWorkflow(WorkflowOverview wf) {
		if (workflows == null) {
			workflows = new HashSet<WorkflowOverview>();
		}
		workflows.add(wf);
	}
}
