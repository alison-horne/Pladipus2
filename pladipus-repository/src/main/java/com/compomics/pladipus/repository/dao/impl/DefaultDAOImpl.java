package com.compomics.pladipus.repository.dao.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.RowMapper;

import com.compomics.pladipus.model.core.Default;
import com.compomics.pladipus.model.db.DbTable;
import com.compomics.pladipus.model.db.DefaultsColumn;
import com.compomics.pladipus.repository.dao.BaseDAOImpl;
import com.compomics.pladipus.repository.mappers.DefaultMapper;

/**
 * Access common_defaults database table
 */
public class DefaultDAOImpl extends BaseDAOImpl<Default>{
	private static final String TYPE = "default";
	
	public DefaultDAOImpl(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	protected Map<String, Object> mapDbColumns(Default def) {
		Map<String, Object> columnMap = new HashMap<String, Object>();
		columnMap.put(DefaultsColumn.DEFAULT_ID.name(), (def.getId() < 0) ? null : def.getId());
		columnMap.put(DefaultsColumn.USER_ID.name(), (def.getUserId() < 0) ? null : def.getUserId());
		columnMap.put(DefaultsColumn.DEFAULT_VALUE.name(), def.getValue());
		columnMap.put(DefaultsColumn.DEFAULT_NAME.name(), def.getName());
		columnMap.put(DefaultsColumn.DEFAULT_TYPE.name(), def.getType());
		return columnMap;
	}

	@Override
	protected String getTableName() {
		return DbTable.COMMON_DEFAULTS.name();
	}

	@Override
	protected String getType() {
		return TYPE;
	}

	@Override
	protected RowMapper<Default> getRowMapper() {
		return new DefaultMapper();
	}

	@Override
	protected boolean isInsertValid(List<String> insertColumns) {
		if (insertColumns.contains(DefaultsColumn.DEFAULT_NAME.name()) &&
			insertColumns.contains(DefaultsColumn.DEFAULT_VALUE.name())) {
			return true;
		}
		return false;
	}

	@Override
	protected List<String> getUniqueIdentifier() {
		return Arrays.asList(DefaultsColumn.DEFAULT_ID.name());
	}
	
}
