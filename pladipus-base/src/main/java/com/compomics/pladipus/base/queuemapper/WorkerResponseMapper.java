package com.compomics.pladipus.base.queuemapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import com.compomics.pladipus.model.persist.RunStatus;
import com.compomics.pladipus.model.queue.messages.worker.WorkerToControlMessage;
import com.compomics.pladipus.repository.service.RunService;
import com.compomics.pladipus.shared.PladipusMessages;
import com.compomics.pladipus.shared.PladipusReportableException;

public class WorkerResponseMapper {
	
	@Autowired
	@Lazy
	private RunService runService;
	
	@Autowired
	private PladipusMessages exceptionMessages;
	
	public void doResponseProcess(WorkerToControlMessage msg, String workerId) throws PladipusReportableException {
		switch(msg.getStatus()) {
			case TIMEOUT:
				runService.workerErrorStatus(msg.getJobId(), workerId, exceptionMessages.getMessage("worker.timeout"));
				break;
			case ACK:
				if (msg.getJobId() != null) {
					runService.workerStepStatus(msg.getJobId(), RunStatus.ON_WORKER, workerId);
				} else {
					// TODO keep-alive check...mechanism to remotely timeout tasks in case failure at worker end,
					// or to check if worker has died so we can update database and re-send task messages.
				}
				break;
			case PROCESSING:
				runService.workerStepStatus(msg.getJobId(), RunStatus.IN_PROGRESS, workerId);
				break;
			case COMPLETED:
				runService.completeRunStep(msg.getJobId(), msg.getOutputs(), workerId);
				break;
			case ERROR:
				runService.workerErrorStatus(msg.getJobId(), workerId, msg.getErrorMessage());
				break;
			case CANCELLED:
				runService.workerStepStatus(msg.getJobId(), RunStatus.CANCELLED, workerId);
				break;
			default:
				break;
		}
	}
}
