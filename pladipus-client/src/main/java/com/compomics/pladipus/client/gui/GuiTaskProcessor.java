package com.compomics.pladipus.client.gui;

import org.springframework.beans.factory.annotation.Autowired;

import com.compomics.pladipus.client.ClientTaskProcessor;
import com.compomics.pladipus.client.queue.MessageSender;
import com.compomics.pladipus.model.queue.messages.client.ClientTask;
import com.compomics.pladipus.model.queue.messages.client.ClientToControlMessage;
import com.compomics.pladipus.shared.PladipusReportableException;

public class GuiTaskProcessor implements ClientTaskProcessor {

	// TODO - separate class to do messages that cmdLine and GUI can call in and 
	// print output, etc. as required.  This is just temporary while writing GUI code...
	// TODO - encode password at this end.
	
//	@Autowired
//	private MessageSender messageSender;
//	
//	@Override
//	public void login(String username, String password) throws PladipusReportableException {
//		ClientToControlMessage msg = new ClientToControlMessage(ClientTask.LOGIN_USER);
//		msg.setUsername(username);
//		msg.setPassword(password);
//		messageSender.makeRequest(msg);
//	}
	
	@Override
	public void login(String username, String password) throws PladipusReportableException {
		if (!TestData.login(username, password)) throw new PladipusReportableException("Incorrect username / password");
	}

	@Override
	public void doTemplateTask(String filepath, boolean force) throws PladipusReportableException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doBatchTask(String filepath, String workflowName, String batchName, boolean force)
			throws PladipusReportableException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doProcessTask(String batchName, boolean force) throws PladipusReportableException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doStatusTask(String batchName) throws PladipusReportableException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doGenerateTask(String filepath, String workflowName, boolean force) throws PladipusReportableException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doDefaultTask(String defaultName, String defaultValue, String defaultType)
			throws PladipusReportableException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doAbortTask(String batchName) throws PladipusReportableException {
		// TODO Auto-generated method stub
		
	}
}
