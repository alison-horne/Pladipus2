package com.compomics.pladipus.repository.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.pladipus.model.core.User;
import com.compomics.pladipus.model.db.UsersColumn;
import com.compomics.pladipus.shared.PladipusMessages;
import com.compomics.pladipus.shared.PladipusReportableException;
import com.compomics.pladipus.repository.dao.BaseDAO;
import com.compomics.pladipus.repository.dao.Query;
import com.compomics.pladipus.repository.helpers.PasswordEncryptor;
import com.compomics.pladipus.repository.service.UserService;

public class UserServiceImpl implements UserService {

	@Autowired
	private BaseDAO<User> userRoleDAO;
	
	@Autowired
	private BaseDAO<User> userDAO;
	
	@Autowired
	private PasswordEncryptor basicEncryptor;
	
	@Autowired
	private PladipusMessages exceptionMessages;
	
	@Transactional(rollbackFor={Exception.class})
	@Override
	public User createUser(User user, String password) throws PladipusReportableException {
		user.setPasswordEncrypted(basicEncryptor.encryptPassword(password));
		user.setId(userDAO.insert(user));
		userRoleDAO.insert(user);
		return user;
	}

	@Override
	public User getUserByName(String username) throws PladipusReportableException {
		Query query = new Query();
		query.setWhereClause("WHERE " + UsersColumn.USER_NAME.toString() + " = :username");
		query.setParameters(new MapSqlParameterSource().addValue("username", username));
		return userDAO.get(query);
	}

	@Override
	public List<User> getAllUsers() throws PladipusReportableException {
		return userDAO.getList(new Query());
	}

	@Override
	public User login(String username, String password) throws PladipusReportableException {
		User user = getUserByName(username);
		if (user != null) {
			if (basicEncryptor.checkPassword(password, user.getPasswordEncrypted())) {
				if (user.isActive()) {
					return user;
				} else {
					throw new PladipusReportableException(exceptionMessages.getMessage("db.userInactive"));
				}
			} else {
				throw new PladipusReportableException(exceptionMessages.getMessage("db.wrongPassword"));
			}
		} else {
			throw new PladipusReportableException(exceptionMessages.getMessage("db.userNotFound"));
		}
	}

	@Transactional(rollbackFor={Exception.class})
	@Override
	public void setActive(User user, boolean active) throws PladipusReportableException {
		user.setActive(active);
		userDAO.update(user);
	}

	@Transactional(rollbackFor={Exception.class})
	@Override
	public void setAdmin(User user, boolean admin) throws PladipusReportableException {
		user.setAdmin(admin);
		userRoleDAO.update(user);
	}

	@Transactional(rollbackFor={Exception.class})
	@Override
	public void changePassword(User user, String newPassword) throws PladipusReportableException {
		user.setPasswordEncrypted(basicEncryptor.encryptPassword(newPassword));
		userDAO.update(user);
	}

}
