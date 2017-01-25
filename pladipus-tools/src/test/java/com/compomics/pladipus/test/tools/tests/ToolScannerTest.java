package com.compomics.pladipus.test.tools.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
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
import com.compomics.pladipus.tools.core.impl.PladipusToolScanner;
import com.google.common.collect.ImmutableSet;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={ToolsConfiguration.class, TestToolsConfiguration.class}, loader=AnnotationConfigContextLoader.class)
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class})
public class ToolScannerTest {
	
	@Autowired
	private PladipusToolScanner pladipusToolScanner;
	private ImmutableSet<BeanDefinition> toolsFound;
	
	@Before
	public void getToolsFound() throws PladipusReportableException {
		if (toolsFound == null || toolsFound.isEmpty()) {
			toolsFound = pladipusToolScanner.getTools();
		}
	}
	
	@Test
	public void testAnnotatedToolsFound() {
		assertEquals(toolsFound.size(), 4);
		for (BeanDefinition beanDef: toolsFound) {
			assertTrue(beanDef.getBeanClassName().contains("TestTool"));
		}
	}
	
	@Test
	public void testUnannotatedToolsNotFound() {
		for (BeanDefinition beanDef: toolsFound) {
			assertFalse(beanDef.getBeanClassName().contains("UnannotatedTool"));
		}
	}
	
	@Test
	public void testAnnotatedButNotExtendingBaseNotFound() {
		for (BeanDefinition beanDef: toolsFound) {
			assertFalse(beanDef.getBeanClassName().contains("NonExtendingTool"));
		}
	}
}
