package com.compomics.pladipus.test.tools.tests;

import static org.junit.Assert.*;

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

import com.compomics.pladipus.test.tools.config.TestToolsConfiguration;
import com.compomics.pladipus.tools.config.ToolsConfiguration;
import com.compomics.pladipus.tools.core.Tool;
import com.compomics.pladipus.tools.core.ToolFactory;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={ToolsConfiguration.class, TestToolsConfiguration.class}, loader=AnnotationConfigContextLoader.class)
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class})
public class ToolFactoryTest {

	@Autowired
	private ToolFactory pladipusToolFactory;
	
	@Test
	public void testBeanDefinitionCount() {
		assertEquals(pladipusToolFactory.getBeanDefinitionCount(), 4);
	}
	
	@Test
	public void testBeansRegistered() {
		String[] toolNames = pladipusToolFactory.getBeanNamesForType(Tool.class);
		for (int i = 0; i < toolNames.length; i++) {
			assertTrue(toolNames[i].equals("One") || toolNames[i].equals("Two") ||
					   toolNames[i].equals("Three") || toolNames[i].equals("Four"));
		}
	}
}
