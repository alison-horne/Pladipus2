package com.compomics.pladipus.client.cmdline;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.cli.ParseException;
import org.springframework.beans.factory.annotation.Autowired;

import com.compomics.pladipus.client.BatchCsvIO;
import com.compomics.pladipus.client.queue.MessageSender;
import com.compomics.pladipus.model.queue.messages.client.ClientTask;
import com.compomics.pladipus.model.queue.messages.client.ClientToControlMessage;
import com.compomics.pladipus.model.queue.messages.client.ControlToClientMessage;
import com.compomics.pladipus.shared.PladipusMessages;
import com.compomics.pladipus.shared.PladipusReportableException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CliTaskProcessorImpl implements CliTaskProcessor {

	@Autowired
	private CommandLineIO cmdLineIO;
	
	@Autowired
	private ResourceBundle cmdLine;
	
	@Autowired
	private PladipusMessages exceptionMessages;
	
	@Autowired
	private MessageSender messageSender;
	
	@Autowired
	private ObjectMapper jsonMapper;
	
	@Autowired
	private BatchCsvIO batchCsvIO;
	
	@Override
	public void login(String username, String password) throws ParseException, PladipusReportableException {
		if (password == null) {
			password = cmdLineIO.getPassword();
		}
		if (password == null) {
			throw new ParseException(cmdLine.getString("error.password"));
		}
		ClientToControlMessage msg = new ClientToControlMessage(ClientTask.LOGIN_USER);
		msg.setUsername(username);
		msg.setPassword(password);
		makeRequest(msg);
	}
	
	@Override
	public void doTemplateTask(String filepath, boolean force) throws PladipusReportableException {
		ClientToControlMessage msg;
		if (force) {
			msg = new ClientToControlMessage(ClientTask.REPLACE_WORKFLOW);
		} else {
			msg = new ClientToControlMessage(ClientTask.CREATE_WORKFLOW);
		}
		msg.setFileContent(batchCsvIO.fileToString(filepath));
		makeRequest(msg);
		cmdLineIO.printOutput(cmdLine.getString("workflow.updated"));
	}
	
	@Override
	public void doBatchTask(String filepath, String workflowName, String batchName, boolean force) throws PladipusReportableException {
		ClientToControlMessage msg;
		if (force) {
			msg = new ClientToControlMessage(ClientTask.REPLACE_BATCH);
		} else {
			msg = new ClientToControlMessage(ClientTask.CREATE_BATCH);
		}
		msg.setFileContent(batchCsvIO.fileToString(filepath));
		msg.setWorkflowName(workflowName);
		if ((batchName == null) || batchName.isEmpty()) batchName = batchCsvIO.getFileName(filepath);
		msg.setBatchName(batchName);
		makeRequest(msg);
		cmdLineIO.printOutput(cmdLine.getString("batch.updated"));
	}
	
	@Override
	public void doProcessTask(String batchName, boolean force) throws PladipusReportableException {
		ClientToControlMessage msg;
		if (force) {
			msg = new ClientToControlMessage(ClientTask.RESTART_BATCH);
		} else {
			msg = new ClientToControlMessage(ClientTask.START_BATCH);
		}
		msg.setBatchName(batchName);
		makeRequest(msg);
		cmdLineIO.printOutput(MessageFormat.format(cmdLine.getString("process.success"), (batchName != null && !batchName.isEmpty()) ? batchName : "all"));
	}
	
	@Override
	public void doStatusTask(String batchName) throws PladipusReportableException {
		ClientToControlMessage msg = new ClientToControlMessage(ClientTask.STATUS);
		msg.setBatchName(batchName);
		cmdLineIO.printOutput(makeRequest(msg)); //TODO make output prettier at this end?
	}
	
	@Override
	public void doGenerateTask(String filepath, String workflowName, boolean force) throws PladipusReportableException {
		batchCsvIO.checkFileValid(filepath, force);
		ClientToControlMessage msg = new ClientToControlMessage(ClientTask.GENERATE_HEADERS);
		msg.setWorkflowName(workflowName);
		String headers = makeRequest(msg);
		try {
			batchCsvIO.writeHeaderFile(filepath, jsonMapper.readValue(headers, new TypeReference<List<String>>() {}));
		} catch (IOException e) {
			throw new PladipusReportableException(exceptionMessages.getMessage("batch.fileWriteError", e.getMessage()));
		}
		cmdLineIO.printOutput(MessageFormat.format(cmdLine.getString("generate.success"), filepath));
	}
	
	@Override
	public void doDefaultTask(String defaultName, String defaultValue, String defaultType) throws PladipusReportableException {
		ClientToControlMessage msg = new ClientToControlMessage(ClientTask.ADD_DEFAULT);
		msg.setDefaultName(defaultName);
		msg.setDefaultValue(defaultValue);
		msg.setDefaultType(defaultType);
		makeRequest(msg);
		cmdLineIO.printOutput(MessageFormat.format(cmdLine.getString("default.success"), defaultName));
	}
	
	@Override
	public void doAbortTask(String batchName) throws PladipusReportableException {
		// TODO check if sure
		ClientToControlMessage msg = new ClientToControlMessage(ClientTask.ABORT);
		msg.setBatchName(batchName);
		makeRequest(msg);
		cmdLineIO.printOutput(MessageFormat.format(cmdLine.getString("abort.success"), (batchName != null && !batchName.isEmpty()) ? batchName : "all"));
	}
	
	private String makeRequest(ClientToControlMessage msg) throws PladipusReportableException {
		ControlToClientMessage responseMessage = messageSender.makeRequest(msg);
    	checkResponseStatus(responseMessage);
    	return responseMessage.getContent();
	}
	
	private void checkResponseStatus(ControlToClientMessage msg) throws PladipusReportableException {
		switch (msg.getStatus()) {
			case ERROR:
				throw new PladipusReportableException(msg.getErrorMsg());
			case NO_LOGIN:
				throw new PladipusReportableException(exceptionMessages.getMessage("clierror.login"));
			case OK:
				return;
			case TIMEOUT:
				throw new PladipusReportableException(exceptionMessages.getMessage("clierror.timeout"));
		}	
	}
}
