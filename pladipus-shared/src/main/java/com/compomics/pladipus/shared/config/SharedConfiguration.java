package com.compomics.pladipus.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.compomics.pladipus.model.persist.Workflow;
import com.compomics.pladipus.shared.PladipusMessages;
import com.compomics.pladipus.shared.WorkflowXMLHelper;
import com.compomics.pladipus.shared.XMLHelper;


@Configuration
public class SharedConfiguration {

	@Bean 
	public PladipusMessages exceptionMessages() {
		return new PladipusMessages("exception", DEFAULT_ERROR_MESSAGE);
	}
	
	@Bean
	public XMLHelper<Workflow> workflowXMLHelper() {
		return new WorkflowXMLHelper();
	}
	
	private static final String DEFAULT_ERROR_MESSAGE = "An error has occurred.  See logs for more details.";
}