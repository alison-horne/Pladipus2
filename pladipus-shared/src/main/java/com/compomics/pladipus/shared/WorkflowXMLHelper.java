package com.compomics.pladipus.shared;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.xml.sax.SAXException;

import com.compomics.pladipus.model.persist.Workflow;
import com.compomics.pladipus.shared.PladipusMessages;
import com.compomics.pladipus.shared.PladipusReportableException;

public class WorkflowXMLHelper implements XMLHelper<Workflow> {
	
	@Value("classpath:template.xsd")
	private Resource templateXsd;
	
	@Autowired
	private PladipusMessages exceptionMessages;
	private JAXBContext Jcontext;
	
	@Override
	public Workflow parseXml(String content) throws PladipusReportableException {
		if (content == null || content.isEmpty()) {
			throw new PladipusReportableException(exceptionMessages.getMessage("template.invalidXml", ""));
		}
		try {
			Unmarshaller unmarshaller = getContext().createUnmarshaller();
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = factory.newSchema(new StreamSource(templateXsd.getInputStream()));
			unmarshaller.setSchema(schema);
			Workflow workflow = (Workflow) unmarshaller.unmarshal(new StringReader(content));
			return workflow;
		} catch (UnmarshalException e) {
			throw new PladipusReportableException(exceptionMessages.getMessage("template.invalidXml", e.getCause().getMessage()));
		} catch (JAXBException | SAXException | IOException e) {
			throw new PladipusReportableException(exceptionMessages.getMessage("template.invalidXml", e.getMessage()));
		}
	}

	@Override
	public String objectToXML(Workflow workflow) throws PladipusReportableException {
		try {
			Marshaller marshaller = getContext().createMarshaller();
			StringWriter stringWriter = new StringWriter();
			marshaller.marshal(workflow, stringWriter);
			return stringWriter.toString();
		} catch (JAXBException e) {
			throw new PladipusReportableException(exceptionMessages.getMessage("template.invalidWorkflow", e.getMessage()));
		}
	}
	
	private JAXBContext getContext() throws PladipusReportableException {
		if (Jcontext == null) {
			try {
				Jcontext = JAXBContext.newInstance(Workflow.class);
			} catch (JAXBException e) {
				throw new PladipusReportableException(exceptionMessages.getMessage("template.jaxbInit", e.getMessage()));
			}
		}
		return Jcontext;
	}
}
