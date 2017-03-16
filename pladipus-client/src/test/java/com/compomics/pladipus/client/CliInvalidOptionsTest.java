package com.compomics.pladipus.client;

import org.apache.commons.cli.Options;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.compomics.pladipus.client.config.ClientConfiguration;
import com.compomics.pladipus.client.config.MockClientConfiguration;

/**
 * Test invalid command line inputs give appropriate error messages.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={ClientConfiguration.class, MockClientConfiguration.class}, loader=AnnotationConfigContextLoader.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
						  TransactionalTestExecutionListener.class })
public class CliInvalidOptionsTest {

	@Autowired
	MainCLI cli;
	
	@Autowired
	CommandLineIO cmdLineIO;
	
	@Before
	public void setUp() {
		Mockito.reset(cmdLineIO);
	}
	
	@Test
	public void testErrorWhenMoreThanOneOption() {
		runAndCheckErrorHelp("-p -s -u username");
	}
	
	@Test
	@DirtiesContext
	public void testErrorWhenInvalidOption() {
		runAndCheckErrorHelp("--invalid -w -u username");
	}
	
	@Test
	public void testErrorWhenNoMainOption() {
		runAndCheckErrorHelp("-u username");
	}
	
	@Test
	public void testErrorWhenMissingMandatoryArgument() {
		runAndCheckErrorHelp("-r -u username");
	}
	
	@Test
	public void testErrorWhenMissingUsername() {
		runAndCheckErrorHelp("-w");
	}
	
	@Test
	@DirtiesContext
	public void testErrorBatchNoWorkflow() {
		runAndCheckErrorHelp("--batch filename -u username");
	}
	
	@Test
	@DirtiesContext
	public void testErrorGenerateNoWorkflow() {
		runAndCheckErrorHelp("--generate filename -u username");
	}
	
	@Test
	@DirtiesContext
	public void testErrorDefaultNoValue() {
		runAndCheckErrorHelp("-d name -u username");
	}
	
	@Test
	@DirtiesContext
	public void testErrorDefaultForce() {
		runAndCheckErrorHelp("-d name -v value --force -u username");
	}
	
	@Test
	@DirtiesContext
	public void testErrorNoPasswordWithoutConsole() {
		cli.cliMain("-s -u username".split(" "));
		Mockito.verify(cmdLineIO).printHelp(Matchers.any(Options.class), Matchers.anyString());
		Mockito.verify(cmdLineIO).getPassword();
	}
	
	private void runAndCheckErrorHelp(String argString) {
		cli.cliMain(argString.split(" "));
		// Check that it never made it as far as login by checking password was not requested
		Mockito.verify(cmdLineIO, Mockito.never()).getPassword();
		Mockito.verify(cmdLineIO).printHelp(Matchers.any(Options.class), Matchers.anyString());
	}
}
