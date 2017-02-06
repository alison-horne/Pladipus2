package com.compomics.pladipus.repository.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.pladipus.shared.PladipusMessages;
import com.compomics.pladipus.shared.PladipusReportableException;
import com.compomics.pladipus.model.hibernate.User;
import com.compomics.pladipus.repository.helpers.PasswordEncryptor;
import com.compomics.pladipus.repository.hibernate.UserRepository;
import com.compomics.pladipus.repository.service.UserService;

public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private PasswordEncryptor basicEncryptor;
	
	@Autowired
	private PladipusMessages exceptionMessages;
	
	@Transactional(rollbackFor={Exception.class})
	@Override
	public void createUser(User user, String password) throws PladipusReportableException {
		user.setPassword(basicEncryptor.encryptPassword(password));
		userRepo.persist(user);
	}

	@Override
	public User getUserByName(String username) throws PladipusReportableException {
		return userRepo.findUserByName(username);
	}

	@Override
	public List<User> getAllUsers() throws PladipusReportableException {
		return userRepo.findAll();
	}

	@Override
	public User login(String username, String password) throws PladipusReportableException {
		User user = getUserByName(username);
		if (user != null) {
			if (basicEncryptor.checkPassword(password, user.getPassword())) {
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
		userRepo.merge(user);
	}

	@Transactional(rollbackFor={Exception.class})
	@Override
	public void setAdmin(User user, boolean admin) throws PladipusReportableException {
		user.setAdmin(admin);
		userRepo.merge(user);
	}

	@Transactional(rollbackFor={Exception.class})
	@Override
	public void changePassword(User user, String newPassword) throws PladipusReportableException {
		user.setPassword(basicEncryptor.encryptPassword(newPassword));
		userRepo.merge(user);
	}

}
