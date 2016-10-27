package com.compomics.pladipus.model.exceptions;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * TODO Move out of model
 *
 */
public class PladipusMessages {
	
	private ResourceBundle bundle;
	private String defaultMessage;
	private String propertiesFile;
	
	public PladipusMessages(String properties, String defaultMessage) {
		this.defaultMessage = defaultMessage;
		this.propertiesFile = properties;
	}
	
	public String getMessage(String key) {
		try {
			return getBundle().getString(key);
		} catch (MissingResourceException e) {
			return defaultMessage;
		}
	}
	
	public String getMessage(String key, Object... parameters) {
		try {
			return MessageFormat.format(getBundle().getString(key), parameters);
		} catch (MissingResourceException | IllegalArgumentException e) {
			return defaultMessage;
		}
	}
	
	private ResourceBundle getBundle() throws MissingResourceException {
		if (bundle == null) {
			bundle = ResourceBundle.getBundle(propertiesFile);
		}
		return bundle;
	}
}
