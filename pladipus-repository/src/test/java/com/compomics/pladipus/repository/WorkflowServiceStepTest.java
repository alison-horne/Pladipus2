package com.compomics.pladipus.repository;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
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

import com.compomics.pladipus.model.core.Step;
import com.compomics.pladipus.model.core.Workflow;
import com.compomics.pladipus.model.db.WorkflowStepsColumn;
import com.compomics.pladipus.model.db.WorkflowsColumn;
import com.compomics.pladipus.shared.PladipusMessages;
import com.compomics.pladipus.shared.PladipusReportableException;
import com.compomics.pladipus.repository.config.RepositoryConfiguration;
import com.compomics.pladipus.repository.config.TestRepositoryConfiguration;
import com.compomics.pladipus.repository.dao.BaseDAO;
import com.compomics.pladipus.repository.dao.Query;
import com.compomics.pladipus.repository.service.WorkflowService;

/**
 * Tests the WorkflowService bean inserts into all DB tables using in-memory database
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={RepositoryConfiguration.class, TestRepositoryConfiguration.class}, loader=AnnotationConfigContextLoader.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
						  TransactionalTestExecutionListener.class })
public class WorkflowServiceStepTest {
	
	@Autowired
	private WorkflowService workflowService;
	
	@Autowired
	private BaseDAO<Workflow> workflowDAO;
	
	@Autowired
	private BaseDAO<Step> workflowStepDAO;
	
	@Autowired
	private PladipusMessages exceptionMessages;
	
	private static final String WF_NAME = "workflow_name";
	private static final int USER1 = 1;
	private static final String TEMPLATE = "template_xml"; // Valid formatted xml not required for this test set
	private static final String STEP1_NAME = "step1";
	private static final String STEP1_ID = "s1";
	private static final String STEP2_NAME = "step2";
	private static final String STEP2_ID = "s2";
	private Workflow workflow;
	
	@Before
	public void setUp() {
		workflow = initWorkflow();
	}

	@Transactional
	@Test
	public void testInsertSteps() {
		try {
			Step step1 = new Step();
			step1.setStepIdentifier(STEP1_ID);
			step1.setToolType(STEP1_NAME);
			Step step2 = new Step();
			step2.setStepIdentifier(STEP2_ID);
			step2.setToolType(STEP2_NAME);
			workflow.addStep(step1);
			workflow.addStep(step2);
			workflowService.insertWorkflow(workflow);
			assertNotNull(getWorkflow(workflow.getId()));
			List<Step> steps = getSteps(workflow.getId());
			assertEquals(2, steps.size());
		} catch (PladipusReportableException e) {
			fail("Insert of workflow should have succeeded: " + e.getMessage());
		}
	}
	
	@Transactional
	@Test
	public void testInsertNoSteps() {
		try {
			workflowService.insertWorkflow(workflow);
			assertNotNull(getWorkflow(workflow.getId()));
			List<Step> steps = getSteps(workflow.getId());
			assertEquals(0, steps.size());
		} catch (PladipusReportableException e) {
			fail("Insert of workflow should have succeeded: " + e.getMessage());
		}
	}
	
	@Test
	public void testInsertFailWithInvalidStep() throws PladipusReportableException {
		try {
			Step step = new Step();
			step.setStepIdentifier(STEP1_ID);
			workflow.addStep(step);
			workflowService.insertWorkflow(workflow);
			fail("Should not insert step with no tool name");
		} catch (PladipusReportableException e) {
			assertEquals(exceptionMessages.getMessage("db.invalidInsert", "step"), e.getMessage());
			// Check transaction was rolled back
			assertNull(getWorkflow(workflow.getId()));
		}
	}
	
	private Workflow initWorkflow() {
		Workflow workflow = new Workflow();
		workflow.setUserId(USER1);
		workflow.setWorkflowName(WF_NAME);
		workflow.setTemplate(TEMPLATE);
		return workflow;
	}
	
	private Workflow getWorkflow(int workflowId) throws PladipusReportableException {
		Query query = new Query();
		query.setWhereClause("WHERE " + WorkflowsColumn.WORKFLOW_ID.name() + " = :wfid");
		query.setParameters(new MapSqlParameterSource().addValue("wfid", workflowId));
		Workflow workflow = workflowDAO.get(query);
		return workflow;
	}
	
	private List<Step> getSteps(int workflowId) throws PladipusReportableException {
		Query query = new Query();
		query.setWhereClause("WHERE " + WorkflowStepsColumn.WORKFLOW_ID.name() + " = :wfid");
		query.setParameters(new MapSqlParameterSource().addValue("wfid", workflowId));
		return workflowStepDAO.getList(query);
	}
}
