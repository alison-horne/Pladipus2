package com.compomics.pladipus.client.controllers;

import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import com.compomics.pladipus.shared.PladipusReportableException;

public class WorkflowController {
//	public String documentToString(Document document) throws PladipusReportableException {   
//        try {
//            Transformer transformer = TransformerFactory.newInstance().newTransformer();
//            StringWriter writer = new StringWriter();
//            transformer.transform(new DOMSource(document), new StreamResult(writer));
//            return writer.getBuffer().toString();
//        } catch (TransformerException e) {
//            throw new PladipusReportableException(exceptionMessages.getMessage("template.workflowError"));
//        }
//    }
}
