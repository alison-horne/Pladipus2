package com.compomics.pladipus.client.queue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.compomics.pladipus.model.queue.messages.client.ClientToControlMessage;
import com.compomics.pladipus.model.queue.messages.client.ControlToClientMessage;
import com.compomics.pladipus.shared.PladipusMessages;
import com.compomics.pladipus.shared.PladipusReportableException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MessageSenderImpl implements MessageSender {
	
	@Autowired
	private PladipusMessages exceptionMessages;
	
	@Autowired
	private MessageMap messageMap;
	
	@Autowired
	private BeanFactory beanFactory;
	
	@Autowired
	private ObjectMapper jsonMapper;
	
	public String makeRequest(ClientToControlMessage message) throws PladipusReportableException {
		try {
			ExecutorService es = Executors.newSingleThreadExecutor();
			Future<String> response = es.submit(beanFactory.getBean(MessageTask.class, jsonMapper.writeValueAsString(message)));
			messageMap.addFuture(response);
			String responseText = response.get();
			messageMap.removeFuture(response);
			es.shutdown();
		    if ((responseText != null) && !responseText.isEmpty()) {
		    	ControlToClientMessage responseMessage = jsonMapper.readValue(responseText, ControlToClientMessage.class);
		    	checkResponseStatus(responseMessage);
		    	return responseMessage.getContent();
		    } else {
			    throw new PladipusReportableException(exceptionMessages.getMessage("clierror.timeout"));
		    }
		} catch (InterruptedException e) {
			throw new PladipusReportableException(exceptionMessages.getMessage("info.taskinterrupt"));
		} catch (Exception e) {
			throw new PladipusReportableException(e.getMessage());
		}
	}
	
	private void checkResponseStatus(ControlToClientMessage msg) throws PladipusReportableException {
		switch (msg.getStatus()) {
			case ERROR:
				throw new PladipusReportableException(msg.getErrorMsg());
			case NO_LOGIN:
				throw new PladipusReportableException(exceptionMessages.getMessage("clierror.login"));
			case OK:
				return;
		}	
	}
}
