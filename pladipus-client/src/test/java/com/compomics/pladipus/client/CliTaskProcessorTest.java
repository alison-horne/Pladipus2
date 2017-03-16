package com.compomics.pladipus.client;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.BeanFactory;
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

import com.compomics.pladipus.client.config.ClientConfiguration;
import com.compomics.pladipus.client.config.FullMockClientConfiguration;
import com.compomics.pladipus.client.config.MockClientConfiguration;
import com.compomics.pladipus.client.queue.MessageTask;
import com.compomics.pladipus.model.core.TaskStatus;
import com.compomics.pladipus.model.persist.User;
import com.compomics.pladipus.model.queue.messages.client.ClientTaskStatus;
import com.compomics.pladipus.model.queue.messages.client.ControlToClientMessage;
import com.compomics.pladipus.shared.PladipusReportableException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Tests to mock message response to CLI commands
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={ClientConfiguration.class, MockClientConfiguration.class}, loader=AnnotationConfigContextLoader.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
						  TransactionalTestExecutionListener.class })
@DirtiesContext(classMode=ClassMode.AFTER_EACH_TEST_METHOD)
public class CliTaskProcessorTest {
	
	@Autowired
	MainCLI cli;
	
	@Autowired
	CommandLineIO cmdLineIO;
	
	@Autowired
	CliTaskProcessor cliTaskProcessor;
	
	@Autowired
	ObjectMapper jsonMapper;
	
	private static final String USER = "username";
	private static final String PASSWORD = "password";
	
    @Before
    public void setup() {
        Mockito.reset(cmdLineIO);
    }


}
