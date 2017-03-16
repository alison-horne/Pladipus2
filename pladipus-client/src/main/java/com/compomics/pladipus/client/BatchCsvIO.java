package com.compomics.pladipus.client;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;

import com.compomics.pladipus.shared.PladipusMessages;
import com.google.common.io.Files;

public class BatchCsvIO {
	
	@Autowired
	private PladipusMessages exceptionMessages;
	
	@Autowired
	private CommandLineIO cmdLineIO;
	
	public void checkFileValid(String filepath, boolean force) {
		checkFileName(filepath);
		checkExistingFile(filepath, force);
	}
	
	private void checkFileName(String filepath) {
		boolean valid = false;
		if ((filepath != null) && !filepath.isEmpty()) {			
			String ext = Files.getFileExtension(filepath);
			if (ext.isEmpty() || ext.equalsIgnoreCase("csv")) {
				valid = true;
			}
		}
		
		if (!valid) {
			cmdLineIO.printError(exceptionMessages.getMessage("batch.invalidFilename"));
		}
	}
	
	private void checkExistingFile(String filepath, boolean force) {
		File file = new File(filepath);
		if (file.exists()) {
			if (file.isDirectory() || !file.canWrite()) {
				cmdLineIO.printError(exceptionMessages.getMessage("batch.invalidFilename"));
			}
			if (!force) {
				cmdLineIO.printError(exceptionMessages.getMessage("batch.replaceFile"));
			}
		}
	}
	
	public void writeHeaderFile(String filepath, List<String> headers) {
        FileWriter writer = null;
        CSVPrinter printer = null;       
        try {
        	writer = new FileWriter(filepath);
        	printer = new CSVPrinter(writer, CSVFormat.EXCEL);
        	printer.printRecord(headers);
        } catch (IOException e) {
        	cmdLineIO.printError(exceptionMessages.getMessage("batch.fileWriteError", e.getMessage()));
        } finally {
        	try {
        		writer.flush();
        		writer.close();
        		printer.close();
        	} catch (IOException e) {
            	cmdLineIO.printError(exceptionMessages.getMessage("batch.fileWriteError", e.getMessage()));
        	}
        }
	}
	
	public String getFileName(String filepath) {
		return Files.getNameWithoutExtension(filepath);
	}
}
