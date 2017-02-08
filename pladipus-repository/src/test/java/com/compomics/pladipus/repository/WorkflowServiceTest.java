package com.compomics.pladipus.repository;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.pladipus.model.persist.Step;
import com.compomics.pladipus.model.persist.User;
import com.compomics.pladipus.model.persist.Workflow;
import com.compomics.pladipus.model.persist.WorkflowGlobalParameter;
import com.compomics.pladipus.model.persist.WorkflowStepParameter;
import com.compomics.pladipus.shared.PladipusMessages;
import com.compomics.pladipus.shared.PladipusReportableException;
import com.compomics.pladipus.repository.config.RepositoryConfiguration;
import com.compomics.pladipus.repository.config.TestRepositoryConfiguration;
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
	private PladipusMessages exceptionMessages;
	
	private static final String WF1 = "test_workflow1";
	private static final String WF2 = "test_workflow2";
	private static final String WF3 = "test_workflow3";
	private static final String WF4 = "test_workflow4";
	private static final String WF5 = "test_workflow5";
	private static User USER1;
	private static User USER2;
	private static User USER3;
	private static final String TEMPLATE = "template_xml"; // Valid formatted xml not required for this test set
	
	static {
		USER1 = new User();
		USER2 = new User();
		USER3 = new User();
		USER1.setUserId(1L);
		USER2.setUserId(2L);
		USER3.setUserId(3L);
	}

	@Transactional
	@Test
	public void testInsertFailIfAlreadyExists() {
		try {
			Workflow workflow = new Workflow();
			workflow.setUser(USER1);
			workflow.setName(WF1);
			workflow.setTemplateXml(TEMPLATE);
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
			workflow.setUser(USER1);
			workflow.setName(WF2);
			workflow.setTemplateXml(TEMPLATE);
			assertNull(workflow.getId());
			workflowService.insertWorkflow(workflow);
			assertNotNull(workflow.getId());
		} catch (PladipusReportableException e) {
			fail("Insert of workflow should have succeeded: " + e.getMessage());
		}
	}
	
	@Transactional
	@Test
	public void testInsertFailIfNoTemplate() {
		try {
			Workflow workflow = new Workflow();
			workflow.setUser(USER1);
			workflow.setName(WF2);
			workflowService.insertWorkflow(workflow);
			fail("Should not have inserted workflow with no template");
		} catch (PladipusReportableException e) {
			assertTrue(e.getMessage().contains("templateXml"));
		}
	}
	
	@Transactional
	@Test
	public void testReplaceEndsOldWorkflow() {
		try {
			Workflow workflow = new Workflow();
			workflow.setUser(USER1);
			workflow.setName(WF1);
			workflow.setTemplateXml(TEMPLATE);
			Workflow oldWorkflow = workflowService.getActiveWorkflowByName(WF1, USER1);
			assertNotNull(oldWorkflow);
			assertTrue(oldWorkflow.getId().equals(1L));
			workflowService.replaceWorkflow(workflow);
			Workflow newWorkflow = workflowService.getActiveWorkflowByName(WF1, USER1);
			assertNotNull(newWorkflow);
			assertFalse(newWorkflow.getId().equals(1L));
		} catch (PladipusReportableException e) {
			fail("Should have replaced workflow: " + e.getMessage());
		}
	}
	
	@Transactional
	@Test
	public void testReplaceInsertsWhenNoExisting() {
		try {
			Workflow workflow = new Workflow();
			workflow.setUser(USER1);
			workflow.setName(WF5);
			workflow.setTemplateXml(TEMPLATE);
			assertNull(workflow.getId());
			workflowService.replaceWorkflow(workflow);
			assertNotNull(workflow.getId());
		} catch (PladipusReportableException e) {
			fail("Should have inserted workflow: " + e.getMessage());
		}
	}
	
	@Test
	public void testReplaceNotEndOldWorkflowIfNewHasError() throws PladipusReportableException {
		Workflow workflow = new Workflow();
		workflow.setUser(USER1);
		workflow.setName(WF1);
		try {
			Workflow oldWorkflow = workflowService.getActiveWorkflowByName(WF1, USER1);
			assertNotNull(oldWorkflow);
			assertTrue(oldWorkflow.getId().equals(1L));
			workflowService.replaceWorkflow(workflow);
			fail("Replace should not have worked with workflow without template");
		} catch (PladipusReportableException e) {
			Workflow oldWorkflow = workflowService.getActiveWorkflowByName(WF1, USER1);
			assertNotNull(oldWorkflow);
			assertTrue(oldWorkflow.getId().equals(1L));
			assertNull(workflow.getId());
			assertTrue(e.getMessage().contains("templateXml"));
		}
	}
	
	@Transactional
	@Test
	public void testDeactivateWorkflow() {
		try {
			Workflow workflow = workflowService.getActiveWorkflowByName(WF1, USER1);
			assertTrue(workflow.getId().equals(1L));
			workflowService.deactivateWorkflow(workflow);
			assertFalse(workflow.isActive());
			assertNull(workflowService.getActiveWorkflowByName(WF1, USER1));
		} catch (PladipusReportableException e) {
			fail("Should have deactivated workflow: " + e.getMessage());
		}
	}
	
	@Test
	public void testGetActiveWorkflowSuccess() {
		try {
			Workflow workflow = workflowService.getActiveWorkflowByName(WF1, USER1);
			assertNotNull(workflow);
			assertTrue(workflow.getId().equals(1L));
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
			assertTrue(workflow.getId().equals(4L));
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
			assertEquals(exceptionMessages.getMessage("db.nonUniqueGet", "Workflow"), e.getMessage());
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
	
	@Test
	public void testGetLoadsAllTablesInfo() {
		try {
			Workflow workflow = workflowService.getActiveWorkflowByName(WF3, USER2);
			assertNotNull(workflow);
			Set<Step> steps = workflow.getTemplateSteps();
			assertNotNull(steps);
			assertEquals(2, steps.size());
			Iterator<Step> iter = steps.iterator();
			while (iter.hasNext()) {
				Step step = iter.next();
				if (step.getId().equals("s1")) {
					assertEquals("One", step.getName());
					assertEquals(1, step.getPrereqs().size());
					assertEquals(2, step.getStepParams().size());
					Iterator<WorkflowStepParameter> params = step.getStepParams().iterator();
					while (params.hasNext()) {
						WorkflowStepParameter param = params.next();
						if (param.getName().equals("input_one")) {
							assertEquals(1, param.getValues().size());
						} else if (param.getName().equals("input_no_type")) {
							assertEquals(0, param.getValues().size());
						} else {
							fail("Invalid step parameter found: " + param.getName());
						}
					}
				} else if (step.getId().equals("s2")) {
					assertEquals("Two", step.getName());
					assertEquals(0, step.getPrereqs().size());
					assertEquals(0, step.getStepParams().size());
				} else {
					fail("Invalid step found: " + step.getId());
				}
			}
			
			Set<WorkflowGlobalParameter> globals = workflow.getGlobalParams();
			assertEquals(2, globals.size());
			Iterator<WorkflowGlobalParameter> glob = globals.iterator();
			while (glob.hasNext()) {
				WorkflowGlobalParameter param = glob.next();
				if (param.getName().equals("globalOne")) {
					assertEquals(0, param.getValues().size());
				} else if (param.getName().equals("globalTwo")) {
					assertEquals(2, param.getValues().size());
				} else {
					fail("Invalid global parameter: " + param.getName());
				}
			}
		} catch (PladipusReportableException e) {
			fail("Failed to get workflow: " + e.getMessage());
		}
	}
}
