package com.compomics.pladipus.client.queue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.compomics.pladipus.model.queue.messages.client.ClientTaskStatus;
import com.compomics.pladipus.model.queue.messages.client.ClientToControlMessage;
import com.compomics.pladipus.model.queue.messages.client.ControlToClientMessage;
import com.compomics.pladipus.shared.PladipusMessages;
import com.compomics.pladipus.shared.PladipusReportableException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

public class MessageSenderImpl implements MessageSender {
	
	@Autowired
	private PladipusMessages exceptionMessages;
	
	@Autowired
	private MessageMap messageMap;
	
	@Autowired
	private BeanFactory beanFactory;
	
	@Autowired
	private ObjectMapper jsonMapper;
	
	public ControlToClientMessage makeRequest(ClientToControlMessage message) throws PladipusReportableException {
		ControlToClientMessage responseMessage;
		try {
			ExecutorService es = Executors.newSingleThreadExecutor();
			Future<String> response = es.submit(beanFactory.getBean(MessageTask.class, jsonMapper.writer().writeValueAsString(message)));
			messageMap.addFuture(response);
			String responseText = response.get();
			messageMap.removeFuture(response);
			es.shutdown();
		    if ((responseText != null) && !responseText.isEmpty()) {
		    	ObjectReader reader = jsonMapper.readerFor(ControlToClientMessage.class);
		    	responseMessage = reader.readValue(responseText);
		    } else {
			    responseMessage = new ControlToClientMessage();
			    responseMessage.setStatus(ClientTaskStatus.TIMEOUT);
		    }
		} catch (InterruptedException e) {
			throw new PladipusReportableException(exceptionMessages.getMessage("info.taskinterrupt"));
		} catch (Exception e) {
			throw new PladipusReportableException(e.getMessage());
		}
		return responseMessage;
	}
}
