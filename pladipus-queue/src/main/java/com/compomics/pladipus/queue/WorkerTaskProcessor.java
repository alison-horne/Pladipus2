package com.compomics.pladipus.queue;

import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;

import com.compomics.pladipus.model.queue.messages.worker.WorkerToControlMessage;
import com.compomics.pladipus.repository.service.RunService;
import com.compomics.pladipus.shared.PladipusReportableException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WorkerTaskProcessor extends Thread {
	
	@Autowired
	private ObjectMapper jsonMapper;
	
	@Autowired
	private RunService runService;
	
	private TextMessage msg;
	
	public WorkerTaskProcessor(TextMessage msg) {
		this.msg = msg;
	}
	
	public void run() {
		try {
			WorkerToControlMessage task = jsonMapper.readValue(msg.getText(), WorkerToControlMessage.class);
			runService.processWorkerResponse(task);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PladipusReportableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
