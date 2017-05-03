package com.compomics.pladipus.worker.impl;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import com.compomics.pladipus.model.queue.messages.worker.WorkerStatus;
import com.compomics.pladipus.model.queue.messages.worker.WorkerTaskMessage;
import com.compomics.pladipus.shared.PladipusMessages;
import com.compomics.pladipus.tools.core.Tool;
import com.compomics.pladipus.tools.core.ToolFactory;
import com.compomics.pladipus.worker.MessageProducer;
import com.compomics.pladipus.worker.QueueProcessor;

public class WorkerTaskQueueProcessor implements QueueProcessor {
	
	@Autowired
	DefaultMessageListenerContainer taskListenerContainer;
	
	@Autowired
	private ToolFactory pladipusToolFactory;
	
	@Autowired
	private MessageProducer workerProducer;
	
	@Autowired
	private PladipusMessages exceptionMessages;
	
	@Autowired
	private int defaultToolTimeout;
	
	private AtomicReference<WorkerTaskMessage> currentTask = new AtomicReference<WorkerTaskMessage>();
	private ConcurrentMap<Long, Future<Map<String, String>>> runningTask = new ConcurrentHashMap<Long, Future<Map<String, String>>>();
	private Queue<WorkerTaskMessage> taskQueue = new LinkedList<WorkerTaskMessage>();
	private Semaphore queueLock = new Semaphore(1);
	private int lockTimeoutMs = 30000;
	
	private boolean getLock() {
		try {			
			return queueLock.tryAcquire(lockTimeoutMs, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			return false;
		}
	}
	
	private void releaseLock() {
		queueLock.release();
	}
	
	class ProcessTask extends Thread {
		public void run() {
			processTasks();
		}
	}
	
	public void addTask(WorkerTaskMessage msg) {
		if (isValidMessage(msg)) {
			if (getLock()) {
				ProcessTask task = null;
				if (currentTask.compareAndSet(null, msg)) {
					task = new ProcessTask();
				} else {
					taskQueue.offer(msg);
				}
				workerProducer.sendMessage(msg.getJobId(), WorkerStatus.ACK, null);
				releaseLock();
				if (task != null) {
					task.start();
				} else {
					if (taskListenerContainer.isRunning()) taskListenerContainer.stop();
				}
			} else {
				endError(msg, exceptionMessages.getMessage("worker.internalError"));
			}
		} else {
			endError(msg, exceptionMessages.getMessage("worker.invalidMessage", msg.getJobId()));
		}
	}
	
	private void processTasks() {
		WorkerTaskMessage taskMsg;
		while ((taskMsg = currentTask.get()) != null) {
			startTask(taskMsg);
			nextTask();
		} 
	}
	
	private void startTask(WorkerTaskMessage msg) {
		Tool tool = null;
		Future<Map<String, String>> future = null;
		ExecutorService es = null;
		Long jobId = msg.getJobId();
		try {
			tool = pladipusToolFactory.getBean(msg.getToolname(), Tool.class, msg.getParameters());
			if (tool != null) {
				int timeout = msg.getTimeout();
				if (timeout < 0) {
					timeout = tool.getDefaultTimeoutSeconds();
					if (timeout < 0) {
						timeout = defaultToolTimeout;
					}
				}
				workerProducer.sendMessage(jobId, WorkerStatus.PROCESSING, null);
				es = Executors.newSingleThreadExecutor();
				future = es.submit(tool);
				runningTask.put(jobId, future);
				Map<String, String> outputs = future.get(timeout, TimeUnit.SECONDS);
				if (outputs != null) {
					if (outputs.get(Tool.ERROR) != null) {
						workerProducer.sendErrorMessage(jobId, outputs.get(Tool.ERROR));
					} else {
						workerProducer.sendMessage(jobId, WorkerStatus.COMPLETED, outputs);
					}
				}
				else {
					workerProducer.sendErrorMessage(jobId, exceptionMessages.getMessage("worker.noOutput"));
				}
			} else {
				workerProducer.sendErrorMessage(jobId, exceptionMessages.getMessage("worker.invalidTool"));
			}
		} catch (BeansException e) {
			workerProducer.sendErrorMessage(jobId, exceptionMessages.getMessage("worker.invalidTool"));
		} catch (TimeoutException e) {
			future.cancel(true);
			workerProducer.sendMessage(jobId, WorkerStatus.TIMEOUT, null);
		} catch (CancellationException e) {
			workerProducer.sendMessage(jobId, WorkerStatus.CANCELLED, null);
		} catch (Exception e) {
			if (future != null) future.cancel(true);
			workerProducer.sendErrorMessage(jobId, exceptionMessages.getMessage("worker.generalError", e.getMessage()));
		} finally {
			synchronized(msg) {
				msg.notify();
			}
			if (es != null) es.shutdownNow();
			runningTask.remove(jobId);
		}		
	}
	
	private void nextTask() {
		if (getLock()) {
			boolean noMore = false;
			if (taskQueue.isEmpty()) {
				currentTask.set(null);
				noMore = true;
			} else {
				currentTask.set(taskQueue.poll());
			}
			releaseLock();
			if (noMore) {
				if (!taskListenerContainer.isRunning()) taskListenerContainer.start();
			}
		} else {
			// TODO what to do if worker is stuck?  Will sort out with keep alive check?
		}
	}
	
	private boolean isValidMessage(WorkerTaskMessage msg) {
		boolean valid = false;
		if (msg.getJobId() != null) {
			String[] validTools = pladipusToolFactory.getBeanDefinitionNames();
			for (String validTool: validTools) {
				if (validTool.equals(msg.getToolname())) {
					valid = true;
					break;
				}
			}
		}
		return valid;
	}
	
	public void cancelTask(Long cancelJobId) {
		if (removeQueueTask(cancelJobId)) {
			Future<Map<String, String>> running = runningTask.get(cancelJobId);
			if (running != null) {
				running.cancel(true);
			}
		} else {
			workerProducer.sendErrorMessage(cancelJobId, exceptionMessages.getMessage("worker.cancelFail"));
		}
	}
	
	public void cancelAll() {
		if (removeQueueTask(null)) {
			for (Future<Map<String, String>> future: runningTask.values()) {
				future.cancel(true);
			}
		} else {
			workerProducer.sendErrorMessage(null, exceptionMessages.getMessage("worker.cancelFail"));
		}
	}
	
	private boolean removeQueueTask(Long jobId) {
		boolean removed = false;
		if (getLock()) {
			if (jobId != null) {
				for (WorkerTaskMessage msg: taskQueue) {
					if (msg.getJobId() == jobId) {
						taskQueue.remove(msg);
						workerProducer.sendMessage(jobId, WorkerStatus.CANCELLED, null);
						break;
					}
				}
			} else {
				for (WorkerTaskMessage msg: taskQueue) {
					workerProducer.sendMessage(msg.getJobId(), WorkerStatus.CANCELLED, null);
				}
				taskQueue.clear();
			}
			releaseLock();
			removed = true;
		}
		return removed;
	}
	
	private void endError(WorkerTaskMessage msg, String errorMessage) {
		synchronized(msg) {
			msg.notify();
		}
		workerProducer.sendErrorMessage(msg.getJobId(), errorMessage);
	}
}
