package com.compomics.pladipus.model.parameters;

public class Substitution {
	private static final String SUBSTITUTE_PREFIX = "{$";
	private static final String SUBSTITUTE_END = "}";
	private static final String DEFAULT_PREFIX = "DEFAULT";
	private static final String GLOBAL_PREFIX = "GLOBAL";
	
	public final static String getPrefix() {
		return SUBSTITUTE_PREFIX;
	}
	public final static String getEnd() {
		return SUBSTITUTE_END;
	}
	public final static String getDefault() {
		return DEFAULT_PREFIX;
	}
	public final static String getGlobal() {
		return GLOBAL_PREFIX;
	}
}
