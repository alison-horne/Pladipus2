package com.compomics.pladipus.base.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

import com.compomics.pladipus.base.BatchControl;
import com.compomics.pladipus.base.DefaultsControl;
import com.compomics.pladipus.base.QueueControl;
import com.compomics.pladipus.base.ToolControl;
import com.compomics.pladipus.base.UserControl;
import com.compomics.pladipus.base.WorkerControl;
import com.compomics.pladipus.base.WorkflowControl;
import com.compomics.pladipus.base.impl.BatchControlImpl;
import com.compomics.pladipus.base.impl.DefaultsControlImpl;
import com.compomics.pladipus.base.impl.PladipusToolControl;
import com.compomics.pladipus.base.impl.QueueControlImpl;
import com.compomics.pladipus.base.impl.UserControlImpl;
import com.compomics.pladipus.base.impl.WorkerControlImpl;
import com.compomics.pladipus.base.impl.WorkflowControlImpl;
import com.compomics.pladipus.model.persist.Workflow;
import com.compomics.pladipus.repository.config.RepositoryConfiguration;
import com.compomics.pladipus.shared.config.SharedConfiguration;
import com.compomics.pladipus.base.helper.impl.WorkflowValidator;
import com.compomics.pladipus.base.helper.impl.WorkflowXMLHelper;
import com.compomics.pladipus.base.helper.ValidationChecker;
import com.compomics.pladipus.base.helper.XMLHelper;
import com.compomics.pladipus.tools.config.ToolsConfiguration;

@Configuration
@Import({ToolsConfiguration.class, SharedConfiguration.class, RepositoryConfiguration.class})
public class BaseConfiguration {

	@Bean 
	public ToolControl toolControl() {
		return new PladipusToolControl();
	}
	
	@Lazy
	@Bean
	public UserControl userControl() {
		return new UserControlImpl();
	}
	
	@Lazy
	@Bean
	public WorkflowControl workflowControl() {
		return new WorkflowControlImpl();
	}
	
	@Lazy
	@Bean
	public BatchControl batchControl() {
		return new BatchControlImpl();
	}
	
	@Lazy
	@Bean
	public DefaultsControl defaultsControl() {
		return new DefaultsControlImpl();
	}
	
	@Lazy
	@Bean
	public QueueControl queueControl() {
		return new QueueControlImpl();
	}
	
	@Lazy
	@Bean
	public WorkerControl workerControl() {
		return new WorkerControlImpl();
	}
		
	@Bean
	public XMLHelper<Workflow> workflowXMLHelper() {
		return new WorkflowXMLHelper();
	}
	
	@Bean
	public ValidationChecker<Workflow> workflowValidator() {
		return new WorkflowValidator();
	}
}
