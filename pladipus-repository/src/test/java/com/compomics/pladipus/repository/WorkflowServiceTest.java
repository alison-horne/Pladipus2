package com.compomics.pladipus.repository;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.pladipus.model.core.Workflow;
import com.compomics.pladipus.model.db.WorkflowsColumn;
import com.compomics.pladipus.shared.PladipusMessages;
import com.compomics.pladipus.shared.PladipusReportableException;
import com.compomics.pladipus.repository.config.RepositoryConfiguration;
import com.compomics.pladipus.repository.config.TestRepositoryConfiguration;
import com.compomics.pladipus.repository.dao.BaseDAO;
import com.compomics.pladipus.repository.dao.Query;
import com.compomics.pladipus.repository.service.WorkflowService;

/**
 * Tests the WorkflowService bean using in-memory database
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={RepositoryConfiguration.class, TestRepositoryConfiguration.class}, loader=AnnotationConfigContextLoader.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
						  TransactionalTestExecutionListener.class })
public class WorkflowServiceTest {
	
	@Autowired
	private WorkflowService workflowService;
	
	@Autowired
	private BaseDAO<Workflow> workflowDAO;
	
	@Autowired
	private PladipusMessages exceptionMessages;
	
	private static final String WF1 = "test_workflow1";
	private static final String WF2 = "test_workflow2";
	private static final String WF3 = "test_workflow3";
	private static final String WF4 = "test_workflow4";
	private static final String WF5 = "test_workflow5";
	private static final int USER1 = 1;
	private static final int USER2 = 2;
	private static final int USER3 = 3;
	private static final String TEMPLATE = "template_xml"; // Valid formatted xml not required for this test set
	private static final String TEMPLATE1 = "template1";

	@Transactional
	@Test
	public void testInsertFailIfAlreadyExists() {
		try {
			Workflow workflow = new Workflow();
			workflow.setUserId(USER1);
			workflow.setWorkflowName(WF1);
			workflow.setTemplate(TEMPLATE);
			workflowService.insertWorkflow(workflow);
			fail("Should not have inserted duplicate workflow");
		} catch (PladipusReportableException e) {
			assertEquals(exceptionMessages.getMessage("db.workflowExists", WF1), e.getMessage());
		}
	}
	
	@Transactional
	@Test
	public void testInsertSuccessIfNoActiveExists() {
		try {
			Workflow workflow = new Workflow();
			workflow.setUserId(USER1);
			workflow.setWorkflowName(WF2);
			workflow.setTemplate(TEMPLATE);
			assertEquals(-1, workflow.getId());
			workflow = workflowService.insertWorkflow(workflow);
			assertNotEquals(-1, workflow.getId());
		} catch (PladipusReportableException e) {
			fail("Insert of workflow should have succeeded: " + e.getMessage());
		}
	}
	
	@Transactional
	@Test
	public void testInsertFailIfNoTemplate() {
		try {
			Workflow workflow = new Workflow();
			workflow.setUserId(USER1);
			workflow.setWorkflowName(WF2);
			workflowService.insertWorkflow(workflow);
			fail("Should not have inserted workflow with no template");
		} catch (PladipusReportableException e) {
			assertEquals(exceptionMessages.getMessage("db.invalidInsert", "workflow"), e.getMessage());
		}
	}
	
	@Transactional
	@Test
	public void testReplaceEndsOldWorkflow() {
		try {
			Workflow workflow = new Workflow();
			workflow.setUserId(USER1);
			workflow.setWorkflowName(WF1);
			workflow.setTemplate(TEMPLATE);
			assertTrue(isWorkflowActive(1));
			workflow = workflowService.replaceWorkflow(workflow);
			assertFalse(isWorkflowActive(1));
			assertNotEquals(1, workflow.getId());
			assertTrue(isWorkflowActive(workflow.getId()));
		} catch (PladipusReportableException e) {
			fail("Should have replaced workflow: " + e.getMessage());
		}
	}
	
	@Transactional
	@Test
	public void testReplaceInsertsWhenNoExisting() {
		try {
			Workflow workflow = new Workflow();
			workflow.setUserId(USER1);
			workflow.setWorkflowName(WF5);
			workflow.setTemplate(TEMPLATE);
			assertEquals(-1, workflow.getId());
			workflow = workflowService.replaceWorkflow(workflow);
			assertNotEquals(-1, workflow.getId());
			assertTrue(isWorkflowActive(workflow.getId()));
		} catch (PladipusReportableException e) {
			fail("Should have inserted workflow: " + e.getMessage());
		}
	}
	
	@Test
	public void testReplaceNotEndOldWorkflowIfNewHasError() throws PladipusReportableException {
		Workflow workflow = new Workflow();
		workflow.setUserId(USER1);
		workflow.setWorkflowName(WF1);
		try {
			assertTrue(isWorkflowActive(1));
			workflow = workflowService.replaceWorkflow(workflow);
			fail("Replace should not have worked with workflow without template");
		} catch (PladipusReportableException e) {
			assertTrue(isWorkflowActive(1));
			assertEquals(-1, workflow.getId());
			assertEquals(exceptionMessages.getMessage("db.invalidInsert", "workflow"), e.getMessage());
		}
	}
	
	@Transactional
	@Test
	public void testDeactivateWorkflow() {
		try {
			Workflow workflow = getWorkflow(1);
			assertTrue(workflow.isActive());
			workflowService.deactivateWorkflow(workflow);
			assertFalse(isWorkflowActive(1));
		} catch (PladipusReportableException e) {
			fail("Should have deactivated workflow: " + e.getMessage());
		}
	}
	
	@Transactional
	@Test
	public void testDeactivateErrorIfNoId() {
		try {
			Workflow workflow = new Workflow();
			workflow.setUserId(USER1);
			workflow.setTemplate(TEMPLATE);
			workflow.setWorkflowName(WF1);
			workflowService.deactivateWorkflow(workflow);
			fail("Deactivate should fail if no ID on workflow object");
		} catch (PladipusReportableException e) {
			assertEquals(exceptionMessages.getMessage("db.invalidUpdate", "workflow"), e.getMessage());
		}
	}
	
	@Test
	public void testGetActiveWorkflowSuccess() {
		try {
			Workflow workflow = workflowService.getActiveWorkflowByName(WF1, USER1);
			assertNotNull(workflow);
			assertEquals(1, workflow.getId());
			assertEquals(TEMPLATE1, workflow.getTemplate());
		} catch (PladipusReportableException e) {
			fail("Should have got workflow: " + e.getMessage());
		}
	}
	
	@Test
	public void testGetActiveWorkflowNullIfWrongUser() {
		try {
			assertNull(workflowService.getActiveWorkflowByName(WF1, USER2));
		} catch (PladipusReportableException e) {
			fail("Get workflow should not fail if none found: " + e.getMessage());
		}
	}
	
	@Test
	public void testGetActiveWorkflowFiltersOnUserForSameName() {
		try {
			Workflow workflow = workflowService.getActiveWorkflowByName(WF3, USER2);
			assertNotNull(workflow);
			assertEquals(4, workflow.getId());
			assertEquals(TEMPLATE1, workflow.getTemplate());
		} catch (PladipusReportableException e) {
			fail("Should have got workflow: " + e.getMessage());
		}
	}
	
	@Test
	public void testGetActiveWorkflowNullIfInactive() {
		try {
			assertNull(workflowService.getActiveWorkflowByName(WF2, USER1));
		} catch (PladipusReportableException e) {
			fail("Get workflow should not fail if none found: " + e.getMessage());
		}
	}
	
	@Test
	public void testGetActiveWorkflowExceptionIfMoreThanOne() {
		try {
			workflowService.getActiveWorkflowByName(WF4, USER2);
			fail("Duplicate active workflow should throw exception");
		} catch (PladipusReportableException e) {
			assertEquals(exceptionMessages.getMessage("db.nonUniqueGet", "workflow"), e.getMessage());
		}
	}
	
	@Test
	public void testGetAllActiveEmptyIfNone() {
		try {
			List<Workflow> workflowList = workflowService.getAllActiveWorkflowsForUser(USER3);
			assertNotNull(workflowList);
			assertTrue(workflowList.isEmpty());
		} catch (PladipusReportableException e) {
			fail("Get all workflows should not fail if none found: " + e.getMessage());
		}
	}
	
	@Test
	public void testGetAllActiveReturnsList() {
		try {
			List<Workflow> workflowList = workflowService.getAllActiveWorkflowsForUser(USER1);
			assertNotNull(workflowList);
			assertEquals(2, workflowList.size());
		} catch (PladipusReportableException e) {
			fail("Get all workflows should not fail: " + e.getMessage());
		}
	}
	
	private boolean isWorkflowActive(int workflowId) throws PladipusReportableException {
		Workflow workflow = getWorkflow(workflowId);
		return workflow.isActive();
	}
	
	private Workflow getWorkflow(int workflowId) throws PladipusReportableException {
		Query query = new Query();
		query.setWhereClause("WHERE " + WorkflowsColumn.WORKFLOW_ID.name() + " = :wfid");
		query.setParameters(new MapSqlParameterSource().addValue("wfid", workflowId));
		Workflow workflow = workflowDAO.get(query);
		assertNotNull(workflow);
		return workflow;
	}
}
