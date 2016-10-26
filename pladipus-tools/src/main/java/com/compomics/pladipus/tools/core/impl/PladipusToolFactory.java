package com.compomics.pladipus.tools.core.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;

import com.compomics.pladipus.model.exceptions.PladipusReportableException;
import com.compomics.pladipus.tools.annotations.PladipusTool;
import com.compomics.pladipus.tools.core.ToolFactory;
import com.compomics.pladipus.tools.core.ToolScanner;

public class PladipusToolFactory extends ToolFactory {
	
	@Autowired
	private ToolScanner pladipusToolScanner;
	
	static final Logger LOGGER = LoggerFactory.getLogger(PladipusToolFactory.class);

	@Override
	public void registerToolBeans() throws PladipusReportableException {
		for (BeanDefinition beanDef: pladipusToolScanner.getTools()) {
			beanDef.setLazyInit(true);
			beanDef.setScope(SCOPE_PROTOTYPE);
			try {
				String displayName = Class.forName(beanDef.getBeanClassName()).getAnnotation(PladipusTool.class).displayName();
				registerBeanDefinition(displayName, beanDef);
			} catch (ClassNotFoundException e) {
				LOGGER.warn("PladipusTool class {} can not be located.  This tool will not be available.",
					     e.getMessage());
			} catch (BeanDefinitionStoreException e) {
				LOGGER.warn("PladipusTool class {} bean registration failed.  This tool will not be available.",
						 beanDef.getBeanClassName());
			}
			
			if (getBeanDefinitionCount() == 0) {
				throw new PladipusReportableException("Registration of PladipusTool beans failed.  See logs for more details.");
			}
		}		
	}
}
