package com.compomics.pladipus.queue;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;

import com.compomics.pladipus.model.persist.RunStatus;
import com.compomics.pladipus.model.persist.RunStep;
import com.compomics.pladipus.model.persist.RunStepParameter;
import com.compomics.pladipus.model.queue.messages.worker.WorkerTaskMessage;
import com.compomics.pladipus.repository.service.RunService;
import com.compomics.pladipus.shared.PladipusReportableException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ReadyTaskScheduler {

	@Autowired
	private RunService runService;
	
	@Autowired
	private ControlWorkerProducer workerProducer;
	
	@Autowired
	private ObjectMapper jsonMapper;
	
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final long intervalSeconds = 30; // TODO decide on this polling duration - config setting?
    private ScheduledFuture<?> taskFuture;
    
    public ReadyTaskScheduler() {
    	taskFuture = scheduler.scheduleAtFixedRate(new PushToWorkers(), intervalSeconds, intervalSeconds, TimeUnit.SECONDS);
    }
    
    public void killScheduler() {
    	taskFuture.cancel(true);
    }
    
    private class PushToWorkers implements Runnable {

		@Override
		public void run() {
			try {
				List<RunStep> readySteps = runService.getReadyRunSteps();
				if (!readySteps.isEmpty()) {
					// TODO max queue size?
					// TODO option to add overriding timeout in workflow?  Or manually in GUI?
					for (RunStep step: readySteps) {
						WorkerTaskMessage message = new WorkerTaskMessage();
						message.setToolname(step.getToolName());
						message.setJobId(step.getRunStepId());
						for (RunStepParameter parameter : step.getParameters()) {
							message.addParameter(parameter.getParamName(), parameter.getParamValue());
						}
						workerProducer.sendMessage(jsonMapper.writeValueAsString(message));
						runService.updateStepStatus(step, RunStatus.QUEUED);
					}
				}
			} catch (PladipusReportableException | JsonProcessingException e) {
				// TODO Log error...or decide how otherwise to deal with it
			}
		}
    }
 }