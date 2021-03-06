package com.compomics.pladipus.queue;

import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;

import com.compomics.pladipus.base.queuemapper.ClientTaskMapper;
import com.compomics.pladipus.model.queue.MessageSelector;
import com.compomics.pladipus.model.queue.messages.client.ClientToControlMessage;
import com.compomics.pladipus.model.queue.messages.client.ControlToClientMessage;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

public class ClientTaskProcessor extends Thread {
	
	@Autowired
	private ControlClientProducer clientProducer;
	
	@Autowired
	private ClientTaskMapper clientTaskMapper;
	
	@Autowired
	private ObjectMapper jsonMapper;
	
	private TextMessage msg;
	private String clientId;
	private String corrId;
	
	public ClientTaskProcessor(TextMessage msg) {
		this.msg = msg;
	}
	
	public void run() {
		try {
			clientId = msg.getStringProperty(MessageSelector.CLIENT_ID.name());
			corrId = msg.getJMSCorrelationID(); // TODO if either of these is null, do not process, just log error.
			ObjectReader reader = jsonMapper.readerFor(ClientToControlMessage.class);
			ControlToClientMessage response = clientTaskMapper.doMessageTask(reader.readValue(msg.getText()), clientId);
			clientProducer.sendMessage(jsonMapper.writer().writeValueAsString(response), corrId, clientId);
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
