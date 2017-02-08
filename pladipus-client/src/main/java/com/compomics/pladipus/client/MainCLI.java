package com.compomics.pladipus.client;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Option.Builder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import com.compomics.pladipus.base.BatchControl;
import com.compomics.pladipus.base.DefaultsControl;
import com.compomics.pladipus.base.QueueControl;
import com.compomics.pladipus.base.UserControl;
import com.compomics.pladipus.base.WorkerControl;
import com.compomics.pladipus.base.WorkflowControl;
import com.compomics.pladipus.model.core.TaskStatus;
import com.compomics.pladipus.shared.PladipusReportableException;

public class MainCLI implements Alert {
	
	@Autowired
	protected ResourceBundle cmdLine;
	
	@Autowired
	protected CommandLineIO cmdLineIO;
	
	@Lazy
	@Autowired
	private UserControl userControl;
	
	@Lazy
	@Autowired
	private WorkerControl workerControl;
	
	@Lazy
	@Autowired
	private WorkflowControl workflowControl;
	
	@Lazy
	@Autowired
	private BatchControl batchControl;
	
	@Lazy
	@Autowired
	private QueueControl queueControl;
	
	@Lazy
	@Autowired
	private DefaultsControl defaultsControl;
	
	// Command line options
	private OptionGroup optGroup = new OptionGroup();
	private Options cmdLineOpts = new Options();
	private Options helpOpts = new Options();
	private CliOption helpOpt, workerOpt, templateOpt, batchOpt, processOpt, rerunOpt, statusOpt, generateOpt, defaultOpt, abortOpt, userOpt, passwordOpt, forceOpt, workflowOpt, batchnameOpt, valueOpt, typeOpt;

	private boolean startWorker = false;
	private String xmlFile;
	private String batchFile;
	private boolean process = false;
	private String batchName;
	private boolean status = false;
	private String generateFile;
	private String defaultName;
	private boolean abort = false;
	private String workflowName;
	private String defaultValue;
	private String defaultType;
	private String userName;
	private boolean force = false;
	private String password;
	
	public void cliMain(String[] args) {
	    try {
	    	initOptions();
			CommandLineParser parser = new DefaultParser();
			CommandLine line = parser.parse(cmdLineOpts, args, false);	
			if (line.hasOption(helpOpt.getShortOpt())) {
				cmdLineIO.printHelp(helpOpts, null);
			} else {
				readOptions(line);
				login();
				doTask(); 
			}
	    } catch (ParseException e) {
	    	cmdLineIO.printHelp(helpOpts, e.getMessage());
	    }
	}
	
	private void readOptions(CommandLine line) throws ParseException {
		
		if (!line.hasOption(userOpt.getShortOpt())) {
			throw new ParseException(cmdLine.getString("error.user"));
		}
		
		if (line.hasOption(forceOpt.getShortOpt())) {
			force = true;
		}
		
		if (line.hasOption(workerOpt.getShortOpt())) {
			startWorker = true;
		}
		
		if (line.hasOption(processOpt.getShortOpt())) {
			process = true;
			force = false;
		}
		
		if (line.hasOption(rerunOpt.getShortOpt())) {
			process = true;
			force = true;
		}
		
		if (line.hasOption(statusOpt.getShortOpt())) {
			status = true;
		}
		
		if (line.hasOption(abortOpt.getShortOpt())) {
			abort = true;
		}

		xmlFile		 = line.getOptionValue(templateOpt.getShortOpt());
		batchFile	 = line.getOptionValue(batchOpt.getShortOpt());
		generateFile = line.getOptionValue(generateOpt.getShortOpt());
		workflowName = line.getOptionValue(workflowOpt.getShortOpt());
		defaultName  = line.getOptionValue(defaultOpt.getShortOpt());
		defaultValue = line.getOptionValue(valueOpt.getShortOpt());
		defaultType	 = line.getOptionValue(typeOpt.getShortOpt());
		
		if (line.getOptionValue(processOpt.getShortOpt()) != null) {
			batchName = line.getOptionValue(processOpt.getShortOpt());
		} else if (line.getOptionValue(rerunOpt.getShortOpt()) != null) {
			batchName = line.getOptionValue(rerunOpt.getShortOpt());
		} else if (line.getOptionValue(abortOpt.getShortOpt()) != null) {
			batchName = line.getOptionValue(abortOpt.getShortOpt());
		} else if (line.getOptionValue(statusOpt.getShortOpt()) != null) {
			batchName = line.getOptionValue(statusOpt.getShortOpt());
		} else if (line.getOptionValue(batchnameOpt.getShortOpt()) != null) {
			batchName = line.getOptionValue(batchnameOpt.getShortOpt());
		}

		userName = line.getOptionValue(userOpt.getShortOpt());
		password = line.getOptionValue(passwordOpt.getShortOpt());
		
		if (((batchFile != null) || (generateFile != null)) && (workflowName == null)) {
			throw new ParseException(cmdLine.getString("error.noworkflow"));
		}
		
		if ((defaultName != null) && (defaultValue == null)) {
			throw new ParseException(cmdLine.getString("error.defaultvalue"));
		}
		
		if ((defaultName != null) && force) {
			throw new ParseException(cmdLine.getString("error.defaultforce"));
		}
	}
	
