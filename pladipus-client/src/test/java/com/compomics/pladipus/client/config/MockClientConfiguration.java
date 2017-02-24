package com.compomics.pladipus.client.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.compomics.pladipus.client.CliTaskProcessor;
import com.compomics.pladipus.client.CommandLineIO;

@Configuration
public class MockClientConfiguration {
	
	@Bean
	public CommandLineIO cmdLineIO() {
		return Mockito.mock(CommandLineIO.class);
	}
	
	@Bean
	public CliTaskProcessor cliTaskProcessor() {
		return Mockito.mock(CliTaskProcessor.class);
	}
}
