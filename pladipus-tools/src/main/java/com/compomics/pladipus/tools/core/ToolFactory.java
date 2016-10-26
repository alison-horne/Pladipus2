package com.compomics.pladipus.tools.core;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import com.compomics.pladipus.model.exceptions.PladipusReportableException;

/**
 * Factory to register and provide tool beans.
 */
public abstract class ToolFactory extends DefaultListableBeanFactory implements InitializingBean {

	/**
	 * Registers the available tools.
	 */
	public abstract void registerToolBeans() throws PladipusReportableException;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		registerToolBeans();		
	}
}
