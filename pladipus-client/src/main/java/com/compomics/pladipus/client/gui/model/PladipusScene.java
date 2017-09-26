package com.compomics.pladipus.client.gui.model;

public enum PladipusScene {
	SETUP("Setup", "setup", null, true, true),
	LOGIN("Login", "login", null, true, true),
	DASHBOARD("Dashboard", "dashboard", null, true, true),
	NEW_WORKFLOW("NewWorkflow", "newworkflow", null, true, false),
	EDIT_WORKFLOW("EditWorkflow", "editworkflow", null, true, false),
	WORKFLOW("Workflow", "workflow", "workflow", false, false),
	USER_DEFAULTS("UserDefaults", "userdefaults", null, true, false),
	TOOL_CHOICE("ToolChoice", "toolchoice", null, true, false),
	STEP_PARAM("StepParameters", "stepparameters", null, true, false),
	PARAM_EDIT("ParamEdit", "paramedit", null, true, false),
	SUB_CHOICE("SubstitutionChoice", "subchoice", null, true, false),
	NEW_DEFAULT("NewDefault", "newdefault", null, true, false),
	NEW_GLOBAL("NewEditGlobal", "newglobal", null, true, false),
	STEP_LINK("StepLink", "steplink", null, true, false),
	REMOVE_LINKS("RemoveLinks", "removelinks", null, true, false);

    private String fxml;
    private String texts;
    private String css;
    private boolean canResize;
    private boolean primary;
    
    PladipusScene(String fxml, String texts, String css, boolean resize, boolean primary) {
        this.fxml = "fxml/" + fxml + ".fxml";
        this.texts = "guiTexts/" + texts;
        if (css != null) this.css = "css/" + css + ".css";
        this.canResize = resize;
        this.primary = primary;
    }

    public String getFxml() {
        return fxml;
    }
    public String getTexts() {
    	return texts;
    }
    public String getCss() {
    	return css;
    }
    public boolean canResize() {
    	return canResize;
    }
    public boolean isPrimary() {
    	return primary;
    }
}