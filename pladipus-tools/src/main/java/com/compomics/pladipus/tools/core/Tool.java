package com.compomics.pladipus.tools.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.compomics.pladipus.model.parameters.InputParameter;
import com.google.common.collect.ImmutableSet;

/**
 * All tools must extend this class.
 * Performs setup and running of a tool.
 *
 * TODO - Do we need to specify output types for a tool, for use as input parameters in other tools?
 * 		- Are we going to perform validation here?  Check able to read/write to file locations in parameters
 * 		- Pre-run setup, e.g. unzip files if necessary.  Tool-specific setup can override.
 * 		- Progress reporting from here?
 */
public abstract class Tool implements Callable<Map<String, String>> {
	
	public static final String ERROR = "Error string";
	public static final String PARAM_START = "{{";
	public static final String PARAM_END = "}}";
	private String errorString;
	private Map<String, String> parameters;
	
	public Tool() {}
	public Tool(Map<String, String> parameters) {
		this.parameters = parameters;
	}
	protected String getParameter(String paramName) {
		if (parameters != null && !parameters.isEmpty()) return parameters.get(paramName);
		return null;
	}
	
	public abstract ImmutableSet<InputParameter> getAllToolInputParameters();
	public abstract String getJar();
	public abstract ImmutableSet<String> getErrorStrings();
	public abstract Map<String, String> getOutputs();
	public int getDefaultTimeoutSeconds() { return -1;}
	public Map<String, String> call() {
		return runTool();
	}
	
	public String getExecCommand() {
		return "java -jar " + getJar() + paramsToString();
	}
	
	private boolean containsError(String output) {
		ImmutableSet<String> errorList = getErrorStrings();
		if (errorList != null) {
			for (String error: errorList) {
				if (output.toUpperCase().contains(error.toUpperCase())) return true;
			}
		}
		return false;
	}
	
	private Map<String, String> getReturnOutput() {
		Map<String, String> outputMap = new HashMap<String, String>();
		if (errorString != null) {			
			outputMap.put(ERROR, errorString);
		} else {
			Map<String, String> possibleOutputs = getOutputs();
			for (Entry<String, String> output: possibleOutputs.entrySet()) {
				String finalOutput = substituteParameters(output.getValue());
				if (Files.exists(Paths.get(finalOutput))) {
					outputMap.put(output.getKey(), finalOutput);
				}
			}
		}
		return outputMap;
	}
	
	private String substituteParameters(String toSub) {
		for (String key: parameters.keySet()) {
			toSub = toSub.replaceAll("(?i)"+Pattern.quote(PARAM_START + key + PARAM_END), Matcher.quoteReplacement(parameters.get(key)));
		}
		return toSub;
	}
	
	private String paramsToString() {
		// TODO what if parameter takes no value, a boolean flag
		StringBuilder builder = new StringBuilder();
		if (parameters != null && !parameters.isEmpty()) {
			for (String param: parameters.keySet()) {
				builder.append(" -");
				if (param.length() > 1) builder.append("-");
				builder.append(param);
				builder.append(" \"");
				builder.append(parameters.get(param));
				builder.append("\"");
			}
		}
		return builder.toString();
	}

	public Map<String, String> runTool() {
		//TODO here check if output dir is unique.  Run identifier is in parameters to use to make unique if needed
		Process pr = null;
		try {
			pr = Runtime.getRuntime().exec(getExecCommand());
			ReadOutput outputReader = new ReadOutput(pr);
			ReadError readError = new ReadError(pr);
			outputReader.start();
			readError.start();
			pr.waitFor();
			return getReturnOutput();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (InterruptedException e) {
			pr.destroyForcibly();
			e.printStackTrace();
			return null;
		}
		
	}
	
	class ReadOutput extends Thread {
		Process proc;
		public ReadOutput(Process proc) {
			this.proc = proc;
		}
		public void run() {
            BufferedReader input = new BufferedReader(new InputStreamReader(proc.getInputStream()));       
            String line=null;
            try {
				while((proc.isAlive()) && (line=input.readLine()) != null) {
				    if (containsError(line)) {
				    	errorString = line;
				    	proc.destroy();
				    }
				}
			} catch (IOException e) {
				errorString = e.getMessage();
				proc.destroy();
			} finally {
				try {
					if (input != null) input.close();
				} catch (IOException e) {}
			}
		}
	}
	
	class ReadError extends Thread {
		Process proc;
		public ReadError(Process proc) {
			this.proc = proc;
		}
		public void run() {
            BufferedReader input = new BufferedReader(new InputStreamReader(proc.getErrorStream()));            
            String line=null;
            try {
				while((proc.isAlive()) && (line=input.readLine()) != null) {
				   	errorString = line;
				   	proc.destroy();
				}
			} catch (IOException e) {
				errorString = e.getMessage();
				proc.destroy();
			} finally {
				try {
					if (input != null) input.close();
				} catch (IOException e) {}
			}
		}
	}
}
