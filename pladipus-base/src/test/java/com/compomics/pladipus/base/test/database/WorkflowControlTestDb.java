package com.compomics.pladipus.base.test.database;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.compomics.pladipus.base.WorkflowControl;
import com.compomics.pladipus.base.config.BaseConfiguration;
import com.compomics.pladipus.model.core.Workflow;
import com.compomics.pladipus.shared.PladipusMessages;
import com.compomics.pladipus.shared.PladipusReportableException;
import com.compomics.pladipus.repository.config.TestRepositoryConfiguration;
import com.compomics.pladipus.repository.service.WorkflowService;
import com.compomics.pladipus.test.tools.config.TestToolsConfiguration;

/**
 * Tests for class which retrieves and stores workflow information, with database interaction.  Uses in-memory database.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={BaseConfiguration.class, TestToolsConfiguration.class, TestRepositoryConfiguration.class}, loader=AnnotationConfigContextLoader.class)
@Transactional
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
						  TransactionalTestExecutionListener.class })
public class WorkflowControlTestDb {

	@Autowired
	private WorkflowControl workflowControl;
	
	@Autowired
	private WorkflowService workflowService;
	
	@Autowired
	private PladipusMessages exceptionMessages;
	
	@Autowired
	private ResourceLoader resourceLoader;
	
	private static final String VALID1_FILE = "classpath:valid1.xml";
	private static final String EXISTING_FILE = "classpath:existing.xml";
	private static final int USERID1 = 1;
	private static final String VALID1_NAME = "valid1";
	private static final String EXISTING_NAME = "test_workflow1";
	
	@Test
	public void testValidCreateFromFile() throws IOException {
		try {
			assertNull(workflowService.getActiveWorkflowByName(VALID1_NAME, USERID1));
			Workflow created = workflowControl.createWorkflow(getXMLFilePath(VALID1_FILE), USERID1);
			Workflow fromDb = workflowService.getActiveWorkflowByName(VALID1_NAME, USERID1);
			assertNotNull(fromDb);
			assertNotNull(created);
			assertEquals(created.getId(), fromDb.getId());
		} catch (PladipusReportableException e) {
			fail("Failed to insert workflow: " + e.getMessage());
		}
	}
	
	@Test
	public void testValidCreateFromDocument() {
		try {
			assertNull(workflowService.getActiveWorkflowByName(VALID1_NAME, USERID1));
			Workflow created = workflowControl.createWorkflow(getBasicXML(VALID1_NAME, true), USERID1);
			Workflow fromDb = workflowService.getActiveWorkflowByName(VALID1_NAME, USERID1);
			assertNotNull(fromDb);
			assertNotNull(created);
			assertEquals(created.getId(), fromDb.getId());
		} catch (PladipusReportableException e) {
			fail("Failed to insert workflow: " + e.getMessage());
		}
	}
	
	@Test
	public void testCreateFailWhenAlreadyExists() {
		try {
			assertNotNull(workflowService.getActiveWorkflowByName(EXISTING_NAME, USERID1));
			workflowControl.createWorkflow(getBasicXML(EXISTING_NAME, true), USERID1);
			fail("Should not have been able to insert workflow with existing name");
		} catch (PladipusReportableException e) {
			assertEquals(exceptionMessages.getMessage("db.workflowExists", EXISTING_NAME), e.getMessage());
		}		
	}
	
	@Test
	public void testValidReplaceFromFile() {
		try{
			Workflow existing = workflowService.getActiveWorkflowByName(EXISTING_NAME, USERID1);
			assertNotNull(existing);
			Workflow replaced = workflowControl.replaceWorkflow(getXMLFilePath(EXISTING_FILE), USERID1);
			Workflow fromDb = workflowService.getActiveWorkflowByName(EXISTING_NAME, USERID1);
			assertNotNull(fromDb);
			assertNotNull(replaced);
			assertEquals(replaced.getId(), fromDb.getId());
			assertNotEquals(existing.getId(), replaced.getId());
		} catch (PladipusReportableException e) {
			fail("Failed to replace workflow: " + e.getMessage());
		}
	}
	
	@Test
	public void testValidReplaceFromDocument() {
		try {
			Workflow existing = workflowService.getActiveWorkflowByName(EXISTING_NAME, USERID1);
			assertNotNull(existing);
			Workflow replaced = workflowControl.replaceWorkflow(getBasicXML(EXISTING_NAME, true), USERID1);
			Workflow fromDb = workflowService.getActiveWorkflowByName(EXISTING_NAME, USERID1);
			assertNotNull(fromDb);
			assertNotNull(replaced);
			assertEquals(replaced.getId(), fromDb.getId());
			assertNotEquals(existing.getId(), replaced.getId());
		} catch (PladipusReportableException e) {
			fail("Failed to replace workflow: " + e.getMessage());
		}
	}
	
	@Test
	public void testCreateFailWithInvalidXML() throws PladipusReportableException {
		try {
			assertNull(workflowService.getActiveWorkflowByName(VALID1_NAME, USERID1));
			workflowControl.createWorkflow(getBasicXML(VALID1_NAME, false), USERID1);
			fail("Should not have been able to create workflow with invalid XML");
		} catch (PladipusReportableException e) {
			assertNull(workflowService.getActiveWorkflowByName(VALID1_NAME, USERID1));
		}
	}
	
	@Test
	public void testReplaceFailWithInvalidXML() throws PladipusReportableException {
		Workflow existing = new Workflow();
		try {
			existing = workflowService.getActiveWorkflowByName(EXISTING_NAME, USERID1);
			assertNotNull(existing);
			workflowControl.replaceWorkflow(getBasicXML(EXISTING_NAME, false), USERID1);
			fail("Should not have been able to replace workflow with invalid XML");
		} catch (PladipusReportableException e) {
			Workflow fromDb = workflowService.getActiveWorkflowByName(EXISTING_NAME, USERID1);
			assertEquals(existing.getId(), fromDb.getId());
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
	
	private Document getBasicXML(String name, boolean valid) {
		try {
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("template");
			rootElement.setAttribute("name", name);
			if (valid) {
				rootElement.appendChild(getStep(doc));
			}
			doc.appendChild(rootElement);
			return doc;
		} catch (Exception e) {
			fail("Failed to create XML document: " + e.getMessage());
			return null;
		}
	}
	
	private Element getStep(Document doc) {
		Element stepsElement = doc.createElement("steps");
		Element stepElement = doc.createElement("step");
		Element idElement = doc.createElement("id");
		idElement.appendChild(doc.createTextNode("s1"));
		Element nameElement = doc.createElement("name");
		nameElement.appendChild(doc.createTextNode("Four"));
		stepElement.appendChild(idElement);
		stepElement.appendChild(nameElement);
		stepsElement.appendChild(stepElement);
		return stepsElement;
	}
}