	private void initOptions() {
		helpOpt 	 = new CliOption("help", false, false, true);
		workerOpt 	 = new CliOption("worker", false, false, true);
		templateOpt  = new CliOption("template", true, false, true);
		batchOpt 	 = new CliOption("batch", true, false, true);
		processOpt 	 = new CliOption("process", true, true, true);
		rerunOpt	 = new CliOption("rerun", true, false, true);
		statusOpt 	 = new CliOption("status", true, true, true);
		generateOpt  = new CliOption("generate", true, false, true);
		defaultOpt 	 = new CliOption("default", true, false, true);
		abortOpt 	 = new CliOption("abort", true, true , true);
		userOpt 	 = new CliOption("user", true, false, false);
		passwordOpt  = new CliOption("password", true, false, false);
		forceOpt 	 = new CliOption("force", false, false, false);
		workflowOpt  = new CliOption("workflow", true, false, false);
		batchnameOpt = new CliOption("batchname", true, false, false);
		valueOpt 	 = new CliOption("value", true, false, false);
		typeOpt 	 = new CliOption("type", true, false, false);
		optGroup.setRequired(true);
		cmdLineOpts.addOptionGroup(optGroup);
	}
	
	private void login() throws ParseException {
		try {
			if (password == null) {
				password = cmdLineIO.getPassword();
			}
			if (password == null) {
				throw new ParseException(cmdLine.getString("error.password"));
			}
			userControl.login(userName, password);
		} catch (PladipusReportableException e) {
			alertAndDie(e.getMessage());
		}
	}
	
	private void doTask() {
		try {
			if (startWorker) doWorkerTask();
			if (xmlFile != null) doTemplateTask();
			if (batchFile != null) doBatchTask();
			if (process) doProcessTask();
			if (status) doStatusTask();
			if (generateFile != null) doGenerateTask();
			if (defaultName != null) doDefaultTask();
			if (abort) doAbortTask();
		} catch (PladipusReportableException e) {
			alertAndDie(e.getMessage());
		}
	}
	
	//TODO move tasks into separate class shared by GUI code
	private void doWorkerTask() throws PladipusReportableException {
		cmdLineIO.printOutput(cmdLine.getString("worker.start"));
		workerControl.startWorker(userControl.getLoggedInUser());
		//TODO output text - worker processing to update user on what it's doing.  Make sure it's kept alive, no ActiveMQ timeout hanging deaths
	}
	
