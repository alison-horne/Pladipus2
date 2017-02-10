package com.compomics.pladipus.base.helper.impl;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.compomics.pladipus.base.helper.XMLHelper;
import com.compomics.pladipus.model.persist.Workflow;
import com.compomics.pladipus.shared.PladipusMessages;
import com.compomics.pladipus.shared.PladipusReportableException;

public class WorkflowXMLHelper implements XMLHelper<Workflow> {
	
	@Value("classpath:template.xsd")
	private Resource templateXsd;
	
	@Autowired
	private PladipusMessages exceptionMessages;
	
	@Override
	public Workflow parseDocument(Document document) throws PladipusReportableException {
		try {
			JAXBContext Jcontext = JAXBContext.newInstance(Workflow.class);
			Unmarshaller unmarshaller = Jcontext.createUnmarshaller();
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = factory.newSchema(new StreamSource(templateXsd.getInputStream()));
			unmarshaller.setSchema(schema);
			Workflow workflow = unmarshaller.unmarshal(document, Workflow.class).getValue();
			return workflow;
		} catch (UnmarshalException e) {
			throw new PladipusReportableException(exceptionMessages.getMessage("template.invalidXml", e.getCause().getMessage()));
		} catch (JAXBException | SAXException | IOException e) {
			throw new PladipusReportableException(exceptionMessages.getMessage("template.invalidXml", e.getMessage()));
		}
	}

	@Override
	public String documentToString(Document document) throws PladipusReportableException {       
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(writer));
            return writer.getBuffer().toString();
        } catch (TransformerException e) {
            throw new PladipusReportableException(exceptionMessages.getMessage("template.workflowError"));
        }
    }
    
    @Override
	public Document filepathToDocument(String filePath) throws PladipusReportableException {
		try {
			return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(filePath));
		} catch (Exception e) {
			throw new PladipusReportableException(exceptionMessages.getMessage("template.workflowError"));
		}
	}
}
