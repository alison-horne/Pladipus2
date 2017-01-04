package com.compomics.pladipus.model.core;

import com.compomics.pladipus.model.db.DefaultsColumn;

/**
 * Default parameter values
 */
public class Default extends UpdateTracked {
	
	private int id = -1;
	private int userId = -1;
	private String name;
	private String value;
	private String type;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		if (userId > 0) {
			this.userId = userId;
		}
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
		trackColumn(DefaultsColumn.DEFAULT_TYPE.name());
	}
}