	private void doTemplateTask() throws PladipusReportableException {
		if (force) {
			workflowControl.replaceWorkflow(xmlFile, userControl.getLoggedInUser());
			cmdLineIO.printOutput(cmdLine.getString("workflow.updated"));
		} else {
			workflowControl.createWorkflow(xmlFile, userControl.getLoggedInUser());
			cmdLineIO.printOutput(cmdLine.getString("workflow.created"));
		}
	}
	
	private void doBatchTask() throws PladipusReportableException {
		if (force) {
			batchControl.replaceBatch(batchFile, workflowName, batchName, userControl.getLoggedInUser());
			cmdLineIO.printOutput(cmdLine.getString("batch.updated"));
		} else {
			batchControl.createBatch(batchFile, workflowName, batchName, userControl.getLoggedInUser());
			cmdLineIO.printOutput(cmdLine.getString("batch.created"));
		}
	}
	
	private void doProcessTask() throws PladipusReportableException {
		if (force) {
			queueControl.restart(batchName, userControl.getLoggedInUser());
		} else {
			queueControl.process(batchName, userControl.getLoggedInUser());
		}
		cmdLineIO.printOutput(MessageFormat.format(cmdLine.getString("process.success"), (batchName != null && !batchName.isEmpty()) ? batchName : "all"));
	}
	
	private void doStatusTask() throws PladipusReportableException {
		TaskStatus taskStatus = queueControl.status(batchName, userControl.getLoggedInUser());
		cmdLineIO.printOutput(taskStatus.toString());
	}
	
	private void doGenerateTask() throws PladipusReportableException {
		batchControl.generateHeaders(generateFile, workflowName, userControl.getLoggedInUser(), force);
		cmdLineIO.printOutput(MessageFormat.format(cmdLine.getString("generate.success"), generateFile));
	}
	
	private void doDefaultTask() throws PladipusReportableException {
		defaultsControl.addDefault(defaultName, defaultValue, defaultType, userControl.getLoggedInUser());
		cmdLineIO.printOutput(MessageFormat.format(cmdLine.getString("default.success"), defaultName));
	}
	
	private void doAbortTask() throws PladipusReportableException {
		queueControl.abort(batchName, userControl.getLoggedInUser());
		cmdLineIO.printOutput(MessageFormat.format(cmdLine.getString("abort.success"), (batchName != null && !batchName.isEmpty()) ? batchName : "all"));
	}
	
	@Override
	public void alert(String msg) {
		cmdLineIO.printAlert(msg);
	}

	@Override
	public void alertAndDie(String msg) {
		cmdLineIO.printAlert(msg);
		System.exit(1);
	}
	
	private class CliOption {
		private String shortOpt;
		private String longOpt;
		private String desc;
		
		CliOption(String identifier, boolean hasArg, boolean optArg, boolean groupOpt) {
			try {
				shortOpt = cmdLine.getString("options.short." + identifier);
			} catch (MissingResourceException e) {
				alertAndDie(e.getMessage());
			}
			
			try {
				longOpt = cmdLine.getString("options.long." + identifier);
			} catch (MissingResourceException e) {
				// Do nothing - fine to not have a long option
			}
			
			try {
				desc = cmdLine.getString("options.desc." + identifier);
			} catch (MissingResourceException e) {
				// Do nothing - again, fine to not have a description
			}
			
			Option option = initOption(hasArg, optArg);
			
			if (groupOpt) {
				optGroup.addOption(option);
			}
			else {
				cmdLineOpts.addOption(option);
			}
			if (desc != null) helpOpts.addOption(option);
		}
		
		String getShortOpt() {
			return shortOpt;
		}
		
		private Option initOption(boolean hasArg, boolean optArg) {
			Builder builder = Option.builder(shortOpt);
			if (longOpt != null) builder.longOpt(longOpt);
			if (hasArg) {
				builder.numberOfArgs(1);
				if (optArg) {
					builder.optionalArg(true);
				} else {
					builder.hasArg(true);
				}
			} else {
				builder.hasArg(false);
			}
			if (desc != null) builder.desc(desc);
			return builder.build();
		}
	}
}
