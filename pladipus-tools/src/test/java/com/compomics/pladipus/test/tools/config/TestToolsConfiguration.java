package com.compomics.pladipus.test.tools.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestToolsConfiguration {
	
	/**
	 * Make sure that tests look for tools under test package
	 */
	@Bean 
	public String pladipusScanPackage() {
		return "com.compomics.pladipus.test.tools";
	}
}
