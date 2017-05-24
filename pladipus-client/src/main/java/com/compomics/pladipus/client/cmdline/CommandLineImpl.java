package com.compomics.pladipus.client.cmdline;

import java.io.Console;
import java.util.Comparator;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.springframework.beans.factory.annotation.Autowired;

public class CommandLineImpl implements CommandLineIO {
		
	@Autowired
	protected ResourceBundle cmdLine;
	
	private static final String DEFAULT_ERROR = "Error.";
	
	private static final String[] USAGE_TEXTS 
    	= new String[]{"main", "help", "user", "template", "batch", "process", "rerun", "status", "generate", "default", "abort"};
	
	@Override
	public String getPassword() {
		String pwd = null;
		Console console = System.console();
		if (console != null) {
			pwd = new String(console.readPassword(cmdLine.getString("password.prompt")));
		}
		return pwd;
	}

	@Override
	public void printOutput(String output) {
		System.out.println(output);
	}
	
	@Override
	public void printError(String error) {
		try {
			System.out.println(cmdLine.getString("error.string"));
		} catch (MissingResourceException e) {
			System.out.println(DEFAULT_ERROR);
		}
		printOutput(error);
	}

	@Override
	public void printHelp(Options helpOpts, String errorMsg) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setOptionComparator(new Comparator<Option>() {
			@Override
			public int compare(Option a, Option b) {
				// Just return help options in order they have been supplied.
				return 0;
			}
		});
        if (errorMsg != null) {
        	printOutput(errorMsg + "\n");
        }
        formatter.printHelp(constructHelpText(), cmdLine.getString("usage.header"), helpOpts, cmdLine.getString("usage.footer"), false);
	}
	
	private String constructHelpText() {
		StringBuilder builder = new StringBuilder();
		for (String text: USAGE_TEXTS) {
			builder.append(cmdLine.getString("usage." + text));
			builder.append("\n");
		}
		return builder.toString();
	}
}
