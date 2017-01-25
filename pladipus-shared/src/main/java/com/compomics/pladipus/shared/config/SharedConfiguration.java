package com.compomics.pladipus.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.compomics.pladipus.shared.PladipusMessages;


@Configuration
public class SharedConfiguration {

	@Bean 
	public PladipusMessages exceptionMessages() {
		return new PladipusMessages("exception", DEFAULT_ERROR_MESSAGE);
	}
	
	private static final String DEFAULT_ERROR_MESSAGE = "An error has occurred.  See logs for more details.";
}