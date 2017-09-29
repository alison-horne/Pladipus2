package com.compomics.pladipus.model.queue;

import com.compomics.pladipus.model.core.GuiSetup;
import com.compomics.pladipus.model.persist.User;

public class LoginUser {
	private User user;
	private GuiSetup setup;
	
	public LoginUser(User user) {
		this.user = user;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public void setSetup(GuiSetup setup) {
		this.setup = setup;
	}
	public GuiSetup getSetup() {
		return setup;
	}
}
