package com.compomics.pladipus.tools.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import com.compomics.pladipus.tools.core.ToolFactory;
import com.compomics.pladipus.tools.core.ToolInfoProvider;
import com.compomics.pladipus.tools.core.ToolScanner;
import com.compomics.pladipus.tools.core.impl.PladipusToolFactory;
import com.compomics.pladipus.tools.core.impl.PladipusToolInfoProvider;
import com.compomics.pladipus.tools.core.impl.PladipusToolScanner;

/**
 * Spring bean definitions for tool package
 */
@Configuration
public class ToolsConfiguration {
	
	/**
	 * Location of tool classes.
	 * TODO Keep this hardcoded here?  Allow different/multiple locations in properties file?
	 */
	@Bean
	public String pladipusScanPackage() {
		return "com.compomics.pladipus.tools";
	}
	
	@Bean
	public ToolScanner pladipusToolScanner() {
	   return new PladipusToolScanner();
	}
	
	@Bean
	public ToolInfoProvider pladipusToolInfoProvider() {
		return new PladipusToolInfoProvider();
	}
	
	@Lazy
	@Bean
	public ToolFactory pladipusToolFactory() {
		return new PladipusToolFactory();
	}
}
