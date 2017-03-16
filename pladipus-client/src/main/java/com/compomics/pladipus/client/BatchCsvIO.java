package com.compomics.pladipus.client;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;

import com.compomics.pladipus.shared.PladipusMessages;
import com.compomics.pladipus.shared.PladipusReportableException;
import com.google.common.io.Files;

public class BatchCsvIO {
	
	@Autowired
	private PladipusMessages exceptionMessages;
	
	public void checkFileValid(String filepath, boolean force) throws PladipusReportableException {
		checkFileName(filepath);
		checkExistingFile(filepath, force);
	}
	
	private void checkFileName(String filepath) throws PladipusReportableException {
		boolean valid = false;
		if ((filepath != null) && !filepath.isEmpty()) {			
			String ext = Files.getFileExtension(filepath);
			if (ext.isEmpty() || ext.equalsIgnoreCase("csv")) {
				valid = true;
			}
		}
		
		if (!valid) {
			throw new PladipusReportableException(exceptionMessages.getMessage("batch.invalidFilename"));
		}
	}
	
	private void checkExistingFile(String filepath, boolean force) throws PladipusReportableException {
		File file = new File(filepath);
		if (file.exists()) {
			if (file.isDirectory() || !file.canWrite()) {
				throw new PladipusReportableException(exceptionMessages.getMessage("batch.invalidFilename"));
			}
			if (!force) {
				throw new PladipusReportableException(exceptionMessages.getMessage("batch.replaceFile"));
			}
		}
	}
	
	public void writeHeaderFile(String filepath, List<String> headers) throws PladipusReportableException {
        FileWriter writer = null;
        CSVPrinter printer = null;       
        try {
        	writer = new FileWriter(filepath);
        	printer = new CSVPrinter(writer, CSVFormat.EXCEL);
        	printer.printRecord(headers);
        } catch (IOException e) {
        	throw new PladipusReportableException(exceptionMessages.getMessage("batch.fileWriteError", e.getMessage()));
        } finally {
        	try {
        		writer.flush();
        		writer.close();
        		printer.close();
        	} catch (IOException e) {
            	throw new PladipusReportableException(exceptionMessages.getMessage("batch.fileWriteError", e.getMessage()));
        	}
        }
	}
	
	public String getFileName(String filepath) {
		return Files.getNameWithoutExtension(filepath);
	}
}
