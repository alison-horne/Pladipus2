package com.compomics.pladipus.model.core;

import com.compomics.pladipus.model.db.UsersColumn;

/**
 * Application user.
 */
public class User extends UpdateTracked {

	private int id = -1;
	private String username;
	private String email;
	private String passwordEncrypted;
	private boolean active = true;
	private UserRole userRole;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
		trackColumn(UsersColumn.EMAIL.name());
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
		trackColumn(UsersColumn.ACTIVE.name());
	}
	public UserRole getUserRole() {
		if (userRole == null) {
			userRole = new UserRole();
		}
		return userRole;
	}
	public void setUserRole(String role) {
		this.userRole = new UserRole(role);
	}
	public boolean isAdmin() {
		return getUserRole().isAdmin(); 
	}
	public void setAdmin(boolean admin) {
		getUserRole().setAdmin(admin);
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPasswordEncrypted() {
		return passwordEncrypted;
	}
	public void setPasswordEncrypted(String passwordEncrypted) {
		this.passwordEncrypted = passwordEncrypted;
		trackColumn(UsersColumn.PASSWORD.name());
	}
}
