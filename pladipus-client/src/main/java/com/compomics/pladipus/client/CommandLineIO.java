package com.compomics.pladipus.client;

import org.apache.commons.cli.Options;

public interface CommandLineIO {
	String getPassword();
	void printOutput(String output);
	void printHelp(Options helpOpts, String errorMsg);
	void printAlert(String alert);
}
