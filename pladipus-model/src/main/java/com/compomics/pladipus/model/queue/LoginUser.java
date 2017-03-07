package com.compomics.pladipus.model.queue;

import com.compomics.pladipus.model.persist.User;

public class LoginUser {
	private User user;
	
	public LoginUser(User user) {
		this.user = user;
	}
	// TODO on login start fetching user's info, e.g. workflows, and cache here.
	// If a GUI client, will want to send this information on startup to populate screen.
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
}
