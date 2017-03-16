package com.compomics.pladipus.client.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

import com.compomics.pladipus.client.CommandLineIO;
import com.compomics.pladipus.client.queue.MessageTask;

@Configuration
public class MockClientConfiguration {
	
	@Bean
	public CommandLineIO cmdLineIO() {
		return Mockito.mock(CommandLineIO.class);
	}
	
	@Bean
	@Scope(value = "prototype")
	@Lazy
	public MessageTask messageTask(String text) {
		return new MockMessageTask(text);
	}
}
