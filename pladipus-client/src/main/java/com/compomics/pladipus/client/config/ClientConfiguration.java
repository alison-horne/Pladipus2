package com.compomics.pladipus.client.config;

import java.util.ResourceBundle;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

import com.compomics.pladipus.base.config.BaseConfiguration;
import com.compomics.pladipus.client.CommandLineIO;
import com.compomics.pladipus.client.CommandLineImpl;
import com.compomics.pladipus.client.MainCLI;
import com.compomics.pladipus.client.MainGUI;

@Configuration
@Import(BaseConfiguration.class)
public class ClientConfiguration {
	
	@Lazy
	@Bean
	public ResourceBundle cmdLine() {
		return ResourceBundle.getBundle("cli_options");
	}
	
	@Lazy
	@Bean
	public MainCLI cli() {
		return new MainCLI();
	}
	
	@Lazy
	@Bean
	public MainGUI gui() {
		return new MainGUI();
	}
	
	@Bean
	public CommandLineIO cmdLineIO() {
		return new CommandLineImpl();
	}
}
