package com.compomics.pladipus.test.tools.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmptyTestToolsConfiguration {
	
	/**
	 * Make tests look for tools in empty package
	 */
	@Bean 
	public String pladipusScanPackage() {
		return "com.compomics.pladipus.empty";
	}
}
