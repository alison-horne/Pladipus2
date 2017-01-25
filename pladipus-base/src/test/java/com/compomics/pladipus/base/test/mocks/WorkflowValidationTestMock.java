package com.compomics.pladipus.base.test.mocks;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.util.collections.Sets;
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

import com.compomics.pladipus.base.config.BaseConfiguration;
import com.compomics.pladipus.base.helper.ValidationChecker;
import com.compomics.pladipus.model.core.Default;
import com.compomics.pladipus.model.core.Step;
import com.compomics.pladipus.model.core.Workflow;
import com.compomics.pladipus.shared.PladipusMessages;
import com.compomics.pladipus.shared.PladipusReportableException;
import com.compomics.pladipus.model.parameters.InputParameter;
import com.compomics.pladipus.repository.config.MockRepositoryConfiguration;
import com.compomics.pladipus.repository.service.DefaultService;
import com.compomics.pladipus.test.tools.config.MockToolsConfiguration;
import com.compomics.pladipus.tools.core.ToolInfo;
import com.compomics.pladipus.tools.core.ToolInfoProvider;
import com.compomics.pladipus.tools.core.impl.PladipusToolInfo;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.ImmutableSortedMap;

/**
 * Tests to check workflowValidator function without calling database.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={BaseConfiguration.class, MockToolsConfiguration.class, MockRepositoryConfiguration.class}, loader=AnnotationConfigContextLoader.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
						  TransactionalTestExecutionListener.class })
@DirtiesContext(classMode=ClassMode.AFTER_EACH_TEST_METHOD)
public class WorkflowValidationTestMock {
	
	@Autowired
	private ToolInfoProvider pladipusToolInfoProvider;
	
	@Autowired
	private DefaultService defaultService;
	
	@Autowired
	private ValidationChecker<Workflow> workflowValidator;
	
	@Autowired
	private PladipusMessages exceptionMessages;
	
	private Workflow workflow;
	private static final String PARAM1 = "param1";
	private static final String PARAM2 = "param2";
	private static final String PARAM3 = "param3";
	private static final String PARAM_DEF = "default_value";
	private static final InputParameter paramNonMand = new InputParameter(PARAM1, null, false);
	private static final InputParameter paramMandWithDefault = new InputParameter(PARAM2, null, true, PARAM_DEF);
	private static final InputParameter paramMandNoDefault = new InputParameter(PARAM3, null, true);
	private static final int USER1 = 1;
	private static final String STEP1 = "s1";
	private static final String STEP2 = "s2";
	private static final String STEP3 = "s3";
	private static final String STEP4 = "s4";
	private static final String TOOL1 = "tool1";
	private static final String TOOL2 = "tool2";
	private static final String DEF1 = "def1";
	private static final int DEF1_ID = 1;
	private static final String GLOBAL1 = "global1";
	private static final String GLOBAL2 = "global2";
	private static final String VALUE1 = "value1";
	private static final String VALUE2 = "value2";
	private static final String GLOBALVALUE1 = "globalvalue1";

	@Before
	public void setUp() throws PladipusReportableException {
		Mockito.reset(defaultService, pladipusToolInfoProvider);
		setupWorkflow();
	}
	
	@Test
	public void testInvalidToolName() throws PladipusReportableException {
		try {
			initMocks(true, true, false, false);
			workflowValidator.validate(workflow);
			fail("Should have failed on invalid tool " + TOOL2);
		} catch (PladipusReportableException e) {
			assertEquals(exceptionMessages.getMessage("template.invalidXmlTool", TOOL2), e.getMessage());
			Mockito.verify(pladipusToolInfoProvider).getAllToolInfo();
			Mockito.verifyZeroInteractions(defaultService);
		}
	}
	
	@Test
	public void testInvalidParamName() throws PladipusReportableException {
		try {
			initMocks(true, false, true, false);
			workflowValidator.validate(workflow);
			fail("Should have failed on invalid step parameter " + PARAM2);
		} catch (PladipusReportableException e) {
			assertEquals(exceptionMessages.getMessage("template.invalidParameterName", STEP1, PARAM2), e.getMessage());
			Mockito.verify(pladipusToolInfoProvider).getAllToolInfo();
			Mockito.verifyZeroInteractions(defaultService);
		}
	}
	
	@Test
	public void testInvalidSubstitutionNoPeriod() throws PladipusReportableException {
		String BAD_PARAM = "{$GLOBALwithnodot}";
		try {
			initMocks(true, true, true, false);
			workflow.getSteps().get(STEP3).addParameterValues(PARAM1, Sets.newSet(BAD_PARAM));
			workflowValidator.validate(workflow);
			fail("Should not allow invalid substitution");
		} catch (PladipusReportableException e) {
			assertEquals(exceptionMessages.getMessage("template.subFormat", BAD_PARAM), e.getMessage());
			Mockito.verify(pladipusToolInfoProvider).getAllToolInfo();
			Mockito.verify(defaultService).getDefaultsForUser(USER1);
		}
	}
	
	@Test
	public void testInvalidSubstitutionBrackets() throws PladipusReportableException {
		String BAD_PARAM = "param {$with no closing bracket";
		try {
			initMocks(true, true, true, false);			
			workflow.getSteps().get(STEP3).addParameterValues(PARAM1, Sets.newSet(BAD_PARAM));
			workflowValidator.validate(workflow);
			fail("Should not allow invalid substitution");
		} catch (PladipusReportableException e) {
			assertEquals(exceptionMessages.getMessage("template.subFormat", BAD_PARAM), e.getMessage());
			Mockito.verify(pladipusToolInfoProvider).getAllToolInfo();
			Mockito.verify(defaultService).getDefaultsForUser(USER1);
		}
	}
	
	@Test
	public void testInvalidDefault() throws PladipusReportableException {
		try {
			initMocks(false, true, true, false);
			workflowValidator.validate(workflow);
			fail("Should not allow invalid default");
		} catch (PladipusReportableException e) {
			assertEquals(exceptionMessages.getMessage("template.invalidDefault", "DEFAULT." + DEF1), e.getMessage());
			Mockito.verify(pladipusToolInfoProvider).getAllToolInfo();
			Mockito.verify(defaultService).getDefaultsForUser(USER1);
		}
	}
	
	@Test
	public void testMissingMandatoryParamNoDefault() throws PladipusReportableException {
		try {
			initMocks(true, true, true, true);
			workflowValidator.validate(workflow);
			fail("Should not allow missing mandatory parameter");
		} catch (PladipusReportableException e) {
			assertTrue(e.getMessage().contains("mandatory"));
			assertTrue(e.getMessage().contains(PARAM3));
			Mockito.verify(pladipusToolInfoProvider).getAllToolInfo();
			Mockito.verifyZeroInteractions(defaultService);
		}
	}
	
	@Test
	public void testDirectStepDependency() throws PladipusReportableException {
		try {
			initMocks(true, true, true, false);
			// s2 -> s3 -> s2
			workflow.getSteps().get(STEP3).addParameterValues(PARAM1, Sets.newSet("{$" + STEP2 + ".out}"));
			workflowValidator.validate(workflow);
			fail("Should not allow step dependency");
		} catch (PladipusReportableException e) {
			assertTrue(e.getMessage().contains("Circular dependency"));
			Mockito.verify(pladipusToolInfoProvider).getAllToolInfo();
			Mockito.verify(defaultService).getDefaultsForUser(USER1);
		}
	}
	
	@Test
	public void testIndirectStepDependency() throws PladipusReportableException {
		try {
			initMocks(true, true, true, false);
			// s1 -> s3 -> s4 -> s1
			workflow.getSteps().get(STEP3).addParameterValues(PARAM1, Sets.newSet("{$" + STEP4 + ".out}"));
			Step step4 = new Step();
			step4.setStepIdentifier(STEP4);
			step4.setToolType(TOOL1);
			step4.addParameterValues(PARAM1, Sets.newSet("{$" + STEP1 + ".out}"));
			workflow.addStep(step4);
			workflowValidator.validate(workflow);
			fail("Should not allow step dependency");
		} catch (PladipusReportableException e) {
			assertTrue(e.getMessage().contains("Circular dependency"));
			Mockito.verify(pladipusToolInfoProvider).getAllToolInfo();
			Mockito.verify(defaultService).getDefaultsForUser(USER1);
		}
	}
	
	@Test
	public void testInvalidGlobalSubstitution() throws PladipusReportableException {
		try {
			initMocks(true, true, true, false);
			workflow.addParameterValues(GLOBAL1, Sets.newSet("{$invalid.input}"));
			workflowValidator.validate(workflow);
			fail("Should not allow invalid global substitution");
		} catch (PladipusReportableException e) {
			assertEquals(exceptionMessages.getMessage("template.invalidGlobalSubstitution", "invalid.input"), e.getMessage());
			Mockito.verify(pladipusToolInfoProvider).getAllToolInfo();
			Mockito.verify(defaultService, Mockito.times(2)).getDefaultsForUser(USER1);
		}
	}
	
	@Test
	public void testCorrectValidation() {
		try {
			initMocks(true, true, true, false);
			workflowValidator.validate(workflow);
			Mockito.verify(pladipusToolInfoProvider).getAllToolInfo();
			Mockito.verify(defaultService, Mockito.times(2)).getDefaultsForUser(USER1);
			verifyWorkflowUpdates();
		} catch (PladipusReportableException e) {
			fail("Validation should have succeeded: " + e.getMessage());
		}
	}
	
	private void setupWorkflow() {
		workflow = new Workflow();
		workflow.setTemplate("template");
		workflow.setUserId(USER1);
		workflow.setWorkflowName("workflow");
		
		Step step1 = new Step();
		step1.setStepIdentifier(STEP1);
		step1.setToolType(TOOL1);
		step1.addParameterValues(PARAM1, Sets.newSet("{$DEFAULT." + DEF1 + "}", VALUE2, "{$" + STEP3 + ".out}", "{$" + STEP2 + ".out}"));
		step1.addParameterValues(PARAM2, null);
		Step step2 = new Step();
		step2.setStepIdentifier(STEP2);
		step2.setToolType(TOOL2);
		step2.addParameterValues(PARAM1, Sets.newSet("{$GLOBAL." + GLOBAL1 + "}/{$GLOBAL." + GLOBAL2 + "}"));
		step2.addParameterValues(PARAM2, Sets.newSet("{$" + STEP3 + ".out}"));
		Step step3 = new Step();
		step3.setStepIdentifier(STEP3);
		step3.setToolType(TOOL1);
		step3.addParameterValues(PARAM1, Sets.newSet(VALUE1));
		workflow.addStep(step1);
		workflow.addStep(step2);
		workflow.addStep(step3);
		
		workflow.addParameterValues(GLOBAL1, Sets.newSet(GLOBALVALUE1));
	}
	
	private ImmutableMap<String, ToolInfo> getTools(boolean validParams, boolean validTools, boolean mand) {
		ImmutableSortedMap.Builder<String, ToolInfo> builder = ImmutableSortedMap.naturalOrder();
		
		ToolInfo tool1Info = new PladipusToolInfo();
		ImmutableSet.Builder<InputParameter> bld1 = new Builder<InputParameter>();
		bld1.add(paramNonMand);
		if (validParams) bld1.add(paramMandWithDefault);
		if (mand) bld1.add(paramMandNoDefault);
		tool1Info.setInputParams(bld1.build());
		builder.put(TOOL1, tool1Info);
		
		if (validTools) {
			ToolInfo tool2Info = new PladipusToolInfo();
			ImmutableSet.Builder<InputParameter> bld2 = new Builder<InputParameter>();
			bld2.add(paramNonMand);
			bld2.add(paramMandWithDefault);
			tool2Info.setInputParams(bld2.build());
			builder.put(TOOL2, tool2Info);
		}
		return builder.build();
	}
	
	private List<Default> getDefaults() {
		Default def1 = new Default();
		def1.setId(DEF1_ID);
		def1.setName(DEF1);
		return Arrays.asList(def1);
	}
	
	private void initMocks(boolean validDefaults, boolean validParams, boolean validTools, boolean mand) throws PladipusReportableException {
		if (validDefaults) {
			Mockito.when(defaultService.getDefaultsForUser(USER1)).thenReturn(getDefaults());
		} else {
			Mockito.when(defaultService.getDefaultsForUser(USER1)).thenReturn(new ArrayList<Default>());
		}
		
		Mockito.when(pladipusToolInfoProvider.getAllToolInfo()).thenReturn(getTools(validParams, validTools, mand));
	}
	
	private void verifyWorkflowUpdates() {
		checkMandatoryParameters();
		checkDefaults();
		checkGlobals();
		checkStepDeps();
	}
	
	private void checkMandatoryParameters() {
		// Mandatory parameter PARAM2 has default.  All steps should have this param in their list.
		// Step1, specifies null, should still have no value
		// Step2, specifies value, should still have that value
		// Step3, parameter not set, should have default value
		Set<String> step1 = workflow.getSteps().get(STEP1).getStepParameters().get(PARAM2);
		assertNotNull(step1);
		assertEquals(0, step1.size());
		Set<String> step2 = workflow.getSteps().get(STEP2).getStepParameters().get(PARAM2);
		assertNotNull(step2);
		assertEquals(1, step2.size());
		assertFalse(step2.contains(PARAM_DEF));
		Set<String> step3 = workflow.getSteps().get(STEP3).getStepParameters().get(PARAM2);
		assertNotNull(step3);
		assertEquals(1, step3.size());
		assertTrue(step3.contains(PARAM_DEF));
	}
	
	private void checkDefaults() {
		// Expect default 1 to have been added to map
		Map<String, String> defaults = workflow.getSubstitutions();
		assertNotNull(defaults);
		assertEquals(1, defaults.size());
		String def = defaults.get("{$DEFAULT." + DEF1 + "}");
		assertNotNull(def);
		assertTrue(def.equals("{$DEFAULT." + DEF1_ID + "}"));
	}
	
	private void checkGlobals() {
		// global 1 should have its original value, global 2 should have been added from step2 with no value
		Map<String, Set<String>> globals = workflow.getGlobalParameters();
		assertNotNull(globals);
		assertEquals(2, globals.size());
		assertNotNull(globals.get(GLOBAL1));
		assertNotNull(globals.get(GLOBAL2));
		assertEquals(1, globals.get(GLOBAL1).size());
		assertEquals(0, globals.get(GLOBAL2).size());
		assertTrue(globals.get(GLOBAL1).contains(GLOBALVALUE1));
	}
	
	private void checkStepDeps() {
		// Step 1 dep on 2 and 3.  Step 2 dep on 3.  Step 3 has no deps
		Set<String> step1 = workflow.getSteps().get(STEP1).getDependencies();
		Set<String> step2 = workflow.getSteps().get(STEP2).getDependencies();
		Set<String> step3 = workflow.getSteps().get(STEP3).getDependencies();
		assertNotNull(step1);
		assertNotNull(step2);
		assertNotNull(step3);
		assertEquals(2, step1.size());
		assertEquals(1, step2.size());
		assertEquals(0, step3.size());
		assertTrue(step1.contains(STEP2) && step1.contains(STEP3));
		assertTrue(step2.contains(STEP3));
	}
}
