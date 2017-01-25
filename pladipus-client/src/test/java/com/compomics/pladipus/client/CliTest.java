package com.compomics.pladipus.client;

import org.apache.commons.cli.Options;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.compomics.pladipus.base.BatchControl;
import com.compomics.pladipus.base.DefaultsControl;
import com.compomics.pladipus.base.QueueControl;
import com.compomics.pladipus.base.UserControl;
import com.compomics.pladipus.base.WorkerControl;
import com.compomics.pladipus.base.WorkflowControl;
import com.compomics.pladipus.base.test.config.MockBaseConfiguration;
import com.compomics.pladipus.client.config.ClientConfiguration;
import com.compomics.pladipus.client.config.MockClientConfiguration;
import com.compomics.pladipus.model.core.TaskStatus;
import com.compomics.pladipus.shared.PladipusReportableException;

/**
 * Tests to check that the correct services are called when command line commands are issued.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={ClientConfiguration.class, MockBaseConfiguration.class, MockClientConfiguration.class}, loader=AnnotationConfigContextLoader.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
						  TransactionalTestExecutionListener.class })
@DirtiesContext(classMode=ClassMode.AFTER_EACH_TEST_METHOD)
public class CliTest {
	
	private static final String USER = "username";
	private static final String PASSWORD = "password";
	private static final int USERID = 1;
	
	@Before
	public void setUp() {
		Mockito.reset(cmdLineIO, userControl, workerControl, workflowControl, batchControl, queueControl, defaultsControl);
	}
	
    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();
	
	@Test
	public void testHelp() {
		runCli("-h");
		Mockito.verify(cmdLineIO).printHelp(Matchers.any(Options.class), Matchers.isNull(String.class));
	}
	
	@Test
	public void testLoginPasswordInput() {
		Mockito.when(cmdLineIO.getPassword()).thenReturn(PASSWORD);
		runCli("-w -u " + USER);
		Mockito.verify(cmdLineIO).getPassword();
		try {
			checkLogin();
		} catch (PladipusReportableException e) {
			Assert.fail("Failed login: " + e.getMessage());
		}
	}
	
	@Test
	public void testWorker() {		
		try {
			runCliWithUser("-w");
			Mockito.verify(workerControl).startWorker(USERID);
		} catch (PladipusReportableException e) {
			Assert.fail("Failed to start worker: " + e.getMessage());
		}
	}
	
	@Test
	public void testTemplateCreate() {
		try {
			runCliWithUser("-t filename");
			Mockito.verify(workflowControl).createWorkflow("filename", USERID);
		} catch (PladipusReportableException e) {
			Assert.fail("Failed template create: " + e.getMessage());
		}
	}
	
	@Test
	public void testTemplateReplace() {
		try {
			runCliWithUser("-t filename -f");
			Mockito.verify(workflowControl).replaceWorkflow("filename", USERID);
		} catch (PladipusReportableException e) {
			Assert.fail("Failed template create: " + e.getMessage());
		}
	}
	
	@Test
	public void testBatchAdd() {
		try {
			runCliWithUser("-b file -W workflow");
			Mockito.verify(batchControl).createBatch("file", "workflow", null, USERID);
		} catch (PladipusReportableException e) {
			Assert.fail("Failed batch add: " + e.getMessage());
		}
	}
	
	@Test
	public void testBatchUpdate() {
		try {
			runCliWithUser("-b file -W workflow -B batchname -f");
			Mockito.verify(batchControl).replaceBatch("file", "workflow", "batchname", USERID);
		} catch (PladipusReportableException e) {
			Assert.fail("Failed batch update: " + e.getMessage());
		}
	}
	
	@Test
	public void testGenerateHeaders() {
		try {
			runCliWithUser("-g file --workflow workflow");
			Mockito.verify(batchControl).generateHeaders("file", "workflow", USERID, false);
		} catch (PladipusReportableException e) {
			Assert.fail("Failed header generate: " + e.getMessage());
		}
	}
	
	@Test
	public void testProcessAll() {
		try {
			runCliWithUser("-p");
			Mockito.verify(queueControl).process(null, USERID);
		} catch (PladipusReportableException e) {
			Assert.fail("Failed process all: " + e.getMessage());
		}
	}
	
	@Test
	public void testRestartBatch() {
		try {
			runCliWithUser("-r batchname");
			Mockito.verify(queueControl).restart("batchname", USERID);
		} catch (PladipusReportableException e) {
			Assert.fail("Failed restart batch: " + e.getMessage());
		}
	}
	
	@Test
	public void testStatusBatch() {
		try {
			Mockito.when(queueControl.status("batchname", USERID)).thenReturn(new TaskStatus());
			runCliWithUser("-s batchname");
			Mockito.verify(queueControl).status("batchname", USERID);
		} catch (PladipusReportableException e) {
			Assert.fail("Failed status: " + e.getMessage());
		}
	}
	
	@Test
	public void testAbortBatch() {
		try {
			runCliWithUser("-a batchname");
			Mockito.verify(queueControl).abort("batchname", USERID);
		} catch (PladipusReportableException e) {
			Assert.fail("Failed batch abort: " + e.getMessage());
		}
	}
	
	@Test
	public void testAbortAll() {
		try {
			runCliWithUser("-a");
			Mockito.verify(queueControl).abort(null, USERID);
		} catch (PladipusReportableException e) {
			Assert.fail("Failed all abort: " + e.getMessage());
		}
	}
	
	@Test
	public void testAddDefaultWithType() {
		try {
			runCliWithUser("--default name --value val -T type");
			Mockito.verify(defaultsControl).addDefault("name", "val", "type", USERID);
		} catch (PladipusReportableException e) {
			Assert.fail("Failed default add: " + e.getMessage());
		}
	}
	
	@Test
	public void testExitOnException() {
		try {
			exit.expectSystemExitWithStatus(1);
			String errorMsg = "Test message";
			Mockito.doThrow(new PladipusReportableException(errorMsg)).when(userControl).login(USER, PASSWORD);
			runCli("-w -u " + USER + " -P " + PASSWORD);
			Mockito.verify(cmdLineIO).printAlert(errorMsg);
		} catch (PladipusReportableException e) {
			Assert.fail("Exception not dealt with: " + e);
		}
	}
	
	private void runCli(String argString) {
		cli.cliMain(argString.split(" "));
	}
	
	private void runCliWithUser(String argString) throws PladipusReportableException {
		Mockito.when(userControl.getUserId()).thenReturn(USERID);
		runCli(argString + " -u " + USER + " -P " + PASSWORD);
		checkLogin();
		Mockito.verify(userControl).getUserId();
		Mockito.verify(cmdLineIO).printOutput(Mockito.anyString());
	}
	
	private void checkLogin() throws PladipusReportableException {
		Mockito.verify(userControl).login(USER, PASSWORD);
	}
	
	@Autowired
	MainCLI cli;
	
	@Autowired
	CommandLineIO cmdLineIO;
	
	@Autowired
	UserControl userControl;
	
	@Autowired
	WorkerControl workerControl;
	
	@Autowired
	WorkflowControl workflowControl;
	
	@Autowired
	BatchControl batchControl;
	
	@Autowired
	QueueControl queueControl;
	
	@Autowired
	DefaultsControl defaultsControl;
}
