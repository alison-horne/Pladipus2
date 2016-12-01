package com.compomics.pladipus.base.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

import com.compomics.pladipus.base.ToolControl;
import com.compomics.pladipus.base.UserControl;
import com.compomics.pladipus.base.impl.PladipusToolControl;
import com.compomics.pladipus.base.impl.UserControlImpl;
import com.compomics.pladipus.model.config.ModelConfiguration;
import com.compomics.pladipus.repository.config.RepositoryConfiguration;
import com.compomics.pladipus.tools.config.ToolsConfiguration;

@Configuration
@Import({ToolsConfiguration.class, ModelConfiguration.class, RepositoryConfiguration.class})
public class BaseConfiguration {

	@Bean 
	public ToolControl pladipusToolControl() {
		return new PladipusToolControl();
	}
	
	@Lazy
	@Bean
	public UserControl userControl() {
		return new UserControlImpl();
	}
}
