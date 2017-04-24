package com.compomics.pladipus.tools.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.Callable;

import com.compomics.pladipus.model.parameters.InputParameter;
import com.google.common.collect.ImmutableSet;

/**
 * All tools must extend this class.
 * Performs setup and running of a tool.
 *
 * TODO - Do we need to specify output types for a tool, for use as input parameters in other tools?
 * 		- Are we going to perform validation here?  Check able to read/write to file locations in parameters
 * 		- Pre-run setup, e.g. unzip files if necessary.  Tool-specific setup can override.
 * 		- Do actual running of tool from here, i.e. construct command line arguments
 * 		- Progress reporting from here?
 */
public abstract class Tool implements Callable<String> {
	
	private Map<String, String> parameters;
	private int TIMEOUT = 100000; // TODO get default timeout from properties file
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
	public abstract String getOutput(); // TODO make sure unique, not overwriting other output...before running tool, maybe add runID?  Check output exists before returning, else error response
	public int getDefaultTimeout() { return TIMEOUT; }
	public String call() {
		return runTool();
	}
	
	public String getExecCommand() {
		return "java -jar " + getJar() + paramsToString();
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

	public String runTool() { 
		Process pr = null;
		try {
			pr = Runtime.getRuntime().exec(getExecCommand());
			ReadOutput outputReader = new ReadOutput(pr);
			outputReader.start();
			pr.waitFor();
			return getOutput();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (InterruptedException e) {
			// Timeout.  TODO what if worker dies/killed mid-task?  How does controller know?
			pr.destroyForcibly();
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
            // TODO capture error stream
            
            String line=null;

            try {
				while((proc.isAlive()) && (line=input.readLine()) != null) {
				    //System.out.println(line);
					//TODO do something with the output
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}
}
