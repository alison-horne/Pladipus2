package com.compomics.pladipus.model.core;

import java.util.HashSet;
import java.util.Set;

/**
 * Parameter for database
 */
public class Parameter extends UpdateTracked {
	
	private int id = -1;
	private int enclosingId = -1;
	private String parameterName;
	private Set<String> values = new HashSet<String>();
	
	public Parameter(int enclosingId) {
		this.enclosingId = enclosingId;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getEnclosingId() {
		return enclosingId;
	}
	public void setEnclosingId(int enclosingId) {
		this.enclosingId = enclosingId;
	}
	public String getParameterName() {
		return parameterName;
	}
	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}
	public Set<String> getValues() {
		return values;
	}
	public void addValue(String value) {
		values.add(value);
	}
}
