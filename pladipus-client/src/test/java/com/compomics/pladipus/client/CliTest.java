package com.compomics.pladipus.client;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.junit.Before;
import org.junit.Test;
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

import com.compomics.pladipus.client.cmdline.CommandLineIO;
import com.compomics.pladipus.client.cmdline.MainCLI;
import com.compomics.pladipus.client.config.ClientConfiguration;
import com.compomics.pladipus.client.config.FullMockClientConfiguration;
import com.compomics.pladipus.shared.PladipusReportableException;

/**
 * Tests to check that the correct services are called when command line commands are issued.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={ClientConfiguration.class, FullMockClientConfiguration.class}, loader=AnnotationConfigContextLoader.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
						  TransactionalTestExecutionListener.class })
@DirtiesContext(classMode=ClassMode.AFTER_EACH_TEST_METHOD)
public class CliTest {
	
	@Autowired
	MainCLI cli;
	
	@Autowired
	CommandLineIO cmdLineIO;
	
	@Autowired
	ClientTaskProcessor cliTaskProcessor;

	private static final String USER = "username";
	private static final String PASSWORD = "password";
	
    @Before
    public void setup() {
        Mockito.reset(cmdLineIO, cliTaskProcessor);
    }
	
	@Test
	public void testHelp() {
		runCli("-h");
		Mockito.verify(cmdLineIO).printHelp(Matchers.any(Options.class), Matchers.isNull(String.class));
	}

	@Test
	public void testTemplateCreate() {
		try {
			runCliWithUser("-t filename");
			Mockito.verify(cliTaskProcessor).doTemplateTask("filename", false);
		} catch (PladipusReportableException e) {
			Assert.fail("Failed template create: " + e.getMessage());
		}
	}
	
	@Test
	public void testTemplateReplace() {
		try {
			runCliWithUser("-t filename -f");
			Mockito.verify(cliTaskProcessor).doTemplateTask("filename", true);
		} catch (PladipusReportableException e) {
			Assert.fail("Failed template create: " + e.getMessage());
		}
	}
	
	@Test
	public void testBatchAdd() {
		try {
			runCliWithUser("-b file -w workflow");
			Mockito.verify(cliTaskProcessor).doBatchTask("file", "workflow", null, false);
		} catch (PladipusReportableException e) {
			Assert.fail("Failed batch add: " + e.getMessage());
		}
	}
	
	@Test
	public void testBatchUpdate() {
		try {
			runCliWithUser("-b file -w workflow -B batchname -f");
			Mockito.verify(cliTaskProcessor).doBatchTask("file", "workflow", "batchname", true);
		} catch (PladipusReportableException e) {
			Assert.fail("Failed batch update: " + e.getMessage());
		}
	}
	
	@Test
	public void testGenerateHeaders() {
		try {
			runCliWithUser("-g file --workflow workflow");
			Mockito.verify(cliTaskProcessor).doGenerateTask("file", "workflow", false);
		} catch (PladipusReportableException e) {
			Assert.fail("Failed header generate: " + e.getMessage());
		}
	}
	
	@Test
	public void testProcessAll() {
		try {
			runCliWithUser("-p");
			Mockito.verify(cliTaskProcessor).doProcessTask(null, false);
		} catch (PladipusReportableException e) {
			Assert.fail("Failed process all: " + e.getMessage());
		}
	}
	
	@Test
	public void testProcessBatch() {
		try {
			runCliWithUser("-p batchname");
			Mockito.verify(cliTaskProcessor).doProcessTask("batchname", false);
		} catch (PladipusReportableException e) {
			Assert.fail("Failed process batch: " + e.getMessage());
		}
	}
	
	@Test
	public void testRestartBatch() {
		try {
			runCliWithUser("-r batchname");
			Mockito.verify(cliTaskProcessor).doProcessTask("batchname", true);
		} catch (PladipusReportableException e) {
			Assert.fail("Failed restart batch: " + e.getMessage());
		}
	}
	
	@Test
	public void testStatusBatch() {
		try {
			runCliWithUser("-s batchname");
			Mockito.verify(cliTaskProcessor).doStatusTask("batchname");
		} catch (PladipusReportableException e) {
			Assert.fail("Failed status: " + e.getMessage());
		}
	}
	
	@Test
	public void testAbortBatch() {
		try {
			runCliWithUser("-a batchname");
			Mockito.verify(cliTaskProcessor).doAbortTask("batchname");
		} catch (PladipusReportableException e) {
			Assert.fail("Failed batch abort: " + e.getMessage());
		}
	}
	
	@Test
	public void testAbortAll() {
		try {
			runCliWithUser("-a");
			Mockito.verify(cliTaskProcessor).doAbortTask(null);
		} catch (PladipusReportableException e) {
			Assert.fail("Failed all abort: " + e.getMessage());
		}
	}
	
	@Test
	public void testAddDefaultWithType() {
		try {
			runCliWithUser("--default name --value val -T type");
			Mockito.verify(cliTaskProcessor).doDefaultTask("name", "val", "type");
		} catch (PladipusReportableException e) {
			Assert.fail("Failed default add: " + e.getMessage());
		}
	}
	
	@Test
	public void testLoginNoPassword() {
		try {
			Mockito.doThrow(new ParseException("no password")).when(cliTaskProcessor).login(USER, null);
			runCli("-s -u " + USER);
			Mockito.verify(cliTaskProcessor).login(USER, null);
			Mockito.verify(cliTaskProcessor, Mockito.never()).doStatusTask(null);
			Mockito.verify(cmdLineIO).printHelp(Matchers.any(Options.class), Matchers.anyString());
		} catch (ParseException | PladipusReportableException e) {
			Assert.fail("Exception not handled: " + e.getMessage());
		}
	}
	
	@Test
	public void testExceptionHandled() {
		try {
			Mockito.doThrow(new PladipusReportableException("ex")).when(cliTaskProcessor).login(USER, PASSWORD);
			runCli("-s -u " + USER + " -P " + PASSWORD);
			Mockito.verify(cliTaskProcessor).login(USER, PASSWORD);
			Mockito.verify(cliTaskProcessor, Mockito.never()).doStatusTask(null);
			Mockito.verify(cmdLineIO).printError("ex");
		} catch (PladipusReportableException | ParseException e) {
			Assert.fail("Exception not handled: " + e.getMessage());
		}
	}
	
	private void runCli(String argString) {
		cli.cliMain(argString.split(" "));
	}
	
	private void runCliWithUser(String argString) throws PladipusReportableException {
		runCli(argString + " -u " + USER + " -P " + PASSWORD);
		try {
			Mockito.verify(cliTaskProcessor).login(USER, PASSWORD);
		} catch (ParseException e) {
			Assert.fail("Unexpected parse exception: " + e.getMessage());
		}
	}
}
