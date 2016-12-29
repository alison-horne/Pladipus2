package com.compomics.pladipus.model.core;

import java.util.HashSet;
import java.util.Set;

/**
 * Parameter as provided in workflow template.
 */
public class Parameter {
	private String name;
	private Set<String> values;
	
	//TODO validation, check parameter names, checks for defaults, globals, step outputs etc.
	private static final String SUBSTITUTE_PREFIX = "{$";
	private static final String DEFAULT_PREFIX = SUBSTITUTE_PREFIX + "DEFAULT";
	private static final String GLOBAL_PREFIX = SUBSTITUTE_PREFIX + "GLOBAL";
	
	public Parameter(String name) {
		this.name = name;
		values = new HashSet<String>();
	}
	
	public String getName() {
		return name;
	}
	
	public void addValue(String value) {
		values.add(value);
	}
}
