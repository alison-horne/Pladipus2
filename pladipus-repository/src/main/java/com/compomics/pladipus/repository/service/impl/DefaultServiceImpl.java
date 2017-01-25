package com.compomics.pladipus.repository.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.pladipus.model.core.Default;
import com.compomics.pladipus.model.db.DefaultsColumn;
import com.compomics.pladipus.shared.PladipusMessages;
import com.compomics.pladipus.shared.PladipusReportableException;
import com.compomics.pladipus.repository.dao.BaseDAO;
import com.compomics.pladipus.repository.dao.Query;
import com.compomics.pladipus.repository.service.DefaultService;

public class DefaultServiceImpl implements DefaultService {

	@Autowired
	private PladipusMessages exceptionMessages;
	
	@Autowired
	BaseDAO<Default> defaultDAO;
	
	@Transactional(rollbackFor={Exception.class})
	@Override
	public Default insertDefault(Default def) throws PladipusReportableException {
		if (getDefault(def.getName(), def.getUserId()) != null) {
			throw new PladipusReportableException(exceptionMessages.getMessage("db.defaultExists", def.getName()));
		}
		def.setId(defaultDAO.insert(def));
		return def;
	}

	@Transactional(rollbackFor={Exception.class})
	@Override
	public void addType(Default def, String type) throws PladipusReportableException {
		def.setType(type);
		defaultDAO.update(def);
	}

	@Override
	public List<Default> getDefaultsForUser(int userId) throws PladipusReportableException {
		Query query = new Query();
		query.setWhereClause("WHERE " + DefaultsColumn.USER_ID.name() + " = :userid OR "
									  + DefaultsColumn.USER_ID.name() + " IS NULL");
		query.setParameters(new MapSqlParameterSource().addValue("userid", userId));
		return defaultDAO.getList(query);
	}

	private Default getDefault(String name, int userId) throws PladipusReportableException {
		Query query = new Query();
		query.setWhereClause("WHERE " + DefaultsColumn.DEFAULT_NAME.name() + " = :name AND (" 
									  + DefaultsColumn.USER_ID.name() + " = :userid OR "
									  + DefaultsColumn.USER_ID.name() + " IS NULL)");
		query.setParameters(new MapSqlParameterSource().addValue("userid", userId).addValue("name", name));
		return defaultDAO.get(query);
	}
}
