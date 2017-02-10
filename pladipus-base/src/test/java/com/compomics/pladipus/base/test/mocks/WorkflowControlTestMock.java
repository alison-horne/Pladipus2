package com.compomics.pladipus.base.test.mocks;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.compomics.pladipus.base.WorkflowControl;
import com.compomics.pladipus.base.config.BaseConfiguration;
import com.compomics.pladipus.model.persist.User;
import com.compomics.pladipus.model.persist.Workflow;
import com.compomics.pladipus.shared.PladipusMessages;
import com.compomics.pladipus.shared.PladipusReportableException;
import com.compomics.pladipus.repository.config.MockRepositoryConfiguration;
import com.compomics.pladipus.repository.service.WorkflowService;
import com.compomics.pladipus.test.tools.config.TestToolsConfiguration;

/**
 * Tests to check workflowControl function without calling database.
 * These tests will check validation and parsing of XML template to Workflow object.  Extra validation in 
 * WorkflowValidator checked in separate test class.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={BaseConfiguration.class, TestToolsConfiguration.class, MockRepositoryConfiguration.class}, loader=AnnotationConfigContextLoader.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
						  TransactionalTestExecutionListener.class })
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
public class WorkflowControlTestMock {
	
	@Autowired
	WorkflowService workflowService;
	
	@Autowired
	WorkflowControl workflowControl;
	
	@Autowired
	private PladipusMessages exceptionMessages;
	
	@Autowired
	private ResourceLoader resourceLoader;
	
	private static final User USER = new User();
	private static final String VALID1_FILE = "classpath:valid1.xml";
	private static final String INVALID_NO_NAME_FILE = "classpath:invalid_noname.xml";

	@Before
	public void setUp() throws PladipusReportableException {
		Mockito.reset(workflowService);
	}
	
	@Test
	public void testValidInsertNoParams() {
		try {
			workflowControl.createWorkflow(getXMLFilePath(VALID1_FILE), USER);
			ArgumentCaptor<Workflow> argument = ArgumentCaptor.forClass(Workflow.class);
			Mockito.verify(workflowService).insertWorkflow(argument.capture());
			assertEquals(USER, argument.getValue().getUser());
			assertEquals(0, argument.getValue().getGlobal().getParameters().getParameter().size());
			assertEquals(1, argument.getValue().getSteps().getStep().size());
		} catch (PladipusReportableException e) {
			fail("Failed insert: " + e.getMessage());
		}
	}
	
	@Test
	public void testValidReplaceNoParams() {
		try {
			workflowControl.replaceWorkflow(getXMLFilePath(VALID1_FILE), USER);
			ArgumentCaptor<Workflow> argument = ArgumentCaptor.forClass(Workflow.class);
			Mockito.verify(workflowService).replaceWorkflow(argument.capture());
			assertEquals(USER, argument.getValue().getUser());
			assertEquals(0, argument.getValue().getGlobal().getParameters().getParameter().size());
			assertEquals(1, argument.getValue().getSteps().getStep().size());
		} catch (PladipusReportableException e) {
			fail("Failed replace: " + e.getMessage());
		}
	}
	
	@Test
	public void testInvalidNoName() {
		try {
			workflowControl.createWorkflow(getXMLFilePath(INVALID_NO_NAME_FILE), USER);
			fail("Should not validate xml file with no name");
		} catch (PladipusReportableException e) {
			Mockito.verifyZeroInteractions(workflowService);
			assertTrue(e.getMessage().contains(exceptionMessages.getMessage("template.invalidXml", "")));
			assertTrue(e.getMessage().contains("name"));
		}
	}
	
	@Test
	public void testInvalidNoSteps() {
		try {
			workflowControl.replaceWorkflow(getDocNoSteps(), USER);
			fail("Should not validate xml file with no steps");
		} catch (PladipusReportableException e) {
			Mockito.verifyZeroInteractions(workflowService);
			assertTrue(e.getMessage().contains(exceptionMessages.getMessage("template.invalidXml", "")));
			assertTrue(e.getMessage().contains("steps"));
		}
	}
	
	private String getXMLFilePath(String classpath) {
		try {
			return resourceLoader.getResource(classpath).getFile().getAbsolutePath();
		} catch (IOException e) {
			fail("Could not load XML file " + classpath);
			return null;
		}
	}
	
	private Document getDocNoSteps() {
		try {
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("template");
			rootElement.setAttribute("name", "name");
			Element globalElement = doc.createElement("global");
			rootElement.appendChild(globalElement);
			doc.appendChild(rootElement);
			return doc;
		} catch (Exception e) {
			fail("Failed to create XML document: " + e.getMessage());
			return null;
		}
	}
}
