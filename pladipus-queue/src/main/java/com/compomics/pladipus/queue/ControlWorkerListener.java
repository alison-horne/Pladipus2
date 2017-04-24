package com.compomics.pladipus.queue;

import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;

import com.compomics.pladipus.base.queuemapper.WorkerResponseMapper;
import com.compomics.pladipus.model.queue.MessageSelector;
import com.compomics.pladipus.model.queue.messages.worker.WorkerToControlMessage;
import com.compomics.pladipus.shared.PladipusReportableException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ControlWorkerListener implements MessageListener {

	@Autowired
	private ObjectMapper jsonMapper;
	
	@Autowired
	private WorkerResponseMapper workerResponseMapper;
	
	@Override
	public void onMessage(Message msg) {
		if (msg instanceof TextMessage) {
			TextMessage message = (TextMessage)msg;
			WorkerToControlMessage task;
			try {
				task = jsonMapper.readValue(message.getText(), WorkerToControlMessage.class);
				String workerId = msg.getStringProperty(MessageSelector.WORKER_ID.toString());
				workerResponseMapper.doResponseProcess(task, workerId);
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
			}
			catch (PladipusReportableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			// TODO deal with bad message
		}
	}
}
