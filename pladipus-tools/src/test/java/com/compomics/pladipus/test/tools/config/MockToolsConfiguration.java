package com.compomics.pladipus.test.tools.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.compomics.pladipus.tools.core.ToolInfoProvider;

@Configuration
public class MockToolsConfiguration {
	
	@Bean
	public ToolInfoProvider pladipusToolInfoProvider() {
		return Mockito.mock(ToolInfoProvider.class);
	}
}
