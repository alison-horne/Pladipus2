package com.compomics.pladipus.model.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.compomics.pladipus.model.exceptions.PladipusLogExceptionMessages;


/**
 * TODO Move this out of model
 *
 */
@Configuration
public class ModelConfiguration {

	@Bean 
	public PladipusLogExceptionMessages logExceptionMessages() {
		return new PladipusLogExceptionMessages();
	}
}