package com.compomics.pladipus.base.test;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import com.compomics.pladipus.base.ToolControl;
import com.compomics.pladipus.base.config.BaseConfiguration;
import com.compomics.pladipus.model.exceptions.PladipusReportableException;
import com.compomics.pladipus.model.parameters.InputParameter;
import com.compomics.pladipus.test.tools.config.TestToolsConfiguration;
import com.compomics.pladipus.tools.core.ToolInfo;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * Tests for class which retrieves information about available tools for use in the GUI.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={BaseConfiguration.class, TestToolsConfiguration.class}, loader=AnnotationConfigContextLoader.class)
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class})
public class ToolControlTest {

	@Autowired
	private ToolControl toolControl;
	
	@Test
	public void testGetAllToolInfo() {
		try {
			ImmutableMap<String, ToolInfo> toolInfo = toolControl.getAllToolInfo();
			assertEquals(toolInfo.size(), 4);
			assertTrue(toolInfo.containsKey("One"));
			assertTrue(toolInfo.containsKey("Two"));
			assertTrue(toolInfo.containsKey("Three"));
			assertTrue(toolInfo.containsKey("Four"));
		} catch (PladipusReportableException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testToolsInAlphabeticalOrder() {
		try {
			ImmutableMap<String, ToolInfo> toolInfo = toolControl.getAllToolInfo();
			Iterator<String> iter = toolInfo.keySet().iterator();
			String previous = "";
			String next;
			while (iter.hasNext()) {
				next = iter.next();
				assertTrue(previous.compareTo(next) < 0);
				previous = next;
			}
		} catch (PladipusReportableException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetToolNames() {
		try {
			ImmutableSet<String> toolNames = toolControl.getToolNames();
			assertEquals(toolNames.size(), 4);
			assertTrue(toolNames.contains("One"));
			assertTrue(toolNames.contains("Two"));
			assertTrue(toolNames.contains("Three"));
			assertTrue(toolNames.contains("Four"));
		} catch (PladipusReportableException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testExceptionForBadToolname() {
		try {
			toolControl.getInfoForTool("Does not exist tool");
			fail("Should throw exception if trying to get information about tool which does not exist.");
		} catch (PladipusReportableException e) {
			assertTrue(e.getMessage().contains("Does not exist tool"));
		}
	}
	
	@Test
	public void testGetParamMapReturnsEmptyIfNone() {
		try {
			ImmutableMap<String, InputParameter> params = toolControl.getParameterMap("Four");
			assertNotNull(params);
			assertEquals(0, params.size());
		} catch (PladipusReportableException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetParamMapReturnsAll() {
		try {
			ImmutableMap<String, InputParameter> params = toolControl.getParameterMap("One");
			assertEquals(6, params.size());
			assertTrue(params.containsKey("input_one"));
			assertTrue(params.containsKey("input_no_default_mandatory"));
			assertTrue(params.containsKey("input_no_type"));
			assertTrue(params.containsKey("input_two"));
			assertTrue(params.containsKey("input_no_default"));
			assertTrue(params.containsKey("input_no_default_or_type"));
		} catch (PladipusReportableException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetMandatoryParameters() {
		try {
			ImmutableSet<InputParameter> mandatory = toolControl.getMandatoryParameters("One");
			assertEquals(mandatory.size(), 3);
			for (InputParameter param: mandatory) {
				assertTrue(param.getParamName().equals("input_one") ||
						   param.getParamName().equals("input_no_default_mandatory") ||
						   param.getParamName().equals("input_no_type"));
			}
		} catch (PladipusReportableException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetOptionalParameters() {
		try {
			ImmutableSet<InputParameter> optional = toolControl.getOptionalParameters("One");
			assertEquals(optional.size(), 3);
			for (InputParameter param: optional) {
				assertTrue(param.getParamName().equals("input_two") ||
						   param.getParamName().equals("input_no_default") ||
						   param.getParamName().equals("input_no_default_or_type"));
			}
		} catch (PladipusReportableException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetInfoReturnsWithNoInputParams() {
		try {
			ToolInfo twoInfo = toolControl.getInfoForTool("Two");
			assertNull(twoInfo.getInputParams());
		} catch (PladipusReportableException e) {
			fail(e.getMessage());
		}
	}
}
