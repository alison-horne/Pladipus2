package com.compomics.pladipus.base.test.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.compomics.pladipus.base.BatchControl;
import com.compomics.pladipus.base.DefaultsControl;
import com.compomics.pladipus.base.QueueControl;
import com.compomics.pladipus.base.UserControl;
import com.compomics.pladipus.base.WorkflowControl;

@Configuration
public class MockBaseConfiguration {
	
	@Bean
	public UserControl userControl() {
		return Mockito.mock(UserControl.class);
	}
	
	@Bean
	public WorkflowControl workflowControl() {
		return Mockito.mock(WorkflowControl.class);
	}
	
	@Bean
	public BatchControl batchControl() {
		return Mockito.mock(BatchControl.class);
	}
	
	@Bean
	public DefaultsControl defaultsControl() {
		return Mockito.mock(DefaultsControl.class);
	}
	
	@Bean
	public QueueControl queueControl() {
		return Mockito.mock(QueueControl.class);
	}
}
