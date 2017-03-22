package com.compomics.pladipus.queue;

import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;

import com.compomics.pladipus.base.queuemapper.ClientTaskMapper;
import com.compomics.pladipus.model.queue.messages.client.ClientToControlMessage;
import com.compomics.pladipus.model.queue.messages.client.ControlToClientMessage;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ClientTaskProcessor extends Thread {
	
	@Autowired
	private ControlClientProducer clientProducer;
	
	@Autowired
	private ClientTaskMapper clientTaskMapper;
	
	@Autowired
	private ObjectMapper jsonMapper;
	
	@Autowired
	private String clientIdProperty;
	
	private TextMessage msg;
	private String clientId;
	private String corrId;
	
	public ClientTaskProcessor(TextMessage msg) {
		this.msg = msg;
	}
	
	public void run() {
		try {
			clientId = msg.getStringProperty(clientIdProperty);
			corrId = msg.getJMSCorrelationID(); // TODO if either of these is null, do not process, just log error.
			ControlToClientMessage response = clientTaskMapper.doMessageTask(jsonMapper.readValue(msg.getText(), ClientToControlMessage.class), clientId);
			clientProducer.sendMessage(jsonMapper.writeValueAsString(response), corrId, clientId);
		} catch (JMSException e) {
			// TODO log error, leave client to timeout
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
