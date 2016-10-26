package com.compomics.pladipus.tools.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Custom annotation to identify Pladipus tools in Spring.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PladipusTool {
	
	/**
	 * Friendly name for tool.  This is mandatory, and will be used in the workflow XML to identify the tool
	 * to be run for a step.  It will also be displayed in the GUI.
	 */
	String displayName();

}
