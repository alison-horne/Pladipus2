package com.compomics.pladipus.model.parameters;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Type of value which may be provided for a tool parameter.
 * To be used for validation / deciding whether to provide a file browser in GUI, etc.
 * 
 * TODO Decide what types needed, what validation to do.  Is this useful at all?  Should this be in tools module instead, 
 * allow users to add to the list?  Keep generic?  Allow tool class to provide regex?
 */
public enum InputType {

    FILE_ZIP("zip file", ""),
    FILE_MGF("mgf file", ""),
    FILE_MGF_ZIP("mgf file (or zip file which will be unzipped before running)", ""),
    DIRECTORY("Directory", ""),
    STRING("Free text", "");

    private final String description;
    private final String regex;
    
    InputType (String description, String regex) {
        this.description = description;
        this.regex = regex;
    }
    
    public String getDescription() { 
    	return description; 
    }
    
    public boolean checkRegex(String matchString) {
    	Pattern pattern = Pattern.compile(regex);
    	Matcher matcher = pattern.matcher(matchString);
    	return matcher.find();
    }
}
