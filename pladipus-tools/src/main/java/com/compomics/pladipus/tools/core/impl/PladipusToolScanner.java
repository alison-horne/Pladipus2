package com.compomics.pladipus.tools.core.impl;

import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import com.compomics.pladipus.model.exceptions.PladipusReportableException;
import com.compomics.pladipus.tools.annotations.PladipusTool;
import com.compomics.pladipus.tools.core.Tool;
import com.compomics.pladipus.tools.core.ToolScanner;
import com.google.common.collect.ImmutableSet;

/**
 * Scans for all Tools annotated with @PladipusTool in Pladipus tools package.
 *
 */
public class PladipusToolScanner implements ToolScanner {
	
	@Autowired
	private String pladipusScanPackage;
	private ImmutableSet<BeanDefinition> toolBeanDefs;
	
	static final Logger LOGGER = LoggerFactory.getLogger(PladipusToolScanner.class);
	
	@Override
	public ImmutableSet<BeanDefinition> getTools() throws PladipusReportableException {
		if (toolBeanDefs == null || toolBeanDefs.isEmpty()) {
			findPladipusTools();
		}
		return toolBeanDefs;
	}
	 
    private void findPladipusTools() throws PladipusReportableException {
        ClassPathScanningCandidateComponentProvider provider = createToolScanner();
        Set<BeanDefinition> candidates = provider.findCandidateComponents(pladipusScanPackage);
        Iterator<BeanDefinition> iter = candidates.iterator();
        while (iter.hasNext()) {
        	try {
        		BeanDefinition candidate = iter.next();
				if (!Tool.class.isAssignableFrom(Class.forName(candidate.getBeanClassName()))) {
					LOGGER.warn("PladipusTool annotated class, {} found which does not extend Tool.  This tool will not be available.", 
							    candidate.getBeanClassName());
					iter.remove();
				}
			} catch (ClassNotFoundException e) {
				LOGGER.warn("PladipusTool class {} can not be located.  This tool will not be available.",
						     e.getMessage());
				iter.remove();
			}
    	}
  
        if (candidates.isEmpty()) {
        	// TODO Temporary error message.  Move to common properties, along with other things such as logging info, db setup...
            String NO_TOOLS_MESSAGE = "No tools were found in the package " + getPladipusScanPackage() + ". \n" +
            		"Please ensure that Pladipus tool classes are in the correct location, and restart the application.";
        	LOGGER.error(NO_TOOLS_MESSAGE);
        	throw new PladipusReportableException(NO_TOOLS_MESSAGE);
        }
        toolBeanDefs = ImmutableSet.copyOf(candidates);
    }
 
    private static ClassPathScanningCandidateComponentProvider createToolScanner() {

        ClassPathScanningCandidateComponentProvider provider
                = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(PladipusTool.class));
        return provider;
    }
    
    public void setPladipusScanPackage(String scanPackage) {
    	this.pladipusScanPackage = scanPackage;
    }
    
    public String getPladipusScanPackage() {
    	return this.pladipusScanPackage;
    }
}
