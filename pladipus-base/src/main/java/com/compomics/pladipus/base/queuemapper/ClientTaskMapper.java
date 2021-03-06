package com.compomics.pladipus.base.queuemapper;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.annotation.Autowired;

import com.compomics.pladipus.base.BatchControl;
import com.compomics.pladipus.base.DefaultsControl;
import com.compomics.pladipus.base.QueueControl;
import com.compomics.pladipus.base.ToolControl;
import com.compomics.pladipus.base.UserControl;
import com.compomics.pladipus.base.WorkflowControl;
import com.compomics.pladipus.model.core.DefaultOverview;
import com.compomics.pladipus.model.core.GuiSetup;
import com.compomics.pladipus.model.core.ToolInformation;
import com.compomics.pladipus.model.core.WorkflowOverview;
import com.compomics.pladipus.model.persist.Batch;
import com.compomics.pladipus.model.persist.Default;
import com.compomics.pladipus.model.persist.User;
import com.compomics.pladipus.model.persist.Workflow;
import com.compomics.pladipus.model.queue.LoginUser;
import com.compomics.pladipus.model.queue.messages.client.ClientTaskStatus;
import com.compomics.pladipus.model.queue.messages.client.ClientToControlMessage;
import com.compomics.pladipus.model.queue.messages.client.ControlToClientMessage;
import com.compomics.pladipus.shared.PladipusMessages;
import com.compomics.pladipus.shared.PladipusReportableException;
import com.compomics.pladipus.tools.core.ToolInfo;
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
	private ToolControl toolControl;
	
	@Autowired
	private ObjectMapper jsonMapper;
	
	@Autowired
	private PladipusMessages exceptionMessages;
	
	private ConcurrentMap<String, LoginUser> clientLogins = new ConcurrentHashMap<String, LoginUser>();
	
	private LoginUser getLoginUser(String clientId, String username) {
		LoginUser loggedIn = clientLogins.get(clientId);
		if (loggedIn != null && loggedIn.getUser().getUserName().equalsIgnoreCase(username)) return loggedIn;
		throw new IllegalArgumentException();
	}
	private User getUser(String clientId, String username) {
		return getLoginUser(clientId, username).getUser();
	}
	
	private void doLogin(String username, String password, String clientId) throws PladipusReportableException {
		clientLogins.put(clientId, userControl.getLoginUser(username, password));
	}
	
	public ControlToClientMessage doMessageTask(ClientToControlMessage msg, String clientId) {
		ControlToClientMessage response = new ControlToClientMessage();
		try {
			switch(msg.getTask()) {
				case CREATE_WORKFLOW:
					workflowControl.createWorkflow(msg.getFileContent(), getUser(clientId, msg.getUsername()));
					break;
				case LOGIN_USER:
					doLogin(msg.getUsername(), msg.getPassword(), clientId);
					break;
				case CREATE_USER:
					userControl.createUser(msg.getUsername(), msg.getEmail(), msg.getPassword());
					break;
				case REPLACE_WORKFLOW:
					mapOutput(response, batchControl.generateHeadersFromWorkflow(workflowControl.replaceWorkflow(msg.getFileContent(), getUser(clientId, msg.getUsername()))));
					break;
				case DELETE:
					User user = getUser(clientId, msg.getUsername());
					if (msg.getWorkflowName() != null) {
						workflowControl.deactivateWorkflow(msg.getWorkflowName(), user);
					} else if (msg.getBatchId() != null) {
						batchControl.deactivateBatch(msg.getBatchId());
					}
					break;
				case START_BATCH:
					queueControl.process(msg.getBatchName(), getUser(clientId, msg.getUsername()));
					break;
				case ABORT:
					User us = getUser(clientId, msg.getUsername());
					if (msg.getBatchRunId() != null) {
						queueControl.abortBatchRun(msg.getBatchRunId());
					} else if (msg.getBatchId() != null) {
						queueControl.abortBatch(msg.getBatchId());
					} else {
						queueControl.abort(msg.getBatchName(), us);
					}
					break;
				case ADD_DEFAULT:
					User u = getUser(clientId, msg.getUsername());
					if (msg.getDefaultGlobal() != null && msg.getDefaultGlobal()) u = null;
					defaultsControl.addDefault(msg.getDefaultName(), msg.getDefaultValue(), msg.getDefaultType(), u);
					break;
				case CREATE_BATCH:
					Batch batch = batchControl.createBatch(msg.getFileContent(), msg.getWorkflowName(), msg.getBatchName(), getUser(clientId, msg.getUsername()));
					if (msg.getBatchRun() != null && msg.getBatchRun()) {
						queueControl.processBatch(batch, getUser(clientId, msg.getUsername()));
					}
					mapOutput(response, batch.getBatchOverview());
					break;
				case GENERATE_HEADERS:
					mapOutput(response, batchControl.generateHeaders(msg.getWorkflowName(), getUser(clientId, msg.getUsername())));
					break;
				case REPLACE_BATCH:
					Batch repBatch = batchControl.replaceBatch(msg.getFileContent(), msg.getWorkflowName(), msg.getBatchName(), getUser(clientId, msg.getUsername()));
					if (msg.getBatchRun() != null && msg.getBatchRun()) {
						queueControl.processBatch(repBatch, getUser(clientId, msg.getUsername()));
					}
					mapOutput(response, repBatch.getBatchOverview());
					break;
				case RESTART_BATCH:
					if (msg.getBatchRunId() != null) {
						queueControl.restartBatchRun(msg.getBatchRunId(), msg.getBatchId(), getUser(clientId, msg.getUsername()));
					} else if (msg.getBatchId() != null) {
						queueControl.restartBatch(msg.getBatchId(), getUser(clientId, msg.getUsername()));
					} else {
						queueControl.restart(msg.getBatchName(), getUser(clientId, msg.getUsername()));
					}
					break;
				case STATUS:
					if (msg.getBatchId() != null && getUser(clientId, msg.getUsername()) != null) {
						mapOutput(response, queueControl.batchStatus(msg.getBatchId()));
					}
					break;
				case GUI_SETUP:
					mapOutput(response, getGuiSetup(getLoginUser(clientId, msg.getUsername())));
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
		} catch (Exception e) {
			response.setStatus(ClientTaskStatus.ERROR);
			response.setErrorMsg(exceptionMessages.getMessage("db.errorGetQuery", e.getMessage()));
		}
		return response;
	}
	
	private void mapOutput(ControlToClientMessage response, Object output) throws PladipusReportableException {
		if (output != null) {
			try {
				response.setContent(jsonMapper.writer().writeValueAsString(output));
			} catch (JsonProcessingException e) {
				throw new PladipusReportableException(exceptionMessages.getMessage("base.mapOutput"));
			}
		}
	}
	
	private GuiSetup getGuiSetup(LoginUser user) throws PladipusReportableException {
		GuiSetup setup = new GuiSetup();
		for (Entry<String, ToolInfo> tool: toolControl.getAllToolInfo().entrySet()) {
			setup.addTool(new ToolInformation(tool.getKey(), tool.getValue().getInputParams(), tool.getValue().getOutputs()));
		}
		for (Default def: defaultsControl.getDefaults(user.getUser())) {
			setup.addDefault(new DefaultOverview(def.getName(), def.getValue(), def.getType(), (def.getUser()==null)));
		}
		for (Workflow wf: workflowControl.getActiveWorkflows(user.getUser())) {
			WorkflowOverview wo = new WorkflowOverview(wf.getName(), wf.getTemplateXml());
			wo.setHeaders(batchControl.generateHeadersFromWorkflow(wf));
			wo.setBatches(batchControl.getBatchOverviewsForWorkflow(wf));
			setup.addWorkflow(wo);
		}
		user.setSetup(setup);
		return setup;
	}
}
