package com.compomics.pladipus.model.exceptions;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * TODO Move out of model
 *
 */
public class PladipusLogExceptionMessages {
	
	private ResourceBundle bundle;
	private static final String DEFAULT_ERROR_MESSAGE = "An error has occurred.  See logs for more details.";
	
	public String getMessage(String key) {
		try {
			return getBundle().getString(key);
		} catch (MissingResourceException e) {
			return DEFAULT_ERROR_MESSAGE;
		}
	}
	
	public String getMessage(String key, Object... parameters) {
		try {
			return MessageFormat.format(getBundle().getString(key), parameters);
		} catch (MissingResourceException | IllegalArgumentException e) {
			return DEFAULT_ERROR_MESSAGE;
		}
	}
	
	private ResourceBundle getBundle() throws MissingResourceException {
		if (bundle == null) {
			bundle = ResourceBundle.getBundle("log_exception");
		}
		return bundle;
	}
}
