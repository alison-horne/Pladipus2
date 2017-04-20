package com.compomics.pladipus.base.queuemapper;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.annotation.Autowired;

import com.compomics.pladipus.base.BatchControl;
import com.compomics.pladipus.base.DefaultsControl;
import com.compomics.pladipus.base.QueueControl;
import com.compomics.pladipus.base.UserControl;
import com.compomics.pladipus.base.WorkflowControl;
import com.compomics.pladipus.model.persist.User;
import com.compomics.pladipus.model.queue.LoginUser;
import com.compomics.pladipus.model.queue.messages.client.ClientTaskStatus;
import com.compomics.pladipus.model.queue.messages.client.ClientToControlMessage;
import com.compomics.pladipus.model.queue.messages.client.ControlToClientMessage;
import com.compomics.pladipus.shared.PladipusMessages;
import com.compomics.pladipus.shared.PladipusReportableException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ClientTaskMapper {
	
	@Autowired
	private WorkflowControl workflowControl;
	
	@Autowired
	private BatchControl batchControl;
	
	@Autowired
	private UserControl userControl;
	
	@Autowired
	private QueueControl queueControl;
	
	@Autowired
	private DefaultsControl defaultsControl;
	
	@Autowired
	private ObjectMapper jsonMapper;
	
	@Autowired
	private PladipusMessages exceptionMessages;
	
	private ConcurrentMap<String, LoginUser> clientLogins = new ConcurrentHashMap<String, LoginUser>();
	
	private User getUser(String clientId) {
		LoginUser loggedIn = clientLogins.get(clientId);
		if (loggedIn != null) return loggedIn.getUser();
		throw new IllegalArgumentException();
	}
	
	private void doLogin(String username, String password, String clientId) throws PladipusReportableException {
		clientLogins.put(clientId, userControl.getLoginUser(username, password));
	}
	
	public ControlToClientMessage doMessageTask(ClientToControlMessage msg, String clientId) {
		ControlToClientMessage response = new ControlToClientMessage();
		try {
			switch(msg.getTask()) {
				case CREATE_WORKFLOW:
					workflowControl.createWorkflow(msg.getFileContent(), getUser(clientId));
					break;
				case LOGIN_USER:
					doLogin(msg.getUsername(), msg.getPassword(), clientId);
					break;
				case REPLACE_WORKFLOW:
					workflowControl.replaceWorkflow(msg.getFileContent(), getUser(clientId));
					break;
				case START_BATCH:
					queueControl.process(msg.getBatchName(), getUser(clientId));
					break;
				case ABORT:
					queueControl.abort(msg.getBatchName(), getUser(clientId));
					break;
				case ADD_DEFAULT:
					defaultsControl.addDefault(msg.getDefaultName(), msg.getDefaultValue(), msg.getDefaultType(), getUser(clientId));
					break;
				case CREATE_BATCH:
					batchControl.createBatch(msg.getFileContent(), msg.getWorkflowName(), msg.getBatchName(), getUser(clientId));
					break;
				case GENERATE_HEADERS:
					mapOutput(response, batchControl.generateHeaders(msg.getWorkflowName(), getUser(clientId)));
					break;
				case REPLACE_BATCH:
					batchControl.replaceBatch(msg.getFileContent(), msg.getWorkflowName(), msg.getBatchName(), getUser(clientId));
					break;
				case RESTART_BATCH:
					queueControl.restart(msg.getBatchName(), getUser(clientId));
					break;
				case STATUS:
					// TODO
					break;
				default:
					break;
			}
			response.setStatus(ClientTaskStatus.OK);
		} catch (PladipusReportableException e) {
			response.setErrorMsg(e.getMessage());
			response.setStatus(ClientTaskStatus.ERROR);
		} catch (IllegalArgumentException e) {
			response.setStatus(ClientTaskStatus.NO_LOGIN); 
		}
		return response;
	}
	
	private void mapOutput(ControlToClientMessage response, Object output) throws PladipusReportableException {
		if (output != null) {
			try {
				response.setContent(jsonMapper.writeValueAsString(output));
			} catch (JsonProcessingException e) {
				throw new PladipusReportableException(exceptionMessages.getMessage("base.mapOutput"));
			}
		}
	}
}
