package com.compomics.pladipus.model.queue.messages.client;

public enum ClientTask {
	LOGIN_USER,
	CREATE_WORKFLOW,
	REPLACE_WORKFLOW,
	CREATE_BATCH,
	REPLACE_BATCH,
	GENERATE_HEADERS,
	START_BATCH,
	RESTART_BATCH,
	STATUS,
	ADD_DEFAULT,
	ABORT,
	GUI_SETUP
}
