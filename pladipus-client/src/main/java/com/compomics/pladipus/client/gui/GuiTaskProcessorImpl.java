package com.compomics.pladipus.client.gui;

import org.springframework.beans.factory.annotation.Autowired;

import com.compomics.pladipus.client.BatchCsvIO;
import com.compomics.pladipus.client.queue.MessageSender;
import com.compomics.pladipus.model.persist.Workflow;
import com.compomics.pladipus.model.queue.messages.client.ClientTask;
import com.compomics.pladipus.model.queue.messages.client.ClientToControlMessage;
import com.compomics.pladipus.shared.PladipusReportableException;
import com.compomics.pladipus.shared.XMLHelper;

public class GuiTaskProcessorImpl implements GuiTaskProcessor {

	// TODO - separate class to do messages that cmdLine and GUI can call in and 
	// print output, etc. as required.  This is just temporary while writing GUI code...
	// TODO - encode password at this end.
	
	@Autowired
	private BatchCsvIO batchCsvIO;
	
	@Autowired
	private XMLHelper<Workflow> workflowXMLHelper;
	
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
	public Workflow getWorkflowFromFilePath(String path) throws PladipusReportableException {
		return workflowXMLHelper.parseXml(batchCsvIO.fileToString(path));
	}
	
	@Override
	public Workflow getWorkflowFromXml(String xml) throws PladipusReportableException {
		return workflowXMLHelper.parseXml(xml);
	}
	
	@Override
	public void login(String username, String password) throws PladipusReportableException {
		if (!TestData.login(username, password)) throw new PladipusReportableException("Incorrect username / password");
	}
}
