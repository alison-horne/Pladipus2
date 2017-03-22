package com.compomics.pladipus.worker;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;

import com.compomics.pladipus.model.queue.messages.worker.WorkerStatus;
import com.compomics.pladipus.model.queue.messages.worker.WorkerTaskMessage;
import com.compomics.pladipus.model.queue.messages.worker.WorkerToControlMessage;
import com.compomics.pladipus.tools.core.Tool;
import com.compomics.pladipus.tools.core.ToolFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WorkerProcessor {
	
	@Autowired
	private ObjectMapper jsonMapper;
	
	@Autowired
	private ToolFactory pladipusToolFactory;
	
	@Autowired
	private WorkerProducer workerProducer;
	
	public void processMessage(TextMessage message) {
		try {
			WorkerTaskMessage taskMsg = jsonMapper.readValue(((TextMessage) message).getText(), WorkerTaskMessage.class);
			processTask(taskMsg);
		} catch (IOException | JMSException e) {
			// TODO deal with bad message
		}
	}
	
	private void processTask(WorkerTaskMessage message) {
		
		Tool tool = null;
		try {
			tool = pladipusToolFactory.getBean(message.getToolname(), Tool.class, message.getParameters());
		} catch (BeansException e) {
			// TODO deal with bad message
		}
		
		if (tool != null) {
			int timeout = message.getTimeout();
			if (timeout < 0) {
				timeout = tool.getDefaultTimeout();
			}
			Long jobId = message.getJobId();
			sendMessage(jobId, WorkerStatus.ACK);
			ExecutorService es = Executors.newSingleThreadExecutor();
			Future<Boolean> future = es.submit(tool);
			try {
				if (future.get(timeout, TimeUnit.MILLISECONDS)) {
					sendMessage(jobId, WorkerStatus.COMPLETED);
				}
				else {
					//TODO error queue
					sendMessage(jobId, WorkerStatus.ERROR);
				}
			} catch (TimeoutException e) {
				future.cancel(true);
				sendMessage(jobId, WorkerStatus.TIMEOUT);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			es.shutdownNow();
		}
	}
	
	private void sendMessage(Long jobId, WorkerStatus status) {
		WorkerToControlMessage msg = new WorkerToControlMessage();
		msg.setJobId(jobId);
		msg.setStatus(status);
		try {
			workerProducer.sendMessage(jsonMapper.writeValueAsString(msg));
		} catch (JsonProcessingException e) {
			// TODO Retry? Ignore?
			e.printStackTrace();
		}
	}
}
