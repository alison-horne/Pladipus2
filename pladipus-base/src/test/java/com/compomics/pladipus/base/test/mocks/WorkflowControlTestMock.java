package com.compomics.pladipus.base.test.mocks;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
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
import com.compomics.pladipus.model.core.Step;
import com.compomics.pladipus.model.core.Workflow;
import com.compomics.pladipus.model.exceptions.PladipusMessages;
import com.compomics.pladipus.model.exceptions.PladipusReportableException;
import com.compomics.pladipus.repository.config.MockRepositoryConfiguration;
import com.compomics.pladipus.repository.service.WorkflowService;
import com.compomics.pladipus.test.tools.config.TestToolsConfiguration;

/**
 * Tests to check workflowControl function without calling database.
 * These tests will check validation and parsing of XML template to Workflow object.
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
	
	private static final int INSERT_ID = 10;
	private static final int USER_ID = 5;
	private static final String VALID1_FILE = "classpath:valid1.xml";
	private static final String VALID2_FILE = "classpath:valid2.xml";
	private static final String INVALID_NO_NAME_FILE = "classpath:invalid_noname.xml";

	@Before
	public void setUp() throws PladipusReportableException {
		Mockito.reset(workflowService);
		setServiceDefaults();
	}
	
	@Test
	public void testValidInsertNoParams() {
		try {
			Workflow workflow = workflowControl.createWorkflow(getXMLFilePath(VALID1_FILE), USER_ID);
			Mockito.verify(workflowService).insertWorkflow(Mockito.any(Workflow.class));
			assertEquals(USER_ID, workflow.getUserId());
			assertEquals(INSERT_ID, workflow.getId());
			assertEquals(0, workflow.getGlobalParameters().size());
			assertEquals(1, workflow.getSteps().size());
		} catch (PladipusReportableException e) {
			fail("Failed insert: " + e.getMessage());
		}
	}
	
	@Test
	public void testValidReplaceNoParams() {
		try {
			Workflow workflow = workflowControl.replaceWorkflow(getXMLFilePath(VALID1_FILE), USER_ID);
			Mockito.verify(workflowService).replaceWorkflow(Mockito.any(Workflow.class));
			assertEquals(USER_ID, workflow.getUserId());
			assertEquals(INSERT_ID, workflow.getId());
			assertEquals(0, workflow.getGlobalParameters().size());
			assertEquals(1, workflow.getSteps().size());
		} catch (PladipusReportableException e) {
			fail("Failed replace: " + e.getMessage());
		}
	}
	
	@Test
	public void testParsing() {
		try {
			Workflow workflow = workflowControl.createWorkflow(getXMLFilePath(VALID2_FILE), USER_ID);
			assertEquals("valid2", workflow.getWorkflowName());
			
			Map<String, Set<String>> globals = workflow.getGlobalParameters();
			assertEquals(1, globals.size());
			assertTrue(globals.containsKey("GlobalParam1"));
			assertEquals(1, globals.get("GlobalParam1").size());
			assertTrue(globals.get("GlobalParam1").contains("Value1"));
			
			Map<String, Step> steps = workflow.getSteps();
			assertEquals(2, steps.size());
			
			Step s1 = steps.get("s1");
			assertNotNull(s1);
			assertEquals(0, s1.getStepParameters().size());
			assertEquals("Four", s1.getToolType());
			
			Step s2 = steps.get("s2");
			assertNotNull(s2);
			assertEquals("One", s2.getToolType());
			Map<String, Set<String>> stepParams = s2.getStepParameters();
			assertEquals(2, stepParams.size());
			assertTrue(stepParams.containsKey("input_one"));
			assertTrue(stepParams.containsKey("input_no_type"));
			assertEquals(0, stepParams.get("input_one").size());
			assertEquals(2, stepParams.get("input_no_type").size());
		} catch (PladipusReportableException e) {
			fail("Failed parsing: " + e.getMessage());
		}
	}
	
	@Test
	public void testInvalidNoName() {
		try {
			workflowControl.createWorkflow(getXMLFilePath(INVALID_NO_NAME_FILE), USER_ID);
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
			workflowControl.replaceWorkflow(getDocNoSteps(), USER_ID);
			fail("Should not validate xml file with no steps");
		} catch (PladipusReportableException e) {
			Mockito.verifyZeroInteractions(workflowService);
			assertTrue(e.getMessage().contains(exceptionMessages.getMessage("template.invalidXml", "")));
			assertTrue(e.getMessage().contains("steps"));
		}
	}
	
	/**
	 * Mock database inserts - return the parsed workflow with ID set, so it can be checked.
	 */
	private void setServiceDefaults() throws PladipusReportableException {
		Answer<Workflow> answer = new Answer<Workflow>() {
			@Override
			public Workflow answer(InvocationOnMock invocation) throws Throwable {
				Workflow workflow = invocation.getArgumentAt(0, Workflow.class);
				workflow.setId(INSERT_ID);
				return workflow;
			}
		};
		Mockito.when(workflowService.insertWorkflow(Mockito.any(Workflow.class))).thenAnswer(answer);
		Mockito.when(workflowService.replaceWorkflow(Mockito.any(Workflow.class))).thenAnswer(answer);
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
