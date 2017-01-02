package com.compomics.pladipus.base.helper.impl;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.compomics.pladipus.base.helper.XMLHelper;
import com.compomics.pladipus.model.core.Parameter;
import com.compomics.pladipus.model.core.Step;
import com.compomics.pladipus.model.core.Workflow;
import com.compomics.pladipus.model.exceptions.PladipusMessages;
import com.compomics.pladipus.model.exceptions.PladipusReportableException;

public class WorkflowXMLHelper implements XMLHelper<Workflow> {
	
	@Value("classpath:template.xsd")
	private Resource templateXsd;
	
	@Autowired
	private PladipusMessages exceptionMessages;
	
	// XML tag names
	private static final String NAME = "name";
	private static final String GLOBAL = "global";
	private static final String ID = "id";
	private static final String STEP = "step";
	private static final String PARAM = "parameter";
	private static final String VALUE = "value";
	
	@Override
	public void validateDocument(Document doc) throws PladipusReportableException {
		try {
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Source schemaFile = new StreamSource(templateXsd.getFile());
			Schema schema = factory.newSchema(schemaFile);
			Validator validator = schema.newValidator();
			validator.validate(new DOMSource(doc));
		} catch (SAXException | IOException e) {
			throw new PladipusReportableException(exceptionMessages.getMessage("template.invalidXml", e.getMessage()));
		} 	
	}
	
	@Override
	public Workflow parseDocument(Document document) throws PladipusReportableException {
		document.normalize();
		Workflow workflow = new Workflow();
		workflow.setWorkflowName(document.getDocumentElement().getAttribute(NAME));
		Element global = (Element) document.getDocumentElement().getElementsByTagName(GLOBAL).item(0);
		workflow.addGlobalParameters(parseParameters(global));
		NodeList steplist = document.getDocumentElement().getElementsByTagName(STEP);
		
		for (int i = 0; i < steplist.getLength(); i++) {
			workflow.addStep(parseStep((Element) steplist.item(i)));
		}
		workflow.setTemplate(documentToString(document));
		
		return workflow;
	}
	
	private Step parseStep(Element stepElem) {
		Step step = new Step();
		step.setStepIdentifier(stepElem.getElementsByTagName(ID).item(0).getTextContent());
		step.setToolType(stepElem.getElementsByTagName(NAME).item(0).getTextContent());
		step.addParameters(parseParameters(stepElem));
		return step;
	}
	
	private List<Parameter> parseParameters(Element element) {
		List<Parameter> parameters = new ArrayList<Parameter>();
		if (element != null) {
			NodeList params = element.getElementsByTagName(PARAM);
			for (int i = 0; i < params.getLength(); i++) {
				parameters.add(parseParameter((Element) params.item(i)));
			}
		}		
		return parameters;
	}
	
	private Parameter parseParameter(Element parameter) {
		Parameter param = new Parameter(parameter.getElementsByTagName(NAME).item(0).getTextContent());
		NodeList values = parameter.getElementsByTagName(VALUE);
		for (int j=0; j < values.getLength(); j++) {
			param.addValue(values.item(j).getTextContent());
		}
		return param;
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
	public Document stringToDocument(String str) throws PladipusReportableException {
        try  
        { 
        	return DocumentBuilderFactory.newInstance().newDocumentBuilder()
        								 .parse(new InputSource(new StringReader(str)));
        } catch (Exception e) {
          	throw new PladipusReportableException(exceptionMessages.getMessage("template.workflowReadError"));
        } 
    }
    
    @Override
	public Document fileToDocument(String filePath) throws PladipusReportableException {
		try {
			return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(filePath));
		} catch (Exception e) {
			throw new PladipusReportableException(exceptionMessages.getMessage("template.workflowError"));
		}
	}
}
