package com.compomics.pladipus.model.core;

/**
 * User Role - administrator or regular user
 */
public class UserRole {
	public enum Role {ADMIN, USER};
	
	private Role role;
	
	public UserRole() {
		this.role = Role.USER;
	}
	
	public UserRole(String role) {
		if (role == null || role.isEmpty()) {
			this.role = Role.USER;
		}
		else {
			this.role = Role.valueOf(role);
		}
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
	
	public boolean isAdmin() {
		return role.equals(Role.ADMIN);
	}
	
	public void setAdmin(boolean admin) {
		if (admin) {
			this.role = Role.ADMIN;
		} else {
			this.role = Role.USER;
		}
	}
}
