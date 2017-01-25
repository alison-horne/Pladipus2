package com.compomics.pladipus.tools.core;

import org.springframework.beans.factory.config.BeanDefinition;

import com.compomics.pladipus.shared.PladipusReportableException;
import com.google.common.collect.ImmutableSet;

/**
 * Finds all available tools.
 */
public interface ToolScanner {
	
	/**
	 * @return Immutable set of all tool BeanDefinitions
	 * @throws PladipusReportableException if no tools found
	 */
	public ImmutableSet<BeanDefinition> getTools() throws PladipusReportableException;

}
