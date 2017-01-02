package com.compomics.pladipus.repository.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.compomics.pladipus.repository.service.UserService;
import com.compomics.pladipus.repository.service.WorkflowService;

@Configuration
public class MockRepositoryConfiguration {
	
	@Bean
	public UserService userService() {
		return Mockito.mock(UserService.class);
	}

	@Bean
	public WorkflowService workflowService() {
		return Mockito.mock(WorkflowService.class);
	}
}
