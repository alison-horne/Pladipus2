package com.compomics.pladipus.repository.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.compomics.pladipus.model.core.Default;
import com.compomics.pladipus.model.db.DefaultsColumn;

/**
 * Map result of common_defaults SELECT query into Default object
 */
public class DefaultMapper implements RowMapper<Default> {
	 
	public Default mapRow(ResultSet rs, int rowNum) throws SQLException {
		Default def = new Default();
		def.setId(rs.getInt(DefaultsColumn.DEFAULT_ID.toString()));
		def.setName(rs.getString(DefaultsColumn.DEFAULT_NAME.toString()));
		def.setType(rs.getString(DefaultsColumn.DEFAULT_TYPE.toString()));
		def.setUserId(rs.getInt(DefaultsColumn.USER_ID.toString()));
		def.setValue(rs.getString(DefaultsColumn.DEFAULT_VALUE.toString()));
		def.clearTrackedChanges();
		return def;
	}
}
