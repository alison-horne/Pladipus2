package com.compomics.pladipus.test.tools.tests;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Before;
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

import com.compomics.pladipus.shared.PladipusReportableException;
import com.compomics.pladipus.test.tools.config.TestToolsConfiguration;
import com.compomics.pladipus.tools.config.ToolsConfiguration;
import com.compomics.pladipus.tools.core.ToolInfo;
import com.compomics.pladipus.tools.core.ToolInfoProvider;
import com.google.common.collect.ImmutableMap;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={ToolsConfiguration.class, TestToolsConfiguration.class}, loader=AnnotationConfigContextLoader.class)
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class})
public class ToolInfoTest {
	
	@Autowired
	private ToolInfoProvider pladipusToolInfoProvider;
	private ImmutableMap<String, ToolInfo> toolInfo;
	
	@Before
	public void getToolInfo() throws PladipusReportableException {
		if (toolInfo == null || toolInfo.isEmpty()) {
			toolInfo = pladipusToolInfoProvider.getAllToolInfo();
		}
	}
	
	@Test
	public void testToolInfoReturned() {
		assertEquals(toolInfo.size(), 4);
	}
	
	@Test
	public void testToolNames() {
		assertTrue(toolInfo.containsKey("One"));
		assertTrue(toolInfo.containsKey("Two"));
		assertTrue(toolInfo.containsKey("Three"));
		assertTrue(toolInfo.containsKey("Four"));
	}
	
	@Test
	public void testToolsInAlphabeticalOrder() {
		Iterator<String> iter = toolInfo.keySet().iterator();
		String previous = "";
		String next;
		while (iter.hasNext()) {
			next = iter.next();
			assertTrue(previous.compareTo(next) < 0);
			previous = next;
		}
	}
	
	@Test
	public void testNullParamsForToolTwo() {
		ToolInfo twoInfo = toolInfo.get("Two");
		assertNotNull(twoInfo);
		assertNull(twoInfo.getInputParams());
	}
	
	@Test
	public void testParamsReturnedForToolOne() {
		ToolInfo oneInfo = toolInfo.get("One");
		assertNotNull(oneInfo);
		assertEquals(oneInfo.getInputParams().size(), 6);
	}
}
