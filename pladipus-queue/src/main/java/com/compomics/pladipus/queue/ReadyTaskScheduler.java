package com.compomics.pladipus.queue;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import com.compomics.pladipus.model.persist.Run;
import com.compomics.pladipus.model.persist.RunStatus;
import com.compomics.pladipus.model.persist.RunStep;
import com.compomics.pladipus.model.persist.RunStepParameter;
import com.compomics.pladipus.model.queue.MessageSelector;
import com.compomics.pladipus.model.queue.messages.worker.WorkerDirectTask;
import com.compomics.pladipus.model.queue.messages.worker.WorkerTaskMessage;
import com.compomics.pladipus.repository.service.RunService;
import com.compomics.pladipus.shared.PladipusReportableException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ReadyTaskScheduler {

	@Lazy
	@Autowired
	private RunService runService;
	
	@Lazy
	@Autowired
	private QueueMessageController workerQueueController;
	
	@Autowired
	private ControlWorkerProducer workerProducer;
	
	@Autowired
	private ControlWorkerDirectProducer workerDirectProducer;
	
	@Autowired
	private ObjectMapper jsonMapper;
	
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final long intervalSeconds = 30; // TODO decide on this polling duration - config setting?
    private ScheduledFuture<?> taskFuture;
    
    public ReadyTaskScheduler() {
    	taskFuture = scheduler.scheduleAtFixedRate(new CheckRunStatus(), intervalSeconds, intervalSeconds, TimeUnit.SECONDS);
    }
    
    public void killScheduler() {
    	taskFuture.cancel(true);
    }
    
    private class CheckRunStatus implements Runnable {

		@Override
		public void run() {
			try {
				abortRuns();
			} catch (PladipusReportableException | JsonProcessingException e) {
				e.printStackTrace();
				// TODO Log error...or decide how otherwise to deal with it
			}
			try {
				queueReadySteps();
			} catch (PladipusReportableException | JsonProcessingException e) {
				// TODO
			}
		}
    }
    
    private void queueReadySteps() throws PladipusReportableException, JsonProcessingException {
    	List<RunStep> readySteps = runService.getReadyRunSteps();
		if (!readySteps.isEmpty()) {
			// TODO max queue size?
			// TODO option to add overriding timeout in workflow?  Or manually in GUI?
			// TODO if sending task to worker direct, make sure JMX ID is added as a parameter.  Used to make sure output location is unique/not overwritten
			for (RunStep step: readySteps) {
				WorkerTaskMessage message = new WorkerTaskMessage();
				message.setToolname(step.getToolName());
				message.setJobId(step.getRunStepId());
				String runIdentifier = runService.getRunIdentifier(step);
				for (RunStepParameter parameter : step.getParameters()) {
					message.addParameter(parameter.getParamName(), runService.doStepSubstitutions(parameter.getParamValue(), step.getRun().getRunId()));
				}
				message.addParameter(MessageSelector.JMX_IDENTIFIER.name(), runIdentifier);
				workerProducer.sendMessage(jsonMapper.writeValueAsString(message), step.getFailedWorkers(), runIdentifier);
				runService.updateStepStatus(step, RunStatus.QUEUED);
			}
		}
    }
    
    private void abortRuns() throws PladipusReportableException, JsonProcessingException {
    	for (Run run: runService.getAbortedRuns()) {
    		workerQueueController.removeMessagesByIdentifier(run.getRunIdentifier());
    		runService.updateRunStatus(run, RunStatus.CANCELLED);
    		String workerId = null;
    		for (RunStep step: run.getRunSteps()) {
    			if ((workerId=step.getCurrentWorker()) != null) {
    				WorkerTaskMessage message = new WorkerTaskMessage();
    				message.setTask(WorkerDirectTask.ABORT_JOB);
    				message.setJobId(step.getRunStepId());
    				workerDirectProducer.sendMessage(jsonMapper.writer().writeValueAsString(message), workerId);
    			}
    		}
    	}	
    }
 }