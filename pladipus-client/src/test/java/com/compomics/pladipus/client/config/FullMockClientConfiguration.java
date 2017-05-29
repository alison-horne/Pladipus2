package com.compomics.pladipus.client.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.compomics.pladipus.client.cmdline.CliTaskProcessor;

@Configuration
@Import(MockClientConfiguration.class)
public class FullMockClientConfiguration {

	@Bean
	public CliTaskProcessor cliTaskProcessor() {
		return Mockito.mock(CliTaskProcessor.class);
	}
}
