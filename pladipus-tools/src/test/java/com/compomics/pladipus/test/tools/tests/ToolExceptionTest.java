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

import com.compomics.pladipus.model.exceptions.PladipusReportableException;
import com.compomics.pladipus.test.tools.config.EmptyTestToolsConfiguration;
import com.compomics.pladipus.tools.config.ToolsConfiguration;
import com.compomics.pladipus.tools.core.impl.PladipusToolScanner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={ToolsConfiguration.class, EmptyTestToolsConfiguration.class}, loader=AnnotationConfigContextLoader.class)
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class})
public class ToolExceptionTest {
	
	@Autowired
	private PladipusToolScanner pladipusToolScanner;
	
	@Autowired
	private String pladipusScanPackage;
	
	@Test
	public void testExceptionThrownWhenNoTools() {
		try {
			pladipusToolScanner.getTools();
			fail("Empty tool package should have thrown exception");
		}
		catch (PladipusReportableException e) {
			assertTrue(e.getMessage().contains(pladipusScanPackage));
		}
	}
}
