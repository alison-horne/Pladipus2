package com.compomics.pladipus.client.gui.model;

public enum PladipusScene {
	SETUP("Setup", "setup", null, PladipusStage.PRIMARY, true),
	LOGIN("Login", "login", null, PladipusStage.PRIMARY, true),
	DASHBOARD("Dashboard", "dashboard", null, PladipusStage.PRIMARY, true),
	NEW_WORKFLOW("NewWorkflow", "newworkflow", null, PladipusStage.WORKFLOW, true),
	EDIT_WORKFLOW("EditWorkflow", "editworkflow", null, PladipusStage.WORKFLOW, true),
	WORKFLOW("Workflow", "workflow", "workflow", PladipusStage.WORKFLOW, false),
	TOOL_CHOICE("ToolChoice", "toolchoice", null, PladipusStage.TOOL, true);

    private String fxml;
    private String texts;
    private String css;
    private PladipusStage stage;
    private boolean canResize;
    
    PladipusScene(String fxml, String texts, String css, PladipusStage stage, boolean resize) {
        this.fxml = "fxml/" + fxml + ".fxml";
        this.texts = "guiTexts/" + texts;
        if (css != null) this.css = "css/" + css + ".css";
        this.stage = stage;
        this.canResize = resize;
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
    public PladipusStage getStage() {
    	return stage;
    }
    public boolean canResize() {
    	return canResize;
    }
}