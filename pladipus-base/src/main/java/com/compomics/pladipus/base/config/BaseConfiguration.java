package com.compomics.pladipus.base.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.compomics.pladipus.base.ToolControl;
import com.compomics.pladipus.base.impl.PladipusToolControl;
import com.compomics.pladipus.model.config.ModelConfiguration;
import com.compomics.pladipus.tools.config.ToolsConfiguration;

@Configuration
@Import({ToolsConfiguration.class, ModelConfiguration.class})
public class BaseConfiguration {

	@Bean 
	public ToolControl pladipusToolControl() {
		return new PladipusToolControl();
	}
}
