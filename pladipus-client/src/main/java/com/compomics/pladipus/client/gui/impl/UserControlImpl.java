package com.compomics.pladipus.client.gui.impl;

import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;

import com.compomics.pladipus.client.gui.UserControl;
import com.compomics.pladipus.client.queue.MessageSender;
import com.compomics.pladipus.model.queue.messages.client.ClientTask;
import com.compomics.pladipus.model.queue.messages.client.ClientToControlMessage;
import com.compomics.pladipus.model.queue.messages.client.ControlToClientMessage;
import com.compomics.pladipus.shared.PladipusReportableException;

public class UserControlImpl implements UserControl {

	@Autowired
	private MessageSender messageSender;
	private ResourceBundle resources = ResourceBundle.getBundle("guiTexts/login");
	
	// TODO add admin options
	private String username = null;
	
	@Override
	public boolean isLoggedIn() {
		return (username != null);
	}

	@Override
	public String getLoggedInUsername() {
		return username;
	}

	@Override
	public void login(String username, String password) throws PladipusReportableException {
		ClientToControlMessage msg = new ClientToControlMessage(ClientTask.LOGIN_USER);
		msg.setUsername(username);
		msg.setPassword(password);
		sendMessage(msg);
		this.username = username;
	}
	
	@Override
	public void logout() {
		this.username = null;
	}
	
	private void sendMessage(ClientToControlMessage msg) throws PladipusReportableException {
		ControlToClientMessage response = messageSender.makeRequest(msg);
		switch (response.getStatus()) {
			case ERROR:
				throw new PladipusReportableException(response.getErrorMsg());
			case OK:
				break;
			default:
				throw new PladipusReportableException(resources.getString("login.error"));
		}
	}
}
