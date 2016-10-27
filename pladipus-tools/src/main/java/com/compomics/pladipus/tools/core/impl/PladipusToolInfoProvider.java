package com.compomics.pladipus.tools.core.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;

import com.compomics.pladipus.model.exceptions.PladipusMessages;
import com.compomics.pladipus.model.exceptions.PladipusReportableException;
import com.compomics.pladipus.tools.annotations.PladipusTool;
import com.compomics.pladipus.tools.core.Tool;
import com.compomics.pladipus.tools.core.ToolInfo;
import com.compomics.pladipus.tools.core.ToolInfoProvider;
import com.compomics.pladipus.tools.core.ToolScanner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMap;

public class PladipusToolInfoProvider implements ToolInfoProvider {
	
	@Autowired
	private ToolScanner pladipusToolScanner;
	
	@Autowired
	private PladipusMessages exceptionMessages;
	
	static final Logger LOGGER = LoggerFactory.getLogger(PladipusToolInfoProvider.class);

	private ImmutableMap<String, ToolInfo> allToolInfo;
	
    private void findAllToolInfo() throws PladipusReportableException {
		ImmutableSortedMap.Builder<String, ToolInfo> builder = ImmutableSortedMap.naturalOrder();
		ImmutableSet<BeanDefinition> beanDefs = pladipusToolScanner.getTools();
		for (BeanDefinition beanDef: beanDefs) {
			try {
				Class<?> beanClass = Class.forName(beanDef.getBeanClassName());
				PladipusToolInfo info = new PladipusToolInfo();
				info.setInputParams(((Tool) beanClass.newInstance()).getAllToolInputParameters());
				builder.put(beanClass.getAnnotation(PladipusTool.class).displayName(), info);
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				LOGGER.warn("PladipusTool {} information could not be retrieved.  This tool will not be available.",
						     beanDef.getBeanClassName());
				LOGGER.debug(e.getStackTrace().toString());
			}
		}
		allToolInfo = builder.build();
		if (allToolInfo.isEmpty()) {
			throw new PladipusReportableException(exceptionMessages.getMessage("tool.noToolInfo"));
		}
    }
	
    @Override
	public ImmutableMap<String, ToolInfo> getAllToolInfo() throws PladipusReportableException {
		if (allToolInfo == null || allToolInfo.isEmpty()) {
			findAllToolInfo();
		}
		return allToolInfo;
	}
}
