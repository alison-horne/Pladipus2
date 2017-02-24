package com.compomics.pladipus.client;

import java.text.MessageFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.cli.ParseException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.compomics.pladipus.client.queue.MessageTask;
import com.compomics.pladipus.model.core.TaskStatus;
import com.compomics.pladipus.shared.PladipusReportableException;

public class CliTaskProcessorImpl implements CliTaskProcessor {

	@Autowired
	private CommandLineIO cmdLineIO;
	
	@Autowired
	private BeanFactory beanFactory;
	
	@Override
	public void login(String username, String password) {
		makeRequest("Login " + username);
//		try {
//			if (password == null) {
//				password = cmdLineIO.getPassword();
//			}
//			if (password == null) {
//				throw new ParseException(cmdLine.getString("error.password"));
//			}
//			cliTaskProcessor.login(userName, password);
//		} catch (PladipusReportableException e) {
//			alertAndDie(e.getMessage());
//		}
	}
	
	@Override
	public void doTemplateTask(String filepath, boolean force) {
		makeRequest("Template");
//		if (force) {
//			workflowControl.replaceWorkflow(xmlFile, userControl.getLoggedInUser());
//			cmdLineIO.printOutput(cmdLine.getString("workflow.updated"));
//		} else {
//			workflowControl.createWorkflow(xmlFile, userControl.getLoggedInUser());
//			cmdLineIO.printOutput(cmdLine.getString("workflow.created"));
//		}
	}
	
	@Override
	public void doBatchTask(String filepath, String workflowName, String batchName, boolean force) {
//		if (force) {
//			batchControl.replaceBatch(batchFile, workflowName, batchName, userControl.getLoggedInUser());
//			cmdLineIO.printOutput(cmdLine.getString("batch.updated"));
//		} else {
//			batchControl.createBatch(batchFile, workflowName, batchName, userControl.getLoggedInUser());
//			cmdLineIO.printOutput(cmdLine.getString("batch.created"));
//		}
	}
	
	@Override
	public void doProcessTask(String batchName, boolean force) {
//		if (force) {
//			queueControl.restart(batchName, userControl.getLoggedInUser());
//		} else {
//			queueControl.process(batchName, userControl.getLoggedInUser());
//		}
//		cmdLineIO.printOutput(MessageFormat.format(cmdLine.getString("process.success"), (batchName != null && !batchName.isEmpty()) ? batchName : "all"));
	}
	
	@Override
	public void doStatusTask(String batchName) {
//		TaskStatus taskStatus = queueControl.status(batchName, userControl.getLoggedInUser());
//		cmdLineIO.printOutput(taskStatus.toString());
	}
	
	@Override
	public void doGenerateTask(String filepath, String workflowName, boolean force) {
//		batchControl.generateHeaders(generateFile, workflowName, userControl.getLoggedInUser(), force);
//		cmdLineIO.printOutput(MessageFormat.format(cmdLine.getString("generate.success"), generateFile));
	}
	
	@Override
	public void doDefaultTask(String defaultName, String defaultValue, String defaultType) {
//		defaultsControl.addDefault(defaultName, defaultValue, defaultType, userControl.getLoggedInUser());
//		cmdLineIO.printOutput(MessageFormat.format(cmdLine.getString("default.success"), defaultName));
	}
	
	@Override
	public void doAbortTask(String batchName) {
//		queueControl.abort(batchName, userControl.getLoggedInUser());
//		cmdLineIO.printOutput(MessageFormat.format(cmdLine.getString("abort.success"), (batchName != null && !batchName.isEmpty()) ? batchName : "all"));
	}
	
	private void makeRequest(String text) {
		ExecutorService es = Executors.newSingleThreadExecutor();
		Future<String> response = es.submit(beanFactory.getBean(MessageTask.class, text));
		try {
			String responseText = response.get();
		    if ((responseText != null) && !responseText.isEmpty()) {
		    	System.out.println("Got response: " + responseText);
		       	// TODO deal with valid response
		    } else {
		    	System.out.println("Empty response");
		       	// TODO deal with timeout/empty response
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}
		es.shutdown();
	}

}
